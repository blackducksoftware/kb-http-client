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
import com.synopsys.kb.httpclient.api.IComponentVersionApi;
import com.synopsys.kb.httpclient.api.IKbHttpApi;
import com.synopsys.kb.httpclient.api.PageRequest;
import com.synopsys.kb.httpclient.model.BdsaVulnerability;
import com.synopsys.kb.httpclient.model.ComponentVersion;
import com.synopsys.kb.httpclient.model.CveVulnerability;
import com.synopsys.kb.httpclient.model.NextVersion;
import com.synopsys.kb.httpclient.model.Page;
import com.synopsys.kb.httpclient.model.UpgradeGuidance;
import com.synopsys.kb.httpclient.model.VulnerabilityScorePriority;
import com.synopsys.kb.httpclient.model.VulnerabilitySourcePriority;

/**
 * Component version HTTP client functional test.
 * 
 * @author skatzman
 */
public class KbComponentVersionHttpClientFuncTest extends AbstractFuncTest {
    private IComponentVersionApi componentVersionApi;

    @BeforeClass
    public void beforeClass() {
        IKbHttpApi kbHttpApi = getKbHttpApi();
        this.componentVersionApi = kbHttpApi.getComponentVersionApi();
    }

    @Test
    public void testFind() {
        UUID componentVersionId = UUID.fromString("d10c3ded-9e0e-468e-909e-637d81ecc554");

        HttpResult<ComponentVersion> httpResult = componentVersionApi.find(componentVersionId, VulnerabilitySourcePriority.CVE,
                VulnerabilityScorePriority.CVSS_2);

        HttpResponse<ComponentVersion> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");

        if (httpResponse.isMigrated()) {
            throw new SkipException("This functional test method was written using a non-migrated component version id.  "
                    + "Since then, this component version id has been migrated by the KnowledgeBase.  "
                    + "Update this test with a new non-migrated component version id.");
        }

        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        ComponentVersion componentVersion = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(componentVersion, "Component version should be initialized.");
    }

    @Test
    public void testFindWhenMergeMigrated() {
        UUID componentVersionId = UUID.fromString("0bca7263-1033-4731-9985-809eb87ecfb7");

        HttpResult<ComponentVersion> httpResult = componentVersionApi.find(componentVersionId, VulnerabilitySourcePriority.CVE,
                VulnerabilityScorePriority.CVSS_2);

        HttpResponse<ComponentVersion> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_MOVED_PERMANENTLY, "Codes should be equal.");
        Assert.assertFalse(httpResponse.isMessageBodyPresent(), "Message body should not be present.");
        Assert.assertTrue(httpResponse.isMigratedMetaPresent(), "Migrated meta should be present.");
        Assert.assertTrue(httpResponse.isMigrated(), "Entity should be migrated.");
        Assert.assertTrue(httpResponse.isMergeMigrated(), "Entity should be merge migrated.");
        Assert.assertFalse(httpResponse.isSplitMigrated(), "Entity should not be split migrated.");
    }

    @Test
    public void testFindWhenSplitMigrated() {
        UUID componentVersionId = UUID.fromString("1623f022-fb37-428a-a491-ab1ce1918d16");

        HttpResult<ComponentVersion> httpResult = componentVersionApi.find(componentVersionId, VulnerabilitySourcePriority.CVE,
                VulnerabilityScorePriority.CVSS_2);

        HttpResponse<ComponentVersion> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_MULTIPLE_CHOICES, "Codes should be equal.");
        Assert.assertFalse(httpResponse.isMessageBodyPresent(), "Message body should not be present.");
        Assert.assertTrue(httpResponse.isMigratedMetaPresent(), "Migrated meta should be present.");
        Assert.assertTrue(httpResponse.isMigrated(), "Entity should be migrated.");
        Assert.assertFalse(httpResponse.isMergeMigrated(), "Entity should not be merge migrated.");
        Assert.assertTrue(httpResponse.isSplitMigrated(), "Entity should be split migrated.");
    }

    @Test
    public void testFindNextVersion() {
        UUID componentVersionId = UUID.fromString("d10c3ded-9e0e-468e-909e-637d81ecc554");

        HttpResult<NextVersion> httpResult = componentVersionApi.findNextVersion(componentVersionId);

        HttpResponse<NextVersion> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");

        if (httpResponse.isMigrated()) {
            throw new SkipException("This functional test method was written using a non-migrated component version id.  "
                    + "Since then, this component version id has been migrated by the KnowledgeBase.  "
                    + "Update this test with a new non-migrated component version id.");
        }

        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        NextVersion nextVersion = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(nextVersion, "Next version should be initialized.");
    }

    @Test
    public void testFindNextVersionWhenMergeMigrated() {
        UUID componentVersionId = UUID.fromString("0bca7263-1033-4731-9985-809eb87ecfb7");

        HttpResult<NextVersion> httpResult = componentVersionApi.findNextVersion(componentVersionId);

        HttpResponse<NextVersion> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_MOVED_PERMANENTLY, "Codes should be equal.");
        Assert.assertFalse(httpResponse.isMessageBodyPresent(), "Message body should not be present.");
        Assert.assertTrue(httpResponse.isMigratedMetaPresent(), "Migrated meta should be present.");
        Assert.assertTrue(httpResponse.isMigrated(), "Entity should be migrated.");
        Assert.assertTrue(httpResponse.isMergeMigrated(), "Entity should be merge migrated.");
        Assert.assertFalse(httpResponse.isSplitMigrated(), "Entity should not be split migrated.");
    }

    @Test
    public void testFindNextVersionWhenSplitMigrated() {
        UUID componentVersionId = UUID.fromString("1623f022-fb37-428a-a491-ab1ce1918d16");

        HttpResult<NextVersion> httpResult = componentVersionApi.findNextVersion(componentVersionId);

        HttpResponse<NextVersion> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_MULTIPLE_CHOICES, "Codes should be equal.");
        Assert.assertFalse(httpResponse.isMessageBodyPresent(), "Message body should not be present.");
        Assert.assertTrue(httpResponse.isMigratedMetaPresent(), "Migrated meta should be present.");
        Assert.assertTrue(httpResponse.isMigrated(), "Entity should be migrated.");
        Assert.assertFalse(httpResponse.isMergeMigrated(), "Entity should not be merge migrated.");
        Assert.assertTrue(httpResponse.isSplitMigrated(), "Entity should be split migrated.");
    }

    @Test
    public void testFindCveVulnerabilities() {
        PageRequest pageRequest = new PageRequest(0, 100, Collections.emptyList());
        UUID componentVersionId = UUID.fromString("c365269e-7378-4277-85dc-f84b217e2d89");

        HttpResult<Page<CveVulnerability>> httpResult = componentVersionApi.findCveVulnerabilities(pageRequest, componentVersionId, null);

        HttpResponse<Page<CveVulnerability>> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");

        if (httpResponse.isMigrated()) {
            throw new SkipException("This functional test method was written using a non-migrated component version id.  "
                    + "Since then, this component version id has been migrated by the KnowledgeBase.  "
                    + "Update this test with a new non-migrated component version id.");
        }

        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        Page<CveVulnerability> page = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(page, "Page should be initialized.");
        List<CveVulnerability> items = page.getItems();
        Assert.assertNotNull(items, "Items should be initialized.");
        Assert.assertFalse(items.isEmpty(), "Items should not be empty.");
    }

    @Test
    public void testFindCveVulnerabilitiesWhenMergeMigrated() {
        PageRequest pageRequest = new PageRequest(0, 100, Collections.emptyList());
        UUID componentVersionId = UUID.fromString("0bca7263-1033-4731-9985-809eb87ecfb7");

        HttpResult<Page<CveVulnerability>> httpResult = componentVersionApi.findCveVulnerabilities(pageRequest, componentVersionId, null);

        HttpResponse<Page<CveVulnerability>> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_MOVED_PERMANENTLY, "Codes should be equal.");
        Assert.assertFalse(httpResponse.isMessageBodyPresent(), "Message body should not be present.");
        Assert.assertTrue(httpResponse.isMigratedMetaPresent(), "Migrated meta should be present.");
        Assert.assertTrue(httpResponse.isMigrated(), "Entity should be migrated.");
        Assert.assertTrue(httpResponse.isMergeMigrated(), "Entity should be merge migrated.");
        Assert.assertFalse(httpResponse.isSplitMigrated(), "Entity should not be split migrated.");
    }

    @Test
    public void testFindCveVulnerabilitiesWhenSplitMigrated() {
        PageRequest pageRequest = new PageRequest(0, 100, Collections.emptyList());
        UUID componentVersionId = UUID.fromString("1623f022-fb37-428a-a491-ab1ce1918d16");

        HttpResult<Page<CveVulnerability>> httpResult = componentVersionApi.findCveVulnerabilities(pageRequest, componentVersionId, null);

        HttpResponse<Page<CveVulnerability>> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_MULTIPLE_CHOICES, "Codes should be equal.");
        Assert.assertFalse(httpResponse.isMessageBodyPresent(), "Message body should not be present.");
        Assert.assertTrue(httpResponse.isMigratedMetaPresent(), "Migrated meta should be present.");
        Assert.assertTrue(httpResponse.isMigrated(), "Entity should be migrated.");
        Assert.assertFalse(httpResponse.isMergeMigrated(), "Entity should not be merge migrated.");
        Assert.assertTrue(httpResponse.isSplitMigrated(), "Entity should be split migrated.");
    }

    @Test
    public void testFindBdsaVulnerabilities() {
        PageRequest pageRequest = new PageRequest(0, 100, Collections.emptyList());
        UUID componentVersionId = UUID.fromString("c365269e-7378-4277-85dc-f84b217e2d89");

        HttpResult<Page<BdsaVulnerability>> httpResult = componentVersionApi.findBdsaVulnerabilities(pageRequest, componentVersionId, null);

        HttpResponse<Page<BdsaVulnerability>> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");

        if (httpResponse.isMigrated()) {
            throw new SkipException("This functional test method was written using a non-migrated component version id.  "
                    + "Since then, this component version id has been migrated by the KnowledgeBase.  "
                    + "Update this test with a new non-migrated component version id.");
        }

        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        Page<BdsaVulnerability> page = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(page, "Page should be initialized.");
        List<BdsaVulnerability> items = page.getItems();
        Assert.assertNotNull(items, "Items should be initialized.");
        Assert.assertFalse(items.isEmpty(), "Items should not be empty.");
    }

    @Test
    public void testFindBdsaVulnerabilitiesWhenMergeMigrated() {
        PageRequest pageRequest = new PageRequest(0, 100, Collections.emptyList());
        UUID componentVersionId = UUID.fromString("0bca7263-1033-4731-9985-809eb87ecfb7");

        HttpResult<Page<BdsaVulnerability>> httpResult = componentVersionApi.findBdsaVulnerabilities(pageRequest, componentVersionId, null);

        HttpResponse<Page<BdsaVulnerability>> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_MOVED_PERMANENTLY, "Codes should be equal.");
        Assert.assertFalse(httpResponse.isMessageBodyPresent(), "Message body should not be present.");
        Assert.assertTrue(httpResponse.isMigratedMetaPresent(), "Migrated meta should be present.");
        Assert.assertTrue(httpResponse.isMigrated(), "Entity should be migrated.");
        Assert.assertTrue(httpResponse.isMergeMigrated(), "Entity should be merge migrated.");
        Assert.assertFalse(httpResponse.isSplitMigrated(), "Entity should not be split migrated.");
    }

    @Test
    public void testFindBdsaVulnerabilitiesWhenSplitMigrated() {
        PageRequest pageRequest = new PageRequest(0, 100, Collections.emptyList());
        UUID componentVersionId = UUID.fromString("1623f022-fb37-428a-a491-ab1ce1918d16");

        HttpResult<Page<BdsaVulnerability>> httpResult = componentVersionApi.findBdsaVulnerabilities(pageRequest, componentVersionId, null);

        HttpResponse<Page<BdsaVulnerability>> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_MULTIPLE_CHOICES, "Codes should be equal.");
        Assert.assertFalse(httpResponse.isMessageBodyPresent(), "Message body should not be present.");
        Assert.assertTrue(httpResponse.isMigratedMetaPresent(), "Migrated meta should be present.");
        Assert.assertTrue(httpResponse.isMigrated(), "Entity should be migrated.");
        Assert.assertFalse(httpResponse.isMergeMigrated(), "Entity should not be merge migrated.");
        Assert.assertTrue(httpResponse.isSplitMigrated(), "Entity should be split migrated.");
    }

    @Test
    public void testFindUpgradeGuidance() {
        UUID componentVersionId = UUID.fromString("d10c3ded-9e0e-468e-909e-637d81ecc554");

        HttpResult<UpgradeGuidance> httpResult = componentVersionApi.findUpgradeGuidance(componentVersionId, VulnerabilitySourcePriority.CVE,
                VulnerabilityScorePriority.CVSS_2);

        HttpResponse<UpgradeGuidance> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");

        if (httpResponse.isMigrated()) {
            throw new SkipException("This functional test method was written using a non-migrated component version id.  "
                    + "Since then, this component version id has been migrated by the KnowledgeBase.  "
                    + "Update this test with a new non-migrated component version id.");
        }

        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        UpgradeGuidance upgradeGuidance = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(upgradeGuidance, "Upgrade guidance should be initialized.");
    }

    @Test
    public void testFindUpgradeGuidanceWhenMergeMigrated() {
        UUID componentVersionId = UUID.fromString("0bca7263-1033-4731-9985-809eb87ecfb7");

        HttpResult<UpgradeGuidance> httpResult = componentVersionApi.findUpgradeGuidance(componentVersionId, VulnerabilitySourcePriority.CVE,
                VulnerabilityScorePriority.CVSS_2);

        HttpResponse<UpgradeGuidance> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_MOVED_PERMANENTLY, "Codes should be equal.");
        Assert.assertFalse(httpResponse.isMessageBodyPresent(), "Message body should not be present.");
        Assert.assertTrue(httpResponse.isMigratedMetaPresent(), "Migrated meta should be present.");
        Assert.assertTrue(httpResponse.isMigrated(), "Entity should be migrated.");
        Assert.assertTrue(httpResponse.isMergeMigrated(), "Entity should be merge migrated.");
        Assert.assertFalse(httpResponse.isSplitMigrated(), "Entity should not be split migrated.");
    }

    @Test
    public void testFindUpgradeGuidanceWhenSplitMigrated() {
        UUID componentVersionId = UUID.fromString("1623f022-fb37-428a-a491-ab1ce1918d16");

        HttpResult<UpgradeGuidance> httpResult = componentVersionApi.findUpgradeGuidance(componentVersionId, VulnerabilitySourcePriority.CVE,
                VulnerabilityScorePriority.CVSS_2);

        HttpResponse<UpgradeGuidance> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_MULTIPLE_CHOICES, "Codes should be equal.");
        Assert.assertFalse(httpResponse.isMessageBodyPresent(), "Message body should not be present.");
        Assert.assertTrue(httpResponse.isMigratedMetaPresent(), "Migrated meta should be present.");
        Assert.assertTrue(httpResponse.isMigrated(), "Entity should be migrated.");
        Assert.assertFalse(httpResponse.isMergeMigrated(), "Entity should not be merge migrated.");
        Assert.assertTrue(httpResponse.isSplitMigrated(), "Entity should be split migrated.");
    }
}
