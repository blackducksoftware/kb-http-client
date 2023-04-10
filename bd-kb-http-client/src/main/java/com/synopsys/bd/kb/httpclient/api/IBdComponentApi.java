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
import com.synopsys.bd.kb.httpclient.model.BdComponentVersionSummary;
import com.synopsys.kb.httpclient.api.PageRequest;
import com.synopsys.kb.httpclient.model.Component;
import com.synopsys.kb.httpclient.model.Page;
import com.synopsys.kb.httpclient.model.VulnerabilityScorePriority;
import com.synopsys.kb.httpclient.model.VulnerabilitySourcePriority;

/**
 * Black Duck component API interface.
 * 
 * @author skatzman
 */
public interface IBdComponentApi {
    /**
     * Finds a component by its id.
     * 
     * - This API will attempt to follow migration links when present to return the final destination component. In the
     * case of a split migration, the first split moved link will be followed.
     * - As a defensive measure, this API will attempt to follow up to maximum ceiling of requests for migration
     * handling.
     * 
     * Expected response codes
     * 200 OK
     * 404 Not Found
     * 
     * Migration response codes
     * 300 Multiple Choices
     * 301 Moved Permanently
     * 
     * @param componentId
     *            The component id.
     * @return Returns the migratable component result.
     */
    MigratableResult<Component> find(UUID componentId);

    /**
     * Finds component versions for a given component.
     * 
     * - This API will attempt to follow migration links when present to return the final destination component version
     * page. In the case of a split migration, the first split moved link will be followed.
     * - As a defensive measure, this API will attempt to follow up to maximum ceiling of requests for migration
     * handling.
     * - Take precaution for differing migration responses when making multiple requests for different pages for the
     * same component id.
     * 
     * Expected response codes
     * 200 OK
     * 402 Payment Required
     * 403 Forbidden
     * 404 Not Found
     * 500 Internal Server Error
     * 
     * Migration response codes
     * 300 Multiple Choices
     * 301 Moved Permanently
     * 
     * @param pageRequest
     *            The page request.
     * @param componentId
     *            The component id.
     * @param searchTermFilter
     *            The search term filter. Optional.
     * @param vulnerabilitySourcePriority
     *            The vulnerability source priority.
     * @param vulnerabilityScorePriority
     *            The vulnerability score priority.
     * @return Returns the component version page result.
     */
    MigratableResult<Page<BdComponentVersion>> findComponentVersions(PageRequest pageRequest,
            UUID componentId,
            @Nullable String searchTermFilter,
            VulnerabilitySourcePriority vulnerabilitySourcePriority,
            VulnerabilityScorePriority vulnerabilityScorePriority);

    /**
     * Finds component versions for a given component.
     * 
     * - This API will attempt to follow migration links when present to return the final destination component version
     * page. In the case of a split migration, the first split moved link will be followed.
     * - As a defensive measure, this API will attempt to follow up to maximum ceiling of requests for migration
     * handling.
     * - Take precaution for differing migration responses when making multiple requests for different pages for the
     * same component id.
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
     * @param componentId
     *            The component id.
     * @param searchTermFilter
     *            The search term filter. Optional.
     * @return Returns the component version summary page result.
     */
    MigratableResult<Page<BdComponentVersionSummary>> findComponentVersionSummaries(PageRequest pageRequest,
            UUID componentId,
            @Nullable String searchTermFilter);
}
