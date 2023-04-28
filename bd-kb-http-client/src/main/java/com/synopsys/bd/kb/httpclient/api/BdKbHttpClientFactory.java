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
package com.synopsys.bd.kb.httpclient.api;

import java.util.Objects;

import com.synopsys.bd.kb.httpclient.impl.BdComponentApi;
import com.synopsys.bd.kb.httpclient.impl.BdComponentFinder;
import com.synopsys.bd.kb.httpclient.impl.BdComponentVariantApi;
import com.synopsys.bd.kb.httpclient.impl.BdComponentVersionApi;
import com.synopsys.bd.kb.httpclient.impl.BdKbHttpApi;
import com.synopsys.bd.kb.httpclient.impl.BdLicenseApi;
import com.synopsys.bd.kb.httpclient.impl.BdLicenseDefinitionFinder;
import com.synopsys.bd.kb.httpclient.impl.BdVulnerabilityMerger;
import com.synopsys.kb.httpclient.api.HttpClientConfiguration;
import com.synopsys.kb.httpclient.api.IComponentApi;
import com.synopsys.kb.httpclient.api.IComponentVariantApi;
import com.synopsys.kb.httpclient.api.IComponentVersionApi;
import com.synopsys.kb.httpclient.api.IKbHttpApi;
import com.synopsys.kb.httpclient.api.ILicenseApi;
import com.synopsys.kb.httpclient.api.KbConfiguration;
import com.synopsys.kb.httpclient.api.KbHttpClientFactory;

/**
 * Black Duck-centric KB HTTP client factory.
 * 
 * Package protected.
 * 
 * @author skatzman
 */
class BdKbHttpClientFactory {
    public BdKbHttpClientFactory() {
    }

    /**
     * Creates the Black Duck-centric KB HTTP client.
     * 
     * @param httpClientConfiguration
     *            The HTTP client configuration.
     * @param kbConfiguration
     *            The KB configuration.
     * @return Returns the Black Duck-centric KB HTTP client.
     */
    public IBdKbHttpApi create(HttpClientConfiguration httpClientConfiguration,
            KbConfiguration kbConfiguration) {
        Objects.requireNonNull(httpClientConfiguration, "HTTP client configuration must be initialized.");
        Objects.requireNonNull(kbConfiguration, "KB configuration must be initialized.");

        IKbHttpApi kbHttpApi = new KbHttpClientFactory().create(httpClientConfiguration, kbConfiguration);

        String baseHref = kbConfiguration.getHref();

        IComponentApi componentApi = kbHttpApi.getComponentApi();
        IBdComponentApi bdComponentApi = new BdComponentApi(componentApi, baseHref);

        IComponentVersionApi componentVersionApi = kbHttpApi.getComponentVersionApi();
        IBdComponentVersionApi bdComponentVersionApi = new BdComponentVersionApi(componentVersionApi, baseHref);

        IComponentVariantApi componentVariantApi = kbHttpApi.getComponentVariantApi();
        IBdComponentVariantApi bdComponentVariantApi = new BdComponentVariantApi(componentVariantApi);

        ILicenseApi licenseApi = kbHttpApi.getLicenseApi();
        IBdLicenseApi bdLicenseApi = new BdLicenseApi(licenseApi);

        BdComponentFinder bdComponentFinder = new BdComponentFinder(bdComponentApi, bdComponentVersionApi, bdComponentVariantApi);
        BdLicenseDefinitionFinder bdLicenseDefinitionFinder = new BdLicenseDefinitionFinder(bdLicenseApi);
        BdVulnerabilityMerger bdVulnerabilityMerger = new BdVulnerabilityMerger();

        return new BdKbHttpApi(bdComponentApi, bdComponentVersionApi, bdComponentVariantApi, bdLicenseApi, bdComponentFinder, bdLicenseDefinitionFinder,
                bdVulnerabilityMerger, kbHttpApi);
    }
}
