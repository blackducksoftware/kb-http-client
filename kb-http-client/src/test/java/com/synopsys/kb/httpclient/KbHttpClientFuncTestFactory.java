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

import org.testng.annotations.Factory;
import org.testng.annotations.Test;

import com.synopsys.kb.httpclient.client.KbAuthenticationHttpClientFuncTest;
import com.synopsys.kb.httpclient.client.KbComponentHttpClientFuncTest;
import com.synopsys.kb.httpclient.client.KbComponentVariantHttpClientFuncTest;
import com.synopsys.kb.httpclient.client.KbComponentVersionHttpClientFuncTest;
import com.synopsys.kb.httpclient.client.KbLicenseHttpClientFuncTest;

/**
 * KB HTTP client functional test factory.
 * 
 * @author skatzman
 */
public class KbHttpClientFuncTestFactory {
    @Test
    @Factory
    public Object[] tests() {
        return new Object[] {
                new KbAuthenticationHttpClientFuncTest(),
                new KbComponentHttpClientFuncTest(),
                new KbComponentVersionHttpClientFuncTest(),
                new KbComponentVariantHttpClientFuncTest(),
                new KbLicenseHttpClientFuncTest(),
        };
    }
}
