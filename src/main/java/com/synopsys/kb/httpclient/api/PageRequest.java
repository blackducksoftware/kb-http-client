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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

/**
 * Page request.
 * 
 * @author skatzman
 */
public class PageRequest {
    public static final int DEFAULT_OFFSET = 0;

    public static final int DEFAULT_LIMIT = 10;

    public static final int MAX_LIMIT = 1000;

    private final int offset;

    private final int limit;

    private final List<SortExpression> sortExpressions;

    public PageRequest() {
        this(DEFAULT_OFFSET, DEFAULT_LIMIT, Collections.emptyList());
    }

    /**
     * Constructs a page request.
     * 
     * @param offset
     *            The offset. Must be greater than or equal to 0.
     * @param limit
     *            The limit. Must be greater than or equal to 0 AND less than or equal to 1000.
     * @param sortExpressions
     *            The sort expressions.
     */
    public PageRequest(int offset, int limit, List<SortExpression> sortExpressions) {
        Preconditions.checkArgument(offset >= 0, "Offset must be greater than or equal to 0.");
        Preconditions.checkArgument(limit >= 0, "Limit must be greater than or equal to 0.");
        Preconditions.checkArgument(limit <= MAX_LIMIT, "Limit must be less than or equal to " + MAX_LIMIT + '.');

        this.offset = offset;
        this.limit = limit;
        this.sortExpressions = (sortExpressions != null) ? ImmutableList.copyOf(sortExpressions) : ImmutableList.of();
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }

    public List<SortExpression> getSortExpressions() {
        return sortExpressions;
    }

    public Optional<String> getSortExpressionsString() {
        String sort = null;

        if (!sortExpressions.isEmpty()) {
            sort = sortExpressions.stream().map((sortExpression) -> {
                String field = sortExpression.getField();
                String order = (sortExpression.isAscending()) ? "asc" : "desc";

                return field + ' ' + order;
            }).collect(Collectors.joining(","));
        }

        return Optional.ofNullable(sort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOffset(), getLimit(), getSortExpressions());
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof PageRequest) {
            PageRequest otherPageRequest = (PageRequest) otherObject;

            return Objects.equals(getOffset(), otherPageRequest.getOffset())
                    && Objects.equals(getLimit(), otherPageRequest.getLimit())
                    && Objects.equals(getSortExpressions(), otherPageRequest.getSortExpressions());
        }

        return false;
    }
}
