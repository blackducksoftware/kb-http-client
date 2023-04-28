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
 * CVSS3 temporal metrics test.
 * 
 * @author skatzman
 */
public class Cvss3TemporalMetricsTest extends AbstractTest {
    private static final Double SCORE = Double.valueOf(5.0d);

    private static final Cvss3ExploitCodeMaturity EXPLOIT_CODE_MATURITY = Cvss3ExploitCodeMaturity.FUNCTIONAL;

    private static final Cvss3RemediationLevel REMEDIATION_LEVEL = Cvss3RemediationLevel.NOT_DEFINED;

    private static final Cvss3ReportConfidence REPORT_CONFIDENCE = Cvss3ReportConfidence.CONFIRMED;

    @Test
    public void testConstructor() {
        Cvss3TemporalMetrics cvss3TemporalMetrics = new Cvss3TemporalMetrics(SCORE, EXPLOIT_CODE_MATURITY, REMEDIATION_LEVEL, REPORT_CONFIDENCE);

        Assert.assertEquals(cvss3TemporalMetrics.getScore().orElse(null), SCORE, "Scores should be equal.");
        Assert.assertEquals(cvss3TemporalMetrics.getExploitCodeMaturity().orElse(null), EXPLOIT_CODE_MATURITY, "Exploit code maturities should be equal.");
        Assert.assertEquals(cvss3TemporalMetrics.getRemediationLevel().orElse(null), REMEDIATION_LEVEL, "Remediation levels should be equal.");
        Assert.assertEquals(cvss3TemporalMetrics.getReportConfidence().orElse(null), REPORT_CONFIDENCE, "Report confidences should be equal.");
    }

    @Test
    public void testDeserialization() throws JsonProcessingException {
        Cvss3TemporalMetrics cvss3TemporalMetrics = new Cvss3TemporalMetrics(SCORE, EXPLOIT_CODE_MATURITY, REMEDIATION_LEVEL, REPORT_CONFIDENCE);

        String json = serialize(cvss3TemporalMetrics);
        Cvss3TemporalMetrics result = deserialize(json, Cvss3TemporalMetrics.class);

        Assert.assertEquals(result.getScore().orElse(null), SCORE, "Scores should be equal.");
        Assert.assertEquals(result.getExploitCodeMaturity().orElse(null), EXPLOIT_CODE_MATURITY, "Exploit code maturities should be equal.");
        Assert.assertEquals(result.getRemediationLevel().orElse(null), REMEDIATION_LEVEL, "Remediation levels should be equal.");
        Assert.assertEquals(result.getReportConfidence().orElse(null), REPORT_CONFIDENCE, "Report confidences should be equal.");
    }

    @Test
    public void testHashCode() {
        Cvss3TemporalMetrics cvss3TemporalMetrics = new Cvss3TemporalMetrics(SCORE, EXPLOIT_CODE_MATURITY, REMEDIATION_LEVEL, REPORT_CONFIDENCE);
        Cvss3TemporalMetrics copyCvss3TemporalMetrics = new Cvss3TemporalMetrics(SCORE, EXPLOIT_CODE_MATURITY, REMEDIATION_LEVEL, REPORT_CONFIDENCE);
        Cvss3TemporalMetrics differentCvss3TemporalMetrics = new Cvss3TemporalMetrics(SCORE, EXPLOIT_CODE_MATURITY, REMEDIATION_LEVEL,
                Cvss3ReportConfidence.NOT_DEFINED);

        assertHashCode(cvss3TemporalMetrics, copyCvss3TemporalMetrics, differentCvss3TemporalMetrics);
    }

    @Test
    public void testEquals() {
        Cvss3TemporalMetrics cvss3TemporalMetrics = new Cvss3TemporalMetrics(SCORE, EXPLOIT_CODE_MATURITY, REMEDIATION_LEVEL, REPORT_CONFIDENCE);
        Cvss3TemporalMetrics copyCvss3TemporalMetrics = new Cvss3TemporalMetrics(SCORE, EXPLOIT_CODE_MATURITY, REMEDIATION_LEVEL, REPORT_CONFIDENCE);
        Cvss3TemporalMetrics differentCvss3TemporalMetrics = new Cvss3TemporalMetrics(SCORE, EXPLOIT_CODE_MATURITY, REMEDIATION_LEVEL,
                Cvss3ReportConfidence.NOT_DEFINED);

        assertEquals(cvss3TemporalMetrics, copyCvss3TemporalMetrics, differentCvss3TemporalMetrics);
    }
}
