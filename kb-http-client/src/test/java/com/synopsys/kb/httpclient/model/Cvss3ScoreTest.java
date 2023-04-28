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

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synopsys.kb.httpclient.AbstractTest;

/**
 * CVSS3 score test.
 * 
 * @author skatzman
 */
public class Cvss3ScoreTest extends AbstractTest {
    private static final Double BASE_SCORE = Double.valueOf(1.0d);

    private static final Double IMPACT_SUBSCORE = Double.valueOf(2.0d);

    private static final Double EXPLOITABILITY_SUBSCORE = Double.valueOf(3.0d);

    private static final VulnerabilitySeverity SEVERITY = VulnerabilitySeverity.MEDIUM;

    private static final Cvss3AttackVector ATTACK_VECTOR = Cvss3AttackVector.ADJACENT;

    private static final Cvss3AttackComplexity ATTACK_COMPLEXITY = Cvss3AttackComplexity.HIGH;

    private static final Cvss3ConfidentialityImpact CONFIDENTIALITY_IMPACT = Cvss3ConfidentialityImpact.HIGH;

    private static final Cvss3IntegrityImpact INTEGRITY_IMPACT = Cvss3IntegrityImpact.LOW;

    private static final Cvss3AvailabilityImpact AVAILABILITY_IMPACT = Cvss3AvailabilityImpact.HIGH;

    private static final Cvss3PrivilegesRequired PRIVILEGES_REQUIRED = Cvss3PrivilegesRequired.HIGH;

    private static final Cvss3Scope SCOPE = Cvss3Scope.CHANGED;

    private static final Cvss3UserInteraction USER_INTERACTION = Cvss3UserInteraction.NONE;

    private static final VulnerabilitySource SOURCE = VulnerabilitySource.BDSA;

    private static final String VECTOR = "this is a vector.";

    private static final Cvss3TemporalMetrics TEMPORAL_METRICS = new Cvss3TemporalMetrics(Double.valueOf(4.0d), Cvss3ExploitCodeMaturity.FUNCTIONAL,
            Cvss3RemediationLevel.NOT_DEFINED, Cvss3ReportConfidence.CONFIRMED);

    @Test
    public void testConstructor() {
        Cvss3Score cvss3Score = new Cvss3Score(BASE_SCORE, IMPACT_SUBSCORE, EXPLOITABILITY_SUBSCORE, SEVERITY, ATTACK_VECTOR, ATTACK_COMPLEXITY,
                CONFIDENTIALITY_IMPACT, INTEGRITY_IMPACT, AVAILABILITY_IMPACT, PRIVILEGES_REQUIRED, SCOPE, USER_INTERACTION, SOURCE, VECTOR, TEMPORAL_METRICS);

        Assert.assertEquals(cvss3Score.getBaseScore().orElse(null), BASE_SCORE, "Base scores should be equal.");
        Assert.assertEquals(cvss3Score.getImpactSubscore().orElse(null), IMPACT_SUBSCORE, "Impact subscore should be equal.");
        Assert.assertEquals(cvss3Score.getExploitabilitySubscore().orElse(null), EXPLOITABILITY_SUBSCORE, "Exploitability subscores should be equal.");
        Assert.assertEquals(cvss3Score.getSeverity().orElse(null), SEVERITY, "Severities should be equal.");
        Assert.assertEquals(cvss3Score.getAttackVector().orElse(null), ATTACK_VECTOR, "Attack vectors should be equal.");
        Assert.assertEquals(cvss3Score.getAttackComplexity().orElse(null), ATTACK_COMPLEXITY, "Attack complexitieis should be equal.");
        Assert.assertEquals(cvss3Score.getConfidentialityImpact().orElse(null), CONFIDENTIALITY_IMPACT, "Confidentiality impacts should be equal.");
        Assert.assertEquals(cvss3Score.getIntegrityImpact().orElse(null), INTEGRITY_IMPACT, "Integrity impacts should be equal.");
        Assert.assertEquals(cvss3Score.getAvailabilityImpact().orElse(null), AVAILABILITY_IMPACT, "Availability impacts should be equal.");
        Assert.assertEquals(cvss3Score.getPrivilegesRequired().orElse(null), PRIVILEGES_REQUIRED, "Privileges required should be equal.");
        Assert.assertEquals(cvss3Score.getScope().orElse(null), SCOPE, "Scopes should be equal.");
        Assert.assertEquals(cvss3Score.getUserInteraction().orElse(null), USER_INTERACTION, "User interactions should be equal.");
        Assert.assertEquals(cvss3Score.getSource().orElse(null), SOURCE, "Sources should be equal.");
        Assert.assertEquals(cvss3Score.getVector().orElse(null), VECTOR, "Vectors should be equal.");
        Assert.assertEquals(cvss3Score.getTemporalMetrics().orElse(null), TEMPORAL_METRICS, "Temporal metrics should be equal.");
    }

    @Test
    public void testDeserialization() throws JsonProcessingException {
        Cvss3Score cvss3Score = new Cvss3Score(BASE_SCORE, IMPACT_SUBSCORE, EXPLOITABILITY_SUBSCORE, SEVERITY, ATTACK_VECTOR, ATTACK_COMPLEXITY,
                CONFIDENTIALITY_IMPACT, INTEGRITY_IMPACT, AVAILABILITY_IMPACT, PRIVILEGES_REQUIRED, SCOPE, USER_INTERACTION, SOURCE, VECTOR, TEMPORAL_METRICS);

        String json = serialize(cvss3Score);
        Cvss3Score result = deserialize(json, Cvss3Score.class);

        Assert.assertEquals(result.getBaseScore().orElse(null), BASE_SCORE, "Base scores should be equal.");
        Assert.assertEquals(result.getImpactSubscore().orElse(null), IMPACT_SUBSCORE, "Impact subscore should be equal.");
        Assert.assertEquals(result.getExploitabilitySubscore().orElse(null), EXPLOITABILITY_SUBSCORE, "Exploitability subscores should be equal.");
        Assert.assertEquals(result.getSeverity().orElse(null), SEVERITY, "Severities should be equal.");
        Assert.assertEquals(result.getAttackVector().orElse(null), ATTACK_VECTOR, "Attack vectors should be equal.");
        Assert.assertEquals(result.getAttackComplexity().orElse(null), ATTACK_COMPLEXITY, "Attack complexitieis should be equal.");
        Assert.assertEquals(result.getConfidentialityImpact().orElse(null), CONFIDENTIALITY_IMPACT, "Confidentiality impacts should be equal.");
        Assert.assertEquals(result.getIntegrityImpact().orElse(null), INTEGRITY_IMPACT, "Integrity impacts should be equal.");
        Assert.assertEquals(result.getAvailabilityImpact().orElse(null), AVAILABILITY_IMPACT, "Availability impacts should be equal.");
        Assert.assertEquals(result.getPrivilegesRequired().orElse(null), PRIVILEGES_REQUIRED, "Privileges required should be equal.");
        Assert.assertEquals(result.getScope().orElse(null), SCOPE, "Scopes should be equal.");
        Assert.assertEquals(result.getUserInteraction().orElse(null), USER_INTERACTION, "User interactions should be equal.");
        Assert.assertEquals(result.getSource().orElse(null), SOURCE, "Sources should be equal.");
        Assert.assertEquals(result.getVector().orElse(null), VECTOR, "Vectors should be equal.");
        Assert.assertEquals(result.getTemporalMetrics().orElse(null), TEMPORAL_METRICS, "Temporal metrics should be equal.");
    }

    @Test
    public void testHashCode() {
        Cvss3Score cvss3Score = new Cvss3Score(BASE_SCORE, IMPACT_SUBSCORE, EXPLOITABILITY_SUBSCORE, SEVERITY, ATTACK_VECTOR, ATTACK_COMPLEXITY,
                CONFIDENTIALITY_IMPACT, INTEGRITY_IMPACT, AVAILABILITY_IMPACT, PRIVILEGES_REQUIRED, SCOPE, USER_INTERACTION, SOURCE, VECTOR, TEMPORAL_METRICS);
        Cvss3Score copyCvss3Score = new Cvss3Score(BASE_SCORE, IMPACT_SUBSCORE, EXPLOITABILITY_SUBSCORE, SEVERITY, ATTACK_VECTOR, ATTACK_COMPLEXITY,
                CONFIDENTIALITY_IMPACT, INTEGRITY_IMPACT, AVAILABILITY_IMPACT, PRIVILEGES_REQUIRED, SCOPE, USER_INTERACTION, SOURCE, VECTOR, TEMPORAL_METRICS);
        Cvss3Score differentCvss3Score = new Cvss3Score(BASE_SCORE, IMPACT_SUBSCORE, EXPLOITABILITY_SUBSCORE, SEVERITY, ATTACK_VECTOR, ATTACK_COMPLEXITY,
                CONFIDENTIALITY_IMPACT, INTEGRITY_IMPACT, AVAILABILITY_IMPACT, PRIVILEGES_REQUIRED, SCOPE, USER_INTERACTION, SOURCE, "differentVector",
                TEMPORAL_METRICS);

        assertHashCode(cvss3Score, copyCvss3Score, differentCvss3Score);
    }

    @Test
    public void testEquals() {
        Cvss3Score cvss3Score = new Cvss3Score(BASE_SCORE, IMPACT_SUBSCORE, EXPLOITABILITY_SUBSCORE, SEVERITY, ATTACK_VECTOR, ATTACK_COMPLEXITY,
                CONFIDENTIALITY_IMPACT, INTEGRITY_IMPACT, AVAILABILITY_IMPACT, PRIVILEGES_REQUIRED, SCOPE, USER_INTERACTION, SOURCE, VECTOR, TEMPORAL_METRICS);
        Cvss3Score copyCvss3Score = new Cvss3Score(BASE_SCORE, IMPACT_SUBSCORE, EXPLOITABILITY_SUBSCORE, SEVERITY, ATTACK_VECTOR, ATTACK_COMPLEXITY,
                CONFIDENTIALITY_IMPACT, INTEGRITY_IMPACT, AVAILABILITY_IMPACT, PRIVILEGES_REQUIRED, SCOPE, USER_INTERACTION, SOURCE, VECTOR, TEMPORAL_METRICS);
        Cvss3Score differentCvss3Score = new Cvss3Score(BASE_SCORE, IMPACT_SUBSCORE, EXPLOITABILITY_SUBSCORE, SEVERITY, ATTACK_VECTOR, ATTACK_COMPLEXITY,
                CONFIDENTIALITY_IMPACT, INTEGRITY_IMPACT, AVAILABILITY_IMPACT, PRIVILEGES_REQUIRED, SCOPE, USER_INTERACTION, SOURCE, "differentVector",
                TEMPORAL_METRICS);

        assertEquals(cvss3Score, copyCvss3Score, differentCvss3Score);
    }
}
