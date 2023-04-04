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
import com.synopsys.kb.httpclient.api.HttpResponse;
import com.synopsys.kb.httpclient.api.IComponentApi;
import com.synopsys.kb.httpclient.api.Result;
import com.synopsys.kb.httpclient.model.Component;
import com.synopsys.kb.httpclient.model.Logo;
import com.synopsys.kb.httpclient.model.Meta;

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
        this.bdComponentApi = new BdComponentApi(componentApi, 3);
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

    private String constructComponentHref(UUID componentId) {
        return BASE_HREF + "/api/components/" + componentId;
    }
}
