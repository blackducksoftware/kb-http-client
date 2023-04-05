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
package com.synopsys.bd.kb.httpclient.impl;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

import com.synopsys.bd.kb.httpclient.api.IBdLicenseApi;
import com.synopsys.bd.kb.httpclient.model.BdLicense;
import com.synopsys.kb.httpclient.api.ILicenseApi;
import com.synopsys.kb.httpclient.api.Result;
import com.synopsys.kb.httpclient.model.License;

/**
 * Black Duck-centric license API implementation.
 * 
 * Primarily used to return license representation in a Black Duck-specific manner.
 * 
 * @author skatzman
 */
public class BdLicenseApi extends AbstractBdApi implements IBdLicenseApi {
    private final ILicenseApi licenseApi;

    public BdLicenseApi(ILicenseApi licenseApi) {
        super();

        this.licenseApi = Objects.requireNonNull(licenseApi, "License API must be initialized.");
    }

    @Override
    public Result<BdLicense> find(UUID licenseId) {
        Objects.requireNonNull(licenseId, "License id must be initialized.");

        Result<License> result = licenseApi.find(licenseId);

        // Convert a license to a BD license.
        Function<License, BdLicense> conversionFunction = (license) -> {
            return new BdLicense(license);
        };

        return convert(result, conversionFunction);
    }
}