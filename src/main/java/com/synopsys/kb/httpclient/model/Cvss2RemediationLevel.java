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
 * CVSS2 remediation level enumeration.
 * 
 * @author skatzman
 */
public enum Cvss2RemediationLevel {
    NOT_DEFINED,
    UNAVAILABLE,
    WORKAROUND,
    TEMPORARY_FIX,
    OFFICIAL_FIX;
}
