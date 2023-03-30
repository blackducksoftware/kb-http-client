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

import com.synopsys.kb.httpclient.model.BdsaVulnerability;
import com.synopsys.kb.httpclient.model.ComponentVersion;
import com.synopsys.kb.httpclient.model.CveVulnerability;
import com.synopsys.kb.httpclient.model.Page;
import com.synopsys.kb.httpclient.model.UpgradeGuidance;
import com.synopsys.kb.httpclient.model.VulnerabilityScorePriority;
import com.synopsys.kb.httpclient.model.VulnerabilitySourcePriority;

/**
 * Component version API interface.
 * 
 * @author skatzman
 */
public interface IComponentVersionApi {
    /**
     * Finds a component version by its id.
     * 
     * Response codes of 402 Payment Required and 403 Forbidden are recommended to be gracefully handled as an absent
     * result. These response codes can occur if a KB request is made without the BDSA feature enabled within product
     * licensing.
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
    Result<ComponentVersion> find(UUID componentVersionId,
            VulnerabilitySourcePriority vulnerabilitySourcePriority,
            VulnerabilityScorePriority vulnerabilityScorePriority);

    /**
     * Finds the CVE vulnerabilities associated to the given component version.
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
     * @return Returns the CVE vulnerability page result.
     */
    Result<Page<CveVulnerability>> findCveVulnerabilities(PageRequest pageRequest, UUID componentVersionId);

    /**
     * Finds the BDSA vulnerabilities associated to the given component version.
     * 
     * Response codes of 402 Payment Required and 403 Forbidden are recommended to be gracefully handled as an absent
     * result. These response codes can occur if a KB request is made without the BDSA feature enabled within product
     * licensing.
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
     * @return Returns the BDSA vulnerability page result.
     */
    Result<Page<BdsaVulnerability>> findBdsaVulnerabilities(PageRequest pageRequest, UUID componentVersionId);

    /**
     * Finds upgrade guidance for the given component version.
     * 
     * Response codes of 402 Payment Required and 403 Forbidden are recommended to be gracefully handled as an absent
     * result. These response codes can occur if a KB request is made without the BDSA feature enabled within product
     * licensing.
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
    Result<UpgradeGuidance> findUpgradeGuidance(UUID componentVersionId,
            VulnerabilitySourcePriority vulnerabilitySourcePriority,
            VulnerabilityScorePriority vulnerabilityScorePriority);
}