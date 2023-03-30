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

import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nullable;

/**
 * Operation result.
 * 
 * @author skatzman
 *
 * @param <T>
 *            The value type.
 */
public class Result<T> {
    private final String requestMethod;

    private final String requestUri;

    @Nullable
    private final HttpResponse<T> httpResponse;

    @Nullable
    private final Throwable cause;

    public Result(String requestMethod, String requestUri, @Nullable HttpResponse<T> httpResponse) {
        this.requestMethod = Objects.requireNonNull(requestMethod, "The request method must be initialized.");
        this.requestUri = Objects.requireNonNull(requestUri, "The request URI must be initialized.");
        this.httpResponse = httpResponse;
        this.cause = null;
    }

    public Result(String requestMethod, String requestUri, @Nullable Throwable cause) {
        this.requestMethod = Objects.requireNonNull(requestMethod, "The request method must be initialized.");
        this.requestUri = Objects.requireNonNull(requestUri, "The request URI must be initialized.");
        this.httpResponse = null;
        this.cause = cause;
    }

    /**
     * Gets the request method.
     * 
     * @return Returns the request method.
     */
    public String getRequestMethod() {
        return requestMethod;
    }

    /**
     * Gets the request URI.
     * 
     * @return Returns the request URI.
     */
    public String getRequestUri() {
        return requestUri;
    }

    /**
     * Determines whether an HTTP response is present or not.
     * 
     * @return Returns true if an HTTP response is present and false otherwise.
     */
    public boolean isHttpResponsePresent() {
        return getHttpResponse().isPresent();
    }

    /**
     * Gets the HTTP response.
     * 
     * @return Returns the HTTP response if present and emptiness otherwise.
     */
    public Optional<HttpResponse<T>> getHttpResponse() {
        return Optional.ofNullable(httpResponse);
    }

    /**
     * Determines whether a failure cause is present or not.
     * 
     * @return Returns true if a failure cause is present and false otherwise.
     */
    public boolean isCausePresent() {
        return getCause().isPresent();
    }

    /**
     * Gets the failure cause.
     * 
     * @return Returns the failure cause if present and emptiness otherwise.
     */
    public Optional<Throwable> getCause() {
        return Optional.ofNullable(cause);
    }
}
