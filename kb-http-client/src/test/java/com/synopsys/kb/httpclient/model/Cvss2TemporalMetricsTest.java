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
 * CVSS2 temporal metrics test.
 * 
 * @author skatzman
 */
public class Cvss2TemporalMetricsTest extends AbstractTest {
    private static final Double SCORE = Double.valueOf(5.0d);

    private static final Cvss2Exploitability EXPLOITABILITY = Cvss2Exploitability.FUNCTIONAL;

    private static final Cvss2RemediationLevel REMEDIATION_LEVEL = Cvss2RemediationLevel.NOT_DEFINED;

    private static final Cvss2ReportConfidence REPORT_CONFIDENCE = Cvss2ReportConfidence.CONFIRMED;

    @Test
    public void testConstructor() {
        Cvss2TemporalMetrics cvss2TemporalMetrics = new Cvss2TemporalMetrics(SCORE, EXPLOITABILITY, REMEDIATION_LEVEL, REPORT_CONFIDENCE);

        Assert.assertEquals(cvss2TemporalMetrics.getScore().orElse(null), SCORE, "Scores should be equal.");
        Assert.assertEquals(cvss2TemporalMetrics.getExploitability().orElse(null), EXPLOITABILITY, "Exploitabilities should be equal.");
        Assert.assertEquals(cvss2TemporalMetrics.getRemediationLevel().orElse(null), REMEDIATION_LEVEL, "Remediation levels should be equal.");
        Assert.assertEquals(cvss2TemporalMetrics.getReportConfidence().orElse(null), REPORT_CONFIDENCE, "Report confidences should be equal.");
    }

    @Test
    public void testDeserialization() throws JsonProcessingException {
        Cvss2TemporalMetrics cvss2TemporalMetrics = new Cvss2TemporalMetrics(SCORE, EXPLOITABILITY, REMEDIATION_LEVEL, REPORT_CONFIDENCE);

        String json = serialize(cvss2TemporalMetrics);
        Cvss2TemporalMetrics result = deserialize(json, Cvss2TemporalMetrics.class);

        Assert.assertEquals(result.getScore().orElse(null), SCORE, "Scores should be equal.");
        Assert.assertEquals(result.getExploitability().orElse(null), EXPLOITABILITY, "Exploitabilities should be equal.");
        Assert.assertEquals(result.getRemediationLevel().orElse(null), REMEDIATION_LEVEL, "Remediation levels should be equal.");
        Assert.assertEquals(result.getReportConfidence().orElse(null), REPORT_CONFIDENCE, "Report confidences should be equal.");
    }
}
