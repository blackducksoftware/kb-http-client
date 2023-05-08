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

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Authentication response data structure.
 * 
 * @author skatzman
 */
public class Authentication {
    private final boolean isExpirationWarning;

    private final String jsonWebToken;

    private final long expiresInMillis;

    @JsonCreator
    public Authentication(@JsonProperty("expirationWarning") boolean isExpirationWarning,
            @JsonProperty("jsonWebToken") String jsonWebToken,
            @JsonProperty("expiresInMillis") long expiresInMillis) {
        this.isExpirationWarning = isExpirationWarning;
        this.jsonWebToken = jsonWebToken;
        this.expiresInMillis = expiresInMillis;
    }

    public boolean isExpirationWarning() {
        return isExpirationWarning;
    }

    public String getJsonWebToken() {
        return jsonWebToken;
    }

    public long getExpiresInMillis() {
        return expiresInMillis;
    }

    @Override
    public int hashCode() {
        return Objects.hash(isExpirationWarning(), getJsonWebToken(), getExpiresInMillis());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof Authentication) {
            Authentication otherAuthentication = (Authentication) otherObject;

            return Objects.equals(isExpirationWarning(), otherAuthentication.isExpirationWarning())
                    && Objects.equals(getJsonWebToken(), otherAuthentication.getJsonWebToken())
                    && Objects.equals(getExpiresInMillis(), otherAuthentication.getExpiresInMillis());
        }

        return false;
    }
}
