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
 * Component search result.
 * 
 * @author skatzman
 */
public class ComponentSearchResult extends AbstractEntity {
    private final String componentName;

    private final String versionName;

    private final String externalId;

    // HREF
    private final String component;

    // HREF
    private final String version;

    // HREF
    private final String variant;

    private final boolean partialMatch;

    private final Meta meta;

    @JsonCreator
    public ComponentSearchResult(@JsonProperty("componentName") String componentName,
            @JsonProperty("versionName") @Nullable String versionName,
            @JsonProperty("externalId") @Nullable String externalId,
            @JsonProperty("component") String component,
            @JsonProperty("version") @Nullable String version,
            @JsonProperty("variant") @Nullable String variant,
            @JsonProperty("partialMatch") Boolean partialMatch,
            @JsonProperty("_meta") Meta meta) {
        this.componentName = componentName;
        this.versionName = versionName;
        this.externalId = externalId;
        this.component = component;
        this.version = version;
        this.variant = variant;
        this.partialMatch = (partialMatch != null) ? partialMatch.booleanValue() : false;
        this.meta = meta;
    }

    public String getComponentName() {
        return componentName;
    }

    public Optional<String> getVersionName() {
        return Optional.ofNullable(versionName);
    }

    public Optional<String> getExternalId() {
        return Optional.ofNullable(externalId);
    }

    public String getComponent() {
        return component;
    }

    public Optional<String> getVersion() {
        return Optional.ofNullable(version);
    }

    public Optional<String> getVariant() {
        return Optional.ofNullable(variant);
    }

    public boolean isPartialMatch() {
        return partialMatch;
    }

    public Optional<UUID> getComponentId() {
        return extractId(getComponent(), "components").map(UUID::fromString);
    }

    public Optional<UUID> getVersionId() {
        return extractId(getVersion().orElse(null), "versions").map(UUID::fromString);
    }

    public Optional<UUID> getVariantId() {
        return extractId(getVariant().orElse(null), "variants").map(UUID::fromString);
    }

    public Meta getMeta() {
        return meta;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getComponentName(),
                getVersionName(),
                getExternalId(),
                getComponent(),
                getVersion(),
                getVariant(),
                isPartialMatch(),
                getMeta());
    }

    @Override
    public boolean equals(@Nullable Object otherObject) {
        boolean result = false;

        if (otherObject instanceof ComponentSearchResult) {
            ComponentSearchResult otherComponentSearchResult = (ComponentSearchResult) otherObject;

            result = Objects.equals(getComponentName(), otherComponentSearchResult.getComponentName())
                    && Objects.equals(getVersionName(), otherComponentSearchResult.getVersionName())
                    && Objects.equals(getExternalId(), otherComponentSearchResult.getExternalId())
                    && Objects.equals(getComponent(), otherComponentSearchResult.getComponent())
                    && Objects.equals(getVersion(), otherComponentSearchResult.getVersion())
                    && Objects.equals(getVariant(), otherComponentSearchResult.getVariant())
                    && Objects.equals(isPartialMatch(), otherComponentSearchResult.isPartialMatch())
                    && Objects.equals(getMeta(), otherComponentSearchResult.getMeta());
        }

        return result;
    }
}
