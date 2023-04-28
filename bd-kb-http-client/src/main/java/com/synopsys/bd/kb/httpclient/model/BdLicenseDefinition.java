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
package com.synopsys.bd.kb.httpclient.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.synopsys.kb.httpclient.model.LicenseDefinition;
import com.synopsys.kb.httpclient.model.LicenseDefinitionType;

/**
 * Black Duck-centric license definition.
 * 
 * Used to convert HREF specific compositions of LicenseDefinition objects to full license representations.
 * 
 * @author skatzman
 */
public class BdLicenseDefinition {
    @Nullable
    private final BdLicense license;

    @Nullable
    private final LicenseDefinitionType type;

    private final List<BdLicenseDefinition> licenseDefinitions;

    /**
     * Constructs a license definition given a license.
     * 
     * Used to represent simple license definition structure of a single license.
     * 
     * @param license
     *            The license.
     */
    public BdLicenseDefinition(BdLicense license) {
        this.license = Objects.requireNonNull(license, "License must be initialized.");
        this.type = null;
        this.licenseDefinitions = ImmutableList.of();
    }

    /**
     * Constructs a license definition given a clause and 2 or more license definitions.
     * 
     * Used to represent complex license definition structure of many licenses.
     * 
     * @param type
     *            The type.
     * @param licenseDefinitions
     *            The license definitions.
     */
    public BdLicenseDefinition(LicenseDefinitionType type, List<BdLicenseDefinition> licenseDefinitions) {
        this.license = null;
        this.type = Objects.requireNonNull(type, "Type must be initialized.");

        Objects.requireNonNull(licenseDefinitions, "License definitions must be initialized.");
        Preconditions.checkArgument(licenseDefinitions.size() > 1, "License definitions must contains more than 1 license definition.");
        this.licenseDefinitions = ImmutableList.copyOf(licenseDefinitions);
    }

    public Optional<BdLicense> getLicense() {
        return Optional.ofNullable(license);
    }

    public Optional<LicenseDefinitionType> getType() {
        return Optional.ofNullable(type);
    }

    public List<BdLicenseDefinition> getLicenseDefinitions() {
        return licenseDefinitions;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLicense(), getType(), getLicenseDefinitions());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof BdLicenseDefinition) {
            BdLicenseDefinition otherBdLicenseDefinition = (BdLicenseDefinition) otherObject;

            return Objects.equals(getLicense(), otherBdLicenseDefinition.getLicense())
                    && Objects.equals(getType(), otherBdLicenseDefinition.getType())
                    && Objects.equals(getLicenseDefinitions(), otherBdLicenseDefinition.getLicenseDefinitions());
        }

        return false;
    }

    /**
     * Gets the set of license ids.
     * 
     * @return Returns the license ids.
     */
    public Set<UUID> getLicenseIds() {
        return getLicenseIds(0);
    }

    private Set<UUID> getLicenseIds(int depth) {
        if (depth >= LicenseDefinition.MAX_DEPTH) {
            // Have a max depth guard for traversing too deep into license trees as self-protection for extreme edge
            // cases or malformed data.
            return Collections.emptySet();
        }

        final ImmutableSet.Builder<UUID> builder = ImmutableSet.builder();

        // License consumer processes if this license definition composes a single license.
        Consumer<BdLicense> licenseConsumer = (bdLicense) -> {
            UUID licenseId = bdLicense.getId();
            builder.add(licenseId);
        };

        // License definition runnable processes if this license definition composes many licenses.
        Runnable licenseDefinitionRunnable = () -> {
            for (BdLicenseDefinition bdLicenseDefinition : getLicenseDefinitions()) {
                BdLicense bdLicense = bdLicenseDefinition.getLicense().orElse(null);
                if (bdLicense != null) {
                    // Composed license definition contains single license.
                    UUID licenseId = bdLicense.getId();
                    builder.add(licenseId);
                } else {
                    // Composed license definition contains many licenses.
                    int updatedDepth = depth + 1;
                    Set<UUID> licenseIds = bdLicenseDefinition.getLicenseIds(updatedDepth);
                    builder.addAll(licenseIds);
                }
            }
        };

        getLicense().ifPresentOrElse(licenseConsumer, licenseDefinitionRunnable);

        return builder.build();
    }
}
