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
import com.synopsys.kb.httpclient.model.BdsaVulnerabilityActivity;
import com.synopsys.kb.httpclient.model.ComponentActivity;
import com.synopsys.kb.httpclient.model.ComponentVariantActivity;
import com.synopsys.kb.httpclient.model.ComponentVersionActivity;
import com.synopsys.kb.httpclient.model.CveVulnerabilityActivity;
import com.synopsys.kb.httpclient.model.LicenseActivity;
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
    public void testFindComponentActivities() {
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

    @Test(enabled = false)
    public void testFindComponentOngoingVersionActivities() {
        UUID componentId = UUID.fromString("9f74f66e-5c27-48d0-9083-e31e3563b7b2");
        Set<UUID> componentIds = Set.of(componentId);

        // Assign to far past date for results.
        OffsetDateTime activitySince = OffsetDateTime.now().minusYears(20L);

        Result<ListHolder<ComponentActivity>> result = activityApi.findComponentOngoingVersionActivities(componentIds, activitySince);

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

    @Test(enabled = false)
    public void testFindComponentVersionActivities() {
        UUID componentVersionId1 = UUID.fromString("0bca7263-1033-4731-9985-809eb87ecfb7");
        Set<UUID> componentVersionIds = Set.of(componentVersionId1);

        // Assign to far past date for results.
        OffsetDateTime activitySince = OffsetDateTime.now().minusYears(20L);

        Result<ListHolder<ComponentVersionActivity>> result = activityApi.findComponentVersionActivities(componentVersionIds, activitySince);

        HttpResponse<ListHolder<ComponentVersionActivity>> httpResponse = result.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        ListHolder<ComponentVersionActivity> listHolder = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(listHolder, "List holder should be initialized.");
        List<ComponentVersionActivity> componentVersionActivities = listHolder.getItems();
        Assert.assertNotNull(componentVersionActivities, "Component version activities should be initialized.");
        Assert.assertEquals(componentVersionActivities.size(), componentVersionIds.size(), "Number of component version activities should be equal.");
        Set<UUID> actualComponentVersionIds = componentVersionActivities.stream().map(ComponentVersionActivity::getComponentVersionId)
                .collect(Collectors.toSet());
        Assert.assertEquals(actualComponentVersionIds.size(), componentVersionIds.size(), "Number of component version activities should be equal.");
        Assert.assertTrue(actualComponentVersionIds.containsAll(componentVersionIds), "Component version ids should be present.");
    }

    @Test(enabled = false)
    public void testFindComponentVersionLicenseActivities() {
        UUID componentVersionId1 = UUID.fromString("0bca7263-1033-4731-9985-809eb87ecfb7");
        Set<UUID> componentVersionIds = Set.of(componentVersionId1);

        // Assign to far past date for results.
        OffsetDateTime activitySince = OffsetDateTime.now().minusYears(20L);

        Result<ListHolder<ComponentVersionActivity>> result = activityApi.findComponentVersionLicenseActivities(componentVersionIds, activitySince);

        HttpResponse<ListHolder<ComponentVersionActivity>> httpResponse = result.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        ListHolder<ComponentVersionActivity> listHolder = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(listHolder, "List holder should be initialized.");
        List<ComponentVersionActivity> componentVersionActivities = listHolder.getItems();
        Assert.assertNotNull(componentVersionActivities, "Component version license activities should be initialized.");
        Assert.assertEquals(componentVersionActivities.size(), componentVersionIds.size(), "Number of component version license activities should be equal.");
        Set<UUID> actualComponentVersionIds = componentVersionActivities.stream().map(ComponentVersionActivity::getComponentVersionId)
                .collect(Collectors.toSet());
        Assert.assertEquals(actualComponentVersionIds.size(), componentVersionIds.size(), "Number of component version license activities should be equal.");
        Assert.assertTrue(actualComponentVersionIds.containsAll(componentVersionIds), "Component version ids should be present.");
    }

    @Test(enabled = false)
    public void testFindComponentVersionCveVulnerabilityActivities() {
        UUID componentVersionId1 = UUID.fromString("0bca7263-1033-4731-9985-809eb87ecfb7");
        Set<UUID> componentVersionIds = Set.of(componentVersionId1);

        // Assign to far past date for results.
        OffsetDateTime activitySince = OffsetDateTime.now().minusYears(20L);

        Result<ListHolder<ComponentVersionActivity>> result = activityApi.findComponentVersionCveVulnerabilityActivities(componentVersionIds, activitySince);

        HttpResponse<ListHolder<ComponentVersionActivity>> httpResponse = result.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        ListHolder<ComponentVersionActivity> listHolder = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(listHolder, "List holder should be initialized.");
        List<ComponentVersionActivity> componentVersionActivities = listHolder.getItems();
        Assert.assertNotNull(componentVersionActivities, "Component version CVE vulnerability activities should be initialized.");
        Assert.assertEquals(componentVersionActivities.size(), componentVersionIds.size(),
                "Number of component version CVE vulnerability activities should be equal.");
        Set<UUID> actualComponentVersionIds = componentVersionActivities.stream().map(ComponentVersionActivity::getComponentVersionId)
                .collect(Collectors.toSet());
        Assert.assertEquals(actualComponentVersionIds.size(), componentVersionIds.size(),
                "Number of component version CVE vulnerability activities should be equal.");
        Assert.assertTrue(actualComponentVersionIds.containsAll(componentVersionIds), "Component version ids should be present.");
    }

    @Test(enabled = false)
    public void testFindComponentVersionBdsaVulnerabilityActivities() {
        UUID componentVersionId1 = UUID.fromString("c365269e-7378-4277-85dc-f84b217e2d89");
        Set<UUID> componentVersionIds = Set.of(componentVersionId1);

        // Assign to far past date for results.
        OffsetDateTime activitySince = OffsetDateTime.now().minusYears(20L);

        Result<ListHolder<ComponentVersionActivity>> result = activityApi.findComponentVersionBdsaVulnerabilityActivities(componentVersionIds, activitySince);

        HttpResponse<ListHolder<ComponentVersionActivity>> httpResponse = result.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        ListHolder<ComponentVersionActivity> listHolder = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(listHolder, "List holder should be initialized.");
        List<ComponentVersionActivity> componentVersionActivities = listHolder.getItems();
        Assert.assertNotNull(componentVersionActivities, "Component version BDSA vulnerability activities should be initialized.");
        Assert.assertEquals(componentVersionActivities.size(), componentVersionIds.size(),
                "Number of component version BDSA vulnerability activities should be equal.");
        Set<UUID> actualComponentVersionIds = componentVersionActivities.stream().map(ComponentVersionActivity::getComponentVersionId)
                .collect(Collectors.toSet());
        Assert.assertEquals(actualComponentVersionIds.size(), componentVersionIds.size(),
                "Number of component version BDSA vulnerability activities should be equal.");
        Assert.assertTrue(actualComponentVersionIds.containsAll(componentVersionIds), "Component version ids should be present.");
    }

    @Test(enabled = false)
    public void testFindComponentVersionUpgradeGuidanceActivities() {
        UUID componentVersionId1 = UUID.fromString("c365269e-7378-4277-85dc-f84b217e2d89");
        Set<UUID> componentVersionIds = Set.of(componentVersionId1);

        // Assign to far past date for results.
        OffsetDateTime activitySince = OffsetDateTime.now().minusYears(20L);

        Result<ListHolder<ComponentVersionActivity>> result = activityApi.findComponentVersionUpgradeGuidanceActivities(componentVersionIds, activitySince);

        HttpResponse<ListHolder<ComponentVersionActivity>> httpResponse = result.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        ListHolder<ComponentVersionActivity> listHolder = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(listHolder, "List holder should be initialized.");
        List<ComponentVersionActivity> componentVersionActivities = listHolder.getItems();
        Assert.assertNotNull(componentVersionActivities, "Component version upgrade guidance activities should be initialized.");
        Assert.assertEquals(componentVersionActivities.size(), componentVersionIds.size(),
                "Number of component version upgrade guidance activities should be equal.");
        Set<UUID> actualComponentVersionIds = componentVersionActivities.stream().map(ComponentVersionActivity::getComponentVersionId)
                .collect(Collectors.toSet());
        Assert.assertEquals(actualComponentVersionIds.size(), componentVersionIds.size(),
                "Number of component version upgrade guidance activities should be equal.");
        Assert.assertTrue(actualComponentVersionIds.containsAll(componentVersionIds), "Component version ids should be present.");
    }

    @Test(enabled = false)
    public void testFindComponentVariantActivities() {
        UUID componentVariantId1 = UUID.fromString("64b5b35d-0cfa-4a0e-b0aa-90db4dcf2734");
        Set<UUID> componentVariantIds = Set.of(componentVariantId1);

        // Assign to far past date for results.
        OffsetDateTime activitySince = OffsetDateTime.now().minusYears(20L);

        Result<ListHolder<ComponentVariantActivity>> result = activityApi.findComponentVariantActivities(componentVariantIds, activitySince);

        HttpResponse<ListHolder<ComponentVariantActivity>> httpResponse = result.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        ListHolder<ComponentVariantActivity> listHolder = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(listHolder, "List holder should be initialized.");
        List<ComponentVariantActivity> componentVariantActivities = listHolder.getItems();
        Assert.assertNotNull(componentVariantActivities, "Component variant activities should be initialized.");
        Assert.assertEquals(componentVariantActivities.size(), componentVariantIds.size(), "Number of component variant activities should be equal.");
        Set<UUID> actualComponentVariantIds = componentVariantActivities.stream().map(ComponentVariantActivity::getComponentVariantId)
                .collect(Collectors.toSet());
        Assert.assertEquals(actualComponentVariantIds.size(), componentVariantIds.size(), "Number of component variant activities should be equal.");
        Assert.assertTrue(actualComponentVariantIds.containsAll(componentVariantIds), "Component variant ids should be present.");
    }

    @Test(enabled = false)
    public void testFindComponentVariantCveVulnerabilityActivities() {
        UUID componentVariantId1 = UUID.fromString("0186d8d0-a29c-4c51-a37c-c03d76d4effa");
        Set<UUID> componentVariantIds = Set.of(componentVariantId1);

        // Assign to far past date for results.
        OffsetDateTime activitySince = OffsetDateTime.now().minusYears(20L);

        Result<ListHolder<ComponentVariantActivity>> result = activityApi.findComponentVariantCveVulnerabilityActivities(componentVariantIds, activitySince);

        HttpResponse<ListHolder<ComponentVariantActivity>> httpResponse = result.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        ListHolder<ComponentVariantActivity> listHolder = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(listHolder, "List holder should be initialized.");
        List<ComponentVariantActivity> componentVariantActivities = listHolder.getItems();
        Assert.assertNotNull(componentVariantActivities, "Component variant CVE vulnerability activities should be initialized.");
        Assert.assertEquals(componentVariantActivities.size(), componentVariantIds.size(),
                "Number of component variant CVE vulnerability activities should be equal.");
        Set<UUID> actualComponentVariantIds = componentVariantActivities.stream().map(ComponentVariantActivity::getComponentVariantId)
                .collect(Collectors.toSet());
        Assert.assertEquals(actualComponentVariantIds.size(), componentVariantIds.size(),
                "Number of component variant CVE vulnerability activities should be equal.");
        Assert.assertTrue(actualComponentVariantIds.containsAll(componentVariantIds), "Component variant ids should be present.");
    }

    @Test(enabled = false)
    public void testFindComponentVariantBdsaVulnerabilityActivities() {
        UUID componentVariantId1 = UUID.fromString("a8254896-a7a5-4495-a6eb-a8977d67639d");
        Set<UUID> componentVariantIds = Set.of(componentVariantId1);

        // Assign to far past date for results.
        OffsetDateTime activitySince = OffsetDateTime.now().minusYears(20L);

        Result<ListHolder<ComponentVariantActivity>> result = activityApi.findComponentVariantBdsaVulnerabilityActivities(componentVariantIds, activitySince);

        HttpResponse<ListHolder<ComponentVariantActivity>> httpResponse = result.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        ListHolder<ComponentVariantActivity> listHolder = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(listHolder, "List holder should be initialized.");
        List<ComponentVariantActivity> componentVariantActivities = listHolder.getItems();
        Assert.assertNotNull(componentVariantActivities, "Component variant BDSA vulnerability activities should be initialized.");
        Assert.assertEquals(componentVariantActivities.size(), componentVariantIds.size(),
                "Number of component variant BDSA vulnerability activities should be equal.");
        Set<UUID> actualComponentVariantIds = componentVariantActivities.stream().map(ComponentVariantActivity::getComponentVariantId)
                .collect(Collectors.toSet());
        Assert.assertEquals(actualComponentVariantIds.size(), componentVariantIds.size(),
                "Number of component variant BDSA vulnerability activities should be equal.");
        Assert.assertTrue(actualComponentVariantIds.containsAll(componentVariantIds), "Component variant ids should be present.");
    }

    @Test(enabled = false)
    public void testFindComponentVariantUpgradeGuidanceActivities() {
        UUID componentVariantId1 = UUID.fromString("0186d8d0-a29c-4c51-a37c-c03d76d4effa");
        Set<UUID> componentVariantIds = Set.of(componentVariantId1);

        // Assign to far past date for results.
        OffsetDateTime activitySince = OffsetDateTime.now().minusYears(20L);

        Result<ListHolder<ComponentVariantActivity>> result = activityApi.findComponentVariantUpgradeGuidanceActivities(componentVariantIds, activitySince);

        HttpResponse<ListHolder<ComponentVariantActivity>> httpResponse = result.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        ListHolder<ComponentVariantActivity> listHolder = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(listHolder, "List holder should be initialized.");
        List<ComponentVariantActivity> componentVariantActivities = listHolder.getItems();
        Assert.assertNotNull(componentVariantActivities, "Component variant upgrade guidance activities should be initialized.");
        Assert.assertEquals(componentVariantActivities.size(), componentVariantIds.size(),
                "Number of component variant upgrade guidance activities should be equal.");
        Set<UUID> actualComponentVariantIds = componentVariantActivities.stream().map(ComponentVariantActivity::getComponentVariantId)
                .collect(Collectors.toSet());
        Assert.assertEquals(actualComponentVariantIds.size(), componentVariantIds.size(),
                "Number of component variant upgrade guidance activities should be equal.");
        Assert.assertTrue(actualComponentVariantIds.containsAll(componentVariantIds), "Component variant ids should be present.");
    }

    @Test(enabled = false)
    public void testFindComponentVariantTransitiveUpgradeGuidanceActivities() {
        UUID componentVariantId1 = UUID.fromString("6fe9d2a3-71cc-4157-a5ef-373f7b93f406");
        Set<UUID> componentVariantIds = Set.of(componentVariantId1);

        // Assign to far past date for results.
        OffsetDateTime activitySince = OffsetDateTime.now().minusYears(20L);

        Result<ListHolder<ComponentVariantActivity>> result = activityApi.findComponentVariantTransitiveUpgradeGuidanceActivities(componentVariantIds,
                activitySince);

        HttpResponse<ListHolder<ComponentVariantActivity>> httpResponse = result.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        ListHolder<ComponentVariantActivity> listHolder = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(listHolder, "List holder should be initialized.");
        List<ComponentVariantActivity> componentVariantActivities = listHolder.getItems();
        Assert.assertNotNull(componentVariantActivities, "Component variant transitive upgrade guidance activities should be initialized.");
        Assert.assertEquals(componentVariantActivities.size(), componentVariantIds.size(),
                "Number of component variant transitive upgrade guidance activities should be equal.");
        Set<UUID> actualComponentVariantIds = componentVariantActivities.stream().map(ComponentVariantActivity::getComponentVariantId)
                .collect(Collectors.toSet());
        Assert.assertEquals(actualComponentVariantIds.size(), componentVariantIds.size(),
                "Number of component variant transitive upgrade guidance activities should be equal.");
        Assert.assertTrue(actualComponentVariantIds.containsAll(componentVariantIds), "Component variant ids should be present.");
    }

    @Test(enabled = false)
    public void testFindLicenseActivities() {
        UUID licenseId1 = UUID.fromString("7cae335f-1193-421e-92f1-8802b4243e93");
        Set<UUID> licenseIds = Set.of(licenseId1);

        // Assign to far past date for results.
        OffsetDateTime activitySince = OffsetDateTime.now().minusYears(20L);

        Result<ListHolder<LicenseActivity>> result = activityApi.findLicenseActivities(licenseIds, activitySince);

        HttpResponse<ListHolder<LicenseActivity>> httpResponse = result.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        ListHolder<LicenseActivity> listHolder = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(listHolder, "List holder should be initialized.");
        List<LicenseActivity> licenseActivities = listHolder.getItems();
        Assert.assertNotNull(licenseActivities, "License activities should be initialized.");
        Assert.assertEquals(licenseActivities.size(), licenseIds.size(), "Number of license activities should be equal.");
        Set<UUID> actualLicenseIds = licenseActivities.stream().map(LicenseActivity::getLicenseId).collect(Collectors.toSet());
        Assert.assertEquals(actualLicenseIds.size(), licenseIds.size(), "Number of license activities should be equal.");
        Assert.assertTrue(actualLicenseIds.containsAll(licenseIds), "License ids should be present.");
    }

    @Test(enabled = false)
    public void testFindLicenseLicenseTermActivities() {
        UUID licenseId1 = UUID.fromString("7cae335f-1193-421e-92f1-8802b4243e93");
        Set<UUID> licenseIds = Set.of(licenseId1);

        // Assign to far past date for results.
        OffsetDateTime activitySince = OffsetDateTime.now().minusYears(20L);

        Result<ListHolder<LicenseActivity>> result = activityApi.findLicenseLicenseTermActivities(licenseIds, activitySince);

        HttpResponse<ListHolder<LicenseActivity>> httpResponse = result.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        ListHolder<LicenseActivity> listHolder = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(listHolder, "List holder should be initialized.");
        List<LicenseActivity> licenseActivities = listHolder.getItems();
        Assert.assertNotNull(licenseActivities, "License activities should be initialized.");
        Assert.assertEquals(licenseActivities.size(), licenseIds.size(), "Number of license activities should be equal.");
        Set<UUID> actualLicenseIds = licenseActivities.stream().map(LicenseActivity::getLicenseId).collect(Collectors.toSet());
        Assert.assertEquals(actualLicenseIds.size(), licenseIds.size(), "Number of license activities should be equal.");
        Assert.assertTrue(actualLicenseIds.containsAll(licenseIds), "License ids should be present.");
    }

    @Test(enabled = false)
    public void testFindCveVulnerabilityActivities() {
        String cveVulnerabilityId1 = "CVE-2014-0160";
        Set<String> cveVulnerabilityIds = Set.of(cveVulnerabilityId1);

        // Assign to far past date for results.
        OffsetDateTime activitySince = OffsetDateTime.now().minusYears(20L);

        Result<ListHolder<CveVulnerabilityActivity>> result = activityApi.findCveVulnerabilityActivities(cveVulnerabilityIds, activitySince);

        HttpResponse<ListHolder<CveVulnerabilityActivity>> httpResponse = result.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        ListHolder<CveVulnerabilityActivity> listHolder = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(listHolder, "List holder should be initialized.");
        List<CveVulnerabilityActivity> cveVulnerabilityActivities = listHolder.getItems();
        Assert.assertNotNull(cveVulnerabilityActivities, "CVE vulnerability activities should be initialized.");
        Assert.assertEquals(cveVulnerabilityActivities.size(), cveVulnerabilityIds.size(), "Number of CVE vulnerability activities should be equal.");
        Set<String> actualCveVulnerabilityIds = cveVulnerabilityActivities.stream().map(CveVulnerabilityActivity::getCveVulnerabilityId)
                .collect(Collectors.toSet());
        Assert.assertEquals(actualCveVulnerabilityIds.size(), cveVulnerabilityIds.size(), "Number of CVE vulnerability activities should be equal.");
        Assert.assertTrue(actualCveVulnerabilityIds.containsAll(cveVulnerabilityIds), "CVE vulnerability ids should be present.");
    }

    @Test(enabled = false)
    public void testFindBdsaVulnerabilityActivities() {
        String bdsaVulnerabilityId1 = "BDSA-2014-0028";
        Set<String> bdsaVulnerabilityIds = Set.of(bdsaVulnerabilityId1);

        // Assign to far past date for results.
        OffsetDateTime activitySince = OffsetDateTime.now().minusYears(20L);

        Result<ListHolder<BdsaVulnerabilityActivity>> result = activityApi.findBdsaVulnerabilityActivities(bdsaVulnerabilityIds, activitySince);

        HttpResponse<ListHolder<BdsaVulnerabilityActivity>> httpResponse = result.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        ListHolder<BdsaVulnerabilityActivity> listHolder = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(listHolder, "List holder should be initialized.");
        List<BdsaVulnerabilityActivity> bdsaVulnerabilityActivities = listHolder.getItems();
        Assert.assertNotNull(bdsaVulnerabilityActivities, "BDSA vulnerability activities should be initialized.");
        Assert.assertEquals(bdsaVulnerabilityActivities.size(), bdsaVulnerabilityIds.size(), "Number of BDSA vulnerability activities should be equal.");
        Set<String> actualBdsaVulnerabilityIds = bdsaVulnerabilityActivities.stream().map(BdsaVulnerabilityActivity::getBdsaVulnerabilityId)
                .collect(Collectors.toSet());
        Assert.assertEquals(actualBdsaVulnerabilityIds.size(), bdsaVulnerabilityIds.size(), "Number of BDSA vulnerability activities should be equal.");
        Assert.assertTrue(actualBdsaVulnerabilityIds.containsAll(bdsaVulnerabilityIds), "BDSA vulnerability ids should be present.");
    }
}
