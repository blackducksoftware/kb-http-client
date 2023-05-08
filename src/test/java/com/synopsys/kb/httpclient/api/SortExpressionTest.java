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
 * Sort expression test.
 * 
 * @author skatzman
 */
public class SortExpressionTest extends AbstractTest {
    private static final String FIELD = "version";

    @Test
    public void testConstructor() {
        SortExpression sortExpression = new SortExpression(FIELD, false);

        Assert.assertEquals(sortExpression.getField(), FIELD, "Fields should be equal.");
        Assert.assertFalse(sortExpression.isAscending(), "Sort expression should not be ascending.");
    }

    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void testConstructorWithNullField() {
        new SortExpression(null, false);
    }

    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void testConstructorWithEmptyField() {
        new SortExpression("", false);
    }
}
