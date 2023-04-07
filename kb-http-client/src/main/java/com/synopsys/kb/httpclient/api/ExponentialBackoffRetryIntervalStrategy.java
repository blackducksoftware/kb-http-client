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

import java.util.concurrent.ThreadLocalRandom;

import org.apache.hc.core5.util.TimeValue;

import com.google.common.base.Preconditions;

/**
 * Exponential backoff retry interval strategy.
 * 
 * Determines and returns a exponential backoff-based retry interval given the execution count.
 * 
 * Based off the Exponential Backoff and Jitter blog article
 * (https://aws.amazon.com/blogs/architecture/exponential-backoff-and-jitter/) on the AWS Architecture Blog.
 * 
 * Adds a full jitter to the retry interval. Jitter adjusts the exponential backoff time interval by a small
 * random-based time change. The goal of jitter is to spread out the spikes of requests on the destination server
 * amongst many competiting client requests.
 * 
 * @author skatzman
 */
public class ExponentialBackoffRetryIntervalStrategy implements IRetryIntervalStrategy {
    private final long baseRetryIntervalMs;

    private final long maxRetryIntervalMs;

    /**
     * Constructs the exponential backoff retry interval strategy.
     * 
     * @param baseRetryIntervalMs
     *            The base retry interval in milliseconds. Must be greater than or equal to 0 milliseconds. Must be less
     *            than or equal to the maximum allowed retry interval in milliseconds.
     * @param maxRetryIntervalMs
     *            The maximum allowed retry interval in milliseconds. Must be greater than or equal to 0 milliseconds.
     *            Must be greater than or equal to the base retry interval in milliseconds.
     */
    public ExponentialBackoffRetryIntervalStrategy(long baseRetryIntervalMs, long maxRetryIntervalMs) {
        Preconditions.checkArgument(baseRetryIntervalMs >= 0L, "Base retry interval in milliseconds must be greater than or equal to 0.");
        Preconditions.checkArgument(maxRetryIntervalMs >= 0L, "Maximum allowed retry interval in milliseconds must be greater than or equal to 0.");
        Preconditions.checkArgument(baseRetryIntervalMs <= maxRetryIntervalMs,
                "Base retry interval in milliseconds must be less than or equal to the maximum allowed retry interval in milliseconds.");

        this.baseRetryIntervalMs = baseRetryIntervalMs;
        this.maxRetryIntervalMs = maxRetryIntervalMs;
    }

    @Override
    public TimeValue determineRetryInterval(int execCount) {
        // Safety to always put floor of 0 if the passed execution count is negative.
        int parsedExecCount = Math.max(execCount, 0);

        // v = min(maxRetryIntervalMs, (2^execCount * baseRetryIntervalMs));
        // sleep = random(0, v)
        double v = Math.min(maxRetryIntervalMs, (Math.pow(2.0d, parsedExecCount) * baseRetryIntervalMs));

        double sleep = 0.0d;
        if (0.0d < v) {
            sleep = ThreadLocalRandom.current().nextDouble(0.0d, v);
        }

        long sleepMs = Math.round(sleep);

        return TimeValue.ofMilliseconds(sleepMs);
    }
}
