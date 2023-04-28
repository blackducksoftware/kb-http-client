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
package com.synopsys.bd.kb.httpclient;

import org.testng.annotations.Factory;
import org.testng.annotations.Test;

import com.synopsys.bd.kb.httpclient.impl.BdComponentApiTest;
import com.synopsys.bd.kb.httpclient.impl.BdComponentFinderTest;
import com.synopsys.bd.kb.httpclient.impl.BdComponentVariantApiTest;
import com.synopsys.bd.kb.httpclient.impl.BdComponentVersionApiTest;
import com.synopsys.bd.kb.httpclient.impl.BdLicenseApiTest;
import com.synopsys.bd.kb.httpclient.impl.BdLicenseDefinitionFinderTest;
import com.synopsys.bd.kb.httpclient.impl.BdVulnerabilityMergerTest;
import com.synopsys.bd.kb.httpclient.model.BdComponentVariantHierarchyTest;
import com.synopsys.bd.kb.httpclient.model.BdComponentVariantTest;
import com.synopsys.bd.kb.httpclient.model.BdComponentVersionHierarchyTest;
import com.synopsys.bd.kb.httpclient.model.BdComponentVersionSummaryTest;
import com.synopsys.bd.kb.httpclient.model.BdComponentVersionTest;
import com.synopsys.bd.kb.httpclient.model.BdLicenseDefinitionTest;
import com.synopsys.bd.kb.httpclient.model.BdLicenseTest;
import com.synopsys.bd.kb.httpclient.model.OverallScoreTest;
import com.synopsys.bd.kb.httpclient.model.RelatedVulnerabilityTest;
import com.synopsys.bd.kb.httpclient.model.VulnerabilityTest;

/**
 * Black Duck KB HTTP client test factory.
 * 
 * @author skatzman
 */
public class BdKbHttpClientTestFactory {
    @Test
    @Factory
    public Object[] tests() {
        return new Object[] {
                new BdComponentApiTest(),
                new BdComponentFinderTest(),
                new BdComponentVariantApiTest(),
                new BdComponentVariantHierarchyTest(),
                new BdComponentVariantTest(),
                new BdComponentVersionApiTest(),
                new BdComponentVersionHierarchyTest(),
                new BdComponentVersionSummaryTest(),
                new BdComponentVersionTest(),
                new BdLicenseApiTest(),
                new BdLicenseDefinitionFinderTest(),
                new BdLicenseDefinitionTest(),
                new BdLicenseTest(),
                new BdVulnerabilityMergerTest(),
                new OverallScoreTest(),
                new RelatedVulnerabilityTest(),
                new VulnerabilityTest()
        };
    }
}
