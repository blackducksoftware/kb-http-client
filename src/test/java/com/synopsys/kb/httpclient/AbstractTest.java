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
package com.synopsys.kb.httpclient;

import org.testng.Assert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.synopsys.kb.httpclient.util.JsonUtil;

/**
 * Abstract test.
 * 
 * @author skatzman
 */
public abstract class AbstractTest {
    protected static final String BASE_HREF = "https://kbtest.blackducksoftware.com";

    /**
     * Serialize the given object to a JSON string.
     * 
     * @param <T>
     *            The object type.
     * @param object
     *            The object.
     * @return Returns the JSON string.
     * @throws JsonProcessingException
     */
    protected <T> String serialize(T object) throws JsonProcessingException {
        return JsonUtil.objectMapper.writeValueAsString(object);
    }

    protected <T> T deserialize(String json, Class<T> clazz) throws JsonMappingException, JsonProcessingException {
        return JsonUtil.objectMapper.readValue(json, clazz);
    }

    protected <T> T deserialize(String json, TypeReference<T> typeReference) throws JsonMappingException, JsonProcessingException {
        return JsonUtil.objectMapper.readValue(json, typeReference);
    }

    protected <T> void assertHashCode(T object, T copyObject, T differentObject) {
        Assert.assertEquals(object.hashCode(), object.hashCode(), "Hash codes should be equal.");
        Assert.assertEquals(object.hashCode(), copyObject.hashCode(), "Hash codes should be equal.");
        Assert.assertNotEquals(object.hashCode(), differentObject.hashCode(), "Hash codes should not be equal.");
    }

    protected <T> void assertEquals(T object, T copyObject, T differentObject) {
        Assert.assertFalse(object.equals((T) null), "Objects should not be equal.");
        Assert.assertFalse(object.equals((Void) null), "Objects should not be equal.");
        Assert.assertTrue(object.equals(object), "Objects should be equal.");
        Assert.assertTrue(object.equals(copyObject), "Objects should be equal.");
        Assert.assertTrue(copyObject.equals(object), "Objects should be equal.");
        Assert.assertFalse(object.equals(differentObject), "Objects should not be equal.");
        Assert.assertFalse(differentObject.equals(object), "Objects should not be equal.");
    }
}
