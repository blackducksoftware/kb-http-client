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

import java.util.UUID;

import com.synopsys.kb.httpclient.model.License;
import com.synopsys.kb.httpclient.model.LicenseTerm;
import com.synopsys.kb.httpclient.model.Page;

/**
 * License API interface.
 * 
 * @author skatzman
 */
public interface ILicenseApi {
    /**
     * Finds a license by its id.
     * 
     * - Response codes of 404 Not Found should be gracefully handled as an absent result.
     * 
     * Version: 4
     * 
     * Expected response codes
     * 200 OK
     * 404 Not Found
     * 
     * @param licenseId
     *            The license id.
     * @return Returns the license result.
     */
    Result<License> findLicense(UUID licenseId);

    /**
     * Finds a license's original license text by its id.
     * 
     * Version: 1
     * 
     * Expected response codes
     * 200 OK
     * 404 Not Found
     * 
     * @param licenseId
     *            The license id.
     * @return Returns the original license text result.
     */
    Result<String> findLicenseText(UUID licenseId);

    /**
     * Finds many licenses.
     * 
     * Version: 4
     * 
     * Expected response codes
     * 200 OK
     * 
     * @param pageRequest
     *            The page request.
     * @return Returns the license page result.
     */
    Result<Page<License>> findManyLicenses(PageRequest pageRequest);

    /**
     * Finds a license term by its id.
     * 
     * Version: 4
     * 
     * Expected response codes
     * 200 OK
     * 404 Not Found
     * 
     * @param licenseTermId
     *            The license term id.
     * @return Returns the license term result.
     */
    Result<LicenseTerm> findLicenseTerm(UUID licenseTermId);
}
