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
package com.synopsys.kb.httpclient.api;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.synopsys.kb.httpclient.AbstractTest;

/**
 * Authentication exception test.'
 * 
 * @author skatzman
 */
public class AuthenticationExceptionTest extends AbstractTest {
    private static final String MESSAGE = "This is a message.";

    @Test
    public void testConstructor() {
        AuthenticationException e = new AuthenticationException(MESSAGE);

        Assert.assertEquals(e.getMessage(), MESSAGE, "Messages should be equal.");
    }
}
