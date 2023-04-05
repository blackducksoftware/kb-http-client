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
 * Component version activity representation.
 * 
 * @author skatzman
 */
public class ComponentVersionActivity extends AbstractActivity {
    private final String version;

    @JsonCreator
    public ComponentVersionActivity(@JsonProperty("version") String version,
            @JsonProperty("updatedDate") OffsetDateTime updatedDate) {
        super(updatedDate);

        this.version = version;
    }

    /**
     * Gets the component version id.
     * 
     * @return Returns the component version id.
     */
    @JsonIgnore
    public UUID getComponentVersionId() {
        return extractId(getVersion(), "versions")
                .map(UUID::fromString)
                .orElseThrow(() -> new IllegalStateException("Unable to get component version id because it is absent."));
    }

    public String getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVersion(), getUpdatedDate());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof ComponentVersionActivity) {
            ComponentVersionActivity otherComponentVersionActivity = (ComponentVersionActivity) otherObject;

            return Objects.equals(getVersion(), otherComponentVersionActivity.getVersion())
                    && Objects.equals(getUpdatedDate(), otherComponentVersionActivity.getUpdatedDate());
        }

        return false;
    }
}
