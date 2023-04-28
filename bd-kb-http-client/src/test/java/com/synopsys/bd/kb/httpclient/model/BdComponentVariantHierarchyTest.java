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
import com.synopsys.kb.httpclient.api.HttpResponse;
import com.synopsys.kb.httpclient.api.HttpResult;
import com.synopsys.kb.httpclient.model.Component;
import com.synopsys.kb.httpclient.model.ComponentVariant;
import com.synopsys.kb.httpclient.model.ComponentVersion;

/**
 * Black Duck-centric component variant hierarchy test.
 * 
 * @author skatzman
 */
public class BdComponentVariantHierarchyTest extends AbstractBdTest {
    private static final UUID COMPONENT_ID = UUID.randomUUID();

    private static final UUID COMPONENT_VERSION_ID = UUID.randomUUID();

    private static final UUID COMPONENT_VARIANT_ID = UUID.randomUUID();

    private MigratableHttpResult<Component> componentHttpResult;

    private MigratableHttpResult<BdComponentVersion> componentVersionHttpResult;

    private HttpResult<BdComponentVariant> componentVariantHttpResult;

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

        ComponentVariant componentVariant = constructComponentVariant(COMPONENT_ID, COMPONENT_VERSION_ID, COMPONENT_VARIANT_ID, "1.0", "maven", "foo:foo:1.0");
        BdComponentVariant bdComponentVariant = new BdComponentVariant(componentVariant);
        HttpResponse<BdComponentVariant> componentVariantHttpResponse = new HttpResponse<>(200, Set.of(200, 404), bdComponentVariant, null);
        componentVariantHttpResult = new HttpResult<>("GET", BASE_HREF + "/api/variants/" + COMPONENT_VARIANT_ID, componentVariantHttpResponse, null);
    }

    @Test
    public void testConstructor() {
        BdComponentVariantHierarchy hierarchy = new BdComponentVariantHierarchy(componentHttpResult, componentVersionHttpResult, componentVariantHttpResult);

        Assert.assertEquals(hierarchy.getComponentResult(), componentHttpResult, "Component HTTP results should be equal.");
        Assert.assertEquals(hierarchy.getComponentVersionResult(), componentVersionHttpResult, "Component version HTTP results should be equal.");
        Assert.assertEquals(hierarchy.getComponentVariantResult(), componentVariantHttpResult, "Component variant HTTP results should be equal.");
    }

    @Test
    public void testIsHierarchyPresentWithAbsentComponent() {
        MigratableHttpResponse<Component> componentHttpResponse = new MigratableHttpResponse<>(404, Set.of(200, 404), null, null, null);
        MigratableHttpResult<Component> absentComponentResult = new MigratableHttpResult<>("GET", BASE_HREF + "/api/components/" + COMPONENT_ID,
                componentHttpResponse, null);

        BdComponentVariantHierarchy hierarchy = new BdComponentVariantHierarchy(absentComponentResult, componentVersionHttpResult, componentVariantHttpResult);

        Assert.assertFalse(hierarchy.isHierarchyPresent(), "Hierarchy should not be present.");
    }

    @Test
    public void testIsHierarchyPresentWithAbsentComponentVersion() {
        MigratableHttpResponse<BdComponentVersion> componentVersionHttpResponse = new MigratableHttpResponse<>(404, Set.of(200, 402, 403, 404),
                null, null, null);
        MigratableHttpResult<BdComponentVersion> absentComponentVersionHttpResult = new MigratableHttpResult<>("GET",
                BASE_HREF + "/api/versions/" + COMPONENT_VERSION_ID,
                componentVersionHttpResponse, null);

        BdComponentVariantHierarchy hierarchy = new BdComponentVariantHierarchy(componentHttpResult, absentComponentVersionHttpResult,
                componentVariantHttpResult);

        Assert.assertFalse(hierarchy.isHierarchyPresent(), "Hierarchy should not be present.");
    }

    @Test
    public void testIsHierarchyPresentWithAbsentComponentVariant() {
        HttpResponse<BdComponentVariant> componentVariantHttpResponse = new HttpResponse<>(200, Set.of(200, 404), null, null);
        HttpResult<BdComponentVariant> absentComponentVariantHttpResult = new HttpResult<>("GET", BASE_HREF + "/api/variants/" + COMPONENT_VARIANT_ID,
                componentVariantHttpResponse, null);

        BdComponentVariantHierarchy hierarchy = new BdComponentVariantHierarchy(componentHttpResult, componentVersionHttpResult,
                absentComponentVariantHttpResult);

        Assert.assertFalse(hierarchy.isHierarchyPresent(), "Hierarchy should not be present.");
    }

    @Test
    public void testIsHierarchyPresentWithAllPresent() {
        BdComponentVariantHierarchy hierarchy = new BdComponentVariantHierarchy(componentHttpResult, componentVersionHttpResult, componentVariantHttpResult);

        Assert.assertTrue(hierarchy.isHierarchyPresent(), "Hierarchy should be present.");
    }
}
