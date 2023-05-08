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
 * Fixed retry interval strategy.
 * 
 * Returns a fixed retry interval regardless of the execution timestamp.
 * 
 * For example, if a fixed retry interval with retry interval of 1000ms...
 * - The retry policy will wait 1000 milliseconds before attempting the 1st retry execution.
 * - The retry policy will wait 1000 milliseconds before attempting the 2nd retry execution.
 * - The retry policy will wait 1000 milliseconds before attempting the 3rd retry execution.
 * - ...
 * - The retry policy will wait 1000 milliseconds before attempting the nth retry execution.
 * 
 * @author skatzman
 */
public class FixedRetryIntervalStrategy implements IRetryIntervalStrategy {
    private final TimeValue fixedRetryInterval;

    /**
     * Constructs the fixed retry interval strategy.
     * 
     * @param fixedRetryIntervalMs
     *            The fixed retry interval in milliseconds. Must be greater than or equal to 0.
     */
    public FixedRetryIntervalStrategy(long fixedRetryIntervalMs) {
        Preconditions.checkArgument(fixedRetryIntervalMs >= 0L, "Fixed retry interval in milliseconds must be greater than or equal to 0.");

        this.fixedRetryInterval = TimeValue.ofMilliseconds(fixedRetryIntervalMs);

    }

    @Override
    public TimeValue determineRetryInterval(int execCount) {
        // Always return the same retry interval regardless of execution count.
        return fixedRetryInterval;
    }
}
