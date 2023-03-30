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
 * CVSS2 score representation.
 * 
 * @author skatzman
 */
public class Cvss2Score {
    private final Double baseScore;

    private final Double impactSubscore;

    private final Double exploitabilitySubscore;

    private final VulnerabilitySeverity severity;

    private final Cvss2AccessVector accessVector;

    private final Cvss2AccessComplexity accessComplexity;

    private final Cvss2Authentication authentication;

    private final Cvss2ConfidentialityImpact confidentialityImpact;

    private final Cvss2IntegrityImpact integrityImpact;

    private final Cvss2AvailabilityImpact availabilityImpact;

    private final VulnerabilitySource source;

    private final String vector;

    private final Cvss2TemporalMetrics temporalMetrics;

    @JsonCreator
    public Cvss2Score(@JsonProperty("baseScore") Double baseScore,
            @JsonProperty("impactSubscore") Double impactSubscore,
            @JsonProperty("exploitabilitySubscore") Double exploitabilitySubscore,
            @JsonProperty("severity") VulnerabilitySeverity severity,
            @JsonProperty("accessVector") Cvss2AccessVector accessVector,
            @JsonProperty("accessComplexity") Cvss2AccessComplexity accessComplexity,
            @JsonProperty("authentication") Cvss2Authentication authentication,
            @JsonProperty("confidentialityImpact") Cvss2ConfidentialityImpact confidentialityImpact,
            @JsonProperty("integrityImpact") Cvss2IntegrityImpact integrityImpact,
            @JsonProperty("availabilityImpact") Cvss2AvailabilityImpact availabilityImpact,
            @JsonProperty("source") VulnerabilitySource source,
            @JsonProperty("vector") String vector,
            @JsonProperty("temporalMetrics") Cvss2TemporalMetrics temporalMetrics) {
        this.baseScore = baseScore;
        this.impactSubscore = impactSubscore;
        this.exploitabilitySubscore = exploitabilitySubscore;
        this.severity = severity;
        this.accessVector = accessVector;
        this.accessComplexity = accessComplexity;
        this.authentication = authentication;
        this.confidentialityImpact = confidentialityImpact;
        this.integrityImpact = integrityImpact;
        this.availabilityImpact = availabilityImpact;
        this.source = source;
        this.vector = vector;
        this.temporalMetrics = temporalMetrics;
    }

    public Optional<Double> getBaseScore() {
        return Optional.ofNullable(baseScore);
    }

    public Optional<Double> getImpactSubscore() {
        return Optional.ofNullable(impactSubscore);
    }

    public Optional<Double> getExploitabilitySubscore() {
        return Optional.ofNullable(exploitabilitySubscore);
    }

    public Optional<VulnerabilitySeverity> getSeverity() {
        return Optional.ofNullable(severity);
    }

    public Optional<Cvss2AccessVector> getAccessVector() {
        return Optional.ofNullable(accessVector);
    }

    public Optional<Cvss2AccessComplexity> getAccessComplexity() {
        return Optional.ofNullable(accessComplexity);
    }

    public Optional<Cvss2Authentication> getAuthentication() {
        return Optional.ofNullable(authentication);
    }

    public Optional<Cvss2ConfidentialityImpact> getConfidentialityImpact() {
        return Optional.ofNullable(confidentialityImpact);
    }

    public Optional<Cvss2IntegrityImpact> getIntegrityImpact() {
        return Optional.ofNullable(integrityImpact);
    }

    public Optional<Cvss2AvailabilityImpact> getAvailabilityImpact() {
        return Optional.ofNullable(availabilityImpact);
    }

    public Optional<VulnerabilitySource> getSource() {
        return Optional.ofNullable(source);
    }

    public Optional<String> getVector() {
        return Optional.ofNullable(vector);
    }

    public Optional<Cvss2TemporalMetrics> getTemporalMetrics() {
        return Optional.ofNullable(temporalMetrics);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBaseScore(), getImpactSubscore(), getExploitabilitySubscore(), getSeverity(), getAccessVector(), getAccessComplexity(),
                getAuthentication(), getConfidentialityImpact(), getIntegrityImpact(), getAvailabilityImpact(), getSource(), getVector(), getTemporalMetrics());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof Cvss2Score) {
            Cvss2Score otherCvss2Score = (Cvss2Score) otherObject;

            return Objects.equals(getBaseScore(), otherCvss2Score.getBaseScore())
                    && Objects.equals(getImpactSubscore(), otherCvss2Score.getImpactSubscore())
                    && Objects.equals(getExploitabilitySubscore(), otherCvss2Score.getExploitabilitySubscore())
                    && Objects.equals(getSeverity(), otherCvss2Score.getSeverity())
                    && Objects.equals(getAccessVector(), otherCvss2Score.getAccessVector())
                    && Objects.equals(getAccessComplexity(), otherCvss2Score.getAccessComplexity())
                    && Objects.equals(getAuthentication(), otherCvss2Score.getAuthentication())
                    && Objects.equals(getConfidentialityImpact(), otherCvss2Score.getConfidentialityImpact())
                    && Objects.equals(getIntegrityImpact(), otherCvss2Score.getIntegrityImpact())
                    && Objects.equals(getAvailabilityImpact(), otherCvss2Score.getAvailabilityImpact())
                    && Objects.equals(getSource(), otherCvss2Score.getSource())
                    && Objects.equals(getVector(), otherCvss2Score.getVector())
                    && Objects.equals(getTemporalMetrics(), otherCvss2Score.getTemporalMetrics());
        }

        return false;
    }
}
