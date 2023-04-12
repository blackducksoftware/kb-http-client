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
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import com.synopsys.kb.httpclient.api.AuthorizationProvider;
import com.synopsys.kb.httpclient.api.IComponentApi;
import com.synopsys.kb.httpclient.api.KbConfiguration;
import com.synopsys.kb.httpclient.api.PageRequest;
import com.synopsys.kb.httpclient.api.Result;
import com.synopsys.kb.httpclient.model.Component;
import com.synopsys.kb.httpclient.model.ComponentSearchResult;
import com.synopsys.kb.httpclient.model.ComponentVersion;
import com.synopsys.kb.httpclient.model.ComponentVersionSummary;
import com.synopsys.kb.httpclient.model.Page;
import com.synopsys.kb.httpclient.model.VulnerabilityScorePriority;
import com.synopsys.kb.httpclient.model.VulnerabilitySourcePriority;

/**
 * Component HTTP client implementation.
 * 
 * Component related API client operations.
 * 
 * @author skatzman
 */
public class KbComponentHttpClient extends AbstractKbHttpClient implements IComponentApi {
    public KbComponentHttpClient(KbConfiguration kbConfiguration,
            ObjectMapper objectMapper,
            CloseableHttpClient httpClient,
            AuthorizationProvider authorizationProvider) {
        super(kbConfiguration, objectMapper, httpClient, authorizationProvider);
    }

    @Override
    public Result<Component> find(UUID componentId) {
        Objects.requireNonNull(componentId, "Component id must be initialized.");

        Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT, KbContentType.KB_COMPONENT_DETAILS_V4_JSON);
        Collection<Header> headers = List.of(acceptHeader);
        ClassicHttpRequest request = constructGetHttpRequest("/api/components/" + componentId, null, headers);

        return execute(request,
                DEFAULT_SUCCESS_CODES,
                DEFAULT_EXPECTED_CODES,
                true, // Reauthenticate on Unauthorized response.
                true, // Request can trigger migrated response.
                Component.class);
    }

    @Override
    public Result<Page<ComponentVersion>> findComponentVersionsByComponent(PageRequest pageRequest,
            UUID componentId,
            @Nullable String searchTermFilter,
            VulnerabilitySourcePriority vulnerabilitySourcePriority,
            VulnerabilityScorePriority vulnerabilityScorePriority,
            @Nullable Boolean excludeDeleted) {
        Objects.requireNonNull(pageRequest, "Page request must be initialized.");
        Objects.requireNonNull(componentId, "Component id must be initialized.");
        Objects.requireNonNull(vulnerabilitySourcePriority, "Vulnerability source priority must be initialized.");
        Objects.requireNonNull(vulnerabilityScorePriority, "Vulnerability score priority must be initialized.");

        Map<String, String> pageRequestParameters = constructPageRequestParameters(pageRequest);
        ImmutableListMultimap.Builder<String, String> builder = ImmutableListMultimap.<String, String> builder()
                .putAll(pageRequestParameters.entrySet())
                .put("source_priority", vulnerabilitySourcePriority.getValue())
                .put("score_priority", vulnerabilityScorePriority.getValue());
        if (!Strings.isNullOrEmpty(searchTermFilter)) {
            builder = builder.put("q", "version:" + searchTermFilter);
        }
        if (excludeDeleted != null) {
            builder = builder.put("excludeDeleted", excludeDeleted.toString());
        }
        ListMultimap<String, String> parameters = builder.build();
        Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT, KbContentType.KB_COMPONENT_DETAILS_V4_JSON);
        Collection<Header> headers = List.of(acceptHeader);
        ClassicHttpRequest request = constructGetHttpRequest("/api/components/" + componentId + "/versions", parameters, headers);

        return execute(request,
                DEFAULT_SUCCESS_CODES,
                Set.of(HttpStatus.SC_OK, HttpStatus.SC_PAYMENT_REQUIRED, HttpStatus.SC_FORBIDDEN, HttpStatus.SC_NOT_FOUND, HttpStatus.SC_INTERNAL_SERVER_ERROR),
                true, // Reauthenticate on Unauthorized response.
                true, // Request can trigger migrated response.
                new TypeReference<Page<ComponentVersion>>() {
                });
    }

    @Override
    public Result<Page<ComponentVersionSummary>> findComponentVersionSummariesByComponent(PageRequest pageRequest,
            UUID componentId,
            @Nullable String searchTermFilter,
            @Nullable Boolean excludeDeleted) {
        Objects.requireNonNull(pageRequest, "Page request must be initialized.");
        Objects.requireNonNull(componentId, "Component id must be initialized.");

        Map<String, String> pageRequestParameters = constructPageRequestParameters(pageRequest);
        ImmutableListMultimap.Builder<String, String> builder = ImmutableListMultimap.<String, String> builder()
                .putAll(pageRequestParameters.entrySet());
        if (!Strings.isNullOrEmpty(searchTermFilter)) {
            builder = builder.put("q", "version:" + searchTermFilter);
        }
        if (excludeDeleted != null) {
            builder = builder.put("excludeDeleted", excludeDeleted.toString());
        }
        ListMultimap<String, String> parameters = builder.build();
        Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT, KbContentType.KB_SUMMARY_V2_JSON);
        Collection<Header> headers = List.of(acceptHeader);
        ClassicHttpRequest request = constructGetHttpRequest("/api/components/" + componentId + "/versions", parameters, headers);

        return execute(request,
                DEFAULT_SUCCESS_CODES,
                DEFAULT_EXPECTED_CODES,
                true, // Reauthenticate on Unauthorized response.
                true, // Request can trigger migrated response.
                new TypeReference<Page<ComponentVersionSummary>>() {
                });
    }

    @Override
    public Result<Page<ComponentSearchResult>> search(PageRequest pageRequest, String searchTermFilter, boolean allowPartialMatches) {
        Objects.requireNonNull(pageRequest, "Page request must be initialized.");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(searchTermFilter), "Search term filter must not be null or empty");

        Map<String, String> pageRequestParameters = constructPageRequestParameters(pageRequest);
        ListMultimap<String, String> parameters = ImmutableListMultimap.<String, String> builder()
                .putAll(pageRequestParameters.entrySet())
                .put("q", searchTermFilter)
                .put("allowPartialMatches", Boolean.toString(allowPartialMatches)).build();
        Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT, KbContentType.KB_COMPONENT_DETAILS_V3_JSON);
        Collection<Header> headers = List.of(acceptHeader);
        ClassicHttpRequest request = constructGetHttpRequest("/api/components", parameters, headers);

        return execute(request,
                DEFAULT_SUCCESS_CODES,
                Set.of(HttpStatus.SC_OK, HttpStatus.SC_BAD_REQUEST, HttpStatus.SC_NOT_FOUND, HttpStatus.SC_GONE),
                new TypeReference<Page<ComponentSearchResult>>() {
                });
    }
}
