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
import com.synopsys.kb.httpclient.model.OngoingVersion;
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
     * @param componentId
     *            The component id.
     * @return Returns the component result.
     */
    MigratableHttpResult<Component> findComponentV4(UUID componentId);

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
     * Version: 4
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
     *            The search term filter. Expected to be in the format field:value. Searching is case-insensitive.
     *            Supported fields are version and releasedOn. Optional.
     * @param vulnerabilitySourcePriority
     *            The vulnerability source priority.
     * @param vulnerabilityScorePriority
     *            The vulnerability score priority.
     * @param excludeDeleted
     *            Excludes results that have been intentionally removed from the KnowledgeBase. Does not exclude by
     *            default. Optional.
     * @return Returns the component version page result.
     */
    MigratableHttpResult<Page<BdComponentVersion>> findComponentVersionsByComponentV4(PageRequest pageRequest,
            UUID componentId,
            @Nullable String searchTermFilter,
            VulnerabilitySourcePriority vulnerabilitySourcePriority,
            VulnerabilityScorePriority vulnerabilityScorePriority,
            @Nullable Boolean excludeDeleted);

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
     * Version: 2
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
     *            The search term filter. Expected to be in the format field:value. Searching is case-insensitive.
     *            Supported fields are version and releasedOn. Optional.
     * @param excludeDeleted
     *            Excludes results that have been intentionally removed from the KnowledgeBase. Does not exclude by
     *            default. Optional.
     * @return Returns the component version summary page result.
     */
    MigratableHttpResult<Page<BdComponentVersionSummary>> findComponentVersionSummariesByComponentV2(PageRequest pageRequest,
            UUID componentId,
            @Nullable String searchTermFilter,
            @Nullable Boolean excludeDeleted);

    /**
     * Finds the ongoing version for the given component.
     * 
     * - This API will attempt to follow migration links when present to return the final destination ongoing version.
     * In the case of a split migration, the first split moved link will be followed.
     * - As a defensive measure, this API will attempt to follow up to maximum ceiling of requests for migration
     * handling.
     * 
     * Version: 3
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
     * @return Returns the ongoing version result.
     */
    MigratableHttpResult<OngoingVersion> findOngoingVersionByComponentV3(UUID componentId);
}
