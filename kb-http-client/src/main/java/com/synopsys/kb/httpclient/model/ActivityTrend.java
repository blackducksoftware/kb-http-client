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
 * Activity trend enumeration.
 * 
 * @author skatzman
 */
public enum ActivityTrend {
    // Indicates that the project is seeing less activity recently than in the past
    DECREASING,
    // Indicates that the project is seeing the same amount of activity as in the past
    STABLE,
    // Indicates that the project is seeing more activity recently than in the past
    INCREASING;
}
