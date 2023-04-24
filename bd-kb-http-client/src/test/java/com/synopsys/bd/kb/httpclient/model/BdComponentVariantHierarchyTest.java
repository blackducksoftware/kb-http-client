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
import com.synopsys.bd.kb.httpclient.api.MigratableResult;
import com.synopsys.kb.httpclient.api.HttpResponse;
import com.synopsys.kb.httpclient.api.Result;
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

    private MigratableResult<Component> componentResult;

    private MigratableResult<BdComponentVersion> componentVersionResult;

    private Result<BdComponentVariant> componentVariantResult;

    @BeforeMethod
    public void beforeMethod() {
        Component component = constructComponent(COMPONENT_ID, "FooComponent");
        MigratableHttpResponse<Component> componentHttpResponse = new MigratableHttpResponse<>(200, Set.of(200, 404), component, null, null);
        componentResult = new MigratableResult<>("GET", BASE_HREF + "/api/components/" + COMPONENT_ID, componentHttpResponse, null);

        ComponentVersion componentVersion = constructComponentVersion(COMPONENT_ID, COMPONENT_VERSION_ID, "1.0");
        BdComponentVersion bdComponentVersion = new BdComponentVersion(componentVersion, BASE_HREF);
        MigratableHttpResponse<BdComponentVersion> componentVersionHttpResponse = new MigratableHttpResponse<>(200, Set.of(200, 402, 403, 404),
                bdComponentVersion, null, null);
        componentVersionResult = new MigratableResult<>("GET", BASE_HREF + "/api/versions/" + COMPONENT_VERSION_ID, componentVersionHttpResponse, null);

        ComponentVariant componentVariant = constructComponentVariant(COMPONENT_ID, COMPONENT_VERSION_ID, COMPONENT_VARIANT_ID, "1.0", "maven", "foo:foo:1.0");
        BdComponentVariant bdComponentVariant = new BdComponentVariant(componentVariant);
        HttpResponse<BdComponentVariant> componentVariantHttpResponse = new HttpResponse<>(200, Set.of(200, 404), bdComponentVariant, null);
        componentVariantResult = new Result<>("GET", BASE_HREF + "/api/variants/" + COMPONENT_VARIANT_ID, componentVariantHttpResponse, null);
    }

    @Test
    public void testConstructor() {
        BdComponentVariantHierarchy hierarchy = new BdComponentVariantHierarchy(componentResult, componentVersionResult, componentVariantResult);

        Assert.assertEquals(hierarchy.getComponentResult(), componentResult, "Component results should be equal.");
        Assert.assertEquals(hierarchy.getComponentVersionResult(), componentVersionResult, "Component version results should be equal.");
        Assert.assertEquals(hierarchy.getComponentVariantResult(), componentVariantResult, "Component variant results should be equal.");
    }

    @Test
    public void testIsHierarchyPresentWithAbsentComponent() {
        MigratableHttpResponse<Component> componentHttpResponse = new MigratableHttpResponse<>(404, Set.of(200, 404), null, null, null);
        MigratableResult<Component> absentComponentResult = new MigratableResult<>("GET", BASE_HREF + "/api/components/" + COMPONENT_ID,
                componentHttpResponse, null);

        BdComponentVariantHierarchy hierarchy = new BdComponentVariantHierarchy(absentComponentResult, componentVersionResult, componentVariantResult);

        Assert.assertFalse(hierarchy.isHierarchyPresent(), "Hierarchy should not be present.");
    }

    @Test
    public void testIsHierarchyPresentWithAbsentComponentVersion() {
        MigratableHttpResponse<BdComponentVersion> componentVersionHttpResponse = new MigratableHttpResponse<>(404, Set.of(200, 402, 403, 404),
                null, null, null);
        MigratableResult<BdComponentVersion> absentComponentVersionResult = new MigratableResult<>("GET", BASE_HREF + "/api/versions/" + COMPONENT_VERSION_ID,
                componentVersionHttpResponse, null);

        BdComponentVariantHierarchy hierarchy = new BdComponentVariantHierarchy(componentResult, absentComponentVersionResult, componentVariantResult);

        Assert.assertFalse(hierarchy.isHierarchyPresent(), "Hierarchy should not be present.");
    }

    @Test
    public void testIsHierarchyPresentWithAbsentComponentVariant() {
        HttpResponse<BdComponentVariant> componentVariantHttpResponse = new HttpResponse<>(200, Set.of(200, 404), null, null);
        Result<BdComponentVariant> absentComponentVariantResult = new Result<>("GET", BASE_HREF + "/api/variants/" + COMPONENT_VARIANT_ID,
                componentVariantHttpResponse, null);

        BdComponentVariantHierarchy hierarchy = new BdComponentVariantHierarchy(componentResult, componentVersionResult, absentComponentVariantResult);

        Assert.assertFalse(hierarchy.isHierarchyPresent(), "Hierarchy should not be present.");
    }

    @Test
    public void testIsHierarchyPresentWithAllPresent() {
        BdComponentVariantHierarchy hierarchy = new BdComponentVariantHierarchy(componentResult, componentVersionResult, componentVariantResult);

        Assert.assertTrue(hierarchy.isHierarchyPresent(), "Hierarchy should be present.");
    }
}
