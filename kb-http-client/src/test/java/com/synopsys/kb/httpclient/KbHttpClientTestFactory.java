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

import com.synopsys.kb.httpclient.api.AuthenticationExceptionTest;
import com.synopsys.kb.httpclient.api.AuthorizationHttpRequestInterceptorTest;
import com.synopsys.kb.httpclient.api.AuthorizationProviderTest;
import com.synopsys.kb.httpclient.api.CustomHttpRequestRetryStrategyTest;
import com.synopsys.kb.httpclient.api.FiniteConnectionKeepAliveStrategyTest;
import com.synopsys.kb.httpclient.api.HttpResponseTest;
import com.synopsys.kb.httpclient.api.KbConfigurationTest;
import com.synopsys.kb.httpclient.api.PageRequestTest;
import com.synopsys.kb.httpclient.api.ProxyConfigurationTest;
import com.synopsys.kb.httpclient.api.SortExpressionTest;
import com.synopsys.kb.httpclient.api.StaticLicenseKeySupplierTest;
import com.synopsys.kb.httpclient.model.AuthenticationTest;
import com.synopsys.kb.httpclient.model.BdsaVulnerabilityTest;
import com.synopsys.kb.httpclient.model.ComponentSearchResultTest;
import com.synopsys.kb.httpclient.model.ComponentTest;
import com.synopsys.kb.httpclient.model.ComponentVariantTest;
import com.synopsys.kb.httpclient.model.ComponentVersionTest;
import com.synopsys.kb.httpclient.model.CveVulnerabilityTest;
import com.synopsys.kb.httpclient.model.Cvss2ScoreTest;
import com.synopsys.kb.httpclient.model.Cvss2TemporalMetricsTest;
import com.synopsys.kb.httpclient.model.Cvss3ScoreTest;
import com.synopsys.kb.httpclient.model.Cvss3TemporalMetricsTest;
import com.synopsys.kb.httpclient.model.LicenseDefinitionItemTest;
import com.synopsys.kb.httpclient.model.LicenseDefinitionTest;
import com.synopsys.kb.httpclient.model.LicenseTest;
import com.synopsys.kb.httpclient.model.LinkTest;
import com.synopsys.kb.httpclient.model.LogoTest;
import com.synopsys.kb.httpclient.model.MetaTest;
import com.synopsys.kb.httpclient.model.MetaWrapperTest;
import com.synopsys.kb.httpclient.model.PageTest;
import com.synopsys.kb.httpclient.model.RiskProfileTest;
import com.synopsys.kb.httpclient.model.UpgradeGuidanceSuggestionTest;
import com.synopsys.kb.httpclient.model.UpgradeGuidanceTest;
import com.synopsys.kb.httpclient.model.VulnerabilityReferenceTest;

/**
 * KB HTTP client test factory.
 * 
 * @author skatzman
 */
public class KbHttpClientTestFactory {
    @Test
    @Factory
    public Object[] tests() {
        return new Object[] {
                new AuthenticationExceptionTest(),
                new AuthenticationTest(),
                new AuthorizationHttpRequestInterceptorTest(),
                new AuthorizationProviderTest(),
                new BdsaVulnerabilityTest(),
                new ComponentSearchResultTest(),
                new ComponentTest(),
                new ComponentVariantTest(),
                new ComponentVersionTest(),
                new CustomHttpRequestRetryStrategyTest(),
                new CveVulnerabilityTest(),
                new Cvss2ScoreTest(),
                new Cvss2TemporalMetricsTest(),
                new Cvss3ScoreTest(),
                new Cvss3TemporalMetricsTest(),
                new FiniteConnectionKeepAliveStrategyTest(),
                new HttpResponseTest(),
                new KbConfigurationTest(),
                new LicenseDefinitionItemTest(),
                new LicenseDefinitionTest(),
                new LicenseTest(),
                new LinkTest(),
                new LogoTest(),
                new MetaTest(),
                new MetaWrapperTest(),
                new PageRequestTest(),
                new PageTest(),
                new ProxyConfigurationTest(),
                new RiskProfileTest(),
                new SortExpressionTest(),
                new StaticLicenseKeySupplierTest(),
                new UpgradeGuidanceSuggestionTest(),
                new UpgradeGuidanceTest(),
                new VulnerabilityReferenceTest(),
        };
    }
}
