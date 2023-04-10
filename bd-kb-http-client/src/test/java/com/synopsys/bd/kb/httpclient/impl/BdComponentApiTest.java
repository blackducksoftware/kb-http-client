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

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.synopsys.bd.kb.httpclient.AbstractBdTest;
import com.synopsys.bd.kb.httpclient.api.IBdComponentApi;
import com.synopsys.bd.kb.httpclient.api.MigratableResult;
import com.synopsys.bd.kb.httpclient.model.BdComponentVersion;
import com.synopsys.bd.kb.httpclient.model.BdComponentVersionSummary;
import com.synopsys.kb.httpclient.api.HttpResponse;
import com.synopsys.kb.httpclient.api.IComponentApi;
import com.synopsys.kb.httpclient.api.PageRequest;
import com.synopsys.kb.httpclient.api.Result;
import com.synopsys.kb.httpclient.model.Component;
import com.synopsys.kb.httpclient.model.ComponentVersion;
import com.synopsys.kb.httpclient.model.ComponentVersionSummary;
import com.synopsys.kb.httpclient.model.Logo;
import com.synopsys.kb.httpclient.model.Meta;
import com.synopsys.kb.httpclient.model.Page;
import com.synopsys.kb.httpclient.model.VulnerabilityScorePriority;
import com.synopsys.kb.httpclient.model.VulnerabilitySourcePriority;

/**
 * Black Duck-centric component API test.
 * 
 * @author skatzman
 */
public class BdComponentApiTest extends AbstractBdTest {
    private static final String REQUEST_METHOD = "GET";

    private static final UUID COMPONENT_ID = UUID.randomUUID();

    private static final String NAME = "FooComponent";

    private static final String DESCRIPTION = "This is a component description.";

    private static final String PRIMARY_LANGUAGE = "Java";

    private static final Set<String> TAGS = Set.of("kb", "http", "client");

    private static final Set<Logo> LOGOS = Set.of(new Logo("small", "http://www.small.com"),
            new Logo("medium", "http://www.medium.com"),
            new Logo("large", "http://www.large.com"));

    private static final Boolean DELETED = Boolean.FALSE;

    private static final String HREF = "https://kbtest.blackducksoftware.com/api/components/" + COMPONENT_ID;

    private static final Meta META = new Meta(HREF, Collections.emptyList());

    @Mock
    private IComponentApi componentApi;

    private IBdComponentApi bdComponentApi;

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.openMocks(this);

        // Limit max attempts to simplify migration testing.
        this.bdComponentApi = new BdComponentApi(componentApi, BASE_HREF, 3);
    }

    @Test
    public void testFindWhenAbsent() {
        // HTTP 404 Not Found response
        String requestUri = constructComponentHref(COMPONENT_ID);
        HttpResponse<Component> httpResponse = new HttpResponse<>(404, Set.of(200, 404), null, null);
        Result<Component> result = constructResult(REQUEST_METHOD, requestUri, httpResponse);

        Mockito.when(componentApi.find(COMPONENT_ID)).thenReturn(result);

        MigratableResult<Component> migratableResult = bdComponentApi.find(COMPONENT_ID);

        assertResult(result, migratableResult, Collections.emptyList());
    }

    @Test
    public void testFindWhenPresentAndNotMigrated() {
        // HTTP 200 OK response
        String requestUri = constructComponentHref(COMPONENT_ID);
        Component component = new Component(NAME, DESCRIPTION, PRIMARY_LANGUAGE, TAGS, LOGOS, DELETED, META);
        HttpResponse<Component> httpResponse = constructHttpResponse(component);
        Result<Component> result = constructResult(REQUEST_METHOD, requestUri, httpResponse);

        Mockito.when(componentApi.find(COMPONENT_ID)).thenReturn(result);

        MigratableResult<Component> migratableResult = bdComponentApi.find(COMPONENT_ID);

        assertResult(result, migratableResult, Collections.emptyList());
    }

    @Test
    public void testFindWhenPresentAndMergeMigrated() {
        String requestUri1 = constructComponentHref(COMPONENT_ID);
        UUID destinationComponentId2 = UUID.randomUUID();
        String requestUri2 = constructComponentHref(destinationComponentId2);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Component> httpResponse1 = constructMergeMigratedHttpResponse(requestUri1, requestUri2);
        Result<Component> result1 = constructResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 200 OK response
        Meta meta2 = constructMeta(requestUri2);
        Component component2 = new Component("DestinationComponent", DESCRIPTION, PRIMARY_LANGUAGE, TAGS, LOGOS, DELETED, meta2);
        HttpResponse<Component> httpResponse2 = constructHttpResponse(component2);
        Result<Component> result2 = constructResult(REQUEST_METHOD, requestUri2, httpResponse2);

        Mockito.when(componentApi.find(COMPONENT_ID)).thenReturn(result1);
        Mockito.when(componentApi.find(destinationComponentId2)).thenReturn(result2);

        MigratableResult<Component> migratableResult = bdComponentApi.find(COMPONENT_ID);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null)).build();
        assertResult(result2, migratableResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindWhenPresentAndSplitMigrated() {
        String requestUri1 = constructComponentHref(COMPONENT_ID);
        UUID destinationComponentId2a = UUID.randomUUID();
        UUID destinationComponentId2b = UUID.randomUUID();
        UUID destinationComponentId2c = UUID.randomUUID();
        String requestUri2a = constructComponentHref(destinationComponentId2a);
        String requestUri2b = constructComponentHref(destinationComponentId2b);
        String requestUri2c = constructComponentHref(destinationComponentId2c);

        // HTTP 300 Multiple Choices response (split migration)
        HttpResponse<Component> httpResponse1 = constructSplitMigratedHttpResponse(requestUri1, List.of(requestUri2a, requestUri2b, requestUri2c));
        Result<Component> result1 = constructResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 200 OK response
        Meta meta2 = constructMeta(requestUri2a);
        Component component2 = new Component("DestinationComponent", DESCRIPTION, PRIMARY_LANGUAGE, TAGS, LOGOS, DELETED, meta2);
        HttpResponse<Component> httpResponse2 = constructHttpResponse(component2);
        Result<Component> result2 = constructResult(REQUEST_METHOD, requestUri2a, httpResponse2);

        Mockito.when(componentApi.find(COMPONENT_ID)).thenReturn(result1);
        Mockito.when(componentApi.find(destinationComponentId2a)).thenReturn(result2);

        MigratableResult<Component> migratableResult = bdComponentApi.find(COMPONENT_ID);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null)).build();
        assertResult(result2, migratableResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindWhenPresentAndMigratedWithMultipleMigrations() {
        String requestUri1 = constructComponentHref(COMPONENT_ID);
        UUID destinationComponentId2a = UUID.randomUUID();
        UUID destinationComponentId2b = UUID.randomUUID();
        UUID destinationComponentId2c = UUID.randomUUID();
        String requestUri2a = constructComponentHref(destinationComponentId2a);
        String requestUri2b = constructComponentHref(destinationComponentId2b);
        String requestUri2c = constructComponentHref(destinationComponentId2c);
        UUID destinationComponentId3 = UUID.randomUUID();
        String requestUri3 = constructComponentHref(destinationComponentId3);

        // HTTP 300 Multiple Choices response (split migration)
        HttpResponse<Component> httpResponse1 = constructSplitMigratedHttpResponse(requestUri1, List.of(requestUri2a, requestUri2b, requestUri2c));
        Result<Component> result1 = constructResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Component> httpResponse2 = constructMergeMigratedHttpResponse(requestUri2a, requestUri3);
        Result<Component> result2 = constructResult(REQUEST_METHOD, requestUri2a, httpResponse2);

        // HTTP 200 OK response
        Meta meta3 = constructMeta(requestUri3);
        Component component3 = new Component("DestinationComponent", DESCRIPTION, PRIMARY_LANGUAGE, TAGS, LOGOS, DELETED, meta3);
        HttpResponse<Component> httpResponse3 = constructHttpResponse(component3);
        Result<Component> result3 = constructResult(REQUEST_METHOD, requestUri3, httpResponse3);

        Mockito.when(componentApi.find(COMPONENT_ID)).thenReturn(result1);
        Mockito.when(componentApi.find(destinationComponentId2a)).thenReturn(result2);
        Mockito.when(componentApi.find(destinationComponentId3)).thenReturn(result3);

        MigratableResult<Component> migratableResult = bdComponentApi.find(COMPONENT_ID);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null))
                .add(httpResponse2.getMigratedMeta().orElse(null)).build();
        assertResult(result3, migratableResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindWhenPresentAndRetriesExhausted() {
        String requestUri1 = constructComponentHref(COMPONENT_ID);
        UUID destinationComponentId2 = UUID.randomUUID();
        UUID destinationComponentId3 = UUID.randomUUID();
        UUID destinationComponentId4 = UUID.randomUUID();
        String requestUri2 = constructComponentHref(destinationComponentId2);
        String requestUri3 = constructComponentHref(destinationComponentId3);
        String requestUri4 = constructComponentHref(destinationComponentId4);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Component> httpResponse1 = constructMergeMigratedHttpResponse(requestUri1, requestUri2);
        Result<Component> result1 = constructResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Component> httpResponse2 = constructMergeMigratedHttpResponse(requestUri2, requestUri3);
        Result<Component> result2 = constructResult(REQUEST_METHOD, requestUri2, httpResponse2);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Component> httpResponse3 = constructMergeMigratedHttpResponse(requestUri3, requestUri4);
        Result<Component> result3 = constructResult(REQUEST_METHOD, requestUri3, httpResponse3);

        Mockito.when(componentApi.find(COMPONENT_ID)).thenReturn(result1);
        Mockito.when(componentApi.find(destinationComponentId2)).thenReturn(result2);
        Mockito.when(componentApi.find(destinationComponentId3)).thenReturn(result3);

        MigratableResult<Component> migratableResult = bdComponentApi.find(COMPONENT_ID);

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
    public void testFindComponentVersionsWhenAbsent() {
        // HTTP 404 Not Found response
        PageRequest pageRequest = new PageRequest(0, 10, Collections.emptyList());
        String requestUri = constructComponentVersionsHref(COMPONENT_ID);
        HttpResponse<Page<ComponentVersion>> httpResponse = new HttpResponse<>(404, Set.of(200, 402, 403, 404, 500), null, null);
        Result<Page<ComponentVersion>> result = constructResult(REQUEST_METHOD, requestUri, httpResponse);

        Mockito.when(componentApi.findComponentVersions(pageRequest, COMPONENT_ID, null, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result);

        MigratableResult<Page<BdComponentVersion>> migratableResult = bdComponentApi.findComponentVersions(pageRequest, COMPONENT_ID, null,
                VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3);

        assertResult(result, migratableResult, Collections.emptyList());
    }

    @Test
    public void testFindComponentVersionsWhenPresentAndNotMigrated() {
        // HTTP 200 OK response
        PageRequest pageRequest = new PageRequest(0, 10, Collections.emptyList());
        String requestUri = constructComponentVersionsHref(COMPONENT_ID);
        Page<ComponentVersion> componentVersionPage = constructComponentVersionPage(COMPONENT_ID);
        HttpResponse<Page<ComponentVersion>> httpResponse = constructHttpResponse(componentVersionPage);
        Result<Page<ComponentVersion>> result = constructResult(REQUEST_METHOD, requestUri, httpResponse);

        Mockito.when(componentApi.findComponentVersions(pageRequest, COMPONENT_ID, null, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result);

        MigratableResult<Page<BdComponentVersion>> migratableResult = bdComponentApi.findComponentVersions(pageRequest, COMPONENT_ID, null,
                VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3);

        assertResult(result, migratableResult, Collections.emptyList());
    }

    @Test
    public void testFindComponentVersionsWhenPresentAndMergeMigrated() {
        PageRequest pageRequest = new PageRequest(0, 10, Collections.emptyList());

        String requestUri1 = constructComponentVersionsHref(COMPONENT_ID);
        UUID destinationComponentId2 = UUID.randomUUID();
        String requestUri2 = constructComponentVersionsHref(destinationComponentId2);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Page<ComponentVersion>> httpResponse1 = constructMergeMigratedHttpResponse(requestUri1, requestUri2);
        Result<Page<ComponentVersion>> result1 = constructResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 200 OK response
        Page<ComponentVersion> componentVersionPage2 = constructComponentVersionPage(destinationComponentId2);
        HttpResponse<Page<ComponentVersion>> httpResponse2 = constructHttpResponse(componentVersionPage2);
        Result<Page<ComponentVersion>> result2 = constructResult(REQUEST_METHOD, requestUri2, httpResponse2);

        Mockito.when(componentApi.findComponentVersions(pageRequest, COMPONENT_ID, null, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result1);
        Mockito.when(componentApi.findComponentVersions(pageRequest, destinationComponentId2, null, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3)).thenReturn(result2);

        MigratableResult<Page<BdComponentVersion>> migratableResult = bdComponentApi.findComponentVersions(pageRequest, COMPONENT_ID, null,
                VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null)).build();
        assertResult(result2, migratableResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindComponentVersionsWhenPresentAndSplitMigrated() {
        PageRequest pageRequest = new PageRequest(0, 10, Collections.emptyList());

        String requestUri1 = constructComponentVersionsHref(COMPONENT_ID);
        UUID destinationComponentId2a = UUID.randomUUID();
        UUID destinationComponentId2b = UUID.randomUUID();
        UUID destinationComponentId2c = UUID.randomUUID();
        String requestUri2a = constructComponentVersionsHref(destinationComponentId2a);
        String requestUri2b = constructComponentVersionsHref(destinationComponentId2b);
        String requestUri2c = constructComponentVersionsHref(destinationComponentId2c);

        // HTTP 300 Multiple Choices response (split migration)
        HttpResponse<Page<ComponentVersion>> httpResponse1 = constructSplitMigratedHttpResponse(requestUri1, List.of(requestUri2a, requestUri2b, requestUri2c));
        Result<Page<ComponentVersion>> result1 = constructResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 200 OK response
        Page<ComponentVersion> componentVersionPage2 = constructComponentVersionPage(destinationComponentId2a);
        HttpResponse<Page<ComponentVersion>> httpResponse2 = constructHttpResponse(componentVersionPage2);
        Result<Page<ComponentVersion>> result2 = constructResult(REQUEST_METHOD, requestUri2a, httpResponse2);

        Mockito.when(componentApi.findComponentVersions(pageRequest, COMPONENT_ID, null, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result1);
        Mockito.when(componentApi.findComponentVersions(pageRequest, destinationComponentId2a, null, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3)).thenReturn(result2);

        MigratableResult<Page<BdComponentVersion>> migratableResult = bdComponentApi.findComponentVersions(pageRequest, COMPONENT_ID, null,
                VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null)).build();
        assertResult(result2, migratableResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindComponentVersionsWhenPresentAndMigratedWithMultipleMigrations() {
        PageRequest pageRequest = new PageRequest(0, 10, Collections.emptyList());

        String requestUri1 = constructComponentVersionsHref(COMPONENT_ID);
        UUID destinationComponentId2a = UUID.randomUUID();
        UUID destinationComponentId2b = UUID.randomUUID();
        UUID destinationComponentId2c = UUID.randomUUID();
        String requestUri2a = constructComponentVersionsHref(destinationComponentId2a);
        String requestUri2b = constructComponentVersionsHref(destinationComponentId2b);
        String requestUri2c = constructComponentVersionsHref(destinationComponentId2c);
        UUID destinationComponentId3 = UUID.randomUUID();
        String requestUri3 = constructComponentVersionsHref(destinationComponentId3);

        // HTTP 300 Multiple Choices response (split migration)
        HttpResponse<Page<ComponentVersion>> httpResponse1 = constructSplitMigratedHttpResponse(requestUri1, List.of(requestUri2a, requestUri2b, requestUri2c));
        Result<Page<ComponentVersion>> result1 = constructResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Page<ComponentVersion>> httpResponse2 = constructMergeMigratedHttpResponse(requestUri2a, requestUri3);
        Result<Page<ComponentVersion>> result2 = constructResult(REQUEST_METHOD, requestUri2a, httpResponse2);

        // HTTP 200 OK response
        Page<ComponentVersion> componentVersionPage3 = constructComponentVersionPage(destinationComponentId3);
        HttpResponse<Page<ComponentVersion>> httpResponse3 = constructHttpResponse(componentVersionPage3);
        Result<Page<ComponentVersion>> result3 = constructResult(REQUEST_METHOD, requestUri3, httpResponse3);

        Mockito.when(componentApi.findComponentVersions(pageRequest, COMPONENT_ID, null, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result1);
        Mockito.when(componentApi.findComponentVersions(pageRequest, destinationComponentId2a, null, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3)).thenReturn(result2);
        Mockito.when(componentApi.findComponentVersions(pageRequest, destinationComponentId3, null, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3)).thenReturn(result3);

        MigratableResult<Page<BdComponentVersion>> migratableResult = bdComponentApi.findComponentVersions(pageRequest, COMPONENT_ID, null,
                VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null))
                .add(httpResponse2.getMigratedMeta().orElse(null)).build();
        assertResult(result3, migratableResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindComponentVersionsWhenPresentAndRetriesExhausted() {
        PageRequest pageRequest = new PageRequest(0, 10, Collections.emptyList());

        String requestUri1 = constructComponentVersionsHref(COMPONENT_ID);
        UUID destinationComponentId2 = UUID.randomUUID();
        UUID destinationComponentId3 = UUID.randomUUID();
        UUID destinationComponentId4 = UUID.randomUUID();
        String requestUri2 = constructComponentVersionsHref(destinationComponentId2);
        String requestUri3 = constructComponentVersionsHref(destinationComponentId3);
        String requestUri4 = constructComponentVersionsHref(destinationComponentId4);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Page<ComponentVersion>> httpResponse1 = constructMergeMigratedHttpResponse(requestUri1, requestUri2);
        Result<Page<ComponentVersion>> result1 = constructResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Page<ComponentVersion>> httpResponse2 = constructMergeMigratedHttpResponse(requestUri2, requestUri3);
        Result<Page<ComponentVersion>> result2 = constructResult(REQUEST_METHOD, requestUri2, httpResponse2);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Page<ComponentVersion>> httpResponse3 = constructMergeMigratedHttpResponse(requestUri3, requestUri4);
        Result<Page<ComponentVersion>> result3 = constructResult(REQUEST_METHOD, requestUri3, httpResponse3);

        Mockito.when(componentApi.findComponentVersions(pageRequest, COMPONENT_ID, null, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(result1);
        Mockito.when(componentApi.findComponentVersions(pageRequest, destinationComponentId2, null, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3)).thenReturn(result2);
        Mockito.when(componentApi.findComponentVersions(pageRequest, destinationComponentId3, null, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3)).thenReturn(result3);

        MigratableResult<Page<BdComponentVersion>> migratableResult = bdComponentApi.findComponentVersions(pageRequest, COMPONENT_ID, null,
                VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3);

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
    public void testFindComponentVersionSummariesWhenAbsent() {
        // HTTP 404 Not Found response
        PageRequest pageRequest = new PageRequest(0, 10, Collections.emptyList());
        String requestUri = constructComponentVersionsHref(COMPONENT_ID);
        HttpResponse<Page<ComponentVersionSummary>> httpResponse = new HttpResponse<>(404, Set.of(200, 402, 403, 404, 500), null, null);
        Result<Page<ComponentVersionSummary>> result = constructResult(REQUEST_METHOD, requestUri, httpResponse);

        Mockito.when(componentApi.findComponentVersionSummaries(pageRequest, COMPONENT_ID, null)).thenReturn(result);

        MigratableResult<Page<BdComponentVersionSummary>> migratableResult = bdComponentApi.findComponentVersionSummaries(pageRequest, COMPONENT_ID, null);

        assertResult(result, migratableResult, Collections.emptyList());
    }

    @Test
    public void testFindComponentVersionSummariesWhenPresentAndNotMigrated() {
        // HTTP 200 OK response
        PageRequest pageRequest = new PageRequest(0, 10, Collections.emptyList());
        String requestUri = constructComponentVersionsHref(COMPONENT_ID);
        Page<ComponentVersionSummary> componentVersionPage = constructComponentVersionSummaryPage(COMPONENT_ID);
        HttpResponse<Page<ComponentVersionSummary>> httpResponse = constructHttpResponse(componentVersionPage);
        Result<Page<ComponentVersionSummary>> result = constructResult(REQUEST_METHOD, requestUri, httpResponse);

        Mockito.when(componentApi.findComponentVersionSummaries(pageRequest, COMPONENT_ID, null)).thenReturn(result);

        MigratableResult<Page<BdComponentVersionSummary>> migratableResult = bdComponentApi.findComponentVersionSummaries(pageRequest, COMPONENT_ID, null);

        assertResult(result, migratableResult, Collections.emptyList());
    }

    @Test
    public void testFindComponentVersionSummariesWhenPresentAndMergeMigrated() {
        PageRequest pageRequest = new PageRequest(0, 10, Collections.emptyList());

        String requestUri1 = constructComponentVersionsHref(COMPONENT_ID);
        UUID destinationComponentId2 = UUID.randomUUID();
        String requestUri2 = constructComponentVersionsHref(destinationComponentId2);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Page<ComponentVersionSummary>> httpResponse1 = constructMergeMigratedHttpResponse(requestUri1, requestUri2);
        Result<Page<ComponentVersionSummary>> result1 = constructResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 200 OK response
        Page<ComponentVersionSummary> componentVersionPage2 = constructComponentVersionSummaryPage(destinationComponentId2);
        HttpResponse<Page<ComponentVersionSummary>> httpResponse2 = constructHttpResponse(componentVersionPage2);
        Result<Page<ComponentVersionSummary>> result2 = constructResult(REQUEST_METHOD, requestUri2, httpResponse2);

        Mockito.when(componentApi.findComponentVersionSummaries(pageRequest, COMPONENT_ID, null)).thenReturn(result1);
        Mockito.when(componentApi.findComponentVersionSummaries(pageRequest, destinationComponentId2, null)).thenReturn(result2);

        MigratableResult<Page<BdComponentVersionSummary>> migratableResult = bdComponentApi.findComponentVersionSummaries(pageRequest, COMPONENT_ID, null);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null)).build();
        assertResult(result2, migratableResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindComponentVersionSummariesWhenPresentAndSplitMigrated() {
        PageRequest pageRequest = new PageRequest(0, 10, Collections.emptyList());

        String requestUri1 = constructComponentVersionsHref(COMPONENT_ID);
        UUID destinationComponentId2a = UUID.randomUUID();
        UUID destinationComponentId2b = UUID.randomUUID();
        UUID destinationComponentId2c = UUID.randomUUID();
        String requestUri2a = constructComponentVersionsHref(destinationComponentId2a);
        String requestUri2b = constructComponentVersionsHref(destinationComponentId2b);
        String requestUri2c = constructComponentVersionsHref(destinationComponentId2c);

        // HTTP 300 Multiple Choices response (split migration)
        HttpResponse<Page<ComponentVersionSummary>> httpResponse1 = constructSplitMigratedHttpResponse(requestUri1,
                List.of(requestUri2a, requestUri2b, requestUri2c));
        Result<Page<ComponentVersionSummary>> result1 = constructResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 200 OK response
        Page<ComponentVersionSummary> componentVersionPage2 = constructComponentVersionSummaryPage(destinationComponentId2a);
        HttpResponse<Page<ComponentVersionSummary>> httpResponse2 = constructHttpResponse(componentVersionPage2);
        Result<Page<ComponentVersionSummary>> result2 = constructResult(REQUEST_METHOD, requestUri2a, httpResponse2);

        Mockito.when(componentApi.findComponentVersionSummaries(pageRequest, COMPONENT_ID, null)).thenReturn(result1);
        Mockito.when(componentApi.findComponentVersionSummaries(pageRequest, destinationComponentId2a, null)).thenReturn(result2);

        MigratableResult<Page<BdComponentVersionSummary>> migratableResult = bdComponentApi.findComponentVersionSummaries(pageRequest, COMPONENT_ID, null);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null)).build();
        assertResult(result2, migratableResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindComponentVersionSummariesWhenPresentAndMigratedWithMultipleMigrations() {
        PageRequest pageRequest = new PageRequest(0, 10, Collections.emptyList());

        String requestUri1 = constructComponentVersionsHref(COMPONENT_ID);
        UUID destinationComponentId2a = UUID.randomUUID();
        UUID destinationComponentId2b = UUID.randomUUID();
        UUID destinationComponentId2c = UUID.randomUUID();
        String requestUri2a = constructComponentVersionsHref(destinationComponentId2a);
        String requestUri2b = constructComponentVersionsHref(destinationComponentId2b);
        String requestUri2c = constructComponentVersionsHref(destinationComponentId2c);
        UUID destinationComponentId3 = UUID.randomUUID();
        String requestUri3 = constructComponentVersionsHref(destinationComponentId3);

        // HTTP 300 Multiple Choices response (split migration)
        HttpResponse<Page<ComponentVersionSummary>> httpResponse1 = constructSplitMigratedHttpResponse(requestUri1,
                List.of(requestUri2a, requestUri2b, requestUri2c));
        Result<Page<ComponentVersionSummary>> result1 = constructResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Page<ComponentVersionSummary>> httpResponse2 = constructMergeMigratedHttpResponse(requestUri2a, requestUri3);
        Result<Page<ComponentVersionSummary>> result2 = constructResult(REQUEST_METHOD, requestUri2a, httpResponse2);

        // HTTP 200 OK response
        Page<ComponentVersionSummary> componentVersionPage3 = constructComponentVersionSummaryPage(destinationComponentId3);
        HttpResponse<Page<ComponentVersionSummary>> httpResponse3 = constructHttpResponse(componentVersionPage3);
        Result<Page<ComponentVersionSummary>> result3 = constructResult(REQUEST_METHOD, requestUri3, httpResponse3);

        Mockito.when(componentApi.findComponentVersionSummaries(pageRequest, COMPONENT_ID, null)).thenReturn(result1);
        Mockito.when(componentApi.findComponentVersionSummaries(pageRequest, destinationComponentId2a, null)).thenReturn(result2);
        Mockito.when(componentApi.findComponentVersionSummaries(pageRequest, destinationComponentId3, null)).thenReturn(result3);

        MigratableResult<Page<BdComponentVersionSummary>> migratableResult = bdComponentApi.findComponentVersionSummaries(pageRequest, COMPONENT_ID, null);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null))
                .add(httpResponse2.getMigratedMeta().orElse(null)).build();
        assertResult(result3, migratableResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindComponentVersionSummariesWhenPresentAndRetriesExhausted() {
        PageRequest pageRequest = new PageRequest(0, 10, Collections.emptyList());

        String requestUri1 = constructComponentVersionsHref(COMPONENT_ID);
        UUID destinationComponentId2 = UUID.randomUUID();
        UUID destinationComponentId3 = UUID.randomUUID();
        UUID destinationComponentId4 = UUID.randomUUID();
        String requestUri2 = constructComponentVersionsHref(destinationComponentId2);
        String requestUri3 = constructComponentVersionsHref(destinationComponentId3);
        String requestUri4 = constructComponentVersionsHref(destinationComponentId4);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Page<ComponentVersionSummary>> httpResponse1 = constructMergeMigratedHttpResponse(requestUri1, requestUri2);
        Result<Page<ComponentVersionSummary>> result1 = constructResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Page<ComponentVersionSummary>> httpResponse2 = constructMergeMigratedHttpResponse(requestUri2, requestUri3);
        Result<Page<ComponentVersionSummary>> result2 = constructResult(REQUEST_METHOD, requestUri2, httpResponse2);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Page<ComponentVersionSummary>> httpResponse3 = constructMergeMigratedHttpResponse(requestUri3, requestUri4);
        Result<Page<ComponentVersionSummary>> result3 = constructResult(REQUEST_METHOD, requestUri3, httpResponse3);

        Mockito.when(componentApi.findComponentVersionSummaries(pageRequest, COMPONENT_ID, null)).thenReturn(result1);
        Mockito.when(componentApi.findComponentVersionSummaries(pageRequest, destinationComponentId2, null)).thenReturn(result2);
        Mockito.when(componentApi.findComponentVersionSummaries(pageRequest, destinationComponentId3, null)).thenReturn(result3);

        MigratableResult<Page<BdComponentVersionSummary>> migratableResult = bdComponentApi.findComponentVersionSummaries(pageRequest, COMPONENT_ID, null);

        // Initial request
        // First migrated request
        // Second migrated request
        // Retries exhausted and return 'second migrated response'.
        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null))
                .add(httpResponse2.getMigratedMeta().orElse(null)).build();
        assertResult(result3, migratableResult, expectedMigratedMetaHistory);
    }

    private Page<ComponentVersion> constructComponentVersionPage(UUID componentId) {
        ComponentVersion componentVersion1 = constructComponentVersion(componentId, UUID.randomUUID(), "1.0");
        ComponentVersion componentVersion2 = constructComponentVersion(componentId, UUID.randomUUID(), "2.0");
        ComponentVersion componentVersion3 = constructComponentVersion(componentId, UUID.randomUUID(), "3.0");
        List<ComponentVersion> componentVersions = List.of(componentVersion1, componentVersion2, componentVersion3);
        String href = constructComponentVersionsHref(componentId);
        Meta meta = new Meta(href, Collections.emptyList());

        return new Page<>(3, componentVersions, meta);
    }

    private Page<ComponentVersionSummary> constructComponentVersionSummaryPage(UUID componentId) {
        ComponentVersionSummary componentVersionSummary1 = constructComponentVersionSummary(componentId, UUID.randomUUID(), "1.0");
        ComponentVersionSummary componentVersionSummary2 = constructComponentVersionSummary(componentId, UUID.randomUUID(), "2.0");
        ComponentVersionSummary componentVersionSummary3 = constructComponentVersionSummary(componentId, UUID.randomUUID(), "3.0");
        List<ComponentVersionSummary> componentVersionSummaries = List.of(componentVersionSummary1, componentVersionSummary2, componentVersionSummary3);
        String href = constructComponentVersionsHref(componentId);
        Meta meta = new Meta(href, Collections.emptyList());

        return new Page<>(3, componentVersionSummaries, meta);
    }

    private String constructComponentHref(UUID componentId) {
        return BASE_HREF + "/api/components/" + componentId;
    }

    private String constructComponentVersionsHref(UUID componentId) {
        return BASE_HREF + "/api/components/" + componentId + "/versions";
    }
}
