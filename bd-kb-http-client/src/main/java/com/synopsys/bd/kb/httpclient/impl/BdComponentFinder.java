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
import java.util.Optional;
import java.util.UUID;

import com.google.common.base.Preconditions;
import com.synopsys.bd.kb.httpclient.api.IBdComponentApi;
import com.synopsys.bd.kb.httpclient.api.IBdComponentVariantApi;
import com.synopsys.bd.kb.httpclient.api.IBdComponentVersionApi;
import com.synopsys.bd.kb.httpclient.api.MigratableHttpResult;
import com.synopsys.bd.kb.httpclient.model.BdComponentVariant;
import com.synopsys.bd.kb.httpclient.model.BdComponentVariantHierarchy;
import com.synopsys.bd.kb.httpclient.model.BdComponentVersion;
import com.synopsys.bd.kb.httpclient.model.BdComponentVersionHierarchy;
import com.synopsys.kb.httpclient.api.HttpResult;
import com.synopsys.kb.httpclient.model.Component;
import com.synopsys.kb.httpclient.model.VulnerabilityScorePriority;
import com.synopsys.kb.httpclient.model.VulnerabilitySourcePriority;

/**
 * Black Duck-centric component finder.
 * 
 * Finds hierarchies of component version and component variant entities.
 * 
 * @author skatzman
 */
public class BdComponentFinder {
    // The maximum number of times the finder should restart retrieval given a migration discrepancy between related
    // entities.
    private static final int MAXIMUM_ATTEMPT_NUMBER = 3;

    private final IBdComponentApi bdComponentApi;

    private final IBdComponentVersionApi bdComponentVersionApi;

    private final IBdComponentVariantApi bdComponentVariantApi;

    private final int maximumAttemptNumber;

    public BdComponentFinder(IBdComponentApi bdComponentApi,
            IBdComponentVersionApi bdComponentVersionApi,
            IBdComponentVariantApi bdComponentVariantApi) {
        this(bdComponentApi, bdComponentVersionApi, bdComponentVariantApi, MAXIMUM_ATTEMPT_NUMBER);
    }

    BdComponentFinder(IBdComponentApi bdComponentApi,
            IBdComponentVersionApi bdComponentVersionApi,
            IBdComponentVariantApi bdComponentVariantApi,
            int maximumAttemptNumber) {
        this.bdComponentApi = Objects.requireNonNull(bdComponentApi, "Black Duck-centric component API must be initialized.");
        this.bdComponentVersionApi = Objects.requireNonNull(bdComponentVersionApi, "Black Duck-centric component version API must be initialized.");
        this.bdComponentVariantApi = Objects.requireNonNull(bdComponentVariantApi, "Black Duck-centric component variant API must be initialized.");

        Preconditions.checkArgument(maximumAttemptNumber >= 1, "Maximum attempt number must be greater than or equal to 0.");
        this.maximumAttemptNumber = maximumAttemptNumber;
    }

    /**
     * Finds a component version hierarchy given a component version id.
     * 
     * Finds a component version and its parent component.
     * 
     * - This API will attempt to follow migration links when present to return the final destination component version
     * and component. In the case of a split migration, the first split moved link will be followed.
     * - As a defensive measure, this API will attempt to follow up to maximum ceiling of requests for migration
     * handling.
     * 
     * @param componentVersionId
     *            The component version id.
     * @param vulnerabilitySourcePriority
     *            The vulnerability source priority.
     * @param vulnerabilityScorePriority
     *            The vulnerability score priority.
     * @return Returns the component version hierarchy if present and emptiness otherwise.
     */
    public Optional<BdComponentVersionHierarchy> findComponentVersionHierarchy(UUID componentVersionId,
            VulnerabilitySourcePriority vulnerabilitySourcePriority,
            VulnerabilityScorePriority vulnerabilityScorePriority) {
        Objects.requireNonNull(componentVersionId, "Component version id must be initialized.");
        Objects.requireNonNull(vulnerabilitySourcePriority, "Vulnerability source priority must be initialized.");
        Objects.requireNonNull(vulnerabilityScorePriority, "Vulnerabilty score priority must be prioritized.");

        return findComponentVersionHierarchy(componentVersionId, vulnerabilitySourcePriority, vulnerabilityScorePriority, 1);
    }

    /**
     * Finds a component variant hierarchy given a component variant id.
     * 
     * Finds a component variant, its parent component version, and its grandparent component.
     * 
     * - This API will attempt to follow migration links when present to return the final destination component variant,
     * component version and component. In the case of a split migration, the first split moved link will be followed.
     * - As a defensive measure, this API will attempt to follow up to maximum ceiling of requests for migration
     * handling.
     * 
     * @param componentVariantId
     *            The component variant id.
     * @param vulnerabilitySourcePriority
     *            The vulnerability source priority.
     * @param vulnerabilityScorePriority
     *            The vulnerability score priority.
     * @return Returns the component version hierarchy if present and emptiness otherwise.
     */
    public Optional<BdComponentVariantHierarchy> findComponentVariantHierarchy(UUID componentVariantId,
            VulnerabilitySourcePriority vulnerabilitySourcePriority,
            VulnerabilityScorePriority vulnerabilityScorePriority) {
        Objects.requireNonNull(componentVariantId, "Component variant id must be initialized.");
        Objects.requireNonNull(vulnerabilitySourcePriority, "Vulnerability source priority must be initialized.");
        Objects.requireNonNull(vulnerabilityScorePriority, "Vulnerabilty score priority must be prioritized.");

        return findComponentVariantHierarchy(componentVariantId, vulnerabilitySourcePriority, vulnerabilityScorePriority, 1);
    }

    private Optional<BdComponentVersionHierarchy> findComponentVersionHierarchy(UUID componentVersionId,
            VulnerabilitySourcePriority vulnerabilitySourcePriority,
            VulnerabilityScorePriority vulnerabilityScorePriority,
            int attemptNumber) {
        if (attemptNumber > maximumAttemptNumber) {
            // Absent. Could not retrieve data within the maximum allowed number of attempts.
            return Optional.empty();
        }

        // Find component version
        MigratableHttpResult<BdComponentVersion> componentVersionHttpResult = bdComponentVersionApi.findComponentVersionV4(componentVersionId,
                vulnerabilitySourcePriority, vulnerabilityScorePriority);
        UUID sourceComponentId = componentVersionHttpResult.getMigratableHttpResponse()
                .map((migratableHttpResponse) -> migratableHttpResponse.getMessageBody().orElse(null))
                .map(BdComponentVersion::getComponentId)
                .orElse(null);
        if (null == sourceComponentId) {
            // Absent. Could not successfully retrieve component version. Return emptiness.
            return Optional.empty();
        } // Component version presence. Continue to find parent component result.

        // Find parent component
        MigratableHttpResult<Component> componentHttpResult = bdComponentApi.findComponentV4(sourceComponentId);
        UUID destinationComponentId = componentHttpResult.getMigratableHttpResponse()
                .map((migratableHttpResponse) -> migratableHttpResponse.getMessageBody().orElse(null))
                .map(Component::getId).orElse(null);
        if (null == destinationComponentId) {
            // Absent. Could not successfully retrieve component. Return emptiness.
            return Optional.empty();
        } // Component presence.

        if (!sourceComponentId.equals(destinationComponentId)) {
            // Present but the retrieved component has a different id which means it was migrated. Need to retry full
            // retrieval from scratch to retrieve consistent hierarchy representation.
            int nextAttemptNumber = attemptNumber + 1;

            return findComponentVersionHierarchy(componentVersionId, vulnerabilitySourcePriority, vulnerabilityScorePriority, nextAttemptNumber);
        }

        // Otherwise, hierarchy is not migrated and consistent from component version to component so return it.
        BdComponentVersionHierarchy hierarchy = new BdComponentVersionHierarchy(componentHttpResult, componentVersionHttpResult);

        return Optional.of(hierarchy);
    }

    private Optional<BdComponentVariantHierarchy> findComponentVariantHierarchy(UUID componentVariantId,
            VulnerabilitySourcePriority vulnerabilitySourcePriority,
            VulnerabilityScorePriority vulnerabilityScorePriority,
            int attemptNumber) {
        if (attemptNumber > maximumAttemptNumber) {
            // Absent. Could not retrieve data within the maximum allowed number of attempts.
            return Optional.empty();
        }

        // Find component variant
        HttpResult<BdComponentVariant> componentVariantHttpResult = bdComponentVariantApi.findComponentVariantV4(componentVariantId);
        UUID sourceComponentVersionId = componentVariantHttpResult.getHttpResponse()
                .map((httpResponse) -> httpResponse.getMessageBody().orElse(null))
                .map(BdComponentVariant::getComponentVersionId).orElse(null);
        if (null == sourceComponentVersionId) {
            // Absent. Could not successfully retrieve component variant. Return emptiness.
            return Optional.empty();
        } // Component variant presence. Continue to find parent component version result.

        // Find parent component version
        MigratableHttpResult<BdComponentVersion> componentVersionHttpResult = bdComponentVersionApi.findComponentVersionV4(sourceComponentVersionId,
                vulnerabilitySourcePriority, vulnerabilityScorePriority);
        BdComponentVersion componentVersion = componentVersionHttpResult.getMigratableHttpResponse()
                .map((migratableHttpResponse) -> migratableHttpResponse.getMessageBody().orElse(null)).orElse(null);
        if (null == componentVersion) {
            // Absent. Could not successfully retrieve component version. Return emptiness.
            return Optional.empty();
        } // Component version presence.
        UUID sourceComponentId = componentVersion.getComponentId();
        UUID destinationComponentVersionId = componentVersion.getId();
        if (!sourceComponentVersionId.equals(destinationComponentVersionId)) {
            // Present but the retrieved component version has a different id which means it was migrated. Need to retry
            // full retrieval from scratch to retrieve consistent hierarchy representation.
            int nextAttemptNumber = attemptNumber + 1;

            return findComponentVariantHierarchy(componentVariantId, vulnerabilitySourcePriority, vulnerabilityScorePriority, nextAttemptNumber);
        }

        // Find grandparent component
        MigratableHttpResult<Component> componentHttpResult = bdComponentApi.findComponentV4(sourceComponentId);
        UUID destinationComponentId = componentHttpResult.getMigratableHttpResponse()
                .map((migratableHttpResponse) -> migratableHttpResponse.getMessageBody().orElse(null))
                .map(Component::getId).orElse(null);
        if (null == destinationComponentId) {
            // Absent. Could not successfully retrieve component. Return emptiness.
            return Optional.empty();
        } // Component presence.

        if (!sourceComponentId.equals(destinationComponentId)) {
            // Present but the retrieved component has a different id which means it was migrated. Need to retry
            // full retrieval from scratch to retrieve consistent hierarchy representation.
            int nextAttemptNumber = attemptNumber + 1;

            return findComponentVariantHierarchy(componentVariantId, vulnerabilitySourcePriority, vulnerabilityScorePriority, nextAttemptNumber);
        }

        // Otherwise, hierarchy is not migrated and consistent from component variant to component version to component
        // so return it.
        BdComponentVariantHierarchy hierarchy = new BdComponentVariantHierarchy(componentHttpResult, componentVersionHttpResult, componentVariantHttpResult);

        return Optional.of(hierarchy);
    }
}
