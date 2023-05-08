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

import java.util.Collections;
import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synopsys.kb.httpclient.AbstractTest;

/**
 * Upgrade guidance test.
 * 
 * @author skatzman
 */
public class UpgradeGuidanceTest extends AbstractTest {
    private static final UUID COMPONENT_ID = UUID.randomUUID();

    private static final String COMPONENT = BASE_HREF + "/api/components/" + COMPONENT_ID;

    private static final UUID VERSION_ID = UUID.randomUUID();

    private static final String VERSION = BASE_HREF + "/api/versions/" + VERSION_ID;

    private static final UUID VARIANT_ID = UUID.randomUUID();

    private static final String VARIANT = BASE_HREF + "/api/variants/" + VARIANT_ID;

    private static final String COMPONENT_NAME = "FooComponent";

    private static final String VERSION_NAME = "1.0";

    private static final String VARIANT_NAME = "1.0";

    private static final String VARIANT_EXTERNAL_NAMESPACE = "maven";

    private static final String VARIANT_EXTERNAL_ID = "foo:foo:1.0";

    private static final UpgradeGuidanceSuggestion SHORT_TERM_SUGGESTION = new UpgradeGuidanceSuggestion(
            BASE_HREF + "/api/versions/" + UUID.randomUUID(), "2.0",
            BASE_HREF + "/api/versions/" + UUID.randomUUID(),
            "2.0", "maven", "foo:foo:2.0", new RiskProfile(0, 0, 0, 0, 0));

    private static final UpgradeGuidanceSuggestion LONG_TERM_SUGGESTION = new UpgradeGuidanceSuggestion(
            BASE_HREF + "/api/versions/" + UUID.randomUUID(), "3.0",
            BASE_HREF + "/api/versions/" + UUID.randomUUID(),
            "2.0", "maven", "foo:foo:2.0", new RiskProfile(0, 0, 0, 0, 0));

    private static final Meta META = new Meta(VARIANT + "/upgrade-guidance", Collections.emptyList());

    @Test
    public void testConstructor() {
        UpgradeGuidance upgradeGuidance = new UpgradeGuidance(COMPONENT, VERSION, VARIANT, COMPONENT_NAME, VERSION_NAME, VARIANT_NAME,
                VARIANT_EXTERNAL_NAMESPACE, VARIANT_EXTERNAL_ID, SHORT_TERM_SUGGESTION, LONG_TERM_SUGGESTION, META);

        Assert.assertEquals(upgradeGuidance.getComponent(), COMPONENT, "Component HREFs should be equal.");
        Assert.assertEquals(upgradeGuidance.getVersion(), VERSION, "Version HREFs should be equal.");
        Assert.assertEquals(upgradeGuidance.getVariant().orElse(null), VARIANT, "Variant HREFs should be equal.");
        Assert.assertEquals(upgradeGuidance.getComponentName(), COMPONENT_NAME, "Component names should be equal.");
        Assert.assertEquals(upgradeGuidance.getVersionName(), VERSION_NAME, "Version names should be equal.");
        Assert.assertEquals(upgradeGuidance.getVariantName().orElse(null), VARIANT_NAME, "Variant names should be equal.");
        Assert.assertEquals(upgradeGuidance.getVariantExternalNamespace().orElse(null), VARIANT_EXTERNAL_NAMESPACE,
                "Variant external namespaces should be equal.");
        Assert.assertEquals(upgradeGuidance.getVariantExternalId().orElse(null), VARIANT_EXTERNAL_ID, "Variant external ids should be equal.");
        Assert.assertEquals(upgradeGuidance.getShortTermSuggestion().orElse(null), SHORT_TERM_SUGGESTION, "Short term suggestions should be equal.");
        Assert.assertEquals(upgradeGuidance.getLongTermSuggestion().orElse(null), LONG_TERM_SUGGESTION, "Long term suggestions should be equal.");
        Assert.assertEquals(upgradeGuidance.getMeta(), META, "Metas should be equal.");

        Assert.assertEquals(upgradeGuidance.getComponentId().orElse(null), COMPONENT_ID, "Component ids should be equal.");
        Assert.assertEquals(upgradeGuidance.getVersionId().orElse(null), VERSION_ID, "Version ids should be equal.");
        Assert.assertEquals(upgradeGuidance.getVariantId().orElse(null), VARIANT_ID, "Variant ids should be equal.");
    }

    @Test
    public void testDeserialization() throws JsonProcessingException {
        UpgradeGuidance upgradeGuidance = new UpgradeGuidance(COMPONENT, VERSION, VARIANT, COMPONENT_NAME, VERSION_NAME, VARIANT_NAME,
                VARIANT_EXTERNAL_NAMESPACE, VARIANT_EXTERNAL_ID, SHORT_TERM_SUGGESTION, LONG_TERM_SUGGESTION, META);

        String json = serialize(upgradeGuidance);
        UpgradeGuidance result = deserialize(json, UpgradeGuidance.class);

        Assert.assertEquals(result.getComponent(), COMPONENT, "Component HREFs should be equal.");
        Assert.assertEquals(result.getVersion(), VERSION, "Version HREFs should be equal.");
        Assert.assertEquals(result.getVariant().orElse(null), VARIANT, "Variant HREFs should be equal.");
        Assert.assertEquals(result.getComponentName(), COMPONENT_NAME, "Component names should be equal.");
        Assert.assertEquals(result.getVersionName(), VERSION_NAME, "Version names should be equal.");
        Assert.assertEquals(result.getVariantName().orElse(null), VARIANT_NAME, "Variant names should be equal.");
        Assert.assertEquals(result.getVariantExternalNamespace().orElse(null), VARIANT_EXTERNAL_NAMESPACE,
                "Variant external namespaces should be equal.");
        Assert.assertEquals(result.getVariantExternalId().orElse(null), VARIANT_EXTERNAL_ID, "Variant external ids should be equal.");
        Assert.assertEquals(result.getShortTermSuggestion().orElse(null), SHORT_TERM_SUGGESTION, "Short term suggestions should be equal.");
        Assert.assertEquals(result.getLongTermSuggestion().orElse(null), LONG_TERM_SUGGESTION, "Long term suggestions should be equal.");
        Assert.assertEquals(result.getMeta(), META, "Metas should be equal.");
    }

    @Test
    public void testHashCode() {
        UpgradeGuidance upgradeGuidance = new UpgradeGuidance(COMPONENT, VERSION, VARIANT, COMPONENT_NAME, VERSION_NAME, VARIANT_NAME,
                VARIANT_EXTERNAL_NAMESPACE, VARIANT_EXTERNAL_ID, SHORT_TERM_SUGGESTION, LONG_TERM_SUGGESTION, META);
        UpgradeGuidance copyUpgradeGuidance = new UpgradeGuidance(COMPONENT, VERSION, VARIANT, COMPONENT_NAME, VERSION_NAME, VARIANT_NAME,
                VARIANT_EXTERNAL_NAMESPACE, VARIANT_EXTERNAL_ID, SHORT_TERM_SUGGESTION, LONG_TERM_SUGGESTION, META);
        UpgradeGuidance differentUpgradeGuidance = new UpgradeGuidance(COMPONENT, VERSION, VARIANT, COMPONENT_NAME, VERSION_NAME, VARIANT_NAME,
                VARIANT_EXTERNAL_NAMESPACE, "differentExternalId", SHORT_TERM_SUGGESTION, LONG_TERM_SUGGESTION, META);

        assertHashCode(upgradeGuidance, copyUpgradeGuidance, differentUpgradeGuidance);
    }

    @Test
    public void testEquals() {
        UpgradeGuidance upgradeGuidance = new UpgradeGuidance(COMPONENT, VERSION, VARIANT, COMPONENT_NAME, VERSION_NAME, VARIANT_NAME,
                VARIANT_EXTERNAL_NAMESPACE, VARIANT_EXTERNAL_ID, SHORT_TERM_SUGGESTION, LONG_TERM_SUGGESTION, META);
        UpgradeGuidance copyUpgradeGuidance = new UpgradeGuidance(COMPONENT, VERSION, VARIANT, COMPONENT_NAME, VERSION_NAME, VARIANT_NAME,
                VARIANT_EXTERNAL_NAMESPACE, VARIANT_EXTERNAL_ID, SHORT_TERM_SUGGESTION, LONG_TERM_SUGGESTION, META);
        UpgradeGuidance differentUpgradeGuidance = new UpgradeGuidance(COMPONENT, VERSION, VARIANT, COMPONENT_NAME, VERSION_NAME, VARIANT_NAME,
                VARIANT_EXTERNAL_NAMESPACE, "differentExternalId", SHORT_TERM_SUGGESTION, LONG_TERM_SUGGESTION, META);

        assertEquals(upgradeGuidance, copyUpgradeGuidance, differentUpgradeGuidance);
    }
}
