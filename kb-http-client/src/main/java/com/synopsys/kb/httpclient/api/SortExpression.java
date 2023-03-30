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

import java.util.Objects;

/**
 * Sort expression.
 * 
 * @author skatzman
 */
public class SortExpression {
    private final String field;

    private final boolean isAscending;

    public SortExpression(String field, boolean isAscending) {
        this.field = Objects.requireNonNull(field, "Field must be initialized.");
        this.isAscending = isAscending;
    }

    public String getField() {
        return field;
    }

    public boolean isAscending() {
        return isAscending;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getField(), isAscending());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof SortExpression) {
            SortExpression otherSortExpression = (SortExpression) otherObject;

            return Objects.equals(getField(), otherSortExpression.getField())
                    && Objects.equals(isAscending(), otherSortExpression.isAscending());
        }

        return false;
    }
}
