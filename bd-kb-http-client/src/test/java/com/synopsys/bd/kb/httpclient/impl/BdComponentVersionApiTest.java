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
import com.synopsys.bd.kb.httpclient.api.MigratableResult;
import com.synopsys.bd.kb.httpclient.model.BdComponentVersion;
import com.synopsys.kb.httpclient.api.HttpResponse;
import com.synopsys.kb.httpclient.api.IComponentVersionApi;
import com.synopsys.kb.httpclient.api.PageRequest;
import com.synopsys.kb.httpclient.api.Result;
import com.synopsys.kb.httpclient.model.BdsaVulnerability;
import com.synopsys.kb.httpclient.model.ComponentVersion;
import com.synopsys.kb.httpclient.model.CveVulnerability;
import com.synopsys.kb.httpclient.model.Link;
import com.synopsys.kb.httpclient.model.Meta;
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
    public void testFindWhenAbsent() {
        // HTTP 404 Not Found response
        String requestUri = constructVersionHref(COMPONENT_VERSION_ID);
        HttpResponse<ComponentVersion> httpResponse = new HttpResponse<>(404, Set.of(200, 404), null, null);
        Result<ComponentVersion> result = constructResult(REQUEST_METHOD, requestUri, httpResponse);

        Mockito.when(componentVersionApi.find(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3)).thenReturn(result);

        MigratableResult<BdComponentVersion> migratableResult = bdComponentVersionApi.find(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3);

        assertResult(result, migratableResult, Collections.emptyList());
    }

    @Test
    public void testFindWhenPresentAndNotMigrated() {
        // HTTP 200 OK response
        String requestUri = constructVersionHref(COMPONENT_VERSION_ID);
        ComponentVersion componentVersion = new ComponentVersion("1.0", OffsetDateTime.now(), null, new RiskProfile(0, 0, 0, 0, 0), Boolean.FALSE,
                Boolean.FALSE, COMPONENT_VERSION_META);
        HttpResponse<ComponentVersion> httpResponse = constructHttpResponse(componentVersion);
        Result<ComponentVersion> result = constructResult(REQUEST_METHOD, requestUri, httpResponse);

        Mockito.when(componentVersionApi.find(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3)).thenReturn(result);

        MigratableResult<BdComponentVersion> migratableResult = bdComponentVersionApi.find(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3);

        assertResult(result, migratableResult, Collections.emptyList());
    }

    @Test
    public void testFindWhenPresentAndMergeMigrated() {
        String requestUri1 = constructVersionHref(COMPONENT_VERSION_ID);
        UUID destinationComponentVersionId2 = UUID.randomUUID();
        String requestUri2 = constructVersionHref(destinationComponentVersionId2);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<ComponentVersion> httpResponse1 = constructMergeMigratedHttpResponse(requestUri1, requestUri2);
        Result<ComponentVersion> result1 = constructResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 200 OK response
        Meta meta2 = constructMeta(requestUri2);
        ComponentVersion componentVersion2 = new ComponentVersion("1.0", OffsetDateTime.now(), null, new RiskProfile(0, 0, 0, 0, 0), Boolean.FALSE,
                Boolean.FALSE, meta2);
        HttpResponse<ComponentVersion> httpResponse2 = constructHttpResponse(componentVersion2);
        Result<ComponentVersion> result2 = constructResult(REQUEST_METHOD, requestUri2, httpResponse2);

        Mockito.when(componentVersionApi.find(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3)).thenReturn(result1);
        Mockito.when(componentVersionApi.find(destinationComponentVersionId2, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result2);

        MigratableResult<BdComponentVersion> migratableResult = bdComponentVersionApi.find(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null)).build();
        assertResult(result2, migratableResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindWhenPresentAndSplitMigrated() {
        String requestUri1 = constructVersionHref(COMPONENT_VERSION_ID);
        UUID destinationComponentVersionId2a = UUID.randomUUID();
        UUID destinationComponentVersionId2b = UUID.randomUUID();
        UUID destinationComponentVersionId2c = UUID.randomUUID();
        String requestUri2a = constructVersionHref(destinationComponentVersionId2a);
        String requestUri2b = constructVersionHref(destinationComponentVersionId2b);
        String requestUri2c = constructVersionHref(destinationComponentVersionId2c);

        // HTTP 300 Multiple Choices response (split migration)
        HttpResponse<ComponentVersion> httpResponse1 = constructSplitMigratedHttpResponse(requestUri1, List.of(requestUri2a, requestUri2b, requestUri2c));
        Result<ComponentVersion> result1 = constructResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 200 OK response
        Meta meta2 = constructMeta(requestUri2a);
        ComponentVersion componentVersion2 = new ComponentVersion("1.0", OffsetDateTime.now(), null, new RiskProfile(0, 0, 0, 0, 0), Boolean.FALSE,
                Boolean.FALSE, meta2);
        HttpResponse<ComponentVersion> httpResponse2 = constructHttpResponse(componentVersion2);
        Result<ComponentVersion> result2 = constructResult(REQUEST_METHOD, requestUri2a, httpResponse2);

        Mockito.when(componentVersionApi.find(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3)).thenReturn(result1);
        Mockito.when(componentVersionApi.find(destinationComponentVersionId2a, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result2);

        MigratableResult<BdComponentVersion> migratableResult = bdComponentVersionApi.find(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null)).build();
        assertResult(result2, migratableResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindWhenPresentAndMigratedWithMultipleMigrations() {
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
        Result<ComponentVersion> result1 = constructResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<ComponentVersion> httpResponse2 = constructMergeMigratedHttpResponse(requestUri2a, requestUri3);
        Result<ComponentVersion> result2 = constructResult(REQUEST_METHOD, requestUri2a, httpResponse2);

        // HTTP 200 OK response
        Meta meta3 = constructMeta(requestUri3);
        ComponentVersion componentVersion3 = new ComponentVersion("1.0", OffsetDateTime.now(), null, new RiskProfile(0, 0, 0, 0, 0), Boolean.FALSE,
                Boolean.FALSE, meta3);
        HttpResponse<ComponentVersion> httpResponse3 = constructHttpResponse(componentVersion3);
        Result<ComponentVersion> result3 = constructResult(REQUEST_METHOD, requestUri3, httpResponse3);

        Mockito.when(componentVersionApi.find(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3)).thenReturn(result1);
        Mockito.when(componentVersionApi.find(destinationComponentVersionId2a, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result2);
        Mockito.when(componentVersionApi.find(destinationComponentVersionId3, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result3);

        MigratableResult<BdComponentVersion> migratableResult = bdComponentVersionApi.find(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null))
                .add(httpResponse2.getMigratedMeta().orElse(null)).build();
        assertResult(result3, migratableResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindWhenPresentAndRetriesExhausted() {
        String requestUri1 = constructVersionHref(COMPONENT_VERSION_ID);
        UUID destinationComponentVersionId2 = UUID.randomUUID();
        UUID destinationComponentVersionId3 = UUID.randomUUID();
        UUID destinationComponentVersionId4 = UUID.randomUUID();
        String requestUri2 = constructVersionHref(destinationComponentVersionId2);
        String requestUri3 = constructVersionHref(destinationComponentVersionId3);
        String requestUri4 = constructVersionHref(destinationComponentVersionId4);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<ComponentVersion> httpResponse1 = constructMergeMigratedHttpResponse(requestUri1, requestUri2);
        Result<ComponentVersion> result1 = constructResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<ComponentVersion> httpResponse2 = constructMergeMigratedHttpResponse(requestUri2, requestUri3);
        Result<ComponentVersion> result2 = constructResult(REQUEST_METHOD, requestUri2, httpResponse2);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<ComponentVersion> httpResponse3 = constructMergeMigratedHttpResponse(requestUri3, requestUri4);
        Result<ComponentVersion> result3 = constructResult(REQUEST_METHOD, requestUri3, httpResponse3);

        Mockito.when(componentVersionApi.find(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3)).thenReturn(result1);
        Mockito.when(componentVersionApi.find(destinationComponentVersionId2, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result2);
        Mockito.when(componentVersionApi.find(destinationComponentVersionId3, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result3);

        MigratableResult<BdComponentVersion> migratableResult = bdComponentVersionApi.find(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3);

        // Initial request
        // First migrated request
        // Second migrated request
        // Retries exhausted and return 'second migrated response'.
        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null))
                .add(httpResponse2.getMigratedMeta().orElse(null)).build();
        assertResult(result3, migratableResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindCveVulnerabilitiesWhenAbsent() {
        PageRequest pageRequest = new PageRequest(0, 100, Collections.emptyList());

        // HTTP 404 Not Found response
        String requestUri = constructVersionHref(COMPONENT_VERSION_ID, "/vulnerabilities-cve");
        HttpResponse<Page<CveVulnerability>> httpResponse = new HttpResponse<>(404, Set.of(200, 404), null, null);
        Result<Page<CveVulnerability>> result = constructResult(REQUEST_METHOD, requestUri, httpResponse);

        Mockito.when(componentVersionApi.findCveVulnerabilities(pageRequest, COMPONENT_VERSION_ID, null)).thenReturn(result);

        MigratableResult<Page<CveVulnerability>> migratableResult = bdComponentVersionApi.findCveVulnerabilities(pageRequest, COMPONENT_VERSION_ID, null);

        assertResult(result, migratableResult, Collections.emptyList());
    }

    @Test
    public void testFindCveVulnerabilitiesWhenPresentAndNotMigrated() {
        PageRequest pageRequest = new PageRequest(0, 100, Collections.emptyList());

        // HTTP 200 OK response
        String requestUri = constructVersionHref(COMPONENT_VERSION_ID, "/vulnerabilities-cve");
        Page<CveVulnerability> cveVulnerabilityPage = constructCveVulnerabilityPage(COMPONENT_VERSION_ID);
        HttpResponse<Page<CveVulnerability>> httpResponse = constructHttpResponse(cveVulnerabilityPage);
        Result<Page<CveVulnerability>> result = constructResult(REQUEST_METHOD, requestUri, httpResponse);

        Mockito.when(componentVersionApi.findCveVulnerabilities(pageRequest, COMPONENT_VERSION_ID, null)).thenReturn(result);

        MigratableResult<Page<CveVulnerability>> migratableResult = bdComponentVersionApi.findCveVulnerabilities(pageRequest, COMPONENT_VERSION_ID, null);

        assertResult(result, migratableResult, Collections.emptyList());
    }

    @Test
    public void testFindCveVulnerabilitiesWhenPresentAndMergeMigrated() {
        PageRequest pageRequest = new PageRequest(0, 100, Collections.emptyList());

        String requestUri1 = constructVersionHref(COMPONENT_VERSION_ID, "/vulnerabilities-cve");
        UUID destinationComponentVersionId2 = UUID.randomUUID();
        String requestUri2 = constructVersionHref(destinationComponentVersionId2, "/vulnerabilities-cve");

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Page<CveVulnerability>> httpResponse1 = constructMergeMigratedHttpResponse(requestUri1, requestUri2);
        Result<Page<CveVulnerability>> result1 = constructResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 200 OK response
        Page<CveVulnerability> cveVulnerabilityPage2 = constructCveVulnerabilityPage(destinationComponentVersionId2);
        HttpResponse<Page<CveVulnerability>> httpResponse2 = constructHttpResponse(cveVulnerabilityPage2);
        Result<Page<CveVulnerability>> result2 = constructResult(REQUEST_METHOD, requestUri2, httpResponse2);

        Mockito.when(componentVersionApi.findCveVulnerabilities(pageRequest, COMPONENT_VERSION_ID, null)).thenReturn(result1);
        Mockito.when(componentVersionApi.findCveVulnerabilities(pageRequest, destinationComponentVersionId2, null)).thenReturn(result2);

        MigratableResult<Page<CveVulnerability>> migratableResult = bdComponentVersionApi.findCveVulnerabilities(pageRequest, COMPONENT_VERSION_ID, null);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null)).build();
        assertResult(result2, migratableResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindCveVulnerabilitiesWhenPresentAndSplitMigrated() {
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
        Result<Page<CveVulnerability>> result1 = constructResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 200 OK response
        Page<CveVulnerability> cveVulnerabilityPage2 = constructCveVulnerabilityPage(destinationComponentVersionId2a);
        HttpResponse<Page<CveVulnerability>> httpResponse2 = constructHttpResponse(cveVulnerabilityPage2);
        Result<Page<CveVulnerability>> result2 = constructResult(REQUEST_METHOD, requestUri2a, httpResponse2);

        Mockito.when(componentVersionApi.findCveVulnerabilities(pageRequest, COMPONENT_VERSION_ID, null)).thenReturn(result1);
        Mockito.when(componentVersionApi.findCveVulnerabilities(pageRequest, destinationComponentVersionId2a, null)).thenReturn(result2);

        MigratableResult<Page<CveVulnerability>> migratableResult = bdComponentVersionApi.findCveVulnerabilities(pageRequest, COMPONENT_VERSION_ID, null);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null)).build();
        assertResult(result2, migratableResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindCveVulnerabilitiesWhenPresentAndMigratedWithMultipleMigrations() {
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
        Result<Page<CveVulnerability>> result1 = constructResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Page<CveVulnerability>> httpResponse2 = constructMergeMigratedHttpResponse(requestUri2a, requestUri3);
        Result<Page<CveVulnerability>> result2 = constructResult(REQUEST_METHOD, requestUri2a, httpResponse2);

        // HTTP 200 OK response
        Page<CveVulnerability> cveVulnerabilityPage3 = constructCveVulnerabilityPage(destinationComponentVersionId3);
        HttpResponse<Page<CveVulnerability>> httpResponse3 = constructHttpResponse(cveVulnerabilityPage3);
        Result<Page<CveVulnerability>> result3 = constructResult(REQUEST_METHOD, requestUri3, httpResponse3);

        Mockito.when(componentVersionApi.findCveVulnerabilities(pageRequest, COMPONENT_VERSION_ID, null)).thenReturn(result1);
        Mockito.when(componentVersionApi.findCveVulnerabilities(pageRequest, destinationComponentVersionId2a, null)).thenReturn(result2);
        Mockito.when(componentVersionApi.findCveVulnerabilities(pageRequest, destinationComponentVersionId3, null)).thenReturn(result3);

        MigratableResult<Page<CveVulnerability>> migratableResult = bdComponentVersionApi.findCveVulnerabilities(pageRequest, COMPONENT_VERSION_ID, null);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null))
                .add(httpResponse2.getMigratedMeta().orElse(null)).build();
        assertResult(result3, migratableResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindCveVulnerabilitiesWhenPresentAndRetriesExhausted() {
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
        Result<Page<CveVulnerability>> result1 = constructResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Page<CveVulnerability>> httpResponse2 = constructMergeMigratedHttpResponse(requestUri2, requestUri3);
        Result<Page<CveVulnerability>> result2 = constructResult(REQUEST_METHOD, requestUri2, httpResponse2);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Page<CveVulnerability>> httpResponse3 = constructMergeMigratedHttpResponse(requestUri3, requestUri4);
        Result<Page<CveVulnerability>> result3 = constructResult(REQUEST_METHOD, requestUri3, httpResponse3);

        Mockito.when(componentVersionApi.findCveVulnerabilities(pageRequest, COMPONENT_VERSION_ID, null)).thenReturn(result1);
        Mockito.when(componentVersionApi.findCveVulnerabilities(pageRequest, destinationComponentVersionId2, null)).thenReturn(result2);
        Mockito.when(componentVersionApi.findCveVulnerabilities(pageRequest, destinationComponentVersionId3, null)).thenReturn(result3);

        MigratableResult<Page<CveVulnerability>> migratableResult = bdComponentVersionApi.findCveVulnerabilities(pageRequest, COMPONENT_VERSION_ID, null);

        // Initial request
        // First migrated request
        // Second migrated request
        // Retries exhausted and return 'second migrated response'.
        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null))
                .add(httpResponse2.getMigratedMeta().orElse(null)).build();
        assertResult(result3, migratableResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindBdsaVulnerabilitiesWhenAbsent() {
        PageRequest pageRequest = new PageRequest(0, 100, Collections.emptyList());

        // HTTP 404 Not Found response
        String requestUri = constructVersionHref(COMPONENT_VERSION_ID, "/vulnerabilities-bdsa");
        HttpResponse<Page<BdsaVulnerability>> httpResponse = new HttpResponse<>(404, Set.of(200, 404), null, null);
        Result<Page<BdsaVulnerability>> result = constructResult(REQUEST_METHOD, requestUri, httpResponse);

        Mockito.when(componentVersionApi.findBdsaVulnerabilities(pageRequest, COMPONENT_VERSION_ID, null)).thenReturn(result);

        MigratableResult<Page<BdsaVulnerability>> migratableResult = bdComponentVersionApi.findBdsaVulnerabilities(pageRequest, COMPONENT_VERSION_ID, null);

        assertResult(result, migratableResult, Collections.emptyList());
    }

    @Test
    public void testFindBdsaVulnerabilitiesWhenPresentAndNotMigrated() {
        PageRequest pageRequest = new PageRequest(0, 100, Collections.emptyList());

        // HTTP 200 OK response
        String requestUri = constructVersionHref(COMPONENT_VERSION_ID, "/vulnerabilities-bdsa");
        Page<BdsaVulnerability> bdsaVulnerabilityPage = constructBdsaVulnerabilityPage(COMPONENT_VERSION_ID);
        HttpResponse<Page<BdsaVulnerability>> httpResponse = constructHttpResponse(bdsaVulnerabilityPage);
        Result<Page<BdsaVulnerability>> result = constructResult(REQUEST_METHOD, requestUri, httpResponse);

        Mockito.when(componentVersionApi.findBdsaVulnerabilities(pageRequest, COMPONENT_VERSION_ID, null)).thenReturn(result);

        MigratableResult<Page<BdsaVulnerability>> migratableResult = bdComponentVersionApi.findBdsaVulnerabilities(pageRequest, COMPONENT_VERSION_ID, null);

        assertResult(result, migratableResult, Collections.emptyList());
    }

    @Test
    public void testFindBdsaVulnerabilitiesWhenPresentAndMergeMigrated() {
        PageRequest pageRequest = new PageRequest(0, 100, Collections.emptyList());

        String requestUri1 = constructVersionHref(COMPONENT_VERSION_ID, "/vulnerabilities-bdsa");
        UUID destinationComponentVersionId2 = UUID.randomUUID();
        String requestUri2 = constructVersionHref(destinationComponentVersionId2, "/vulnerabilities-bdsa");

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Page<BdsaVulnerability>> httpResponse1 = constructMergeMigratedHttpResponse(requestUri1, requestUri2);
        Result<Page<BdsaVulnerability>> result1 = constructResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 200 OK response
        Page<BdsaVulnerability> bdsaVulnerabilityPage2 = constructBdsaVulnerabilityPage(destinationComponentVersionId2);
        HttpResponse<Page<BdsaVulnerability>> httpResponse2 = constructHttpResponse(bdsaVulnerabilityPage2);
        Result<Page<BdsaVulnerability>> result2 = constructResult(REQUEST_METHOD, requestUri2, httpResponse2);

        Mockito.when(componentVersionApi.findBdsaVulnerabilities(pageRequest, COMPONENT_VERSION_ID, null)).thenReturn(result1);
        Mockito.when(componentVersionApi.findBdsaVulnerabilities(pageRequest, destinationComponentVersionId2, null)).thenReturn(result2);

        MigratableResult<Page<BdsaVulnerability>> migratableResult = bdComponentVersionApi.findBdsaVulnerabilities(pageRequest, COMPONENT_VERSION_ID, null);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null)).build();
        assertResult(result2, migratableResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindBdsaVulnerabilitiesWhenPresentAndSplitMigrated() {
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
        Result<Page<BdsaVulnerability>> result1 = constructResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 200 OK response
        Page<BdsaVulnerability> bdsaVulnerabilityPage2 = constructBdsaVulnerabilityPage(destinationComponentVersionId2a);
        HttpResponse<Page<BdsaVulnerability>> httpResponse2 = constructHttpResponse(bdsaVulnerabilityPage2);
        Result<Page<BdsaVulnerability>> result2 = constructResult(REQUEST_METHOD, requestUri2a, httpResponse2);

        Mockito.when(componentVersionApi.findBdsaVulnerabilities(pageRequest, COMPONENT_VERSION_ID, null)).thenReturn(result1);
        Mockito.when(componentVersionApi.findBdsaVulnerabilities(pageRequest, destinationComponentVersionId2a, null)).thenReturn(result2);

        MigratableResult<Page<BdsaVulnerability>> migratableResult = bdComponentVersionApi.findBdsaVulnerabilities(pageRequest, COMPONENT_VERSION_ID, null);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null)).build();
        assertResult(result2, migratableResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindBdsaVulnerabilitiesWhenPresentAndMigratedWithMultipleMigrations() {
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
        Result<Page<BdsaVulnerability>> result1 = constructResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Page<BdsaVulnerability>> httpResponse2 = constructMergeMigratedHttpResponse(requestUri2a, requestUri3);
        Result<Page<BdsaVulnerability>> result2 = constructResult(REQUEST_METHOD, requestUri2a, httpResponse2);

        // HTTP 200 OK response
        Page<BdsaVulnerability> bdsaVulnerabilityPage3 = constructBdsaVulnerabilityPage(destinationComponentVersionId3);
        HttpResponse<Page<BdsaVulnerability>> httpResponse3 = constructHttpResponse(bdsaVulnerabilityPage3);
        Result<Page<BdsaVulnerability>> result3 = constructResult(REQUEST_METHOD, requestUri3, httpResponse3);

        Mockito.when(componentVersionApi.findBdsaVulnerabilities(pageRequest, COMPONENT_VERSION_ID, null)).thenReturn(result1);
        Mockito.when(componentVersionApi.findBdsaVulnerabilities(pageRequest, destinationComponentVersionId2a, null)).thenReturn(result2);
        Mockito.when(componentVersionApi.findBdsaVulnerabilities(pageRequest, destinationComponentVersionId3, null)).thenReturn(result3);

        MigratableResult<Page<BdsaVulnerability>> migratableResult = bdComponentVersionApi.findBdsaVulnerabilities(pageRequest, COMPONENT_VERSION_ID, null);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null))
                .add(httpResponse2.getMigratedMeta().orElse(null)).build();
        assertResult(result3, migratableResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindBdsaVulnerabilitiesWhenPresentAndRetriesExhausted() {
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
        Result<Page<BdsaVulnerability>> result1 = constructResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Page<BdsaVulnerability>> httpResponse2 = constructMergeMigratedHttpResponse(requestUri2, requestUri3);
        Result<Page<BdsaVulnerability>> result2 = constructResult(REQUEST_METHOD, requestUri2, httpResponse2);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Page<BdsaVulnerability>> httpResponse3 = constructMergeMigratedHttpResponse(requestUri3, requestUri4);
        Result<Page<BdsaVulnerability>> result3 = constructResult(REQUEST_METHOD, requestUri3, httpResponse3);

        Mockito.when(componentVersionApi.findBdsaVulnerabilities(pageRequest, COMPONENT_VERSION_ID, null)).thenReturn(result1);
        Mockito.when(componentVersionApi.findBdsaVulnerabilities(pageRequest, destinationComponentVersionId2, null)).thenReturn(result2);
        Mockito.when(componentVersionApi.findBdsaVulnerabilities(pageRequest, destinationComponentVersionId3, null)).thenReturn(result3);

        MigratableResult<Page<BdsaVulnerability>> migratableResult = bdComponentVersionApi.findBdsaVulnerabilities(pageRequest, COMPONENT_VERSION_ID, null);

        // Initial request
        // First migrated request
        // Second migrated request
        // Retries exhausted and return 'second migrated response'.
        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null))
                .add(httpResponse2.getMigratedMeta().orElse(null)).build();
        assertResult(result3, migratableResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindUpgradeGuidanceWhenAbsent() {
        // HTTP 404 Not Found response
        String requestUri = constructVersionHref(COMPONENT_VERSION_ID, "/upgrade-guidance");
        HttpResponse<UpgradeGuidance> httpResponse = new HttpResponse<>(404, Set.of(200, 404), null, null);
        Result<UpgradeGuidance> result = constructResult(REQUEST_METHOD, requestUri, httpResponse);

        Mockito.when(componentVersionApi.findUpgradeGuidance(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result);

        MigratableResult<UpgradeGuidance> migratableResult = bdComponentVersionApi.findUpgradeGuidance(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3);

        assertResult(result, migratableResult, Collections.emptyList());
    }

    @Test
    public void testFindUpgradeGuidanceWhenPresentAndNotMigrated() {
        // HTTP 200 OK response
        String requestUri = constructVersionHref(COMPONENT_VERSION_ID, "/upgrade-guidance");
        UpgradeGuidance upgradeGuidance = constructUpgradeGuidance(COMPONENT_ID, COMPONENT_VERSION_ID);
        HttpResponse<UpgradeGuidance> httpResponse = constructHttpResponse(upgradeGuidance);
        Result<UpgradeGuidance> result = constructResult(REQUEST_METHOD, requestUri, httpResponse);

        Mockito.when(componentVersionApi.findUpgradeGuidance(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result);

        MigratableResult<UpgradeGuidance> migratableResult = bdComponentVersionApi.findUpgradeGuidance(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3);

        assertResult(result, migratableResult, Collections.emptyList());
    }

    @Test
    public void testFindUpgradeGuidanceWhenPresentAndMergeMigrated() {
        String requestUri1 = constructVersionHref(COMPONENT_VERSION_ID, "/upgrade-guidance");
        UUID destinationComponentVersionId2 = UUID.randomUUID();
        String requestUri2 = constructVersionHref(destinationComponentVersionId2, "/upgrade-guidance");

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<UpgradeGuidance> httpResponse1 = constructMergeMigratedHttpResponse(requestUri1, requestUri2);
        Result<UpgradeGuidance> result1 = constructResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 200 OK response
        UpgradeGuidance upgradeGuidance2 = constructUpgradeGuidance(UUID.randomUUID(), destinationComponentVersionId2);
        HttpResponse<UpgradeGuidance> httpResponse2 = constructHttpResponse(upgradeGuidance2);
        Result<UpgradeGuidance> result2 = constructResult(REQUEST_METHOD, requestUri2, httpResponse2);

        Mockito.when(componentVersionApi.findUpgradeGuidance(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result1);
        Mockito.when(
                componentVersionApi.findUpgradeGuidance(destinationComponentVersionId2, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result2);

        MigratableResult<UpgradeGuidance> migratableResult = bdComponentVersionApi.findUpgradeGuidance(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null)).build();
        assertResult(result2, migratableResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindUpgradeGuidanceWhenPresentAndSplitMigrated() {
        String requestUri1 = constructVersionHref(COMPONENT_VERSION_ID, "/upgrade-guidance");
        UUID destinationComponentVersionId2a = UUID.randomUUID();
        UUID destinationComponentVersionId2b = UUID.randomUUID();
        UUID destinationComponentVersionId2c = UUID.randomUUID();
        String requestUri2a = constructVersionHref(destinationComponentVersionId2a, "/upgrade-guidance");
        String requestUri2b = constructVersionHref(destinationComponentVersionId2b, "/upgrade-guidance");
        String requestUri2c = constructVersionHref(destinationComponentVersionId2c, "/upgrade-guidance");

        // HTTP 300 Multiple Choices response (split migration)
        HttpResponse<UpgradeGuidance> httpResponse1 = constructSplitMigratedHttpResponse(requestUri1, List.of(requestUri2a, requestUri2b, requestUri2c));
        Result<UpgradeGuidance> result1 = constructResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 200 OK response
        UpgradeGuidance upgradeGuidance2 = constructUpgradeGuidance(UUID.randomUUID(), destinationComponentVersionId2a);
        HttpResponse<UpgradeGuidance> httpResponse2 = constructHttpResponse(upgradeGuidance2);
        Result<UpgradeGuidance> result2 = constructResult(REQUEST_METHOD, requestUri2a, httpResponse2);

        Mockito.when(componentVersionApi.findUpgradeGuidance(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result1);
        Mockito.when(
                componentVersionApi.findUpgradeGuidance(destinationComponentVersionId2a, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result2);

        MigratableResult<UpgradeGuidance> migratableResult = bdComponentVersionApi.findUpgradeGuidance(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null)).build();
        assertResult(result2, migratableResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindUpgradeGuidanceWhenPresentAndMigratedWithMultipleMigrations() {
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
        Result<UpgradeGuidance> result1 = constructResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<UpgradeGuidance> httpResponse2 = constructMergeMigratedHttpResponse(requestUri2a, requestUri3);
        Result<UpgradeGuidance> result2 = constructResult(REQUEST_METHOD, requestUri2a, httpResponse2);

        // HTTP 200 OK response
        UpgradeGuidance upgradeGuidance3 = constructUpgradeGuidance(UUID.randomUUID(), destinationComponentVersionId3);
        HttpResponse<UpgradeGuidance> httpResponse3 = constructHttpResponse(upgradeGuidance3);
        Result<UpgradeGuidance> result3 = constructResult(REQUEST_METHOD, requestUri3, httpResponse3);

        Mockito.when(componentVersionApi.findUpgradeGuidance(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result1);
        Mockito.when(
                componentVersionApi.findUpgradeGuidance(destinationComponentVersionId2a, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result2);
        Mockito.when(
                componentVersionApi.findUpgradeGuidance(destinationComponentVersionId3, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result3);

        MigratableResult<UpgradeGuidance> migratableResult = bdComponentVersionApi.findUpgradeGuidance(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null))
                .add(httpResponse2.getMigratedMeta().orElse(null)).build();
        assertResult(result3, migratableResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindUpgradeGuidanceWhenPresentAndRetriesExhausted() {
        String requestUri1 = constructVersionHref(COMPONENT_VERSION_ID, "/upgrade-guidance");
        UUID destinationComponentVersionId2 = UUID.randomUUID();
        UUID destinationComponentVersionId3 = UUID.randomUUID();
        UUID destinationComponentVersionId4 = UUID.randomUUID();
        String requestUri2 = constructVersionHref(destinationComponentVersionId2, "/upgrade-guidance");
        String requestUri3 = constructVersionHref(destinationComponentVersionId3, "/upgrade-guidance");
        String requestUri4 = constructVersionHref(destinationComponentVersionId4, "/upgrade-guidance");

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<UpgradeGuidance> httpResponse1 = constructMergeMigratedHttpResponse(requestUri1, requestUri2);
        Result<UpgradeGuidance> result1 = constructResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<UpgradeGuidance> httpResponse2 = constructMergeMigratedHttpResponse(requestUri2, requestUri3);
        Result<UpgradeGuidance> result2 = constructResult(REQUEST_METHOD, requestUri2, httpResponse2);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<UpgradeGuidance> httpResponse3 = constructMergeMigratedHttpResponse(requestUri3, requestUri4);
        Result<UpgradeGuidance> result3 = constructResult(REQUEST_METHOD, requestUri3, httpResponse3);

        Mockito.when(componentVersionApi.findUpgradeGuidance(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result1);
        Mockito.when(
                componentVersionApi.findUpgradeGuidance(destinationComponentVersionId2, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result2);
        Mockito.when(
                componentVersionApi.findUpgradeGuidance(destinationComponentVersionId3, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result3);

        MigratableResult<UpgradeGuidance> migratableResult = bdComponentVersionApi.findUpgradeGuidance(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3);

        // Initial request
        // First migrated request
        // Second migrated request
        // Retries exhausted and return 'second migrated response'.
        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null))
                .add(httpResponse2.getMigratedMeta().orElse(null)).build();
        assertResult(result3, migratableResult, expectedMigratedMetaHistory);
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

    private String constructVersionHref(UUID componentVersionId, @Nullable String pathSuffix) {
        String href = BASE_HREF + "/api/versions/" + componentVersionId;

        if (pathSuffix != null) {
            href = href + pathSuffix;
        }

        return href;
    }
}
