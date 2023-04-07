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
package com.synopsys.kb.httpclient.api;

import java.util.concurrent.TimeUnit;

import org.apache.hc.core5.util.TimeValue;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Fixed retry interval strategy test.
 * 
 * @author skatzman
 */
public class FixedRetryIntervalStrategyTest {
    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void testConstructorWithNegativeFixedRetryIntervalMs() {
        new FixedRetryIntervalStrategy(-1L);
    }

    @Test
    public void testDetermineRetryInterval() {
        long fixedRetryIntervalMs = TimeUnit.SECONDS.toMillis(1L);
        IRetryIntervalStrategy retryIntervalStrategy = new FixedRetryIntervalStrategy(fixedRetryIntervalMs);

        for (int i = 0; i < 100; i++) {
            TimeValue retryInterval = retryIntervalStrategy.determineRetryInterval(i);

            TimeValue expectedRetryInterval = TimeValue.ofMilliseconds(1000L);
            Assert.assertEquals(retryInterval, expectedRetryInterval, "Retry intervals should be equal.");
        }
    }
}
