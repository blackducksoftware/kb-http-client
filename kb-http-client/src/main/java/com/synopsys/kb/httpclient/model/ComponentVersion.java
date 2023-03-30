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
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.synopsys.kb.httpclient.api.Relationship;

/**
 * Component version representation.
 * 
 * A component version can be migrated via merge or split migration mechanisms via the KnowledgeBase.
 * 
 * A component version is eligible for soft deletion.
 * 
 * @author skatzman
 */
public class ComponentVersion extends AbstractEntity {
    private final String version;

    private final OffsetDateTime releasedOn;

    private final LicenseDefinition licenseDefinition;

    private final RiskProfile riskProfile;

    private final boolean isDeleted;

    private final boolean isComponentIntelligencePresent;

    private final Meta meta;

    @JsonCreator
    public ComponentVersion(@JsonProperty("version") String version,
            @JsonProperty("releasedOn") OffsetDateTime releasedOn,
            @JsonProperty("license") LicenseDefinition licenseDefinition,
            @JsonProperty("riskProfile") RiskProfile riskProfile,
            @JsonProperty("deleted") Boolean deleted,
            @JsonProperty("hasComponentIntelligence") Boolean componentIntelligence,
            @JsonProperty("_meta") Meta meta) {
        this.version = version;
        this.releasedOn = releasedOn;
        this.licenseDefinition = licenseDefinition;
        this.riskProfile = (riskProfile != null) ? riskProfile : new RiskProfile();
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

        return meta.getHrefId("versions")
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

    public Optional<String> getVersion() {
        return Optional.ofNullable(version);
    }

    public Optional<OffsetDateTime> getReleasedOn() {
        return Optional.ofNullable(releasedOn);
    }

    public Optional<LicenseDefinition> getLicenseDefinition() {
        return Optional.ofNullable(licenseDefinition);
    }

    public RiskProfile getRiskProfile() {
        return riskProfile;
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
        return Objects.hash(getVersion(), getReleasedOn(), getLicenseDefinition(), getRiskProfile(), isDeleted(), isComponentIntelligencePresent(), getMeta());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof ComponentVersion) {
            ComponentVersion otherComponentVersion = (ComponentVersion) otherObject;

            return Objects.equals(getVersion(), otherComponentVersion.getVersion())
                    && Objects.equals(getReleasedOn(), otherComponentVersion.getReleasedOn())
                    && Objects.equals(getLicenseDefinition(), otherComponentVersion.getLicenseDefinition())
                    && Objects.equals(getRiskProfile(), otherComponentVersion.getRiskProfile())
                    && Objects.equals(isDeleted(), otherComponentVersion.isDeleted())
                    && Objects.equals(isComponentIntelligencePresent(), otherComponentVersion.isComponentIntelligencePresent())
                    && Objects.equals(getMeta(), otherComponentVersion.getMeta());
        }

        return false;
    }
}
