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
import java.util.UUID;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Upgrade guidance suggestion representation.
 * 
 * @author skatzman
 */
public class UpgradeGuidanceSuggestion extends AbstractEntity {
    private final String version;

    private final String versionName;

    private final String variant;

    private final String variantName;

    @Nullable
    private final String variantExternalNamespace;

    private final String variantExternalId;

    private final RiskProfile riskProfile;

    @JsonCreator
    public UpgradeGuidanceSuggestion(@JsonProperty("version") String version,
            @JsonProperty("versionName") String versionName,
            @JsonProperty("variant") String variant,
            @JsonProperty("variantName") String variantName,
            @JsonProperty("variantExternalNamespace") String variantExternalNamespace,
            @JsonProperty("variantExternalId") String variantExternalId,
            @JsonProperty("riskProfile") RiskProfile riskProfile) {
        this.version = version;
        this.versionName = versionName;
        this.variant = variant;
        this.variantName = variantName;
        this.variantExternalNamespace = variantExternalNamespace;
        this.variantExternalId = variantExternalId;
        this.riskProfile = riskProfile;
    }

    public String getVersion() {
        return version;
    }

    public Optional<UUID> getVersionId() {
        return extractId(getVersion(), "versions").map(UUID::fromString);
    }

    public String getVersionName() {
        return versionName;
    }

    public String getVariant() {
        return variant;
    }

    public Optional<UUID> getVariantId() {
        return extractId(getVariant(), "variants").map(UUID::fromString);
    }

    public String getVariantName() {
        return variantName;
    }

    public Optional<String> getVariantExternalNamespace() {
        return Optional.ofNullable(variantExternalNamespace);
    }

    public String getVariantExternalId() {
        return variantExternalId;
    }

    public RiskProfile getRiskProfile() {
        return riskProfile;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVersion(), getVersionName(), getVariant(), getVariantName(), getVariantExternalNamespace(), getVariantExternalId(),
                getRiskProfile());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof UpgradeGuidanceSuggestion) {
            UpgradeGuidanceSuggestion otherUpgradeGuidanceSuggestion = (UpgradeGuidanceSuggestion) otherObject;

            return Objects.equals(getVersion(), otherUpgradeGuidanceSuggestion.getVersion())
                    && Objects.equals(getVersionName(), otherUpgradeGuidanceSuggestion.getVersionName())
                    && Objects.equals(getVariant(), otherUpgradeGuidanceSuggestion.getVariant())
                    && Objects.equals(getVariantName(), otherUpgradeGuidanceSuggestion.getVariantName())
                    && Objects.equals(getVariantExternalNamespace(), otherUpgradeGuidanceSuggestion.getVariantExternalNamespace())
                    && Objects.equals(getVariantExternalId(), otherUpgradeGuidanceSuggestion.getVariantExternalId())
                    && Objects.equals(getRiskProfile(), otherUpgradeGuidanceSuggestion.getRiskProfile());
        }

        return false;
    }
}
