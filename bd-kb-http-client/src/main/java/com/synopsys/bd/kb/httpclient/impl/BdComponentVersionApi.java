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

import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.synopsys.bd.kb.httpclient.api.IBdComponentVersionApi;
import com.synopsys.bd.kb.httpclient.api.MigratableHttpResult;
import com.synopsys.bd.kb.httpclient.model.BdComponentVersion;
import com.synopsys.kb.httpclient.api.HttpResult;
import com.synopsys.kb.httpclient.api.IComponentVersionApi;
import com.synopsys.kb.httpclient.api.PageRequest;
import com.synopsys.kb.httpclient.model.BdsaVulnerability;
import com.synopsys.kb.httpclient.model.ComponentVersion;
import com.synopsys.kb.httpclient.model.CveVulnerability;
import com.synopsys.kb.httpclient.model.NextVersion;
import com.synopsys.kb.httpclient.model.Page;
import com.synopsys.kb.httpclient.model.UpgradeGuidance;
import com.synopsys.kb.httpclient.model.VulnerabilityScorePriority;
import com.synopsys.kb.httpclient.model.VulnerabilitySourcePriority;

/**
 * Black Duck-centric component version API implementation.
 * 
 * Primarily used to follow component version migration paths in a Black Duck-specific manner.
 * 
 * @author skatzman
 */
public class BdComponentVersionApi extends AbstractMigratableBdApi implements IBdComponentVersionApi {
    private final IComponentVersionApi componentVersionApi;

    private final String baseHref;

    public BdComponentVersionApi(IComponentVersionApi componentVersionApi, String baseHref) {
        super();

        Objects.requireNonNull(componentVersionApi, "Component version API must be initialized.");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(baseHref), "Base HREF must not be null or empty.");

        this.componentVersionApi = componentVersionApi;
        this.baseHref = baseHref;
    }

    BdComponentVersionApi(IComponentVersionApi componentVersionApi, String baseHref, int maximumAttemptNumber) {
        super(maximumAttemptNumber);

        Objects.requireNonNull(componentVersionApi, "Component version API must be initialized.");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(baseHref), "Base HREF must not be null or empty.");

        this.componentVersionApi = componentVersionApi;
        this.baseHref = baseHref;
    }

    @Override
    public MigratableHttpResult<BdComponentVersion> findComponentVersionV4(UUID componentVersionId,
            final VulnerabilitySourcePriority vulnerabilitySourcePriority,
            final VulnerabilityScorePriority vulnerabilityScorePriority) {
        Objects.requireNonNull(componentVersionId, "Component version id must be initialized.");
        Objects.requireNonNull(vulnerabilitySourcePriority, "Vulnerability source priority must be initialized.");
        Objects.requireNonNull(vulnerabilityScorePriority, "Vulnerabilty score priority must be prioritized.");

        // Find a component version result given a dynamic component version id.
        // Source priority and score priority should remain consistent across multiple requests.
        Function<UUID, HttpResult<ComponentVersion>> resultFunction = (sourceComponentVersionId) -> componentVersionApi
                .findComponentVersionV4(sourceComponentVersionId, vulnerabilitySourcePriority, vulnerabilityScorePriority);

        // Convert from a component version to a Black Duck-centric component version.
        Function<ComponentVersion, BdComponentVersion> conversionFunction = (componentVersion) -> {
            return new BdComponentVersion(componentVersion, baseHref);
        };

        return findMigratableHttpResult(componentVersionId, resultFunction, conversionFunction, "versions");
    }

    @Override
    public MigratableHttpResult<NextVersion> findNextVersionV4(UUID componentVersionId) {
        Objects.requireNonNull(componentVersionId, "Component version id must be initialized.");

        // Find a next version result given a dynamic component version id.
        // Source priority and score priority should remain consistent across multiple requests.
        Function<UUID, HttpResult<NextVersion>> resultFunction = (sourceComponentVersionId) -> componentVersionApi.findNextVersionV4(sourceComponentVersionId);

        // No conversion is required.
        Function<NextVersion, NextVersion> conversionFunction = Function.identity();

        return findMigratableHttpResult(componentVersionId, resultFunction, conversionFunction, "versions");
    }

    @Override
    public MigratableHttpResult<Page<CveVulnerability>> findCveVulnerabilitiesV7(final PageRequest pageRequest,
            UUID componentVersionId,
            @Nullable final String searchTermFilter) {
        Objects.requireNonNull(pageRequest, "Page request must be initialized.");
        Objects.requireNonNull(componentVersionId, "Component version id must be initialized.");

        // Find a CVE vulnerability page result given a dynamic component version id.
        // Page request should remain consistent across multiple requests.
        Function<UUID, HttpResult<Page<CveVulnerability>>> resultFunction = (sourceComponentVersionId) -> componentVersionApi
                .findCveVulnerabilitiesV7(pageRequest, sourceComponentVersionId, searchTermFilter);

        // No conversion is required.
        Function<Page<CveVulnerability>, Page<CveVulnerability>> conversionFunction = Function.identity();

        return findMigratableHttpResult(componentVersionId, resultFunction, conversionFunction, "versions");
    }

    @Override
    public MigratableHttpResult<Page<BdsaVulnerability>> findBdsaVulnerabilitiesV7(final PageRequest pageRequest,
            UUID componentVersionId,
            @Nullable final String searchTermFilter) {
        Objects.requireNonNull(pageRequest, "Page request must be initialized.");
        Objects.requireNonNull(componentVersionId, "Component version id must be initialized.");

        // Find a BDSA vulnerability page result given a dynamic component version id.
        // Page request should remain consistent across multiple requests.
        Function<UUID, HttpResult<Page<BdsaVulnerability>>> resultFunction = (sourceComponentVersionId) -> componentVersionApi
                .findBdsaVulnerabilitiesV7(pageRequest, sourceComponentVersionId, searchTermFilter);

        // No conversion is required.
        Function<Page<BdsaVulnerability>, Page<BdsaVulnerability>> conversionFunction = Function.identity();

        return findMigratableHttpResult(componentVersionId, resultFunction, conversionFunction, "versions");
    }

    @Override
    public MigratableHttpResult<UpgradeGuidance> findUpgradeGuidanceV4(UUID componentVersionId,
            final VulnerabilitySourcePriority vulnerabilitySourcePriority,
            final VulnerabilityScorePriority vulnerabilityScorePriority) {
        Objects.requireNonNull(componentVersionId, "Component version id must be initialized.");
        Objects.requireNonNull(vulnerabilitySourcePriority, "Vulnerability source priority must be initialized.");
        Objects.requireNonNull(vulnerabilityScorePriority, "Vulnerabilty score priority must be prioritized.");

        // Find a component version result given a dynamic component version id.
        // Source priority and score priority should remain consistent across multiple requests.
        Function<UUID, HttpResult<UpgradeGuidance>> resultFunction = (sourceComponentVersionId) -> componentVersionApi
                .findUpgradeGuidanceV4(sourceComponentVersionId, vulnerabilitySourcePriority, vulnerabilityScorePriority);

        // No conversion is required.
        Function<UpgradeGuidance, UpgradeGuidance> conversionFunction = Function.identity();

        return findMigratableHttpResult(componentVersionId, resultFunction, conversionFunction, "versions");
    }
}
