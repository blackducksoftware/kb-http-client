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
package com.synopsys.kb.httpclient.model;

import java.util.concurrent.TimeUnit;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synopsys.kb.httpclient.AbstractTest;

/**
 * Authentication test.
 * 
 * @author skatzman
 */
public class AuthenticationTest extends AbstractTest {
    private static final boolean EXPIRATION_WARNING = false;

    private static final String JSON_WEB_TOKEN = "12345";

    private static final long EXPIRES_IN_MILLIS = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(10L);

    @Test
    public void testConstructor() {
        Authentication authentication = new Authentication(EXPIRATION_WARNING, JSON_WEB_TOKEN, EXPIRES_IN_MILLIS);

        Assert.assertEquals(authentication.isExpirationWarning(), EXPIRATION_WARNING, "Expiration warning flags should be equal.");
        Assert.assertEquals(authentication.getJsonWebToken(), JSON_WEB_TOKEN, "JSON web tokens should be equal.");
        Assert.assertEquals(authentication.getExpiresInMillis(), EXPIRES_IN_MILLIS, "Expires in millis should be equal.");
    }

    @Test
    public void testDeserialization() throws JsonProcessingException {
        Authentication authentication = new Authentication(EXPIRATION_WARNING, JSON_WEB_TOKEN, EXPIRES_IN_MILLIS);

        String json = serialize(authentication);
        Authentication result = deserialize(json, Authentication.class);

        Assert.assertEquals(result.isExpirationWarning(), EXPIRATION_WARNING, "Expiration warning flags should be equal.");
        Assert.assertEquals(result.getJsonWebToken(), JSON_WEB_TOKEN, "JSON web tokens should be equal.");
        Assert.assertEquals(result.getExpiresInMillis(), EXPIRES_IN_MILLIS, "Expires in millis should be equal.");
    }
}
