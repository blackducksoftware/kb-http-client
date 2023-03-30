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
 * Risk profile test.
 * 
 * @author skatzman
 */
public class RiskProfileTest extends AbstractTest {
    private static final int CRITICAL = 5;

    private static final int HIGH = 4;

    private static final int MEDIUM = 3;

    private static final int LOW = 2;

    private static final int UNSCORED = 1;

    @Test
    public void testConstructor() {
        RiskProfile riskProfile = new RiskProfile(CRITICAL, HIGH, MEDIUM, LOW, UNSCORED);

        Assert.assertEquals(riskProfile.getCritical(), CRITICAL, "Critical counts should be equal.");
        Assert.assertEquals(riskProfile.getHigh(), HIGH, "High counts should be equal.");
        Assert.assertEquals(riskProfile.getMedium(), MEDIUM, "Medium counts should be equal.");
        Assert.assertEquals(riskProfile.getLow(), LOW, "Low counts should be equal.");
        Assert.assertEquals(riskProfile.getUnscored(), UNSCORED, "Unscored counts should be equal.");
    }

    @Test
    public void testDeserialization() throws JsonProcessingException {
        RiskProfile riskProfile = new RiskProfile(CRITICAL, HIGH, MEDIUM, LOW, UNSCORED);

        String json = serialize(riskProfile);
        RiskProfile result = deserialize(json, RiskProfile.class);

        Assert.assertEquals(result.getCritical(), CRITICAL, "Critical counts should be equal.");
        Assert.assertEquals(result.getHigh(), HIGH, "High counts should be equal.");
        Assert.assertEquals(result.getMedium(), MEDIUM, "Medium counts should be equal.");
        Assert.assertEquals(result.getLow(), LOW, "Low counts should be equal.");
        Assert.assertEquals(result.getUnscored(), UNSCORED, "Unscored counts should be equal.");
    }
}
