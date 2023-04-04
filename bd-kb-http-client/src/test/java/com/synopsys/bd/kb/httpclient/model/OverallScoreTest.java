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

import org.testng.Assert;
import org.testng.annotations.Test;

import com.synopsys.kb.httpclient.model.VulnerabilitySeverity;

/**
 * Overall score test.
 * 
 * @author skatzman
 */
public class OverallScoreTest {
    private static final double SCORE = 5.0d;

    private static final VulnerabilitySeverity SEVERITY = VulnerabilitySeverity.MEDIUM;

    private static final CvssScore CVSS_SCORE = CvssScore.CVSS3;

    @Test
    public void testConstructor() {
        OverallScore overallScore = new OverallScore(SCORE, SEVERITY, CVSS_SCORE);

        Assert.assertEquals(overallScore.getScore(), SCORE, "Scores should be equal.");
        Assert.assertEquals(overallScore.getSeverity(), SEVERITY, "Severities should be equal.");
        Assert.assertEquals(overallScore.getCvssScore(), CVSS_SCORE, "CVSS scores should be equal.");
    }
}
