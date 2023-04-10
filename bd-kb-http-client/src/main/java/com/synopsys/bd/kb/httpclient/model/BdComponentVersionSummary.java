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
package com.synopsys.bd.kb.httpclient.model;

import java.time.OffsetDateTime;
import java.util.Optional;

import com.synopsys.kb.httpclient.model.ComponentVersionSummary;
import com.synopsys.kb.httpclient.model.Meta;

/**
 * Black Duck-centric component version summary.
 * 
 * Used to provide a default version when none is provided.
 * 
 * @author skatzman
 */
public class BdComponentVersionSummary extends ComponentVersionSummary {
    protected static final String UNKNOWN_VERSION = "unknown";

    public BdComponentVersionSummary(String version,
            OffsetDateTime releasedOn,
            Boolean deleted,
            Meta meta) {
        super(version, releasedOn, deleted, meta);
    }

    public BdComponentVersionSummary(ComponentVersionSummary bdComponentVersionSummary) {
        super(bdComponentVersionSummary);
    }

    @Override
    public Optional<String> getVersion() {
        // Assign default for absent version.
        return super.getVersion().or(() -> Optional.of(UNKNOWN_VERSION));
    }

    public String getRequiredVersion() {
        return getVersion().orElseThrow(() -> new IllegalStateException("Version must be defined."));
    }
}
