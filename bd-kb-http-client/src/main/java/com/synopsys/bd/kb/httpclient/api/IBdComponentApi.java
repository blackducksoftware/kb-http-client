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

import com.synopsys.kb.httpclient.model.Component;

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
}
