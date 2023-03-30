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

import java.util.Objects;

/**
 * KB HTTP API implementation.
 * 
 * Package protected.
 * 
 * @author skatzman
 */
class KbHttpApi implements IKbHttpApi {
    private final IAuthenticationApi authenticationApi;

    private final IComponentApi componentApi;

    private final IComponentVersionApi componentVersionApi;

    private final IComponentVariantApi componentVariantApi;

    private final ILicenseApi licenseApi;

    public KbHttpApi(IAuthenticationApi authenticationApi,
            IComponentApi componentApi,
            IComponentVersionApi componentVersionApi,
            IComponentVariantApi componentVariantApi,
            ILicenseApi licenseApi) {
        this.authenticationApi = Objects.requireNonNull(authenticationApi, "Authentication API must be initialized.");
        this.componentApi = Objects.requireNonNull(componentApi, "Component API must be initialized.");
        this.componentVersionApi = Objects.requireNonNull(componentVersionApi, "Component version API must be initialized.");
        this.componentVariantApi = Objects.requireNonNull(componentVariantApi, "Component variant API must be initialized.");
        this.licenseApi = Objects.requireNonNull(licenseApi, "License API must be initialized.");
    }

    @Override
    public IAuthenticationApi getAuthenticationApi() {
        return authenticationApi;
    }

    @Override
    public IComponentApi getComponentApi() {
        return componentApi;
    }

    @Override
    public IComponentVersionApi getComponentVersionApi() {
        return componentVersionApi;
    }

    @Override
    public IComponentVariantApi getComponentVariantApi() {
        return componentVariantApi;
    }

    @Override
    public ILicenseApi getLicenseApi() {
        return licenseApi;
    }
}
