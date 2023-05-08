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

import java.time.OffsetDateTime;

/**
 * Abstract activity representation.
 * 
 * Package protected.
 * 
 * @author skatzman
 *
 */
class AbstractActivity extends AbstractEntity {
    private final OffsetDateTime updatedDate;

    protected AbstractActivity(OffsetDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public OffsetDateTime getUpdatedDate() {
        return updatedDate;
    }
}
