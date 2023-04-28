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
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.message.BasicHeader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import com.synopsys.kb.httpclient.api.AuthorizationProvider;
import com.synopsys.kb.httpclient.api.ILicenseApi;
import com.synopsys.kb.httpclient.api.KbConfiguration;
import com.synopsys.kb.httpclient.api.PageRequest;
import com.synopsys.kb.httpclient.api.HttpResult;
import com.synopsys.kb.httpclient.model.License;
import com.synopsys.kb.httpclient.model.LicenseTerm;
import com.synopsys.kb.httpclient.model.Page;

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
    public HttpResult<License> findLicense(UUID licenseId) {
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
    public HttpResult<String> findLicenseText(UUID licenseId) {
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

    @Override
    public HttpResult<Page<License>> findManyLicenses(PageRequest pageRequest,
            @Nullable String searchTermFilter,
            @Nullable Map<String, String> filters) {
        Objects.requireNonNull(pageRequest, "Page request must be initialized.");

        Map<String, String> pageRequestParameters = constructPageRequestParameters(pageRequest);
        ImmutableListMultimap.Builder<String, String> builder = ImmutableListMultimap.<String, String> builder()
                .putAll(pageRequestParameters.entrySet());
        if (!Strings.isNullOrEmpty(searchTermFilter)) {
            builder = builder.put("q", searchTermFilter);
        }
        if (filters != null && !filters.isEmpty()) {
            builder = builder.putAll(filters.entrySet());
        }
        ListMultimap<String, String> parameters = builder.build();
        Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT, KbContentType.KB_COMPONENT_DETAILS_V4_JSON);
        Collection<Header> headers = List.of(acceptHeader);
        ClassicHttpRequest request = constructGetHttpRequest("/api/licenses", parameters, headers);

        return execute(request,
                DEFAULT_SUCCESS_CODES,
                Set.of(HttpStatus.SC_OK),
                true, // Reauthenticate on Unauthorized response.
                false, // Request does not trigger migrated response.
                new TypeReference<Page<License>>() {
                });
    }

    @Override
    public HttpResult<Page<License>> findLicensesByLicenseTerm(PageRequest pageRequest, UUID licenseTermId) {
        Objects.requireNonNull(pageRequest, "Page request must be initialized.");
        Objects.requireNonNull(licenseTermId, "License term id must be initialized.");

        Map<String, String> pageRequestParameters = constructPageRequestParameters(pageRequest);
        ListMultimap<String, String> parameters = ImmutableListMultimap.<String, String> builder()
                .putAll(pageRequestParameters.entrySet()).build();
        Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT, KbContentType.KB_COMPONENT_DETAILS_V4_JSON);
        Collection<Header> headers = List.of(acceptHeader);
        ClassicHttpRequest request = constructGetHttpRequest("/api/license-terms/" + licenseTermId + "/licenses", parameters, headers);

        return execute(request,
                DEFAULT_SUCCESS_CODES,
                DEFAULT_EXPECTED_CODES,
                true, // Reauthenticate on Unauthorized response.
                false, // Request does not trigger migrated response.
                new TypeReference<Page<License>>() {
                });
    }

    @Override
    public HttpResult<LicenseTerm> findLicenseTerm(UUID licenseTermId) {
        Objects.requireNonNull(licenseTermId, "License term id must be initialized.");

        Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT, KbContentType.KB_COMPONENT_DETAILS_V4_JSON);
        Collection<Header> headers = List.of(acceptHeader);
        ClassicHttpRequest request = constructGetHttpRequest("/api/license-terms/" + licenseTermId, null, headers);

        return execute(request,
                DEFAULT_SUCCESS_CODES,
                DEFAULT_EXPECTED_CODES,
                LicenseTerm.class);
    }

    @Override
    public HttpResult<Page<LicenseTerm>> findManyLicenseTerms(PageRequest pageRequest) {
        Objects.requireNonNull(pageRequest, "Page request must be initialized.");

        Map<String, String> pageRequestParameters = constructPageRequestParameters(pageRequest);
        ListMultimap<String, String> parameters = ImmutableListMultimap.<String, String> builder()
                .putAll(pageRequestParameters.entrySet()).build();
        Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT, KbContentType.KB_COMPONENT_DETAILS_V4_JSON);
        Collection<Header> headers = List.of(acceptHeader);
        ClassicHttpRequest request = constructGetHttpRequest("/api/license-terms", parameters, headers);

        return execute(request,
                DEFAULT_SUCCESS_CODES,
                Set.of(HttpStatus.SC_OK),
                true, // Reauthenticate on Unauthorized response.
                false, // Request does not trigger migrated response.
                new TypeReference<Page<LicenseTerm>>() {
                });
    }

    @Override
    public HttpResult<Page<LicenseTerm>> findLicenseTermsByLicense(PageRequest pageRequest, UUID licenseId) {
        Objects.requireNonNull(pageRequest, "Page request must be initialized.");
        Objects.requireNonNull(licenseId, "License id must be initialized.");

        ImmutableListMultimap.Builder<String, String> builder = ImmutableListMultimap.builder();
        Map<String, String> pageRequestParameters = constructPageRequestParameters(pageRequest);
        ListMultimap<String, String> parameters = builder.putAll(pageRequestParameters.entrySet()).build();
        Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT, KbContentType.KB_COMPONENT_DETAILS_V4_JSON);
        Collection<Header> headers = List.of(acceptHeader);
        ClassicHttpRequest request = constructGetHttpRequest("/api/licenses/" + licenseId + "/license-terms", parameters, headers);

        return execute(request,
                DEFAULT_SUCCESS_CODES,
                DEFAULT_EXPECTED_CODES,
                new TypeReference<Page<LicenseTerm>>() {
                });
    }
}
