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

/**
 * Code base maturity enumeration.
 * 
 * @author skatzman
 */
public enum CodeBaseMaturity {
    // Indicates that the project has a short history
    SHORT_HISTORY,
    // Indicates that the project is young, but has an established presence
    YOUNG_BUT_ESTABLISHED,
    // Indicates that the project is well established
    WELL_ESTABLISHED,
    // Indicates that the project is mature
    MATURE;
}
