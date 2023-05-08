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
 * Component version summary test.
 * 
 * @author skatzman
 */
public class ComponentVersionSummaryTest extends AbstractTest {
    private static final UUID ID = UUID.randomUUID();

    private static final UUID COMPONENT_ID = UUID.randomUUID();

    private static final String VERSION = "1.0";

    private static final OffsetDateTime RELEASED_ON = OffsetDateTime.now();

    private static final Boolean DELETED = Boolean.FALSE;

    private static final Meta META = new Meta(BASE_HREF + "/api/versions/" + ID,
            List.of(new Link("component", BASE_HREF + "/api/components/" + COMPONENT_ID)));

    @Test
    public void testConstructor() {
        ComponentVersionSummary componentVersionSummary = new ComponentVersionSummary(VERSION, RELEASED_ON, DELETED, META);

        Assert.assertEquals(componentVersionSummary.getVersion().orElse(null), VERSION, "Versions should be equal.");
        Assert.assertEquals(componentVersionSummary.getReleasedOn().orElse(null), RELEASED_ON, "Released ons should be equal.");
        Assert.assertFalse(componentVersionSummary.isDeleted(), "Component version should not be deleted.");
        Assert.assertEquals(componentVersionSummary.getMeta(), META, "Metas should be equal.");

        Assert.assertEquals(componentVersionSummary.getId(), ID, "Ids should be equal.");
        Assert.assertEquals(componentVersionSummary.getComponentId(), COMPONENT_ID, "Component ids should be equal.");
    }

    @Test
    public void testConstructorWithNullValues() {
        ComponentVersionSummary componentVersionSummary = new ComponentVersionSummary(VERSION, RELEASED_ON, null, META);

        Assert.assertEquals(componentVersionSummary.getVersion().orElse(null), VERSION, "Versions should be equal.");
        Assert.assertEquals(componentVersionSummary.getReleasedOn().orElse(null), RELEASED_ON, "Released ons should be equal.");
        Assert.assertFalse(componentVersionSummary.isDeleted(), "Component version should not be deleted.");
        Assert.assertEquals(componentVersionSummary.getMeta(), META, "Metas should be equal.");

        Assert.assertEquals(componentVersionSummary.getId(), ID, "Ids should be equal.");
        Assert.assertEquals(componentVersionSummary.getComponentId(), COMPONENT_ID, "Component ids should be equal.");
    }

    @Test
    public void testCopyConstructor() {
        ComponentVersionSummary sourceComponentVersionSummary = new ComponentVersionSummary(VERSION, RELEASED_ON, DELETED, META);
        ComponentVersionSummary componentVersionSummary = new ComponentVersionSummary(sourceComponentVersionSummary);

        Assert.assertEquals(componentVersionSummary, sourceComponentVersionSummary, "Component version summaries should be equal.");
    }

    @Test
    public void testDeserialization() throws JsonProcessingException {
        ComponentVersionSummary componentVersionSummary = new ComponentVersionSummary(VERSION, RELEASED_ON, DELETED, META);

        String json = serialize(componentVersionSummary);
        ComponentVersionSummary result = deserialize(json, ComponentVersionSummary.class);

        Assert.assertEquals(result.getVersion().orElse(null), VERSION, "Versions should be equal.");
        Assert.assertNotNull(result.getReleasedOn().orElse(null), "Released on should be initialized.");
        Assert.assertFalse(result.isDeleted(), "Component version should not be deleted.");
        Assert.assertEquals(result.getMeta(), META, "Metas should be equal.");
    }

    @Test
    public void testHashCode() {
        ComponentVersionSummary componentVersionSummary = new ComponentVersionSummary(VERSION, RELEASED_ON, DELETED, META);
        ComponentVersionSummary copyComponentVersionSummary = new ComponentVersionSummary(VERSION, RELEASED_ON, DELETED, META);
        ComponentVersionSummary differentComponentVersionSummary = new ComponentVersionSummary(VERSION, RELEASED_ON, Boolean.TRUE, META);

        assertHashCode(componentVersionSummary, copyComponentVersionSummary, differentComponentVersionSummary);
    }

    @Test
    public void testEquals() {
        ComponentVersionSummary componentVersionSummary = new ComponentVersionSummary(VERSION, RELEASED_ON, DELETED, META);
        ComponentVersionSummary copyComponentVersionSummary = new ComponentVersionSummary(VERSION, RELEASED_ON, DELETED, META);
        ComponentVersionSummary differentComponentVersionSummary = new ComponentVersionSummary(VERSION, RELEASED_ON, Boolean.TRUE, META);

        assertEquals(componentVersionSummary, copyComponentVersionSummary, differentComponentVersionSummary);
    }
}
