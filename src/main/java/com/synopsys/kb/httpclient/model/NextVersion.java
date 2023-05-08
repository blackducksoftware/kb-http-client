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
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Next version representation.
 * 
 * @author skatzman
 */
public class NextVersion extends AbstractEntity {
    private final int nextVersionsIncludingDeletedCount;

    private final int nextVersionsExcludingDeletedCount;

    private final Meta meta;

    @JsonCreator
    public NextVersion(@JsonProperty("nextVersionsIncludingDeletedCount") Integer nextVersionsIncludingDeletedCount,
            @JsonProperty("nextVersionsExcludingDeletedCount") Integer nextVersionsExcludingDeletedCount,
            @JsonProperty("_meta") Meta meta) {
        this.nextVersionsIncludingDeletedCount = (nextVersionsIncludingDeletedCount != null) ? nextVersionsIncludingDeletedCount.intValue() : 0;
        this.nextVersionsExcludingDeletedCount = (nextVersionsExcludingDeletedCount != null) ? nextVersionsExcludingDeletedCount.intValue() : 0;
        this.meta = meta;
    }

    /**
     * Gets the component version id.
     *
     * @return UUID Returns the component version id.
     * @throws IllegalArgumentException
     *             Throws if the id is absent or not a valid UUID.
     */
    @JsonIgnore
    public final UUID getComponentVersionId() {
        Meta meta = getMeta();

        return meta.getHrefId("versions")
                .map(UUID::fromString)
                .orElseThrow(() -> new IllegalArgumentException("Unable to get component version id because it is absent."));
    }

    public int getNextVersionsIncludingDeletedCount() {
        return nextVersionsIncludingDeletedCount;
    }

    public int getNextVersionsExcludingDeletedCount() {
        return nextVersionsExcludingDeletedCount;
    }

    public Meta getMeta() {
        return meta;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNextVersionsIncludingDeletedCount(), getNextVersionsExcludingDeletedCount(), getMeta());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof NextVersion) {
            NextVersion otherNextVersion = (NextVersion) otherObject;

            return Objects.equals(getNextVersionsIncludingDeletedCount(), otherNextVersion.getNextVersionsIncludingDeletedCount())
                    && Objects.equals(getNextVersionsExcludingDeletedCount(), otherNextVersion.getNextVersionsExcludingDeletedCount())
                    && Objects.equals(getMeta(), otherNextVersion.getMeta());
        }

        return false;
    }
}
