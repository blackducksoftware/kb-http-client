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
 * Next version test.
 * 
 * @author skatzman
 */
public class NextVersionTest extends AbstractTest {
    private static final UUID COMPONENT_VERSION_ID = UUID.randomUUID();

    private static final int NEXT_VERSIONS_INCLUDING_DELETED_COUNT = 100;

    private static final int NEXT_VERSIONS_EXCLUDING_DELETED_COUNT = 90;

    private static final Meta META = new Meta(BASE_HREF + "/api/versions/" + COMPONENT_VERSION_ID + "/next", Collections.emptyList());

    @Test
    public void testConstructor() {
        NextVersion nextVersion = new NextVersion(NEXT_VERSIONS_INCLUDING_DELETED_COUNT, NEXT_VERSIONS_EXCLUDING_DELETED_COUNT, META);

        Assert.assertEquals(nextVersion.getNextVersionsIncludingDeletedCount(), NEXT_VERSIONS_INCLUDING_DELETED_COUNT,
                "Next versions including deleted counts should be equal.");
        Assert.assertEquals(nextVersion.getNextVersionsExcludingDeletedCount(), NEXT_VERSIONS_EXCLUDING_DELETED_COUNT,
                "Next versions excluding deleted counts should be equal.");
        Assert.assertEquals(nextVersion.getMeta(), META, "Metas should be equal.");

        Assert.assertEquals(nextVersion.getComponentVersionId(), COMPONENT_VERSION_ID, "Component version ids should be equal.");
    }

    @Test
    public void testDeserialization() throws JsonProcessingException {
        NextVersion nextVersion = new NextVersion(NEXT_VERSIONS_INCLUDING_DELETED_COUNT, NEXT_VERSIONS_EXCLUDING_DELETED_COUNT, META);

        String json = serialize(nextVersion);
        NextVersion result = deserialize(json, NextVersion.class);

        Assert.assertEquals(result.getNextVersionsIncludingDeletedCount(), NEXT_VERSIONS_INCLUDING_DELETED_COUNT,
                "Next versions including deleted counts should be equal.");
        Assert.assertEquals(result.getNextVersionsExcludingDeletedCount(), NEXT_VERSIONS_EXCLUDING_DELETED_COUNT,
                "Next versions excluding deleted counts should be equal.");
        Assert.assertEquals(result.getMeta(), META, "Metas should be equal.");
    }

    @Test
    public void testHashCode() {
        NextVersion nextVersion = new NextVersion(NEXT_VERSIONS_INCLUDING_DELETED_COUNT, NEXT_VERSIONS_EXCLUDING_DELETED_COUNT, META);
        NextVersion copyNextVersion = new NextVersion(NEXT_VERSIONS_INCLUDING_DELETED_COUNT, NEXT_VERSIONS_EXCLUDING_DELETED_COUNT, META);
        NextVersion differentNextVersion = new NextVersion(NEXT_VERSIONS_INCLUDING_DELETED_COUNT, 99999, META);

        assertHashCode(nextVersion, copyNextVersion, differentNextVersion);
    }

    @Test
    public void testEquals() {
        NextVersion nextVersion = new NextVersion(NEXT_VERSIONS_INCLUDING_DELETED_COUNT, NEXT_VERSIONS_EXCLUDING_DELETED_COUNT, META);
        NextVersion copyNextVersion = new NextVersion(NEXT_VERSIONS_INCLUDING_DELETED_COUNT, NEXT_VERSIONS_EXCLUDING_DELETED_COUNT, META);
        NextVersion differentNextVersion = new NextVersion(NEXT_VERSIONS_INCLUDING_DELETED_COUNT, 99999, META);

        assertEquals(nextVersion, copyNextVersion, differentNextVersion);
    }
}
