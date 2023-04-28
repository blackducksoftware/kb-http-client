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
 * CVSS2 score test.
 * 
 * @author skatzman
 */
public class Cvss2ScoreTest extends AbstractTest {
    private static final Double BASE_SCORE = Double.valueOf(1.0d);

    private static final Double IMPACT_SUBSCORE = Double.valueOf(2.0d);

    private static final Double EXPLOITABILITY_SUBSCORE = Double.valueOf(3.0d);

    private static final VulnerabilitySeverity SEVERITY = VulnerabilitySeverity.LOW;

    private static final Cvss2AccessVector ACCESS_VECTOR = Cvss2AccessVector.ADJACENT_NETWORK;

    private static final Cvss2AccessComplexity ACCESS_COMPLEXITY = Cvss2AccessComplexity.HIGH;

    private static final Cvss2Authentication AUTHENTICATION = Cvss2Authentication.MULTIPLE_INSTANCES;

    private static final Cvss2ConfidentialityImpact CONFIDENTIALITY_IMPACT = Cvss2ConfidentialityImpact.COMPLETE;

    private static final Cvss2IntegrityImpact INTEGRITY_IMPACT = Cvss2IntegrityImpact.COMPLETE;

    private static final Cvss2AvailabilityImpact AVAILABILITY_IMPACT = Cvss2AvailabilityImpact.COMPLETE;

    private static final VulnerabilitySource SOURCE = VulnerabilitySource.NVD;

    private static final String VECTOR = "(AV:N/AC:M/Au:N/C:N/I:P/A:N)";

    private static final Cvss2TemporalMetrics TEMPORAL_METRICS = new Cvss2TemporalMetrics(Double.valueOf(4.0d), Cvss2Exploitability.FUNCTIONAL,
            Cvss2RemediationLevel.NOT_DEFINED, Cvss2ReportConfidence.CONFIRMED);

    @Test
    public void testConstructor() {
        Cvss2Score cvss2Score = new Cvss2Score(BASE_SCORE, IMPACT_SUBSCORE, EXPLOITABILITY_SUBSCORE, SEVERITY, ACCESS_VECTOR, ACCESS_COMPLEXITY, AUTHENTICATION,
                CONFIDENTIALITY_IMPACT, INTEGRITY_IMPACT, AVAILABILITY_IMPACT, SOURCE, VECTOR, TEMPORAL_METRICS);

        Assert.assertEquals(cvss2Score.getBaseScore().orElse(null), BASE_SCORE, "Base scores should be equal.");
        Assert.assertEquals(cvss2Score.getImpactSubscore().orElse(null), IMPACT_SUBSCORE, "Impact subscores should be equal.");
        Assert.assertEquals(cvss2Score.getExploitabilitySubscore().orElse(null), EXPLOITABILITY_SUBSCORE, "Exploitability subscores should be equal.");
        Assert.assertEquals(cvss2Score.getSeverity().orElse(null), SEVERITY, "Severities should be equal.");
        Assert.assertEquals(cvss2Score.getAccessVector().orElse(null), ACCESS_VECTOR, "Access vectors should be equal.");
        Assert.assertEquals(cvss2Score.getAccessComplexity().orElse(null), ACCESS_COMPLEXITY, "Access complexities should be equal.");
        Assert.assertEquals(cvss2Score.getAuthentication().orElse(null), AUTHENTICATION, "Authentications should be equal.");
        Assert.assertEquals(cvss2Score.getConfidentialityImpact().orElse(null), CONFIDENTIALITY_IMPACT, "Confidentiality impacts should be equal.");
        Assert.assertEquals(cvss2Score.getIntegrityImpact().orElse(null), INTEGRITY_IMPACT, "Integrity impacts should be equal.");
        Assert.assertEquals(cvss2Score.getAvailabilityImpact().orElse(null), AVAILABILITY_IMPACT, "Availability impacts should be equal.");
        Assert.assertEquals(cvss2Score.getSource().orElse(null), SOURCE, "Sources should be equal.");
        Assert.assertEquals(cvss2Score.getVector().orElse(null), VECTOR, "Vectors should be equal.");
        Assert.assertEquals(cvss2Score.getTemporalMetrics().orElse(null), TEMPORAL_METRICS, "Temporal metrics should be equal.");
    }

    @Test
    public void testDeserialization() throws JsonProcessingException {
        Cvss2Score cvss2Score = new Cvss2Score(BASE_SCORE, IMPACT_SUBSCORE, EXPLOITABILITY_SUBSCORE, SEVERITY, ACCESS_VECTOR, ACCESS_COMPLEXITY, AUTHENTICATION,
                CONFIDENTIALITY_IMPACT, INTEGRITY_IMPACT, AVAILABILITY_IMPACT, SOURCE, VECTOR, TEMPORAL_METRICS);

        String json = serialize(cvss2Score);
        Cvss2Score result = deserialize(json, Cvss2Score.class);

        Assert.assertEquals(result.getBaseScore().orElse(null), BASE_SCORE, "Base scores should be equal.");
        Assert.assertEquals(result.getImpactSubscore().orElse(null), IMPACT_SUBSCORE, "Impact subscores should be equal.");
        Assert.assertEquals(result.getExploitabilitySubscore().orElse(null), EXPLOITABILITY_SUBSCORE, "Exploitability subscores should be equal.");
        Assert.assertEquals(result.getSeverity().orElse(null), SEVERITY, "Severities should be equal.");
        Assert.assertEquals(result.getAccessVector().orElse(null), ACCESS_VECTOR, "Access vectors should be equal.");
        Assert.assertEquals(result.getAccessComplexity().orElse(null), ACCESS_COMPLEXITY, "Access complexities should be equal.");
        Assert.assertEquals(result.getAuthentication().orElse(null), AUTHENTICATION, "Authentications should be equal.");
        Assert.assertEquals(result.getConfidentialityImpact().orElse(null), CONFIDENTIALITY_IMPACT, "Confidentiality impacts should be equal.");
        Assert.assertEquals(result.getIntegrityImpact().orElse(null), INTEGRITY_IMPACT, "Integrity impacts should be equal.");
        Assert.assertEquals(result.getAvailabilityImpact().orElse(null), AVAILABILITY_IMPACT, "Availability impacts should be equal.");
        Assert.assertEquals(result.getSource().orElse(null), SOURCE, "Sources should be equal.");
        Assert.assertEquals(result.getVector().orElse(null), VECTOR, "Vectors should be equal.");
        Assert.assertEquals(result.getTemporalMetrics().orElse(null), TEMPORAL_METRICS, "Temporal metrics should be equal.");
    }

    @Test
    public void testHashCode() {
        Cvss2Score cvss2Score = new Cvss2Score(BASE_SCORE, IMPACT_SUBSCORE, EXPLOITABILITY_SUBSCORE, SEVERITY, ACCESS_VECTOR, ACCESS_COMPLEXITY, AUTHENTICATION,
                CONFIDENTIALITY_IMPACT, INTEGRITY_IMPACT, AVAILABILITY_IMPACT, SOURCE, VECTOR, TEMPORAL_METRICS);
        Cvss2Score copyCvss2Score = new Cvss2Score(BASE_SCORE, IMPACT_SUBSCORE, EXPLOITABILITY_SUBSCORE, SEVERITY, ACCESS_VECTOR, ACCESS_COMPLEXITY,
                AUTHENTICATION, CONFIDENTIALITY_IMPACT, INTEGRITY_IMPACT, AVAILABILITY_IMPACT, SOURCE, VECTOR, TEMPORAL_METRICS);
        Cvss2Score differentCvss2Score = new Cvss2Score(BASE_SCORE, IMPACT_SUBSCORE, EXPLOITABILITY_SUBSCORE, SEVERITY, ACCESS_VECTOR, ACCESS_COMPLEXITY,
                AUTHENTICATION, CONFIDENTIALITY_IMPACT, INTEGRITY_IMPACT, AVAILABILITY_IMPACT, SOURCE, "differentVector", TEMPORAL_METRICS);

        assertHashCode(cvss2Score, copyCvss2Score, differentCvss2Score);
    }

    @Test
    public void testEquals() {
        Cvss2Score cvss2Score = new Cvss2Score(BASE_SCORE, IMPACT_SUBSCORE, EXPLOITABILITY_SUBSCORE, SEVERITY, ACCESS_VECTOR, ACCESS_COMPLEXITY, AUTHENTICATION,
                CONFIDENTIALITY_IMPACT, INTEGRITY_IMPACT, AVAILABILITY_IMPACT, SOURCE, VECTOR, TEMPORAL_METRICS);
        Cvss2Score copyCvss2Score = new Cvss2Score(BASE_SCORE, IMPACT_SUBSCORE, EXPLOITABILITY_SUBSCORE, SEVERITY, ACCESS_VECTOR, ACCESS_COMPLEXITY,
                AUTHENTICATION, CONFIDENTIALITY_IMPACT, INTEGRITY_IMPACT, AVAILABILITY_IMPACT, SOURCE, VECTOR, TEMPORAL_METRICS);
        Cvss2Score differentCvss2Score = new Cvss2Score(BASE_SCORE, IMPACT_SUBSCORE, EXPLOITABILITY_SUBSCORE, SEVERITY, ACCESS_VECTOR, ACCESS_COMPLEXITY,
                AUTHENTICATION, CONFIDENTIALITY_IMPACT, INTEGRITY_IMPACT, AVAILABILITY_IMPACT, SOURCE, "differentVector", TEMPORAL_METRICS);

        assertEquals(cvss2Score, copyCvss2Score, differentCvss2Score);
    }
}
