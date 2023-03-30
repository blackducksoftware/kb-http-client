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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * CVSS2 temporal metrics representation.
 * 
 * @author skatzman
 */
public class Cvss2TemporalMetrics {
    private final Cvss2Exploitability exploitability;

    private final Cvss2RemediationLevel remediationLevel;

    private final Cvss2ReportConfidence reportConfidence;

    private final Double score;

    @JsonCreator
    public Cvss2TemporalMetrics(@JsonProperty("score") Double score,
            @JsonProperty("exploitability") Cvss2Exploitability exploitability,
            @JsonProperty("remediationLevel") Cvss2RemediationLevel remediationLevel,
            @JsonProperty("reportConfidence") Cvss2ReportConfidence reportConfidence) {
        this.score = score;
        this.exploitability = exploitability;
        this.remediationLevel = remediationLevel;
        this.reportConfidence = reportConfidence;
    }

    public Optional<Double> getScore() {
        return Optional.ofNullable(score);
    }

    public Optional<Cvss2Exploitability> getExploitability() {
        return Optional.ofNullable(exploitability);
    }

    public Optional<Cvss2RemediationLevel> getRemediationLevel() {
        return Optional.ofNullable(remediationLevel);
    }

    public Optional<Cvss2ReportConfidence> getReportConfidence() {
        return Optional.ofNullable(reportConfidence);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getScore(), getExploitability(), getRemediationLevel(), getReportConfidence());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof Cvss2TemporalMetrics) {
            Cvss2TemporalMetrics otherCvss2TemporalMetrics = (Cvss2TemporalMetrics) otherObject;

            return Objects.equals(getScore(), otherCvss2TemporalMetrics.getScore())
                    && Objects.equals(getExploitability(), otherCvss2TemporalMetrics.getExploitability())
                    && Objects.equals(getRemediationLevel(), otherCvss2TemporalMetrics.getRemediationLevel())
                    && Objects.equals(getReportConfidence(), otherCvss2TemporalMetrics.getReportConfidence());
        }

        return false;
    }
}
