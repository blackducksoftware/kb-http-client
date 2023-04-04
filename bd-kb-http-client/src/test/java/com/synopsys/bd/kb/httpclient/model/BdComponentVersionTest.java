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
package com.synopsys.bd.kb.httpclient.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.synopsys.bd.kb.httpclient.AbstractBdTest;
import com.synopsys.kb.httpclient.model.LicenseDefinition;
import com.synopsys.kb.httpclient.model.LicenseDefinitionItem;
import com.synopsys.kb.httpclient.model.LicenseDefinitionType;
import com.synopsys.kb.httpclient.model.Link;
import com.synopsys.kb.httpclient.model.Meta;
import com.synopsys.kb.httpclient.model.RiskProfile;

/**
 * Black Duck-centric component version test.
 * 
 * @author skatzman
 */
public class BdComponentVersionTest extends AbstractBdTest {
    private static final UUID ID = UUID.randomUUID();

    private static final UUID COMPONENT_ID = UUID.randomUUID();

    private static final String VERSION = "1.0";

    private static final OffsetDateTime RELEASED_ON = OffsetDateTime.now();

    private static final LicenseDefinition LICENSE_DEFINITION = new LicenseDefinition(LicenseDefinitionType.CONJUNCTIVE,
            List.of(new LicenseDefinitionItem(BASE_HREF + "/api/licenses/" + UUID.randomUUID(), null)));

    private static final RiskProfile RISK_PROFILE = new RiskProfile(5, 4, 3, 2, 1);

    private static final Boolean DELETED = Boolean.FALSE;

    private static final Boolean COMPONENT_INTELLIGENCE = Boolean.TRUE;

    private static final Meta META = new Meta(BASE_HREF + "/api/versions/" + ID,
            List.of(new Link("component", BASE_HREF + "/api/components/" + COMPONENT_ID)));

    @Test
    public void testConstructor() {
        BdComponentVersion bdComponentVersion = new BdComponentVersion(VERSION, RELEASED_ON, LICENSE_DEFINITION, RISK_PROFILE, DELETED, COMPONENT_INTELLIGENCE,
                META, BASE_HREF);

        Assert.assertEquals(bdComponentVersion.getVersion().orElse(null), VERSION, "Versions should be equal.");
        Assert.assertEquals(bdComponentVersion.getReleasedOn().orElse(null), RELEASED_ON, "Released ons should be equal.");
        Assert.assertEquals(bdComponentVersion.getLicenseDefinition().orElse(null), LICENSE_DEFINITION, "License definitions should be equal.");
        Assert.assertEquals(bdComponentVersion.getRiskProfile(), RISK_PROFILE, "Risk profiles should be equal.");
        Assert.assertFalse(bdComponentVersion.isDeleted(), "Component version should not be deleted.");
        Assert.assertTrue(bdComponentVersion.isComponentIntelligencePresent(), "Component intelligence should be present.");
        Assert.assertEquals(bdComponentVersion.getMeta(), META, "Metas should be equal.");

        Assert.assertEquals(bdComponentVersion.getRequiredVersion(), VERSION, "Versions should be equal.");
        Assert.assertEquals(bdComponentVersion.getRequiredLicenseDefinition(), LICENSE_DEFINITION, "License definitions should be equal.");
        Assert.assertEquals(bdComponentVersion.getId(), ID, "Ids should be equal.");
        Assert.assertEquals(bdComponentVersion.getComponentId(), COMPONENT_ID, "Component ids should be equal.");
    }

    @Test
    public void testGetVersionWhenAbsent() {
        BdComponentVersion bdComponentVersion = new BdComponentVersion(null, RELEASED_ON, LICENSE_DEFINITION, RISK_PROFILE, DELETED, COMPONENT_INTELLIGENCE,
                META, BASE_HREF);

        Assert.assertEquals(bdComponentVersion.getVersion().orElse(null), BdComponentVersion.UNKNOWN_VERSION, "Versions should be equal.");
        Assert.assertEquals(bdComponentVersion.getRequiredVersion(), BdComponentVersion.UNKNOWN_VERSION, "Versions should be equal.");
    }

    @Test
    public void testGetLicenseDefinitionWhenAbsent() {
        BdComponentVersion bdComponentVersion = new BdComponentVersion(VERSION, RELEASED_ON, null, RISK_PROFILE, DELETED, COMPONENT_INTELLIGENCE,
                META, BASE_HREF);

        String href = BASE_HREF + "/api/licenses/" + BdComponentVersion.UNKNOWN_KB_LICENSE_ID;
        LicenseDefinitionItem licenseDefinitionItem = new LicenseDefinitionItem(href, null);
        List<LicenseDefinitionItem> licenseDefinitionItems = List.of(licenseDefinitionItem);
        LicenseDefinition unknownLicenseDefinition = new LicenseDefinition(LicenseDefinitionType.CONJUNCTIVE, licenseDefinitionItems);

        Assert.assertEquals(bdComponentVersion.getLicenseDefinition().orElse(null), unknownLicenseDefinition, "License definitions should be equal.");
        Assert.assertEquals(bdComponentVersion.getRequiredLicenseDefinition(), unknownLicenseDefinition, "License definitions should be equal.");
    }
}
