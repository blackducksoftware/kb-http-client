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

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.Method;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.synopsys.kb.httpclient.AbstractTest;
import com.synopsys.kb.httpclient.model.Authentication;

/**
 * JSON web token provider test.
 * 
 * @author skatzman
 */
public class AuthorizationProviderTest extends AbstractTest {
    private static final String LICENSE_KEY = "12345";

    @Mock
    private Supplier<String> licenseKeySupplier;

    @Mock
    private IAuthenticationApi authenticationApi;

    private AuthorizationProvider provider;

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.openMocks(this);

        provider = new AuthorizationProvider(licenseKeySupplier, authenticationApi);
    }

    @Test
    public void testGetOrAuthenticateWhenPresent() {
        Authentication authentication = new Authentication(false, "67890", TimeUnit.MINUTES.toMillis(1L));
        AtomicReference<Authentication> authenticationAtomicReference = new AtomicReference<>(authentication);
        AuthorizationProvider provider = new AuthorizationProvider(authenticationAtomicReference, licenseKeySupplier, authenticationApi);

        Optional<Authentication> result = provider.getOrAuthenticate();

        Assert.assertTrue(result.isPresent(), "Result should be present.");
        Assert.assertEquals(result.orElse(null), authentication, "Authentications should be equal.");
    }

    @Test
    public void testGetOrAuthenticateWhenAbsentAndInvalidAuthentication() {
        Set<Integer> expectedCodes = Set.of(HttpStatus.SC_OK, HttpStatus.SC_NOT_FOUND);
        HttpResponse<Authentication> httpResponse = new HttpResponse<>(HttpStatus.SC_UNAUTHORIZED, expectedCodes, null, null);
        HttpResult<Authentication> authenticationHttpResult = new HttpResult<>(Method.POST.name(), "/api/authenticate", httpResponse);

        Mockito.when(licenseKeySupplier.get()).thenReturn(LICENSE_KEY);
        Mockito.when(authenticationApi.authenticateV1(LICENSE_KEY)).thenReturn(authenticationHttpResult);

        Optional<Authentication> result = provider.getOrAuthenticate();

        Assert.assertFalse(result.isPresent(), "Result should not be present.");
    }

    @Test
    public void testGetOrAuthenticateWhenAbsentAndValidAuthentication() {
        Set<Integer> expectedCodes = Set.of(HttpStatus.SC_OK, HttpStatus.SC_NOT_FOUND);
        Authentication authentication = new Authentication(false, "67890", TimeUnit.MINUTES.toMillis(1L));
        HttpResponse<Authentication> httpResponse = new HttpResponse<>(HttpStatus.SC_OK, expectedCodes, authentication, null);
        HttpResult<Authentication> authenticationHttpResult = new HttpResult<>(Method.POST.name(), "/api/authenticate", httpResponse);

        Mockito.when(licenseKeySupplier.get()).thenReturn(LICENSE_KEY);
        Mockito.when(authenticationApi.authenticateV1(LICENSE_KEY)).thenReturn(authenticationHttpResult);

        Optional<Authentication> result = provider.getOrAuthenticate();

        Assert.assertTrue(result.isPresent(), "Result should be present.");
        Assert.assertEquals(result.orElse(null), authentication, "Authentications should be equal.");
    }
}
