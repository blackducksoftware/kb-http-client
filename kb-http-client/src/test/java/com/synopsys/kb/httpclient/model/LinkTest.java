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
 * Link test.
 * 
 * @author skatzman
 */
public class LinkTest extends AbstractTest {
    private static final String REL = "text";

    private static final String HREF = "https://kbtest.blackducksoftware.com/api/licenses/" + UUID.randomUUID() + "/text";

    @Test
    public void testConstructor() {
        Link link = new Link(REL, HREF);

        Assert.assertEquals(link.getRel(), REL, "Rels should be equal.");
        Assert.assertEquals(link.getHref(), HREF, "HREFs should be equal.");
    }

    @Test
    public void testDeserialization() throws JsonProcessingException {
        Link link = new Link(REL, HREF);

        String json = serialize(link);
        Link result = deserialize(json, Link.class);

        Assert.assertEquals(result.getRel(), REL, "Rels should be equal.");
        Assert.assertEquals(result.getHref(), HREF, "HREFs should be equal.");
    }
}
