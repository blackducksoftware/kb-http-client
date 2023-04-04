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
import com.synopsys.kb.httpclient.api.Result;
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
     * Converts the given source result to the destination result.
     * 
     * @param <S>
     *            The source type.
     * @param <T>
     *            The destination type.
     * @param sourceResult
     *            The source result.
     * @param conversionFunction
     *            The conversion function.
     * @return Returns the destination result.
     */
    protected <S, T> Result<T> convert(Result<S> sourceResult, Function<S, T> conversionFunction) {
        Objects.requireNonNull(sourceResult, "Source result must be initialized.");
        Objects.requireNonNull(conversionFunction, "Conversion function must be initialized.");

        String sourceRequestMethod = sourceResult.getRequestMethod();
        String sourceRequestUri = sourceResult.getRequestUri();
        HttpResponse<S> sourceHttpResponse = sourceResult.getHttpResponse().orElse(null);
        Throwable sourceCause = sourceResult.getCause().orElse(null);

        final Result<T> destinationResult;
        if (sourceHttpResponse != null) {
            // Source result contains a HTTP response.
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

            destinationResult = new Result<>(sourceRequestMethod, sourceRequestUri, destinationHttpResponse, sourceCause);
        } else {
            // Source result does NOT contain a HTTP response.
            destinationResult = new Result<>(sourceRequestMethod, sourceRequestUri, null, sourceCause);
        }

        return destinationResult;
    }
}
