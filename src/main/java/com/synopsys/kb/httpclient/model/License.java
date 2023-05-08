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

/**
 * License response data structure.
 * 
 * @author skatzman
 */
public class License {
    private final String name;

    private final LicenseCodeSharing codeSharing;

    private final LicenseOwnership ownership;

    private final OffsetDateTime lastUpdatedAt;

    private final String spdxId;

    private final boolean isParentDeleted;

    private final LicenseRestriction restriction;

    private final Meta meta;

    @JsonCreator
    public License(@JsonProperty("name") String name,
            @JsonProperty("codeSharing") LicenseCodeSharing codeSharing,
            @JsonProperty("ownership") LicenseOwnership ownership,
            @JsonProperty("lastUpdated") OffsetDateTime lastUpdatedAt,
            @JsonProperty("spdxId") String spdxId,
            @JsonProperty("parentDeleted") Boolean parentDeleted,
            @JsonProperty("restriction") LicenseRestriction restriction,
            @JsonProperty("_meta") Meta meta) {
        this.name = name;
        this.codeSharing = (codeSharing != null) ? codeSharing : LicenseCodeSharing.UNKNOWN;
        this.ownership = (ownership != null) ? ownership : LicenseOwnership.UNKNOWN;
        this.lastUpdatedAt = lastUpdatedAt;
        this.spdxId = spdxId;
        this.isParentDeleted = (parentDeleted != null) ? parentDeleted : false;
        this.restriction = (restriction != null) ? restriction : LicenseRestriction.UNKNOWN;
        this.meta = meta;
    }

    public License(License license) {
        Objects.requireNonNull(license, "License must be initialized.");

        this.name = license.getName();
        this.codeSharing = license.getCodeSharing();
        this.ownership = license.getOwnership();
        this.lastUpdatedAt = license.getLastUpdatedAt();
        this.spdxId = license.getSpdxId().orElse(null);
        this.isParentDeleted = license.isParentDeleted();
        this.restriction = license.getRestriction();
        this.meta = license.getMeta();
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
        String idString = meta.getHrefId("licenses").orElseThrow(() -> new IllegalArgumentException("Unable to get id because it is absent."));

        return UUID.fromString(idString);
    }

    public String getName() {
        return name;
    }

    public LicenseCodeSharing getCodeSharing() {
        return codeSharing;
    }

    public LicenseOwnership getOwnership() {
        return ownership;
    }

    public OffsetDateTime getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public Optional<String> getSpdxId() {
        return Optional.ofNullable(spdxId);
    }

    public boolean isParentDeleted() {
        return isParentDeleted;
    }

    public LicenseRestriction getRestriction() {
        return restriction;
    }

    public Meta getMeta() {
        return meta;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getCodeSharing(), getOwnership(), getLastUpdatedAt(), getSpdxId(), isParentDeleted(), getRestriction(), getMeta());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof License) {
            License otherLicense = (License) otherObject;

            return Objects.equals(getName(), otherLicense.getName())
                    && Objects.equals(getCodeSharing(), otherLicense.getCodeSharing())
                    && Objects.equals(getOwnership(), otherLicense.getOwnership())
                    && Objects.equals(getLastUpdatedAt(), otherLicense.getLastUpdatedAt())
                    && Objects.equals(getSpdxId(), otherLicense.getSpdxId())
                    && Objects.equals(isParentDeleted(), otherLicense.isParentDeleted())
                    && Objects.equals(getRestriction(), otherLicense.getRestriction())
                    && Objects.equals(getMeta(), otherLicense.getMeta());
        }

        return false;
    }
}
