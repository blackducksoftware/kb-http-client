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

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synopsys.kb.httpclient.AbstractTest;

/**
 * Main language test.
 * 
 * @author skatzman
 */
public class MainLanguageTest extends AbstractTest {
    private static final String LANGUAGE_NAME = "Java";

    @Test
    public void testConstructor() {
        MainLanguage mainLanguage = new MainLanguage(LANGUAGE_NAME);

        Assert.assertEquals(mainLanguage.getLanguageName().orElse(null), LANGUAGE_NAME, "Language names should be equal.");
    }

    @Test
    public void testDeserialization() throws JsonProcessingException {
        MainLanguage mainLanguage = new MainLanguage(LANGUAGE_NAME);

        String json = serialize(mainLanguage);
        MainLanguage result = deserialize(json, MainLanguage.class);

        Assert.assertEquals(result.getLanguageName().orElse(null), LANGUAGE_NAME, "Language names should be equal.");
    }
}
