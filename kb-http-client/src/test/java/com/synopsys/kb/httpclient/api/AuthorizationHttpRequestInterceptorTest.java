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

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.io.entity.NullEntity;
import org.apache.hc.core5.http.message.BasicHttpRequest;
import org.apache.hc.core5.http.protocol.BasicHttpContext;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.synopsys.kb.httpclient.model.Authentication;

/**
 * Authorization HTTP request interceptor test.
 * 
 * @author skatzman
 */
public class AuthorizationHttpRequestInterceptorTest {
    @Mock
    private AuthorizationProvider authorizationProvider;

    private AuthorizationHttpRequestInterceptor interceptor;

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.openMocks(this);

        interceptor = new AuthorizationHttpRequestInterceptor(authorizationProvider);
    }

    @Test(expectedExceptions = { AuthenticationException.class })
    public void testProcessWithNullAuthentication() throws HttpException, IOException {
        HttpRequest request = new BasicHttpRequest(Method.GET, "/api/vegetables");
        EntityDetails entity = NullEntity.INSTANCE;
        HttpContext context = new BasicHttpContext();

        Mockito.when(authorizationProvider.getOrAuthenticate()).thenReturn(Optional.empty());

        interceptor.process(request, entity, context);
    }

    @Test(expectedExceptions = { AuthenticationException.class })
    public void testProcessWithNullJsonWebToken() throws HttpException, IOException {
        HttpRequest request = new BasicHttpRequest(Method.GET, "/api/vegetables");
        EntityDetails entity = NullEntity.INSTANCE;
        HttpContext context = new BasicHttpContext();
        Authentication authentication = new Authentication(false, null, TimeUnit.MINUTES.toMillis(10L));

        Mockito.when(authorizationProvider.getOrAuthenticate()).thenReturn(Optional.of(authentication));

        interceptor.process(request, entity, context);
    }

    @Test(expectedExceptions = { AuthenticationException.class })
    public void testProcessWithEmptyJsonWebToken() throws HttpException, IOException {
        HttpRequest request = new BasicHttpRequest(Method.GET, "/api/vegetables");
        EntityDetails entity = NullEntity.INSTANCE;
        HttpContext context = new BasicHttpContext();
        Authentication authentication = new Authentication(false, "", TimeUnit.MINUTES.toMillis(10L));

        Mockito.when(authorizationProvider.getOrAuthenticate()).thenReturn(Optional.of(authentication));

        interceptor.process(request, entity, context);
    }

    @Test
    public void testProcess() throws HttpException, IOException {
        HttpRequest request = new BasicHttpRequest(Method.GET, "/api/vegetables");
        EntityDetails entity = NullEntity.INSTANCE;
        HttpContext context = new BasicHttpContext();
        String jsonWebToken = "67890";
        Authentication authentication = new Authentication(false, jsonWebToken, TimeUnit.MINUTES.toMillis(10L));

        Mockito.when(authorizationProvider.getOrAuthenticate()).thenReturn(Optional.of(authentication));

        interceptor.process(request, entity, context);

        Header authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        Assert.assertNotNull(authorizationHeader, "Authorization header should be initialized.");
        Assert.assertEquals(authorizationHeader.getValue(), "Bearer " + jsonWebToken, "Values should be equal.");
    }
}
