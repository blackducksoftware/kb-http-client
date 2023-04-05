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

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.message.BasicHeader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.synopsys.kb.httpclient.api.AuthorizationProvider;
import com.synopsys.kb.httpclient.api.IActivityApi;
import com.synopsys.kb.httpclient.api.KbConfiguration;
import com.synopsys.kb.httpclient.api.Result;
import com.synopsys.kb.httpclient.model.BdsaVulnerabilityActivity;
import com.synopsys.kb.httpclient.model.ComponentActivity;
import com.synopsys.kb.httpclient.model.ComponentVariantActivity;
import com.synopsys.kb.httpclient.model.ComponentVersionActivity;
import com.synopsys.kb.httpclient.model.CveVulnerabilityActivity;
import com.synopsys.kb.httpclient.model.IdHolder;
import com.synopsys.kb.httpclient.model.LicenseActivity;
import com.synopsys.kb.httpclient.model.ListHolder;

/**
 * Activity HTTP client implementation.
 * 
 * Activity related API client operations.
 * 
 * @author skatzman
 */
public class KbActivityHttpClient extends AbstractKbHttpClient implements IActivityApi {
    private static final int ID_LIMIT = 1000;

    public KbActivityHttpClient(KbConfiguration kbConfiguration,
            ObjectMapper objectMapper,
            CloseableHttpClient httpClient,
            AuthorizationProvider authorizationProvider) {
        super(kbConfiguration, objectMapper, httpClient, authorizationProvider);
    }

    @Override
    public Result<ListHolder<ComponentActivity>> findComponentActivities(Set<UUID> componentIds, OffsetDateTime activitySince) {
        Objects.requireNonNull(componentIds, "Component ids must be initialized.");
        Preconditions.checkArgument(!componentIds.isEmpty(), "Component ids must not be empty.");
        Preconditions.checkArgument(componentIds.size() <= ID_LIMIT, "Number of component ids must be less than or equal to " + ID_LIMIT);
        Objects.requireNonNull(activitySince, "Activity since must be initialized.");

        String activitySinceString = activitySince.format(DateTimeFormatter.ISO_INSTANT);
        Map<String, String> parameters = ImmutableMap.<String, String> builder()
                .put("activitySince", activitySinceString).build();
        Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT, KbContentType.KB_ACTIVITY_V3_JSON);
        Header contentTypeHeader = new BasicHeader(HttpHeaders.CONTENT_TYPE, KbContentType.KB_ACTIVITY_V3_JSON);
        Collection<Header> headers = List.of(acceptHeader, contentTypeHeader);
        IdHolder<UUID> idHolder = new IdHolder<>(componentIds);
        HttpEntity httpEntity = constructHttpEntity(idHolder, KbContentType.KB_ACTIVITY_V3_JSON);
        ClassicHttpRequest request = constructPostHttpRequest("/api/activity/components", parameters, headers, httpEntity);

        return execute(request,
                DEFAULT_SUCCESS_CODES,
                Set.of(HttpStatus.SC_OK),
                new TypeReference<ListHolder<ComponentActivity>>() {
                });
    }

    @Override
    public Result<ListHolder<ComponentVersionActivity>> findComponentVersionActivities(Set<UUID> componentVersionIds, OffsetDateTime activitySince) {
        Objects.requireNonNull(componentVersionIds, "Component version ids must be initialized.");
        Preconditions.checkArgument(!componentVersionIds.isEmpty(), "Component version ids must not be empty.");
        Preconditions.checkArgument(componentVersionIds.size() <= ID_LIMIT, "Number of component version ids must be less than or equal to " + ID_LIMIT);
        Objects.requireNonNull(activitySince, "Activity since must be initialized.");

        String activitySinceString = activitySince.format(DateTimeFormatter.ISO_INSTANT);
        Map<String, String> parameters = ImmutableMap.<String, String> builder()
                .put("activitySince", activitySinceString).build();
        Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT, KbContentType.KB_ACTIVITY_V3_JSON);
        Header contentTypeHeader = new BasicHeader(HttpHeaders.CONTENT_TYPE, KbContentType.KB_ACTIVITY_V3_JSON);
        Collection<Header> headers = List.of(acceptHeader, contentTypeHeader);
        IdHolder<UUID> idHolder = new IdHolder<>(componentVersionIds);
        HttpEntity httpEntity = constructHttpEntity(idHolder, KbContentType.KB_ACTIVITY_V3_JSON);
        ClassicHttpRequest request = constructPostHttpRequest("/api/activity/versions", parameters, headers, httpEntity);

        return execute(request,
                DEFAULT_SUCCESS_CODES,
                Set.of(HttpStatus.SC_OK),
                new TypeReference<ListHolder<ComponentVersionActivity>>() {
                });
    }

    @Override
    public Result<ListHolder<ComponentVersionActivity>> findComponentVersionLicenseActivities(Set<UUID> componentVersionIds, OffsetDateTime activitySince) {
        Objects.requireNonNull(componentVersionIds, "Component version ids must be initialized.");
        Preconditions.checkArgument(!componentVersionIds.isEmpty(), "Component version ids must not be empty.");
        Preconditions.checkArgument(componentVersionIds.size() <= ID_LIMIT, "Number of component version ids must be less than or equal to " + ID_LIMIT);
        Objects.requireNonNull(activitySince, "Activity since must be initialized.");

        String activitySinceString = activitySince.format(DateTimeFormatter.ISO_INSTANT);
        Map<String, String> parameters = ImmutableMap.<String, String> builder()
                .put("activitySince", activitySinceString).build();
        Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT, KbContentType.KB_ACTIVITY_V3_JSON);
        Header contentTypeHeader = new BasicHeader(HttpHeaders.CONTENT_TYPE, KbContentType.KB_ACTIVITY_V3_JSON);
        Collection<Header> headers = List.of(acceptHeader, contentTypeHeader);
        IdHolder<UUID> idHolder = new IdHolder<>(componentVersionIds);
        HttpEntity httpEntity = constructHttpEntity(idHolder, KbContentType.KB_ACTIVITY_V3_JSON);
        ClassicHttpRequest request = constructPostHttpRequest("/api/activity/versions/license", parameters, headers, httpEntity);

        return execute(request,
                DEFAULT_SUCCESS_CODES,
                Set.of(HttpStatus.SC_OK),
                new TypeReference<ListHolder<ComponentVersionActivity>>() {
                });
    }

    @Override
    public Result<ListHolder<ComponentVersionActivity>> findComponentVersionCveVulnerabilityActivities(Set<UUID> componentVersionIds,
            OffsetDateTime activitySince) {
        Objects.requireNonNull(componentVersionIds, "Component version ids must be initialized.");
        Preconditions.checkArgument(!componentVersionIds.isEmpty(), "Component version ids must not be empty.");
        Preconditions.checkArgument(componentVersionIds.size() <= ID_LIMIT, "Number of component version ids must be less than or equal to " + ID_LIMIT);
        Objects.requireNonNull(activitySince, "Activity since must be initialized.");

        String activitySinceString = activitySince.format(DateTimeFormatter.ISO_INSTANT);
        Map<String, String> parameters = ImmutableMap.<String, String> builder()
                .put("activitySince", activitySinceString).build();
        Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT, KbContentType.KB_ACTIVITY_V3_JSON);
        Header contentTypeHeader = new BasicHeader(HttpHeaders.CONTENT_TYPE, KbContentType.KB_ACTIVITY_V3_JSON);
        Collection<Header> headers = List.of(acceptHeader, contentTypeHeader);
        IdHolder<UUID> idHolder = new IdHolder<>(componentVersionIds);
        HttpEntity httpEntity = constructHttpEntity(idHolder, KbContentType.KB_ACTIVITY_V3_JSON);
        ClassicHttpRequest request = constructPostHttpRequest("/api/activity/versions/cve", parameters, headers, httpEntity);

        return execute(request,
                DEFAULT_SUCCESS_CODES,
                Set.of(HttpStatus.SC_OK),
                new TypeReference<ListHolder<ComponentVersionActivity>>() {
                });
    }

    @Override
    public Result<ListHolder<ComponentVersionActivity>> findComponentVersionBdsaVulnerabilityActivities(Set<UUID> componentVersionIds,
            OffsetDateTime activitySince) {
        Objects.requireNonNull(componentVersionIds, "Component version ids must be initialized.");
        Preconditions.checkArgument(!componentVersionIds.isEmpty(), "Component version ids must not be empty.");
        Preconditions.checkArgument(componentVersionIds.size() <= ID_LIMIT, "Number of component version ids must be less than or equal to " + ID_LIMIT);
        Objects.requireNonNull(activitySince, "Activity since must be initialized.");

        String activitySinceString = activitySince.format(DateTimeFormatter.ISO_INSTANT);
        Map<String, String> parameters = ImmutableMap.<String, String> builder()
                .put("activitySince", activitySinceString).build();
        Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT, KbContentType.KB_ACTIVITY_V3_JSON);
        Header contentTypeHeader = new BasicHeader(HttpHeaders.CONTENT_TYPE, KbContentType.KB_ACTIVITY_V3_JSON);
        Collection<Header> headers = List.of(acceptHeader, contentTypeHeader);
        IdHolder<UUID> idHolder = new IdHolder<>(componentVersionIds);
        HttpEntity httpEntity = constructHttpEntity(idHolder, KbContentType.KB_ACTIVITY_V3_JSON);
        ClassicHttpRequest request = constructPostHttpRequest("/api/activity/versions/bdsa", parameters, headers, httpEntity);

        return execute(request,
                DEFAULT_SUCCESS_CODES,
                Set.of(HttpStatus.SC_OK, HttpStatus.SC_PAYMENT_REQUIRED, HttpStatus.SC_FORBIDDEN),
                new TypeReference<ListHolder<ComponentVersionActivity>>() {
                });
    }

    @Override
    public Result<ListHolder<ComponentVersionActivity>> findComponentVersionUpgradeGuidanceActivities(Set<UUID> componentVersionIds,
            OffsetDateTime activitySince) {
        Objects.requireNonNull(componentVersionIds, "Component version ids must be initialized.");
        Preconditions.checkArgument(!componentVersionIds.isEmpty(), "Component version ids must not be empty.");
        Preconditions.checkArgument(componentVersionIds.size() <= ID_LIMIT, "Number of component version ids must be less than or equal to " + ID_LIMIT);
        Objects.requireNonNull(activitySince, "Activity since must be initialized.");

        String activitySinceString = activitySince.format(DateTimeFormatter.ISO_INSTANT);
        Map<String, String> parameters = ImmutableMap.<String, String> builder()
                .put("activitySince", activitySinceString).build();
        Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT, KbContentType.KB_ACTIVITY_V3_JSON);
        Header contentTypeHeader = new BasicHeader(HttpHeaders.CONTENT_TYPE, KbContentType.KB_ACTIVITY_V3_JSON);
        Collection<Header> headers = List.of(acceptHeader, contentTypeHeader);
        IdHolder<UUID> idHolder = new IdHolder<>(componentVersionIds);
        HttpEntity httpEntity = constructHttpEntity(idHolder, KbContentType.KB_ACTIVITY_V3_JSON);
        ClassicHttpRequest request = constructPostHttpRequest("/api/activity/versions/upgrade-guidance", parameters, headers, httpEntity);

        return execute(request,
                DEFAULT_SUCCESS_CODES,
                Set.of(HttpStatus.SC_OK),
                new TypeReference<ListHolder<ComponentVersionActivity>>() {
                });
    }

    @Override
    public Result<ListHolder<ComponentVariantActivity>> findComponentVariantActivities(Set<UUID> componentVariantIds, OffsetDateTime activitySince) {
        Objects.requireNonNull(componentVariantIds, "Component variant ids must be initialized.");
        Preconditions.checkArgument(!componentVariantIds.isEmpty(), "Component variant ids must not be empty.");
        Preconditions.checkArgument(componentVariantIds.size() <= ID_LIMIT, "Number of component variant ids must be less than or equal to " + ID_LIMIT);
        Objects.requireNonNull(activitySince, "Activity since must be initialized.");

        String activitySinceString = activitySince.format(DateTimeFormatter.ISO_INSTANT);
        Map<String, String> parameters = ImmutableMap.<String, String> builder()
                .put("activitySince", activitySinceString).build();
        Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT, KbContentType.KB_ACTIVITY_V3_JSON);
        Header contentTypeHeader = new BasicHeader(HttpHeaders.CONTENT_TYPE, KbContentType.KB_ACTIVITY_V3_JSON);
        Collection<Header> headers = List.of(acceptHeader, contentTypeHeader);
        IdHolder<UUID> idHolder = new IdHolder<>(componentVariantIds);
        HttpEntity httpEntity = constructHttpEntity(idHolder, KbContentType.KB_ACTIVITY_V3_JSON);
        ClassicHttpRequest request = constructPostHttpRequest("/api/activity/variants", parameters, headers, httpEntity);

        return execute(request,
                DEFAULT_SUCCESS_CODES,
                Set.of(HttpStatus.SC_OK),
                new TypeReference<ListHolder<ComponentVariantActivity>>() {
                });
    }

    @Override
    public Result<ListHolder<ComponentVariantActivity>> findComponentVariantCveVulnerabilityActivities(Set<UUID> componentVariantIds,
            OffsetDateTime activitySince) {
        Objects.requireNonNull(componentVariantIds, "Component variant ids must be initialized.");
        Preconditions.checkArgument(!componentVariantIds.isEmpty(), "Component variant ids must not be empty.");
        Preconditions.checkArgument(componentVariantIds.size() <= ID_LIMIT, "Number of component variant ids must be less than or equal to " + ID_LIMIT);
        Objects.requireNonNull(activitySince, "Activity since must be initialized.");

        String activitySinceString = activitySince.format(DateTimeFormatter.ISO_INSTANT);
        Map<String, String> parameters = ImmutableMap.<String, String> builder()
                .put("activitySince", activitySinceString).build();
        Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT, KbContentType.KB_ACTIVITY_V3_JSON);
        Header contentTypeHeader = new BasicHeader(HttpHeaders.CONTENT_TYPE, KbContentType.KB_ACTIVITY_V3_JSON);
        Collection<Header> headers = List.of(acceptHeader, contentTypeHeader);
        IdHolder<UUID> idHolder = new IdHolder<>(componentVariantIds);
        HttpEntity httpEntity = constructHttpEntity(idHolder, KbContentType.KB_ACTIVITY_V3_JSON);
        ClassicHttpRequest request = constructPostHttpRequest("/api/activity/variants/cve", parameters, headers, httpEntity);

        return execute(request,
                DEFAULT_SUCCESS_CODES,
                Set.of(HttpStatus.SC_OK),
                new TypeReference<ListHolder<ComponentVariantActivity>>() {
                });
    }

    @Override
    public Result<ListHolder<ComponentVariantActivity>> findComponentVariantBdsaVulnerabilityActivities(Set<UUID> componentVariantIds,
            OffsetDateTime activitySince) {
        Objects.requireNonNull(componentVariantIds, "Component variant ids must be initialized.");
        Preconditions.checkArgument(!componentVariantIds.isEmpty(), "Component variant ids must not be empty.");
        Preconditions.checkArgument(componentVariantIds.size() <= ID_LIMIT, "Number of component variant ids must be less than or equal to " + ID_LIMIT);
        Objects.requireNonNull(activitySince, "Activity since must be initialized.");

        String activitySinceString = activitySince.format(DateTimeFormatter.ISO_INSTANT);
        Map<String, String> parameters = ImmutableMap.<String, String> builder()
                .put("activitySince", activitySinceString).build();
        Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT, KbContentType.KB_ACTIVITY_V3_JSON);
        Header contentTypeHeader = new BasicHeader(HttpHeaders.CONTENT_TYPE, KbContentType.KB_ACTIVITY_V3_JSON);
        Collection<Header> headers = List.of(acceptHeader, contentTypeHeader);
        IdHolder<UUID> idHolder = new IdHolder<>(componentVariantIds);
        HttpEntity httpEntity = constructHttpEntity(idHolder, KbContentType.KB_ACTIVITY_V3_JSON);
        ClassicHttpRequest request = constructPostHttpRequest("/api/activity/variants/bdsa", parameters, headers, httpEntity);

        return execute(request,
                DEFAULT_SUCCESS_CODES,
                Set.of(HttpStatus.SC_OK, HttpStatus.SC_PAYMENT_REQUIRED, HttpStatus.SC_FORBIDDEN),
                new TypeReference<ListHolder<ComponentVariantActivity>>() {
                });
    }

    @Override
    public Result<ListHolder<ComponentVariantActivity>> findComponentVariantUpgradeGuidanceActivities(Set<UUID> componentVariantIds,
            OffsetDateTime activitySince) {
        Objects.requireNonNull(componentVariantIds, "Component variant ids must be initialized.");
        Preconditions.checkArgument(!componentVariantIds.isEmpty(), "Component variant ids must not be empty.");
        Preconditions.checkArgument(componentVariantIds.size() <= ID_LIMIT, "Number of component variant ids must be less than or equal to " + ID_LIMIT);
        Objects.requireNonNull(activitySince, "Activity since must be initialized.");

        String activitySinceString = activitySince.format(DateTimeFormatter.ISO_INSTANT);
        Map<String, String> parameters = ImmutableMap.<String, String> builder()
                .put("activitySince", activitySinceString).build();
        Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT, KbContentType.KB_ACTIVITY_V3_JSON);
        Header contentTypeHeader = new BasicHeader(HttpHeaders.CONTENT_TYPE, KbContentType.KB_ACTIVITY_V3_JSON);
        Collection<Header> headers = List.of(acceptHeader, contentTypeHeader);
        IdHolder<UUID> idHolder = new IdHolder<>(componentVariantIds);
        HttpEntity httpEntity = constructHttpEntity(idHolder, KbContentType.KB_ACTIVITY_V3_JSON);
        ClassicHttpRequest request = constructPostHttpRequest("/api/activity/variants/upgrade-guidance", parameters, headers, httpEntity);

        return execute(request,
                DEFAULT_SUCCESS_CODES,
                Set.of(HttpStatus.SC_OK),
                new TypeReference<ListHolder<ComponentVariantActivity>>() {
                });
    }

    @Override
    public Result<ListHolder<ComponentVariantActivity>> findComponentVariantTransitiveUpgradeGuidanceActivities(Set<UUID> componentVariantIds,
            OffsetDateTime activitySince) {
        Objects.requireNonNull(componentVariantIds, "Component variant ids must be initialized.");
        Preconditions.checkArgument(!componentVariantIds.isEmpty(), "Component variant ids must not be empty.");
        Preconditions.checkArgument(componentVariantIds.size() <= ID_LIMIT, "Number of component variant ids must be less than or equal to " + ID_LIMIT);
        Objects.requireNonNull(activitySince, "Activity since must be initialized.");

        String activitySinceString = activitySince.format(DateTimeFormatter.ISO_INSTANT);
        Map<String, String> parameters = ImmutableMap.<String, String> builder()
                .put("activitySince", activitySinceString).build();
        Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT, KbContentType.KB_ACTIVITY_V3_JSON);
        Header contentTypeHeader = new BasicHeader(HttpHeaders.CONTENT_TYPE, KbContentType.KB_ACTIVITY_V3_JSON);
        Collection<Header> headers = List.of(acceptHeader, contentTypeHeader);
        IdHolder<UUID> idHolder = new IdHolder<>(componentVariantIds);
        HttpEntity httpEntity = constructHttpEntity(idHolder, KbContentType.KB_ACTIVITY_V3_JSON);
        ClassicHttpRequest request = constructPostHttpRequest("/api/activity/variants/transitive-upgrade-guidance", parameters, headers, httpEntity);

        return execute(request,
                DEFAULT_SUCCESS_CODES,
                Set.of(HttpStatus.SC_OK),
                new TypeReference<ListHolder<ComponentVariantActivity>>() {
                });
    }

    @Override
    public Result<ListHolder<LicenseActivity>> findLicenseActivities(Set<UUID> licenseIds, OffsetDateTime activitySince) {
        Objects.requireNonNull(licenseIds, "License ids must be initialized.");
        Preconditions.checkArgument(!licenseIds.isEmpty(), "License ids must not be empty.");
        Preconditions.checkArgument(licenseIds.size() <= ID_LIMIT, "Number of license ids must be less than or equal to " + ID_LIMIT);
        Objects.requireNonNull(activitySince, "Activity since must be initialized.");

        String activitySinceString = activitySince.format(DateTimeFormatter.ISO_INSTANT);
        Map<String, String> parameters = ImmutableMap.<String, String> builder()
                .put("activitySince", activitySinceString).build();
        Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT, KbContentType.KB_ACTIVITY_V3_JSON);
        Header contentTypeHeader = new BasicHeader(HttpHeaders.CONTENT_TYPE, KbContentType.KB_ACTIVITY_V3_JSON);
        Collection<Header> headers = List.of(acceptHeader, contentTypeHeader);
        IdHolder<UUID> idHolder = new IdHolder<>(licenseIds);
        HttpEntity httpEntity = constructHttpEntity(idHolder, KbContentType.KB_ACTIVITY_V3_JSON);
        ClassicHttpRequest request = constructPostHttpRequest("/api/activity/licenses", parameters, headers, httpEntity);

        return execute(request,
                DEFAULT_SUCCESS_CODES,
                Set.of(HttpStatus.SC_OK),
                new TypeReference<ListHolder<LicenseActivity>>() {
                });
    }

    @Override
    public Result<ListHolder<CveVulnerabilityActivity>> findCveVulnerabilityActivities(Set<String> cveVulnerabilityIds, OffsetDateTime activitySince) {
        Objects.requireNonNull(cveVulnerabilityIds, "CVE vulnerability ids must be initialized.");
        Preconditions.checkArgument(!cveVulnerabilityIds.isEmpty(), "CVE vulnerability ids must not be empty.");
        Preconditions.checkArgument(cveVulnerabilityIds.size() <= ID_LIMIT, "Number of CVE vulnerability ids must be less than or equal to " + ID_LIMIT);
        Objects.requireNonNull(activitySince, "Activity since must be initialized.");

        String activitySinceString = activitySince.format(DateTimeFormatter.ISO_INSTANT);
        Map<String, String> parameters = ImmutableMap.<String, String> builder()
                .put("activitySince", activitySinceString).build();
        Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT, KbContentType.KB_ACTIVITY_V3_JSON);
        Header contentTypeHeader = new BasicHeader(HttpHeaders.CONTENT_TYPE, KbContentType.KB_ACTIVITY_V3_JSON);
        Collection<Header> headers = List.of(acceptHeader, contentTypeHeader);
        IdHolder<String> idHolder = new IdHolder<>(cveVulnerabilityIds);
        HttpEntity httpEntity = constructHttpEntity(idHolder, KbContentType.KB_ACTIVITY_V3_JSON);
        ClassicHttpRequest request = constructPostHttpRequest("/api/activity/vulnerabilities/cve", parameters, headers, httpEntity);

        return execute(request,
                DEFAULT_SUCCESS_CODES,
                Set.of(HttpStatus.SC_OK),
                new TypeReference<ListHolder<CveVulnerabilityActivity>>() {
                });
    }

    @Override
    public Result<ListHolder<BdsaVulnerabilityActivity>> findBdsaVulnerabilityActivities(Set<String> bdsaVulnerabilityIds, OffsetDateTime activitySince) {
        Objects.requireNonNull(bdsaVulnerabilityIds, "BDSA vulnerability ids must be initialized.");
        Preconditions.checkArgument(!bdsaVulnerabilityIds.isEmpty(), "BDSA vulnerability ids must not be empty.");
        Preconditions.checkArgument(bdsaVulnerabilityIds.size() <= ID_LIMIT, "Number of BDSA vulnerability ids must be less than or equal to " + ID_LIMIT);
        Objects.requireNonNull(activitySince, "Activity since must be initialized.");

        String activitySinceString = activitySince.format(DateTimeFormatter.ISO_INSTANT);
        Map<String, String> parameters = ImmutableMap.<String, String> builder()
                .put("activitySince", activitySinceString).build();
        Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT, KbContentType.KB_ACTIVITY_V3_JSON);
        Header contentTypeHeader = new BasicHeader(HttpHeaders.CONTENT_TYPE, KbContentType.KB_ACTIVITY_V3_JSON);
        Collection<Header> headers = List.of(acceptHeader, contentTypeHeader);
        IdHolder<String> idHolder = new IdHolder<>(bdsaVulnerabilityIds);
        HttpEntity httpEntity = constructHttpEntity(idHolder, KbContentType.KB_ACTIVITY_V3_JSON);
        ClassicHttpRequest request = constructPostHttpRequest("/api/activity/vulnerabilities/bdsa", parameters, headers, httpEntity);

        return execute(request,
                DEFAULT_SUCCESS_CODES,
                Set.of(HttpStatus.SC_OK, HttpStatus.SC_PAYMENT_REQUIRED, HttpStatus.SC_FORBIDDEN),
                new TypeReference<ListHolder<BdsaVulnerabilityActivity>>() {
                });
    }
}
