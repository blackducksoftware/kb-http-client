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

import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import com.synopsys.bd.kb.httpclient.model.BdLicense;
import com.synopsys.kb.httpclient.api.HttpResult;
import com.synopsys.kb.httpclient.api.PageRequest;
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
     * Version: 4
     * 
     * Expected response codes
     * 200 OK
     * 404 Not Found
     * 
     * @param licenseId
     *            The license id.
     * @return Returns the Black Duck-centric license result.
     */
    HttpResult<BdLicense> findLicenseV4(UUID licenseId);

    /**
     * Finds many Black Duck-centric licenses.
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
     * @return Returns the Black Duck-centric license page result.
     */
    HttpResult<Page<BdLicense>> findManyLicensesV4(PageRequest pageRequest,
            @Nullable String searchTermFilter,
            @Nullable Map<String, String> filters);

    /**
     * Finds Black Duck-centric licenses that are associated to the given license term.
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
    HttpResult<Page<BdLicense>> findLicensesByLicenseTermV4(PageRequest pageRequest, UUID licenseTermId);
}
