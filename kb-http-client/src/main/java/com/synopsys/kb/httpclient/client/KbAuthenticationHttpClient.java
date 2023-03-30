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
package com.synopsys.kb.httpclient.client;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicHeader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.synopsys.kb.httpclient.api.IAuthenticationApi;
import com.synopsys.kb.httpclient.api.KbConfiguration;
import com.synopsys.kb.httpclient.api.Result;
import com.synopsys.kb.httpclient.model.Authentication;

/**
 * Authentication HTTP client implementation.
 * 
 * Authentication operations for granting KnowledgeBase access.
 * 
 * @author skatzman
 */
public class KbAuthenticationHttpClient extends AbstractKbHttpClient implements IAuthenticationApi {
    public KbAuthenticationHttpClient(KbConfiguration kbConfiguration,
            ObjectMapper objectMapper,
            CloseableHttpClient httpClient) {
        // The authentication HTTP client does not rely on an authorization provider as authentication is the means of
        // yielding authorization.
        super(kbConfiguration, objectMapper, httpClient, null);
    }

    @Override
    public Result<Authentication> authenticate(String licenseKey) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(licenseKey), "License key must not be null or empty.");

        Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT, KbContentType.KB_AUTHENTICATE_V1_JSON);
        Header authorizationHeader = new BasicHeader(HttpHeaders.AUTHORIZATION, "bdsLicenseKey " + licenseKey);
        Header cacheControlHeader = new BasicHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        Collection<Header> headers = List.of(acceptHeader, authorizationHeader, cacheControlHeader);
        // Authentication requests are via POST and need to contain a no content entity as POST requests require content
        // length disposition.
        HttpEntity entity = new StringEntity("", ContentType.create(KbContentType.KB_AUTHENTICATE_V1_JSON));
        ClassicHttpRequest request = constructPostHttpRequest("/api/authenticate", null, headers, entity);

        return execute(request,
                DEFAULT_SUCCESS_CODES,
                Set.of(HttpStatus.SC_OK),
                false, // Do not reauthenticate on Unauthorized response.
                false, // Request does not trigger migrated response.
                Authentication.class);
    }
}
