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
package com.synopsys.bd.kb.httpclient.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.synopsys.bd.kb.httpclient.api.IBdLicenseApi;
import com.synopsys.bd.kb.httpclient.model.BdLicense;
import com.synopsys.bd.kb.httpclient.model.BdLicenseDefinition;
import com.synopsys.kb.httpclient.api.HttpResult;
import com.synopsys.kb.httpclient.model.LicenseDefinition;
import com.synopsys.kb.httpclient.model.LicenseDefinitionItem;
import com.synopsys.kb.httpclient.model.LicenseDefinitionType;

/**
 * Black Duck-centric license definition finder.
 * 
 * Finds complete license definition metadata.
 * 
 * @author skatzman
 */
public class BdLicenseDefinitionFinder {
    private final IBdLicenseApi bdLicenseApi;

    private final int maximumAttemptNumber;

    public BdLicenseDefinitionFinder(IBdLicenseApi bdLicenseApi) {
        this(bdLicenseApi, LicenseDefinition.MAX_DEPTH);
    }

    BdLicenseDefinitionFinder(IBdLicenseApi bdLicenseApi, int maximumAttemptNumber) {
        this.bdLicenseApi = Objects.requireNonNull(bdLicenseApi, "Black Duck-centric license API must be initialized.");

        Preconditions.checkArgument(maximumAttemptNumber >= 1, "Maximum attempt number must be greater than or equal to 0.");
        this.maximumAttemptNumber = maximumAttemptNumber;
    }

    /**
     * Finds a Black Duck-centric license definition representation given the source license definition.
     * 
     * @param licenseDefinition
     *            The license definition.
     * @return Returns the Black Duck-centric license definition if present and emptiness otherwise.
     */
    public Optional<BdLicenseDefinition> find(LicenseDefinition licenseDefinition) {
        Objects.requireNonNull(licenseDefinition, "License definition must be initialized.");

        return find(licenseDefinition, 0);
    }

    public Optional<BdLicenseDefinition> find(LicenseDefinition sourceLicenseDefinition, int depth) {
        if (depth >= this.maximumAttemptNumber) {
            // Have a max depth guard for traversing too deep into license trees as self-protection for extreme edge
            // cases or malformed data.
            return Optional.empty();
        }

        ImmutableList.Builder<BdLicenseDefinition> builder = ImmutableList.builder();

        LicenseDefinitionType sourceType = sourceLicenseDefinition.getType();
        List<LicenseDefinitionItem> sourceItems = sourceLicenseDefinition.getItems();
        for (LicenseDefinitionItem sourceItem : sourceItems) {
            UUID licenseId = sourceItem.getLicenseId().orElse(null);
            LicenseDefinition licenseDefinition = sourceItem.getLicenseDefinition().orElse(null);
            if (licenseId != null) {
                // License definition item for a license.
                BdLicense bdLicense = findBdLicense(licenseId).orElse(null);
                if (bdLicense != null) {
                    BdLicenseDefinition bdLicenseDefinition = new BdLicenseDefinition(bdLicense);
                    builder = builder.add(bdLicenseDefinition);
                } else {
                    // Otherwise, this is an network error or data integrity issue, short-circuit and return emptiness.
                    return Optional.empty();
                }
            } else if (licenseDefinition != null) {
                // License definition item for a license definition.
                int updatedDepth = depth + 1;
                BdLicenseDefinition bdLicenseDefinition = find(licenseDefinition, updatedDepth).orElse(null);
                if (bdLicenseDefinition != null) {
                    builder = builder.add(bdLicenseDefinition);
                } else {
                    // Otherwise, this is an invalid license definition item due to network error or data integrity
                    // issue, short-circuit and return emptiness.
                    return Optional.empty();
                }
            } // Otherwise, this is an invalid license definition item - ignore it.
        }

        BdLicenseDefinition bdLicenseDefinition = null;

        List<BdLicenseDefinition> bdLicenseDefinitions = builder.build();
        int numberOfBdLicenseDefinitions = bdLicenseDefinitions.size();
        if (1 == numberOfBdLicenseDefinitions) {
            // Single license.
            bdLicenseDefinition = bdLicenseDefinitions.get(0);
        } else if (numberOfBdLicenseDefinitions > 1) {
            // Multiple licenses so join them by the given clause.
            bdLicenseDefinition = new BdLicenseDefinition(sourceType, bdLicenseDefinitions);
        } // Otherwise, this is an invalid license definition - return emptiness.

        return Optional.ofNullable(bdLicenseDefinition);
    }

    private Optional<BdLicense> findBdLicense(UUID licenseId) {
        // License definition finder does not provide an explicit caching mechanism itself for duplicative finds of the
        // same license id.
        HttpResult<BdLicense> bdLicenseHttpResult = bdLicenseApi.findLicenseV4(licenseId);

        return bdLicenseHttpResult.getHttpResponse().map((httpResponse) -> httpResponse.getMessageBody().orElse(null));
    }
}
