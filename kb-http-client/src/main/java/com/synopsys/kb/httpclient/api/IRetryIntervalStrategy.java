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

/**
 * Retry interval strategy.
 * 
 * Enables configuration of a strategy for defining retry interval based off of the current execution count.
 * 
 * @author skatzman
 */
public interface IRetryIntervalStrategy {
    /**
     * Determines the retry interval given the execution count.
     * 
     * @param execCount
     *            The execution count.
     * @return Returns the retry interval.
     */
    TimeValue determineRetryInterval(int execCount);
}
