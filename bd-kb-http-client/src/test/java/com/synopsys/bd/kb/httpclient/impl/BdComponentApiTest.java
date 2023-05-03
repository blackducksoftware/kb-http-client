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
import com.synopsys.bd.kb.httpclient.api.MigratableHttpResult;
import com.synopsys.bd.kb.httpclient.model.BdComponentVersion;
import com.synopsys.bd.kb.httpclient.model.BdComponentVersionSummary;
import com.synopsys.kb.httpclient.api.HttpResponse;
import com.synopsys.kb.httpclient.api.HttpResult;
import com.synopsys.kb.httpclient.api.IComponentApi;
import com.synopsys.kb.httpclient.api.PageRequest;
import com.synopsys.kb.httpclient.model.Component;
import com.synopsys.kb.httpclient.model.ComponentVersion;
import com.synopsys.kb.httpclient.model.ComponentVersionSummary;
import com.synopsys.kb.httpclient.model.Logo;
import com.synopsys.kb.httpclient.model.Meta;
import com.synopsys.kb.httpclient.model.OngoingVersion;
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
    public void testFindComponentV4WhenAbsent() {
        // HTTP 404 Not Found response
        String requestUri = constructComponentHref(COMPONENT_ID);
        HttpResponse<Component> httpResponse = new HttpResponse<>(404, Set.of(200, 404), null, null);
        HttpResult<Component> httpResult = constructHttpResult(REQUEST_METHOD, requestUri, httpResponse);

        Mockito.when(componentApi.findComponentV4(COMPONENT_ID)).thenReturn(httpResult);

        MigratableHttpResult<Component> migratableHttpResult = bdComponentApi.findComponentV4(COMPONENT_ID);

        assertHttpResult(httpResult, migratableHttpResult, Collections.emptyList());
    }

    @Test
    public void testFindComponentV4WhenPresentAndNotMigrated() {
        // HTTP 200 OK response
        String requestUri = constructComponentHref(COMPONENT_ID);
        Component component = new Component(NAME, DESCRIPTION, PRIMARY_LANGUAGE, TAGS, LOGOS, DELETED, META);
        HttpResponse<Component> httpResponse = constructHttpResponse(component);
        HttpResult<Component> httpResult = constructHttpResult(REQUEST_METHOD, requestUri, httpResponse);

        Mockito.when(componentApi.findComponentV4(COMPONENT_ID)).thenReturn(httpResult);

        MigratableHttpResult<Component> migratableHttpResult = bdComponentApi.findComponentV4(COMPONENT_ID);

        assertHttpResult(httpResult, migratableHttpResult, Collections.emptyList());
    }

    @Test
    public void testFindComponentV4WhenPresentAndMergeMigrated() {
        String requestUri1 = constructComponentHref(COMPONENT_ID);
        UUID destinationComponentId2 = UUID.randomUUID();
        String requestUri2 = constructComponentHref(destinationComponentId2);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Component> httpResponse1 = constructMergeMigratedHttpResponse(requestUri1, requestUri2);
        HttpResult<Component> result1 = constructHttpResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 200 OK response
        Meta meta2 = constructMeta(requestUri2);
        Component component2 = new Component("DestinationComponent", DESCRIPTION, PRIMARY_LANGUAGE, TAGS, LOGOS, DELETED, meta2);
        HttpResponse<Component> httpResponse2 = constructHttpResponse(component2);
        HttpResult<Component> result2 = constructHttpResult(REQUEST_METHOD, requestUri2, httpResponse2);

        Mockito.when(componentApi.findComponentV4(COMPONENT_ID)).thenReturn(result1);
        Mockito.when(componentApi.findComponentV4(destinationComponentId2)).thenReturn(result2);

        MigratableHttpResult<Component> migratableHttpResult = bdComponentApi.findComponentV4(COMPONENT_ID);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null)).build();
        assertHttpResult(result2, migratableHttpResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindComponentWhenPresentAndSplitMigrated() {
        String requestUri1 = constructComponentHref(COMPONENT_ID);
        UUID destinationComponentId2a = UUID.randomUUID();
        UUID destinationComponentId2b = UUID.randomUUID();
        UUID destinationComponentId2c = UUID.randomUUID();
        String requestUri2a = constructComponentHref(destinationComponentId2a);
        String requestUri2b = constructComponentHref(destinationComponentId2b);
        String requestUri2c = constructComponentHref(destinationComponentId2c);

        // HTTP 300 Multiple Choices response (split migration)
        HttpResponse<Component> httpResponse1 = constructSplitMigratedHttpResponse(requestUri1, List.of(requestUri2a, requestUri2b, requestUri2c));
        HttpResult<Component> result1 = constructHttpResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 200 OK response
        Meta meta2 = constructMeta(requestUri2a);
        Component component2 = new Component("DestinationComponent", DESCRIPTION, PRIMARY_LANGUAGE, TAGS, LOGOS, DELETED, meta2);
        HttpResponse<Component> httpResponse2 = constructHttpResponse(component2);
        HttpResult<Component> result2 = constructHttpResult(REQUEST_METHOD, requestUri2a, httpResponse2);

        Mockito.when(componentApi.findComponentV4(COMPONENT_ID)).thenReturn(result1);
        Mockito.when(componentApi.findComponentV4(destinationComponentId2a)).thenReturn(result2);

        MigratableHttpResult<Component> migratableHttpResult = bdComponentApi.findComponentV4(COMPONENT_ID);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null)).build();
        assertHttpResult(result2, migratableHttpResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindComponentV4WhenPresentAndMigratedWithMultipleMigrations() {
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
        HttpResult<Component> result1 = constructHttpResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Component> httpResponse2 = constructMergeMigratedHttpResponse(requestUri2a, requestUri3);
        HttpResult<Component> result2 = constructHttpResult(REQUEST_METHOD, requestUri2a, httpResponse2);

        // HTTP 200 OK response
        Meta meta3 = constructMeta(requestUri3);
        Component component3 = new Component("DestinationComponent", DESCRIPTION, PRIMARY_LANGUAGE, TAGS, LOGOS, DELETED, meta3);
        HttpResponse<Component> httpResponse3 = constructHttpResponse(component3);
        HttpResult<Component> result3 = constructHttpResult(REQUEST_METHOD, requestUri3, httpResponse3);

        Mockito.when(componentApi.findComponentV4(COMPONENT_ID)).thenReturn(result1);
        Mockito.when(componentApi.findComponentV4(destinationComponentId2a)).thenReturn(result2);
        Mockito.when(componentApi.findComponentV4(destinationComponentId3)).thenReturn(result3);

        MigratableHttpResult<Component> migratableHttpResult = bdComponentApi.findComponentV4(COMPONENT_ID);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null))
                .add(httpResponse2.getMigratedMeta().orElse(null)).build();
        assertHttpResult(result3, migratableHttpResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindComponentV4WhenPresentAndRetriesExhausted() {
        String requestUri1 = constructComponentHref(COMPONENT_ID);
        UUID destinationComponentId2 = UUID.randomUUID();
        UUID destinationComponentId3 = UUID.randomUUID();
        UUID destinationComponentId4 = UUID.randomUUID();
        String requestUri2 = constructComponentHref(destinationComponentId2);
        String requestUri3 = constructComponentHref(destinationComponentId3);
        String requestUri4 = constructComponentHref(destinationComponentId4);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Component> httpResponse1 = constructMergeMigratedHttpResponse(requestUri1, requestUri2);
        HttpResult<Component> result1 = constructHttpResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Component> httpResponse2 = constructMergeMigratedHttpResponse(requestUri2, requestUri3);
        HttpResult<Component> result2 = constructHttpResult(REQUEST_METHOD, requestUri2, httpResponse2);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Component> httpResponse3 = constructMergeMigratedHttpResponse(requestUri3, requestUri4);
        HttpResult<Component> result3 = constructHttpResult(REQUEST_METHOD, requestUri3, httpResponse3);

        Mockito.when(componentApi.findComponentV4(COMPONENT_ID)).thenReturn(result1);
        Mockito.when(componentApi.findComponentV4(destinationComponentId2)).thenReturn(result2);
        Mockito.when(componentApi.findComponentV4(destinationComponentId3)).thenReturn(result3);

        MigratableHttpResult<Component> migratableHttpResult = bdComponentApi.findComponentV4(COMPONENT_ID);

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
    public void testFindComponentVersionsByComponentV4WhenAbsent() {
        // HTTP 404 Not Found response
        PageRequest pageRequest = new PageRequest(0, 10, Collections.emptyList());
        String requestUri = constructComponentVersionsHref(COMPONENT_ID);
        HttpResponse<Page<ComponentVersion>> httpResponse = new HttpResponse<>(404, Set.of(200, 402, 403, 404, 500), null, null);
        HttpResult<Page<ComponentVersion>> httpResult = constructHttpResult(REQUEST_METHOD, requestUri, httpResponse);

        Mockito.when(componentApi.findComponentVersionsByComponentV4(pageRequest, COMPONENT_ID, null, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3, Boolean.FALSE)).thenReturn(httpResult);

        MigratableHttpResult<Page<BdComponentVersion>> migratableHttpResult = bdComponentApi.findComponentVersionsByComponentV4(pageRequest, COMPONENT_ID, null,
                VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3, Boolean.FALSE);

        assertHttpResult(httpResult, migratableHttpResult, Collections.emptyList());
    }

    @Test
    public void testFindComponentVersionsByComponentV4WhenPresentAndNotMigrated() {
        // HTTP 200 OK response
        PageRequest pageRequest = new PageRequest(0, 10, Collections.emptyList());
        String requestUri = constructComponentVersionsHref(COMPONENT_ID);
        Page<ComponentVersion> componentVersionPage = constructComponentVersionPage(COMPONENT_ID);
        HttpResponse<Page<ComponentVersion>> httpResponse = constructHttpResponse(componentVersionPage);
        HttpResult<Page<ComponentVersion>> httpResult = constructHttpResult(REQUEST_METHOD, requestUri, httpResponse);

        Mockito.when(componentApi.findComponentVersionsByComponentV4(pageRequest, COMPONENT_ID, null, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3, Boolean.FALSE)).thenReturn(httpResult);

        MigratableHttpResult<Page<BdComponentVersion>> migratableHttpResult = bdComponentApi.findComponentVersionsByComponentV4(pageRequest, COMPONENT_ID, null,
                VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3, Boolean.FALSE);

        assertHttpResult(httpResult, migratableHttpResult, Collections.emptyList());
    }

    @Test
    public void testFindComponentVersionsByComponentV4WhenPresentAndMergeMigrated() {
        PageRequest pageRequest = new PageRequest(0, 10, Collections.emptyList());

        String requestUri1 = constructComponentVersionsHref(COMPONENT_ID);
        UUID destinationComponentId2 = UUID.randomUUID();
        String requestUri2 = constructComponentVersionsHref(destinationComponentId2);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Page<ComponentVersion>> httpResponse1 = constructMergeMigratedHttpResponse(requestUri1, requestUri2);
        HttpResult<Page<ComponentVersion>> result1 = constructHttpResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 200 OK response
        Page<ComponentVersion> componentVersionPage2 = constructComponentVersionPage(destinationComponentId2);
        HttpResponse<Page<ComponentVersion>> httpResponse2 = constructHttpResponse(componentVersionPage2);
        HttpResult<Page<ComponentVersion>> result2 = constructHttpResult(REQUEST_METHOD, requestUri2, httpResponse2);

        Mockito.when(componentApi.findComponentVersionsByComponentV4(pageRequest, COMPONENT_ID, null, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3, Boolean.FALSE)).thenReturn(result1);
        Mockito.when(componentApi.findComponentVersionsByComponentV4(pageRequest, destinationComponentId2, null, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3, Boolean.FALSE)).thenReturn(result2);

        MigratableHttpResult<Page<BdComponentVersion>> migratableHttpResult = bdComponentApi.findComponentVersionsByComponentV4(pageRequest, COMPONENT_ID, null,
                VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3, Boolean.FALSE);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null)).build();
        assertHttpResult(result2, migratableHttpResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindComponentVersionsByComponentV4WhenPresentAndSplitMigrated() {
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
        HttpResult<Page<ComponentVersion>> result1 = constructHttpResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 200 OK response
        Page<ComponentVersion> componentVersionPage2 = constructComponentVersionPage(destinationComponentId2a);
        HttpResponse<Page<ComponentVersion>> httpResponse2 = constructHttpResponse(componentVersionPage2);
        HttpResult<Page<ComponentVersion>> result2 = constructHttpResult(REQUEST_METHOD, requestUri2a, httpResponse2);

        Mockito.when(componentApi.findComponentVersionsByComponentV4(pageRequest, COMPONENT_ID, null, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3, Boolean.FALSE)).thenReturn(result1);
        Mockito.when(componentApi.findComponentVersionsByComponentV4(pageRequest, destinationComponentId2a, null, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3, Boolean.FALSE)).thenReturn(result2);

        MigratableHttpResult<Page<BdComponentVersion>> migratableHttpResult = bdComponentApi.findComponentVersionsByComponentV4(pageRequest, COMPONENT_ID, null,
                VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3, Boolean.FALSE);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null)).build();
        assertHttpResult(result2, migratableHttpResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindComponentVersionsByComponentV4WhenPresentAndMigratedWithMultipleMigrations() {
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
        HttpResult<Page<ComponentVersion>> result1 = constructHttpResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Page<ComponentVersion>> httpResponse2 = constructMergeMigratedHttpResponse(requestUri2a, requestUri3);
        HttpResult<Page<ComponentVersion>> result2 = constructHttpResult(REQUEST_METHOD, requestUri2a, httpResponse2);

        // HTTP 200 OK response
        Page<ComponentVersion> componentVersionPage3 = constructComponentVersionPage(destinationComponentId3);
        HttpResponse<Page<ComponentVersion>> httpResponse3 = constructHttpResponse(componentVersionPage3);
        HttpResult<Page<ComponentVersion>> result3 = constructHttpResult(REQUEST_METHOD, requestUri3, httpResponse3);

        Mockito.when(componentApi.findComponentVersionsByComponentV4(pageRequest, COMPONENT_ID, null, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3, Boolean.FALSE)).thenReturn(result1);
        Mockito.when(componentApi.findComponentVersionsByComponentV4(pageRequest, destinationComponentId2a, null, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3, Boolean.FALSE)).thenReturn(result2);
        Mockito.when(componentApi.findComponentVersionsByComponentV4(pageRequest, destinationComponentId3, null, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3, Boolean.FALSE)).thenReturn(result3);

        MigratableHttpResult<Page<BdComponentVersion>> migratableHttpResult = bdComponentApi.findComponentVersionsByComponentV4(pageRequest, COMPONENT_ID, null,
                VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3, Boolean.FALSE);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null))
                .add(httpResponse2.getMigratedMeta().orElse(null)).build();
        assertHttpResult(result3, migratableHttpResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindComponentVersionsByComponentV4WhenPresentAndRetriesExhausted() {
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
        HttpResult<Page<ComponentVersion>> result1 = constructHttpResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Page<ComponentVersion>> httpResponse2 = constructMergeMigratedHttpResponse(requestUri2, requestUri3);
        HttpResult<Page<ComponentVersion>> result2 = constructHttpResult(REQUEST_METHOD, requestUri2, httpResponse2);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Page<ComponentVersion>> httpResponse3 = constructMergeMigratedHttpResponse(requestUri3, requestUri4);
        HttpResult<Page<ComponentVersion>> result3 = constructHttpResult(REQUEST_METHOD, requestUri3, httpResponse3);

        Mockito.when(componentApi.findComponentVersionsByComponentV4(pageRequest, COMPONENT_ID, null, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3, Boolean.FALSE)).thenReturn(result1);
        Mockito.when(componentApi.findComponentVersionsByComponentV4(pageRequest, destinationComponentId2, null, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3, Boolean.FALSE)).thenReturn(result2);
        Mockito.when(componentApi.findComponentVersionsByComponentV4(pageRequest, destinationComponentId3, null, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3, Boolean.FALSE)).thenReturn(result3);

        MigratableHttpResult<Page<BdComponentVersion>> migratableHttpResult = bdComponentApi.findComponentVersionsByComponentV4(pageRequest, COMPONENT_ID, null,
                VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3, Boolean.FALSE);

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
    public void testFindComponentVersionSummariesByComponentV2WhenAbsent() {
        // HTTP 404 Not Found response
        PageRequest pageRequest = new PageRequest(0, 10, Collections.emptyList());
        String requestUri = constructComponentVersionsHref(COMPONENT_ID);
        HttpResponse<Page<ComponentVersionSummary>> httpResponse = new HttpResponse<>(404, Set.of(200, 402, 403, 404, 500), null, null);
        HttpResult<Page<ComponentVersionSummary>> httpResult = constructHttpResult(REQUEST_METHOD, requestUri, httpResponse);

        Mockito.when(componentApi.findComponentVersionSummariesByComponentV2(pageRequest, COMPONENT_ID, null, Boolean.FALSE)).thenReturn(httpResult);

        MigratableHttpResult<Page<BdComponentVersionSummary>> migratableHttpResult = bdComponentApi.findComponentVersionSummariesByComponentV2(pageRequest,
                COMPONENT_ID,
                null, Boolean.FALSE);

        assertHttpResult(httpResult, migratableHttpResult, Collections.emptyList());
    }

    @Test
    public void testFindComponentVersionSummariesByComponentV2WhenPresentAndNotMigrated() {
        // HTTP 200 OK response
        PageRequest pageRequest = new PageRequest(0, 10, Collections.emptyList());
        String requestUri = constructComponentVersionsHref(COMPONENT_ID);
        Page<ComponentVersionSummary> componentVersionPage = constructComponentVersionSummaryPage(COMPONENT_ID);
        HttpResponse<Page<ComponentVersionSummary>> httpResponse = constructHttpResponse(componentVersionPage);
        HttpResult<Page<ComponentVersionSummary>> httpResult = constructHttpResult(REQUEST_METHOD, requestUri, httpResponse);

        Mockito.when(componentApi.findComponentVersionSummariesByComponentV2(pageRequest, COMPONENT_ID, null, Boolean.FALSE)).thenReturn(httpResult);

        MigratableHttpResult<Page<BdComponentVersionSummary>> migratableHttpResult = bdComponentApi.findComponentVersionSummariesByComponentV2(pageRequest,
                COMPONENT_ID,
                null, Boolean.FALSE);

        assertHttpResult(httpResult, migratableHttpResult, Collections.emptyList());
    }

    @Test
    public void testFindComponentVersionSummariesByComponentV2WhenPresentAndMergeMigrated() {
        PageRequest pageRequest = new PageRequest(0, 10, Collections.emptyList());

        String requestUri1 = constructComponentVersionsHref(COMPONENT_ID);
        UUID destinationComponentId2 = UUID.randomUUID();
        String requestUri2 = constructComponentVersionsHref(destinationComponentId2);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Page<ComponentVersionSummary>> httpResponse1 = constructMergeMigratedHttpResponse(requestUri1, requestUri2);
        HttpResult<Page<ComponentVersionSummary>> result1 = constructHttpResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 200 OK response
        Page<ComponentVersionSummary> componentVersionPage2 = constructComponentVersionSummaryPage(destinationComponentId2);
        HttpResponse<Page<ComponentVersionSummary>> httpResponse2 = constructHttpResponse(componentVersionPage2);
        HttpResult<Page<ComponentVersionSummary>> result2 = constructHttpResult(REQUEST_METHOD, requestUri2, httpResponse2);

        Mockito.when(componentApi.findComponentVersionSummariesByComponentV2(pageRequest, COMPONENT_ID, null, Boolean.FALSE)).thenReturn(result1);
        Mockito.when(componentApi.findComponentVersionSummariesByComponentV2(pageRequest, destinationComponentId2, null, Boolean.FALSE)).thenReturn(result2);

        MigratableHttpResult<Page<BdComponentVersionSummary>> migratableHttpResult = bdComponentApi.findComponentVersionSummariesByComponentV2(pageRequest,
                COMPONENT_ID,
                null, Boolean.FALSE);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null)).build();
        assertHttpResult(result2, migratableHttpResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindComponentVersionSummariesByComponentV2WhenPresentAndSplitMigrated() {
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
        HttpResult<Page<ComponentVersionSummary>> result1 = constructHttpResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 200 OK response
        Page<ComponentVersionSummary> componentVersionPage2 = constructComponentVersionSummaryPage(destinationComponentId2a);
        HttpResponse<Page<ComponentVersionSummary>> httpResponse2 = constructHttpResponse(componentVersionPage2);
        HttpResult<Page<ComponentVersionSummary>> result2 = constructHttpResult(REQUEST_METHOD, requestUri2a, httpResponse2);

        Mockito.when(componentApi.findComponentVersionSummariesByComponentV2(pageRequest, COMPONENT_ID, null, Boolean.FALSE)).thenReturn(result1);
        Mockito.when(componentApi.findComponentVersionSummariesByComponentV2(pageRequest, destinationComponentId2a, null, Boolean.FALSE)).thenReturn(result2);

        MigratableHttpResult<Page<BdComponentVersionSummary>> migratableHttpResult = bdComponentApi.findComponentVersionSummariesByComponentV2(pageRequest,
                COMPONENT_ID,
                null, Boolean.FALSE);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null)).build();
        assertHttpResult(result2, migratableHttpResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindComponentVersionSummariesByComponentV2WhenPresentAndMigratedWithMultipleMigrations() {
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
        HttpResult<Page<ComponentVersionSummary>> result1 = constructHttpResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Page<ComponentVersionSummary>> httpResponse2 = constructMergeMigratedHttpResponse(requestUri2a, requestUri3);
        HttpResult<Page<ComponentVersionSummary>> result2 = constructHttpResult(REQUEST_METHOD, requestUri2a, httpResponse2);

        // HTTP 200 OK response
        Page<ComponentVersionSummary> componentVersionPage3 = constructComponentVersionSummaryPage(destinationComponentId3);
        HttpResponse<Page<ComponentVersionSummary>> httpResponse3 = constructHttpResponse(componentVersionPage3);
        HttpResult<Page<ComponentVersionSummary>> result3 = constructHttpResult(REQUEST_METHOD, requestUri3, httpResponse3);

        Mockito.when(componentApi.findComponentVersionSummariesByComponentV2(pageRequest, COMPONENT_ID, null, Boolean.FALSE)).thenReturn(result1);
        Mockito.when(componentApi.findComponentVersionSummariesByComponentV2(pageRequest, destinationComponentId2a, null, Boolean.FALSE)).thenReturn(result2);
        Mockito.when(componentApi.findComponentVersionSummariesByComponentV2(pageRequest, destinationComponentId3, null, Boolean.FALSE)).thenReturn(result3);

        MigratableHttpResult<Page<BdComponentVersionSummary>> migratableHttpResult = bdComponentApi.findComponentVersionSummariesByComponentV2(pageRequest,
                COMPONENT_ID,
                null, Boolean.FALSE);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null))
                .add(httpResponse2.getMigratedMeta().orElse(null)).build();
        assertHttpResult(result3, migratableHttpResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindComponentVersionSummariesByComponentV2WhenPresentAndRetriesExhausted() {
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
        HttpResult<Page<ComponentVersionSummary>> result1 = constructHttpResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Page<ComponentVersionSummary>> httpResponse2 = constructMergeMigratedHttpResponse(requestUri2, requestUri3);
        HttpResult<Page<ComponentVersionSummary>> result2 = constructHttpResult(REQUEST_METHOD, requestUri2, httpResponse2);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<Page<ComponentVersionSummary>> httpResponse3 = constructMergeMigratedHttpResponse(requestUri3, requestUri4);
        HttpResult<Page<ComponentVersionSummary>> result3 = constructHttpResult(REQUEST_METHOD, requestUri3, httpResponse3);

        Mockito.when(componentApi.findComponentVersionSummariesByComponentV2(pageRequest, COMPONENT_ID, null, Boolean.FALSE)).thenReturn(result1);
        Mockito.when(componentApi.findComponentVersionSummariesByComponentV2(pageRequest, destinationComponentId2, null, Boolean.FALSE)).thenReturn(result2);
        Mockito.when(componentApi.findComponentVersionSummariesByComponentV2(pageRequest, destinationComponentId3, null, Boolean.FALSE)).thenReturn(result3);

        MigratableHttpResult<Page<BdComponentVersionSummary>> migratableHttpResult = bdComponentApi.findComponentVersionSummariesByComponentV2(pageRequest,
                COMPONENT_ID,
                null, Boolean.FALSE);

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
    public void testFindOngoingVersionByComponentV3WhenAbsent() {
        // HTTP 404 Not Found response
        String requestUri = constructOngoingVersionHref(COMPONENT_ID);
        HttpResponse<OngoingVersion> httpResponse = new HttpResponse<>(404, Set.of(200, 404), null, null);
        HttpResult<OngoingVersion> httpResult = constructHttpResult(REQUEST_METHOD, requestUri, httpResponse);

        Mockito.when(componentApi.findOngoingVersionByComponentV3(COMPONENT_ID)).thenReturn(httpResult);

        MigratableHttpResult<OngoingVersion> migratableHttpResult = bdComponentApi.findOngoingVersionByComponentV3(COMPONENT_ID);

        assertHttpResult(httpResult, migratableHttpResult, Collections.emptyList());
    }

    @Test
    public void testFindOngoingVersionByComponentV3WhenPresentAndNotMigrated() {
        // HTTP 200 OK response
        String requestUri = constructComponentHref(COMPONENT_ID);
        OngoingVersion ongoingVersion = constructOngoingVersion(COMPONENT_ID);
        HttpResponse<OngoingVersion> httpResponse = constructHttpResponse(ongoingVersion);
        HttpResult<OngoingVersion> httpResult = constructHttpResult(REQUEST_METHOD, requestUri, httpResponse);

        Mockito.when(componentApi.findOngoingVersionByComponentV3(COMPONENT_ID)).thenReturn(httpResult);

        MigratableHttpResult<OngoingVersion> migratableHttpResult = bdComponentApi.findOngoingVersionByComponentV3(COMPONENT_ID);

        assertHttpResult(httpResult, migratableHttpResult, Collections.emptyList());
    }

    @Test
    public void testFindOngoingVersionByComponentV3WhenPresentAndMergeMigrated() {
        String requestUri1 = constructComponentHref(COMPONENT_ID);
        UUID destinationComponentId2 = UUID.randomUUID();
        String requestUri2 = constructComponentHref(destinationComponentId2);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<OngoingVersion> httpResponse1 = constructMergeMigratedHttpResponse(requestUri1, requestUri2);
        HttpResult<OngoingVersion> result1 = constructHttpResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 200 OK response
        OngoingVersion ongoingVersion = constructOngoingVersion(destinationComponentId2);
        HttpResponse<OngoingVersion> httpResponse2 = constructHttpResponse(ongoingVersion);
        HttpResult<OngoingVersion> result2 = constructHttpResult(REQUEST_METHOD, requestUri2, httpResponse2);

        Mockito.when(componentApi.findOngoingVersionByComponentV3(COMPONENT_ID)).thenReturn(result1);
        Mockito.when(componentApi.findOngoingVersionByComponentV3(destinationComponentId2)).thenReturn(result2);

        MigratableHttpResult<OngoingVersion> migratableHttpResult = bdComponentApi.findOngoingVersionByComponentV3(COMPONENT_ID);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null)).build();
        assertHttpResult(result2, migratableHttpResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindOngoingVersionByComponentV3WhenPresentAndSplitMigrated() {
        String requestUri1 = constructComponentHref(COMPONENT_ID);
        UUID destinationComponentId2a = UUID.randomUUID();
        UUID destinationComponentId2b = UUID.randomUUID();
        UUID destinationComponentId2c = UUID.randomUUID();
        String requestUri2a = constructComponentHref(destinationComponentId2a);
        String requestUri2b = constructComponentHref(destinationComponentId2b);
        String requestUri2c = constructComponentHref(destinationComponentId2c);

        // HTTP 300 Multiple Choices response (split migration)
        HttpResponse<OngoingVersion> httpResponse1 = constructSplitMigratedHttpResponse(requestUri1,
                List.of(requestUri2a, requestUri2b, requestUri2c));
        HttpResult<OngoingVersion> result1 = constructHttpResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 200 OK response
        OngoingVersion ongoingVersion2 = constructOngoingVersion(destinationComponentId2a);
        HttpResponse<OngoingVersion> httpResponse2 = constructHttpResponse(ongoingVersion2);
        HttpResult<OngoingVersion> result2 = constructHttpResult(REQUEST_METHOD, requestUri2a, httpResponse2);

        Mockito.when(componentApi.findOngoingVersionByComponentV3(COMPONENT_ID)).thenReturn(result1);
        Mockito.when(componentApi.findOngoingVersionByComponentV3(destinationComponentId2a)).thenReturn(result2);

        MigratableHttpResult<OngoingVersion> migratableHttpResult = bdComponentApi.findOngoingVersionByComponentV3(COMPONENT_ID);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null)).build();
        assertHttpResult(result2, migratableHttpResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindOngoingVersionByComponentV3WhenPresentAndMigratedWithMultipleMigrations() {
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
        HttpResponse<OngoingVersion> httpResponse1 = constructSplitMigratedHttpResponse(requestUri1,
                List.of(requestUri2a, requestUri2b, requestUri2c));
        HttpResult<OngoingVersion> result1 = constructHttpResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<OngoingVersion> httpResponse2 = constructMergeMigratedHttpResponse(requestUri2a, requestUri3);
        HttpResult<OngoingVersion> result2 = constructHttpResult(REQUEST_METHOD, requestUri2a, httpResponse2);

        // HTTP 200 OK response
        OngoingVersion ongoingVersion3 = constructOngoingVersion(destinationComponentId3);
        HttpResponse<OngoingVersion> httpResponse3 = constructHttpResponse(ongoingVersion3);
        HttpResult<OngoingVersion> result3 = constructHttpResult(REQUEST_METHOD, requestUri3, httpResponse3);

        Mockito.when(componentApi.findOngoingVersionByComponentV3(COMPONENT_ID)).thenReturn(result1);
        Mockito.when(componentApi.findOngoingVersionByComponentV3(destinationComponentId2a)).thenReturn(result2);
        Mockito.when(componentApi.findOngoingVersionByComponentV3(destinationComponentId3)).thenReturn(result3);

        MigratableHttpResult<OngoingVersion> migratableHttpResult = bdComponentApi.findOngoingVersionByComponentV3(COMPONENT_ID);

        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null))
                .add(httpResponse2.getMigratedMeta().orElse(null)).build();
        assertHttpResult(result3, migratableHttpResult, expectedMigratedMetaHistory);
    }

    @Test
    public void testFindOngoingVersionByComponentV3WhenPresentAndRetriesExhausted() {
        String requestUri1 = constructComponentHref(COMPONENT_ID);
        UUID destinationComponentId2 = UUID.randomUUID();
        UUID destinationComponentId3 = UUID.randomUUID();
        UUID destinationComponentId4 = UUID.randomUUID();
        String requestUri2 = constructComponentHref(destinationComponentId2);
        String requestUri3 = constructComponentHref(destinationComponentId3);
        String requestUri4 = constructComponentHref(destinationComponentId4);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<OngoingVersion> httpResponse1 = constructMergeMigratedHttpResponse(requestUri1, requestUri2);
        HttpResult<OngoingVersion> result1 = constructHttpResult(REQUEST_METHOD, requestUri1, httpResponse1);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<OngoingVersion> httpResponse2 = constructMergeMigratedHttpResponse(requestUri2, requestUri3);
        HttpResult<OngoingVersion> result2 = constructHttpResult(REQUEST_METHOD, requestUri2, httpResponse2);

        // HTTP 301 Moved Permanently response (merge migration)
        HttpResponse<OngoingVersion> httpResponse3 = constructMergeMigratedHttpResponse(requestUri3, requestUri4);
        HttpResult<OngoingVersion> result3 = constructHttpResult(REQUEST_METHOD, requestUri3, httpResponse3);

        Mockito.when(componentApi.findOngoingVersionByComponentV3(COMPONENT_ID)).thenReturn(result1);
        Mockito.when(componentApi.findOngoingVersionByComponentV3(destinationComponentId2)).thenReturn(result2);
        Mockito.when(componentApi.findOngoingVersionByComponentV3(destinationComponentId3)).thenReturn(result3);

        MigratableHttpResult<OngoingVersion> migratableHttpResult = bdComponentApi.findOngoingVersionByComponentV3(COMPONENT_ID);

        // Initial request
        // First migrated request
        // Second migrated request
        // Retries exhausted and return 'second migrated response'.
        List<Meta> expectedMigratedMetaHistory = ImmutableList.<Meta> builder()
                .add(httpResponse1.getMigratedMeta().orElse(null))
                .add(httpResponse2.getMigratedMeta().orElse(null)).build();
        assertHttpResult(result3, migratableHttpResult, expectedMigratedMetaHistory);
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

    private String constructOngoingVersionHref(UUID componentId) {
        return BASE_HREF + "/api/components/" + componentId + "/ongoing-version";
    }
}
