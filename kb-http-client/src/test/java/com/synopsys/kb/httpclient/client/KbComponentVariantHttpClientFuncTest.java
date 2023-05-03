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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.synopsys.kb.httpclient.AbstractFuncTest;
import com.synopsys.kb.httpclient.api.HttpResponse;
import com.synopsys.kb.httpclient.api.HttpResult;
import com.synopsys.kb.httpclient.api.IComponentVariantApi;
import com.synopsys.kb.httpclient.api.IKbHttpApi;
import com.synopsys.kb.httpclient.api.PageRequest;
import com.synopsys.kb.httpclient.model.BdsaVulnerability;
import com.synopsys.kb.httpclient.model.ComponentVariant;
import com.synopsys.kb.httpclient.model.CveVulnerability;
import com.synopsys.kb.httpclient.model.Page;
import com.synopsys.kb.httpclient.model.UpgradeGuidance;
import com.synopsys.kb.httpclient.model.VulnerabilityScorePriority;
import com.synopsys.kb.httpclient.model.VulnerabilitySourcePriority;

/**
 * Component variant HTTP client functional test.
 * 
 * @author skatzman
 */
public class KbComponentVariantHttpClientFuncTest extends AbstractFuncTest {
    private IComponentVariantApi componentVariantApi;

    @BeforeClass
    public void beforeClass() {
        IKbHttpApi kbHttpApi = getKbHttpApi();
        this.componentVariantApi = kbHttpApi.getComponentVariantApi();
    }

    @Test
    public void testFindComponentVariantV4() {
        UUID componentVariantId = UUID.fromString("64b5b35d-0cfa-4a0e-b0aa-90db4dcf2734");

        HttpResult<ComponentVariant> httpResult = componentVariantApi.findComponentVariantV4(componentVariantId);

        HttpResponse<ComponentVariant> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        ComponentVariant componentVariant = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(componentVariant, "Component variant should be initialized.");
    }

    @Test
    public void testFindCveVulnerabilitiesV7() {
        PageRequest pageRequest = new PageRequest(0, 100, Collections.emptyList());
        UUID componentVariantId = UUID.fromString("400db080-9a5b-4819-83a8-48ec7c050447");

        HttpResult<Page<CveVulnerability>> httpResult = componentVariantApi.findCveVulnerabilitiesV7(pageRequest, componentVariantId, null);

        HttpResponse<Page<CveVulnerability>> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        Page<CveVulnerability> page = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(page, "Page should be initialized.");
        List<CveVulnerability> items = page.getItems();
        Assert.assertNotNull(items, "Items should be initialized.");
        Assert.assertFalse(items.isEmpty(), "Items should not be empty.");
    }

    @Test
    public void testFindBdsaVulnerabilitiesV7() {
        PageRequest pageRequest = new PageRequest(0, 100, Collections.emptyList());
        UUID componentVariantId = UUID.fromString("a8254896-a7a5-4495-a6eb-a8977d67639d");

        HttpResult<Page<BdsaVulnerability>> httpResult = componentVariantApi.findBdsaVulnerabilitiesV7(pageRequest, componentVariantId, null);

        HttpResponse<Page<BdsaVulnerability>> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        Page<BdsaVulnerability> page = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(page, "Page should be initialized.");
        List<BdsaVulnerability> items = page.getItems();
        Assert.assertNotNull(items, "Items should be initialized.");
        Assert.assertFalse(items.isEmpty(), "Items should not be empty.");
    }

    @Test
    public void testFindUpgradeGuidanceV4() {
        UUID componentVariantId = UUID.fromString("26d300de-5d0a-4740-b7c1-fb913d91c54d");

        HttpResult<UpgradeGuidance> httpResult = componentVariantApi.findUpgradeGuidanceV4(componentVariantId, VulnerabilitySourcePriority.CVE,
                VulnerabilityScorePriority.CVSS_2);

        HttpResponse<UpgradeGuidance> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        UpgradeGuidance upgradeGuidance = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(upgradeGuidance, "Upgrade guidance should be initialized.");
    }

    @Test
    public void testFindTransitiveUpgradeGuidanceV4() {
        UUID componentVariantId = UUID.fromString("0a6b80d0-e64e-4a68-8002-5a5e6951c568");

        HttpResult<UpgradeGuidance> httpResult = componentVariantApi.findTransitiveUpgradeGuidanceV4(componentVariantId, VulnerabilitySourcePriority.CVE,
                VulnerabilityScorePriority.CVSS_2);

        HttpResponse<UpgradeGuidance> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        UpgradeGuidance transitiveUpgradeGuidance = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(transitiveUpgradeGuidance, "Transitive upgrade guidance should be initialized.");
    }
}
