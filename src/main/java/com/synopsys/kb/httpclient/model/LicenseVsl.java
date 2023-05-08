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

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * License VSL representation.
 * 
 * @author skatzman
 */
public class LicenseVsl extends AbstractEntity {
    private final String filteredVsl;

    private final String vsl;

    private final String licenseName;

    private final String licenseFamilyName;

    // License URI
    private final String license;

    @JsonCreator
    public LicenseVsl(@JsonProperty("filteredVsl") String filteredVsl,
            @JsonProperty("vsl") String vsl,
            @JsonProperty("licenseName") String licenseName,
            @JsonProperty("licenseFamilyName") String licenseFamilyName,
            @JsonProperty("license") String license) {
        this.filteredVsl = filteredVsl;
        this.vsl = vsl;
        this.licenseName = licenseName;
        this.licenseFamilyName = licenseFamilyName;
        this.license = license;
    }

    public String getFilteredVsl() {
        return filteredVsl;
    }

    public String getVsl() {
        return vsl;
    }

    public String getLicenseName() {
        return licenseName;
    }

    public String getLicenseFamilyName() {
        return licenseFamilyName;
    }

    @JsonIgnore
    public UUID getLicenseId() {
        String licenseHref = getLicense();

        return extractId(licenseHref, "licenses").map(UUID::fromString)
                .orElseThrow(() -> new IllegalStateException("Unable to get license id because it is absent."));
    }

    public String getLicense() {
        return license;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFilteredVsl(),
                getVsl(),
                getLicenseName(),
                getLicenseFamilyName(),
                getLicense());
    }

    @Override
    public boolean equals(@Nullable Object otherObject) {
        boolean result = false;

        if (otherObject instanceof LicenseVsl) {
            LicenseVsl otherLicenseVsl = (LicenseVsl) otherObject;

            result = Objects.equals(getFilteredVsl(), otherLicenseVsl.getFilteredVsl())
                    && Objects.equals(getVsl(), otherLicenseVsl.getVsl())
                    && Objects.equals(getLicenseName(), otherLicenseVsl.getLicenseName())
                    && Objects.equals(getLicenseFamilyName(), otherLicenseVsl.getLicenseFamilyName())
                    && Objects.equals(getLicense(), otherLicenseVsl.getLicense());
        }

        return result;
    }
}
