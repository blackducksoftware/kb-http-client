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
import com.synopsys.kb.httpclient.api.IKbHttpApi;
import com.synopsys.kb.httpclient.api.ILicenseApi;
import com.synopsys.kb.httpclient.api.PageRequest;
import com.synopsys.kb.httpclient.model.License;
import com.synopsys.kb.httpclient.model.LicenseFamilyVsl;
import com.synopsys.kb.httpclient.model.LicenseTerm;
import com.synopsys.kb.httpclient.model.LicenseVsl;
import com.synopsys.kb.httpclient.model.Page;

/**
 * KB license HTTP client functional test.
 * 
 * @author skatzman
 */
public class KbLicenseHttpClientFuncTest extends AbstractFuncTest {
    private ILicenseApi licenseApi;

    @BeforeClass
    public void beforeClass() {
        IKbHttpApi kbHttpApi = getKbHttpApi();
        this.licenseApi = kbHttpApi.getLicenseApi();
    }

    @Test
    public void testFindLicenseV4WhenAbsent() {
        HttpResult<License> httpResult = licenseApi.findLicenseV4(UUID.randomUUID());

        HttpResponse<License> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_NOT_FOUND, "Codes should be equal.");
        Assert.assertFalse(httpResponse.isMessageBodyPresent(), "Message body should not be present.");
    }

    @Test
    public void testFindLicenseV4WhenPresent() {
        UUID apacheLicense20LicenseId = UUID.fromString("7cae335f-1193-421e-92f1-8802b4243e93");
        HttpResult<License> httpResult = licenseApi.findLicenseV4(apacheLicense20LicenseId);

        HttpResponse<License> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        License license = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(license, "License should be initialized.");
        Assert.assertEquals(license.getId(), apacheLicense20LicenseId, "License ids should be equal.");
    }

    @Test
    public void testFindLicenseTextV1() {
        UUID apacheLicense20LicenseId = UUID.fromString("7cae335f-1193-421e-92f1-8802b4243e93");
        HttpResult<String> httpResult = licenseApi.findLicenseTextV1(apacheLicense20LicenseId);

        HttpResponse<String> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        String licenseText = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(licenseText, "License text should be initialized.");
    }

    @Test
    public void testFindManyLicensesV4() {
        PageRequest pageRequest = new PageRequest(0, 10, Collections.emptyList());

        HttpResult<Page<License>> httpResult = licenseApi.findManyLicensesV4(pageRequest, null, null);

        HttpResponse<Page<License>> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        Page<License> page = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(page, "Page should be initialized.");
        List<License> items = page.getItems();
        Assert.assertNotNull(items, "Items should be initialized.");
        Assert.assertFalse(items.isEmpty(), "Items should not be empty.");
    }

    @Test
    public void testFindLicensesByLicenseTermV4() {
        PageRequest pageRequest = new PageRequest(0, 10, Collections.emptyList());
        UUID licenseTermId = UUID.fromString("052a4876-67d7-4f72-959f-e56914ecbb33");

        HttpResult<Page<License>> httpResult = licenseApi.findLicensesByLicenseTermV4(pageRequest, licenseTermId);

        HttpResponse<Page<License>> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        Page<License> page = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(page, "Page should be initialized.");
        List<License> items = page.getItems();
        Assert.assertNotNull(items, "Items should be initialized.");
        Assert.assertFalse(items.isEmpty(), "Items should not be empty.");
    }

    @Test
    public void testFindLicenseTermV4() {
        UUID licenseTermId = UUID.fromString("04625083-2bc6-407c-ba9f-f461152859de");

        HttpResult<LicenseTerm> httpResult = licenseApi.findLicenseTermV4(licenseTermId);

        HttpResponse<LicenseTerm> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        LicenseTerm licenseTerm = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(licenseTerm, "License term should be initialized.");
        Assert.assertEquals(licenseTerm.getId(), licenseTermId, "License term ids should be equal.");
    }

    @Test
    public void testFindManyLicenseTermsV4() {
        PageRequest pageRequest = new PageRequest(0, 10, Collections.emptyList());

        HttpResult<Page<LicenseTerm>> httpResult = licenseApi.findManyLicenseTermsV4(pageRequest);

        HttpResponse<Page<LicenseTerm>> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        Page<LicenseTerm> page = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(page, "Page should be initialized.");
        List<LicenseTerm> items = page.getItems();
        Assert.assertNotNull(items, "Items should be initialized.");
        Assert.assertFalse(items.isEmpty(), "Items should not be empty.");
    }

    @Test
    public void testFindLicenseTermsByLicenseV4() {
        PageRequest pageRequest = new PageRequest(0, 10, Collections.emptyList());
        UUID apacheLicense20LicenseId = UUID.fromString("7cae335f-1193-421e-92f1-8802b4243e93");

        HttpResult<Page<LicenseTerm>> httpResult = licenseApi.findLicenseTermsByLicenseV4(pageRequest, apacheLicense20LicenseId);

        HttpResponse<Page<LicenseTerm>> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        Page<LicenseTerm> page = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(page, "Page should be initialized.");
        List<LicenseTerm> items = page.getItems();
        Assert.assertNotNull(items, "Items should be initialized.");
        Assert.assertFalse(items.isEmpty(), "Items should not be empty.");
    }

    @Test
    public void testFindManyLicenseVslsV4() {
        PageRequest pageRequest = new PageRequest(0, 10, Collections.emptyList());

        HttpResult<Page<LicenseVsl>> httpResult = licenseApi.findManyLicenseVslsV4(pageRequest);

        HttpResponse<Page<LicenseVsl>> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        Page<LicenseVsl> page = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(page, "Page should be initialized.");
        List<LicenseVsl> items = page.getItems();
        Assert.assertNotNull(items, "Items should be initialized.");
        Assert.assertFalse(items.isEmpty(), "Items should not be empty.");
    }

    @Test
    public void testFindManyLicenseFamilyVslsV4() {
        PageRequest pageRequest = new PageRequest(0, 10, Collections.emptyList());

        HttpResult<Page<LicenseFamilyVsl>> httpResult = licenseApi.findManyLicenseFamilyVslsV4(pageRequest);

        HttpResponse<Page<LicenseFamilyVsl>> httpResponse = httpResult.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        Page<LicenseFamilyVsl> page = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(page, "Page should be initialized.");
        List<LicenseFamilyVsl> items = page.getItems();
        Assert.assertNotNull(items, "Items should be initialized.");
        Assert.assertFalse(items.isEmpty(), "Items should not be empty.");
    }
}
