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

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * License family VSL representation.
 * 
 * @author skatzman
 */
public class LicenseFamilyVsl {
    private final String filteredVsl;

    private final String vsl;

    private final String licenseFamilyName;

    @JsonCreator
    public LicenseFamilyVsl(@JsonProperty("filteredVsl") String filteredVsl,
            @JsonProperty("vsl") String vsl,
            @JsonProperty("licenseFamilyName") String licenseFamilyName) {
        this.filteredVsl = filteredVsl;
        this.vsl = vsl;
        this.licenseFamilyName = licenseFamilyName;
    }

    public String getFilteredVsl() {
        return filteredVsl;
    }

    public String getVsl() {
        return vsl;
    }

    public String getLicenseFamilyName() {
        return licenseFamilyName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFilteredVsl(),
                getVsl(),
                getLicenseFamilyName());
    }

    @Override
    public boolean equals(@Nullable Object otherObject) {
        boolean result = false;

        if (otherObject instanceof LicenseFamilyVsl) {
            LicenseFamilyVsl otherLicenseFamilyVsl = (LicenseFamilyVsl) otherObject;

            result = Objects.equals(getFilteredVsl(), otherLicenseFamilyVsl.getFilteredVsl())
                    && Objects.equals(getVsl(), otherLicenseFamilyVsl.getVsl())
                    && Objects.equals(getLicenseFamilyName(), otherLicenseFamilyVsl.getLicenseFamilyName());
        }

        return result;
    }
}
