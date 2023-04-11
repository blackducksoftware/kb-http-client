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
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.synopsys.bd.kb.httpclient.api.IBdComponentApi;
import com.synopsys.bd.kb.httpclient.api.MigratableResult;
import com.synopsys.bd.kb.httpclient.model.BdComponentVersion;
import com.synopsys.bd.kb.httpclient.model.BdComponentVersionSummary;
import com.synopsys.kb.httpclient.api.IComponentApi;
import com.synopsys.kb.httpclient.api.PageRequest;
import com.synopsys.kb.httpclient.api.Result;
import com.synopsys.kb.httpclient.model.Component;
import com.synopsys.kb.httpclient.model.ComponentVersion;
import com.synopsys.kb.httpclient.model.ComponentVersionSummary;
import com.synopsys.kb.httpclient.model.Meta;
import com.synopsys.kb.httpclient.model.Page;
import com.synopsys.kb.httpclient.model.VulnerabilityScorePriority;
import com.synopsys.kb.httpclient.model.VulnerabilitySourcePriority;

/**
 * Black Duck-centric component API implementation.
 * 
 * Primarily used to follow component migration paths in a Black Duck-specific manner.
 * 
 * @author skatzman
 */
public class BdComponentApi extends AbstractMigratableBdApi implements IBdComponentApi {
    private final IComponentApi componentApi;

    private final String baseHref;

    public BdComponentApi(IComponentApi componentApi, String baseHref) {
        super();

        Objects.requireNonNull(componentApi, "Component API must be initialized.");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(baseHref), "Base HREF must not be null or empty.");

        this.componentApi = componentApi;
        this.baseHref = baseHref;
    }

    BdComponentApi(IComponentApi componentApi, String baseHref, int maximumAttemptNumber) {
        super(maximumAttemptNumber);

        Objects.requireNonNull(componentApi, "Component API must be initialized.");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(baseHref), "Base HREF must not be null or empty.");

        this.componentApi = componentApi;
        this.baseHref = baseHref;
    }

    @Override
    public MigratableResult<Component> find(UUID componentId) {
        Objects.requireNonNull(componentId, "Component id must be initialized.");

        // Find a component result given a dynamic component id.
        Function<UUID, Result<Component>> resultFunction = (sourceComponentId) -> componentApi.find(sourceComponentId);

        // No conversion is required.
        Function<Component, Component> conversionFunction = Function.identity();

        return findMigratableResult(componentId, resultFunction, conversionFunction, "components");
    }

    @Override
    public MigratableResult<Page<BdComponentVersion>> findComponentVersionsByComponent(final PageRequest pageRequest,
            UUID componentId,
            @Nullable final String searchTermFilter,
            final VulnerabilitySourcePriority vulnerabilitySourcePriority,
            final VulnerabilityScorePriority vulnerabilityScorePriority,
            @Nullable final Boolean excludeDeleted) {
        Objects.requireNonNull(pageRequest, "Page request must be initialized.");
        Objects.requireNonNull(componentId, "Component id must be initialized.");
        Objects.requireNonNull(vulnerabilitySourcePriority, "Vulnerability source priority must be initialized.");
        Objects.requireNonNull(vulnerabilityScorePriority, "Vulnerabilty score priority must be prioritized.");

        // Find a component version page result given a dynamic component id.
        Function<UUID, Result<Page<ComponentVersion>>> resultFunction = (sourceComponentId) -> componentApi.findComponentVersionsByComponent(pageRequest,
                sourceComponentId, searchTermFilter, vulnerabilitySourcePriority, vulnerabilityScorePriority, excludeDeleted);

        // Convert from a component version page to a Black Duck-centric component version page.
        Function<Page<ComponentVersion>, Page<BdComponentVersion>> conversionFunction = (sourceComponentVersionPage) -> {
            int sourceTotalCount = sourceComponentVersionPage.getTotalCount();
            List<BdComponentVersion> destinationItems = sourceComponentVersionPage.getItems().stream()
                    .map((sourceComponentVersion) -> new BdComponentVersion(sourceComponentVersion, baseHref))
                    .collect(Collectors.toList());
            Meta sourceMeta = sourceComponentVersionPage.getMeta();

            return new Page<>(sourceTotalCount, destinationItems, sourceMeta);
        };

        return findMigratableResult(componentId, resultFunction, conversionFunction, "components");
    }

    @Override
    public MigratableResult<Page<BdComponentVersionSummary>> findComponentVersionSummariesByComponent(final PageRequest pageRequest,
            UUID componentId,
            @Nullable final String searchTermFilter,
            @Nullable final Boolean excludeDeleted) {
        Objects.requireNonNull(pageRequest, "Page request must be initialized.");
        Objects.requireNonNull(componentId, "Component id must be initialized.");

        // Find a component version summary page result given a dynamic component id.
        Function<UUID, Result<Page<ComponentVersionSummary>>> resultFunction = (sourceComponentId) -> componentApi.findComponentVersionSummariesByComponent(
                pageRequest, sourceComponentId, searchTermFilter, excludeDeleted);

        // Convert from a component version summary page to a Black Duck-centric component version summary page.
        Function<Page<ComponentVersionSummary>, Page<BdComponentVersionSummary>> conversionFunction = (sourceComponentVersionSummaryPage) -> {
            int sourceTotalCount = sourceComponentVersionSummaryPage.getTotalCount();
            List<BdComponentVersionSummary> destinationItems = sourceComponentVersionSummaryPage.getItems().stream()
                    .map((sourceComponentVersionSummary) -> new BdComponentVersionSummary(sourceComponentVersionSummary))
                    .collect(Collectors.toList());
            Meta sourceMeta = sourceComponentVersionSummaryPage.getMeta();

            return new Page<>(sourceTotalCount, destinationItems, sourceMeta);
        };

        return findMigratableResult(componentId, resultFunction, conversionFunction, "components");
    }
}
