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

import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http.message.BasicHttpResponse;
import org.apache.hc.core5.http.protocol.BasicHttpContext;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.TimeValue;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.synopsys.kb.httpclient.AbstractTest;

/**
 * Finite connection keep alive strategy test.
 * 
 * @author skatzman
 */
public class FiniteConnectionKeepAliveStrategyTest extends AbstractTest {
    private static final long TIME_TO_LIVE_SECONDS = TimeUnit.MINUTES.toSeconds(3L);

    private HttpContext context;

    private ConnectionKeepAliveStrategy strategy;

    @BeforeMethod
    public void beforeMethod() {
        context = new BasicHttpContext();
        strategy = new FiniteConnectionKeepAliveStrategy(TIME_TO_LIVE_SECONDS);
    }

    @Test
    public void testGetKeepAliveDurationWithKeepAliveDuration() {
        HttpResponse response = new BasicHttpResponse(HttpStatus.SC_OK);
        response.addHeader(new BasicHeader(HttpHeaders.KEEP_ALIVE, "timeout=20"));
        TimeValue keepAliveDurstion = strategy.getKeepAliveDuration(response, context);

        Assert.assertEquals(keepAliveDurstion, TimeValue.ofSeconds(20), "Keep alive durations should be equal.");
    }

    @Test
    public void testGetKeepAliveDurationWithNullKeepAliveDuration() {
        HttpResponse response = new BasicHttpResponse(HttpStatus.SC_OK);
        response.addHeader(new BasicHeader(HttpHeaders.KEEP_ALIVE, null));
        TimeValue keepAliveDurstion = strategy.getKeepAliveDuration(response, context);

        Assert.assertEquals(keepAliveDurstion, TimeValue.ofSeconds(TIME_TO_LIVE_SECONDS), "Keep alive durations should be equal.");
    }

    @Test
    public void testGetKeepAliveDurationWithInvalidKeepAliveDuration() {
        HttpResponse response = new BasicHttpResponse(HttpStatus.SC_OK);
        response.addHeader(new BasicHeader(HttpHeaders.KEEP_ALIVE, "wee=200"));
        TimeValue keepAliveDurstion = strategy.getKeepAliveDuration(response, context);

        Assert.assertEquals(keepAliveDurstion, TimeValue.ofSeconds(TIME_TO_LIVE_SECONDS), "Keep alive durations should be equal.");
    }

    @Test
    public void testGetKeepAliveDurationWithIncompleteKeepAliveDuration() {
        HttpResponse response = new BasicHttpResponse(HttpStatus.SC_OK);
        response.addHeader(new BasicHeader(HttpHeaders.KEEP_ALIVE, "max=400"));
        TimeValue keepAliveDurstion = strategy.getKeepAliveDuration(response, context);

        Assert.assertEquals(keepAliveDurstion, TimeValue.ofSeconds(TIME_TO_LIVE_SECONDS), "Keep alive durations should be equal.");
    }

    @Test
    public void testGetKeepAliveDurationWithNegativeKeepAliveDuration() {
        HttpResponse response = new BasicHttpResponse(HttpStatus.SC_OK);
        response.addHeader(new BasicHeader(HttpHeaders.KEEP_ALIVE, "timeout=-100"));
        TimeValue keepAliveDurstion = strategy.getKeepAliveDuration(response, context);

        Assert.assertEquals(keepAliveDurstion, TimeValue.ofSeconds(TIME_TO_LIVE_SECONDS), "Keep alive durations should be equal.");
    }

    @Test
    public void testGetKeepAliveDuration() {
        HttpResponse response = new BasicHttpResponse(HttpStatus.SC_OK);
        TimeValue keepAliveDurstion = strategy.getKeepAliveDuration(response, context);

        Assert.assertEquals(keepAliveDurstion, TimeValue.ofSeconds(TIME_TO_LIVE_SECONDS), "Keep alive durations should be equal.");
    }
}
