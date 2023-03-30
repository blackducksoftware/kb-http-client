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

import com.synopsys.kb.httpclient.model.Authentication;

/**
 * Authentication API interface.
 * 
 * Authentication operations for granting KnowledgeBase access.
 * 
 * @author skatzman
 */
public interface IAuthenticationApi {
    /**
     * Authenticate given the license key.
     * 
     * Expected response codes
     * 200 OK
     * 
     * @param licenseKey
     *            The license key.
     * @return Returns the authentication result.
     */
    Result<Authentication> authenticate(String licenseKey);
}
