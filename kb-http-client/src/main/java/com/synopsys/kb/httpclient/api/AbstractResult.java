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
 * Abstract operation result
 * 
 * @author skatzman
 */
public abstract class AbstractResult {
    private final String requestMethod;

    private final String requestUri;

    @Nullable
    private final Throwable cause;

    protected AbstractResult(String requestMethod, String requestUri, @Nullable Throwable cause) {
        this.requestMethod = Objects.requireNonNull(requestMethod, "The request method must be initialized.");
        this.requestUri = Objects.requireNonNull(requestUri, "The request URI must be initialized.");
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
     * Gets the failure cause.
     * 
     * @return Returns the failure cause if present and emptiness otherwise.
     */
    public Optional<Throwable> getCause() {
        return Optional.ofNullable(cause);
    }
}
