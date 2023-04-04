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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.synopsys.kb.httpclient.AbstractFuncTest;
import com.synopsys.kb.httpclient.api.HttpResponse;
import com.synopsys.kb.httpclient.api.IComponentApi;
import com.synopsys.kb.httpclient.api.IKbHttpApi;
import com.synopsys.kb.httpclient.api.PageRequest;
import com.synopsys.kb.httpclient.api.Result;
import com.synopsys.kb.httpclient.model.Component;
import com.synopsys.kb.httpclient.model.ComponentSearchResult;
import com.synopsys.kb.httpclient.model.Page;

/**
 * 
 * KB component HTTP client functional test.
 * 
 * @author skatzman
 */
public class KbComponentHttpClientFuncTest extends AbstractFuncTest {
    private IComponentApi componentApi;

    @BeforeMethod
    public void beforeMethod() {
        IKbHttpApi kbHttpApi = getKbHttpApi();
        this.componentApi = kbHttpApi.getComponentApi();
    }

    @Test(enabled = false)
    public void testFind() {
        UUID componentId = UUID.fromString("b75f622a-30da-46e4-a9c9-56f4ab75e22e");
        Result<Component> result = componentApi.find(componentId);

        HttpResponse<Component> httpResponse = result.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");

        if (httpResponse.isMigrated()) {
            Assert.fail("This functional test method was written using a non-migrated component id.  "
                    + "Since then, this component id has been migrated by the KnowledgeBase.  "
                    + "Update this test with a new non-migrated component id.");
        }

        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        Component component = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(component, "Component should be initialized.");
        Assert.assertEquals(component.getId(), componentId, "Component ids should be equal.");
    }

    @Test(enabled = false)
    public void testFindWhenMergeMigrated() {
        UUID mergeComponentId = UUID.fromString("d82fb719-977c-49aa-b799-3953356aa15c");
        Result<Component> result = componentApi.find(mergeComponentId);

        HttpResponse<Component> httpResponse = result.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_MOVED_PERMANENTLY, "Codes should be equal.");
        Assert.assertFalse(httpResponse.isMessageBodyPresent(), "Message body should not be present.");
        Assert.assertTrue(httpResponse.isMigratedMetaPresent(), "Migrated meta should be present.");
        Assert.assertTrue(httpResponse.isMigrated(), "Entity should be migrated.");
        Assert.assertTrue(httpResponse.isMergeMigrated(), "Entity should be merge migrated.");
        Assert.assertFalse(httpResponse.isSplitMigrated(), "Entity should not be split migrated.");
    }

    @Test(enabled = false)
    public void testFindWhenSplitMigrated() {
        UUID splitComponentId = UUID.fromString("2510dcac-ef8a-4088-a60c-ae6605054c3c");
        Result<Component> result = componentApi.find(splitComponentId);

        HttpResponse<Component> httpResponse = result.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_MULTIPLE_CHOICES, "Codes should be equal.");
        Assert.assertFalse(httpResponse.isMessageBodyPresent(), "Message body should not be present.");
        Assert.assertTrue(httpResponse.isMigratedMetaPresent(), "Migrated meta should be present.");
        Assert.assertTrue(httpResponse.isMigrated(), "Entity should be migrated.");
        Assert.assertFalse(httpResponse.isMergeMigrated(), "Entity should not be merge migrated.");
        Assert.assertTrue(httpResponse.isSplitMigrated(), "Entity should be split migrated.");
    }

    @Test(enabled = false)
    public void testSearch() {
        PageRequest pageRequest = new PageRequest(0, 100, Collections.emptyList());
        String searchTermFilter = "maven:org.apache.logging.log4j:log4j-core:2.4.1";
        Result<Page<ComponentSearchResult>> result = componentApi.search(pageRequest, searchTermFilter, false);

        HttpResponse<Page<ComponentSearchResult>> httpResponse = result.getHttpResponse().orElse(null);

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
