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
 * Component activity test.
 * 
 * @author skatzman
 */
public class ComponentActivityTest extends AbstractTest {
    private static final UUID COMPONENT_ID = UUID.randomUUID();

    private static final String COMPONENT = BASE_HREF + "/api/components/" + COMPONENT_ID;

    private static final OffsetDateTime UPDATED_DATE = OffsetDateTime.now();

    @Test
    public void testConstructor() {
        ComponentActivity activity = new ComponentActivity(COMPONENT, UPDATED_DATE);

        Assert.assertEquals(activity.getComponent(), COMPONENT, "Components should be equal.");
        Assert.assertEquals(activity.getUpdatedDate(), UPDATED_DATE, "Updated dates should be equal.");

        Assert.assertEquals(activity.getComponentId(), COMPONENT_ID, "Component ids should be equal.");
    }

    @Test
    public void testDeserialization() throws JsonProcessingException {
        ComponentActivity activity = new ComponentActivity(COMPONENT, UPDATED_DATE);

        String json = serialize(activity);
        ComponentActivity result = deserialize(json, ComponentActivity.class);

        Assert.assertEquals(result.getComponent(), COMPONENT, "Components should be equal.");
        Assert.assertNotNull(result.getUpdatedDate(), "Updated date should be initialized.");
    }

    @Test
    public void testHashCode() {
        ComponentActivity activity = new ComponentActivity(COMPONENT, UPDATED_DATE);
        ComponentActivity copyActivity = new ComponentActivity(COMPONENT, UPDATED_DATE);
        ComponentActivity differentActivity = new ComponentActivity(COMPONENT, OffsetDateTime.now().plusDays(1L));

        assertHashCode(activity, copyActivity, differentActivity);
    }

    @Test
    public void testEquals() {
        ComponentActivity activity = new ComponentActivity(COMPONENT, UPDATED_DATE);
        ComponentActivity copyActivity = new ComponentActivity(COMPONENT, UPDATED_DATE);
        ComponentActivity differentActivity = new ComponentActivity(COMPONENT, OffsetDateTime.now().plusDays(1L));

        assertEquals(activity, copyActivity, differentActivity);
    }
}
