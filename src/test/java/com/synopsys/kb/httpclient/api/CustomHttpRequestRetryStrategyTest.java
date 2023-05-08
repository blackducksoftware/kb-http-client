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
import java.net.UnknownHostException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.hc.client5.http.HttpRequestRetryStrategy;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http.message.BasicHttpRequest;
import org.apache.hc.core5.http.message.BasicHttpResponse;
import org.apache.hc.core5.http.protocol.BasicHttpContext;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.TimeValue;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.synopsys.kb.httpclient.AbstractTest;

/**
 * Custom HTTP request strategy test.
 * 
 * @author skatzman
 */
public class CustomHttpRequestRetryStrategyTest extends AbstractTest {
    private static final long RETRY_INTERVAL_MS = TimeUnit.MILLISECONDS.toMillis(1000L);

    private HttpContext context;

    private HttpRequestRetryStrategy strategy;

    @BeforeMethod
    public void beforeMethod() {
        context = new BasicHttpContext();

        IRetryIntervalStrategy retryIntervalStrategy = new FixedRetryIntervalStrategy(TimeUnit.MILLISECONDS.toMillis(RETRY_INTERVAL_MS));
        strategy = new CustomHttpRequestRetryStrategy(3, Set.of(HttpStatus.SC_TOO_MANY_REQUESTS), retryIntervalStrategy);
    }

    @Test
    public void testRetryRequestForIOExceptionWhenGreaterThanMaxRetries() {
        HttpRequest request = new BasicHttpRequest(Method.GET, "/api/licenses/" + UUID.randomUUID());
        IOException exception = new IOException("This is an exception.");
        boolean isRetry = strategy.retryRequest(request, exception, 4, context);

        Assert.assertFalse(isRetry, "Request should not be retried.");
    }

    @Test
    public void testRetryRequestForIOExceptionWithUnretriableException() {
        HttpRequest request = new BasicHttpRequest(Method.GET, "/api/licenses/" + UUID.randomUUID());
        IOException exception = new UnknownHostException("This is an exception.");
        boolean isRetry = strategy.retryRequest(request, exception, 0, context);

        Assert.assertFalse(isRetry, "Request should not be retried.");
    }

    @Test
    public void testRetryRequestForIOExceptionWithUnretriableRequest() {
        HttpGet request = new HttpGet("/api/licenses/" + UUID.randomUUID());
        request.cancel();
        IOException exception = new IOException("This is an exception.");
        boolean isRetry = strategy.retryRequest(request, exception, 0, context);

        Assert.assertFalse(isRetry, "Request should not be retried.");
    }

    @Test
    public void testRetryRequestForIOExceptionWithNonIdempotentMethod() {
        HttpRequest request = new BasicHttpRequest(Method.POST, "/api/licenses");
        IOException exception = new IOException("This is an exception.");
        boolean isRetry = strategy.retryRequest(request, exception, 0, context);

        Assert.assertFalse(isRetry, "Request should not be retried.");
    }

    @Test
    public void testRetryRequestForIOException() {
        HttpRequest request = new BasicHttpRequest(Method.GET, "/api/licenses/" + UUID.randomUUID());
        IOException exception = new IOException("This is an exception.");
        boolean isRetry = strategy.retryRequest(request, exception, 0, context);

        Assert.assertTrue(isRetry, "Request should be retried.");
    }

    @Test
    public void testRetryRequestForResponseWhenGreaterThanMaxRetries() {
        HttpResponse response = new BasicHttpResponse(HttpStatus.SC_TOO_MANY_REQUESTS);
        boolean isRetry = strategy.retryRequest(response, 4, context);

        Assert.assertFalse(isRetry, "Request should not be retried.");
    }

    @Test
    public void testRetryRequestForResponseWithNonRetriableCode() {
        HttpResponse response = new BasicHttpResponse(HttpStatus.SC_NOT_FOUND);
        boolean isRetry = strategy.retryRequest(response, 0, context);

        Assert.assertFalse(isRetry, "Request should not be retried.");
    }

    @Test
    public void testRetryRequestForResponse() {
        HttpResponse response = new BasicHttpResponse(HttpStatus.SC_TOO_MANY_REQUESTS);
        boolean isRetry = strategy.retryRequest(response, 0, context);

        Assert.assertTrue(isRetry, "Request should be retried.");
    }

    @Test
    public void testGetRetryIntervalForIOException() {
        HttpRequest request = new BasicHttpRequest(Method.GET, "/api/licenses/" + UUID.randomUUID());
        IOException exception = new IOException("This is an exception.");
        TimeValue retryInterval = strategy.getRetryInterval(request, exception, 0, context);

        Assert.assertEquals(retryInterval, TimeValue.ofMilliseconds(RETRY_INTERVAL_MS), "Retry intervals should be equal.");
    }

    @Test
    public void testGetRetryIntervalForResponseWithoutRetryAfterHeader() {
        HttpResponse response = new BasicHttpResponse(HttpStatus.SC_TOO_MANY_REQUESTS);
        TimeValue retryInterval = strategy.getRetryInterval(response, 0, context);

        Assert.assertEquals(retryInterval, TimeValue.ofMilliseconds(RETRY_INTERVAL_MS), "Retry intervals should be equal.");
    }

    @Test
    public void testGetRetryIntervalForResponseWithInvalidRetryAfterHeader() {
        HttpResponse response = new BasicHttpResponse(HttpStatus.SC_TOO_MANY_REQUESTS);
        response.addHeader(new BasicHeader(HttpHeaders.RETRY_AFTER, "invalid_header_value"));
        TimeValue retryInterval = strategy.getRetryInterval(response, 0, context);

        Assert.assertEquals(retryInterval, TimeValue.ofMilliseconds(RETRY_INTERVAL_MS), "Retry intervals should be equal.");
    }

    @Test
    public void testGetRetryIntervalForResponseWithNegativeRetryAfterHeader() {
        HttpResponse response = new BasicHttpResponse(HttpStatus.SC_TOO_MANY_REQUESTS);
        response.addHeader(new BasicHeader(HttpHeaders.RETRY_AFTER, "-100"));
        TimeValue retryInterval = strategy.getRetryInterval(response, 0, context);

        Assert.assertEquals(retryInterval, TimeValue.ofMilliseconds(RETRY_INTERVAL_MS), "Retry intervals should be equal.");
    }

    @Test
    public void testGetRetryIntervalForResponseWithValidRetryAfterHeader() {
        HttpResponse response = new BasicHttpResponse(HttpStatus.SC_TOO_MANY_REQUESTS);
        int retryAfterSeconds = 100;
        response.addHeader(new BasicHeader(HttpHeaders.RETRY_AFTER, String.valueOf(retryAfterSeconds)));
        TimeValue retryInterval = strategy.getRetryInterval(response, 0, context);

        Assert.assertEquals(retryInterval, TimeValue.ofSeconds(retryAfterSeconds), "Retry intervals should be equal.");
    }
}
