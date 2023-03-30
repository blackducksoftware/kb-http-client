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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.synopsys.kb.httpclient.AbstractTest;

/**
 * Static license key supplier test.
 * 
 * @author skatzman
 */
public class StaticLicenseKeySupplierTest extends AbstractTest {
    private static final String LICENSE_KEY = "12345";

    private Supplier<String> supplier;

    @BeforeMethod
    public void beforeMethod() {
        supplier = new StaticLicenseKeySupplier(LICENSE_KEY);
    }

    @Test
    public void testGet() {
        String licenseKey = supplier.get();

        Assert.assertEquals(licenseKey, LICENSE_KEY, "License keys should be equal.");
    }
}
