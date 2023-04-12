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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.synopsys.bd.kb.httpclient.api.IBdLicenseApi;
import com.synopsys.bd.kb.httpclient.model.BdLicense;
import com.synopsys.kb.httpclient.api.ILicenseApi;
import com.synopsys.kb.httpclient.api.PageRequest;
import com.synopsys.kb.httpclient.api.Result;
import com.synopsys.kb.httpclient.model.License;
import com.synopsys.kb.httpclient.model.Meta;
import com.synopsys.kb.httpclient.model.Page;

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
    public Result<BdLicense> findLicense(UUID licenseId) {
        Objects.requireNonNull(licenseId, "License id must be initialized.");

        Result<License> result = licenseApi.findLicense(licenseId);

        // Convert a license to a BD license.
        Function<License, BdLicense> conversionFunction = (license) -> {
            return new BdLicense(license);
        };

        return convert(result, conversionFunction);
    }

    @Override
    public Result<Page<BdLicense>> findManyLicenses(PageRequest pageRequest,
            @Nullable String searchTermFilter,
            @Nullable Map<String, String> filters) {
        Objects.requireNonNull(pageRequest, "Page request must be initialized.");

        Result<Page<License>> result = licenseApi.findManyLicenses(pageRequest, searchTermFilter, filters);

        Function<Page<License>, Page<BdLicense>> conversionFunction = (licensePage) -> {
            int totalCount = licensePage.getTotalCount();
            List<BdLicense> bdLicenses = licensePage.getItems().stream().map(BdLicense::new).collect(Collectors.toList());
            Meta meta = licensePage.getMeta();

            return new Page<>(totalCount, bdLicenses, meta);
        };

        return convert(result, conversionFunction);
    }
}
