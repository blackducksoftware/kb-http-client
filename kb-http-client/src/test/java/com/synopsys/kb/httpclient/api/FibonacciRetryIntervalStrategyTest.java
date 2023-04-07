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
 * Fibonacci retry interval strategy test.
 * 
 * @author skatzman
 */
public class FibonacciRetryIntervalStrategyTest {
    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void testConstructorWithNegativeMultiplier() {
        new FibonacciRetryIntervalStrategy(-1L, 32000L);
    }

    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void testConstructorWithZeroMultiplier() {
        new FibonacciRetryIntervalStrategy(0L, 32000L);
    }

    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void testConstructorWithNegativeMaxWait() {
        new FibonacciRetryIntervalStrategy(1L, -1L);
    }

    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void testConstructorWithMultiplierEqualToMaxWait() {
        new FibonacciRetryIntervalStrategy(1L, 1L);
    }

    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void testConstructorWithMultiplierGreaterThanMaxWait() {
        new FibonacciRetryIntervalStrategy(2L, 1L);
    }

    @Test
    public void testDetermineRetryInterval() {
        long maxWaitSeconds = 32L;

        TimeValue zeroSeconds = TimeValue.ofSeconds(0L);

        IRetryIntervalStrategy retryIntervalStrategy = new FibonacciRetryIntervalStrategy(1L, maxWaitSeconds);
        for (int i = 0; i < 100; i++) {
            TimeValue retryInterval = retryIntervalStrategy.determineRetryInterval(i);

            // Retry interval
            if (retryInterval.compareTo(zeroSeconds) <= 0) {
                retryInterval = zeroSeconds;
            }

            long expectedFibonacci = fibonacci(i);
            if (expectedFibonacci > maxWaitSeconds || expectedFibonacci < 0L) {
                expectedFibonacci = maxWaitSeconds;
            }
            TimeValue expectedRetryInterval = TimeValue.ofSeconds(expectedFibonacci);

            Assert.assertEquals(retryInterval, expectedRetryInterval, "Retry intervals should be equal.");
        }
    }

    private long fibonacci(long n) {
        if (0L == n) {
            return 0L;
        }

        if (1L == n) {
            return 1L;
        }

        long previousToPrevious = 0L;
        long previous = 1L;
        long result = 0L;

        for (long i = 2L; i <= n; i++) {
            result = previous + previousToPrevious;
            previousToPrevious = previous;
            previous = result;
        }

        return result;
    }
}
