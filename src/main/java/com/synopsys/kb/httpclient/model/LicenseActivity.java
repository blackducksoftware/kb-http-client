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
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * License activity representation.
 * 
 * @author skatzman
 */
public class LicenseActivity extends AbstractActivity {
    private final String license;

    @JsonCreator
    public LicenseActivity(@JsonProperty("license") String license,
            @JsonProperty("updatedDate") OffsetDateTime updatedDate) {
        super(updatedDate);

        this.license = license;
    }

    /**
     * Gets the license id.
     * 
     * @return Returns the license id.
     */
    @JsonIgnore
    public UUID getLicenseId() {
        return extractId(getLicense(), "licenses")
                .map(UUID::fromString)
                .orElseThrow(() -> new IllegalStateException("Unable to get license id because it is absent."));
    }

    public String getLicense() {
        return license;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLicense(), getUpdatedDate());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof LicenseActivity) {
            LicenseActivity otherLicenseActivity = (LicenseActivity) otherObject;

            return Objects.equals(getLicense(), otherLicenseActivity.getLicense())
                    && Objects.equals(getUpdatedDate(), otherLicenseActivity.getUpdatedDate());
        }

        return false;
    }
}
