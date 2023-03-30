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
import java.util.Objects;

import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http.protocol.HttpContext;

import com.google.common.base.Strings;
import com.synopsys.kb.httpclient.model.Authentication;

/**
 * Authorization HTTP request interceptor.
 * 
 * HTTP request interceptor to automatically populate Authorization header with the KB JSON web token.
 * - Bypass HTTP request interceptor for non-applicable request URIs (e.g. - /api/authenticate).
 * - Add Authorization header if KB JSON web token value is present.
 * - Authenticate to KB if KB JSON web token value is absent.
 * - ... On successful KB authentication, add Authorization header.
 * - ... On failed KB authentication, fail request with unretriable IOException.
 * 
 * Package protected.
 * 
 * @author skatzman
 */
class AuthorizationHttpRequestInterceptor implements HttpRequestInterceptor {
    private final AuthorizationProvider authorizationProvider;

    public AuthorizationHttpRequestInterceptor(AuthorizationProvider authorizationProvider) {
        this.authorizationProvider = Objects.requireNonNull(authorizationProvider, "Authorization provider must be initialized.");
    }

    @Override
    public void process(HttpRequest request, EntityDetails entity, HttpContext context) throws HttpException, IOException {
        Authentication authentication = authorizationProvider.getOrAuthenticate().orElse(null);
        if (authentication != null) {
            String jsonWebToken = authentication.getJsonWebToken();
            if (!Strings.isNullOrEmpty(jsonWebToken)) {
                // Add authorization header.
                Header authorizationHeader = new BasicHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jsonWebToken);
                request.addHeader(authorizationHeader);
            } else {
                // Fail request with unretriable IOException.
                throw new AuthenticationException("Unable to add Authorization header because an authentication was returned with an invalid JSON web token.");
            }
        } else {
            // Fail request with unretriable IOException.
            throw new AuthenticationException("Unable to add Authorization header because an authentication could not be acquired.");
        }
    }
}
