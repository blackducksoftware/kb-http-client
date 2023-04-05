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

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Component variant activity representation.
 * 
 * @author skatzman
 */
public class ComponentVariantActivity extends AbstractActivity {
    private final String variant;

    @JsonCreator
    public ComponentVariantActivity(@JsonProperty("variant") String variant,
            @JsonProperty("updatedDate") OffsetDateTime updatedDate) {
        super(updatedDate);

        this.variant = variant;
    }

    /**
     * Gets the component variant id.
     * 
     * @return Returns the component variant id.
     */
    @JsonIgnore
    public UUID getComponentVariantId() {
        return extractId(getVariant(), "variants")
                .map(UUID::fromString)
                .orElseThrow(() -> new IllegalStateException("Unable to get component variant id because it is absent."));
    }

    public String getVariant() {
        return variant;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVariant(), getUpdatedDate());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof ComponentVariantActivity) {
            ComponentVariantActivity otherComponentVariantActivity = (ComponentVariantActivity) otherObject;

            return Objects.equals(getVariant(), otherComponentVariantActivity.getVariant())
                    && Objects.equals(getUpdatedDate(), otherComponentVariantActivity.getUpdatedDate());
        }

        return false;
    }
}
