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

import org.apache.hc.client5.http.impl.DefaultConnectionKeepAliveStrategy;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.TimeValue;

/**
 * Finite connection keep alive strategy.
 * 
 * Package protected.
 * 
 * @author skatzman
 */
class FiniteConnectionKeepAliveStrategy extends DefaultConnectionKeepAliveStrategy {
    private final long timeToLiveSeconds;

    FiniteConnectionKeepAliveStrategy(long timeToLiveSeconds) {
        this.timeToLiveSeconds = timeToLiveSeconds;
    }

    @Override
    public TimeValue getKeepAliveDuration(HttpResponse response, HttpContext context) {
        TimeValue keepAliveDuration = super.getKeepAliveDuration(response, context);

        // If no keep alive duration is defined, returned the override value.
        if (null == keepAliveDuration) {
            return TimeValue.ofSeconds(timeToLiveSeconds);
        }

        // If keep alive is less than or equal to 0, return the override value.
        long keepAliveDurationMs = keepAliveDuration.convert(TimeUnit.MILLISECONDS);
        if (keepAliveDurationMs <= 0L) {
            return TimeValue.ofSeconds(timeToLiveSeconds);
        }

        // Otherwise, return as-is.
        return keepAliveDuration;
    }
}
