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

import org.apache.hc.core5.util.TimeValue;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Exponential backoff retry interval strategy test.
 * 
 * @author skatzman
 */
public class ExponentialBackoffRetryIntervalStrategyTest {
    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void testConstructorWithNegativeBaseRetryIntervalMs() {
        new ExponentialBackoffRetryIntervalStrategy(-1L, 1L);
    }

    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void testConstructorWithNegativeMaxRetryIntervalMs() {
        new ExponentialBackoffRetryIntervalStrategy(1L, -1L);
    }

    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void testConstructorWithBaseRetryIntervalMsGreaterThanMaxRetryIntervalMs() {
        new ExponentialBackoffRetryIntervalStrategy(2L, 1L);
    }

    @Test
    public void testDetermineRetryInterval() {
        long baseRetryIntervalMs = 1000L;
        long maxRetryIntervalMs = 32000L;
        IRetryIntervalStrategy retryIntervalStrategy = new ExponentialBackoffRetryIntervalStrategy(baseRetryIntervalMs, maxRetryIntervalMs);

        TimeValue zeroMsRetryInterval = TimeValue.ofMilliseconds(0L);
        TimeValue maxRetryIntervalMsRetryInterval = TimeValue.ofMilliseconds(maxRetryIntervalMs);

        for (int i = 0; i < 100; i++) {
            TimeValue retryInterval = retryIntervalStrategy.determineRetryInterval(i);

            Assert.assertTrue(retryInterval.compareTo(zeroMsRetryInterval) >= 0, "Retry interval should be greater than or equal to 0 milliseconds.");
            Assert.assertTrue(retryInterval.compareTo(maxRetryIntervalMsRetryInterval) <= 0,
                    "Retry interval should be less than or equal to " + maxRetryIntervalMs + " milliseconds.");
        }
    }
}
