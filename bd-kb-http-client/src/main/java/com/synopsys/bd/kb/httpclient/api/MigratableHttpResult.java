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
package com.synopsys.bd.kb.httpclient.api;

import java.util.Optional;

import javax.annotation.Nullable;

import com.synopsys.kb.httpclient.api.AbstractHttpResult;

/**
 * Migratable HTTP result representation.
 * 
 * @author skatzman
 *
 * @param <T>
 *            The message body type.
 */
public class MigratableHttpResult<T> extends AbstractHttpResult {
    @Nullable
    private final MigratableHttpResponse<T> migratableHttpResponse;

    public MigratableHttpResult(String requestMethod,
            String requestUri,
            @Nullable MigratableHttpResponse<T> migratableHttpResponse,
            @Nullable Throwable cause) {
        super(requestMethod, requestUri, cause);

        this.migratableHttpResponse = migratableHttpResponse;
    }

    /**
     * Gets the migratable HTTP response.
     * 
     * @return Returns the HTTP response if present and emptiness otherwise.
     */
    public Optional<MigratableHttpResponse<T>> getMigratableHttpResponse() {
        return Optional.ofNullable(migratableHttpResponse);
    }
}
