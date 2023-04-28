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

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * License definition representation.
 * 
 * @author skatzman
 */
public class LicenseDefinition extends AbstractEntity {
    /**
     * The default maximum depth that should be traversed for a multi-depth license definition as an internal
     * self-protection.
     */
    public static final int MAX_DEPTH = 10;

    private final LicenseDefinitionType type;

    private final List<LicenseDefinitionItem> items;

    @JsonCreator
    public LicenseDefinition(@JsonProperty("type") LicenseDefinitionType type,
            @JsonProperty("items") List<LicenseDefinitionItem> items) {
        this.type = type;
        this.items = (items != null) ? ImmutableList.copyOf(items) : ImmutableList.of();
    }

    public LicenseDefinitionType getType() {
        return type;
    }

    public List<LicenseDefinitionItem> getItems() {
        return items;
    }

    /**
     * Gets the set of license ids.
     * 
     * @return Returns the license ids.
     */
    @JsonIgnore
    public Set<UUID> getLicenseIds() {
        return getLicenseIds(0);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getItems());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof LicenseDefinition) {
            LicenseDefinition otherLicenseDefinition = (LicenseDefinition) otherObject;

            return Objects.equals(getType(), otherLicenseDefinition.getType())
                    && Objects.equals(getItems(), otherLicenseDefinition.getItems());
        }

        return false;
    }

    private Set<UUID> getLicenseIds(int depth) {
        if (depth >= MAX_DEPTH) {
            // Have a max depth guard for traversing too deep into KB license trees as self-protection for extreme edge
            // cases or malformed data.
            return Collections.emptySet();
        }

        ImmutableSet.Builder<UUID> builder = ImmutableSet.builder();

        for (LicenseDefinitionItem item : getItems()) {
            UUID licenseId = item.getLicenseId().orElse(null);
            if (licenseId != null) {
                builder = builder.add(licenseId);
            } else {
                int updatedDepth = depth + 1;
                Set<UUID> itemLicenseIds = item.getLicenseDefinition().map((licenseDefinition) -> licenseDefinition.getLicenseIds(updatedDepth))
                        .orElse(Collections.emptySet());
                builder = builder.addAll(itemLicenseIds);
            }
        }

        return builder.build();
    }
}
