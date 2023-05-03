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

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.hc.core5.http.HttpStatus;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.synopsys.kb.httpclient.AbstractFuncTest;
import com.synopsys.kb.httpclient.api.HttpResponse;
import com.synopsys.kb.httpclient.api.HttpResult;
import com.synopsys.kb.httpclient.api.IComponentApi;
import com.synopsys.kb.httpclient.api.IKbHttpApi;
import com.synopsys.kb.httpclient.api.PageRequest;
import com.synopsys.kb.httpclient.model.Component;
import com.synopsys.kb.httpclient.model.ComponentSearchResult;
import com.synopsys.kb.httpclient.model.ComponentVersion;
import com.synopsys.kb.httpclient.model.ComponentVersionSummary;
import com.synopsys.kb.httpclient.model.OngoingVersion;
import com.synopsys.kb.httpclient.model.Page;
import com.synopsys.kb.httpclient.model.VulnerabilityScorePriority;
import com.synopsys.kb.httpclient.model.VulnerabilitySourcePriority;

/**
 * 
 * KB component HTTP client functional test.
 * 
 * @author skatzman
 */
public class KbComponentHttpClientFuncTest extends AbstractFuncTest {
    private IComponentApi componentApi;

    @BeforeClass
    public void beforeClass() {
        IKbHttpApi kbHttpApi = getKbHttpApi();
        this.componentApi = kbHttpApi.getComponentApi();
    }

    @Test
    public void testFindComponentV4() {
        UUID componentId = UUID.fromString("b75f622a-30da-46e4-a9c9-56f4ab75e22e");
        HttpResult<Component> httpResult = componentApi.findComponentV4(componentId);

        HttpResponse<Component> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");

        if (httpResponse.isMigrated()) {
            throw new SkipException("This functional test method was written using a non-migrated component id.  "
                    + "Since then, this component id has been migrated by the KnowledgeBase.  "
                    + "Update this test with a new non-migrated component id.");
        }

        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        Component component = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(component, "Component should be initialized.");
        Assert.assertEquals(component.getId(), componentId, "Component ids should be equal.");
    }

    @Test
    public void testFindComponentV4WhenMergeMigrated() {
        UUID mergeComponentId = UUID.fromString("d82fb719-977c-49aa-b799-3953356aa15c");
        HttpResult<Component> httpResult = componentApi.findComponentV4(mergeComponentId);

        HttpResponse<Component> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_MOVED_PERMANENTLY, "Codes should be equal.");
        Assert.assertFalse(httpResponse.isMessageBodyPresent(), "Message body should not be present.");
        Assert.assertTrue(httpResponse.isMigratedMetaPresent(), "Migrated meta should be present.");
        Assert.assertTrue(httpResponse.isMigrated(), "Entity should be migrated.");
        Assert.assertTrue(httpResponse.isMergeMigrated(), "Entity should be merge migrated.");
        Assert.assertFalse(httpResponse.isSplitMigrated(), "Entity should not be split migrated.");
    }

    @Test
    public void testFindComponentV4WhenSplitMigrated() {
        UUID splitComponentId = UUID.fromString("2510dcac-ef8a-4088-a60c-ae6605054c3c");
        HttpResult<Component> httpResult = componentApi.findComponentV4(splitComponentId);

        HttpResponse<Component> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_MULTIPLE_CHOICES, "Codes should be equal.");
        Assert.assertFalse(httpResponse.isMessageBodyPresent(), "Message body should not be present.");
        Assert.assertTrue(httpResponse.isMigratedMetaPresent(), "Migrated meta should be present.");
        Assert.assertTrue(httpResponse.isMigrated(), "Entity should be migrated.");
        Assert.assertFalse(httpResponse.isMergeMigrated(), "Entity should not be merge migrated.");
        Assert.assertTrue(httpResponse.isSplitMigrated(), "Entity should be split migrated.");
    }

    @Test
    public void testFindComponentVersionsByComponentV4() {
        PageRequest pageRequest = new PageRequest(0, 10, Collections.emptyList());
        UUID componentId = UUID.fromString("b75f622a-30da-46e4-a9c9-56f4ab75e22e");

        HttpResult<Page<ComponentVersion>> httpResult = componentApi.findComponentVersionsByComponentV4(pageRequest, componentId, null,
                VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3, Boolean.FALSE);

        HttpResponse<Page<ComponentVersion>> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");

        if (httpResponse.isMigrated()) {
            throw new SkipException("This functional test method was written using a non-migrated component id.  "
                    + "Since then, this component id has been migrated by the KnowledgeBase.  "
                    + "Update this test with a new non-migrated component id.");
        }

        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        Page<ComponentVersion> page = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(page, "Page should be initialized.");
        List<ComponentVersion> items = page.getItems();
        Assert.assertNotNull(items, "Items should be initialized.");
        Assert.assertFalse(items.isEmpty(), "Items should not be empty.");
    }

    @Test
    public void testFindComponentVersionsByComponentV4WhenMergeMigrated() {
        PageRequest pageRequest = new PageRequest(0, 10, Collections.emptyList());
        UUID mergeComponentId = UUID.fromString("d82fb719-977c-49aa-b799-3953356aa15c");

        HttpResult<Page<ComponentVersion>> httpResult = componentApi.findComponentVersionsByComponentV4(pageRequest, mergeComponentId, null,
                VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3, Boolean.FALSE);

        HttpResponse<Page<ComponentVersion>> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_MOVED_PERMANENTLY, "Codes should be equal.");
        Assert.assertFalse(httpResponse.isMessageBodyPresent(), "Message body should not be present.");
        Assert.assertTrue(httpResponse.isMigratedMetaPresent(), "Migrated meta should be present.");
        Assert.assertTrue(httpResponse.isMigrated(), "Entity should be migrated.");
        Assert.assertTrue(httpResponse.isMergeMigrated(), "Entity should be merge migrated.");
        Assert.assertFalse(httpResponse.isSplitMigrated(), "Entity should not be split migrated.");
    }

    @Test
    public void testFindComponentVersionsByComponentV4WhenSplitMigrated() {
        PageRequest pageRequest = new PageRequest(0, 10, Collections.emptyList());
        UUID splitComponentId = UUID.fromString("2510dcac-ef8a-4088-a60c-ae6605054c3c");

        HttpResult<Page<ComponentVersion>> httpResult = componentApi.findComponentVersionsByComponentV4(pageRequest, splitComponentId, null,
                VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3, Boolean.FALSE);

        HttpResponse<Page<ComponentVersion>> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_MULTIPLE_CHOICES, "Codes should be equal.");
        Assert.assertFalse(httpResponse.isMessageBodyPresent(), "Message body should not be present.");
        Assert.assertTrue(httpResponse.isMigratedMetaPresent(), "Migrated meta should be present.");
        Assert.assertTrue(httpResponse.isMigrated(), "Entity should be migrated.");
        Assert.assertFalse(httpResponse.isMergeMigrated(), "Entity should not be merge migrated.");
        Assert.assertTrue(httpResponse.isSplitMigrated(), "Entity should be split migrated.");
    }

    @Test
    public void testFindComponentVersionSummariesByComponentV2() {
        PageRequest pageRequest = new PageRequest(0, 10, Collections.emptyList());
        UUID componentId = UUID.fromString("b75f622a-30da-46e4-a9c9-56f4ab75e22e");

        HttpResult<Page<ComponentVersionSummary>> httpResult = componentApi.findComponentVersionSummariesByComponentV2(pageRequest, componentId, null,
                Boolean.FALSE);

        HttpResponse<Page<ComponentVersionSummary>> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");

        if (httpResponse.isMigrated()) {
            throw new SkipException("This functional test method was written using a non-migrated component id.  "
                    + "Since then, this component id has been migrated by the KnowledgeBase.  "
                    + "Update this test with a new non-migrated component id.");
        }

        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        Page<ComponentVersionSummary> page = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(page, "Page should be initialized.");
        List<ComponentVersionSummary> items = page.getItems();
        Assert.assertNotNull(items, "Items should be initialized.");
        Assert.assertFalse(items.isEmpty(), "Items should not be empty.");
    }

    @Test
    public void testFindComponentVersionSummariesByComponentV2WhenMergeMigrated() {
        PageRequest pageRequest = new PageRequest(0, 10, Collections.emptyList());
        UUID mergeComponentId = UUID.fromString("d82fb719-977c-49aa-b799-3953356aa15c");

        HttpResult<Page<ComponentVersionSummary>> httpResult = componentApi.findComponentVersionSummariesByComponentV2(pageRequest, mergeComponentId, null,
                Boolean.FALSE);

        HttpResponse<Page<ComponentVersionSummary>> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_MOVED_PERMANENTLY, "Codes should be equal.");
        Assert.assertFalse(httpResponse.isMessageBodyPresent(), "Message body should not be present.");
        Assert.assertTrue(httpResponse.isMigratedMetaPresent(), "Migrated meta should be present.");
        Assert.assertTrue(httpResponse.isMigrated(), "Entity should be migrated.");
        Assert.assertTrue(httpResponse.isMergeMigrated(), "Entity should be merge migrated.");
        Assert.assertFalse(httpResponse.isSplitMigrated(), "Entity should not be split migrated.");
    }

    @Test
    public void testFindComponentVersionSummariesByComponentV2WhenSplitMigrated() {
        PageRequest pageRequest = new PageRequest(0, 10, Collections.emptyList());
        UUID splitComponentId = UUID.fromString("2510dcac-ef8a-4088-a60c-ae6605054c3c");

        HttpResult<Page<ComponentVersionSummary>> httpResult = componentApi.findComponentVersionSummariesByComponentV2(pageRequest, splitComponentId, null,
                Boolean.FALSE);

        HttpResponse<Page<ComponentVersionSummary>> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_MULTIPLE_CHOICES, "Codes should be equal.");
        Assert.assertFalse(httpResponse.isMessageBodyPresent(), "Message body should not be present.");
        Assert.assertTrue(httpResponse.isMigratedMetaPresent(), "Migrated meta should be present.");
        Assert.assertTrue(httpResponse.isMigrated(), "Entity should be migrated.");
        Assert.assertFalse(httpResponse.isMergeMigrated(), "Entity should not be merge migrated.");
        Assert.assertTrue(httpResponse.isSplitMigrated(), "Entity should be split migrated.");
    }

    @Test
    public void testFindOngoingVersionByComponentV3() {
        UUID componentId = UUID.fromString("9f74f66e-5c27-48d0-9083-e31e3563b7b2");

        HttpResult<OngoingVersion> httpResult = componentApi.findOngoingVersionByComponentV3(componentId);

        HttpResponse<OngoingVersion> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");

        if (httpResponse.isMigrated()) {
            throw new SkipException("This functional test method was written using a non-migrated component id.  "
                    + "Since then, this component id has been migrated by the KnowledgeBase.  "
                    + "Update this test with a new non-migrated component id.");
        }

        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        OngoingVersion ongoingVersion = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(ongoingVersion, "Ongoing version should be initialized.");
        Assert.assertEquals(ongoingVersion.getComponentId(), componentId, "Component ids should be equal.");
    }

    @Test
    public void testFindOngoingVersionByComponentV3WhenMergeMigrated() {
        UUID mergeComponentId = UUID.fromString("d82fb719-977c-49aa-b799-3953356aa15c");

        HttpResult<OngoingVersion> httpResult = componentApi.findOngoingVersionByComponentV3(mergeComponentId);

        HttpResponse<OngoingVersion> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_MOVED_PERMANENTLY, "Codes should be equal.");
        Assert.assertFalse(httpResponse.isMessageBodyPresent(), "Message body should not be present.");
        Assert.assertTrue(httpResponse.isMigratedMetaPresent(), "Migrated meta should be present.");
        Assert.assertTrue(httpResponse.isMigrated(), "Entity should be migrated.");
        Assert.assertTrue(httpResponse.isMergeMigrated(), "Entity should be merge migrated.");
        Assert.assertFalse(httpResponse.isSplitMigrated(), "Entity should not be split migrated.");
    }

    @Test
    public void testFindOngoingVersionByComponentV3WhenSplitMigrated() {
        UUID splitComponentId = UUID.fromString("2510dcac-ef8a-4088-a60c-ae6605054c3c");

        HttpResult<OngoingVersion> httpResult = componentApi.findOngoingVersionByComponentV3(splitComponentId);

        HttpResponse<OngoingVersion> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_MULTIPLE_CHOICES, "Codes should be equal.");
        Assert.assertFalse(httpResponse.isMessageBodyPresent(), "Message body should not be present.");
        Assert.assertTrue(httpResponse.isMigratedMetaPresent(), "Migrated meta should be present.");
        Assert.assertTrue(httpResponse.isMigrated(), "Entity should be migrated.");
        Assert.assertFalse(httpResponse.isMergeMigrated(), "Entity should not be merge migrated.");
        Assert.assertTrue(httpResponse.isSplitMigrated(), "Entity should be split migrated.");
    }

    @Test
    public void testSearchComponentsV3() {
        PageRequest pageRequest = new PageRequest(0, 100, Collections.emptyList());
        String searchTermFilter = "maven:org.apache.logging.log4j:log4j-core:2.4.1";
        HttpResult<Page<ComponentSearchResult>> httpResult = componentApi.searchComponentsV3(pageRequest, searchTermFilter, false);

        HttpResponse<Page<ComponentSearchResult>> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        Page<ComponentSearchResult> page = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(page, "Page should be initialized.");
        List<ComponentSearchResult> items = page.getItems();
        Assert.assertNotNull(items, "Items should be initialized.");
        Assert.assertFalse(items.isEmpty(), "Items should not be empty.");
    }
}
