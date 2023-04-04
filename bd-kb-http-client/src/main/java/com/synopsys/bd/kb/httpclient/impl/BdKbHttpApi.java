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

import com.synopsys.bd.kb.httpclient.api.IBdComponentApi;
import com.synopsys.bd.kb.httpclient.api.IBdComponentVariantApi;
import com.synopsys.bd.kb.httpclient.api.IBdComponentVersionApi;
import com.synopsys.bd.kb.httpclient.api.IBdKbHttpApi;
import com.synopsys.bd.kb.httpclient.api.IBdLicenseApi;
import com.synopsys.kb.httpclient.api.IKbHttpApi;

/**
 * Black Duck-centric KB HTTP API implementation.
 * 
 * Package protected.
 * 
 * @author skatzman
 */
public class BdKbHttpApi implements IBdKbHttpApi {
    private final IBdComponentApi bdComponentApi;

    private final IBdComponentVersionApi bdComponentVersionApi;

    private final IBdComponentVariantApi bdComponentVariantApi;

    private final IBdLicenseApi bdLicenseApi;

    private final BdVulnerabilityMerger bdVulnerabilityMerger;

    private final IKbHttpApi kbHttpApi;

    public BdKbHttpApi(IBdComponentApi bdComponentApi,
            IBdComponentVersionApi bdComponentVersionApi,
            IBdComponentVariantApi bdComponentVariantApi,
            IBdLicenseApi bdLicenseApi,
            BdVulnerabilityMerger bdVulnerabilityMerger,
            IKbHttpApi kbHttpApi) {
        this.bdComponentApi = Objects.requireNonNull(bdComponentApi, "BD component API must be initialized.");
        this.bdComponentVersionApi = Objects.requireNonNull(bdComponentVersionApi, "BD component version API must be initialized.");
        this.bdComponentVariantApi = Objects.requireNonNull(bdComponentVariantApi, "BD component variant API must be initialized.");
        this.bdLicenseApi = Objects.requireNonNull(bdLicenseApi, "BD license API must be initialized.");
        this.bdVulnerabilityMerger = Objects.requireNonNull(bdVulnerabilityMerger, "BD vulnerability merger must be initialized.");
        this.kbHttpApi = Objects.requireNonNull(kbHttpApi, "KB HTTP API must be initialized.");
    }

    @Override
    public IBdComponentApi getBdComponentApi() {
        return bdComponentApi;
    }

    @Override
    public IBdComponentVersionApi getBdComponentVersionApi() {
        return bdComponentVersionApi;
    }

    @Override
    public IBdComponentVariantApi getBdComponentVariantApi() {
        return bdComponentVariantApi;
    }

    @Override
    public IBdLicenseApi getBdLicenseApi() {
        return bdLicenseApi;
    }

    @Override
    public BdVulnerabilityMerger getBdVulnerabilityMerger() {
        return bdVulnerabilityMerger;
    }

    @Override
    public IKbHttpApi getKbHttpApi() {
        return kbHttpApi;
    }
}
