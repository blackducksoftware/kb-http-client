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

import org.apache.hc.core5.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.synopsys.kb.httpclient.AbstractFuncTest;
import com.synopsys.kb.httpclient.api.HttpResponse;
import com.synopsys.kb.httpclient.api.IAuthenticationApi;
import com.synopsys.kb.httpclient.api.IKbHttpApi;
import com.synopsys.kb.httpclient.api.Result;
import com.synopsys.kb.httpclient.model.Authentication;

/**
 * KB authentication HTTP client functional test.
 * 
 * @author skatzman
 */
public class KbAuthenticationHttpClientFuncTest extends AbstractFuncTest {
    private IAuthenticationApi authenticationApi;

    @BeforeMethod
    public void beforeMethod() {
        IKbHttpApi kbHttpApi = getKbHttpApi();
        this.authenticationApi = kbHttpApi.getAuthenticationApi();
    }

    @Test
    public void testAuthenticateWithInvalidLicenseKey() {
        Result<Authentication> result = authenticationApi.authenticate("this-is-an-invalid-bds-license-key");

        HttpResponse<Authentication> httpResponse = result.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_UNAUTHORIZED, "Codes should be equal.");
        Assert.assertFalse(httpResponse.getMessageBody().isPresent(), "Message body should not be present.");
    }

    @Test
    public void testAuthenticateWithValidLicenseKey() {
        Result<Authentication> result = authenticationApi.authenticate("eng_hub_build");

        HttpResponse<Authentication> httpResponse = result.getHttpResponse().orElse(null);

        Assert.assertNotNull(httpResponse, "HTTP response should be initialized.");
        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        Authentication authentication = httpResponse.getMessageBody().orElse(null);
        Assert.assertNotNull(authentication, "Authentication should be initialized.");
        Assert.assertNotNull(authentication.getJsonWebToken(), "JSON web token should be initialized.");
        Assert.assertTrue(authentication.getExpiresInMillis() > 0L, "Expires in millis should be positive.");
    }
}
