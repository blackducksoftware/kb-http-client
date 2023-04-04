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
package com.synopsys.bd.kb.httpclient.model;

import java.util.Objects;

import com.synopsys.kb.httpclient.model.VulnerabilitySeverity;

/**
 * Overall score representation.
 * 
 * @author skatzman
 */
public class OverallScore {
    private final double score;

    private final VulnerabilitySeverity severity;

    private final CvssScore cvssScore;

    public OverallScore(double score,
            VulnerabilitySeverity severity,
            CvssScore cvssScore) {
        this.score = score;
        this.severity = Objects.requireNonNull(severity, "Severity must be initialized.");
        this.cvssScore = Objects.requireNonNull(cvssScore, "CVSS score must be initialized.");
    }

    /**
     * Gets the score for this overall score.
     * 
     * @return Returns the core.
     */
    public double getScore() {
        return score;
    }

    /**
     * Gets the severity for this overall score.
     * 
     * @return Returns the severity.
     */
    public VulnerabilitySeverity getSeverity() {
        return severity;
    }

    /**
     * Gets the CVSS score under which this overall score was determined.
     * 
     * @return Returns the CVSS score.
     */
    public CvssScore getCvssScore() {
        return cvssScore;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getScore(), getSeverity(), getCvssScore());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof OverallScore) {
            OverallScore otherOverallScore = (OverallScore) otherObject;

            return Objects.equals(getScore(), otherOverallScore.getScore())
                    && Objects.equals(getSeverity(), otherOverallScore.getSeverity())
                    && Objects.equals(getCvssScore(), otherOverallScore.getCvssScore());
        }

        return false;
    }
}
