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

import javax.annotation.Nullable;

import com.synopsys.bd.kb.httpclient.model.BdComponentVersion;
import com.synopsys.kb.httpclient.api.PageRequest;
import com.synopsys.kb.httpclient.model.BdsaVulnerability;
import com.synopsys.kb.httpclient.model.CveVulnerability;
import com.synopsys.kb.httpclient.model.NextVersion;
import com.synopsys.kb.httpclient.model.Page;
import com.synopsys.kb.httpclient.model.UpgradeGuidance;
import com.synopsys.kb.httpclient.model.VulnerabilityScorePriority;
import com.synopsys.kb.httpclient.model.VulnerabilitySourcePriority;

/**
 * Black Duck-centric component version API interface.
 * 
 * @author skatzman
 */
public interface IBdComponentVersionApi {
    /**
     * Finds a component version by its id and follows migration links.
     * 
     * - This API will attempt to follow migration links when present to return the final destination component. In the
     * case of a split migration, the first split moved link will be followed.
     * - As a defensive measure, this API will attempt to follow up to maximum ceiling of requests for migration
     * handling.
     * 
     * Version: 4
     * 
     * Expected response codes
     * 200 OK
     * 402 Payment Required
     * 403 Forbidden
     * 404 Not Found
     * 
     * Migration response codes
     * 300 Multiple Choices
     * 301 Moved Permanently
     * 
     * @param componentVersionId
     *            The component version id.
     * @param vulnerabilitySourcePriority
     *            The vulnerability source priority.
     * @param vulnerabilityScorePriority
     *            The vulnerability score priority.
     * @return Returns the component version result.
     */
    MigratableHttpResult<BdComponentVersion> findComponentVersionV4(UUID componentVersionId,
            VulnerabilitySourcePriority vulnerabilitySourcePriority,
            VulnerabilityScorePriority vulnerabilityScorePriority);

    /**
     * Finds a next version for the given component version and follows migration links.
     * 
     * - This API will attempt to follow migration links when present to return the final destination next version. In
     * the case of a split migration, the first split moved link will be followed.
     * - As a defensive measure, this API will attempt to follow up to maximum ceiling of requests for migration
     * handling.
     * 
     * Version: 4
     * 
     * Expected response codes
     * 200 OK
     * 404 Not Found
     * 
     * Migration response codes
     * 300 Multiple Choices
     * 301 Moved Permanently
     * 
     * @param componentVersionId
     *            The component version id.
     * @return Returns the next version result.
     */
    MigratableHttpResult<NextVersion> findNextVersionV4(UUID componentVersionId);

    /**
     * Finds the CVE vulnerabilities associated to the given component version and follows migration links.
     *
     * - Raw unfiltered CVE vulnerability representations are returned.
     * - This API will attempt to follow migration links when present to return the final destination component. In the
     * case of a split migration, the first split moved link will be followed.
     * - As a defensive measure, this API will attempt to follow up to maximum ceiling of requests for migration
     * handling.
     * - Take precaution for differing migration responses when making multiple requests for different pages for the
     * same component version id.
     * 
     * Version: 7
     * 
     * Expected response codes
     * 200 OK
     * 404 Not Found
     * 
     * Migration response codes
     * 300 Multiple Choices
     * 301 Moved Permanently
     * 
     * @param pageRequest
     *            The page request.
     * @param componentVersionId
     *            The component version id.
     * @param searchTermFilter
     *            The search term filter. Expected to be in the format field:value. Searching is case-insensitive.
     *            Supported fields are id and description. Optional.
     * @return Returns the CVE vulnerability page result.
     */
    MigratableHttpResult<Page<CveVulnerability>> findCveVulnerabilitiesV7(PageRequest pageRequest,
            UUID componentVersionId,
            @Nullable String searchTermFilter);

    /**
     * Finds the BDSA vulnerabilities associated to the given component version and follows migration links.
     * 
     * - Raw unfiltered BDSA vulnerability representations are returned.
     * - This API will attempt to follow migration links when present to return the final destination component. In the
     * case of a split migration, the first split moved link will be followed.
     * - As a defensive measure, this API will attempt to follow up to maximum ceiling of requests for migration
     * handling.
     * - Take precaution for differing migration responses when making multiple requests for different pages for the
     * same component version id.
     * 
     * Version: 7
     * 
     * Expected response codes
     * 200 OK
     * 402 Payment Required
     * 403 Forbidden
     * 404 Not Found
     * 
     * Migration response codes
     * 300 Multiple Choices
     * 301 Moved Permanently
     * 
     * @param pageRequest
     *            The page request.
     * @param componentVersionId
     *            The component version id.
     * @param searchTermFilter
     *            The search term filter. Expected to be in the format field:value. Searching is case-insensitive.
     *            Supported fields are id and description. Optional.
     * @return Returns the BDSA vulnerability page result.
     */
    MigratableHttpResult<Page<BdsaVulnerability>> findBdsaVulnerabilitiesV7(PageRequest pageRequest,
            UUID componentVersionId,
            @Nullable String searchTermFilter);

    /**
     * Finds upgrade guidance for the given component version and follow migration links.
     * 
     * - This API will attempt to follow migration links when present to return the final destination component. In the
     * case of a split migration, the first split moved link will be followed.
     * - As a defensive measure, this API will attempt to follow up to maximum ceiling of requests for migration
     * handling.
     *
     * Version: 4
     * 
     * Expected response codes
     * 200 OK
     * 402 Payment Required
     * 403 Forbidden
     * 404 Not Found
     * 
     * Migration response codes
     * 300 Multiple Choices
     * 301 Moved Permanently
     * 
     * @param componentVersionId
     *            The component version id.
     * @param vulnerabilitySourcePriority
     *            The vulnerability source priority.
     * @param vulnerabilityScorePriority
     *            The vulnerability score priority.
     * @return Returns the upgrade guidance result.
     */
    MigratableHttpResult<UpgradeGuidance> findUpgradeGuidanceV4(UUID componentVersionId,
            VulnerabilitySourcePriority vulnerabilitySourcePriority,
            VulnerabilityScorePriority vulnerabilityScorePriority);
}
