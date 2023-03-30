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

import java.util.function.Supplier;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.synopsys.kb.httpclient.AbstractTest;

/**
 * KnowledgeBase configuration test.
 * 
 * @author skatzman
 */
public class KbConfigurationTest extends AbstractTest {
    private static final String HOST = "kbtest.blackducksoftware.com";

    private static final Supplier<String> LICENSE_KEY_SUPPLIER = new StaticLicenseKeySupplier("12345");

    @Test
    public void testConstructor() {
        KbConfiguration kbConfiguration = new KbConfiguration(KbConfiguration.DEFAULT_SCHEME, HOST, KbConfiguration.DEFAULT_PORT, LICENSE_KEY_SUPPLIER);

        Assert.assertEquals(kbConfiguration.getScheme(), KbConfiguration.DEFAULT_SCHEME, "Schemes should be equal.");
        Assert.assertEquals(kbConfiguration.getHost(), HOST, "Hosts should be equal.");
        Assert.assertEquals(kbConfiguration.getPort(), KbConfiguration.DEFAULT_PORT, "Ports should be equal.");
        Assert.assertEquals(kbConfiguration.getLicenseKeySupplier(), LICENSE_KEY_SUPPLIER, "License keys should be equal.");
    }

    @Test
    public void testToString() {
        KbConfiguration kbConfiguration = new KbConfiguration(KbConfiguration.DEFAULT_SCHEME, HOST, KbConfiguration.DEFAULT_PORT, LICENSE_KEY_SUPPLIER);
        String value = kbConfiguration.toString();

        Assert.assertEquals(value, KbConfiguration.DEFAULT_SCHEME + "://" + HOST + ':' + KbConfiguration.DEFAULT_PORT, "Values should be equal.");
    }
}
