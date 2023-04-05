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

import java.util.Set;
import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.synopsys.kb.httpclient.AbstractTest;

/**
 * Id holder test.
 * 
 * @author skatzman
 */
public class IdHolderTest extends AbstractTest {
    private static final Set<UUID> UUID_IDS = Set.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

    private static final Set<String> STRING_IDS = Set.of("1", "2", "3");

    @Test
    public void testConstructorForUuid() {
        IdHolder<UUID> idHolder = new IdHolder<>(UUID_IDS);

        Assert.assertEquals(idHolder.getIds(), UUID_IDS, "Ids should be equal.");
    }

    @Test
    public void testConstructorForString() {
        IdHolder<String> idHolder = new IdHolder<>(STRING_IDS);

        Assert.assertEquals(idHolder.getIds(), STRING_IDS, "Ids should be equal.");
    }

    @Test
    public void testDeserializationForUuid() throws JsonProcessingException {
        IdHolder<UUID> idHolder = new IdHolder<>(UUID_IDS);

        String json = serialize(idHolder);
        IdHolder<UUID> result = deserialize(json, new TypeReference<IdHolder<UUID>>() {
        });

        Assert.assertEquals(result.getIds(), UUID_IDS, "Ids should be equal.");
    }

    @Test
    public void testDeserializationForString() throws JsonProcessingException {
        IdHolder<String> idHolder = new IdHolder<>(STRING_IDS);

        String json = serialize(idHolder);
        IdHolder<String> result = deserialize(json, new TypeReference<IdHolder<String>>() {
        });

        Assert.assertEquals(result.getIds(), STRING_IDS, "Ids should be equal.");
    }
}
