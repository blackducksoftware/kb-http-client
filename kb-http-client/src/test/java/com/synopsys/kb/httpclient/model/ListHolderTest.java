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
import java.util.List;
import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.synopsys.kb.httpclient.AbstractTest;

/**
 * List holder test.
 * 
 * @author skatzman
 */
public class ListHolderTest extends AbstractTest {
    private static final List<String> ITEMS = List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");

    private static final UUID COMPONENT_ID = UUID.randomUUID();

    private static final String HREF = BASE_HREF + "/api/activity/components/" + COMPONENT_ID;

    private static final Meta META = new Meta(HREF, Collections.emptyList());

    @Test
    public void testConstructor() {
        ListHolder<String> listHolder = new ListHolder<>(ITEMS, META);

        Assert.assertEquals(listHolder.getItems(), ITEMS, "Items should be equal.");
        Assert.assertEquals(listHolder.getMeta(), META, "Metas should be equal.");
    }

    @Test
    public void testDeserialization() throws JsonProcessingException {
        ListHolder<String> listHolder = new ListHolder<>(ITEMS, META);

        String json = serialize(listHolder);
        ListHolder<String> result = deserialize(json, new TypeReference<ListHolder<String>>() {
        });

        Assert.assertEquals(result.getItems(), ITEMS, "Items should be equal.");
        Assert.assertEquals(result.getMeta(), META, "Metas should be equal.");
    }
}
