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
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.hc.core5.http.HttpStatus;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;

/**
 * HTTP client configuration builder.
 * 
 * @author skatzman
 */
public class HttpClientConfigurationBuilder {
    private String userAgent;

    private boolean isRedirectHandlingEnabled = false;

    private boolean isContentCompressionEnabled = false;

    private ListMultimap<String, String> defaultHeaders = ImmutableListMultimap.of();

    private boolean isEvictingExpiredConnections = false;

    private boolean isEvictingIdleConnections = false;

    private int maxConnTotal = 256;

    private int maxConnPerRoute = 20;

    private long maxIdleTimeSeconds = TimeUnit.SECONDS.toSeconds(60L);

    private long socketTimeoutMinutes = TimeUnit.MINUTES.toMinutes(3L);

    private long connectTimeoutSeconds = TimeUnit.SECONDS.toSeconds(5L);

    private long timeToLiveSeconds = TimeUnit.MINUTES.toSeconds(5L);

    private ProxyConfiguration proxyConfiguration;

    private int maxRetries = 3;

    // Defaults of 408, 429, 502, 503, and 504
    private Set<Integer> retriableCodes = ImmutableSet.of(HttpStatus.SC_REQUEST_TIMEOUT,
            HttpStatus.SC_TOO_MANY_REQUESTS,
            HttpStatus.SC_BAD_GATEWAY,
            HttpStatus.SC_SERVICE_UNAVAILABLE,
            HttpStatus.SC_GATEWAY_TIMEOUT);

    private long defaultRetryIntervalMs = TimeUnit.MILLISECONDS.toMillis(1000L);

    private HttpClientConfigurationBuilder() {
    }

    /**
     * Creates a new builder.
     * 
     * @return Returns a new builder.
     */
    public static HttpClientConfigurationBuilder create() {
        return new HttpClientConfigurationBuilder();
    }

    /**
     * Defines the User-Agent header value.
     * 
     * The default system User-Agent header will be used by default if undefined.
     * 
     * @param userAgent
     *            The User-Agent header value.
     * @return Returns the builder.
     */
    public HttpClientConfigurationBuilder userAgent(String userAgent) {
        this.userAgent = userAgent;

        return this;
    }

    /**
     * Enabled or disables automatic redirect handling.
     * 
     * Redirect handling is disabled by default if undefined.
     * 
     * @param isRedirectHandlingEnabled
     *            The redirect handling flag.
     * @return Returns the builder.
     */
    public HttpClientConfigurationBuilder redirectHandling(boolean isRedirectHandlingEnabled) {
        this.isRedirectHandlingEnabled = isRedirectHandlingEnabled;

        return this;
    }

    /**
     * Enabled or disables automatic content decompression.
     * 
     * Content compression is disabled by default if undefined.
     * 
     * @param isContentCompressionEnabled
     *            The content compression flag.
     * @return Returns the builder.
     */
    public HttpClientConfigurationBuilder contentCompression(boolean isContentCompressionEnabled) {
        this.isContentCompressionEnabled = isContentCompressionEnabled;

        return this;
    }

    /**
     * Defines default request header values.
     * 
     * Headers with null or empty names or values will automatically be ignored.
     * 
     * No default headers are added by default.
     * 
     * @param defaultHeaders
     *            The default request header values.
     * @return Returns the builder.
     */
    public HttpClientConfigurationBuilder defaultHeaders(ListMultimap<String, String> defaultHeaders) {
        this.defaultHeaders = (defaultHeaders != null) ? defaultHeaders : ImmutableListMultimap.of();

        return this;
    }

    /**
     * Enables or disables proactive eviction of expired connections from the connection pool using a background thread.
     * 
     * Expired connections are not proactively evicted by default.
     * 
     * @param isEvictingExpiredConnections
     *            The evicting expired connection flag.
     * @return Returns the builder.
     */
    public HttpClientConfigurationBuilder evictingExpiredConnections(boolean isEvictingExpiredConnections) {
        this.isEvictingExpiredConnections = isEvictingExpiredConnections;

        return this;
    }

    /**
     * Enables or disables proactive eviction of idle connections from the connection pool using a background thread.
     * 
     * Idle connections are not proactively evicted by default.
     * 
     * @param isEvictingIdleConnections
     *            The evicting idle connections flag.
     * @return Returns the builder.
     */
    public HttpClientConfigurationBuilder evictingIdleConnections(boolean isEvictingIdleConnections) {
        this.isEvictingIdleConnections = isEvictingIdleConnections;

        return this;
    }

    /**
     * Sets the maximum total connections value.
     * 
     * Maximum total connections value is 256 by default if undefined.
     * 
     * Must be greater than 0.
     * 
     * @param maxConnTotal
     *            The maximum total connections value.
     * @return Returns the builder.
     */
    public HttpClientConfigurationBuilder maxConnTotal(int maxConnTotal) {
        Preconditions.checkArgument(maxConnTotal > 0, "Maximum total connections must be greater than 0.");

        this.maxConnTotal = maxConnTotal;

        return this;
    }

    /**
     * Sets the maximum connections per route value.
     * 
     * Maximum connections per route value is 20 by default if undefined.
     * 
     * Must be greater than 0.
     * 
     * @param maxConnPerRoute
     *            The maximum connections per route value.
     * @return Returns the builder.
     */
    public HttpClientConfigurationBuilder maxConnPerRoute(int maxConnPerRoute) {
        Preconditions.checkArgument(maxConnPerRoute > 0, "Maximum connections per route must be greater than 0.");

        this.maxConnPerRoute = maxConnPerRoute;

        return this;
    }

    /**
     * Sets the maximum idle time in seconds that persistent connections can stay idle while kept alive in the
     * connection pool. Connections whose inactivity period exceeds this value will get closed and evicted from the
     * connection pool.
     * 
     * This value is applicable if and only if the evicting idle connections flag is enabled.
     * 
     * Max idle time seconds is 60 seconds by default if undefined.
     * 
     * Must be greater than 0.
     * 
     * @param maxIdleTimeSeconds
     *            The max idle time in seconds.
     * @return Returns the builder.
     */
    public HttpClientConfigurationBuilder maxIdleTimeSeconds(long maxIdleTimeSeconds) {
        Preconditions.checkArgument(maxIdleTimeSeconds > 0, "Maximum idle time must be greater than 0.");

        this.maxIdleTimeSeconds = maxIdleTimeSeconds;

        return this;

    }

    /**
     * Sets the socket timeout in minutes.
     * 
     * The socket timeout is the time to wait for data after establishing the connection - the maximum time of
     * inactivity allowed between two data packets.
     * 
     * Socket timeout is 3 minutes by default if undefined.
     * 
     * Must be greater than or equal to 0.
     * 
     * @param socketTimeoutMinutes
     *            The socket timeout in minutes.
     * @return Returns the builder.
     */
    public HttpClientConfigurationBuilder socketTimeoutMinutes(long socketTimeoutMinutes) {
        Preconditions.checkArgument(socketTimeoutMinutes >= 0L, "Socket timeout in minutes must be greater than or equal to 0.");

        this.socketTimeoutMinutes = socketTimeoutMinutes;

        return this;
    }

    /**
     * Sets the connect timeout in seconds.
     * 
     * The connect timeout is the time to establish connection with the remote host.
     * 
     * Connect timeout is 5 seconds by default if undefined.
     * 
     * Must be greater than or equal to 0.
     * 
     * @param connectTimeoutSeconds
     *            The connect timeout in seconds.
     * @return Returns the builder.
     */
    public HttpClientConfigurationBuilder connectTimeoutSeconds(long connectTimeoutSeconds) {
        Preconditions.checkArgument(connectTimeoutSeconds >= 0L, "Connect timeout in seconds must be greater than or equal to 0.");

        this.connectTimeoutSeconds = connectTimeoutSeconds;

        return this;
    }

    /**
     * Sets the connection time-to-live in seconds.
     * 
     * Connection time-to-live is 300 seconds (5 minutes) by default if undefined.
     * 
     * Must be greater than or equal to 0.
     * 
     * @param timeToLiveSeconds
     *            The time to live in seconds.
     * @return Returns the builder.
     */
    public HttpClientConfigurationBuilder timeToLiveSeconds(long timeToLiveSeconds) {
        Preconditions.checkArgument(timeToLiveSeconds >= 0L, "Time-to-live in seconds must be greater than or equal to 0.");
        
        this.timeToLiveSeconds = timeToLiveSeconds;

        return this;
    }

    /**
     * Defines the proxy configuration.
     * 
     * Proxy configuration is undefined by default.
     * 
     * @param proxyConfiguration
     *            The proxy configuration.
     * @return Returns the builder.
     */
    public HttpClientConfigurationBuilder proxyConfiguration(ProxyConfiguration proxyConfiguration) {
        this.proxyConfiguration = proxyConfiguration;

        return this;
    }

    /**
     * Defines the maximum number of request retries given an unexpected error or response code.
     * 
     * Maximum number of request retries is 3 by default if undefined.
     * 
     * Must be greater than or equal to 0.
     * 
     * @param maxRetries
     *            The maximum number of request retries.
     * @return Returns the builder.
     */
    public HttpClientConfigurationBuilder maxRetries(int maxRetries) {
        Preconditions.checkArgument(maxRetries >= 0L, "Maximum number of request retries must be greater than or equal to 0.");

        this.maxRetries = maxRetries;

        return this;
    }

    /**
     * Defines the retriable response codes.
     * 
     * Retriable response codes include 408 Request Timeout, 429 Too Many Requests, 502 Bad Gateway, 503 Service
     * Unavailable, and 504 Gateway Timeout by default if undefined.
     * 
     * @param retriableCodes
     *            The retriable response codes.
     * @return Returns the builder.
     */
    public HttpClientConfigurationBuilder retriableCodes(Set<Integer> retriableCodes) {
        this.retriableCodes = (retriableCodes != null) ? retriableCodes : Collections.emptySet();

        return this;
    }

    /**
     * Defines the default retry interval in milliseconds.
     * 
     * Default retry interval in milliseconds is 1000 milliseconds by default if undefined.
     * 
     * Must be greater than or equal to 0.
     * 
     * @param defaultRetryIntervalMs
     *            The default retry interval in milliseconds.
     * @return Returns the builder.
     */
    public HttpClientConfigurationBuilder defaultRetryIntervalMs(long defaultRetryIntervalMs) {
        Preconditions.checkArgument(defaultRetryIntervalMs >= 0L, "Default retry interval must be greater than or equal to 0.");

        this.defaultRetryIntervalMs = defaultRetryIntervalMs;

        return this;
    }

    /**
     * Builds the configuration.
     * 
     * @return Returns the configuration.
     */
    public HttpClientConfiguration build() {
        HttpClientConfiguration httpClientConfiguration = new HttpClientConfiguration(this.userAgent, this.isRedirectHandlingEnabled,
                this.isContentCompressionEnabled, this.defaultHeaders, this.isEvictingExpiredConnections, this.isEvictingIdleConnections,
                this.maxConnTotal, this.maxConnPerRoute, this.maxIdleTimeSeconds, this.socketTimeoutMinutes, this.connectTimeoutSeconds, this.timeToLiveSeconds,
                this.proxyConfiguration, this.maxRetries, this.retriableCodes, this.defaultRetryIntervalMs);

        return httpClientConfiguration;
    }
}
