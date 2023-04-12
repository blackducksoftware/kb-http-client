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
import com.synopsys.kb.httpclient.api.IComponentVariantApi;
import com.synopsys.kb.httpclient.api.KbConfiguration;
import com.synopsys.kb.httpclient.api.PageRequest;
import com.synopsys.kb.httpclient.api.Result;
import com.synopsys.kb.httpclient.model.BdsaVulnerability;
import com.synopsys.kb.httpclient.model.ComponentVariant;
import com.synopsys.kb.httpclient.model.CveVulnerability;
import com.synopsys.kb.httpclient.model.Page;
import com.synopsys.kb.httpclient.model.UpgradeGuidance;
import com.synopsys.kb.httpclient.model.VulnerabilityScorePriority;
import com.synopsys.kb.httpclient.model.VulnerabilitySourcePriority;

/**
 * Component variant HTTP client implementation.
 * 
 * Component variant related API client operations.
 * 
 * @author skatzman
 */
public class KbComponentVariantHttpClient extends AbstractKbHttpClient implements IComponentVariantApi {
    public KbComponentVariantHttpClient(KbConfiguration kbConfiguration,
            ObjectMapper objectMapper,
            CloseableHttpClient httpClient,
            AuthorizationProvider authorizationProvider) {
        super(kbConfiguration, objectMapper, httpClient, authorizationProvider);
    }

    @Override
    public Result<ComponentVariant> find(UUID componentVariantId) {
        Objects.requireNonNull(componentVariantId, "Component variant id must be initialized.");

        Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT, KbContentType.KB_COMPONENT_DETAILS_V4_JSON);
        Collection<Header> headers = List.of(acceptHeader);
        ClassicHttpRequest request = constructGetHttpRequest("/api/variants/" + componentVariantId, null, headers);

        return execute(request,
                DEFAULT_SUCCESS_CODES,
                DEFAULT_EXPECTED_CODES,
                ComponentVariant.class);
    }

    @Override
    public Result<Page<CveVulnerability>> findCveVulnerabilities(PageRequest pageRequest,
            UUID componentVariantId,
            @Nullable String searchTermFilter) {
        Objects.requireNonNull(pageRequest, "Page request must be initialized.");
        Objects.requireNonNull(componentVariantId, "Component variant id must be initialized.");

        Map<String, String> pageRequestParameters = constructPageRequestParameters(pageRequest);
        ImmutableListMultimap.Builder<String, String> builder = ImmutableListMultimap.<String, String> builder()
                .putAll(pageRequestParameters.entrySet());
        if (!Strings.isNullOrEmpty(searchTermFilter)) {
            builder = builder.put("q", searchTermFilter);
        }
        ListMultimap<String, String> parameters = builder.build();
        Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT, KbContentType.KB_VULNERABILITY_V7_JSON);
        Collection<Header> headers = List.of(acceptHeader);
        ClassicHttpRequest request = constructGetHttpRequest("/api/variants/" + componentVariantId + "/vulnerabilities-cve", parameters, headers);

        return execute(request,
                DEFAULT_SUCCESS_CODES,
                DEFAULT_EXPECTED_CODES,
                new TypeReference<Page<CveVulnerability>>() {
                });
    }

    @Override
    public Result<Page<BdsaVulnerability>> findBdsaVulnerabilities(PageRequest pageRequest,
            UUID componentVariantId,
            @Nullable String searchTermFilter) {
        Objects.requireNonNull(pageRequest, "Page request must be initialized.");
        Objects.requireNonNull(componentVariantId, "Component variant id must be initialized.");

        Map<String, String> pageRequestParameters = constructPageRequestParameters(pageRequest);
        ImmutableListMultimap.Builder<String, String> builder = ImmutableListMultimap.<String, String> builder()
                .putAll(pageRequestParameters.entrySet());
        if (!Strings.isNullOrEmpty(searchTermFilter)) {
            builder = builder.put("q", searchTermFilter);
        }
        ListMultimap<String, String> parameters = builder.build();
        Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT, KbContentType.KB_VULNERABILITY_V7_JSON);
        Collection<Header> headers = List.of(acceptHeader);
        ClassicHttpRequest request = constructGetHttpRequest("/api/variants/" + componentVariantId + "/vulnerabilities-bdsa", parameters, headers);

        return execute(request,
                DEFAULT_SUCCESS_CODES,
                Set.of(HttpStatus.SC_OK, HttpStatus.SC_PAYMENT_REQUIRED, HttpStatus.SC_FORBIDDEN, HttpStatus.SC_NOT_FOUND),
                new TypeReference<Page<BdsaVulnerability>>() {
                });
    }

    @Override
    public Result<UpgradeGuidance> findUpgradeGuidance(UUID componentVariantId,
            VulnerabilitySourcePriority vulnerabilitySourcePriority,
            VulnerabilityScorePriority vulnerabilityScorePriority) {
        Objects.requireNonNull(componentVariantId, "Component variant id must be initialized.");
        Objects.requireNonNull(vulnerabilitySourcePriority, "Vulnerability source priority must be initialized.");
        Objects.requireNonNull(vulnerabilityScorePriority, "Vulnerability score priority must be initialized.");

        ListMultimap<String, String> parameters = ImmutableListMultimap.<String, String> builder()
                .put("source_priority", vulnerabilitySourcePriority.getValue())
                .put("score_priority", vulnerabilityScorePriority.getValue())
                .build();
        Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT, KbContentType.KB_COMPONENT_DETAILS_V4_JSON);
        Collection<Header> headers = List.of(acceptHeader);
        ClassicHttpRequest request = constructGetHttpRequest("/api/variants/" + componentVariantId + "/upgrade-guidance", parameters, headers);

        return execute(request,
                DEFAULT_SUCCESS_CODES,
                Set.of(HttpStatus.SC_OK, HttpStatus.SC_PAYMENT_REQUIRED, HttpStatus.SC_FORBIDDEN, HttpStatus.SC_NOT_FOUND),
                UpgradeGuidance.class);
    }

    @Override
    public Result<UpgradeGuidance> findTransitiveUpgradeGuidance(UUID componentVariantId,
            VulnerabilitySourcePriority vulnerabilitySourcePriority,
            VulnerabilityScorePriority vulnerabilityScorePriority) {
        Objects.requireNonNull(componentVariantId, "Component variant id must be initialized.");
        Objects.requireNonNull(vulnerabilitySourcePriority, "Vulnerability source priority must be initialized.");
        Objects.requireNonNull(vulnerabilityScorePriority, "Vulnerability score priority must be initialized.");

        ListMultimap<String, String> parameters = ImmutableListMultimap.<String, String> builder()
                .put("source_priority", vulnerabilitySourcePriority.getValue())
                .put("score_priority", vulnerabilityScorePriority.getValue())
                .build();
        Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT, KbContentType.KB_COMPONENT_DETAILS_V4_JSON);
        Collection<Header> headers = List.of(acceptHeader);
        ClassicHttpRequest request = constructGetHttpRequest("/api/variants/" + componentVariantId + "/transitive-upgrade-guidance", parameters, headers);

        return execute(request,
                DEFAULT_SUCCESS_CODES,
                Set.of(HttpStatus.SC_OK, HttpStatus.SC_PAYMENT_REQUIRED, HttpStatus.SC_FORBIDDEN, HttpStatus.SC_NOT_FOUND),
                UpgradeGuidance.class);
    }
}
