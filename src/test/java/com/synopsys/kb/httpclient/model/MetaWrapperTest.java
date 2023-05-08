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

/**
 * Meta wrapper test.
 * 
 * @author skatzman
 */
public class MetaWrapperTest extends AbstractTest {
    private static final Meta META = new Meta(BASE_HREF + "/api/components/" + UUID.randomUUID(),
            List.of(new Link("moved", BASE_HREF + "/api/components/" + UUID.randomUUID()),
                    new Link("moved", BASE_HREF + "/api/components/" + UUID.randomUUID()),
                    new Link("moved", BASE_HREF + "/api/components/" + UUID.randomUUID())));

    @Test
    public void testConstructor() {
        MetaWrapper metaWrapper = new MetaWrapper(META);

        Assert.assertEquals(metaWrapper.getMeta().orElse(null), META, "Metas should be equal.");
    }

    @Test
    public void testDeserialization() throws JsonProcessingException {
        MetaWrapper metaWrapper = new MetaWrapper(META);

        String json = serialize(metaWrapper);
        MetaWrapper result = deserialize(json, MetaWrapper.class);

        Assert.assertEquals(result.getMeta().orElse(null), META, "Metas should be equal.");
    }

    @Test
    public void testHashCode() {
        MetaWrapper metaWrapper = new MetaWrapper(META);
        MetaWrapper copyMetaWrapper = new MetaWrapper(META);
        Meta differentMeta = new Meta(BASE_HREF + "/api/components/" + UUID.randomUUID(),
                List.of(new Link("moved", BASE_HREF + "/api/components/" + UUID.randomUUID())));
        MetaWrapper differentMetaWrapper = new MetaWrapper(differentMeta);

        assertHashCode(metaWrapper, copyMetaWrapper, differentMetaWrapper);
    }

    @Test
    public void testEquals() {
        MetaWrapper metaWrapper = new MetaWrapper(META);
        MetaWrapper copyMetaWrapper = new MetaWrapper(META);
        Meta differentMeta = new Meta(BASE_HREF + "/api/components/" + UUID.randomUUID(),
                List.of(new Link("moved", BASE_HREF + "/api/components/" + UUID.randomUUID())));
        MetaWrapper differentMetaWrapper = new MetaWrapper(differentMeta);

        assertEquals(metaWrapper, copyMetaWrapper, differentMetaWrapper);
    }
}
