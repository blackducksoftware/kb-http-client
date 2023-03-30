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
import com.fasterxml.jackson.core.type.TypeReference;
import com.synopsys.kb.httpclient.AbstractTest;

/**
 * Page test.
 * 
 * @author skatzman
 */
public class PageTest extends AbstractTest {
    private static final int TOTAL_COUNT = 10;

    private static final List<String> ITEMS = List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");

    private static final UUID ID = UUID.randomUUID();

    private static final String HREF = "https://kbtest.blackducksoftware.com/api/components/" + ID;

    private static final Meta META = new Meta(HREF,
            List.of(new Link("versions", HREF + "/versions")));

    @Test
    public void testConstructor() {
        Page<String> page = new Page<>(TOTAL_COUNT, ITEMS, META);

        Assert.assertEquals(page.getTotalCount(), TOTAL_COUNT, "Total counts should be equal.");
        Assert.assertEquals(page.getItems(), ITEMS, "Items should be equal.");
        Assert.assertEquals(page.getMeta(), META, "Metas should be equal.");
    }

    @Test
    public void testDeserialization() throws JsonProcessingException {
        Page<String> page = new Page<>(TOTAL_COUNT, ITEMS, META);

        String json = serialize(page);
        Page<String> result = deserialize(json, new TypeReference<Page<String>>() {
        });

        Assert.assertEquals(result.getTotalCount(), TOTAL_COUNT, "Total counts should be equal.");
        Assert.assertEquals(result.getItems(), ITEMS, "Items should be equal.");
        Assert.assertEquals(result.getMeta(), META, "Metas should be equal.");
    }
}
