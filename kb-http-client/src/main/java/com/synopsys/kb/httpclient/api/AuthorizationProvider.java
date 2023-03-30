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
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.synopsys.kb.httpclient.model.Authentication;

/**
 * Authorization provider.
 * 
 * Package protected.
 * 
 * @author skatzman
 */
public class AuthorizationProvider {
    private final AtomicReference<Authentication> authenticationAtomicReference;

    private final Object lock = new Object();

    private final Supplier<String> licenseKeySupplier;

    private final IAuthenticationApi authenticationApi;

    /**
     * Constructs the authorization provider.
     * 
     * Package protected.
     * 
     * @param licenseKeySupplier
     *            The license key supplier.
     * @param authenticationApi
     *            The authentication API.
     */
    AuthorizationProvider(Supplier<String> licenseKeySupplier, IAuthenticationApi authenticationApi) {
        this(new AtomicReference<>(), licenseKeySupplier, authenticationApi);
    }

    /**
     * Constructs the authorization provider.
     * 
     * Package protected.
     * 
     * @param authenticationAtomicReference
     *            The authentication atomic reference.
     * @param licenseKeySupplier
     *            The license key supplier.
     * @param authenticationApi
     *            The authentication API.
     */
    AuthorizationProvider(AtomicReference<Authentication> authenticationAtomicReference,
            Supplier<String> licenseKeySupplier,
            IAuthenticationApi authenticationApi) {
        this.authenticationAtomicReference = Objects.requireNonNull(authenticationAtomicReference, "Authentication atomic reference must be initialized.");
        this.licenseKeySupplier = Objects.requireNonNull(licenseKeySupplier, "License key supplier must be initialized.");
        this.authenticationApi = Objects.requireNonNull(authenticationApi, "Authentication API must be initialized.");
    }

    /**
     * Gets the current authentication (includes authorization metadata) or authenticates if it is not present.
     * 
     * @return Returns the authentication if present and emptiness otherwise.
     */
    public Optional<Authentication> getOrAuthenticate() {
        Authentication authentication = authenticationAtomicReference.get();
        if (null == authentication) {
            synchronized (lock) {
                // Thread may have waited for another thread when attempting to enter the synchronized block.
                authentication = authenticationAtomicReference.get();

                // Subsequent threads shouldn't regenerate authentication if already managed.
                if (authentication != null) {
                    return Optional.of(authentication);
                }

                // First thread in synchronized block needs to authenticate.
                String licenseKey = licenseKeySupplier.get();
                Preconditions.checkArgument(!Strings.isNullOrEmpty(licenseKey), "License key must not be null or empty.");
                authentication = authenticate(licenseKey);
                authenticationAtomicReference.compareAndSet(null, authentication);
            }
        } // Otherwise, authentication is present so use it.

        return Optional.ofNullable(authentication);
    }

    /**
     * Clears the current authentication (includes authorization metadata) and authenticates.
     * 
     * @return Returns the authentication if present and emptiness otherwise.
     */
    public Optional<Authentication> clearAndAuthenticate() {
        Authentication authentication = null;

        // Clear the existing authentication atomic reference no matter its current state.
        authenticationAtomicReference.set(null);
        synchronized (lock) {
            // Thread may have waited for another thread when attempting to enter the synchronized block.
            authentication = authenticationAtomicReference.get();

            // Subsequent threads shouldn't regenerate authentication if already managed.
            if (authentication != null) {
                return Optional.of(authentication);
            }

            // First thread in synchronized block needs to authenticate.
            String licenseKey = licenseKeySupplier.get();
            Preconditions.checkArgument(!Strings.isNullOrEmpty(licenseKey), "License key must not be null or empty.");
            authentication = authenticate(licenseKey);
            authenticationAtomicReference.compareAndSet(null, authentication);
        }

        return Optional.ofNullable(authentication);
    }

    @Nullable
    private Authentication authenticate(String licenseKey) {
        Authentication authentication = null;

        Result<Authentication> authenticationResult = authenticationApi.authenticate(licenseKey);
        if (authenticationResult.isHttpResponsePresent()) {
            HttpResponse<Authentication> authenticationHttpResponse = authenticationResult.getHttpResponse().orElse(null);
            if (authenticationHttpResponse.isMessageBodyPresent()) {
                authentication = authenticationHttpResponse.getMessageBody().orElse(null);
            }
        }

        return authentication;
    }
}
