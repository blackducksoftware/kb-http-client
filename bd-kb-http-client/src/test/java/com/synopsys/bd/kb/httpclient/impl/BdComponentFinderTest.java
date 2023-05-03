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
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.synopsys.bd.kb.httpclient.AbstractBdTest;
import com.synopsys.bd.kb.httpclient.api.IBdComponentApi;
import com.synopsys.bd.kb.httpclient.api.IBdComponentVariantApi;
import com.synopsys.bd.kb.httpclient.api.IBdComponentVersionApi;
import com.synopsys.bd.kb.httpclient.api.MigratableHttpResponse;
import com.synopsys.bd.kb.httpclient.api.MigratableHttpResult;
import com.synopsys.bd.kb.httpclient.model.BdComponentVariant;
import com.synopsys.bd.kb.httpclient.model.BdComponentVariantHierarchy;
import com.synopsys.bd.kb.httpclient.model.BdComponentVersion;
import com.synopsys.bd.kb.httpclient.model.BdComponentVersionHierarchy;
import com.synopsys.kb.httpclient.api.HttpResponse;
import com.synopsys.kb.httpclient.api.HttpResult;
import com.synopsys.kb.httpclient.model.Component;
import com.synopsys.kb.httpclient.model.ComponentVariant;
import com.synopsys.kb.httpclient.model.ComponentVersion;
import com.synopsys.kb.httpclient.model.Meta;
import com.synopsys.kb.httpclient.model.VulnerabilityScorePriority;
import com.synopsys.kb.httpclient.model.VulnerabilitySourcePriority;

/**
 * Black Duck-centric component finder test.
 * 
 * @author skatzman
 */
public class BdComponentFinderTest extends AbstractBdTest {
    private static final UUID COMPONENT_ID = UUID.randomUUID();

    private static final UUID COMPONENT_VERSION_ID = UUID.randomUUID();

    private static final UUID COMPONENT_VARIANT_ID = UUID.randomUUID();

    @Mock
    private IBdComponentApi bdComponentApi;

    @Mock
    private IBdComponentVersionApi bdComponentVersionApi;

    @Mock
    private IBdComponentVariantApi bdComponentVariantApi;

    private BdComponentFinder componentFinder;

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.openMocks(this);

        componentFinder = new BdComponentFinder(bdComponentApi, bdComponentVersionApi, bdComponentVariantApi, 2);
    }

    // Component version hierarchy

    @Test
    public void testFindComponentVersionHierarchyWithAbsentComponentVersion() {
        // Find component version - absent response.
        MigratableHttpResponse<BdComponentVersion> componentVersionHttpResponse = new MigratableHttpResponse<>(404, Set.of(200, 402, 403, 404), null, null,
                null);
        MigratableHttpResult<BdComponentVersion> componentVersionResult = new MigratableHttpResult<BdComponentVersion>("GET",
                BASE_HREF + "/api/versions/" + COMPONENT_VERSION_ID, componentVersionHttpResponse, null);

        Mockito.when(bdComponentVersionApi.findComponentVersionV4(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(componentVersionResult);

        Optional<BdComponentVersionHierarchy> result = componentFinder.findComponentVersionHierarchy(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3);

        Assert.assertFalse(result.isPresent(), "Hierarchy should not be present.");
    }

    @Test
    public void testFindComponentVersionHierarchyWithAbsentComponent() {
        // Find component version - present response.
        // Find parent component - absent response.
        ComponentVersion componentVersion = constructComponentVersion(COMPONENT_ID, COMPONENT_VERSION_ID, "1.0");
        BdComponentVersion bdComponentVersion = new BdComponentVersion(componentVersion, BASE_HREF);
        MigratableHttpResponse<BdComponentVersion> componentVersionHttpResponse = new MigratableHttpResponse<>(200, Set.of(200, 402, 403, 404),
                bdComponentVersion, null, null);
        MigratableHttpResult<BdComponentVersion> componentVersionResult = new MigratableHttpResult<>("GET", BASE_HREF + "/api/versions/" + COMPONENT_VERSION_ID,
                componentVersionHttpResponse, null);

        MigratableHttpResponse<Component> componentHttpResponse = new MigratableHttpResponse<>(404, Set.of(200, 404), null, null, null);
        MigratableHttpResult<Component> componentResult = new MigratableHttpResult<>("GET", BASE_HREF + "/api/components/" + COMPONENT_ID,
                componentHttpResponse, null);

        Mockito.when(bdComponentVersionApi.findComponentVersionV4(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(componentVersionResult);
        Mockito.when(bdComponentApi.findComponentV4(COMPONENT_ID)).thenReturn(componentResult);

        Optional<BdComponentVersionHierarchy> result = componentFinder.findComponentVersionHierarchy(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3);

        Assert.assertFalse(result.isPresent(), "Hierarchy should not be present.");
    }

    @Test
    public void testFindComponentVersionHierarchyWithMigratedComponent() {
        // Find component version - present response (round 1).
        // Find parent component - migrated response (round 1).
        // Find component version - present response (round 2).
        // Find parent component - present response (round 2).
        ComponentVersion componentVersion1 = constructComponentVersion(COMPONENT_ID, COMPONENT_VERSION_ID, "1.0");
        BdComponentVersion bdComponentVersion1 = new BdComponentVersion(componentVersion1, BASE_HREF);
        MigratableHttpResponse<BdComponentVersion> componentVersionHttpResponse1 = new MigratableHttpResponse<>(200, Set.of(200, 402, 403, 404),
                bdComponentVersion1, null, null);
        MigratableHttpResult<BdComponentVersion> componentVersionResult1 = new MigratableHttpResult<>("GET",
                BASE_HREF + "/api/versions/" + COMPONENT_VERSION_ID,
                componentVersionHttpResponse1, null);

        UUID componentId2 = UUID.randomUUID();
        Component migratedComponent = constructComponent(componentId2, "MigratedComponent");
        List<Meta> migratedComponentMetaHistory = List.of(new Meta(BASE_HREF + "/api/components/" + COMPONENT_ID, Collections.emptyList()));
        MigratableHttpResponse<Component> migratedComponentHttpResponse = new MigratableHttpResponse<>(200, Set.of(200, 404), migratedComponent, null,
                migratedComponentMetaHistory);
        MigratableHttpResult<Component> migratedComponentResult = new MigratableHttpResult<>("GET", BASE_HREF + "/api/components/" + componentId2,
                migratedComponentHttpResponse, null);

        UUID componentVersionId2 = UUID.randomUUID();
        ComponentVersion componentVersion2 = constructComponentVersion(componentId2, componentVersionId2, "1.0-Migrated");
        BdComponentVersion bdComponentVersion2 = new BdComponentVersion(componentVersion2, BASE_HREF);
        MigratableHttpResponse<BdComponentVersion> componentVersionHttpResponse2 = new MigratableHttpResponse<>(200, Set.of(200, 402, 403, 404),
                bdComponentVersion2, null, null);
        MigratableHttpResult<BdComponentVersion> componentVersionResult2 = new MigratableHttpResult<>("GET", BASE_HREF + "/api/versions/" + componentVersionId2,
                componentVersionHttpResponse2, null);

        Mockito.when(bdComponentVersionApi.findComponentVersionV4(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(componentVersionResult1);
        Mockito.when(bdComponentApi.findComponentV4(COMPONENT_ID)).thenReturn(migratedComponentResult);
        Mockito.when(bdComponentVersionApi.findComponentVersionV4(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(componentVersionResult2);
        Mockito.when(bdComponentApi.findComponentV4(componentId2)).thenReturn(migratedComponentResult);

        Optional<BdComponentVersionHierarchy> result = componentFinder.findComponentVersionHierarchy(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3);

        Assert.assertTrue(result.isPresent(), "Hierarchy should be present.");
    }

    @Test
    public void testFindComponentVersionHierarchy() {
        // Find component version - present response.
        // Find parent component - present response.
        ComponentVersion componentVersion = constructComponentVersion(COMPONENT_ID, COMPONENT_VERSION_ID, "1.0");
        BdComponentVersion bdComponentVersion = new BdComponentVersion(componentVersion, BASE_HREF);
        MigratableHttpResponse<BdComponentVersion> componentVersionHttpResponse = new MigratableHttpResponse<>(200, Set.of(200, 402, 403, 404),
                bdComponentVersion, null, null);
        MigratableHttpResult<BdComponentVersion> componentVersionResult = new MigratableHttpResult<>("GET", BASE_HREF + "/api/versions/" + COMPONENT_VERSION_ID,
                componentVersionHttpResponse, null);

        Component component = constructComponent(COMPONENT_ID, "MigratedComponent");
        MigratableHttpResponse<Component> componentHttpResponse = new MigratableHttpResponse<>(200, Set.of(200, 404), component, null, null);
        MigratableHttpResult<Component> componentResult = new MigratableHttpResult<>("GET", BASE_HREF + "/api/components/" + COMPONENT_ID,
                componentHttpResponse, null);

        Mockito.when(bdComponentVersionApi.findComponentVersionV4(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(componentVersionResult);
        Mockito.when(bdComponentApi.findComponentV4(COMPONENT_ID)).thenReturn(componentResult);

        Optional<BdComponentVersionHierarchy> result = componentFinder.findComponentVersionHierarchy(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3);

        Assert.assertTrue(result.isPresent(), "Hierarchy should be present.");
    }

    @Test
    public void testFindComponentVersionHierarchyWhenExhausted() {
        // Find component version - present response (round 1).
        // Find parent component - migrated response (round 1).
        // Find component version - present response (round 2).
        // Find parent component - migrated response (round 2).
        ComponentVersion componentVersion1 = constructComponentVersion(COMPONENT_ID, COMPONENT_VERSION_ID, "1.0");
        BdComponentVersion bdComponentVersion1 = new BdComponentVersion(componentVersion1, BASE_HREF);
        MigratableHttpResponse<BdComponentVersion> componentVersionHttpResponse1 = new MigratableHttpResponse<>(200, Set.of(200, 402, 403, 404),
                bdComponentVersion1, null, null);
        MigratableHttpResult<BdComponentVersion> componentVersionResult1 = new MigratableHttpResult<>("GET",
                BASE_HREF + "/api/versions/" + COMPONENT_VERSION_ID,
                componentVersionHttpResponse1, null);

        UUID componentId2 = UUID.randomUUID();
        Component migratedComponent1 = constructComponent(componentId2, "MigratedComponent");
        List<Meta> migratedComponentMetaHistory1 = List.of(new Meta(BASE_HREF + "/api/components/" + COMPONENT_ID, Collections.emptyList()));
        MigratableHttpResponse<Component> migratedComponentHttpResponse1 = new MigratableHttpResponse<>(200, Set.of(200, 404), migratedComponent1, null,
                migratedComponentMetaHistory1);
        MigratableHttpResult<Component> migratedComponentResult1 = new MigratableHttpResult<>("GET", BASE_HREF + "/api/components/" + componentId2,
                migratedComponentHttpResponse1, null);

        UUID componentVersionId2 = UUID.randomUUID();
        ComponentVersion componentVersion2 = constructComponentVersion(componentId2, componentVersionId2, "1.0-Migrated");
        BdComponentVersion bdComponentVersion2 = new BdComponentVersion(componentVersion2, BASE_HREF);
        MigratableHttpResponse<BdComponentVersion> componentVersionHttpResponse2 = new MigratableHttpResponse<>(200, Set.of(200, 402, 403, 404),
                bdComponentVersion2, null, null);
        MigratableHttpResult<BdComponentVersion> componentVersionResult2 = new MigratableHttpResult<>("GET", BASE_HREF + "/api/versions/" + componentVersionId2,
                componentVersionHttpResponse2, null);

        UUID componentId3 = UUID.randomUUID();
        Component migratedComponent2 = constructComponent(componentId3, "MigratedAgainComponent");
        List<Meta> migratedComponentMetaHistory2 = List.of(new Meta(BASE_HREF + "/api/components/" + COMPONENT_ID, Collections.emptyList()));
        MigratableHttpResponse<Component> migratedComponentHttpResponse2 = new MigratableHttpResponse<>(200, Set.of(200, 404), migratedComponent2, null,
                migratedComponentMetaHistory2);
        MigratableHttpResult<Component> migratedComponentResult2 = new MigratableHttpResult<>("GET", BASE_HREF + "/api/components/" + componentId3,
                migratedComponentHttpResponse2, null);

        Mockito.when(bdComponentVersionApi.findComponentVersionV4(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(componentVersionResult1);
        Mockito.when(bdComponentApi.findComponentV4(COMPONENT_ID)).thenReturn(migratedComponentResult1);
        Mockito.when(bdComponentVersionApi.findComponentVersionV4(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(componentVersionResult2);
        Mockito.when(bdComponentApi.findComponentV4(componentId2)).thenReturn(migratedComponentResult2);

        Optional<BdComponentVersionHierarchy> result = componentFinder.findComponentVersionHierarchy(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3);

        Assert.assertFalse(result.isPresent(), "Hierarchy should not be present.");
    }

    // Component variant hierarchy

    @Test
    public void testFindComponentVariantHierarchyWithAbsentComponentVariant() {
        // Find component variant - absent response.
        HttpResponse<BdComponentVariant> componentVariantHttpResponse = new HttpResponse<>(404, Set.of(200, 404), null, null);
        HttpResult<BdComponentVariant> componentVariantResult = new HttpResult<>("GET", BASE_HREF + "/api/variants/" + COMPONENT_VARIANT_ID,
                componentVariantHttpResponse);

        Mockito.when(bdComponentVariantApi.findComponentVariantV4(COMPONENT_VARIANT_ID)).thenReturn(componentVariantResult);

        Optional<BdComponentVariantHierarchy> result = componentFinder.findComponentVariantHierarchy(COMPONENT_VARIANT_ID, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3);

        Assert.assertFalse(result.isPresent(), "Hierarchy should not be present.");
    }

    @Test
    public void testFindComponentVariantHierarchyWithAbsentComponentVersion() {
        // Find component variant - present response.
        // Find parent component version - absent response.
        ComponentVariant componentVariant = constructComponentVariant(COMPONENT_ID, COMPONENT_VERSION_ID, COMPONENT_VARIANT_ID, "1.0", "maven", "foo:foo:1.0");
        BdComponentVariant bdComponentVariant = new BdComponentVariant(componentVariant);
        HttpResponse<BdComponentVariant> componentVariantHttpResponse = new HttpResponse<>(200, Set.of(200, 404), bdComponentVariant, null);
        HttpResult<BdComponentVariant> componentVariantResult = new HttpResult<>("GET", BASE_HREF + "/api/variants/" + COMPONENT_VARIANT_ID,
                componentVariantHttpResponse);

        MigratableHttpResponse<BdComponentVersion> componentVersionHttpResponse = new MigratableHttpResponse<>(404, Set.of(200, 402, 403, 404),
                null, null, null);
        MigratableHttpResult<BdComponentVersion> componentVersionResult = new MigratableHttpResult<>("GET", BASE_HREF + "/api/versions/" + COMPONENT_VERSION_ID,
                componentVersionHttpResponse, null);

        Mockito.when(bdComponentVariantApi.findComponentVariantV4(COMPONENT_VARIANT_ID)).thenReturn(componentVariantResult);
        Mockito.when(bdComponentVersionApi.findComponentVersionV4(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(componentVersionResult);

        Optional<BdComponentVariantHierarchy> result = componentFinder.findComponentVariantHierarchy(COMPONENT_VARIANT_ID, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3);

        Assert.assertFalse(result.isPresent(), "Hierarchy should not be present.");
    }

    @Test
    public void testFindComponentVariantHierarchyWithMigratedComponentVersion() {
        // Find component variant - present response (round 1).
        // Find parent component version - migrated response (round 1).
        // Find component variant - present response (round 2).
        // Find parent component version - present response (round 2).
        ComponentVariant componentVariant1 = constructComponentVariant(COMPONENT_ID, COMPONENT_VERSION_ID, COMPONENT_VARIANT_ID, "1.0", "maven", "foo:foo:1.0");
        BdComponentVariant bdComponentVariant1 = new BdComponentVariant(componentVariant1);
        HttpResponse<BdComponentVariant> componentVariantHttpResponse1 = new HttpResponse<>(200, Set.of(200, 404), bdComponentVariant1, null);
        HttpResult<BdComponentVariant> componentVariantResult1 = new HttpResult<>("GET", BASE_HREF + "/api/variants/" + COMPONENT_VARIANT_ID,
                componentVariantHttpResponse1);

        UUID componentId2 = UUID.randomUUID();
        UUID componentVersionId2 = UUID.randomUUID();
        ComponentVersion migratedComponentVersion = constructComponentVersion(componentId2, componentVersionId2, "1.0");
        BdComponentVersion migratedBdComponentVersion = new BdComponentVersion(migratedComponentVersion, BASE_HREF);
        List<Meta> migratedComponentVersionMetaHistory = List.of(new Meta(BASE_HREF + "/api/versions/" + COMPONENT_VERSION_ID, Collections.emptyList()));
        MigratableHttpResponse<BdComponentVersion> migratedComponentVersionHttpResponse = new MigratableHttpResponse<>(200, Set.of(200, 402, 403, 404),
                migratedBdComponentVersion, null, migratedComponentVersionMetaHistory);
        MigratableHttpResult<BdComponentVersion> migratedComponentVersionResult = new MigratableHttpResult<>("GET",
                BASE_HREF + "/api/versions/" + componentVersionId2,
                migratedComponentVersionHttpResponse, null);

        ComponentVariant componentVariant2 = constructComponentVariant(componentId2, componentVersionId2, COMPONENT_VARIANT_ID, "1.0", "maven", "foo:foo:1.0");
        BdComponentVariant bdComponentVariant2 = new BdComponentVariant(componentVariant2);
        HttpResponse<BdComponentVariant> componentVariantHttpResponse2 = new HttpResponse<>(200, Set.of(200, 404), bdComponentVariant2, null);
        HttpResult<BdComponentVariant> componentVariantResult2 = new HttpResult<>("GET", BASE_HREF + "/api/variants/" + COMPONENT_VARIANT_ID,
                componentVariantHttpResponse2);

        Component component2 = constructComponent(componentId2, "MigratedComponent");
        MigratableHttpResponse<Component> componentHttpResponse2 = new MigratableHttpResponse<>(200, Set.of(200, 404), component2, null, null);
        MigratableHttpResult<Component> componentResult2 = new MigratableHttpResult<>("GET", BASE_HREF + "/api/components/" + componentId2,
                componentHttpResponse2, null);

        Mockito.when(bdComponentVariantApi.findComponentVariantV4(COMPONENT_VARIANT_ID)).thenReturn(componentVariantResult1);
        Mockito.when(bdComponentVersionApi.findComponentVersionV4(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(migratedComponentVersionResult);
        Mockito.when(bdComponentVariantApi.findComponentVariantV4(COMPONENT_VARIANT_ID)).thenReturn(componentVariantResult2);
        Mockito.when(bdComponentVersionApi.findComponentVersionV4(componentVersionId2, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(migratedComponentVersionResult);
        Mockito.when(bdComponentApi.findComponentV4(componentId2)).thenReturn(componentResult2);

        Optional<BdComponentVariantHierarchy> result = componentFinder.findComponentVariantHierarchy(COMPONENT_VARIANT_ID, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3);

        Assert.assertTrue(result.isPresent(), "Hierarchy should be present.");
    }

    @Test
    public void testFindComponentVariantHierarchyWithAbsentComponent() {
        // Find component variant - present response.
        // Find parent component version - present response.
        // Find grandparent component - absent response.
        ComponentVariant componentVariant = constructComponentVariant(COMPONENT_ID, COMPONENT_VERSION_ID, COMPONENT_VARIANT_ID, "1.0", "maven", "foo:foo:1.0");
        BdComponentVariant bdComponentVariant = new BdComponentVariant(componentVariant);
        HttpResponse<BdComponentVariant> componentVariantHttpResponse = new HttpResponse<>(200, Set.of(200, 404), bdComponentVariant, null);
        HttpResult<BdComponentVariant> componentVariantResult = new HttpResult<>("GET", BASE_HREF + "/api/variants/" + COMPONENT_VARIANT_ID,
                componentVariantHttpResponse);

        ComponentVersion componentVersion = constructComponentVersion(COMPONENT_ID, COMPONENT_VERSION_ID, "1.0");
        BdComponentVersion bdComponentVersion = new BdComponentVersion(componentVersion, BASE_HREF);
        MigratableHttpResponse<BdComponentVersion> componentVersionHttpResponse = new MigratableHttpResponse<>(200, Set.of(200, 402, 403, 404),
                bdComponentVersion, null, null);
        MigratableHttpResult<BdComponentVersion> componentVersionResult = new MigratableHttpResult<>("GET", BASE_HREF + "/api/versions/" + COMPONENT_VERSION_ID,
                componentVersionHttpResponse, null);

        MigratableHttpResponse<Component> componentHttpResponse = new MigratableHttpResponse<>(404, Set.of(200, 404), null, null, null);
        MigratableHttpResult<Component> componentResult = new MigratableHttpResult<>("GET", BASE_HREF + "/api/components/" + COMPONENT_ID,
                componentHttpResponse, null);

        Mockito.when(bdComponentVariantApi.findComponentVariantV4(COMPONENT_VARIANT_ID)).thenReturn(componentVariantResult);
        Mockito.when(bdComponentVersionApi.findComponentVersionV4(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(componentVersionResult);
        Mockito.when(bdComponentApi.findComponentV4(COMPONENT_ID)).thenReturn(componentResult);

        Optional<BdComponentVariantHierarchy> result = componentFinder.findComponentVariantHierarchy(COMPONENT_VARIANT_ID, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3);

        Assert.assertFalse(result.isPresent(), "Hierarchy should not be present.");
    }

    @Test
    public void testFindComponentVariantHierarchyWithMigratedComponent() {
        // Find component variant - present response (round 1).
        // Find parent component version - present response (round 1).
        // Find grandparent component - migrated response (round 1).
        // Find component variant - present response (round 2).
        // Find parent component version - present response (round 2).
        // Find grandparent component - present response (round 2).
        ComponentVariant componentVariant1 = constructComponentVariant(COMPONENT_ID, COMPONENT_VERSION_ID, COMPONENT_VARIANT_ID, "1.0", "maven", "foo:foo:1.0");
        BdComponentVariant bdComponentVariant1 = new BdComponentVariant(componentVariant1);
        HttpResponse<BdComponentVariant> componentVariantHttpResponse1 = new HttpResponse<>(200, Set.of(200, 404), bdComponentVariant1, null);
        HttpResult<BdComponentVariant> componentVariantResult1 = new HttpResult<>("GET", BASE_HREF + "/api/variants/" + COMPONENT_VARIANT_ID,
                componentVariantHttpResponse1);

        ComponentVersion componentVersion1 = constructComponentVersion(COMPONENT_ID, COMPONENT_VERSION_ID, "1.0");
        BdComponentVersion bdComponentVersion1 = new BdComponentVersion(componentVersion1, BASE_HREF);
        MigratableHttpResponse<BdComponentVersion> componentVersionHttpResponse1 = new MigratableHttpResponse<>(200, Set.of(200, 402, 403, 404),
                bdComponentVersion1, null, null);
        MigratableHttpResult<BdComponentVersion> componentVersionResult1 = new MigratableHttpResult<>("GET",
                BASE_HREF + "/api/versions/" + COMPONENT_VERSION_ID,
                componentVersionHttpResponse1, null);

        UUID componentId2 = UUID.randomUUID();
        Component migratedComponent = constructComponent(componentId2, "MigratedComponent");
        List<Meta> migratedComponentMetaHistory = List.of(new Meta(BASE_HREF + "/api/components/" + COMPONENT_ID, Collections.emptyList()));
        MigratableHttpResponse<Component> migratedComponentHttpResponse = new MigratableHttpResponse<>(200, Set.of(200, 404), migratedComponent, null,
                migratedComponentMetaHistory);
        MigratableHttpResult<Component> migratedComponentResult = new MigratableHttpResult<>("GET", BASE_HREF + "/api/components/" + componentId2,
                migratedComponentHttpResponse, null);

        UUID componentVersionId2 = UUID.randomUUID();
        ComponentVariant componentVariant2 = constructComponentVariant(componentId2, componentVersionId2, COMPONENT_VARIANT_ID, "1.0", "maven", "foo:foo:1.0");
        BdComponentVariant bdComponentVariant2 = new BdComponentVariant(componentVariant2);
        HttpResponse<BdComponentVariant> componentVariantHttpResponse2 = new HttpResponse<>(200, Set.of(200, 404), bdComponentVariant2, null);
        HttpResult<BdComponentVariant> componentVariantResult2 = new HttpResult<>("GET", BASE_HREF + "/api/variants/" + COMPONENT_VARIANT_ID,
                componentVariantHttpResponse2);

        ComponentVersion componentVersion2 = constructComponentVersion(componentId2, componentVersionId2, "1.0");
        BdComponentVersion bdComponentVersion2 = new BdComponentVersion(componentVersion2, BASE_HREF);
        MigratableHttpResponse<BdComponentVersion> componentVersionHttpResponse2 = new MigratableHttpResponse<>(200, Set.of(200, 402, 403, 404),
                bdComponentVersion2, null, null);
        MigratableHttpResult<BdComponentVersion> componentVersionResult2 = new MigratableHttpResult<>("GET", BASE_HREF + "/api/versions/" + componentVersionId2,
                componentVersionHttpResponse2, null);

        Mockito.when(bdComponentVariantApi.findComponentVariantV4(COMPONENT_VARIANT_ID)).thenReturn(componentVariantResult1);
        Mockito.when(bdComponentVersionApi.findComponentVersionV4(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(componentVersionResult1);
        Mockito.when(bdComponentApi.findComponentV4(COMPONENT_ID)).thenReturn(migratedComponentResult);
        Mockito.when(bdComponentVariantApi.findComponentVariantV4(COMPONENT_VARIANT_ID)).thenReturn(componentVariantResult2);
        Mockito.when(bdComponentVersionApi.findComponentVersionV4(componentVersionId2, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(componentVersionResult2);
        Mockito.when(bdComponentApi.findComponentV4(componentId2)).thenReturn(migratedComponentResult);

        Optional<BdComponentVariantHierarchy> result = componentFinder.findComponentVariantHierarchy(COMPONENT_VARIANT_ID, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3);

        Assert.assertTrue(result.isPresent(), "Hierarchy should be present.");
    }

    @Test
    public void testFindComponentVariantHierarchy() {
        // Find component variant - present response.
        // Find parent component version - present response.
        // Find grandparent component - present response.
        ComponentVariant componentVariant = constructComponentVariant(COMPONENT_ID, COMPONENT_VERSION_ID, COMPONENT_VARIANT_ID, "1.0", "maven", "foo:foo:1.0");
        BdComponentVariant bdComponentVariant = new BdComponentVariant(componentVariant);
        HttpResponse<BdComponentVariant> componentVariantHttpResponse = new HttpResponse<>(200, Set.of(200, 404), bdComponentVariant, null);
        HttpResult<BdComponentVariant> componentVariantResult = new HttpResult<>("GET", BASE_HREF + "/api/variants/" + COMPONENT_VARIANT_ID,
                componentVariantHttpResponse);

        ComponentVersion componentVersion = constructComponentVersion(COMPONENT_ID, COMPONENT_VERSION_ID, "1.0");
        BdComponentVersion bdComponentVersion = new BdComponentVersion(componentVersion, BASE_HREF);
        MigratableHttpResponse<BdComponentVersion> componentVersionHttpResponse = new MigratableHttpResponse<>(200, Set.of(200, 402, 403, 404),
                bdComponentVersion, null, null);
        MigratableHttpResult<BdComponentVersion> componentVersionResult = new MigratableHttpResult<>("GET", BASE_HREF + "/api/versions/" + COMPONENT_VERSION_ID,
                componentVersionHttpResponse, null);

        Component component = constructComponent(COMPONENT_ID, "MigratedComponent");
        MigratableHttpResponse<Component> componentHttpResponse = new MigratableHttpResponse<>(200, Set.of(200, 404), component, null, null);
        MigratableHttpResult<Component> componentResult = new MigratableHttpResult<>("GET", BASE_HREF + "/api/components/" + COMPONENT_ID,
                componentHttpResponse, null);

        Mockito.when(bdComponentVariantApi.findComponentVariantV4(COMPONENT_VARIANT_ID)).thenReturn(componentVariantResult);
        Mockito.when(bdComponentVersionApi.findComponentVersionV4(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(componentVersionResult);
        Mockito.when(bdComponentApi.findComponentV4(COMPONENT_ID)).thenReturn(componentResult);

        Optional<BdComponentVariantHierarchy> result = componentFinder.findComponentVariantHierarchy(COMPONENT_VARIANT_ID, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3);

        Assert.assertTrue(result.isPresent(), "Hierarchy should be present.");
    }

    @Test
    public void testFindComponentVariantHierarchyWhenExhausted() {
        // Find component variant - present response (round 1).
        // Find parent component version - present response (round 1).
        // Find grandparent component - migrated response (round 1).
        // Find component variant - present response (round 2).
        // Find parent component version - present response (round 2).
        // Find grandparent component - migrated response (round 2).
        ComponentVariant componentVariant1 = constructComponentVariant(COMPONENT_ID, COMPONENT_VERSION_ID, COMPONENT_VARIANT_ID, "1.0", "maven", "foo:foo:1.0");
        BdComponentVariant bdComponentVariant1 = new BdComponentVariant(componentVariant1);
        HttpResponse<BdComponentVariant> componentVariantHttpResponse1 = new HttpResponse<>(200, Set.of(200, 404), bdComponentVariant1, null);
        HttpResult<BdComponentVariant> componentVariantResult1 = new HttpResult<>("GET", BASE_HREF + "/api/variants/" + COMPONENT_VARIANT_ID,
                componentVariantHttpResponse1);

        ComponentVersion componentVersion1 = constructComponentVersion(COMPONENT_ID, COMPONENT_VERSION_ID, "1.0");
        BdComponentVersion bdComponentVersion1 = new BdComponentVersion(componentVersion1, BASE_HREF);
        MigratableHttpResponse<BdComponentVersion> componentVersionHttpResponse1 = new MigratableHttpResponse<>(200, Set.of(200, 402, 403, 404),
                bdComponentVersion1, null, null);
        MigratableHttpResult<BdComponentVersion> componentVersionResult1 = new MigratableHttpResult<>("GET",
                BASE_HREF + "/api/versions/" + COMPONENT_VERSION_ID,
                componentVersionHttpResponse1, null);

        UUID componentId2 = UUID.randomUUID();
        Component migratedComponent1 = constructComponent(componentId2, "MigratedComponent");
        List<Meta> migratedComponentMetaHistory1 = List.of(new Meta(BASE_HREF + "/api/components/" + COMPONENT_ID, Collections.emptyList()));
        MigratableHttpResponse<Component> migratedComponentHttpResponse1 = new MigratableHttpResponse<>(200, Set.of(200, 404), migratedComponent1, null,
                migratedComponentMetaHistory1);
        MigratableHttpResult<Component> migratedComponentResult1 = new MigratableHttpResult<>("GET", BASE_HREF + "/api/components/" + componentId2,
                migratedComponentHttpResponse1, null);

        UUID componentVersionId2 = UUID.randomUUID();
        ComponentVariant componentVariant2 = constructComponentVariant(componentId2, componentVersionId2, COMPONENT_VARIANT_ID, "1.0", "maven", "foo:foo:1.0");
        BdComponentVariant bdComponentVariant2 = new BdComponentVariant(componentVariant2);
        HttpResponse<BdComponentVariant> componentVariantHttpResponse2 = new HttpResponse<>(200, Set.of(200, 404), bdComponentVariant2, null);
        HttpResult<BdComponentVariant> componentVariantResult2 = new HttpResult<>("GET", BASE_HREF + "/api/variants/" + COMPONENT_VARIANT_ID,
                componentVariantHttpResponse2);

        ComponentVersion componentVersion2 = constructComponentVersion(componentId2, componentVersionId2, "1.0");
        BdComponentVersion bdComponentVersion2 = new BdComponentVersion(componentVersion2, BASE_HREF);
        MigratableHttpResponse<BdComponentVersion> componentVersionHttpResponse2 = new MigratableHttpResponse<>(200, Set.of(200, 402, 403, 404),
                bdComponentVersion2, null, null);
        MigratableHttpResult<BdComponentVersion> componentVersionResult2 = new MigratableHttpResult<>("GET", BASE_HREF + "/api/versions/" + componentVersionId2,
                componentVersionHttpResponse2, null);

        UUID componentId3 = UUID.randomUUID();
        Component migratedComponent2 = constructComponent(componentId3, "MigratedComponent");
        List<Meta> migratedComponentMetaHistory2 = List.of(new Meta(BASE_HREF + "/api/components/" + componentId2, Collections.emptyList()));
        MigratableHttpResponse<Component> migratedComponentHttpResponse2 = new MigratableHttpResponse<>(200, Set.of(200, 404), migratedComponent2, null,
                migratedComponentMetaHistory2);
        MigratableHttpResult<Component> migratedComponentResult2 = new MigratableHttpResult<>("GET", BASE_HREF + "/api/components/" + componentId3,
                migratedComponentHttpResponse2, null);

        Mockito.when(bdComponentVariantApi.findComponentVariantV4(COMPONENT_VARIANT_ID)).thenReturn(componentVariantResult1);
        Mockito.when(bdComponentVersionApi.findComponentVersionV4(COMPONENT_VERSION_ID, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(componentVersionResult1);
        Mockito.when(bdComponentApi.findComponentV4(COMPONENT_ID)).thenReturn(migratedComponentResult1);
        Mockito.when(bdComponentVariantApi.findComponentVariantV4(COMPONENT_VARIANT_ID)).thenReturn(componentVariantResult2);
        Mockito.when(bdComponentVersionApi.findComponentVersionV4(componentVersionId2, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3))
                .thenReturn(componentVersionResult2);
        Mockito.when(bdComponentApi.findComponentV4(componentId2)).thenReturn(migratedComponentResult2);

        Optional<BdComponentVariantHierarchy> result = componentFinder.findComponentVariantHierarchy(COMPONENT_VARIANT_ID, VulnerabilitySourcePriority.BDSA,
                VulnerabilityScorePriority.CVSS_3);

        Assert.assertFalse(result.isPresent(), "Hierarchy should not be present.");
    }
}
