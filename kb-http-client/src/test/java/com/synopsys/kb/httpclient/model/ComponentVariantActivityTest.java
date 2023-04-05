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
import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synopsys.kb.httpclient.AbstractTest;

/**
 * Component variant activity test.
 * 
 * @author skatzman
 */
public class ComponentVariantActivityTest extends AbstractTest {
    private static final UUID COMPONENT_VARIANT_ID = UUID.randomUUID();

    private static final String VARIANT = BASE_HREF + "/api/variants/" + COMPONENT_VARIANT_ID;

    private static final OffsetDateTime UPDATED_DATE = OffsetDateTime.now();

    @Test
    public void testConstructor() {
        ComponentVariantActivity activity = new ComponentVariantActivity(VARIANT, UPDATED_DATE);

        Assert.assertEquals(activity.getVariant(), VARIANT, "Variant should be equal.");
        Assert.assertEquals(activity.getUpdatedDate(), UPDATED_DATE, "Updated dates should be equal.");

        Assert.assertEquals(activity.getComponentVariantId(), COMPONENT_VARIANT_ID, "Component variant ids should be equal.");
    }

    @Test
    public void testDeserialization() throws JsonProcessingException {
        ComponentVariantActivity activity = new ComponentVariantActivity(VARIANT, UPDATED_DATE);

        String json = serialize(activity);
        ComponentVariantActivity result = deserialize(json, ComponentVariantActivity.class);

        Assert.assertEquals(result.getVariant(), VARIANT, "Variants should be equal.");
        Assert.assertNotNull(result.getUpdatedDate(), "Updated date should be initialized.");
    }
}
