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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.synopsys.bd.kb.httpclient.api.MigratableHttpResponse;
import com.synopsys.bd.kb.httpclient.api.MigratableResult;
import com.synopsys.kb.httpclient.api.HttpResponse;
import com.synopsys.kb.httpclient.api.Relationship;
import com.synopsys.kb.httpclient.api.Result;
import com.synopsys.kb.httpclient.model.Link;
import com.synopsys.kb.httpclient.model.Meta;

/**
 * Abstract migratable Black Duck API.
 * 
 * Package protected.
 * 
 * @author skatzman
 */
abstract class AbstractMigratableBdApi extends AbstractBdApi {
    private static final int MAXIMUM_ATTEMPT_NUMBER = 5;

    private final int maximumAttemptNumber;

    /**
     * Constructs an abstract migratable Black Duck-centric API.
     * 
     * Utilizes the default maximum attempt number value.
     */
    protected AbstractMigratableBdApi() {
        this(MAXIMUM_ATTEMPT_NUMBER);
    }

    /**
     * Constructs an abstract migratable Black Duck-centric API.
     * 
     * Enables configuration of the maximum attempt number value which is useful for testing.
     * 
     * @param maximumAttemptNumber
     *            The maximum attempt number.
     */
    protected AbstractMigratableBdApi(int maximumAttemptNumber) {
        super();

        Preconditions.checkArgument(maximumAttemptNumber >= 1, "Maximum attempt number must be greater than or equal to 0.");

        this.maximumAttemptNumber = maximumAttemptNumber;
    }

    /**
     * Finds a migratable result.
     * 
     * @param <S>
     *            The source message body type.
     * @param <T>
     *            The destination message body type.
     * @param id
     *            The id.
     * @param resultFunction
     *            The result function.
     * @param conversionFunction
     *            the conversion function.
     * @param pathVariable
     *            The path variable to find moved migration ids.
     * @return Returns the migratable result.
     */
    protected <S, T> MigratableResult<T> findMigratableResult(UUID id,
            Function<UUID, Result<S>> resultFunction,
            Function<S, T> conversionFunction,
            String pathVariable) {
        List<Meta> migratedMetaHistory = new ArrayList<>();

        return findMigratableResult(id, resultFunction, conversionFunction, pathVariable, migratedMetaHistory, 1)
                .orElseThrow(() -> new IllegalStateException("Migratable result must be present."));
    }

    private <S, T> Optional<MigratableResult<T>> findMigratableResult(UUID id,
            Function<UUID, Result<S>> resultFunction,
            Function<S, T> conversionFunction,
            String pathVariable,
            List<Meta> sourceMigratedMetaHistory,
            int attemptNumber) {
        Objects.requireNonNull(id, "Id must be initialized.");
        Objects.requireNonNull(resultFunction, "Result function must be initialized.");
        Objects.requireNonNull(conversionFunction, "Conversion function must be initialized.");

        MigratableResult<T> migratableResult = null;

        if (attemptNumber <= maximumAttemptNumber) {
            Result<S> sourceResult = resultFunction.apply(id);
            HttpResponse<S> sourceHttpResponse = sourceResult.getHttpResponse().orElse(null);
            if (sourceHttpResponse != null) {
                // Source result contains a HTTP response.
                Optional<Meta> optionalSourceMigratedMeta = sourceHttpResponse.getMigratedMeta();
                UUID destinationMigrationId = optionalSourceMigratedMeta
                        .map((migratedMeta) -> migratedMeta.findLinks(Relationship.MOVED))
                        .map((movedLinks) -> findFirstDestinationId(movedLinks, pathVariable))
                        .orElse(null);
                if (destinationMigrationId != null) {
                    // Source HTTP response contains a destination migration id.

                    // Append to the migrated meta history chain.
                    Meta sourceMigratedMeta = optionalSourceMigratedMeta.orElse(null);
                    List<Meta> updatedMigratedMetaHistory = ImmutableList.<Meta> builder()
                            .addAll(sourceMigratedMetaHistory)
                            .add(sourceMigratedMeta).build();

                    // Follow the migration path and try again.
                    int nextAttemptNumber = attemptNumber + 1;
                    migratableResult = findMigratableResult(destinationMigrationId, resultFunction, conversionFunction, pathVariable,
                            updatedMigratedMetaHistory, nextAttemptNumber)
                                    .orElseGet(() -> convertToMigratableResult(sourceResult, conversionFunction, sourceMigratedMetaHistory));
                } else {
                    // Source HTTP response is not migrated or has malformed migrated meta.
                    migratableResult = convertToMigratableResult(sourceResult, conversionFunction, sourceMigratedMetaHistory);
                }
            } else {
                // Source result does NOT contain a HTTP response.
                migratableResult = convertToMigratableResult(sourceResult, conversionFunction, sourceMigratedMetaHistory);
            }
        } // No more retry attempts remain, return emptiness.

        return Optional.ofNullable(migratableResult);
    }

    @Nullable
    private UUID findFirstDestinationId(List<Link> links, String pathVariable) {
        UUID destinationId = null;

        if (!links.isEmpty()) {
            // The first link in the list is used for both merge migrations and split migrations.
            Link movedLink = links.get(0);
            destinationId = movedLink.getHrefId(pathVariable).map(UUID::fromString).orElse(null);
        }

        return destinationId;
    }

    private <S, T> MigratableResult<T> convertToMigratableResult(Result<S> sourceResult,
            Function<S, T> conversionFunction,
            List<Meta> migratedMetaHistory) {
        Objects.requireNonNull(sourceResult, "Source result must be initialized.");
        Objects.requireNonNull(conversionFunction, "Conversion function must be initialized.");

        String sourceRequestMethod = sourceResult.getRequestMethod();
        String sourceRequestUri = sourceResult.getRequestUri();
        HttpResponse<S> sourceHttpResponse = sourceResult.getHttpResponse().orElse(null);
        Throwable sourceCause = sourceResult.getCause().orElse(null);

        final MigratableResult<T> destinationMigratableResult;
        if (sourceHttpResponse != null) {
            // Source result contains a HTTP response.
            int sourceCode = sourceHttpResponse.getCode();
            Set<Integer> sourceExpectedCodes = sourceHttpResponse.getExpectedCodes();
            S sourceMessageBody = sourceHttpResponse.getMessageBody().orElse(null);
            Meta sourceMigratedMeta = sourceHttpResponse.getMigratedMeta().orElse(null);

            final MigratableHttpResponse<T> destinationMigratableHttpResponse;
            if (sourceMessageBody != null) {
                // Source HTTP response contains a message body.
                T destinationMessageBody = conversionFunction.apply(sourceMessageBody);
                destinationMigratableHttpResponse = new MigratableHttpResponse<>(sourceCode, sourceExpectedCodes, destinationMessageBody, sourceMigratedMeta,
                        migratedMetaHistory);
            } else {
                // Source HTTP response does NOT contain a message body.
                destinationMigratableHttpResponse = new MigratableHttpResponse<>(sourceCode, sourceExpectedCodes, null, sourceMigratedMeta,
                        migratedMetaHistory);
            }

            destinationMigratableResult = new MigratableResult<>(sourceRequestMethod, sourceRequestUri, destinationMigratableHttpResponse, sourceCause);
        } else {
            // Source result does NOT contain a HTTP response.
            destinationMigratableResult = new MigratableResult<>(sourceRequestMethod, sourceRequestUri, null, sourceCause);
        }

        return destinationMigratableResult;
    }
}
