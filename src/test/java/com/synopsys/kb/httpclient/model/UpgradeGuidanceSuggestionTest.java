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

import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synopsys.kb.httpclient.AbstractTest;

/**
 * Upgrade guidance suggestion test.
 * 
 * @author skatzman
 */
public class UpgradeGuidanceSuggestionTest extends AbstractTest {
    private static final UUID VERSION_ID = UUID.randomUUID();

    private static final String VERSION = BASE_HREF + "/api/versions/" + VERSION_ID;

    private static final String VERSION_NAME = "1.0";

    private static final UUID VARIANT_ID = UUID.randomUUID();

    private static final String VARIANT = BASE_HREF + "/api/variants/" + VARIANT_ID;

    private static final String VARIANT_NAME = "1.0";

    private static final String VARIANT_EXTERNAL_NAMESPACE = "maven";

    private static final String VARIANT_EXTERNAL_ID = "foo:foo:1.0";

    private static final RiskProfile RISK_PROFILE = new RiskProfile(5, 4, 3, 2, 1);

    @Test
    public void testConstructor() {
        UpgradeGuidanceSuggestion upgradeGuidanceSuggestion = new UpgradeGuidanceSuggestion(VERSION, VERSION_NAME, VARIANT, VARIANT_NAME,
                VARIANT_EXTERNAL_NAMESPACE, VARIANT_EXTERNAL_ID, RISK_PROFILE);

        Assert.assertEquals(upgradeGuidanceSuggestion.getVersion(), VERSION, "Version HREFs should be equal.");
        Assert.assertEquals(upgradeGuidanceSuggestion.getVersionName(), VERSION_NAME, "Version names should be equal.");
        Assert.assertEquals(upgradeGuidanceSuggestion.getVariant(), VARIANT, "Variant HREFs should be equal.");
        Assert.assertEquals(upgradeGuidanceSuggestion.getVariantName(), VARIANT_NAME, "Variant names should be equal.");
        Assert.assertEquals(upgradeGuidanceSuggestion.getVariantExternalNamespace().orElse(null), VARIANT_EXTERNAL_NAMESPACE,
                "Variant external namespaces should be equal.");
        Assert.assertEquals(upgradeGuidanceSuggestion.getVariantExternalId(), VARIANT_EXTERNAL_ID, "Variant external ids should be equal.");
        Assert.assertEquals(upgradeGuidanceSuggestion.getRiskProfile(), RISK_PROFILE, "Risk profiles should be equal.");

        Assert.assertEquals(upgradeGuidanceSuggestion.getVersionId().orElse(null), VERSION_ID, "Version ids should be equal.");
        Assert.assertEquals(upgradeGuidanceSuggestion.getVariantId().orElse(null), VARIANT_ID, "Variant ids should be equal.");
    }

    @Test
    public void testDeserialization() throws JsonProcessingException {
        UpgradeGuidanceSuggestion upgradeGuidanceSuggestion = new UpgradeGuidanceSuggestion(VERSION, VERSION_NAME, VARIANT, VARIANT_NAME,
                VARIANT_EXTERNAL_NAMESPACE, VARIANT_EXTERNAL_ID, RISK_PROFILE);

        String json = serialize(upgradeGuidanceSuggestion);
        UpgradeGuidanceSuggestion result = deserialize(json, UpgradeGuidanceSuggestion.class);

        Assert.assertEquals(result.getVersion(), VERSION, "Version HREFs should be equal.");
        Assert.assertEquals(result.getVersionName(), VERSION_NAME, "Version names should be equal.");
        Assert.assertEquals(result.getVariant(), VARIANT, "Variant HREFs should be equal.");
        Assert.assertEquals(result.getVariantName(), VARIANT_NAME, "Variant names should be equal.");
        Assert.assertEquals(result.getVariantExternalNamespace().orElse(null), VARIANT_EXTERNAL_NAMESPACE,
                "Variant external namespaces should be equal.");
        Assert.assertEquals(result.getVariantExternalId(), VARIANT_EXTERNAL_ID, "Variant external ids should be equal.");
        Assert.assertEquals(result.getRiskProfile(), RISK_PROFILE, "Risk profiles should be equal.");
    }

    @Test
    public void testHashCode() {
        UpgradeGuidanceSuggestion upgradeGuidanceSuggestion = new UpgradeGuidanceSuggestion(VERSION, VERSION_NAME, VARIANT, VARIANT_NAME,
                VARIANT_EXTERNAL_NAMESPACE, VARIANT_EXTERNAL_ID, RISK_PROFILE);
        UpgradeGuidanceSuggestion copyUpgradeGuidanceSuggestion = new UpgradeGuidanceSuggestion(VERSION, VERSION_NAME, VARIANT, VARIANT_NAME,
                VARIANT_EXTERNAL_NAMESPACE, VARIANT_EXTERNAL_ID, RISK_PROFILE);
        UpgradeGuidanceSuggestion differentUpgradeGuidanceSuggestion = new UpgradeGuidanceSuggestion(VERSION, VERSION_NAME, VARIANT, VARIANT_NAME,
                VARIANT_EXTERNAL_NAMESPACE, "differentExternalId", RISK_PROFILE);

        assertHashCode(upgradeGuidanceSuggestion, copyUpgradeGuidanceSuggestion, differentUpgradeGuidanceSuggestion);
    }

    @Test
    public void testEquals() {
        UpgradeGuidanceSuggestion upgradeGuidanceSuggestion = new UpgradeGuidanceSuggestion(VERSION, VERSION_NAME, VARIANT, VARIANT_NAME,
                VARIANT_EXTERNAL_NAMESPACE, VARIANT_EXTERNAL_ID, RISK_PROFILE);
        UpgradeGuidanceSuggestion copyUpgradeGuidanceSuggestion = new UpgradeGuidanceSuggestion(VERSION, VERSION_NAME, VARIANT, VARIANT_NAME,
                VARIANT_EXTERNAL_NAMESPACE, VARIANT_EXTERNAL_ID, RISK_PROFILE);
        UpgradeGuidanceSuggestion differentUpgradeGuidanceSuggestion = new UpgradeGuidanceSuggestion(VERSION, VERSION_NAME, VARIANT, VARIANT_NAME,
                VARIANT_EXTERNAL_NAMESPACE, "differentExternalId", RISK_PROFILE);

        assertEquals(upgradeGuidanceSuggestion, copyUpgradeGuidanceSuggestion, differentUpgradeGuidanceSuggestion);
    }
}
