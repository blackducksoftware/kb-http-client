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
}
