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

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * HTTP client configuration.
 * 
 * @author skatzman
 */
public class HttpClientConfiguration {
    private final String userAgent;

    private final boolean isRedirectHandlingEnabled;

    private final boolean isContentCompressionEnabled;

    private final ListMultimap<String, String> defaultHeaders;

    private final boolean isEvictingExpiredConnections;

    private final boolean isEvictingIdleConnections;

    private final int maxConnTotal;

    private final int maxConnPerRoute;

    private final long maxIdleTimeSeconds;

    private final long socketTimeoutMinutes;

    private final long connectTimeoutSeconds;

    private final long timeToLiveSeconds;

    private final ProxyConfiguration proxyConfiguration;

    private final int maxRetries;

    private final Set<Integer> retriableCodes;

    private final long defaultRetryIntervalMs;

    /*
     * Package protected.
     */
    HttpClientConfiguration(String userAgent,
            boolean isRedirectHandlingEnabled,
            boolean isContentCompressionEnabled,
            ListMultimap<String, String> defaultHeaders,
            boolean isEvictingExpiredConnections,
            boolean isEvictingIdleConnections,
            int maxConnTotal,
            int maxConnPerRoute,
            long maxIdleTimeSeconds,
            long socketTimeoutMinutes,
            long connectTimeoutSeconds,
            long timeToLiveSeconds,
            ProxyConfiguration proxyConfiguration,
            int maxRetries,
            Set<Integer> retriableCodes,
            long defaultRetryIntervalMs) {
        this.userAgent = userAgent;
        this.isRedirectHandlingEnabled = isRedirectHandlingEnabled;
        this.isContentCompressionEnabled = isContentCompressionEnabled;
        this.defaultHeaders = (defaultHeaders != null) ? defaultHeaders : ImmutableListMultimap.of();
        this.isEvictingExpiredConnections = isEvictingExpiredConnections;
        this.isEvictingIdleConnections = isEvictingIdleConnections;
        this.maxConnTotal = maxConnTotal;
        this.maxConnPerRoute = maxConnPerRoute;
        this.maxIdleTimeSeconds = maxIdleTimeSeconds;
        this.socketTimeoutMinutes = socketTimeoutMinutes;
        this.connectTimeoutSeconds = connectTimeoutSeconds;
        this.timeToLiveSeconds = timeToLiveSeconds;
        this.proxyConfiguration = proxyConfiguration;
        this.maxRetries = maxRetries;
        this.retriableCodes = (retriableCodes != null) ? retriableCodes : Collections.emptySet();
        this.defaultRetryIntervalMs = defaultRetryIntervalMs;
    }

    public Optional<String> getUserAgent() {
        return Optional.ofNullable(userAgent);
    }

    public boolean isRedirectHandlingEnabled() {
        return isRedirectHandlingEnabled;
    }

    public boolean isContentCompressionEnabled() {
        return isContentCompressionEnabled;
    }

    public ListMultimap<String, String> getDefaultHeaders() {
        return defaultHeaders;
    }

    public boolean isEvictingExpiredConnections() {
        return isEvictingExpiredConnections;
    }

    public boolean isEvictingIdleConnections() {
        return isEvictingIdleConnections;
    }

    public int getMaxConnTotal() {
        return maxConnTotal;
    }

    public int getMaxConnPerRoute() {
        return maxConnPerRoute;
    }

    public long getMaxIdleTimeSeconds() {
        return maxIdleTimeSeconds;
    }

    public long getSocketTimeoutMinutes() {
        return socketTimeoutMinutes;
    }

    public long getConnectTimeoutSeconds() {
        return connectTimeoutSeconds;
    }

    public long getTimeToLiveSeconds() {
        return timeToLiveSeconds;
    }

    public Optional<ProxyConfiguration> getProxyConfiguration() {
        return Optional.ofNullable(proxyConfiguration);
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public Set<Integer> getRetriableCodes() {
        return retriableCodes;
    }

    public long getDefaultRetryIntervalMs() {
        return defaultRetryIntervalMs;
    }
}
