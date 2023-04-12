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
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Main language representation.
 * 
 * @author skatzman
 */
public class MainLanguage {
    private final String languageName;

    @JsonCreator
    public MainLanguage(@JsonProperty("languageName") String languageName) {
        this.languageName = languageName;
    }

    public Optional<String> getLanguageName() {
        return Optional.ofNullable(languageName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLanguageName());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof MainLanguage) {
            MainLanguage otherMainLanguage = (MainLanguage) otherObject;

            return Objects.equals(getLanguageName(), otherMainLanguage.getLanguageName());
        }

        return false;
    }
}
