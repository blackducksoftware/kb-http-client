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

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synopsys.kb.httpclient.AbstractTest;

/**
 * Logo test.
 * 
 * @author skatzman
 */
public class LogoTest extends AbstractTest {
    private static final String TYPE = "small";

    private static final String URL = "https://s3.amazonaws.com/cloud.ohloh.net/attachments/1183/java_library_small.png";

    @Test
    public void testConstructor() {
        Logo logo = new Logo(TYPE, URL);

        Assert.assertEquals(logo.getType(), TYPE, "Types should be equal.");
        Assert.assertEquals(logo.getUrl(), URL, "URLs should be equal.");
    }

    @Test
    public void testDeserialization() throws JsonProcessingException {
        Logo logo = new Logo(TYPE, URL);

        String json = serialize(logo);
        Logo result = deserialize(json, Logo.class);

        Assert.assertEquals(result.getType(), TYPE, "Types should be equal.");
        Assert.assertEquals(result.getUrl(), URL, "URLs should be equal.");
    }

    @Test
    public void testHashCode() {
        Logo logo = new Logo(TYPE, URL);
        Logo copyLogo = new Logo(TYPE, URL);
        Logo differentLogo = new Logo(TYPE, "https://different.com");

        assertHashCode(logo, copyLogo, differentLogo);
    }

    @Test
    public void testEquals() {
        Logo logo = new Logo(TYPE, URL);
        Logo copyLogo = new Logo(TYPE, URL);
        Logo differentLogo = new Logo(TYPE, "https://different.com");

        assertEquals(logo, copyLogo, differentLogo);
    }
}
