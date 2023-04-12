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
 * Team size enumeration.
 * 
 * @author skatzman
 */
public enum TeamSize {
    // Indicates that the project has not been contributed to recently
    NO_RECENT_ACTIVITY,
    // Indicates that the project has a single active developer contributing to it
    SINGLE_ACTIVE_DEVELOPER,
    // Indicates that the project is contributed to by a small development team
    SMALL_DEVELOPMENT_TEAM,
    // Indicates that the project is contributed to by an average sized development team
    AVERAGE_SIZE_DEVELOPMENT_TEAM,
    // Indicates that the project is contributed to by a large development team
    LARGE_DEVELOPMENT_TEAM,
    // Indicates that the project is contributed to by a very large development team
    VERY_LARGE_DEVELOPMENT_TEAM;
}
