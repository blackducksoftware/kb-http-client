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
    private volatile IKbHttpApi kbHttpApi;

    /**
     * Gets the KB HTTP API.
     * 
     * @return Returns the KB HTTP API.
     */
    protected synchronized IKbHttpApi getKbHttpApi() {
        if (null == kbHttpApi) {
            HttpClientConfiguration httpClientConfiguration = HttpClientConfigurationBuilder.create().userAgent("KB HTTP Client/latest").build();
            KbConfiguration kbConfiguration = new KbConfiguration("https", "kbtest.blackducksoftware.com", 443, "eng_hub_build");

            this.kbHttpApi = new KbHttpClientFactory().create(httpClientConfiguration, kbConfiguration);
        }

        return this.kbHttpApi;
    }
}
