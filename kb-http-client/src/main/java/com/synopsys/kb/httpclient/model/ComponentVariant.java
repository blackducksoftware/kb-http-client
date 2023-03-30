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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.synopsys.kb.httpclient.api.Relationship;

/**
 * Component variant representation.
 * 
 * A component variant cannot be migrated but its parent component version is eligible for migration. Due to this, it is
 * possible that the component version id pointer for a component variant can change at a future point in time via the
 * KnowledgeBase.
 * 
 * A component variant is eligible for soft deletion.
 * 
 * @author skatzman
 */
public class ComponentVariant extends AbstractEntity {
    private final String version;

    private final String externalNamespace;

    private final String externalId;

    private final boolean isExternalNamespaceDistribution;

    private final String packageUrl;

    private final String type;

    private final LicenseDefinition licenseDefinition;

    private final boolean isDeleted;

    private final boolean isComponentIntelligencePresent;

    private final Meta meta;

    public ComponentVariant(@JsonProperty("version") String version,
            @JsonProperty("externalNamespace") String externalNamespace,
            @JsonProperty("externalId") String externalId,
            @JsonProperty("externalNamespaceIsDistribution") Boolean externalNamespaceDistribution,
            @JsonProperty("packageUrl") String packageUrl,
            @JsonProperty("type") String type,
            @JsonProperty("license") LicenseDefinition licenseDefinition,
            @JsonProperty("deleted") Boolean deleted,
            @JsonProperty("hasComponentIntelligence") Boolean componentIntelligence,
            @JsonProperty("_meta") Meta meta) {
        this.version = version;
        this.externalNamespace = externalNamespace;
        this.isExternalNamespaceDistribution = (externalNamespaceDistribution != null) ? externalNamespaceDistribution.booleanValue() : false;
        this.externalId = externalId;
        this.packageUrl = packageUrl;
        this.type = type;
        this.licenseDefinition = licenseDefinition;
        this.isDeleted = (deleted != null) ? deleted.booleanValue() : false;
        this.isComponentIntelligencePresent = (componentIntelligence != null) ? componentIntelligence.booleanValue() : false;
        this.meta = meta;
    }

    /**
     * Gets the id.
     *
     * @return UUID Returns the id.
     * @throws IllegalArgumentException
     *             Throws if the id is absent or not a valid UUID.
     */
    @JsonIgnore
    public final UUID getId() {
        Meta meta = getMeta();

        return meta.getHrefId("variants")
                .map(UUID::fromString)
                .orElseThrow(() -> new IllegalArgumentException("Unable to get id because it is absent."));
    }

    @JsonIgnore
    public final UUID getComponentId() {
        Meta meta = getMeta();

        return meta.findUniqueLink(Relationship.COMPONENT)
                .map(Link::getHref)
                .map((componentHref) -> extractId(componentHref, "components").orElse(null))
                .map(UUID::fromString)
                .orElseThrow(() -> new IllegalArgumentException("Unable to get component id because it is absent."));
    }

    @JsonIgnore
    public final UUID getComponentVersionId() {
        Meta meta = getMeta();

        return meta.findUniqueLink(Relationship.VERSION)
                .map(Link::getHref)
                .map((componentHref) -> extractId(componentHref, "versions").orElse(null))
                .map(UUID::fromString)
                .orElseThrow(() -> new IllegalArgumentException("Unable to get component version id because it is absent."));
    }

    public String getVersion() {
        return version;
    }

    public Optional<String> getExternalNamespace() {
        return Optional.ofNullable(externalNamespace);
    }

    public Optional<String> getExternalId() {
        return Optional.of(externalId);
    }

    public boolean isExternalNamespaceDistribution() {
        return isExternalNamespaceDistribution;
    }

    public Optional<String> getPackageUrl() {
        return Optional.ofNullable(packageUrl);
    }

    public String getType() {
        return type;
    }

    public Optional<LicenseDefinition> getLicenseDefinition() {
        return Optional.ofNullable(licenseDefinition);
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    @JsonIgnore
    public boolean isComponentIntelligencePresent() {
        return isComponentIntelligencePresent;
    }

    @JsonProperty("hasComponentIntelligence")
    public boolean hasComponentIntelligence() {
        return isComponentIntelligencePresent();
    }

    public Meta getMeta() {
        return meta;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVersion(), getExternalNamespace(), getExternalId(), isExternalNamespaceDistribution(), getPackageUrl(), getType(),
                getLicenseDefinition(), isDeleted(), isComponentIntelligencePresent(), getMeta());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof ComponentVariant) {
            ComponentVariant otherComponentVariant = (ComponentVariant) otherObject;

            return Objects.equals(getVersion(), otherComponentVariant.getVersion())
                    && Objects.equals(getExternalNamespace(), otherComponentVariant.getExternalNamespace())
                    && Objects.equals(getExternalId(), otherComponentVariant.getExternalId())
                    && Objects.equals(isExternalNamespaceDistribution(), otherComponentVariant.isExternalNamespaceDistribution())
                    && Objects.equals(getPackageUrl(), otherComponentVariant.getPackageUrl())
                    && Objects.equals(getType(), otherComponentVariant.getType())
                    && Objects.equals(getLicenseDefinition(), otherComponentVariant.getLicenseDefinition())
                    && Objects.equals(isDeleted(), otherComponentVariant.isDeleted())
                    && Objects.equals(isComponentIntelligencePresent(), otherComponentVariant.isComponentIntelligencePresent())
                    && Objects.equals(getMeta(), otherComponentVariant.getMeta());
        }

        return false;
    }
}
