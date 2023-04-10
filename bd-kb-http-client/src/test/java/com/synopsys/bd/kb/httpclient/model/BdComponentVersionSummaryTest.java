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
import com.synopsys.kb.httpclient.model.Link;
import com.synopsys.kb.httpclient.model.Meta;

/**
 * Black Duck-centric component version summary test.
 * 
 * @author skatzman
 */
public class BdComponentVersionSummaryTest extends AbstractBdTest {
    private static final UUID ID = UUID.randomUUID();

    private static final UUID COMPONENT_ID = UUID.randomUUID();

    private static final String VERSION = "1.0";

    private static final OffsetDateTime RELEASED_ON = OffsetDateTime.now();

    private static final Boolean DELETED = Boolean.FALSE;

    private static final Meta META = new Meta(BASE_HREF + "/api/versions/" + ID,
            List.of(new Link("component", BASE_HREF + "/api/components/" + COMPONENT_ID)));

    @Test
    public void testConstructor() {
        BdComponentVersionSummary bdComponentVersionSummary = new BdComponentVersionSummary(VERSION, RELEASED_ON, DELETED, META);

        Assert.assertEquals(bdComponentVersionSummary.getVersion().orElse(null), VERSION, "Versions should be equal.");
        Assert.assertEquals(bdComponentVersionSummary.getReleasedOn().orElse(null), RELEASED_ON, "Released ons should be equal.");
        Assert.assertFalse(bdComponentVersionSummary.isDeleted(), "Component version should not be deleted.");
        Assert.assertEquals(bdComponentVersionSummary.getMeta(), META, "Metas should be equal.");

        Assert.assertEquals(bdComponentVersionSummary.getRequiredVersion(), VERSION, "Versions should be equal.");
        Assert.assertEquals(bdComponentVersionSummary.getId(), ID, "Ids should be equal.");
        Assert.assertEquals(bdComponentVersionSummary.getComponentId(), COMPONENT_ID, "Component ids should be equal.");
    }

    @Test
    public void testGetVersionWhenAbsent() {
        BdComponentVersionSummary bdComponentVersionSummary = new BdComponentVersionSummary(null, RELEASED_ON, DELETED, META);

        Assert.assertEquals(bdComponentVersionSummary.getVersion().orElse(null), BdComponentVersion.UNKNOWN_VERSION, "Versions should be equal.");
        Assert.assertEquals(bdComponentVersionSummary.getRequiredVersion(), BdComponentVersion.UNKNOWN_VERSION, "Versions should be equal.");
    }
}
