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

import java.util.Optional;

import com.synopsys.kb.httpclient.model.ComponentVariant;
import com.synopsys.kb.httpclient.model.LicenseDefinition;
import com.synopsys.kb.httpclient.model.Meta;

/**
 * Black Duck-centric component variant representation.
 * 
 * Used to have a default external namespace and/or external id when none is provided.
 * 
 * @author skatzman
 */
public class BdComponentVariant extends ComponentVariant {
    protected static final String DEFAULT_EXTERNAL_NAMESPACE = "unknown";

    protected static final String DEFAULT_EXTERNAL_ID = "unknown";

    public BdComponentVariant(String version,
            String externalNamespace,
            String externalId,
            Boolean externalNamespaceDistribution,
            String packageUrl,
            String type,
            LicenseDefinition licenseDefinition,
            Boolean deleted,
            Boolean componentIntelligence,
            Meta meta) {
        super(version, externalNamespace, externalId, externalNamespaceDistribution, packageUrl, type, licenseDefinition, deleted, componentIntelligence, meta);
    }

    public BdComponentVariant(ComponentVariant componentVariant) {
        super(componentVariant);
    }

    @Override
    public Optional<String> getExternalNamespace() {
        // Assign default for absent external namespace.
        return super.getExternalNamespace().or(() -> Optional.of(DEFAULT_EXTERNAL_NAMESPACE));
    }

    public String getRequiredExternalNamespace() {
        return getExternalNamespace().orElseThrow(() -> new IllegalStateException("External namespace must be defined."));
    }

    @Override
    public Optional<String> getExternalId() {
        // Assign default for absent external id.
        return super.getExternalId().or(() -> Optional.of(DEFAULT_EXTERNAL_ID));
    }

    public String getRequiredExternalId() {
        return getExternalId().orElseThrow(() -> new IllegalStateException("External id must be defined."));
    }
}
