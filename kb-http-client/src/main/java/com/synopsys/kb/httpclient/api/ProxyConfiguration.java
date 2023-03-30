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

import java.util.Optional;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * Proxy configuration.
 * 
 * @author skatzman
 */
public class ProxyConfiguration {
    private final String scheme;

    private final String host;

    private final int port;

    @Nullable
    private final String userName;

    @Nullable
    private final String password;

    public ProxyConfiguration(String scheme, String host, int port, @Nullable String userName, @Nullable String password) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(scheme), "Scheme must not be null or empty.");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(host), "Host must not be null or empty.");
        Preconditions.checkArgument(port > 0, "Port must be greater than 0.");

        this.scheme = scheme;
        this.host = host;
        this.port = port;
        this.userName = userName;
        this.password = password;
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

    public Optional<String> getUserName() {
        return Optional.ofNullable(userName);
    }

    public Optional<String> getPassword() {
        return Optional.ofNullable(password);
    }
}
