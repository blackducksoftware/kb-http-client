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

import java.util.Optional;

import javax.annotation.Nullable;

/**
 * Operation result.
 * 
 * @author skatzman
 *
 * @param <T>
 *            The message body type.
 */
public class Result<T> extends AbstractResult {
    @Nullable
    private final HttpResponse<T> httpResponse;

    public Result(String requestMethod, String requestUri, @Nullable HttpResponse<T> httpResponse) {
        this(requestMethod, requestUri, httpResponse, null);
    }

    public Result(String requestMethod, String requestUri, @Nullable Throwable cause) {
        this(requestMethod, requestUri, null, cause);
    }

    public Result(String requestMethod, String requestUri, @Nullable HttpResponse<T> httpResponse, @Nullable Throwable cause) {
        super(requestMethod, requestUri, cause);

        this.httpResponse = httpResponse;
    }

    /**
     * Gets the HTTP response.
     * 
     * @return Returns the HTTP response if present and emptiness otherwise.
     */
    public Optional<HttpResponse<T>> getHttpResponse() {
        return Optional.ofNullable(httpResponse);
    }
}
