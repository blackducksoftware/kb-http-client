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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Component version representation.
 * 
 * A component version can be migrated via merge or split migration mechanisms via the KnowledgeBase.
 * 
 * A component version is eligible for soft deletion.
 * 
 * @author skatzman
 */
public class ComponentVersion extends ComponentVersionSummary {
    private final LicenseDefinition licenseDefinition;

    private final RiskProfile riskProfile;

    private final boolean isComponentIntelligencePresent;

    @JsonCreator
    public ComponentVersion(@JsonProperty("version") String version,
            @JsonProperty("releasedOn") OffsetDateTime releasedOn,
            @JsonProperty("license") LicenseDefinition licenseDefinition,
            @JsonProperty("riskProfile") RiskProfile riskProfile,
            @JsonProperty("deleted") Boolean deleted,
            @JsonProperty("hasComponentIntelligence") Boolean componentIntelligence,
            @JsonProperty("_meta") Meta meta) {
        super(version, releasedOn, deleted, meta);

        this.licenseDefinition = licenseDefinition;
        this.riskProfile = (riskProfile != null) ? riskProfile : new RiskProfile();
        this.isComponentIntelligencePresent = (componentIntelligence != null) ? componentIntelligence.booleanValue() : false;
    }

    public ComponentVersion(ComponentVersion componentVersion) {
        super(componentVersion);

        this.licenseDefinition = componentVersion.getLicenseDefinition().orElse(null);
        this.riskProfile = componentVersion.getRiskProfile();
        this.isComponentIntelligencePresent = componentVersion.isComponentIntelligencePresent();
    }

    public Optional<LicenseDefinition> getLicenseDefinition() {
        return Optional.ofNullable(licenseDefinition);
    }

    public RiskProfile getRiskProfile() {
        return riskProfile;
    }

    @JsonIgnore
    public boolean isComponentIntelligencePresent() {
        return isComponentIntelligencePresent;
    }

    @JsonProperty("hasComponentIntelligence")
    public boolean hasComponentIntelligence() {
        return isComponentIntelligencePresent();
    }

    @Override
    public int hashCode() {
        int superHashCode = super.hashCode();

        return Objects.hash(superHashCode, getLicenseDefinition(), getRiskProfile(), isComponentIntelligencePresent());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof ComponentVersion) {
            ComponentVersion otherComponentVersion = (ComponentVersion) otherObject;

            return super.equals(otherObject)
                    && Objects.equals(getLicenseDefinition(), otherComponentVersion.getLicenseDefinition())
                    && Objects.equals(getRiskProfile(), otherComponentVersion.getRiskProfile())
                    && Objects.equals(isComponentIntelligencePresent(), otherComponentVersion.isComponentIntelligencePresent());
        }

        return false;
    }
}
