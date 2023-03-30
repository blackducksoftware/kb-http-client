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
 * Logo representation.
 * 
 * @author skatzman
 */
public class Logo {
    private final String type;

    private final String url;

    @JsonCreator
    public Logo(@JsonProperty("type") String type,
            @JsonProperty("url") String url) {
        this.type = type;
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getUrl());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof Logo) {
            Logo otherComponent = (Logo) otherObject;

            return Objects.equals(getType(), otherComponent.getType())
                    && Objects.equals(getUrl(), otherComponent.getUrl());
        }

        return false;
    }
}
