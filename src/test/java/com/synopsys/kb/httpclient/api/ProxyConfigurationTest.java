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
package com.synopsys.kb.httpclient.api;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.synopsys.kb.httpclient.AbstractTest;

public class ProxyConfigurationTest extends AbstractTest {
    private static final String SCHEME = "https";

    private static final String HOST = "tank.blackducksoftware.com";

    private static final int PORT = 443;

    private static final String USER_NAME = "jsmith";

    private static final String PASSWORD = "123456";

    @Test
    public void testConstructor() {
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration(SCHEME, HOST, PORT, USER_NAME, PASSWORD);

        Assert.assertEquals(proxyConfiguration.getScheme(), SCHEME, "Schemes should be equal.");
        Assert.assertEquals(proxyConfiguration.getHost(), HOST, "Hosts should be equal.");
        Assert.assertEquals(proxyConfiguration.getPort(), PORT, "Ports should be equal.");
        Assert.assertEquals(proxyConfiguration.getUserName().orElse(null), USER_NAME, "User names should be equal.");
        Assert.assertEquals(proxyConfiguration.getPassword().orElse(null), PASSWORD, "Passwords should be equal.");
    }

    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void testConstructorWithNullScheme() {
        new ProxyConfiguration(null, HOST, PORT, USER_NAME, PASSWORD);
    }

    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void testConstructorWithEmptyScheme() {
        new ProxyConfiguration("", HOST, PORT, USER_NAME, PASSWORD);
    }

    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void testConstructorWithNullHost() {
        new ProxyConfiguration(SCHEME, null, PORT, USER_NAME, PASSWORD);
    }

    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void testConstructorWithEmptyHost() {
        new ProxyConfiguration(SCHEME, "", PORT, USER_NAME, PASSWORD);
    }

    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void testConstructorWithPortBelowMinimum() {
        new ProxyConfiguration(SCHEME, HOST, -1, USER_NAME, PASSWORD);
    }
}
