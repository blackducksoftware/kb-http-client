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

import com.synopsys.kb.httpclient.model.Component;
import com.synopsys.kb.httpclient.model.ComponentSearchResult;
import com.synopsys.kb.httpclient.model.ComponentVersion;
import com.synopsys.kb.httpclient.model.ComponentVersionSummary;
import com.synopsys.kb.httpclient.model.Page;
import com.synopsys.kb.httpclient.model.VulnerabilityScorePriority;
import com.synopsys.kb.httpclient.model.VulnerabilitySourcePriority;

/**
 * Component API interface.
 * 
 * @author skatzman
 */
public interface IComponentApi {
    /**
     * Finds a component by its id.
     * 
     * This operation explicitly does NOT follow migration links.
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
    Result<Component> find(UUID componentId);

    /**
     * Finds component versions for a given component.
     * 
     * This operation explicitly does NOT follow migration links.
     * 
     * - This operation considers HTTP 500 Internal Server Error responses as an expected response code to reliably
     * accommodate an existing KB query timeout performance problem in requesting component versions for a component
     * with a very large number of associated component versions.
     * - Response codes of 402 Payment Required and 403 Forbidden are recommended to be gracefully handled as an absent
     * result. These response codes can occur if a KB request is made without the BDSA feature enabled within product
     * licensing.
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
     *            The search term filter. Optional.
     * @param vulnerabilitySourcePriority
     *            The vulnerability source priority.
     * @param vulnerabilityScorePriority
     *            The vulnerability score priority.
     * @param excludeDeleted
     *            Excludes results that have been intentionally removed from the KnowledgeBase. Does not exclude by
     *            default. Optional.
     * @return Returns the component version page result.
     */
    Result<Page<ComponentVersion>> findComponentVersionsByComponent(PageRequest pageRequest,
            UUID componentId,
            @Nullable String searchTermFilter,
            VulnerabilitySourcePriority vulnerabilitySourcePriority,
            VulnerabilityScorePriority vulnerabilityScorePriority,
            @Nullable Boolean excludeDeleted);

    /**
     * Finds component version summaries for a given component.
     * 
     * This operation explicitly does NOT follow migration links.
     * 
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
     *            The search term filter. Optional.
     * @param excludeDeleted
     *            Excludes results that have been intentionally removed from the KnowledgeBase. Does not exclude by
     *            default. Optional.
     * @return Returns the component version summary page result.
     */
    Result<Page<ComponentVersionSummary>> findComponentVersionSummariesByComponent(PageRequest pageRequest,
            UUID componentId,
            @Nullable String searchTermFilter,
            @Nullable Boolean excludeDeleted);

    /**
     * Search for components.
     * 
     * This operation is used to match package identifiers to components.
     * 
     * - Response codes of 400 Bad Request, 404 Not Found, and 410 Gone should be gracefully handled as an absent search
     * result.
     * 
     * Version: 3
     * 
     * Expected response codes
     * 200 OK
     * 400 Bad Request
     * 404 Not Found
     * 410 Gone
     * 
     * @param pageRequest
     *            The page request.
     * @param searchTermFilter
     *            The search term filter.
     * @param allowPartialMatches
     *            The allow partial matches flag. If enabled, fuzzy component results are supported. If disabled, only
     *            exact component results are supported.
     * @return Returns a component search result page result.
     */
    Result<Page<ComponentSearchResult>> search(PageRequest pageRequest, String searchTermFilter, boolean allowPartialMatches);
}
