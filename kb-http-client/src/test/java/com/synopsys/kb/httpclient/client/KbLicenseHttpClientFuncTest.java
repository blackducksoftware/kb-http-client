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

import java.util.UUID;

import org.apache.hc.core5.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.synopsys.kb.httpclient.AbstractFuncTest;
import com.synopsys.kb.httpclient.api.HttpResponse;
import com.synopsys.kb.httpclient.api.IKbHttpApi;
import com.synopsys.kb.httpclient.api.ILicenseApi;
import com.synopsys.kb.httpclient.api.Result;
import com.synopsys.kb.httpclient.model.License;

/**
 * KB license HTTP client functional test.
 * 
 * @author skatzman
 */
public class KbLicenseHttpClientFuncTest extends AbstractFuncTest {
    private ILicenseApi licenseApi;

    @BeforeMethod
    public void beforeMethod() {
        IKbHttpApi kbHttpApi = getKbHttpApi();
        this.licenseApi = kbHttpApi.getLicenseApi();
    }

    @Test(enabled = false)
    public void testFindLicenseWhenAbsent() {
        Result<License> result = licenseApi.find(UUID.randomUUID());

        HttpResponse<License> httpResponse = result.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_NOT_FOUND, "Codes should be equal.");
        Assert.assertFalse(httpResponse.isMessageBodyPresent(), "Message body should not be present.");
    }

    @Test(enabled = false)
    public void testFindLicenseWhenPresent() {
        UUID apacheLicense20LicenseId = UUID.fromString("7cae335f-1193-421e-92f1-8802b4243e93");
        Result<License> result = licenseApi.find(apacheLicense20LicenseId);

        HttpResponse<License> httpResponse = result.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        License license = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(license, "License should be initialized.");
        Assert.assertEquals(license.getId(), apacheLicense20LicenseId, "License ids should be equal.");
    }
}
