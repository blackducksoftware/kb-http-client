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

import com.google.common.base.Preconditions;

/**
 * Fibonacci retry interval strategy.
 * 
 * Determines and returns a fibonacci-based retry interval given the execution count.
 * 
 * For example, if a fibonacci retry interval with multiplier of 1 and maximum wait of 32...
 * - The retry policy will wait 1 seconds before attempting the 1st retry execution.
 * - The retry policy will wait 1 seconds before attempting the 2nd retry execution.
 * - The retry policy will wait 2 seconds before attempting the 3rd retry execution.
 * - The retry policy will wait 3 seconds before attempting the 4rd retry execution.
 * - The retry policy will wait 5 seconds before attempting the 5rd retry execution.
 * - ...
 * - The retry policy will wait fib(n) seconds before attempting the nth retry execution.
 * 
 * @author skatzman
 */
public class FibonacciRetryIntervalStrategy implements IRetryIntervalStrategy {
    private final long multiplier;

    private final long maxWaitSeconds;

    /**
     * Constructs the fibonacci retry strategy.
     * 
     * Uses a default multiplier of 1.
     * 
     * @param maxWaitSeconds
     *            The maximum wait in seconds. Must be greater than 1 - the default multiplier.
     */
    public FibonacciRetryIntervalStrategy(long maxWaitSeconds) {
        this(1L, maxWaitSeconds);
    }

    /**
     * Constructs the fibonacci retry interval strategy.
     * 
     * @param multiplier
     *            The multiplier. Must be greater than 0. Must be less than the maximum wait.
     * @param maxWaitSeconds
     *            The maximum wait in seconds. Must be greater than or equal to 0. Must be greater than the multiplier.
     */
    public FibonacciRetryIntervalStrategy(long multiplier, long maxWaitSeconds) {
        Preconditions.checkArgument(multiplier > 0L, "Multiplier must be greater than 0.");
        Preconditions.checkArgument(maxWaitSeconds >= 0L, "Maximum wait in seconds must be greater than or equal to 0.");
        Preconditions.checkArgument(multiplier < maxWaitSeconds, "Multiplier must be less than maximum wait in seconds.");

        this.multiplier = multiplier;
        this.maxWaitSeconds = maxWaitSeconds;
    }

    @Override
    public TimeValue determineRetryInterval(int execCount) {
        // Safety to always put floor of 0 if the passed execution count is negative.
        int parsedExecCount = Math.max(execCount, 0);

        long fibonacciResult = fibonacci(parsedExecCount);

        // Product by the multiplier.
        long result = multiplier * fibonacciResult;

        // Apply a ceiling given the defined maximum wait.
        if (result > maxWaitSeconds || result < 0L) {
            result = maxWaitSeconds;
        }

        result = (result >= 0L) ? result : 0L;

        return TimeValue.ofSeconds(result);
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
