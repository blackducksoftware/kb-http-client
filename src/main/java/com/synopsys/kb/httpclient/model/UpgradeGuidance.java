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
 * Upgrade guidance representation.
 * 
 * @author skatzman
 */
public class UpgradeGuidance extends AbstractEntity {
    private final String component;

    private final String version;

    private final String variant;

    private final String componentName;

    private final String versionName;

    private final String variantName;

    @Nullable
    private final String variantExternalNamespace;

    private final String variantExternalId;

    private final UpgradeGuidanceSuggestion shortTermSuggestion;

    private final UpgradeGuidanceSuggestion longTermSuggestion;

    private final Meta meta;

    @JsonCreator
    public UpgradeGuidance(@JsonProperty("component") String component,
            @JsonProperty("version") String version,
            @JsonProperty("variant") String variant,
            @JsonProperty("componentName") String componentName,
            @JsonProperty("versionName") String versionName,
            @JsonProperty("variantName") String variantName,
            @JsonProperty("variantExternalNamespace") String variantExternalNamespace,
            @JsonProperty("variantExternalId") String variantExternalId,
            @JsonProperty("shortTerm") UpgradeGuidanceSuggestion shortTermSuggestion,
            @JsonProperty("longTerm") UpgradeGuidanceSuggestion longTermSuggestion,
            @JsonProperty("_meta") Meta meta) {
        this.component = component;
        this.version = version;
        this.variant = variant;
        this.componentName = componentName;
        this.versionName = versionName;
        this.variantName = variantName;
        this.variantExternalNamespace = variantExternalNamespace;
        this.variantExternalId = variantExternalId;
        this.shortTermSuggestion = shortTermSuggestion;
        this.longTermSuggestion = longTermSuggestion;
        this.meta = meta;
    }

    public String getComponent() {
        return component;
    }

    public Optional<UUID> getComponentId() {
        return extractId(getComponent(), "components").map(UUID::fromString);
    }

    public String getVersion() {
        return version;
    }

    public Optional<UUID> getVersionId() {
        return extractId(getVersion(), "versions").map(UUID::fromString);
    }

    public Optional<String> getVariant() {
        return Optional.ofNullable(variant);
    }

    public Optional<UUID> getVariantId() {
        return extractId(getVariant().orElse(null), "variants").map(UUID::fromString);
    }

    public String getComponentName() {
        return componentName;
    }

    public String getVersionName() {
        return versionName;
    }

    public Optional<String> getVariantName() {
        return Optional.ofNullable(variantName);
    }

    public Optional<String> getVariantExternalNamespace() {
        return Optional.ofNullable(variantExternalNamespace);
    }

    public Optional<String> getVariantExternalId() {
        return Optional.ofNullable(variantExternalId);
    }

    public Optional<UpgradeGuidanceSuggestion> getShortTermSuggestion() {
        return Optional.ofNullable(shortTermSuggestion);
    }

    public Optional<UpgradeGuidanceSuggestion> getLongTermSuggestion() {
        return Optional.ofNullable(longTermSuggestion);
    }

    public Meta getMeta() {
        return meta;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVersion(), getVersionName(), getVariant(), getVariantName(), getVariantExternalNamespace(), getVariantExternalId());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof UpgradeGuidance) {
            UpgradeGuidance otherUpgradeGuidance = (UpgradeGuidance) otherObject;

            return Objects.equals(getComponent(), otherUpgradeGuidance.getComponent())
                    && Objects.equals(getVersion(), otherUpgradeGuidance.getVersion())
                    && Objects.equals(getVariant(), otherUpgradeGuidance.getVariant())
                    && Objects.equals(getComponentName(), otherUpgradeGuidance.getComponentName())
                    && Objects.equals(getVersionName(), otherUpgradeGuidance.getVersionName())
                    && Objects.equals(getVariantExternalNamespace(), otherUpgradeGuidance.getVariantExternalNamespace())
                    && Objects.equals(getVariantExternalId(), otherUpgradeGuidance.getVariantExternalId())
                    && Objects.equals(getShortTermSuggestion(), otherUpgradeGuidance.getShortTermSuggestion())
                    && Objects.equals(getLongTermSuggestion(), otherUpgradeGuidance.getLongTermSuggestion())
                    && Objects.equals(getMeta(), otherUpgradeGuidance.getMeta());
        }

        return false;
    }
}
