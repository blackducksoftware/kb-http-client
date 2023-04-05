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

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import com.synopsys.kb.httpclient.model.ComponentActivity;
import com.synopsys.kb.httpclient.model.ListHolder;

/**
 * Activity API interface.
 * 
 * @author skatzman
 */
public interface IActivityApi {
    /**
     * Finds component activities.
     * 
     * @param componentIds
     *            The set of component ids. Must be not null, not empty, and less than or equal to 1000 components.
     * @param activitySince
     *            The activity since date. Must be not null.
     * @return Returns the component activity result.
     */
    Result<ListHolder<ComponentActivity>> findComponentActivities(Set<UUID> componentIds, OffsetDateTime activitySince);
}
