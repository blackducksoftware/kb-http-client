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
package com.synopsys.kb.httpclient;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.testng.SkipException;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import com.synopsys.kb.httpclient.api.HttpClientConfiguration;
import com.synopsys.kb.httpclient.api.HttpClientConfigurationBuilder;
import com.synopsys.kb.httpclient.api.IKbHttpApi;
import com.synopsys.kb.httpclient.api.KbConfiguration;
import com.synopsys.kb.httpclient.api.KbHttpClientFactory;

/**
 * Abstract functional test.
 * 
 * @author skatzman
 */
public abstract class AbstractFuncTest {
    private static final String LICENSE_KEY_PATH_PROPERTY = "synopsys_kb_httpclient_license_key_path";

    private volatile IKbHttpApi kbHttpApi;

    /**
     * Gets the KB HTTP API.
     * 
     * Throws a TestNG SkipException if the license key cannot be retrieved.
     * 
     * @return Returns the KB HTTP API.
     */
    protected synchronized IKbHttpApi getKbHttpApi() {
        if (null == kbHttpApi) {
            HttpClientConfiguration httpClientConfiguration = HttpClientConfigurationBuilder.create().userAgent("KB HTTP Client/latest").build();
            // Requires KB test product registration key.
            String licenseKey = getLicenseKey();
            KbConfiguration kbConfiguration = new KbConfiguration("https", "kbtest.blackducksoftware.com", 443, licenseKey);

            this.kbHttpApi = new KbHttpClientFactory().create(httpClientConfiguration, kbConfiguration);
        }

        return this.kbHttpApi;
    }

    /**
     * Gets the license key.
     * 
     * Throws a TestNG SkipException if the license key cannot be retrieved.
     * 
     * @return Returns the license key.
     */
    protected String getLicenseKey() {
        String licenseKey = readLicenseKey().orElse(null);

        if (null == licenseKey) {
            throw new SkipException("License key could not be read.");
        }

        return licenseKey;
    }

    private Optional<String> readLicenseKey() {
        String licenseKey = null;

        String licenseKeyPath = System.getProperty(LICENSE_KEY_PATH_PROPERTY);
        if (!Strings.isNullOrEmpty(licenseKeyPath)) {
            File file = new File(licenseKeyPath);
            CharSource charSource = Files.asCharSource(file, Charsets.UTF_8);
            try {
                String line = charSource.readFirstLine();
                licenseKey = line.trim();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return Optional.ofNullable(licenseKey);
    }
}
