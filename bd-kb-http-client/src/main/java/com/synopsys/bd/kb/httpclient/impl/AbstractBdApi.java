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
package com.synopsys.bd.kb.httpclient.impl;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import com.synopsys.kb.httpclient.api.HttpResponse;
import com.synopsys.kb.httpclient.api.HttpResult;
import com.synopsys.kb.httpclient.model.Meta;

/**
 * Abstract Black Duck-centric API.
 * 
 * Package protected.
 * 
 * @author skatzman
 */
abstract class AbstractBdApi {
    protected AbstractBdApi() {
    }

    /**
     * Converts the given source HTTP result to the destination HTTP result.
     * 
     * @param <S>
     *            The source type.
     * @param <T>
     *            The destination type.
     * @param sourceHttpResult
     *            The source HTTP result.
     * @param conversionFunction
     *            The conversion function.
     * @return Returns the destination HTTP result.
     */
    protected <S, T> HttpResult<T> convert(HttpResult<S> sourceHttpResult, Function<S, T> conversionFunction) {
        Objects.requireNonNull(sourceHttpResult, "Source HTTP result must be initialized.");
        Objects.requireNonNull(conversionFunction, "Conversion function must be initialized.");

        String sourceRequestMethod = sourceHttpResult.getRequestMethod();
        String sourceRequestUri = sourceHttpResult.getRequestUri();
        HttpResponse<S> sourceHttpResponse = sourceHttpResult.getHttpResponse().orElse(null);
        Throwable sourceCause = sourceHttpResult.getCause().orElse(null);

        final HttpResult<T> destinationHttpResult;

        if (sourceHttpResponse != null) {
            // Source HTTP result contains a HTTP response.
            int sourceCode = sourceHttpResponse.getCode();
            Set<Integer> sourceExpectedCodes = sourceHttpResponse.getExpectedCodes();
            S sourceMessageBody = sourceHttpResponse.getMessageBody().orElse(null);
            Meta sourceMigratedMeta = sourceHttpResponse.getMigratedMeta().orElse(null);

            final HttpResponse<T> destinationHttpResponse;
            if (sourceMessageBody != null) {
                // Source HTTP response contains a message body.
                T destinationMessageBody = conversionFunction.apply(sourceMessageBody);
                destinationHttpResponse = new HttpResponse<>(sourceCode, sourceExpectedCodes, destinationMessageBody, sourceMigratedMeta);
            } else {
                // Source HTTP response does NOT contain a message body.
                destinationHttpResponse = new HttpResponse<>(sourceCode, sourceExpectedCodes, null, sourceMigratedMeta);
            }

            destinationHttpResult = new HttpResult<>(sourceRequestMethod, sourceRequestUri, destinationHttpResponse, sourceCause);
        } else {
            // Source HTTP result does NOT contain a HTTP response.
            destinationHttpResult = new HttpResult<>(sourceRequestMethod, sourceRequestUri, null, sourceCause);
        }

        return destinationHttpResult;
    }
}
