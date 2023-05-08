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
import java.util.List;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

/**
 * List holder.
 * 
 * @author skatzman
 *
 * @param <T>
 *            The item type.
 */
public class ListHolder<T> {
    private final List<T> items;

    private final Meta meta;

    @JsonCreator
    public ListHolder(@JsonProperty("items") @Nullable Collection<T> items,
            @JsonProperty("_meta") Meta meta) {
        this.items = (items != null) ? ImmutableList.copyOf(items) : ImmutableList.of();
        this.meta = meta;
    }

    public List<T> getItems() {
        return items;
    }

    public Meta getMeta() {
        return meta;
    }
}
