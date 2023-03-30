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

import java.io.IOException;

/**
 * Authentication exception.
 * 
 * Package protected.
 * 
 * @author skatzman
 */
class AuthenticationException extends IOException {
    private static final long serialVersionUID = 1L;

    public AuthenticationException(String message) {
        super(message);
    }
}
