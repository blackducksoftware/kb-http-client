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
 * Component version activity test.
 * 
 * @author skatzman
 */
public class ComponentVersionActivityTest extends AbstractTest {
    private static final UUID COMPONENT_VERSION_ID = UUID.randomUUID();

    private static final String VERSION = BASE_HREF + "/api/versions/" + COMPONENT_VERSION_ID;

    private static final OffsetDateTime UPDATED_DATE = OffsetDateTime.now();

    @Test
    public void testConstructor() {
        ComponentVersionActivity activity = new ComponentVersionActivity(VERSION, UPDATED_DATE);

        Assert.assertEquals(activity.getVersion(), VERSION, "Versions should be equal.");
        Assert.assertEquals(activity.getUpdatedDate(), UPDATED_DATE, "Updated dates should be equal.");

        Assert.assertEquals(activity.getComponentVersionId(), COMPONENT_VERSION_ID, "Component version ids should be equal.");
    }

    @Test
    public void testDeserialization() throws JsonProcessingException {
        ComponentVersionActivity activity = new ComponentVersionActivity(VERSION, UPDATED_DATE);

        String json = serialize(activity);
        ComponentVersionActivity result = deserialize(json, ComponentVersionActivity.class);

        Assert.assertEquals(result.getVersion(), VERSION, "Versions should be equal.");
        Assert.assertNotNull(result.getUpdatedDate(), "Updated date should be initialized.");
    }

    @Test
    public void testHashCode() {
        ComponentVersionActivity activity = new ComponentVersionActivity(VERSION, UPDATED_DATE);
        ComponentVersionActivity copyActivity = new ComponentVersionActivity(VERSION, UPDATED_DATE);
        ComponentVersionActivity differentActivity = new ComponentVersionActivity(VERSION, OffsetDateTime.now().plusDays(1L));

        assertHashCode(activity, copyActivity, differentActivity);
    }

    @Test
    public void testEquals() {
        ComponentVersionActivity activity = new ComponentVersionActivity(VERSION, UPDATED_DATE);
        ComponentVersionActivity copyActivity = new ComponentVersionActivity(VERSION, UPDATED_DATE);
        ComponentVersionActivity differentActivity = new ComponentVersionActivity(VERSION, OffsetDateTime.now().plusDays(1L));

        assertEquals(activity, copyActivity, differentActivity);
    }
}
