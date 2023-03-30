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

import java.util.Objects;
import java.util.function.Supplier;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * KnowledgeBase access configuration.
 * 
 * @author skatzman
 */
public class KbConfiguration {
    public static final String DEFAULT_SCHEME = "https";

    public static final String DEFAULT_HOST = "kb.blackducksoftware.com";

    public static final int DEFAULT_PORT = 443;

    private final String scheme;

    private final String host;

    private final int port;

    private final Supplier<String> licenseKeySupplier;

    /**
     * Constructs a KB configuration given the license key.
     * 
     * Uses default scheme, host, and port properties.
     * 
     * Presumes a static license key that will not change at runtime.
     * 
     * @param licenseKey
     *            The license key.
     */
    public KbConfiguration(String licenseKey) {
        this(DEFAULT_SCHEME, DEFAULT_HOST, DEFAULT_PORT, new StaticLicenseKeySupplier(licenseKey));
    }

    /**
     * Constructs a KB configuration given the license key.
     * 
     * Uses default scheme, host, and port properties.
     * 
     * Enables explicit definition of a license key supplier for callers to enable flexibility to, for example, define a
     * dynamic license key provider that enables rotation of the license key at runtime.
     * 
     * @param licenseKeySupplier
     *            The license key supplier.
     */
    public KbConfiguration(Supplier<String> licenseKeySupplier) {
        this(DEFAULT_SCHEME, DEFAULT_HOST, DEFAULT_PORT, licenseKeySupplier);
    }

    /**
     * Creates a KB configuration.
     * 
     * Presumes a static license key that will not change at runtime.
     * 
     * @param scheme
     *            The scheme.
     * @param host
     *            The host.
     * @param port
     *            The port.
     * @param licenseKey
     *            The license key.
     */
    public KbConfiguration(String scheme, String host, int port, String licenseKey) {
        this(scheme, host, port, new StaticLicenseKeySupplier(licenseKey));
    }

    /**
     * Creates a KB configuration.
     * 
     * Enables explicit definition of a license key supplier for callers to enable flexibility to, for example, define a
     * dynamic license key provider that enables rotation of the license key at runtime.
     * 
     * @param scheme
     *            The scheme.
     * @param host
     *            The host.
     * @param port
     *            The port.
     * @param licenseKeySupplier
     *            The license key supplier.
     */
    public KbConfiguration(String scheme, String host, int port, Supplier<String> licenseKeySupplier) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(scheme), "Scheme must not be null or empty.");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(host), "Host must not be null or empty.");
        Preconditions.checkArgument(port > 0, "Port must be greater than 0.");
        Objects.requireNonNull(licenseKeySupplier, "License key supplier must be initialized.");

        this.scheme = scheme;
        this.host = host;
        this.port = port;
        this.licenseKeySupplier = licenseKeySupplier;
    }

    public String getScheme() {
        return scheme;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public Supplier<String> getLicenseKeySupplier() {
        return licenseKeySupplier;
    }

    @Override
    public String toString() {
        return scheme + "://" + host + ':' + port;
    }
}
