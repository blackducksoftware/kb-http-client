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
package com.synopsys.bd.kb.httpclient.api;

import java.util.UUID;

import com.synopsys.bd.kb.httpclient.model.BdComponentVariant;
import com.synopsys.kb.httpclient.api.HttpResult;

/**
 * Black Duck-centric component variant API interface.
 * 
 * @author skatzman
 */
public interface IBdComponentVariantApi {
    /**
     * Finds a component variant by its id and follows migration links.
     * 
     * Version: 4
     * 
     * Expected response codes
     * 200 OK
     * 404 Not Found
     * 
     * @param componentVariantId
     *            The component variant id.
     * @return Returns the Black Duck-centric component variant result.
     */
    HttpResult<BdComponentVariant> findComponentVariantV4(UUID componentVariantId);
}
