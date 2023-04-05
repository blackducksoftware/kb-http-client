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
package com.synopsys.kb.httpclient.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synopsys.kb.httpclient.AbstractTest;

/**
 * Component version test.
 * 
 * @author skatzman
 */
public class ComponentVersionTest extends AbstractTest {
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
        ComponentVersion componentVersion = new ComponentVersion(VERSION, RELEASED_ON, LICENSE_DEFINITION, RISK_PROFILE, DELETED, COMPONENT_INTELLIGENCE, META);

        Assert.assertEquals(componentVersion.getVersion().orElse(null), VERSION, "Versions should be equal.");
        Assert.assertEquals(componentVersion.getReleasedOn().orElse(null), RELEASED_ON, "Released ons should be equal.");
        Assert.assertEquals(componentVersion.getLicenseDefinition().orElse(null), LICENSE_DEFINITION, "License definitions should be equal.");
        Assert.assertEquals(componentVersion.getRiskProfile(), RISK_PROFILE, "Risk profiles should be equal.");
        Assert.assertFalse(componentVersion.isDeleted(), "Component version should not be deleted.");
        Assert.assertTrue(componentVersion.isComponentIntelligencePresent(), "Component intelligence should be present.");
        Assert.assertEquals(componentVersion.getMeta(), META, "Metas should be equal.");

        Assert.assertEquals(componentVersion.getId(), ID, "Ids should be equal.");
        Assert.assertEquals(componentVersion.getComponentId(), COMPONENT_ID, "Component ids should be equal.");
    }

    @Test
    public void testDeserialization() throws JsonProcessingException {
        ComponentVersion componentVersion = new ComponentVersion(VERSION, RELEASED_ON, LICENSE_DEFINITION, RISK_PROFILE, DELETED, COMPONENT_INTELLIGENCE, META);

        String json = serialize(componentVersion);
        ComponentVersion result = deserialize(json, ComponentVersion.class);

        Assert.assertEquals(result.getVersion().orElse(null), VERSION, "Versions should be equal.");
        Assert.assertNotNull(result.getReleasedOn().orElse(null), "Released on should be initialized.");
        Assert.assertEquals(result.getLicenseDefinition().orElse(null), LICENSE_DEFINITION, "License definitions should be equal.");
        Assert.assertEquals(result.getRiskProfile(), RISK_PROFILE, "Risk profiles should be equal.");
        Assert.assertFalse(result.isDeleted(), "Component version should not be deleted.");
        Assert.assertTrue(result.isComponentIntelligencePresent(), "Component intelligence should be present.");
        Assert.assertEquals(result.getMeta(), META, "Metas should be equal.");
    }
}
