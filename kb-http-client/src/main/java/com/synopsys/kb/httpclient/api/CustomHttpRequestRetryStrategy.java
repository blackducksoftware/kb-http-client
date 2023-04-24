/*
 * Copyright (C) 2023 Synopsys Inc.
 * http://www.synopsys.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Synopsys ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Synopsys.
 */
package com.synopsys.kb.httpclient.api;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.net.ssl.SSLException;

import org.apache.hc.client5.http.HttpRequestRetryStrategy;
import org.apache.hc.client5.http.utils.DateUtils;
import org.apache.hc.core5.concurrent.CancellableDependency;
import org.apache.hc.core5.http.ConnectionClosedException;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.TimeValue;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

/**
 * Custom HTTP request retry strategy.
 * 
 * Package protected.
 * 
 * @author skatzman
 */
class CustomHttpRequestRetryStrategy implements HttpRequestRetryStrategy {
    private final int maxRetries;

    private final Set<Integer> retriableCodes;

    private final Set<Class<? extends IOException>> nonRetriableIOExceptionClasses;

    private final IRetryIntervalStrategy retryIntervalStrategy;

    public CustomHttpRequestRetryStrategy(int maxRetries,
            Set<Integer> retriableCodes,
            IRetryIntervalStrategy retryIntervalStrategy) {
        this(maxRetries,
                retriableCodes,
                retryIntervalStrategy,
                Set.<Class<? extends IOException>> of(AuthenticationException.class,
                        InterruptedIOException.class,
                        UnknownHostException.class,
                        ConnectException.class,
                        ConnectionClosedException.class,
                        NoRouteToHostException.class,
                        SSLException.class));
    }

    CustomHttpRequestRetryStrategy(int maxRetries,
            Set<Integer> retriableCodes,
            IRetryIntervalStrategy retryIntervalStrategy,
            Set<Class<? extends IOException>> nonRetriableIOExceptionClasses) {
        Preconditions.checkArgument(maxRetries >= 0, "Maximum retries must be greater than or equal to 0.");

        if (retriableCodes != null) {
            Preconditions.checkArgument(!retriableCodes.contains(HttpStatus.SC_UNAUTHORIZED),
                    "HTTP 401 Unauthorized is not a supported retriable response code.");
        }

        this.maxRetries = maxRetries;
        this.retriableCodes = (retriableCodes != null) ? ImmutableSet.copyOf(retriableCodes) : ImmutableSet.of();
        this.retryIntervalStrategy = Objects.requireNonNull(retryIntervalStrategy, "Retry interval strategy must be initialized.");
        this.nonRetriableIOExceptionClasses = (nonRetriableIOExceptionClasses != null) ? ImmutableSet.copyOf(nonRetriableIOExceptionClasses)
                : ImmutableSet.of();
    }

    // Determines if a method should be retried after an I/O exception occurred during execution.
    @Override
    public boolean retryRequest(HttpRequest request, IOException exception, int execCount, HttpContext context) {
        if (execCount > maxRetries) {
            // Do not retry if max retries is exceeded.
            return false;
        }

        if (!isRetriable(exception)) {
            // This specific type of IO exception is explicitly not retriable.
            return false;
        }

        if (!isRetriable(request)) {
            // This specfic type of HTTP request is explicitly not retriable.
            return false;
        }

        if (!isIdempotent(request)) {
            // This request is not idempotent and only idempotent requests are supported for retry.
            // See: org.apache.hc.core5.http.Method.
            return false;
        }

        // Otherwise...
        // More retry attempts allowed.
        // IOException is not in the deny list.
        // Request is retriable.
        // Request is idempotent.
        // So retry!
        return true;
    }

    // Determines if a method should be retried given the response from the target server.
    @Override
    public boolean retryRequest(HttpResponse response, int execCount, HttpContext context) {
        if (execCount > maxRetries) {
            // Do not retry if max retries is exceeded.
            return false;
        }

        int code = response.getCode();
        if (!retriableCodes.contains(code)) {
            // HTTP response code is not supported for retry.
            return false;
        }

        return true;
    }

    // Determines the retry interval between subsequent retries after an I/O exception occurred during execution.
    @Override
    public TimeValue getRetryInterval(HttpRequest request, IOException exception, int execCount, HttpContext context) {
        // Utilize the default retry interval.
        return retryIntervalStrategy.determineRetryInterval(execCount);
    }

    // Determines the retry interval between subsequent retries gien the response from the target server.
    @Override
    public TimeValue getRetryInterval(HttpResponse response, int execCount, HttpContext context) {
        // Attempt to honor the Retry-After HTTP response header is available and valid.
        // Otherwise, utilize the default retry interval.
        return readRetryAfter(response).orElseGet(() -> retryIntervalStrategy.determineRetryInterval(execCount));
    }

    private boolean isRetriable(IOException exception) {
        if (this.nonRetriableIOExceptionClasses.contains(exception.getClass())) {
            return false;
        } else {
            for (final Class<? extends IOException> rejectException : this.nonRetriableIOExceptionClasses) {
                if (rejectException.isInstance(exception)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean isRetriable(HttpRequest request) {
        if (request instanceof CancellableDependency && ((CancellableDependency) request).isCancelled()) {
            return false;
        }

        return true;
    }

    private boolean isIdempotent(HttpRequest request) {
        return Method.isIdempotent(request.getMethod());
    }

    private Optional<TimeValue> readRetryAfter(HttpResponse response) {
        TimeValue retryAfterTimeValue = null;

        final Header header = response.getFirstHeader(HttpHeaders.RETRY_AFTER);
        if (header != null) {
            TimeValue headerTimeValue = null;
            final String value = header.getValue();
            try {
                // Retry-After header values that define HTTP date format header values are not considered for retry.
                headerTimeValue = TimeValue.ofSeconds(Long.parseLong(value));
            } catch (final NumberFormatException ignore) {
                final Instant retryAfterDate = DateUtils.parseStandardDate(value);
                if (retryAfterDate != null) {
                    headerTimeValue = TimeValue.ofMilliseconds(retryAfterDate.toEpochMilli() - System.currentTimeMillis());
                }
            }

            if (TimeValue.isPositive(headerTimeValue)) {
                retryAfterTimeValue = headerTimeValue;
            }
        }

        return Optional.ofNullable(retryAfterTimeValue);
    }
}
