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

import java.util.UUID;

import com.synopsys.bd.kb.httpclient.model.BdLicense;
import com.synopsys.kb.httpclient.api.PageRequest;
import com.synopsys.kb.httpclient.api.Result;
import com.synopsys.kb.httpclient.model.Page;

/**
 * Black Duck-centric license API interface.
 * 
 * @author skatzman
 */
public interface IBdLicenseApi {
    /**
     * Finds a Black Duck-centric license by its id.
     * 
     * Expected response codes
     * 200 OK
     * 404 Not Found
     * 
     * @param licenseId
     *            The license id.
     * @return Returns the Black Duck-centric license result.
     */
    Result<BdLicense> find(UUID licenseId);

    /**
     * Finds many Black Duck-centric licenses.
     * 
     * Expected response codes
     * 200 OK
     * 
     * @param pageRequest
     *            The page request.
     * @return Returns the Black Duck-centric license page result.
     */
    Result<Page<BdLicense>> findMany(PageRequest pageRequest);
}
