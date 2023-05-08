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

import javax.annotation.Nullable;

import com.synopsys.kb.httpclient.model.BdsaVulnerability;
import com.synopsys.kb.httpclient.model.ComponentVariant;
import com.synopsys.kb.httpclient.model.CveVulnerability;
import com.synopsys.kb.httpclient.model.Page;
import com.synopsys.kb.httpclient.model.UpgradeGuidance;
import com.synopsys.kb.httpclient.model.VulnerabilityScorePriority;
import com.synopsys.kb.httpclient.model.VulnerabilitySourcePriority;

/**
 * Component variant API interface.
 * 
 * @author skatzman
 */
public interface IComponentVariantApi {
    /**
     * Finds a component variant by its id.
     * 
     * Version: 4
     * 
     * Expected response codes
     * 200 OK
     * 404 Not Found
     * 
     * @param componentVariantId
     *            The component variant id.
     * @return Returns the component variant result.
     */
    HttpResult<ComponentVariant> findComponentVariantV4(UUID componentVariantId);

    /**
     * Finds the CVE vulnerabilities associated to the given component variant.
     *
     * - Raw unfiltered CVE vulnerability representations are returned.
     * 
     * Version: 7
     * 
     * Expected response codes
     * 200 OK
     * 404 Not Found
     * 
     * @param pageRequest
     *            The page request.
     * @param componentVariantId
     *            The component variant id.
     * @param searchTermFilter
     *            The search term filter. Expected to be in the format field:value. Searching is case-insensitive.
     *            Supported fields are id and description. Optional.
     * @return Returns the CVE vulnerability page result.
     */
    HttpResult<Page<CveVulnerability>> findCveVulnerabilitiesV7(PageRequest pageRequest,
            UUID componentVariantId,
            @Nullable String searchTermFilter);

    /**
     * Finds the BDSA vulnerabilities associated to the given component variant.
     * 
     * - Raw unfiltered BDSA vulnerability representations are returned.
     * - Response codes of 402 Payment Required and 403 Forbidden are recommended to be gracefully handled as an absent
     * result. These response codes can occur if a KB request is made without the BDSA feature enabled within product
     * licensing.
     * 
     * Version: 7
     * 
     * Expected response codes
     * 200 OK
     * 402 Payment Required
     * 403 Forbidden
     * 404 Not Found
     * 
     * @param pageRequest
     *            The page request.
     * @param componentVariantId
     *            The component variant id.
     * @param searchTermFilter
     *            The search term filter. Expected to be in the format field:value. Searching is case-insensitive.
     *            Supported fields are id and description. Optional.
     * @return Returns the BDSA vulnerability page result.
     */
    HttpResult<Page<BdsaVulnerability>> findBdsaVulnerabilitiesV7(PageRequest pageRequest,
            UUID componentVariantId,
            @Nullable String searchTermFilter);

    /**
     * Finds upgrade guidance for the given component variant.
     * 
     * - Response codes of 402 Payment Required and 403 Forbidden are recommended to be gracefully handled as an absent
     * result. These response codes can occur if a KB request is made without the BDSA feature enabled within product
     * licensing.
     *
     * Version: 4
     * 
     * Expected response codes
     * 200 OK
     * 402 Payment Required
     * 403 Forbidden
     * 404 Not Found
     * 
     * @param componentVariantId
     *            The component variant id.
     * @param vulnerabilitySourcePriority
     *            The vulnerability source priority.
     * @param vulnerabilityScorePriority
     *            The vulnerability score priority.
     * @return Returns the upgrade guidance result.
     */
    HttpResult<UpgradeGuidance> findUpgradeGuidanceV4(UUID componentVariantId,
            VulnerabilitySourcePriority vulnerabilitySourcePriority,
            VulnerabilityScorePriority vulnerabilityScorePriority);

    /**
     * Finds transitive upgrade guidance for the given component variant.
     * 
     * The provided component variant should be the direct component variant that is directly relevant to the transitive
     * upgrade guidance.
     * 
     * For example, if ComponentVariant1 depends on ComponentVariant2, it is relevant to fetch transitive upgrade
     * guidance for ComponentVariant1 as transitive upgrade guidance for ComponentVariant1 and all of its dependency
     * child components. As a result, from the perspective of a transitively identified component variant, it is
     * possible that it has multiple transitive upgrade guidance representations if that component variant was
     * identified via multiple direct component variants.
     * 
     * - Response codes of 402 Payment Required and 403 Forbidden are recommended to be gracefully handled as an absent
     * result. These response codes can occur if a KB request is made without the BDSA feature enabled within product
     * licensing.
     * 
     * Version: 4
     * 
     * Expected response codes
     * 200 OK
     * 402 Payment Required
     * 403 Forbidden
     * 404 Not Found
     * 
     * @param componentVariantId
     *            The component variant id.
     * @param vulnerabilitySourcePriority
     *            The vulnerability source priority.
     * @param vulnerabilityScorePriority
     *            The vulnerability score priority.
     * @return Returns the upgrade guidance result.
     */
    HttpResult<UpgradeGuidance> findTransitiveUpgradeGuidanceV4(UUID componentVariantId,
            VulnerabilitySourcePriority vulnerabilitySourcePriority,
            VulnerabilityScorePriority vulnerabilityScorePriority);
}
