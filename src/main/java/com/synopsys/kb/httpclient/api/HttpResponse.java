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
package com.synopsys.kb.httpclient.api;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;
import com.synopsys.kb.httpclient.model.Meta;

/**
 * HTTP response given an acceptable 2xx, 3xx, 4xx, or 5xx response code.
 * 
 * @author skatzman
 *
 * @param <T>
 *            The value type.
 */
public class HttpResponse<T> {
    private final int code;

    private final Set<Integer> expectedCodes;

    @Nullable
    private final T messageBody;

    @Nullable
    private final Meta migratedMeta;

    /**
     * Constructs a HTTP response.
     * 
     * @param code
     *            The actual response code.
     * @param expectedCodes
     *            The expected response codes given the request.
     * @param messageBody
     *            The message body which typically is provided if the actual response code is an expected response code.
     * @param migratedMeta
     *            The migrated metadata.
     */
    public HttpResponse(int code, Set<Integer> expectedCodes, @Nullable T messageBody, @Nullable Meta migratedMeta) {
        this.code = code;
        this.expectedCodes = (expectedCodes != null) ? ImmutableSet.copyOf(expectedCodes) : ImmutableSet.of();
        this.messageBody = messageBody;
        this.migratedMeta = migratedMeta;
    }

    /**
     * Gets the response code.
     * 
     * @return Returns the response code.
     */
    public int getCode() {
        return code;
    }

    /**
     * Gets the expected response codes.
     * 
     * Expected response codes are response codes in which it is suggested that the message body value be used whether
     * present or absent.
     * 
     * @return Returns the expected response codes.
     */
    public Set<Integer> getExpectedCodes() {
        return expectedCodes;
    }

    /**
     * Determines whether the message body is present or not.
     * 
     * @return Returns true if the message body if present and false otherwise.
     */
    public boolean isMessageBodyPresent() {
        return getMessageBody().isPresent();
    }

    /**
     * Gets the response's message body.
     * 
     * Contextual - Absence of response message body does not always denote lack of valid response.
     * 
     * @return Returns the response message body is present and emptiness otherwise.
     */
    public Optional<T> getMessageBody() {
        return Optional.ofNullable(messageBody);
    }

    /**
     * Gets the response's message body if the actual response code is contained within the set of expected response
     * codes and throws the given runtime exception otherwise.
     * 
     * Convenience method.
     * 
     * @param <X>
     *            The runtime exception type.
     * @param runtimeExceptionSupplier
     *            The runtime exception supplier.
     * @return Returns the response message body if present and emptiness otherwise.
     */
    public <X extends RuntimeException> Optional<T> getMessageBodyOrElseThrow(Supplier<X> runtimeExceptionSupplier) {
        Objects.requireNonNull(runtimeExceptionSupplier, "Runtime exception supplier must be initialized.");

        if (!getExpectedCodes().contains(getCode())) {
            // Response code is not in the collection of expected response codes.
            X runtimeException = runtimeExceptionSupplier.get();
            Objects.requireNonNull(runtimeException, "Runtime exception must be initialized.");

            throw runtimeException;
        }

        return getMessageBody();
    }

    /**
     * Determines whether the migrated meta is present or not.
     * 
     * @return Returns true if the migrated meta is present and false otherwise.
     */
    public boolean isMigratedMetaPresent() {
        return getMigratedMeta().isPresent();
    }

    /**
     * Gets the response's migrated meta.
     * 
     * The migration meta is present for migratable entities given a migration response.
     * 
     * Migration responses include
     * - HTTP 300 Multiple Choices
     * - HTTP 301 Moved Permanently
     * 
     * @return Returns the meta if present and emptiness otherwise.
     */
    public Optional<Meta> getMigratedMeta() {
        return Optional.ofNullable(migratedMeta);
    }

    /**
     * Determines if the given entity is migrated.
     * 
     * @return Returns true if migrated and false otherwise.
     */
    public boolean isMigrated() {
        long numberOfMovedLinks = findNumberOfMovedMigratedLinks();

        return (numberOfMovedLinks > 0L);
    }

    /**
     * Determines if the given entity is migrated by merge mechanism.
     * 
     * @return Returns true if migrated by merge mechanism and false otherwise.
     */
    public boolean isMergeMigrated() {
        long numberOfMovedLinks = findNumberOfMovedMigratedLinks();

        return (1L == numberOfMovedLinks);
    }

    /**
     * Determines if the given entity is migrated by split mechanism.
     * 
     * @return Returns true if migrated by split mechanism and false otherwise.
     */
    public boolean isSplitMigrated() {
        long numberOfMovedLinks = findNumberOfMovedMigratedLinks();

        return (numberOfMovedLinks > 1L);
    }

    private long findNumberOfMovedMigratedLinks() {
        return getMigratedMeta().map((meta) -> meta.findNumberOfLinks(Relationship.MOVED)).orElse(Long.valueOf(0L)).longValue();
    }
}
