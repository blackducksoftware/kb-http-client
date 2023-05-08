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
 * Component activity representation.
 * 
 * @author skatzman
 */
public class ComponentActivity extends AbstractActivity {
    private final String component;

    @JsonCreator
    public ComponentActivity(@JsonProperty("component") String component,
            @JsonProperty("updatedDate") OffsetDateTime updatedDate) {
        super(updatedDate);

        this.component = component;
    }

    /**
     * Gets the component id.
     * 
     * @return Returns the component id.
     */
    @JsonIgnore
    public UUID getComponentId() {
        return extractId(getComponent(), "components")
                .map(UUID::fromString)
                .orElseThrow(() -> new IllegalStateException("Unable to get component id because it is absent."));
    }

    public String getComponent() {
        return component;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getComponent(), getUpdatedDate());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof ComponentActivity) {
            ComponentActivity otherComponentActivity = (ComponentActivity) otherObject;

            return Objects.equals(getComponent(), otherComponentActivity.getComponent())
                    && Objects.equals(getUpdatedDate(), otherComponentActivity.getUpdatedDate());
        }

        return false;
    }
}
