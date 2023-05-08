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

import java.util.Collection;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Page.
 * 
 * @author skatzman
 *
 * @param <T>
 *            The item type.
 */
public class Page<T> extends ListHolder<T> {
    private final int totalCount;

    @JsonCreator
    public Page(@JsonProperty("totalCount") @Nullable Integer totalCount,
            @JsonProperty("items") @Nullable Collection<T> items,
            @JsonProperty("_meta") Meta meta) {
        super(items, meta);

        this.totalCount = (totalCount != null) ? totalCount.intValue() : 0;
    }

    public int getTotalCount() {
        return totalCount;
    }
}
