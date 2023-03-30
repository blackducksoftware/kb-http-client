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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.synopsys.kb.httpclient.AbstractTest;

/**
 * Page request test.
 * 
 * @author skatzman
 */
public class PageRequestTest extends AbstractTest {
    private static final int OFFSET = 0;

    private static final int LIMIT = 1000;

    private static final SortExpression SORT_EXPRESSION = new SortExpression("version", false);

    @Test
    public void testConstructor() {
        List<SortExpression> sortExpressions = List.of(SORT_EXPRESSION);

        PageRequest pageRequest = new PageRequest(OFFSET, LIMIT, sortExpressions);

        Assert.assertEquals(pageRequest.getOffset(), OFFSET, "Offsets should be equal.");
        Assert.assertEquals(pageRequest.getLimit(), LIMIT, "Limits should be equal.");
        Assert.assertEquals(pageRequest.getSortExpressions(), sortExpressions, "Sort expressions should be equal.");
    }

    @Test
    public void testGetSortExpressionsStringWithoutSortExpressions() {
        PageRequest pageRequest = new PageRequest(OFFSET, LIMIT, Collections.emptyList());

        Optional<String> result = pageRequest.getSortExpressionsString();

        Assert.assertFalse(result.isPresent(), "Sort expressions string should not be present.");
    }

    @Test
    public void testGetSortExpressionsStringWithOneSortExpression() {
        SortExpression sortExpression1 = new SortExpression("v1", false);
        List<SortExpression> sortExpressions = List.of(sortExpression1);

        PageRequest pageRequest = new PageRequest(OFFSET, LIMIT, sortExpressions);

        Optional<String> result = pageRequest.getSortExpressionsString();

        Assert.assertTrue(result.isPresent(), "Sort expressions string should be present.");
        Assert.assertEquals(result.orElse(null), "v1 desc", "Sort expression strings should be equal.");
    }

    @Test
    public void testGetSortExpressionsStringWithManySortExpressions() {
        SortExpression sortExpression1 = new SortExpression("v1", false);
        SortExpression sortExpression2 = new SortExpression("v2", true);
        SortExpression sortExpression3 = new SortExpression("v3", false);
        List<SortExpression> sortExpressions = List.of(sortExpression1, sortExpression2, sortExpression3);

        PageRequest pageRequest = new PageRequest(OFFSET, LIMIT, sortExpressions);

        Optional<String> result = pageRequest.getSortExpressionsString();

        Assert.assertTrue(result.isPresent(), "Sort expressions string should be present.");
        Assert.assertEquals(result.orElse(null), "v1 desc,v2 asc,v3 desc", "Sort expression strings should be equal.");
    }
}
