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
 * CVSS3 temporal metrics representation.
 * 
 * @author skatzman
 */
public class Cvss3TemporalMetrics {
    private final Double score;

    private final Cvss3ExploitCodeMaturity exploitCodeMaturity;

    private final Cvss3RemediationLevel remediationLevel;

    private final Cvss3ReportConfidence reportConfidence;

    @JsonCreator
    public Cvss3TemporalMetrics(@JsonProperty("score") Double score,
            @JsonProperty("exploitCodeMaturity") Cvss3ExploitCodeMaturity exploitCodeMaturity,
            @JsonProperty("remediationLevel") Cvss3RemediationLevel remediationLevel,
            @JsonProperty("reportConfidence") Cvss3ReportConfidence reportConfidence) {
        this.score = score;
        this.exploitCodeMaturity = exploitCodeMaturity;
        this.remediationLevel = remediationLevel;
        this.reportConfidence = reportConfidence;
    }

    public Optional<Double> getScore() {
        return Optional.ofNullable(score);
    }

    public Optional<Cvss3ExploitCodeMaturity> getExploitCodeMaturity() {
        return Optional.ofNullable(exploitCodeMaturity);
    }

    public Optional<Cvss3RemediationLevel> getRemediationLevel() {
        return Optional.ofNullable(remediationLevel);
    }

    public Optional<Cvss3ReportConfidence> getReportConfidence() {
        return Optional.ofNullable(reportConfidence);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getScore(), getExploitCodeMaturity(), getRemediationLevel(), getReportConfidence());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof Cvss3TemporalMetrics) {
            Cvss3TemporalMetrics otherCvss3TemporalMetrics = (Cvss3TemporalMetrics) otherObject;

            return Objects.equals(getScore(), otherCvss3TemporalMetrics.getScore())
                    && Objects.equals(getExploitCodeMaturity(), otherCvss3TemporalMetrics.getExploitCodeMaturity())
                    && Objects.equals(getRemediationLevel(), otherCvss3TemporalMetrics.getRemediationLevel())
                    && Objects.equals(getReportConfidence(), otherCvss3TemporalMetrics.getReportConfidence());
        }

        return false;
    }
}
