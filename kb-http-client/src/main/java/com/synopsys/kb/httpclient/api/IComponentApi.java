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

import com.synopsys.kb.httpclient.model.Component;
import com.synopsys.kb.httpclient.model.ComponentSearchResult;
import com.synopsys.kb.httpclient.model.Page;

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
     * Search for components.
     * 
     * This operation is used to match package identifiers to components.
     * 
     * - Response codes of 400 Bad Request, 404 Not Found, and 410 Gone should be gracefully handled as an absent search
     * result.
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
