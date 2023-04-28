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
import com.synopsys.bd.kb.httpclient.api.MigratableHttpResult;
import com.synopsys.kb.httpclient.api.HttpResponse;
import com.synopsys.kb.httpclient.api.HttpResult;
import com.synopsys.kb.httpclient.api.Relationship;
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
    // The maximum number of times that a chosen migration moved link should be followed before giving up.
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
     * Finds a migratable HTTP result.
     * 
     * @param <S>
     *            The source message body type.
     * @param <T>
     *            The destination message body type.
     * @param id
     *            The id.
     * @param httpResultFunction
     *            The HTTP result function.
     * @param conversionFunction
     *            the conversion function.
     * @param pathVariable
     *            The path variable to find moved migration ids.
     * @return Returns the migratable HTTP result.
     */
    protected <S, T> MigratableHttpResult<T> findMigratableHttpResult(UUID id,
            Function<UUID, HttpResult<S>> httpResultFunction,
            Function<S, T> conversionFunction,
            String pathVariable) {
        List<Meta> migratedMetaHistory = new ArrayList<>();

        return findMigratableHttpResult(id, httpResultFunction, conversionFunction, pathVariable, migratedMetaHistory, 1)
                .orElseThrow(() -> new IllegalStateException("Migratable HTTP result must be present."));
    }

    private <S, T> Optional<MigratableHttpResult<T>> findMigratableHttpResult(UUID id,
            Function<UUID, HttpResult<S>> httpResultFunction,
            Function<S, T> conversionFunction,
            String pathVariable,
            List<Meta> sourceMigratedMetaHistory,
            int attemptNumber) {
        Objects.requireNonNull(id, "Id must be initialized.");
        Objects.requireNonNull(httpResultFunction, "HTTP result function must be initialized.");
        Objects.requireNonNull(conversionFunction, "Conversion function must be initialized.");

        MigratableHttpResult<T> httpResult = null;

        if (attemptNumber <= maximumAttemptNumber) {
            HttpResult<S> sourceHttpResult = httpResultFunction.apply(id);
            HttpResponse<S> sourceHttpResponse = sourceHttpResult.getHttpResponse().orElse(null);
            if (sourceHttpResponse != null) {
                // Source HTTP result contains a HTTP response.
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
                    httpResult = findMigratableHttpResult(destinationMigrationId, httpResultFunction, conversionFunction, pathVariable,
                            updatedMigratedMetaHistory, nextAttemptNumber)
                                    .orElseGet(() -> convertToMigratableHttpResult(sourceHttpResult, conversionFunction, sourceMigratedMetaHistory));
                } else {
                    // Source HTTP response is not migrated or has malformed migrated meta.
                    httpResult = convertToMigratableHttpResult(sourceHttpResult, conversionFunction, sourceMigratedMetaHistory);
                }
            } else {
                // Source HTTP result does NOT contain a HTTP response.
                httpResult = convertToMigratableHttpResult(sourceHttpResult, conversionFunction, sourceMigratedMetaHistory);
            }
        } // No more retry attempts remain, return emptiness.

        return Optional.ofNullable(httpResult);
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

    private <S, T> MigratableHttpResult<T> convertToMigratableHttpResult(HttpResult<S> sourceHttpResult,
            Function<S, T> conversionFunction,
            List<Meta> migratedMetaHistory) {
        Objects.requireNonNull(sourceHttpResult, "Source HTTP result must be initialized.");
        Objects.requireNonNull(conversionFunction, "Conversion function must be initialized.");

        String sourceRequestMethod = sourceHttpResult.getRequestMethod();
        String sourceRequestUri = sourceHttpResult.getRequestUri();
        HttpResponse<S> sourceHttpResponse = sourceHttpResult.getHttpResponse().orElse(null);
        Throwable sourceCause = sourceHttpResult.getCause().orElse(null);

        final MigratableHttpResult<T> destinationHttpResult;

        if (sourceHttpResponse != null) {
            // Source HTTP result contains a HTTP response.
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

            destinationHttpResult = new MigratableHttpResult<>(sourceRequestMethod, sourceRequestUri, destinationMigratableHttpResponse, sourceCause);
        } else {
            // Source HTTP result does NOT contain a HTTP response.
            destinationHttpResult = new MigratableHttpResult<>(sourceRequestMethod, sourceRequestUri, null, sourceCause);
        }

        return destinationHttpResult;
    }
}
