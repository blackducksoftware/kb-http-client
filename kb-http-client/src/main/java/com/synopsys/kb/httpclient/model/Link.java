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
 * Link representation.
 * 
 * @author skatzman
 */
public class Link {
    private final String rel;

    private final String href;

    @JsonCreator
    public Link(@JsonProperty("rel") String rel,
            @JsonProperty("href") String href) {
        this.rel = rel;
        this.href = href;
    }

    public String getRel() {
        return rel;
    }

    public String getHref() {
        return href;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRel(), getHref());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof Link) {
            Link otherLink = (Link) otherObject;

            return Objects.equals(getRel(), otherLink.getRel())
                    && Objects.equals(getHref(), otherLink.getHref());
        }

        return false;
    }
}
