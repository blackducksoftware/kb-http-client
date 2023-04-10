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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * License term representation.
 * 
 * @author skatzman
 */
public class LicenseTerm extends AbstractEntity {
    private final String name;

    private final String description;

    private final LicenseTermResponsibility responsibility;

    private final Meta meta;

    @JsonCreator
    public LicenseTerm(@JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("responsibility") LicenseTermResponsibility responsibility,
            @JsonProperty("_meta") Meta meta) {
        this.name = name;
        this.description = description;
        this.responsibility = responsibility;
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
    public UUID getId() {
        Meta meta = getMeta();

        return meta.getHrefId("license-terms")
                .map(UUID::fromString)
                .orElseThrow(() -> new IllegalArgumentException("Unable to get id because it is absent."));
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LicenseTermResponsibility getResponsibility() {
        return responsibility;
    }

    public Meta getMeta() {
        return meta;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDescription(), getResponsibility(), getMeta());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof LicenseTerm) {
            LicenseTerm otherLicenseTerm = (LicenseTerm) otherObject;

            return Objects.equals(getName(), otherLicenseTerm.getName())
                    && Objects.equals(getDescription(), otherLicenseTerm.getDescription())
                    && Objects.equals(getResponsibility(), otherLicenseTerm.getResponsibility())
                    && Objects.equals(getMeta(), otherLicenseTerm.getMeta());
        }

        return false;
    }
}
