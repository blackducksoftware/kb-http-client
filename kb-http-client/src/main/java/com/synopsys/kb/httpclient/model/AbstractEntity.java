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

import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nullable;

import org.apache.hc.core5.net.URIBuilder;

import com.google.common.base.Splitter;

/**
 * Abstract entity.
 * 
 * Package protected.
 * 
 * @author skatzman
 */
abstract class AbstractEntity {
    private static final char PATH_VARIABLE_SEPARATOR_CHAR = '/';

    protected AbstractEntity() {
    }

    protected Optional<String> extractId(@Nullable final String href, final String pathVariable) {
        Objects.requireNonNull(pathVariable, "Path variable must be initialized.");

        String id = null;

        if (href != null) {
            try {
                URIBuilder uriBuilder = new URIBuilder(href);
                String uriPath = uriBuilder.getPath();
                if (uriPath != null) {
                    List<String> pathSegments = Splitter.on(PATH_VARIABLE_SEPARATOR_CHAR).omitEmptyStrings().splitToList(uriPath);

                    int numberOfPathSegments = pathSegments.size();
                    if (numberOfPathSegments >= 2) {
                        String ownerPathSegment = pathSegments.get(numberOfPathSegments - 2);
                        if (ownerPathSegment.equalsIgnoreCase(pathVariable)) {
                            id = pathSegments.get(numberOfPathSegments - 1);
                        } else {
                            // Manage final check in case owner is not at end of URL.
                            for (int i = 0; i < numberOfPathSegments; i++) {
                                int nextIndex = i + 1;
                                String pathSegment = pathSegments.get(i);
                                if ((pathSegment.equalsIgnoreCase(pathVariable)) && (nextIndex < numberOfPathSegments)) {
                                    id = pathSegments.get(nextIndex);
                                }
                            }
                        }
                    }
                }
            } catch (URISyntaxException e) {
                id = null;
            }
        }

        return Optional.ofNullable(id);
    }
}
