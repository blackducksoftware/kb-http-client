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
package com.synopsys.bd.kb.httpclient.model;

/**
 * Black Duck license family enumeration.
 * 
 * @author skatzman
 */
public enum LicenseFamily {
    PERMISSIVE,
    WEAK_RECIPROCAL,
    RECIPROCAL,
    RECIPROCAL_AGPL,
    RECIPROCAL_NETWORK,
    RESTRICTED_PROPRIETARY, // BD-specific.
    UNKNOWN;
}
