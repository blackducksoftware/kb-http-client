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

import java.util.List;
import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synopsys.kb.httpclient.AbstractTest;

public class ComponentSearchResultTest extends AbstractTest {
    private static final String COMPONENT_NAME = "FooComponent";

    private static final String VERSION_NAME = "1.0";

    private static final String EXTERNAL_ID = "foo:foo:1.0";

    private static final UUID COMPONENT_ID = UUID.randomUUID();

    private static final String COMPONENT_HREF = BASE_HREF + "/api/components/" + COMPONENT_ID;

    private static final UUID VERSION_ID = UUID.randomUUID();

    private static final String VERSION_HREF = BASE_HREF + "/api/versions/" + VERSION_ID;

    private static final UUID VARIANT_ID = UUID.randomUUID();

    private static final String VARIANT_HREF = BASE_HREF + "/api/variants/" + VARIANT_ID;

    private static final Meta META = new Meta(null, List.of(new Link("upgrade-guidance", VARIANT_HREF + "/upgrade-guidance"),
            new Link("transitive-upgrade-guidance", VARIANT_HREF + "/transitive-upgrade-guidance"),
            new Link("vulnerabilities", VARIANT_HREF + "/vulnerabilities")));

    @Test
    public void testConstructor() {
        ComponentSearchResult componentSearchResult = new ComponentSearchResult(COMPONENT_NAME, VERSION_NAME, EXTERNAL_ID, COMPONENT_HREF, VERSION_HREF,
                VARIANT_HREF, Boolean.FALSE, META);

        Assert.assertEquals(componentSearchResult.getComponentName(), COMPONENT_NAME, "Component names should be equal.");
        Assert.assertEquals(componentSearchResult.getVersionName().orElse(null), VERSION_NAME, "Version names should be equal.");
        Assert.assertEquals(componentSearchResult.getExternalId().orElse(null), EXTERNAL_ID, "External ids should be equal.");
        Assert.assertEquals(componentSearchResult.getComponent(), COMPONENT_HREF, "Component HREFs should be equal.");
        Assert.assertEquals(componentSearchResult.getVersion().orElse(null), VERSION_HREF, "Version HREFs should be equal.");
        Assert.assertEquals(componentSearchResult.getVariant().orElse(null), VARIANT_HREF, "Variant HREFs should be equal.");
        Assert.assertFalse(componentSearchResult.isPartialMatch(), "Partial match should not be enabled.");
        Assert.assertEquals(componentSearchResult.getMeta(), META, "Metas should be equal.");

        Assert.assertEquals(componentSearchResult.getComponentId().orElse(null), COMPONENT_ID, "Component ids should be equal.");
        Assert.assertEquals(componentSearchResult.getVersionId().orElse(null), VERSION_ID, "Version ids should be equal.");
        Assert.assertEquals(componentSearchResult.getVariantId().orElse(null), VARIANT_ID, "Variant ids should be equal.");
    }

    @Test
    public void testConstructorWithNullValues() {
        ComponentSearchResult componentSearchResult = new ComponentSearchResult(COMPONENT_NAME, VERSION_NAME, EXTERNAL_ID, COMPONENT_HREF, VERSION_HREF,
                VARIANT_HREF, null, META);

        Assert.assertEquals(componentSearchResult.getComponentName(), COMPONENT_NAME, "Component names should be equal.");
        Assert.assertEquals(componentSearchResult.getVersionName().orElse(null), VERSION_NAME, "Version names should be equal.");
        Assert.assertEquals(componentSearchResult.getExternalId().orElse(null), EXTERNAL_ID, "External ids should be equal.");
        Assert.assertEquals(componentSearchResult.getComponent(), COMPONENT_HREF, "Component HREFs should be equal.");
        Assert.assertEquals(componentSearchResult.getVersion().orElse(null), VERSION_HREF, "Version HREFs should be equal.");
        Assert.assertEquals(componentSearchResult.getVariant().orElse(null), VARIANT_HREF, "Variant HREFs should be equal.");
        Assert.assertFalse(componentSearchResult.isPartialMatch(), "Partial match should not be enabled.");
        Assert.assertEquals(componentSearchResult.getMeta(), META, "Metas should be equal.");

        Assert.assertEquals(componentSearchResult.getComponentId().orElse(null), COMPONENT_ID, "Component ids should be equal.");
        Assert.assertEquals(componentSearchResult.getVersionId().orElse(null), VERSION_ID, "Version ids should be equal.");
        Assert.assertEquals(componentSearchResult.getVariantId().orElse(null), VARIANT_ID, "Variant ids should be equal.");
    }

    @Test
    public void testDeserialization() throws JsonProcessingException {
        ComponentSearchResult componentSearchResult = new ComponentSearchResult(COMPONENT_NAME, VERSION_NAME, EXTERNAL_ID, COMPONENT_HREF, VERSION_HREF,
                VARIANT_HREF, Boolean.FALSE, META);

        String json = serialize(componentSearchResult);
        ComponentSearchResult result = deserialize(json, ComponentSearchResult.class);

        Assert.assertEquals(result.getComponentName(), COMPONENT_NAME, "Component names should be equal.");
        Assert.assertEquals(result.getVersionName().orElse(null), VERSION_NAME, "Version names should be equal.");
        Assert.assertEquals(result.getExternalId().orElse(null), EXTERNAL_ID, "External ids should be equal.");
        Assert.assertEquals(result.getComponent(), COMPONENT_HREF, "Component HREFs should be equal.");
        Assert.assertEquals(result.getVersion().orElse(null), VERSION_HREF, "Version HREFs should be equal.");
        Assert.assertEquals(result.getVariant().orElse(null), VARIANT_HREF, "Variant HREFs should be equal.");
        Assert.assertFalse(result.isPartialMatch(), "Partial match should not be enabled.");
        Assert.assertEquals(componentSearchResult.getMeta(), META, "Metas should be equal.");
    }

    @Test
    public void testHashCode() {
        ComponentSearchResult componentSearchResult = new ComponentSearchResult(COMPONENT_NAME, VERSION_NAME, EXTERNAL_ID, COMPONENT_HREF, VERSION_HREF,
                VARIANT_HREF, Boolean.FALSE, META);
        ComponentSearchResult copyComponentSearchResult = new ComponentSearchResult(COMPONENT_NAME, VERSION_NAME, EXTERNAL_ID, COMPONENT_HREF, VERSION_HREF,
                VARIANT_HREF, Boolean.FALSE, META);
        ComponentSearchResult differentComponentSearchResult = new ComponentSearchResult(COMPONENT_NAME, VERSION_NAME, EXTERNAL_ID, COMPONENT_HREF,
                VERSION_HREF, VARIANT_HREF, Boolean.TRUE, META);

        assertHashCode(componentSearchResult, copyComponentSearchResult, differentComponentSearchResult);
    }

    @Test
    public void testEquals() {
        ComponentSearchResult componentSearchResult = new ComponentSearchResult(COMPONENT_NAME, VERSION_NAME, EXTERNAL_ID, COMPONENT_HREF, VERSION_HREF,
                VARIANT_HREF, Boolean.FALSE, META);
        ComponentSearchResult copyComponentSearchResult = new ComponentSearchResult(COMPONENT_NAME, VERSION_NAME, EXTERNAL_ID, COMPONENT_HREF, VERSION_HREF,
                VARIANT_HREF, Boolean.FALSE, META);
        ComponentSearchResult differentComponentSearchResult = new ComponentSearchResult(COMPONENT_NAME, VERSION_NAME, EXTERNAL_ID, COMPONENT_HREF,
                VERSION_HREF, VARIANT_HREF, Boolean.TRUE, META);

        assertEquals(componentSearchResult, copyComponentSearchResult, differentComponentSearchResult);
    }
}
