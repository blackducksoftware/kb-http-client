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
 * CVSS3 score representation.
 * 
 * @author skatzman
 */
public class Cvss3Score {
    private final Double baseScore;

    private final Double impactSubscore;

    private final Double exploitabilitySubscore;

    private final Cvss3AttackVector attackVector;

    private final Cvss3AttackComplexity attackComplexity;

    private final Cvss3ConfidentialityImpact confidentialityImpact;

    private final Cvss3IntegrityImpact integrityImpact;

    private final Cvss3AvailabilityImpact availabilityImpact;

    private final Cvss3PrivilegesRequired privilegesRequired;

    private final Cvss3Scope scope;

    private final Cvss3UserInteraction userInteraction;

    private final VulnerabilitySeverity severity;

    private final VulnerabilitySource source;

    private final String vector;

    private final Cvss3TemporalMetrics temporalMetrics;

    @JsonCreator
    public Cvss3Score(@JsonProperty("baseScore") Double baseScore,
            @JsonProperty("impactSubscore") Double impactSubscore,
            @JsonProperty("exploitabilitySubscore") Double exploitabilitySubscore,
            @JsonProperty("severity") VulnerabilitySeverity severity,
            @JsonProperty("attackVector") Cvss3AttackVector attackVector,
            @JsonProperty("attackComplexity") Cvss3AttackComplexity attackComplexity,
            @JsonProperty("confidentialityImpact") Cvss3ConfidentialityImpact confidentialityImpact,
            @JsonProperty("integrityImpact") Cvss3IntegrityImpact integrityImpact,
            @JsonProperty("availabilityImpact") Cvss3AvailabilityImpact availabilityImpact,
            @JsonProperty("privilegesRequired") Cvss3PrivilegesRequired privilegesRequired,
            @JsonProperty("scope") Cvss3Scope scope,
            @JsonProperty("userInteraction") Cvss3UserInteraction userInteraction,
            @JsonProperty("source") VulnerabilitySource source,
            @JsonProperty("vector") String vector,
            @JsonProperty("temporalMetrics") Cvss3TemporalMetrics temporalMetrics) {
        this.baseScore = baseScore;
        this.impactSubscore = impactSubscore;
        this.exploitabilitySubscore = exploitabilitySubscore;
        this.severity = severity;
        this.attackVector = attackVector;
        this.attackComplexity = attackComplexity;
        this.confidentialityImpact = confidentialityImpact;
        this.integrityImpact = integrityImpact;
        this.availabilityImpact = availabilityImpact;
        this.privilegesRequired = privilegesRequired;
        this.scope = scope;
        this.userInteraction = userInteraction;
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

    public Optional<Cvss3AttackVector> getAttackVector() {
        return Optional.ofNullable(attackVector);
    }

    public Optional<Cvss3AttackComplexity> getAttackComplexity() {
        return Optional.ofNullable(attackComplexity);
    }

    public Optional<Cvss3ConfidentialityImpact> getConfidentialityImpact() {
        return Optional.ofNullable(confidentialityImpact);
    }

    public Optional<Cvss3IntegrityImpact> getIntegrityImpact() {
        return Optional.ofNullable(integrityImpact);
    }

    public Optional<Cvss3AvailabilityImpact> getAvailabilityImpact() {
        return Optional.ofNullable(availabilityImpact);
    }

    public Optional<Cvss3PrivilegesRequired> getPrivilegesRequired() {
        return Optional.ofNullable(privilegesRequired);
    }

    public Optional<Cvss3Scope> getScope() {
        return Optional.ofNullable(scope);
    }

    public Optional<Cvss3UserInteraction> getUserInteraction() {
        return Optional.ofNullable(userInteraction);
    }

    public Optional<VulnerabilitySource> getSource() {
        return Optional.ofNullable(source);
    }

    public Optional<String> getVector() {
        return Optional.ofNullable(vector);
    }

    public Optional<Cvss3TemporalMetrics> getTemporalMetrics() {
        return Optional.ofNullable(temporalMetrics);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBaseScore(), getImpactSubscore(), getExploitabilitySubscore(), getSeverity(), getAttackVector(), getAttackComplexity(),
                getConfidentialityImpact(), getIntegrityImpact(), getAvailabilityImpact(), getPrivilegesRequired(), getScope(), getUserInteraction(),
                getSource(), getVector(), getTemporalMetrics());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof Cvss3Score) {
            Cvss3Score otherCvss3Score = (Cvss3Score) otherObject;

            return Objects.equals(getBaseScore(), otherCvss3Score.getBaseScore())
                    && Objects.equals(getImpactSubscore(), otherCvss3Score.getImpactSubscore())
                    && Objects.equals(getExploitabilitySubscore(), otherCvss3Score.getExploitabilitySubscore())
                    && Objects.equals(getSeverity(), otherCvss3Score.getSeverity())
                    && Objects.equals(getAttackVector(), otherCvss3Score.getAttackVector())
                    && Objects.equals(getAttackComplexity(), otherCvss3Score.getAttackComplexity())
                    && Objects.equals(getConfidentialityImpact(), otherCvss3Score.getConfidentialityImpact())
                    && Objects.equals(getIntegrityImpact(), otherCvss3Score.getIntegrityImpact())
                    && Objects.equals(getAvailabilityImpact(), otherCvss3Score.getAvailabilityImpact())
                    && Objects.equals(getPrivilegesRequired(), otherCvss3Score.getPrivilegesRequired())
                    && Objects.equals(getScope(), otherCvss3Score.getScope())
                    && Objects.equals(getUserInteraction(), otherCvss3Score.getUserInteraction())
                    && Objects.equals(getSource(), otherCvss3Score.getSource())
                    && Objects.equals(getVector(), otherCvss3Score.getVector())
                    && Objects.equals(getTemporalMetrics(), otherCvss3Score.getTemporalMetrics());
        }

        return false;
    }
}
