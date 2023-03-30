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
package com.synopsys.kb.httpclient.util;

import java.lang.reflect.Type;
import java.util.Objects;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Class type reference.
 * 
 * Enables wrapping of a class object within a type reference.
 * 
 * Package protected.
 * 
 * @author skatzman
 */
public class ClassTypeReference<T> extends TypeReference<T> {
    private final Class<T> clazz;

    /**
     * Constructs the class type reference.
     * 
     * @param clazz
     *            The class.
     */
    public ClassTypeReference(Class<T> clazz) {
        this.clazz = Objects.requireNonNull(clazz, "Class must be initialized.");
    }

    @Override
    public Type getType() {
        return clazz;
    }
}
