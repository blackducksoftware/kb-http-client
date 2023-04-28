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
package com.synopsys.bd.kb.httpclient.model;

import java.util.Set;
import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.synopsys.bd.kb.httpclient.AbstractBdTest;
import com.synopsys.bd.kb.httpclient.api.MigratableHttpResponse;
import com.synopsys.bd.kb.httpclient.api.MigratableHttpResult;
import com.synopsys.kb.httpclient.model.Component;
import com.synopsys.kb.httpclient.model.ComponentVersion;

/**
 * Black Duck-centric component version hierarchy test.
 * 
 * @author skatzman
 */
public class BdComponentVersionHierarchyTest extends AbstractBdTest {
    private static final UUID COMPONENT_ID = UUID.randomUUID();

    private static final UUID COMPONENT_VERSION_ID = UUID.randomUUID();

    private MigratableHttpResult<Component> componentHttpResult;

    private MigratableHttpResult<BdComponentVersion> componentVersionHttpResult;

    @BeforeMethod
    public void beforeMethod() {
        Component component = constructComponent(COMPONENT_ID, "FooComponent");
        MigratableHttpResponse<Component> componentHttpResponse = new MigratableHttpResponse<>(200, Set.of(200, 404), component, null, null);
        componentHttpResult = new MigratableHttpResult<>("GET", BASE_HREF + "/api/components/" + COMPONENT_ID, componentHttpResponse, null);

        ComponentVersion componentVersion = constructComponentVersion(COMPONENT_ID, COMPONENT_VERSION_ID, "1.0");
        BdComponentVersion bdComponentVersion = new BdComponentVersion(componentVersion, BASE_HREF);
        MigratableHttpResponse<BdComponentVersion> componentVersionHttpResponse = new MigratableHttpResponse<>(200, Set.of(200, 402, 403, 404),
                bdComponentVersion, null, null);
        componentVersionHttpResult = new MigratableHttpResult<>("GET", BASE_HREF + "/api/versions/" + COMPONENT_VERSION_ID,
                componentVersionHttpResponse, null);
    }

    @Test
    public void testConstructor() {
        BdComponentVersionHierarchy hierarchy = new BdComponentVersionHierarchy(componentHttpResult, componentVersionHttpResult);

        Assert.assertEquals(hierarchy.getComponentResult(), componentHttpResult, "Component results should be equal.");
        Assert.assertEquals(hierarchy.getComponentVersionResult(), componentVersionHttpResult, "Component version results should be equal.");
    }

    @Test
    public void testIsHierarchyPresentWithBothAbsent() {
        MigratableHttpResponse<Component> absentComponentHttpResponse = new MigratableHttpResponse<>(404, Set.of(200, 404), null, null, null);
        MigratableHttpResult<Component> absentComponentResult = new MigratableHttpResult<>("GET", BASE_HREF + "/api/components/" + COMPONENT_ID,
                absentComponentHttpResponse, null);

        MigratableHttpResponse<BdComponentVersion> absentComponentVersionHttpResponse = new MigratableHttpResponse<>(404, Set.of(200, 402, 403, 404),
                null, null, null);
        MigratableHttpResult<BdComponentVersion> absentComponentVersionResult = new MigratableHttpResult<>("GET",
                BASE_HREF + "/api/versions/" + COMPONENT_VERSION_ID,
                absentComponentVersionHttpResponse, null);

        BdComponentVersionHierarchy hierarchy = new BdComponentVersionHierarchy(absentComponentResult, absentComponentVersionResult);

        Assert.assertFalse(hierarchy.isHierarchyPresent(), "Component and component version should not be present.");
    }

    @Test
    public void testIsHierarchyPresentWithPresentComponent() {
        MigratableHttpResponse<BdComponentVersion> absentComponentVersionHttpResponse = new MigratableHttpResponse<>(404, Set.of(200, 402, 403, 404),
                null, null, null);
        MigratableHttpResult<BdComponentVersion> absentComponentVersionResult = new MigratableHttpResult<>("GET",
                BASE_HREF + "/api/versions/" + COMPONENT_VERSION_ID,
                absentComponentVersionHttpResponse, null);

        BdComponentVersionHierarchy hierarchy = new BdComponentVersionHierarchy(componentHttpResult, absentComponentVersionResult);

        Assert.assertFalse(hierarchy.isHierarchyPresent(), "Component and component version should not be present.");
    }

    @Test
    public void testIsHierarchyPresentWithComponentVersionPresent() {
        MigratableHttpResponse<Component> absentComponentHttpResponse = new MigratableHttpResponse<>(404, Set.of(200, 404), null, null, null);
        MigratableHttpResult<Component> absentComponentHttpResult = new MigratableHttpResult<>("GET", BASE_HREF + "/api/components/" + COMPONENT_ID,
                absentComponentHttpResponse, null);

        BdComponentVersionHierarchy hierarchy = new BdComponentVersionHierarchy(absentComponentHttpResult, componentVersionHttpResult);

        Assert.assertFalse(hierarchy.isHierarchyPresent(), "Component and component version should not be present.");
    }

    @Test
    public void testIsHierarchyPresentWithBothPresent() {
        BdComponentVersionHierarchy hierarchy = new BdComponentVersionHierarchy(componentHttpResult, componentVersionHttpResult);

        Assert.assertTrue(hierarchy.isHierarchyPresent(), "Component and component version should be present.");
    }
}
