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

/**
 * KB HTTP API interface.
 * 
 * @author skatzman
 */
public interface IKbHttpApi {
    /**
     * Gets the authentication API.
     * 
     * The authentication API contains operations relating to authentication.
     * 
     * @return Returns the authentication API.
     */
    IAuthenticationApi getAuthenticationApi();

    /**
     * Gets the component API.
     * 
     * The component API contains operations relating to components.
     * 
     * @return Returns the component API.
     */
    IComponentApi getComponentApi();

    /**
     * Gets the component version API.
     * 
     * The component version API contains operations relating to component versions.
     * 
     * @return Returns the component version API.
     */
    IComponentVersionApi getComponentVersionApi();

    /**
     * Gets the component variant API.
     * 
     * The component variant API contains operations relating to component variants.
     * 
     * @return Returns the component variant API.
     */
    IComponentVariantApi getComponentVariantApi();

    /**
     * Gets the license API.
     * 
     * The license API contains operations relating to licenses.
     * 
     * @return Returns the license API.
     */
    ILicenseApi getLicenseApi();

    /**
     * Gets the activity API.
     * 
     * The activity API contains operations relating to activities.
     * 
     * @return Returns the activity API.
     */
    IActivityApi getActivityApi();
}
