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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.HttpRequestRetryStrategy;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

import com.google.common.base.Strings;
import com.google.common.collect.ListMultimap;
import com.synopsys.kb.httpclient.client.KbActivityHttpClient;
import com.synopsys.kb.httpclient.client.KbAuthenticationHttpClient;
import com.synopsys.kb.httpclient.client.KbComponentHttpClient;
import com.synopsys.kb.httpclient.client.KbComponentVariantHttpClient;
import com.synopsys.kb.httpclient.client.KbComponentVersionHttpClient;
import com.synopsys.kb.httpclient.client.KbLicenseHttpClient;
import com.synopsys.kb.httpclient.client.KbVulnerabilityHttpClient;
import com.synopsys.kb.httpclient.util.JsonUtil;

/**
 * KnowledgeBase HTTP client factory.
 * 
 * @author skatzman
 */
public class KbHttpClientFactory {
    public KbHttpClientFactory() {
    }

    /**
     * Creates the KB HTTP client.
     * 
     * @param httpClientConfiguration
     *            The HTTP client configuration.
     * @param kbConfiguration
     *            The KB configuration.
     * @return Returns the KB HTTP client.
     */
    public IKbHttpApi create(HttpClientConfiguration httpClientConfiguration,
            KbConfiguration kbConfiguration) {
        Objects.requireNonNull(httpClientConfiguration, "HTTP client configuration must be initialized.");
        Objects.requireNonNull(kbConfiguration, "KB configuration must be initialized.");

        CloseableHttpClient unauthorizedHttpClient = constructUnauthorizedHttpClient(httpClientConfiguration);
        IAuthenticationApi authenticationApi = new KbAuthenticationHttpClient(kbConfiguration, JsonUtil.objectMapper, unauthorizedHttpClient);

        Supplier<String> licenseKeySupplier = kbConfiguration.getLicenseKeySupplier();
        AuthorizationProvider authorizationProvider = new AuthorizationProvider(licenseKeySupplier, authenticationApi);
        CloseableHttpClient authorizedHttpClient = constructAuthorizedHttpClient(httpClientConfiguration, authorizationProvider);
        IComponentApi componentApi = new KbComponentHttpClient(kbConfiguration, JsonUtil.objectMapper, authorizedHttpClient, authorizationProvider);
        IComponentVersionApi componentVersionApi = new KbComponentVersionHttpClient(kbConfiguration, JsonUtil.objectMapper, authorizedHttpClient,
                authorizationProvider);
        IComponentVariantApi componentVariantApi = new KbComponentVariantHttpClient(kbConfiguration, JsonUtil.objectMapper, authorizedHttpClient,
                authorizationProvider);
        ILicenseApi licenseApi = new KbLicenseHttpClient(kbConfiguration, JsonUtil.objectMapper, authorizedHttpClient, authorizationProvider);
        IVulnerabilityApi vulnerabilityApi = new KbVulnerabilityHttpClient(kbConfiguration, JsonUtil.objectMapper, authorizedHttpClient, authorizationProvider);
        IActivityApi activityApi = new KbActivityHttpClient(kbConfiguration, JsonUtil.objectMapper, authorizedHttpClient, authorizationProvider);

        return new KbHttpApi(authenticationApi, componentApi, componentVersionApi, componentVariantApi, licenseApi, vulnerabilityApi, activityApi);
    }

    private CloseableHttpClient constructUnauthorizedHttpClient(HttpClientConfiguration httpClientConfiguration) {
        HttpClientBuilder builder = constructDefaultHttpClientBuilder(httpClientConfiguration);

        return builder.build();
    }

    private CloseableHttpClient constructAuthorizedHttpClient(HttpClientConfiguration httpClientConfiguration,
            AuthorizationProvider authorizationProvider) {
        HttpClientBuilder builder = constructDefaultHttpClientBuilder(httpClientConfiguration);

        // In addition, add request interceptor to automatically populate Authorization header.
        HttpRequestInterceptor authorizationHttpRequestInterceptor = new AuthorizationHttpRequestInterceptor(authorizationProvider);
        builder = builder.addRequestInterceptorLast(authorizationHttpRequestInterceptor);

        return builder.build();
    }

    private HttpClientBuilder constructDefaultHttpClientBuilder(HttpClientConfiguration httpClientConfiguration) {
        HttpClientBuilder builder = HttpClientBuilder.create();

        // User agent header value.
        if (httpClientConfiguration.getUserAgent().isPresent()) {
            builder = builder.disableDefaultUserAgent();
            String userAgent = httpClientConfiguration.getUserAgent().orElse(null);
            builder = builder.setUserAgent(userAgent);
        }

        // Redirect handling.
        if (!httpClientConfiguration.isRedirectHandlingEnabled()) {
            builder = builder.disableRedirectHandling();
        }

        // Content compression.
        if (!httpClientConfiguration.isContentCompressionEnabled()) {
            builder = builder.disableContentCompression();
        }

        // Default headers to add to every request.
        if (!httpClientConfiguration.getDefaultHeaders().isEmpty()) {
            Collection<Header> defaultHeaders = new ArrayList<>();
            ListMultimap<String, String> providedDefaultHeaders = httpClientConfiguration.getDefaultHeaders();
            for (Entry<String, String> entry : providedDefaultHeaders.entries()) {
                String headerName = entry.getKey();
                String headerValue = entry.getValue();
                if (!Strings.isNullOrEmpty(headerName) && !Strings.isNullOrEmpty(headerValue)) {
                    Header header = new BasicHeader(headerName, headerValue);
                    defaultHeaders.add(header);
                }
            }

            if (!defaultHeaders.isEmpty()) {
                builder = builder.setDefaultHeaders(defaultHeaders);
            }
        }

        // Background thread to periodically evict expired connections.
        if (httpClientConfiguration.isEvictingExpiredConnections()) {
            builder = builder.evictExpiredConnections();
        }

        // Background thread to periodically evict idle connections.
        if (httpClientConfiguration.isEvictingIdleConnections()) {
            long maxIdleTimeSeconds = httpClientConfiguration.getMaxIdleTimeSeconds();

            TimeValue maxIdleTime = (maxIdleTimeSeconds > 0L) ? TimeValue.ofSeconds(maxIdleTimeSeconds) : TimeValue.ofMinutes(1L);
            builder = builder.evictIdleConnections(maxIdleTime);
        }

        // Proxy configuration
        if (httpClientConfiguration.getProxyConfiguration().isPresent()) {
            ProxyConfiguration proxyConfiguration = httpClientConfiguration.getProxyConfiguration().orElse(null);
            String proxyScheme = proxyConfiguration.getScheme();
            String proxyHost = proxyConfiguration.getHost();
            int proxyPort = proxyConfiguration.getPort();
            HttpHost proxy = new HttpHost(proxyScheme, proxyHost, proxyPort);
            builder = builder.setProxy(proxy);

            if (proxyConfiguration.getUserName().isPresent() && proxyConfiguration.getPassword().isPresent()) {
                String proxyUserName = proxyConfiguration.getUserName().orElse(null);
                String proxyPassword = proxyConfiguration.getPassword().orElse(null);
                BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                AuthScope proxyAuthScope = new AuthScope(proxyHost, proxyPort);
                Credentials proxyCredentials = new UsernamePasswordCredentials(proxyUserName, proxyPassword.toCharArray());
                credentialsProvider.setCredentials(proxyAuthScope, proxyCredentials);
                builder = builder.setDefaultCredentialsProvider(credentialsProvider);
            }
        }

        // Connection manager
        int maxConnTotal = httpClientConfiguration.getMaxConnTotal();
        int maxConnPerRoute = httpClientConfiguration.getMaxConnPerRoute();
        long socketTimeoutMinutes = httpClientConfiguration.getSocketTimeoutMinutes();
        long connectTimeoutSeconds = httpClientConfiguration.getConnectTimeoutSeconds();
        long timeToLiveSeconds = httpClientConfiguration.getTimeToLiveSeconds();
        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
                        .setSslContext(SSLContexts.createSystemDefault())
                        .setTlsVersions(TLS.V_1_2, TLS.V_1_3)
                        .build())
                .setDefaultSocketConfig(SocketConfig.custom()
                        .setSoTimeout(Timeout.ofMinutes(socketTimeoutMinutes))
                        .build())
                .setPoolConcurrencyPolicy(PoolConcurrencyPolicy.STRICT)
                .setConnPoolPolicy(PoolReusePolicy.LIFO)
                .setMaxConnTotal(maxConnTotal)
                .setMaxConnPerRoute(maxConnPerRoute)
                .setDefaultConnectionConfig(ConnectionConfig.custom()
                        .setSocketTimeout(Timeout.ofMinutes(socketTimeoutMinutes))
                        .setConnectTimeout(Timeout.ofSeconds(connectTimeoutSeconds))
                        .setTimeToLive(TimeValue.ofSeconds(timeToLiveSeconds))
                        .build())
                .build();
        builder = builder.setConnectionManager(poolingHttpClientConnectionManager);

        // Keep-alive strategy
        ConnectionKeepAliveStrategy connectionKeepAliveStrategy = new FiniteConnectionKeepAliveStrategy(timeToLiveSeconds);
        builder = builder.setKeepAliveStrategy(connectionKeepAliveStrategy);

        // Retry strategy for subsets of unexpected errors and unexpected HTTP response codes.
        int maxRetries = httpClientConfiguration.getMaxRetries();
        Set<Integer> retriableCodes = httpClientConfiguration.getRetriableCodes();
        IRetryIntervalStrategy retryStrategyInterval = httpClientConfiguration.getRetryIntervalStrategy()
                .orElseGet(() -> new FixedRetryIntervalStrategy(1000L));
        HttpRequestRetryStrategy httpRequestRetryStrategy = new CustomHttpRequestRetryStrategy(maxRetries, retriableCodes, retryStrategyInterval);
        builder = builder.setRetryStrategy(httpRequestRetryStrategy);

        return builder;
    }
}
