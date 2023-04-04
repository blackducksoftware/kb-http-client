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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.synopsys.kb.httpclient.model.ComponentVersion;
import com.synopsys.kb.httpclient.model.LicenseDefinition;
import com.synopsys.kb.httpclient.model.LicenseDefinitionItem;
import com.synopsys.kb.httpclient.model.LicenseDefinitionType;
import com.synopsys.kb.httpclient.model.Meta;
import com.synopsys.kb.httpclient.model.RiskProfile;

/**
 * Black Duck-centric component version.
 * 
 * Used to provide a default version and/or license definition when none is provided.
 * 
 * @author skatzman
 */
public class BdComponentVersion extends ComponentVersion {
    protected static final String UNKNOWN_VERSION = "unknown";

    protected static final UUID UNKNOWN_KB_LICENSE_ID = UUID.fromString("00000000-0010-0000-0000-000000000000");

    private final LicenseDefinition unknownLicenseDefinition;

    public BdComponentVersion(String version,
            OffsetDateTime releasedOn,
            LicenseDefinition licenseDefinition,
            RiskProfile riskProfile,
            Boolean deleted,
            Boolean componentIntelligence,
            Meta meta,
            String baseHref) {
        super(version, releasedOn, licenseDefinition, riskProfile, deleted, componentIntelligence, meta);

        Preconditions.checkArgument(!Strings.isNullOrEmpty(baseHref), "Base HREF must not be null or empty.");

        // Need to dynamically construct in order to preserve accurate HREF.
        String href = baseHref + "/api/licenses/" + UNKNOWN_KB_LICENSE_ID;
        LicenseDefinitionItem licenseDefinitionItem = new LicenseDefinitionItem(href, null);
        List<LicenseDefinitionItem> licenseDefinitionItems = List.of(licenseDefinitionItem);
        this.unknownLicenseDefinition = new LicenseDefinition(LicenseDefinitionType.CONJUNCTIVE, licenseDefinitionItems);
    }

    public BdComponentVersion(ComponentVersion componentVersion,
            String baseHref) {
        super(componentVersion);

        Preconditions.checkArgument(!Strings.isNullOrEmpty(baseHref), "Base HREF must not be null or empty.");

        // Need to dynamically construct in order to preserve accurate HREF.
        String href = baseHref + "/api/licenses/" + UNKNOWN_KB_LICENSE_ID;
        LicenseDefinitionItem licenseDefinitionItem = new LicenseDefinitionItem(href, null);
        List<LicenseDefinitionItem> licenseDefinitionItems = List.of(licenseDefinitionItem);
        this.unknownLicenseDefinition = new LicenseDefinition(LicenseDefinitionType.CONJUNCTIVE, licenseDefinitionItems);
    }

    @Override
    public Optional<String> getVersion() {
        // Assign default for absent version.
        return super.getVersion().or(() -> Optional.of(UNKNOWN_VERSION));
    }

    public String getRequiredVersion() {
        return getVersion().orElseThrow(() -> new IllegalStateException("Version must be defined."));
    }

    @Override
    public Optional<LicenseDefinition> getLicenseDefinition() {
        // Assign default for absent license definition.
        return super.getLicenseDefinition().or(() -> Optional.of(this.unknownLicenseDefinition));
    }

    public LicenseDefinition getRequiredLicenseDefinition() {
        return getLicenseDefinition().orElseThrow(() -> new IllegalStateException("License definition must be defined."));
    }
}
