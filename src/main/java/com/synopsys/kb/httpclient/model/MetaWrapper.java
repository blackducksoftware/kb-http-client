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

import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Meta wrapper representation.
 * 
 * @author skatzman
 */
public class MetaWrapper {
    private final Meta meta;

    @JsonCreator
    public MetaWrapper(@JsonProperty("_meta") Meta meta) {
        this.meta = meta;
    }

    public Optional<Meta> getMeta() {
        return Optional.ofNullable(meta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMeta());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof MetaWrapper) {
            MetaWrapper otherMetaWrapper = (MetaWrapper) otherObject;

            return Objects.equals(getMeta(), otherMetaWrapper.getMeta());
        }

        return false;
    }
}
