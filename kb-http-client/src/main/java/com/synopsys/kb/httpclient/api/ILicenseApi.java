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

import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import com.synopsys.kb.httpclient.model.License;
import com.synopsys.kb.httpclient.model.LicenseTerm;
import com.synopsys.kb.httpclient.model.LicenseVsl;
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
    HttpResult<License> findLicense(UUID licenseId);

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
    HttpResult<String> findLicenseText(UUID licenseId);

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
     * @param searchTermFilter
     *            The search term filter. Expected to be in the format bdsuite:suite-identifier or spdx:spdx-identifier.
     *            Searching is done as a case-sensitive prefix search, so a search query of spdx:Apache will match
     *            licenses with SPDX identifiers of Apache-1.0, Apache-1.1, and Apache-2.0. Optional.
     * @param filters
     *            The filters. Expected to be in the format field:value. Possible fields are codeSharing, ownership,
     *            restriction. Optional.
     * @return Returns the license page result.
     */
    HttpResult<Page<License>> findManyLicenses(PageRequest pageRequest,
            @Nullable String searchTermFilter,
            @Nullable Map<String, String> filters);

    /**
     * Finds licenses that are associated to the given license term.
     * 
     * Version: 4
     * 
     * Expected response codes
     * 200 OK
     * 404 Not Found
     * 
     * @param pageRequest
     *            The page request.
     * @param licenseTermId
     *            The license term id.
     * @return Returns the license page result.
     */
    HttpResult<Page<License>> findLicensesByLicenseTerm(PageRequest pageRequest, UUID licenseTermId);

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
    HttpResult<LicenseTerm> findLicenseTerm(UUID licenseTermId);

    /**
     * Finds many license terms.
     * 
     * Version: 4
     * 
     * Expected response codes
     * 200 OK
     * 
     * @param pageRequest
     *            The page request.
     * @return Returns the license term page result.
     */
    HttpResult<Page<LicenseTerm>> findManyLicenseTerms(PageRequest pageRequest);

    /**
     * Finds license terms for a given license.
     * 
     * Version: 4
     * 
     * Expected response codes
     * 200 OK
     * 404 Not Found
     * 
     * @param pageRequest
     *            The page request.
     * @param licenseId
     *            The license id.
     * @return Returns the license term page result.
     */
    HttpResult<Page<LicenseTerm>> findLicenseTermsByLicense(PageRequest pageRequest, UUID licenseId);

    /**
     * Finds many license VSLs.
     * 
     * Version: 4
     * 
     * Expected response codes
     * 200 OK
     * 
     * @param pageRequest
     *            The page request.
     * @return Returns the license VSL page result.
     */
    HttpResult<Page<LicenseVsl>> findManyLicenseVsls(PageRequest pageRequest);
}
