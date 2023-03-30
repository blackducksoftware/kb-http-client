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

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * Static license key provider.
 * 
 * Does not allow for dynamic revision of the license key at runtime.
 * 
 * Package protected.
 * 
 * @author skatzman
 */
class StaticLicenseKeySupplier implements Supplier<String> {
    private final String licenseKey;

    public StaticLicenseKeySupplier(String licenseKey) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(licenseKey), "License key must not be null or empty.");

        this.licenseKey = licenseKey;
    }

    @Override
    public String get() {
        return licenseKey;
    }
}
