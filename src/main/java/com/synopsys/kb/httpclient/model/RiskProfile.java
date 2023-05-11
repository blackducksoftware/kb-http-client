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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Risk profile representation.
 * 
 * @author skatzman
 */
public class RiskProfile {
    private final int critical;

    private final int high;

    private final int medium;

    private final int low;

    private final int unscored;

    public RiskProfile() {
        this(0, 0, 0, 0, 0);
    }

    @JsonCreator
    public RiskProfile(@JsonProperty("critical") Integer critical,
            @JsonProperty("high") Integer high,
            @JsonProperty("medium") Integer medium,
            @JsonProperty("low") Integer low,
            @JsonProperty("unscored") Integer unscored) {
        this.critical = (critical != null) ? critical.intValue() : 0;
        this.high = (high != null) ? high.intValue() : 0;
        this.medium = (medium != null) ? medium.intValue() : 0;
        this.low = (low != null) ? low.intValue() : 0;
        this.unscored = (unscored != null) ? unscored.intValue() : 0;
    }

    public int getCritical() {
        return critical;
    }

    public int getHigh() {
        return high;
    }

    public int getMedium() {
        return medium;
    }

    public int getLow() {
        return low;
    }

    public int getUnscored() {
        return unscored;
    }
    
    public boolean areVulnerabilitiesPresent() {
    	return ((getCritical() > 0) || (getHigh() > 0) || (getMedium() > 0) || (getLow() > 0) || (getUnscored() > 0));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCritical(), getHigh(), getMedium(), getLow(), getUnscored());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof RiskProfile) {
            RiskProfile otherRiskProfile = (RiskProfile) otherObject;

            return Objects.equals(getCritical(), otherRiskProfile.getCritical())
                    && Objects.equals(getHigh(), otherRiskProfile.getHigh())
                    && Objects.equals(getMedium(), otherRiskProfile.getMedium())
                    && Objects.equals(getLow(), otherRiskProfile.getLow())
                    && Objects.equals(getUnscored(), otherRiskProfile.getUnscored());
        }

        return false;
    }
}
