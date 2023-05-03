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

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.synopsys.bd.kb.httpclient.AbstractBdTest;
import com.synopsys.bd.kb.httpclient.api.IBdComponentVersionApi;
import com.synopsys.bd.kb.httpclient.api.MigratableHttpResult;
import com.synopsys.bd.kb.httpclient.model.BdComponentVersion;
import com.synopsys.kb.httpclient.api.HttpResponse;
import com.synopsys.kb.httpclient.api.HttpResult;
import com.synopsys.kb.httpclient.api.IComponentVersionApi;
import com.synopsys.kb.httpclient.api.PageRequest;
import com.synopsys.kb.httpclient.model.BdsaVulnerability;
import com.synopsys.kb.httpclient.model.ComponentVersion;
import com.synopsys.kb.httpclient.model.CveVulnerability;
import com.synopsys.kb.httpclient.model.Link;
import com.synopsys.kb.httpclient.model.Meta;
import com.synopsys.kb.httpclient.model.NextVersion;
import com.synopsys.kb.httpclient.model.Page;
import com.synopsys.kb.httpclient.model.RiskProfile;
import com.synopsys.kb.httpclient.model.UpgradeGuidance;
import com.synopsys.kb.httpclient.model.UpgradeGuidanceSuggestion;
import com.synopsys.kb.httpclient.model.VulnerabilityScorePriority;
import com.synopsys.kb.httpclient.model.VulnerabilitySourcePriority;
import com.synopsys.kb.httpclient.model.VulnerabilityStatus;

public class BdComponentVersionApiTest extends AbstractBdTest {
    private static final String REQUEST_METHOD = "GET";

    private static final UUID COMPONENT_ID = UUID.randomUUID();

    private static final UUID COMPONENT_VERSION_ID = UUID.randomUUID();

    private static final Meta COMPONENT_VERSION_META = new Meta(BASE_HREF + "/api/versions/" + COMPONENT_VERSION_ID,
            List.of(new Link("component", BASE_HREF + "/api/components/" + COMPONENT_ID)));

    @Mock
    private IComponentVersionApi componentVersionApi;

    private IBdComponentVersionApi bdComponentVersionApi;

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.openMocks(this);

        // Limit max attempts to simplify migration testing.
        this.bdComponentVersionApi = new BdComponentVersionApi(componentVersionApi, BASE_HREF, 3);
    }

    @Test
    public void testFindComponentVersionV4WhenAbsent() {
        // HTTP 404 Not Found response
        String requestUri = constructVersionHref(COMPONENT_VERSION_ID);
        HttpResponse<ComponentVersion> httpResponse = new HttpResponse<>(404, Set.of(200, 404), null, null);
        HttpResult<ComponentVersion> httpResult = constructHttpResult(REQUEST_METHOD, requestUri, httpResponse);

        Mockito.when(componentVersionApi.findComponentVersionV4(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(httpResult);

        MigratableHttpResult<BdComponentVersion> migratableHttpResult = bdComponentVersionApi.findComponentVersionV4(COMPONENT_VERSION_ID,
                VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3);

        assertHttpResult(httpResult, migratableHttpResult, Collections.emptyList());
    }

    @Test
    public void testFindComponentVersionV4WhenPresentAndNotMigrated() {
        // HTTP 200 OK response
        String requestUri = constructVersionHref(COMPONENT_VERSION_ID);
        ComponentVersion componentVersion = new ComponentVersion("1.0", OffsetDateTime.now(), null, new RiskProfile(0, 0, 0, 0, 0), Boolean.FALSE,
                Boolean.FALSE, COMPONENT_VERSION_META);
        HttpResponse<ComponentVersion> httpResponse = constructHttpResponse(componentVersion);
        HttpResult<ComponentVersion> httpResult = constructHttpResult(REQUEST_METHOD, requestUri, httpResponse);

        Mockito.when(componentVersionApi.findComponentVersionV4(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(httpResult);

        MigratableHttpResult<BdComponentVersion> migratableHttpResult = bdComponentVersionApi.findComponentVersionV4(COMPONENT_VERSION_ID,
                VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3);

        assertHttpResult(httpResult, migratableHttpResult, Collections.emptyList());
    }

    @Test
    public void testFindComponentVersionV4WhenPresentAndMergeMigrated() {
        String requestUri1 = constructVersionHref(COMPONENT_VERSION_ID);
        UUID destinationComponentVersionId2 = UUID.randomUUID();
        String requestUri2 = constructVersionHref(destinationComponentVersionId2);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<ComponentVersion> httpResponse1 = constructMergeMigratedHttpResponse(requestUri1, requestUri2);
        HttpResult<ComponentVersion> result1 = constructHttpResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 200 OK response
        Meta meta2 = constructMeta(requestUri2);
        ComponentVersion componentVersion2 = new ComponentVersion("1.0", OffsetDateTime.now(), null, new RiskProfile(0, 0, 0, 0, 0), Boolean.FALSE,
                Boolean.FALSE, meta2);
        HttpResponse<ComponentVersion> httpResponse2 = constructHttpResponse(componentVersion2);
        HttpResult<ComponentVersion> result2 = constructHttpResult(REQUEST_METHOD, requestUri2, httpResponse2);

        Mockito.when(componentVersionApi.findComponentVersionV4(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result1);
        Mockito.when(
                componentVersionApi.findComponentVersionV4(destinationComponentVersionId2, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result2);

        MigratableHttpResult<BdComponentVersion> migratableHttpResult = bdComponentVersionApi.findComponentVersionV4(COMPONENT_VERSION_ID,
                VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null)).build();
        assertHttpResult(result2, migratableHttpResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindComponentVersionV4WhenPresentAndSplitMigrated() {
        String requestUri1 = constructVersionHref(COMPONENT_VERSION_ID);
        UUID destinationComponentVersionId2a = UUID.randomUUID();
        UUID destinationComponentVersionId2b = UUID.randomUUID();
        UUID destinationComponentVersionId2c = UUID.randomUUID();
        String requestUri2a = constructVersionHref(destinationComponentVersionId2a);
        String requestUri2b = constructVersionHref(destinationComponentVersionId2b);
        String requestUri2c = constructVersionHref(destinationComponentVersionId2c);

        // HTTP 300 Multiple Choices response (split migration)
        HttpResponse<ComponentVersion> httpResponse1 = constructSplitMigratedHttpResponse(requestUri1, List.of(requestUri2a, requestUri2b, requestUri2c));
        HttpResult<ComponentVersion> result1 = constructHttpResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 200 OK response
        Meta meta2 = constructMeta(requestUri2a);
        ComponentVersion componentVersion2 = new ComponentVersion("1.0", OffsetDateTime.now(), null, new RiskProfile(0, 0, 0, 0, 0), Boolean.FALSE,
                Boolean.FALSE, meta2);
        HttpResponse<ComponentVersion> httpResponse2 = constructHttpResponse(componentVersion2);
        HttpResult<ComponentVersion> result2 = constructHttpResult(REQUEST_METHOD, requestUri2a, httpResponse2);

        Mockito.when(componentVersionApi.findComponentVersionV4(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result1);
        Mockito.when(componentVersionApi.findComponentVersionV4(destinationComponentVersionId2a, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result2);

        MigratableHttpResult<BdComponentVersion> migratableHttpResult = bdComponentVersionApi.findComponentVersionV4(COMPONENT_VERSION_ID,
                VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null)).build();
        assertHttpResult(result2, migratableHttpResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindComponentVersionV4WhenPresentAndMigratedWithMultipleMigrations() {
        String requestUri1 = constructVersionHref(COMPONENT_VERSION_ID);
        UUID destinationComponentVersionId2a = UUID.randomUUID();
        UUID destinationComponentVersionId2b = UUID.randomUUID();
        UUID destinationComponentVersionId2c = UUID.randomUUID();
        String requestUri2a = constructVersionHref(destinationComponentVersionId2a);
        String requestUri2b = constructVersionHref(destinationComponentVersionId2b);
        String requestUri2c = constructVersionHref(destinationComponentVersionId2c);
        UUID destinationComponentVersionId3 = UUID.randomUUID();
        String requestUri3 = constructVersionHref(destinationComponentVersionId3);

        // HTTP 300 Multiple Choices response (split migration)
        HttpResponse<ComponentVersion> httpResponse1 = constructSplitMigratedHttpResponse(requestUri1, List.of(requestUri2a, requestUri2b, requestUri2c));
        HttpResult<ComponentVersion> result1 = constructHttpResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<ComponentVersion> httpResponse2 = constructMergeMigratedHttpResponse(requestUri2a, requestUri3);
        HttpResult<ComponentVersion> result2 = constructHttpResult(REQUEST_METHOD, requestUri2a, httpResponse2);

        // HTTP 200 OK response
        Meta meta3 = constructMeta(requestUri3);
        ComponentVersion componentVersion3 = new ComponentVersion("1.0", OffsetDateTime.now(), null, new RiskProfile(0, 0, 0, 0, 0), Boolean.FALSE,
                Boolean.FALSE, meta3);
        HttpResponse<ComponentVersion> httpResponse3 = constructHttpResponse(componentVersion3);
        HttpResult<ComponentVersion> result3 = constructHttpResult(REQUEST_METHOD, requestUri3, httpResponse3);

        Mockito.when(componentVersionApi.findComponentVersionV4(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result1);
        Mockito.when(componentVersionApi.findComponentVersionV4(destinationComponentVersionId2a, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3)).thenReturn(result2);
        Mockito.when(
                componentVersionApi.findComponentVersionV4(destinationComponentVersionId3, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result3);

        MigratableHttpResult<BdComponentVersion> migratableHttpResult = bdComponentVersionApi.findComponentVersionV4(COMPONENT_VERSION_ID,
                VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null))
                .add(httpResponse2.getMigratedMeta().orElse(null)).build();
        assertHttpResult(result3, migratableHttpResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindComponentVersionV4WhenPresentAndRetriesExhausted() {
        String requestUri1 = constructVersionHref(COMPONENT_VERSION_ID);
        UUID destinationComponentVersionId2 = UUID.randomUUID();
        UUID destinationComponentVersionId3 = UUID.randomUUID();
        UUID destinationComponentVersionId4 = UUID.randomUUID();
        String requestUri2 = constructVersionHref(destinationComponentVersionId2);
        String requestUri3 = constructVersionHref(destinationComponentVersionId3);
        String requestUri4 = constructVersionHref(destinationComponentVersionId4);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<ComponentVersion> httpResponse1 = constructMergeMigratedHttpResponse(requestUri1, requestUri2);
        HttpResult<ComponentVersion> result1 = constructHttpResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<ComponentVersion> httpResponse2 = constructMergeMigratedHttpResponse(requestUri2, requestUri3);
        HttpResult<ComponentVersion> result2 = constructHttpResult(REQUEST_METHOD, requestUri2, httpResponse2);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<ComponentVersion> httpResponse3 = constructMergeMigratedHttpResponse(requestUri3, requestUri4);
        HttpResult<ComponentVersion> result3 = constructHttpResult(REQUEST_METHOD, requestUri3, httpResponse3);

        Mockito.when(componentVersionApi.findComponentVersionV4(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result1);
        Mockito.when(
                componentVersionApi.findComponentVersionV4(destinationComponentVersionId2, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result2);
        Mockito.when(
                componentVersionApi.findComponentVersionV4(destinationComponentVersionId3, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result3);

        MigratableHttpResult<BdComponentVersion> migratableHttpResult = bdComponentVersionApi.findComponentVersionV4(COMPONENT_VERSION_ID,
                VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3);

        // Initial request
        // First migrated request
        // Second migrated request
        // Retries exhausted and return 'second migrated response'.
        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null))
                .add(httpResponse2.getMigratedMeta().orElse(null)).build();
        assertHttpResult(result3, migratableHttpResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindNextVersionV4WhenAbsent() {
        // HTTP 404 Not Found response
        String requestUri = constructNextVersionHref(COMPONENT_VERSION_ID);
        HttpResponse<NextVersion> httpResponse = new HttpResponse<>(404, Set.of(200, 404), null, null);
        HttpResult<NextVersion> httpResult = constructHttpResult(REQUEST_METHOD, requestUri, httpResponse);

        Mockito.when(componentVersionApi.findNextVersionV4(COMPONENT_VERSION_ID)).thenReturn(httpResult);

        MigratableHttpResult<NextVersion> migratableHttpResult = bdComponentVersionApi.findNextVersionV4(COMPONENT_VERSION_ID);

        assertHttpResult(httpResult, migratableHttpResult, Collections.emptyList());
    }

    @Test
    public void testFindNextVersionV4WhenPresentAndNotMigrated() {
        // HTTP 200 OK response
        String requestUri = constructNextVersionHref(COMPONENT_VERSION_ID);
        NextVersion nextVersion = constructNextVersion(COMPONENT_VERSION_ID);
        HttpResponse<NextVersion> httpResponse = constructHttpResponse(nextVersion);
        HttpResult<NextVersion> httpResult = constructHttpResult(REQUEST_METHOD, requestUri, httpResponse);

        Mockito.when(componentVersionApi.findNextVersionV4(COMPONENT_VERSION_ID)).thenReturn(httpResult);

        MigratableHttpResult<NextVersion> migratableHttpResult = bdComponentVersionApi.findNextVersionV4(COMPONENT_VERSION_ID);

        assertHttpResult(httpResult, migratableHttpResult, Collections.emptyList());
    }

    @Test
    public void testFindNextVersionV4WhenPresentAndMergeMigrated() {
        String requestUri1 = constructNextVersionHref(COMPONENT_VERSION_ID);
        UUID destinationComponentVersionId2 = UUID.randomUUID();
        String requestUri2 = constructNextVersionHref(destinationComponentVersionId2);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<NextVersion> httpResponse1 = constructMergeMigratedHttpResponse(requestUri1, requestUri2);
        HttpResult<NextVersion> result1 = constructHttpResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 200 OK response
        NextVersion nextVersion2 = constructNextVersion(destinationComponentVersionId2);
        HttpResponse<NextVersion> httpResponse2 = constructHttpResponse(nextVersion2);
        HttpResult<NextVersion> result2 = constructHttpResult(REQUEST_METHOD, requestUri2, httpResponse2);

        Mockito.when(componentVersionApi.findNextVersionV4(COMPONENT_VERSION_ID)).thenReturn(result1);
        Mockito.when(componentVersionApi.findNextVersionV4(destinationComponentVersionId2)).thenReturn(result2);

        MigratableHttpResult<NextVersion> migratableHttpResult = bdComponentVersionApi.findNextVersionV4(COMPONENT_VERSION_ID);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null)).build();
        assertHttpResult(result2, migratableHttpResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindNextVersionV4WhenPresentAndSplitMigrated() {
        String requestUri1 = constructNextVersionHref(COMPONENT_VERSION_ID);
        UUID destinationComponentVersionId2a = UUID.randomUUID();
        UUID destinationComponentVersionId2b = UUID.randomUUID();
        UUID destinationComponentVersionId2c = UUID.randomUUID();
        String requestUri2a = constructNextVersionHref(destinationComponentVersionId2a);
        String requestUri2b = constructNextVersionHref(destinationComponentVersionId2b);
        String requestUri2c = constructNextVersionHref(destinationComponentVersionId2c);

        // HTTP 300 Multiple Choices response (split migration)
        HttpResponse<NextVersion> httpResponse1 = constructSplitMigratedHttpResponse(requestUri1, List.of(requestUri2a, requestUri2b, requestUri2c));
        HttpResult<NextVersion> result1 = constructHttpResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 200 OK response
        NextVersion nextVersion2 = constructNextVersion(destinationComponentVersionId2a);
        HttpResponse<NextVersion> httpResponse2 = constructHttpResponse(nextVersion2);
        HttpResult<NextVersion> result2 = constructHttpResult(REQUEST_METHOD, requestUri2a, httpResponse2);

        Mockito.when(componentVersionApi.findNextVersionV4(COMPONENT_VERSION_ID)).thenReturn(result1);
        Mockito.when(componentVersionApi.findNextVersionV4(destinationComponentVersionId2a)).thenReturn(result2);

        MigratableHttpResult<NextVersion> migratableHttpResult = bdComponentVersionApi.findNextVersionV4(COMPONENT_VERSION_ID);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null)).build();
        assertHttpResult(result2, migratableHttpResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindNextVersionV4WhenPresentAndMigratedWithMultipleMigrations() {
        String requestUri1 = constructNextVersionHref(COMPONENT_VERSION_ID);
        UUID destinationComponentVersionId2a = UUID.randomUUID();
        UUID destinationComponentVersionId2b = UUID.randomUUID();
        UUID destinationComponentVersionId2c = UUID.randomUUID();
        String requestUri2a = constructNextVersionHref(destinationComponentVersionId2a);
        String requestUri2b = constructNextVersionHref(destinationComponentVersionId2b);
        String requestUri2c = constructNextVersionHref(destinationComponentVersionId2c);
        UUID destinationComponentVersionId3 = UUID.randomUUID();
        String requestUri3 = constructNextVersionHref(destinationComponentVersionId3);

        // HTTP 300 Multiple Choices response (split migration)
        HttpResponse<NextVersion> httpResponse1 = constructSplitMigratedHttpResponse(requestUri1, List.of(requestUri2a, requestUri2b, requestUri2c));
        HttpResult<NextVersion> result1 = constructHttpResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<NextVersion> httpResponse2 = constructMergeMigratedHttpResponse(requestUri2a, requestUri3);
        HttpResult<NextVersion> result2 = constructHttpResult(REQUEST_METHOD, requestUri2a, httpResponse2);

        // HTTP 200 OK response
        NextVersion nextVersion3 = constructNextVersion(destinationComponentVersionId3);
        HttpResponse<NextVersion> httpResponse3 = constructHttpResponse(nextVersion3);
        HttpResult<NextVersion> result3 = constructHttpResult(REQUEST_METHOD, requestUri3, httpResponse3);

        Mockito.when(componentVersionApi.findNextVersionV4(COMPONENT_VERSION_ID)).thenReturn(result1);
        Mockito.when(componentVersionApi.findNextVersionV4(destinationComponentVersionId2a)).thenReturn(result2);
        Mockito.when(componentVersionApi.findNextVersionV4(destinationComponentVersionId3)).thenReturn(result3);

        MigratableHttpResult<NextVersion> migratableHttpResult = bdComponentVersionApi.findNextVersionV4(COMPONENT_VERSION_ID);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null))
                .add(httpResponse2.getMigratedMeta().orElse(null)).build();
        assertHttpResult(result3, migratableHttpResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindNextVersionV4WhenPresentAndRetriesExhausted() {
        String requestUri1 = constructNextVersionHref(COMPONENT_VERSION_ID);
        UUID destinationComponentVersionId2 = UUID.randomUUID();
        UUID destinationComponentVersionId3 = UUID.randomUUID();
        UUID destinationComponentVersionId4 = UUID.randomUUID();
        String requestUri2 = constructNextVersionHref(destinationComponentVersionId2);
        String requestUri3 = constructNextVersionHref(destinationComponentVersionId3);
        String requestUri4 = constructNextVersionHref(destinationComponentVersionId4);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<NextVersion> httpResponse1 = constructMergeMigratedHttpResponse(requestUri1, requestUri2);
        HttpResult<NextVersion> result1 = constructHttpResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<NextVersion> httpResponse2 = constructMergeMigratedHttpResponse(requestUri2, requestUri3);
        HttpResult<NextVersion> result2 = constructHttpResult(REQUEST_METHOD, requestUri2, httpResponse2);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<NextVersion> httpResponse3 = constructMergeMigratedHttpResponse(requestUri3, requestUri4);
        HttpResult<NextVersion> result3 = constructHttpResult(REQUEST_METHOD, requestUri3, httpResponse3);

        Mockito.when(componentVersionApi.findNextVersionV4(COMPONENT_VERSION_ID)).thenReturn(result1);
        Mockito.when(componentVersionApi.findNextVersionV4(destinationComponentVersionId2)).thenReturn(result2);
        Mockito.when(componentVersionApi.findNextVersionV4(destinationComponentVersionId3)).thenReturn(result3);

        MigratableHttpResult<NextVersion> migratableHttpResult = bdComponentVersionApi.findNextVersionV4(COMPONENT_VERSION_ID);

        // Initial request
        // First migrated request
        // Second migrated request
        // Retries exhausted and return 'second migrated response'.
        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null))
                .add(httpResponse2.getMigratedMeta().orElse(null)).build();
        assertHttpResult(result3, migratableHttpResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindCveVulnerabilitiesV7WhenAbsent() {
        PageRequest pageRequest = new PageRequest(0, 100, Collections.emptyList());

        // HTTP 404 Not Found response
        String requestUri = constructVersionHref(COMPONENT_VERSION_ID, "/vulnerabilities-cve");
        HttpResponse<Page<CveVulnerability>> httpResponse = new HttpResponse<>(404, Set.of(200, 404), null, null);
        HttpResult<Page<CveVulnerability>> httpResult = constructHttpResult(REQUEST_METHOD, requestUri, httpResponse);

        Mockito.when(componentVersionApi.findCveVulnerabilitiesV7(pageRequest, COMPONENT_VERSION_ID, null)).thenReturn(httpResult);

        MigratableHttpResult<Page<CveVulnerability>> migratableHttpResult = bdComponentVersionApi.findCveVulnerabilitiesV7(pageRequest, COMPONENT_VERSION_ID,
                null);

        assertHttpResult(httpResult, migratableHttpResult, Collections.emptyList());
    }

    @Test
    public void testFindCveVulnerabilitiesV7WhenPresentAndNotMigrated() {
        PageRequest pageRequest = new PageRequest(0, 100, Collections.emptyList());

        // HTTP 200 OK response
        String requestUri = constructVersionHref(COMPONENT_VERSION_ID, "/vulnerabilities-cve");
        Page<CveVulnerability> cveVulnerabilityPage = constructCveVulnerabilityPage(COMPONENT_VERSION_ID);
        HttpResponse<Page<CveVulnerability>> httpResponse = constructHttpResponse(cveVulnerabilityPage);
        HttpResult<Page<CveVulnerability>> httpResult = constructHttpResult(REQUEST_METHOD, requestUri, httpResponse);

        Mockito.when(componentVersionApi.findCveVulnerabilitiesV7(pageRequest, COMPONENT_VERSION_ID, null)).thenReturn(httpResult);

        MigratableHttpResult<Page<CveVulnerability>> migratableHttpResult = bdComponentVersionApi.findCveVulnerabilitiesV7(pageRequest, COMPONENT_VERSION_ID,
                null);

        assertHttpResult(httpResult, migratableHttpResult, Collections.emptyList());
    }

    @Test
    public void testFindCveVulnerabilitiesV7WhenPresentAndMergeMigrated() {
        PageRequest pageRequest = new PageRequest(0, 100, Collections.emptyList());

        String requestUri1 = constructVersionHref(COMPONENT_VERSION_ID, "/vulnerabilities-cve");
        UUID destinationComponentVersionId2 = UUID.randomUUID();
        String requestUri2 = constructVersionHref(destinationComponentVersionId2, "/vulnerabilities-cve");

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Page<CveVulnerability>> httpResponse1 = constructMergeMigratedHttpResponse(requestUri1, requestUri2);
        HttpResult<Page<CveVulnerability>> result1 = constructHttpResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 200 OK response
        Page<CveVulnerability> cveVulnerabilityPage2 = constructCveVulnerabilityPage(destinationComponentVersionId2);
        HttpResponse<Page<CveVulnerability>> httpResponse2 = constructHttpResponse(cveVulnerabilityPage2);
        HttpResult<Page<CveVulnerability>> result2 = constructHttpResult(REQUEST_METHOD, requestUri2, httpResponse2);

        Mockito.when(componentVersionApi.findCveVulnerabilitiesV7(pageRequest, COMPONENT_VERSION_ID, null)).thenReturn(result1);
        Mockito.when(componentVersionApi.findCveVulnerabilitiesV7(pageRequest, destinationComponentVersionId2, null)).thenReturn(result2);

        MigratableHttpResult<Page<CveVulnerability>> migratableHttpResult = bdComponentVersionApi.findCveVulnerabilitiesV7(pageRequest, COMPONENT_VERSION_ID,
                null);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null)).build();
        assertHttpResult(result2, migratableHttpResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindCveVulnerabilitiesV7WhenPresentAndSplitMigrated() {
        PageRequest pageRequest = new PageRequest(0, 100, Collections.emptyList());

        String requestUri1 = constructVersionHref(COMPONENT_VERSION_ID, "/vulnerabilities-cve");
        UUID destinationComponentVersionId2a = UUID.randomUUID();
        UUID destinationComponentVersionId2b = UUID.randomUUID();
        UUID destinationComponentVersionId2c = UUID.randomUUID();
        String requestUri2a = constructVersionHref(destinationComponentVersionId2a, "/vulnerabilities-cve");
        String requestUri2b = constructVersionHref(destinationComponentVersionId2b, "/vulnerabilities-cve");
        String requestUri2c = constructVersionHref(destinationComponentVersionId2c, "/vulnerabilities-cve");

        // HTTP 300 Multiple Choices response (split migration)
        HttpResponse<Page<CveVulnerability>> httpResponse1 = constructSplitMigratedHttpResponse(requestUri1, List.of(requestUri2a, requestUri2b, requestUri2c));
        HttpResult<Page<CveVulnerability>> result1 = constructHttpResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 200 OK response
        Page<CveVulnerability> cveVulnerabilityPage2 = constructCveVulnerabilityPage(destinationComponentVersionId2a);
        HttpResponse<Page<CveVulnerability>> httpResponse2 = constructHttpResponse(cveVulnerabilityPage2);
        HttpResult<Page<CveVulnerability>> result2 = constructHttpResult(REQUEST_METHOD, requestUri2a, httpResponse2);

        Mockito.when(componentVersionApi.findCveVulnerabilitiesV7(pageRequest, COMPONENT_VERSION_ID, null)).thenReturn(result1);
        Mockito.when(componentVersionApi.findCveVulnerabilitiesV7(pageRequest, destinationComponentVersionId2a, null)).thenReturn(result2);

        MigratableHttpResult<Page<CveVulnerability>> migratableHttpResult = bdComponentVersionApi.findCveVulnerabilitiesV7(pageRequest, COMPONENT_VERSION_ID,
                null);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null)).build();
        assertHttpResult(result2, migratableHttpResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindCveVulnerabilitiesV7WhenPresentAndMigratedWithMultipleMigrations() {
        PageRequest pageRequest = new PageRequest(0, 100, Collections.emptyList());

        String requestUri1 = constructVersionHref(COMPONENT_VERSION_ID, "/vulnerabilities-cve");
        UUID destinationComponentVersionId2a = UUID.randomUUID();
        UUID destinationComponentVersionId2b = UUID.randomUUID();
        UUID destinationComponentVersionId2c = UUID.randomUUID();
        String requestUri2a = constructVersionHref(destinationComponentVersionId2a, "/vulnerabilities-cve");
        String requestUri2b = constructVersionHref(destinationComponentVersionId2b, "/vulnerabilities-cve");
        String requestUri2c = constructVersionHref(destinationComponentVersionId2c, "/vulnerabilities-cve");
        UUID destinationComponentVersionId3 = UUID.randomUUID();
        String requestUri3 = constructVersionHref(destinationComponentVersionId3, "/vulnerabilities-cve");

        // HTTP 300 Multiple Choices response (split migration)
        HttpResponse<Page<CveVulnerability>> httpResponse1 = constructSplitMigratedHttpResponse(requestUri1, List.of(requestUri2a, requestUri2b, requestUri2c));
        HttpResult<Page<CveVulnerability>> result1 = constructHttpResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Page<CveVulnerability>> httpResponse2 = constructMergeMigratedHttpResponse(requestUri2a, requestUri3);
        HttpResult<Page<CveVulnerability>> result2 = constructHttpResult(REQUEST_METHOD, requestUri2a, httpResponse2);

        // HTTP 200 OK response
        Page<CveVulnerability> cveVulnerabilityPage3 = constructCveVulnerabilityPage(destinationComponentVersionId3);
        HttpResponse<Page<CveVulnerability>> httpResponse3 = constructHttpResponse(cveVulnerabilityPage3);
        HttpResult<Page<CveVulnerability>> result3 = constructHttpResult(REQUEST_METHOD, requestUri3, httpResponse3);

        Mockito.when(componentVersionApi.findCveVulnerabilitiesV7(pageRequest, COMPONENT_VERSION_ID, null)).thenReturn(result1);
        Mockito.when(componentVersionApi.findCveVulnerabilitiesV7(pageRequest, destinationComponentVersionId2a, null)).thenReturn(result2);
        Mockito.when(componentVersionApi.findCveVulnerabilitiesV7(pageRequest, destinationComponentVersionId3, null)).thenReturn(result3);

        MigratableHttpResult<Page<CveVulnerability>> migratableHttpResult = bdComponentVersionApi.findCveVulnerabilitiesV7(pageRequest, COMPONENT_VERSION_ID,
                null);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null))
                .add(httpResponse2.getMigratedMeta().orElse(null)).build();
        assertHttpResult(result3, migratableHttpResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindCveVulnerabilitiesV7WhenPresentAndRetriesExhausted() {
        PageRequest pageRequest = new PageRequest(0, 100, Collections.emptyList());

        String requestUri1 = constructVersionHref(COMPONENT_VERSION_ID, "/vulnerabilities-cve");
        UUID destinationComponentVersionId2 = UUID.randomUUID();
        UUID destinationComponentVersionId3 = UUID.randomUUID();
        UUID destinationComponentVersionId4 = UUID.randomUUID();
        String requestUri2 = constructVersionHref(destinationComponentVersionId2, "/vulnerabilities-cve");
        String requestUri3 = constructVersionHref(destinationComponentVersionId3, "/vulnerabilities-cve");
        String requestUri4 = constructVersionHref(destinationComponentVersionId4, "/vulnerabilities-cve");

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Page<CveVulnerability>> httpResponse1 = constructMergeMigratedHttpResponse(requestUri1, requestUri2);
        HttpResult<Page<CveVulnerability>> result1 = constructHttpResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Page<CveVulnerability>> httpResponse2 = constructMergeMigratedHttpResponse(requestUri2, requestUri3);
        HttpResult<Page<CveVulnerability>> result2 = constructHttpResult(REQUEST_METHOD, requestUri2, httpResponse2);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Page<CveVulnerability>> httpResponse3 = constructMergeMigratedHttpResponse(requestUri3, requestUri4);
        HttpResult<Page<CveVulnerability>> result3 = constructHttpResult(REQUEST_METHOD, requestUri3, httpResponse3);

        Mockito.when(componentVersionApi.findCveVulnerabilitiesV7(pageRequest, COMPONENT_VERSION_ID, null)).thenReturn(result1);
        Mockito.when(componentVersionApi.findCveVulnerabilitiesV7(pageRequest, destinationComponentVersionId2, null)).thenReturn(result2);
        Mockito.when(componentVersionApi.findCveVulnerabilitiesV7(pageRequest, destinationComponentVersionId3, null)).thenReturn(result3);

        MigratableHttpResult<Page<CveVulnerability>> migratableHttpResult = bdComponentVersionApi.findCveVulnerabilitiesV7(pageRequest, COMPONENT_VERSION_ID,
                null);

        // Initial request
        // First migrated request
        // Second migrated request
        // Retries exhausted and return 'second migrated response'.
        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null))
                .add(httpResponse2.getMigratedMeta().orElse(null)).build();
        assertHttpResult(result3, migratableHttpResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindBdsaVulnerabilitiesV7WhenAbsent() {
        PageRequest pageRequest = new PageRequest(0, 100, Collections.emptyList());

        // HTTP 404 Not Found response
        String requestUri = constructVersionHref(COMPONENT_VERSION_ID, "/vulnerabilities-bdsa");
        HttpResponse<Page<BdsaVulnerability>> httpResponse = new HttpResponse<>(404, Set.of(200, 404), null, null);
        HttpResult<Page<BdsaVulnerability>> httpResult = constructHttpResult(REQUEST_METHOD, requestUri, httpResponse);

        Mockito.when(componentVersionApi.findBdsaVulnerabilitiesV7(pageRequest, COMPONENT_VERSION_ID, null)).thenReturn(httpResult);

        MigratableHttpResult<Page<BdsaVulnerability>> migratableHttpResult = bdComponentVersionApi.findBdsaVulnerabilitiesV7(pageRequest, COMPONENT_VERSION_ID,
                null);

        assertHttpResult(httpResult, migratableHttpResult, Collections.emptyList());
    }

    @Test
    public void testFindBdsaVulnerabilitiesV7WhenPresentAndNotMigrated() {
        PageRequest pageRequest = new PageRequest(0, 100, Collections.emptyList());

        // HTTP 200 OK response
        String requestUri = constructVersionHref(COMPONENT_VERSION_ID, "/vulnerabilities-bdsa");
        Page<BdsaVulnerability> bdsaVulnerabilityPage = constructBdsaVulnerabilityPage(COMPONENT_VERSION_ID);
        HttpResponse<Page<BdsaVulnerability>> httpResponse = constructHttpResponse(bdsaVulnerabilityPage);
        HttpResult<Page<BdsaVulnerability>> httpResult = constructHttpResult(REQUEST_METHOD, requestUri, httpResponse);

        Mockito.when(componentVersionApi.findBdsaVulnerabilitiesV7(pageRequest, COMPONENT_VERSION_ID, null)).thenReturn(httpResult);

        MigratableHttpResult<Page<BdsaVulnerability>> migratableHttpResult = bdComponentVersionApi.findBdsaVulnerabilitiesV7(pageRequest, COMPONENT_VERSION_ID,
                null);

        assertHttpResult(httpResult, migratableHttpResult, Collections.emptyList());
    }

    @Test
    public void testFindBdsaVulnerabilitiesV7WhenPresentAndMergeMigrated() {
        PageRequest pageRequest = new PageRequest(0, 100, Collections.emptyList());

        String requestUri1 = constructVersionHref(COMPONENT_VERSION_ID, "/vulnerabilities-bdsa");
        UUID destinationComponentVersionId2 = UUID.randomUUID();
        String requestUri2 = constructVersionHref(destinationComponentVersionId2, "/vulnerabilities-bdsa");

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Page<BdsaVulnerability>> httpResponse1 = constructMergeMigratedHttpResponse(requestUri1, requestUri2);
        HttpResult<Page<BdsaVulnerability>> result1 = constructHttpResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 200 OK response
        Page<BdsaVulnerability> bdsaVulnerabilityPage2 = constructBdsaVulnerabilityPage(destinationComponentVersionId2);
        HttpResponse<Page<BdsaVulnerability>> httpResponse2 = constructHttpResponse(bdsaVulnerabilityPage2);
        HttpResult<Page<BdsaVulnerability>> result2 = constructHttpResult(REQUEST_METHOD, requestUri2, httpResponse2);

        Mockito.when(componentVersionApi.findBdsaVulnerabilitiesV7(pageRequest, COMPONENT_VERSION_ID, null)).thenReturn(result1);
        Mockito.when(componentVersionApi.findBdsaVulnerabilitiesV7(pageRequest, destinationComponentVersionId2, null)).thenReturn(result2);

        MigratableHttpResult<Page<BdsaVulnerability>> migratableHttpResult = bdComponentVersionApi.findBdsaVulnerabilitiesV7(pageRequest, COMPONENT_VERSION_ID,
                null);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null)).build();
        assertHttpResult(result2, migratableHttpResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindBdsaVulnerabilitiesV7WhenPresentAndSplitMigrated() {
        PageRequest pageRequest = new PageRequest(0, 100, Collections.emptyList());

        String requestUri1 = constructVersionHref(COMPONENT_VERSION_ID, "/vulnerabilities-bdsa");
        UUID destinationComponentVersionId2a = UUID.randomUUID();
        UUID destinationComponentVersionId2b = UUID.randomUUID();
        UUID destinationComponentVersionId2c = UUID.randomUUID();
        String requestUri2a = constructVersionHref(destinationComponentVersionId2a, "/vulnerabilities-bdsa");
        String requestUri2b = constructVersionHref(destinationComponentVersionId2b, "/vulnerabilities-bdsa");
        String requestUri2c = constructVersionHref(destinationComponentVersionId2c, "/vulnerabilities-bdsa");

        // HTTP 300 Multiple Choices response (split migration)
        HttpResponse<Page<BdsaVulnerability>> httpResponse1 = constructSplitMigratedHttpResponse(requestUri1,
                List.of(requestUri2a, requestUri2b, requestUri2c));
        HttpResult<Page<BdsaVulnerability>> result1 = constructHttpResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 200 OK response
        Page<BdsaVulnerability> bdsaVulnerabilityPage2 = constructBdsaVulnerabilityPage(destinationComponentVersionId2a);
        HttpResponse<Page<BdsaVulnerability>> httpResponse2 = constructHttpResponse(bdsaVulnerabilityPage2);
        HttpResult<Page<BdsaVulnerability>> result2 = constructHttpResult(REQUEST_METHOD, requestUri2a, httpResponse2);

        Mockito.when(componentVersionApi.findBdsaVulnerabilitiesV7(pageRequest, COMPONENT_VERSION_ID, null)).thenReturn(result1);
        Mockito.when(componentVersionApi.findBdsaVulnerabilitiesV7(pageRequest, destinationComponentVersionId2a, null)).thenReturn(result2);

        MigratableHttpResult<Page<BdsaVulnerability>> migratableHttpResult = bdComponentVersionApi.findBdsaVulnerabilitiesV7(pageRequest, COMPONENT_VERSION_ID,
                null);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null)).build();
        assertHttpResult(result2, migratableHttpResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindBdsaVulnerabilitiesV7WhenPresentAndMigratedWithMultipleMigrations() {
        PageRequest pageRequest = new PageRequest(0, 100, Collections.emptyList());

        String requestUri1 = constructVersionHref(COMPONENT_VERSION_ID, "/vulnerabilities-bdsa");
        UUID destinationComponentVersionId2a = UUID.randomUUID();
        UUID destinationComponentVersionId2b = UUID.randomUUID();
        UUID destinationComponentVersionId2c = UUID.randomUUID();
        String requestUri2a = constructVersionHref(destinationComponentVersionId2a, "/vulnerabilities-bdsa");
        String requestUri2b = constructVersionHref(destinationComponentVersionId2b, "/vulnerabilities-bdsa");
        String requestUri2c = constructVersionHref(destinationComponentVersionId2c, "/vulnerabilities-bdsa");
        UUID destinationComponentVersionId3 = UUID.randomUUID();
        String requestUri3 = constructVersionHref(destinationComponentVersionId3, "/vulnerabilities-bdsa");

        // HTTP 300 Multiple Choices response (split migration)
        HttpResponse<Page<BdsaVulnerability>> httpResponse1 = constructSplitMigratedHttpResponse(requestUri1,
                List.of(requestUri2a, requestUri2b, requestUri2c));
        HttpResult<Page<BdsaVulnerability>> result1 = constructHttpResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Page<BdsaVulnerability>> httpResponse2 = constructMergeMigratedHttpResponse(requestUri2a, requestUri3);
        HttpResult<Page<BdsaVulnerability>> result2 = constructHttpResult(REQUEST_METHOD, requestUri2a, httpResponse2);

        // HTTP 200 OK response
        Page<BdsaVulnerability> bdsaVulnerabilityPage3 = constructBdsaVulnerabilityPage(destinationComponentVersionId3);
        HttpResponse<Page<BdsaVulnerability>> httpResponse3 = constructHttpResponse(bdsaVulnerabilityPage3);
        HttpResult<Page<BdsaVulnerability>> result3 = constructHttpResult(REQUEST_METHOD, requestUri3, httpResponse3);

        Mockito.when(componentVersionApi.findBdsaVulnerabilitiesV7(pageRequest, COMPONENT_VERSION_ID, null)).thenReturn(result1);
        Mockito.when(componentVersionApi.findBdsaVulnerabilitiesV7(pageRequest, destinationComponentVersionId2a, null)).thenReturn(result2);
        Mockito.when(componentVersionApi.findBdsaVulnerabilitiesV7(pageRequest, destinationComponentVersionId3, null)).thenReturn(result3);

        MigratableHttpResult<Page<BdsaVulnerability>> migratableHttpResult = bdComponentVersionApi.findBdsaVulnerabilitiesV7(pageRequest, COMPONENT_VERSION_ID,
                null);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null))
                .add(httpResponse2.getMigratedMeta().orElse(null)).build();
        assertHttpResult(result3, migratableHttpResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindBdsaVulnerabilitiesV7WhenPresentAndRetriesExhausted() {
        PageRequest pageRequest = new PageRequest(0, 100, Collections.emptyList());

        String requestUri1 = constructVersionHref(COMPONENT_VERSION_ID, "/vulnerabilities-bdsa");
        UUID destinationComponentVersionId2 = UUID.randomUUID();
        UUID destinationComponentVersionId3 = UUID.randomUUID();
        UUID destinationComponentVersionId4 = UUID.randomUUID();
        String requestUri2 = constructVersionHref(destinationComponentVersionId2, "/vulnerabilities-bdsa");
        String requestUri3 = constructVersionHref(destinationComponentVersionId3, "/vulnerabilities-bdsa");
        String requestUri4 = constructVersionHref(destinationComponentVersionId4, "/vulnerabilities-bdsa");

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Page<BdsaVulnerability>> httpResponse1 = constructMergeMigratedHttpResponse(requestUri1, requestUri2);
        HttpResult<Page<BdsaVulnerability>> result1 = constructHttpResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Page<BdsaVulnerability>> httpResponse2 = constructMergeMigratedHttpResponse(requestUri2, requestUri3);
        HttpResult<Page<BdsaVulnerability>> result2 = constructHttpResult(REQUEST_METHOD, requestUri2, httpResponse2);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Page<BdsaVulnerability>> httpResponse3 = constructMergeMigratedHttpResponse(requestUri3, requestUri4);
        HttpResult<Page<BdsaVulnerability>> result3 = constructHttpResult(REQUEST_METHOD, requestUri3, httpResponse3);

        Mockito.when(componentVersionApi.findBdsaVulnerabilitiesV7(pageRequest, COMPONENT_VERSION_ID, null)).thenReturn(result1);
        Mockito.when(componentVersionApi.findBdsaVulnerabilitiesV7(pageRequest, destinationComponentVersionId2, null)).thenReturn(result2);
        Mockito.when(componentVersionApi.findBdsaVulnerabilitiesV7(pageRequest, destinationComponentVersionId3, null)).thenReturn(result3);

        MigratableHttpResult<Page<BdsaVulnerability>> migratableHttpResult = bdComponentVersionApi.findBdsaVulnerabilitiesV7(pageRequest, COMPONENT_VERSION_ID,
                null);

        // Initial request
        // First migrated request
        // Second migrated request
        // Retries exhausted and return 'second migrated response'.
        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null))
                .add(httpResponse2.getMigratedMeta().orElse(null)).build();
        assertHttpResult(result3, migratableHttpResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindUpgradeGuidanceV4WhenAbsent() {
        // HTTP 404 Not Found response
        String requestUri = constructVersionHref(COMPONENT_VERSION_ID, "/upgrade-guidance");
        HttpResponse<UpgradeGuidance> httpResponse = new HttpResponse<>(404, Set.of(200, 404), null, null);
        HttpResult<UpgradeGuidance> httpResult = constructHttpResult(REQUEST_METHOD, requestUri, httpResponse);

        Mockito.when(componentVersionApi.findUpgradeGuidanceV4(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(httpResult);

        MigratableHttpResult<UpgradeGuidance> migratableHttpResult = bdComponentVersionApi.findUpgradeGuidanceV4(COMPONENT_VERSION_ID,
                VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3);

        assertHttpResult(httpResult, migratableHttpResult, Collections.emptyList());
    }

    @Test
    public void testFindUpgradeGuidanceV4WhenPresentAndNotMigrated() {
        // HTTP 200 OK response
        String requestUri = constructVersionHref(COMPONENT_VERSION_ID, "/upgrade-guidance");
        UpgradeGuidance upgradeGuidance = constructUpgradeGuidance(COMPONENT_ID, COMPONENT_VERSION_ID);
        HttpResponse<UpgradeGuidance> httpResponse = constructHttpResponse(upgradeGuidance);
        HttpResult<UpgradeGuidance> httpResult = constructHttpResult(REQUEST_METHOD, requestUri, httpResponse);

        Mockito.when(componentVersionApi.findUpgradeGuidanceV4(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(httpResult);

        MigratableHttpResult<UpgradeGuidance> migratableHttpResult = bdComponentVersionApi.findUpgradeGuidanceV4(COMPONENT_VERSION_ID,
                VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3);

        assertHttpResult(httpResult, migratableHttpResult, Collections.emptyList());
    }

    @Test
    public void testFindUpgradeGuidanceV4WhenPresentAndMergeMigrated() {
        String requestUri1 = constructVersionHref(COMPONENT_VERSION_ID, "/upgrade-guidance");
        UUID destinationComponentVersionId2 = UUID.randomUUID();
        String requestUri2 = constructVersionHref(destinationComponentVersionId2, "/upgrade-guidance");

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<UpgradeGuidance> httpResponse1 = constructMergeMigratedHttpResponse(requestUri1, requestUri2);
        HttpResult<UpgradeGuidance> result1 = constructHttpResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 200 OK response
        UpgradeGuidance upgradeGuidance2 = constructUpgradeGuidance(UUID.randomUUID(), destinationComponentVersionId2);
        HttpResponse<UpgradeGuidance> httpResponse2 = constructHttpResponse(upgradeGuidance2);
        HttpResult<UpgradeGuidance> result2 = constructHttpResult(REQUEST_METHOD, requestUri2, httpResponse2);

        Mockito.when(componentVersionApi.findUpgradeGuidanceV4(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result1);
        Mockito.when(
                componentVersionApi.findUpgradeGuidanceV4(destinationComponentVersionId2, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result2);

        MigratableHttpResult<UpgradeGuidance> migratableHttpResult = bdComponentVersionApi.findUpgradeGuidanceV4(COMPONENT_VERSION_ID,
                VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null)).build();
        assertHttpResult(result2, migratableHttpResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindUpgradeGuidanceV4WhenPresentAndSplitMigrated() {
        String requestUri1 = constructVersionHref(COMPONENT_VERSION_ID, "/upgrade-guidance");
        UUID destinationComponentVersionId2a = UUID.randomUUID();
        UUID destinationComponentVersionId2b = UUID.randomUUID();
        UUID destinationComponentVersionId2c = UUID.randomUUID();
        String requestUri2a = constructVersionHref(destinationComponentVersionId2a, "/upgrade-guidance");
        String requestUri2b = constructVersionHref(destinationComponentVersionId2b, "/upgrade-guidance");
        String requestUri2c = constructVersionHref(destinationComponentVersionId2c, "/upgrade-guidance");

        // HTTP 300 Multiple Choices response (split migration)
        HttpResponse<UpgradeGuidance> httpResponse1 = constructSplitMigratedHttpResponse(requestUri1, List.of(requestUri2a, requestUri2b, requestUri2c));
        HttpResult<UpgradeGuidance> result1 = constructHttpResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 200 OK response
        UpgradeGuidance upgradeGuidance2 = constructUpgradeGuidance(UUID.randomUUID(), destinationComponentVersionId2a);
        HttpResponse<UpgradeGuidance> httpResponse2 = constructHttpResponse(upgradeGuidance2);
        HttpResult<UpgradeGuidance> result2 = constructHttpResult(REQUEST_METHOD, requestUri2a, httpResponse2);

        Mockito.when(componentVersionApi.findUpgradeGuidanceV4(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result1);
        Mockito.when(
                componentVersionApi.findUpgradeGuidanceV4(destinationComponentVersionId2a, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result2);

        MigratableHttpResult<UpgradeGuidance> migratableHttpResult = bdComponentVersionApi.findUpgradeGuidanceV4(COMPONENT_VERSION_ID,
                VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null)).build();
        assertHttpResult(result2, migratableHttpResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindUpgradeGuidanceV4WhenPresentAndMigratedWithMultipleMigrations() {
        String requestUri1 = constructVersionHref(COMPONENT_VERSION_ID, "/upgrade-guidance");
        UUID destinationComponentVersionId2a = UUID.randomUUID();
        UUID destinationComponentVersionId2b = UUID.randomUUID();
        UUID destinationComponentVersionId2c = UUID.randomUUID();
        String requestUri2a = constructVersionHref(destinationComponentVersionId2a, "/upgrade-guidance");
        String requestUri2b = constructVersionHref(destinationComponentVersionId2b, "/upgrade-guidance");
        String requestUri2c = constructVersionHref(destinationComponentVersionId2c, "/upgrade-guidance");
        UUID destinationComponentVersionId3 = UUID.randomUUID();
        String requestUri3 = constructVersionHref(destinationComponentVersionId3, "/upgrade-guidance");

        // HTTP 300 Multiple Choices response (split migration)
        HttpResponse<UpgradeGuidance> httpResponse1 = constructSplitMigratedHttpResponse(requestUri1, List.of(requestUri2a, requestUri2b, requestUri2c));
        HttpResult<UpgradeGuidance> result1 = constructHttpResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<UpgradeGuidance> httpResponse2 = constructMergeMigratedHttpResponse(requestUri2a, requestUri3);
        HttpResult<UpgradeGuidance> result2 = constructHttpResult(REQUEST_METHOD, requestUri2a, httpResponse2);

        // HTTP 200 OK response
        UpgradeGuidance upgradeGuidance3 = constructUpgradeGuidance(UUID.randomUUID(), destinationComponentVersionId3);
        HttpResponse<UpgradeGuidance> httpResponse3 = constructHttpResponse(upgradeGuidance3);
        HttpResult<UpgradeGuidance> result3 = constructHttpResult(REQUEST_METHOD, requestUri3, httpResponse3);

        Mockito.when(componentVersionApi.findUpgradeGuidanceV4(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result1);
        Mockito.when(
                componentVersionApi.findUpgradeGuidanceV4(destinationComponentVersionId2a, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result2);
        Mockito.when(
                componentVersionApi.findUpgradeGuidanceV4(destinationComponentVersionId3, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result3);

        MigratableHttpResult<UpgradeGuidance> migratableHttpResult = bdComponentVersionApi.findUpgradeGuidanceV4(COMPONENT_VERSION_ID,
                VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null))
                .add(httpResponse2.getMigratedMeta().orElse(null)).build();
        assertHttpResult(result3, migratableHttpResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindUpgradeGuidanceV4WhenPresentAndRetriesExhausted() {
        String requestUri1 = constructVersionHref(COMPONENT_VERSION_ID, "/upgrade-guidance");
        UUID destinationComponentVersionId2 = UUID.randomUUID();
        UUID destinationComponentVersionId3 = UUID.randomUUID();
        UUID destinationComponentVersionId4 = UUID.randomUUID();
        String requestUri2 = constructVersionHref(destinationComponentVersionId2, "/upgrade-guidance");
        String requestUri3 = constructVersionHref(destinationComponentVersionId3, "/upgrade-guidance");
        String requestUri4 = constructVersionHref(destinationComponentVersionId4, "/upgrade-guidance");

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<UpgradeGuidance> httpResponse1 = constructMergeMigratedHttpResponse(requestUri1, requestUri2);
        HttpResult<UpgradeGuidance> result1 = constructHttpResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<UpgradeGuidance> httpResponse2 = constructMergeMigratedHttpResponse(requestUri2, requestUri3);
        HttpResult<UpgradeGuidance> result2 = constructHttpResult(REQUEST_METHOD, requestUri2, httpResponse2);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<UpgradeGuidance> httpResponse3 = constructMergeMigratedHttpResponse(requestUri3, requestUri4);
        HttpResult<UpgradeGuidance> result3 = constructHttpResult(REQUEST_METHOD, requestUri3, httpResponse3);

        Mockito.when(componentVersionApi.findUpgradeGuidanceV4(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result1);
        Mockito.when(
                componentVersionApi.findUpgradeGuidanceV4(destinationComponentVersionId2, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result2);
        Mockito.when(
                componentVersionApi.findUpgradeGuidanceV4(destinationComponentVersionId3, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result3);

        MigratableHttpResult<UpgradeGuidance> migratableHttpResult = bdComponentVersionApi.findUpgradeGuidanceV4(COMPONENT_VERSION_ID,
                VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3);

        // Initial request
        // First migrated request
        // Second migrated request
        // Retries exhausted and return 'second migrated response'.
        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null))
                .add(httpResponse2.getMigratedMeta().orElse(null)).build();
        assertHttpResult(result3, migratableHttpResult, expectedMigratedMetaHistory);
    }

    private UpgradeGuidance constructUpgradeGuidance(UUID componentId, UUID componentVersionId) {
        String shortTermVersionHref = BASE_HREF + "/api/versions/" + UUID.randomUUID();
        UpgradeGuidanceSuggestion shortTerm = new UpgradeGuidanceSuggestion(shortTermVersionHref, "2.0", null, null, null, null,
                new RiskProfile(0, 0, 0, 0, 0));
        String longTermVersionHref = BASE_HREF + "/api/versions/" + UUID.randomUUID();
        UpgradeGuidanceSuggestion longTerm = new UpgradeGuidanceSuggestion(longTermVersionHref, "3.0", null, null, null, null, new RiskProfile(0, 0, 0, 0, 0));

        String componentHref = BASE_HREF + "/api/components/" + componentId;
        String versionHref = BASE_HREF + "/api/versions/" + componentVersionId;
        Meta meta = new Meta(versionHref + "/upgrade-guidance", Collections.emptyList());

        return new UpgradeGuidance(componentHref, versionHref, null, "FooComponent", "1.0", null, null, null, shortTerm, longTerm, meta);
    }

    private Page<CveVulnerability> constructCveVulnerabilityPage(UUID componentVersionId) {
        CveVulnerability cveVulnerability1 = constructCveVulnerability("CVE-2023-0001", VulnerabilityStatus.AFFECTED);
        CveVulnerability cveVulnerability2 = constructCveVulnerability("CVE-2023-0002", VulnerabilityStatus.AFFECTED);
        CveVulnerability cveVulnerability3 = constructCveVulnerability("CVE-2023-0003", VulnerabilityStatus.AFFECTED);
        List<CveVulnerability> cveVulnerabilities = List.of(cveVulnerability1, cveVulnerability2, cveVulnerability3);
        String href = constructVersionHref(componentVersionId, "vulnerabilities-cve");
        Meta meta = new Meta(href, Collections.emptyList());

        return new Page<>(3, cveVulnerabilities, meta);
    }

    private Page<BdsaVulnerability> constructBdsaVulnerabilityPage(UUID componentVersionId) {
        BdsaVulnerability bdsaVulnerability1 = constructBdsaVulnerability("BDSA-2023-0001", VulnerabilityStatus.AFFECTED);
        BdsaVulnerability bdsaVulnerability2 = constructBdsaVulnerability("BDSA-2023-0002", VulnerabilityStatus.AFFECTED);
        BdsaVulnerability bdsaVulnerability3 = constructBdsaVulnerability("BDSA-2023-0003", VulnerabilityStatus.AFFECTED);
        List<BdsaVulnerability> bdsaVulnerabilities = List.of(bdsaVulnerability1, bdsaVulnerability2, bdsaVulnerability3);
        String href = constructVersionHref(componentVersionId, "vulnerabilities-bdsa");
        Meta meta = new Meta(href, Collections.emptyList());

        return new Page<>(3, bdsaVulnerabilities, meta);
    }

    private String constructVersionHref(UUID componentVersionId) {
        return constructVersionHref(componentVersionId, null);
    }

    private String constructNextVersionHref(UUID componentVersionId) {
        return constructVersionHref(componentVersionId, "/next");
    }

    private String constructVersionHref(UUID componentVersionId, @Nullable String pathSuffix) {
        String href = BASE_HREF + "/api/versions/" + componentVersionId;

        if (pathSuffix != null) {
            href = href + pathSuffix;
        }

        return href;
    }
}
