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
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.hc.core5.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.synopsys.kb.httpclient.AbstractFuncTest;
import com.synopsys.kb.httpclient.api.HttpResponse;
import com.synopsys.kb.httpclient.api.IActivityApi;
import com.synopsys.kb.httpclient.api.IKbHttpApi;
import com.synopsys.kb.httpclient.api.Result;
import com.synopsys.kb.httpclient.model.ComponentActivity;
import com.synopsys.kb.httpclient.model.ListHolder;

/**
 * 
 * KB activity HTTP client functional test.
 * 
 * @author skatzman
 */
public class KbActivityHttpClientFuncTest extends AbstractFuncTest {
    private IActivityApi activityApi;

    @BeforeMethod
    public void beforeMethod() {
        IKbHttpApi kbHttpApi = getKbHttpApi();
        this.activityApi = kbHttpApi.getActivityApi();
    }

    @Test(enabled = false)
    public void testFindComponentActivitiesWithNoResults() {
        Set<UUID> componentIds = Set.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        // Assign to future date to increase probability of no results.
        OffsetDateTime activitySince = OffsetDateTime.now().plusMonths(1L);

        Result<ListHolder<ComponentActivity>> result = activityApi.findComponentActivities(componentIds, activitySince);

        HttpResponse<ListHolder<ComponentActivity>> httpResponse = result.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        ListHolder<ComponentActivity> listHolder = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(listHolder, "List holder should be initialized.");
        List<ComponentActivity> componentActivities = listHolder.getItems();
        Assert.assertNotNull(componentActivities, "Component activities should be initialized.");
        Assert.assertTrue(componentActivities.isEmpty(), "Component activities should be empty.");
    }

    @Test(enabled = false)
    public void testFindComponentActivitiesWithResults() {
        UUID componentId1 = UUID.fromString("b75f622a-30da-46e4-a9c9-56f4ab75e22e");
        UUID componentId2 = UUID.fromString("d82fb719-977c-49aa-b799-3953356aa15c");
        UUID componentId3 = UUID.fromString("2510dcac-ef8a-4088-a60c-ae6605054c3c");
        Set<UUID> componentIds = Set.of(componentId1, componentId2, componentId3);

        // Assign to far past date for results.
        OffsetDateTime activitySince = OffsetDateTime.now().minusYears(20L);

        Result<ListHolder<ComponentActivity>> result = activityApi.findComponentActivities(componentIds, activitySince);

        HttpResponse<ListHolder<ComponentActivity>> httpResponse = result.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        ListHolder<ComponentActivity> listHolder = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(listHolder, "List holder should be initialized.");
        List<ComponentActivity> componentActivities = listHolder.getItems();
        Assert.assertNotNull(componentActivities, "Component activities should be initialized.");
        Assert.assertEquals(componentActivities.size(), componentIds.size(), "Number of component activities should be equal.");
        Set<UUID> actualComponentIds = componentActivities.stream().map(ComponentActivity::getComponentId).collect(Collectors.toSet());
        Assert.assertEquals(actualComponentIds.size(), componentIds.size(), "Number of component activities should be equal.");
        Assert.assertTrue(actualComponentIds.containsAll(componentIds), "Component ids should be present.");
    }
}
