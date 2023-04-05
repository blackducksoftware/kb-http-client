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
import java.util.Objects;
import java.util.UUID;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.message.BasicHeader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synopsys.kb.httpclient.api.AuthorizationProvider;
import com.synopsys.kb.httpclient.api.ILicenseApi;
import com.synopsys.kb.httpclient.api.KbConfiguration;
import com.synopsys.kb.httpclient.api.Result;
import com.synopsys.kb.httpclient.model.License;

/**
 * License HTTP client implementation.
 * 
 * License related API client operations.
 * 
 * @author skatzman
 */
public class KbLicenseHttpClient extends AbstractKbHttpClient implements ILicenseApi {
    public KbLicenseHttpClient(KbConfiguration kbConfiguration,
            ObjectMapper objectMapper,
            CloseableHttpClient httpClient,
            AuthorizationProvider authorizationProvider) {
        super(kbConfiguration, objectMapper, httpClient, authorizationProvider);
    }

    @Override
    public Result<License> find(UUID licenseId) {
        Objects.requireNonNull(licenseId, "License id must be initialized.");

        Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT, KbContentType.KB_COMPONENT_DETAILS_V4_JSON);
        Collection<Header> headers = List.of(acceptHeader);
        ClassicHttpRequest request = constructGetHttpRequest("/api/licenses/" + licenseId, null, headers);

        return execute(request,
                DEFAULT_SUCCESS_CODES,
                DEFAULT_EXPECTED_CODES,
                License.class);
    }

    @Override
    public Result<String> findText(UUID licenseId) {
        Objects.requireNonNull(licenseId, "License id must be initialized.");

        Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT, KbContentType.KB_LICENSE_TEXT_ORIGINAL_V1);
        Collection<Header> headers = List.of(acceptHeader);
        ClassicHttpRequest request = constructGetHttpRequest("/api/licenses/" + licenseId + "/text", null, headers);

        return execute(request,
                DEFAULT_SUCCESS_CODES,
                DEFAULT_EXPECTED_CODES,
                true,
                false);
    }
}
