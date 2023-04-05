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
package com.synopsys.kb.httpclient.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

/**
 * Id holder representation.
 * 
 * @author skatzman
 */
public class IdHolder<T> {
    private final Set<T> ids;

    @JsonCreator
    public IdHolder(@JsonProperty("ids") Set<T> ids) {
        this.ids = (ids != null) ? ImmutableSet.copyOf(ids) : ImmutableSet.of();
    }

    public Set<T> getIds() {
        return ids;
    }
}
