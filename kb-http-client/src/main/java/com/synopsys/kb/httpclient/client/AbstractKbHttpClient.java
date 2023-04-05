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
package com.synopsys.kb.httpclient.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.net.URIBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.synopsys.kb.httpclient.api.AuthorizationProvider;
import com.synopsys.kb.httpclient.api.HttpResponse;
import com.synopsys.kb.httpclient.api.KbConfiguration;
import com.synopsys.kb.httpclient.api.PageRequest;
import com.synopsys.kb.httpclient.api.Result;
import com.synopsys.kb.httpclient.model.Meta;
import com.synopsys.kb.httpclient.model.MetaWrapper;
import com.synopsys.kb.httpclient.util.ClassTypeReference;

/**
 * Abstract KnowledgeBase HTTP client.
 * 
 * Base HTTP client that many HTTP clients can extend from to solicit common functionality.
 * 
 * @author skatzman
 */
public abstract class AbstractKbHttpClient {
    protected static final Set<Integer> DEFAULT_SUCCESS_CODES = ImmutableSet.of(HttpStatus.SC_OK);

    protected static final Set<Integer> DEFAULT_EXPECTED_CODES = ImmutableSet.of(HttpStatus.SC_OK, HttpStatus.SC_NOT_FOUND);

    private static final Set<Integer> MIGRATION_CODES = ImmutableSet.of(HttpStatus.SC_MULTIPLE_CHOICES, HttpStatus.SC_MOVED_PERMANENTLY);

    private final KbConfiguration kbConfiguration;

    private final ObjectMapper objectMapper;

    private final CloseableHttpClient httpClient;

    @Nullable
    private final AuthorizationProvider authorizationProvider;

    /**
     * Constructs the KB HTTP client.
     * 
     * @param kbConfiguration
     *            The KB configuration.
     * @param objectMapper
     *            The object mapper.
     * @param httpClient
     *            The HTTP client.
     * @param authorizationProvider
     *            The authorization provider.
     */
    protected AbstractKbHttpClient(KbConfiguration kbConfiguration,
            ObjectMapper objectMapper,
            CloseableHttpClient httpClient,
            @Nullable AuthorizationProvider authorizationProvider) {
        this.kbConfiguration = Objects.requireNonNull(kbConfiguration, "KB configuration must be initialized.");
        this.objectMapper = Objects.requireNonNull(objectMapper, "Object mapper must be initialized.");
        this.httpClient = Objects.requireNonNull(httpClient, "HTTP client must be initialized.");
        this.authorizationProvider = authorizationProvider;
    }

    /**
     * Gets the authorization provider.
     * 
     * @return Returns the authorization provider if present and emptiness otherwise.
     */
    protected final Optional<AuthorizationProvider> getAuthorizationProvider() {
        return Optional.ofNullable(authorizationProvider);
    }

    /**
     * Constructs a map of page request parameters.
     * 
     * @param pageRequest
     *            The page request.
     * @return Returns a map of page request parameters.
     */
    protected Map<String, String> constructPageRequestParameters(PageRequest pageRequest) {
        Objects.requireNonNull(pageRequest, "Page request must be initialized.");

        int offset = pageRequest.getOffset();
        int limit = pageRequest.getLimit();

        ImmutableMap.Builder<String, String> builder = ImmutableMap.<String, String> builder()
                .put("offset", String.valueOf(offset))
                .put("limit", String.valueOf(limit));

        String sortExpressionsString = pageRequest.getSortExpressionsString().orElse(null);
        if (sortExpressionsString != null) {
            String parsedSortExpressionsString = sortExpressionsString.replace(" ", "+");
            builder = builder.put("sort", parsedSortExpressionsString);
        }

        return builder.build();
    }

    /**
     * Constructs the HTTP entity.
     * 
     * Throws IllegalArgumentException if the given object cannot be serialized.
     * 
     * @param <T>
     *            The object type.
     * @param object
     *            The object.
     * @param contentType
     *            The content type.
     * @return Returns the HTTP entity.
     */
    protected <T> HttpEntity constructHttpEntity(T object, String contentType) {
        Objects.requireNonNull(object, "Object must be initialized.");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(contentType), "Content type must not be null or empty.");

        try {
            String value = writeValueAsString(object);

            return new StringEntity(value, ContentType.create(contentType));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Unable to serialize object.", e);
        }
    }

    /**
     * Construct a POST HTTP request.
     * 
     * @param path
     *            The path.
     * @param parameters
     *            The parameters.
     * @param headers
     *            The headers.
     * @param entity
     *            The entity.
     * @return Returns the classic HTTP request.
     */
    protected ClassicHttpRequest constructPostHttpRequest(String path,
            @Nullable Map<String, String> parameters,
            @Nullable Collection<Header> headers,
            HttpEntity entity) {
        return constructHttpRequest(path, parameters, (uri) -> ClassicRequestBuilder.post(uri), headers, entity);
    }

    /**
     * Construct a GET HTTP request.
     * 
     * @param path
     *            The path.
     * @param parameters
     *            The parameters.
     * @param headers
     *            The headers.
     * @return Returns the classic HTTP request.
     */
    protected ClassicHttpRequest constructGetHttpRequest(String path,
            @Nullable Map<String, String> parameters,
            @Nullable Collection<Header> headers) {
        return constructHttpRequest(path, parameters, (uri) -> ClassicRequestBuilder.get(uri), headers, null);
    }

    /**
     * Executes a HTTP request.
     * 
     * - Assumes reauthentication on Unauthorized response.
     * - Assumes no migratable responses.
     * 
     * @param request
     *            The request.
     * @param successCodes
     *            The success response codes in which a standard message body is expected.
     * @param expectedCodes
     *            The expected response codes.
     * @param clazz
     *            The class.
     * @param <T>
     *            The result type.
     * @return Returns the result.
     */
    protected <T> Result<T> execute(ClassicHttpRequest request,
            Set<Integer> successCodes,
            Set<Integer> expectedCodes,
            Class<T> clazz) {
        ClassTypeReference<T> typeReference = new ClassTypeReference<>(clazz);

        return execute(request, successCodes, expectedCodes, true, false, typeReference);
    }

    /**
     * Executes a HTTP request.
     * 
     * - Assumes reauthentication on Unauthorized response.
     * - Assumes no migratable responses.
     * 
     * @param request
     *            The request.
     * @param successCodes
     *            The success response codes in which a standard message body is expected.
     * @param expectedCodes
     *            The expected response codes.
     * @param typeReference
     *            The type reference.
     * @param <T>
     *            The result type.
     * @return Returns the result.
     */
    protected <T> Result<T> execute(ClassicHttpRequest request,
            Set<Integer> successCodes,
            Set<Integer> expectedCodes,
            TypeReference<T> typeReference) {
        return execute(request, successCodes, expectedCodes, true, false, typeReference);
    }

    /**
     * Executes a HTTP request.
     * 
     * @param request
     *            The request.
     * @param successCodes
     *            The success response codes in which a standard message body is expected.
     * @param expectedCodes
     *            The expected response codes.
     * @param isReauthenticationSupported
     *            The given request support reauthentication if an unauthorized response is returned.
     * @param isMigratable
     *            The given request can yield a migrated response when enabled and false otherwise.
     * @param clazz
     *            The class.
     * @param <T>
     *            The result type.
     * @return Returns the result.
     */
    protected <T> Result<T> execute(ClassicHttpRequest request,
            Set<Integer> successCodes,
            Set<Integer> expectedCodes,
            boolean isReauthenticationSupported,
            boolean isMigratable,
            Class<T> clazz) {
        ClassTypeReference<T> typeReference = new ClassTypeReference<>(clazz);

        return execute(request, successCodes, expectedCodes, isReauthenticationSupported, isMigratable, typeReference);
    }

    /**
     * Executes a HTTP request.
     * 
     * @param request
     *            The request.
     * @param successCodes
     *            The success response codes in which a standard message body is expected.
     * @param expectedCodes
     *            The expected response codes.
     * @param isReauthenticationSupported
     *            The given request supports reauthentication if an unauthorized response is returned.
     * @param isMigratable
     *            The given request can yield a migrated response when enabled and false otherwise.
     * @param typeReference
     *            The type reference.
     * @param <T>
     *            The result type.
     * @return Returns the result.
     */
    protected <T> Result<T> execute(ClassicHttpRequest request,
            Set<Integer> successCodes,
            Set<Integer> expectedCodes,
            boolean isReauthenticationSupported,
            boolean isMigratable,
            TypeReference<T> typeReference) {
        Objects.requireNonNull(request, "Request must be initialized.");
        Objects.requireNonNull(successCodes, "Success response codes must be initialized.");
        Preconditions.checkArgument(!successCodes.isEmpty(), "Success response codes must not be empty.");
        Objects.requireNonNull(expectedCodes, "Expected response codes must be initialized.");
        Preconditions.checkArgument(!expectedCodes.isEmpty(), "Expected response codes must not be empty.");
        Objects.requireNonNull(typeReference, "The type reference must be initialized.");

        String method = request.getMethod();
        String requestUri = request.getRequestUri();

        try {
            return httpClient.execute(request, (response) -> {
                try {
                    // HTTP response yielded response code.
                    int code = response.getCode();

                    // HTTP 401 Unauthorized handling.
                    // If a HTTP 401 Unauthorized response is received, then the original request was sent with invalid
                    // or expired authorization. If this HTTP client supports an authorization provider and the request
                    // supports re-authentication, then re-authenticate to yield a fresh authorization token.
                    if (((isReauthenticationSupported)) && (getAuthorizationProvider().isPresent()) && (HttpStatus.SC_UNAUTHORIZED == code)) {
                        // Clear the existing authentication object and attempt to re-authenticate.
                        AuthorizationProvider authorizationProvider = getAuthorizationProvider().get();
                        if (authorizationProvider.clearAndAuthenticate().isPresent()) {
                            // Re-authentication is successful. Repeat the original request but do not support
                            // re-authentication if a HTTP 401 Unauthorized response is received on the subsequent
                            // request.
                            return execute(request, successCodes, expectedCodes, false, isMigratable, typeReference);
                        } else {
                            // Re-authentication failed. Return the HTTP 401 Unauthorized response from the original
                            // request.
                            HttpResponse<T> httpResponse = new HttpResponse<>(code, expectedCodes, null, null);

                            return new Result<>(method, requestUri, httpResponse);
                        }
                    } else {
                        // Response codes other than HTTP 401 Unauthorized or HTTP client does not support authorization
                        // or HTTP request does not support re-authentication.
                        T messageBody = null;
                        Meta migratedMeta = null;

                        if (isMigratable && MIGRATION_CODES.contains(code)) {
                            // Request can receive migrated responses and actual response code is contained within the
                            // set of expected, migration response codes. This means the response message body should
                            // contain expected contents and requires deserialization.
                            MetaWrapper migratedMetaWrapper = readValueAsObject(response, MetaWrapper.class);
                            migratedMeta = migratedMetaWrapper.getMeta().orElse(null);
                        } else if (successCodes.contains(code)) {
                            // Received response code is contained within the set of expected, successful response
                            // codes. This means the response message body should contain expected contents and requires
                            // deserialization.
                            messageBody = readValueAsObject(response, typeReference);
                        } // Unexpected response code.

                        HttpResponse<T> httpResponse = new HttpResponse<>(code, expectedCodes, messageBody, migratedMeta);

                        return new Result<>(method, requestUri, httpResponse);
                    }
                } finally {
                    response.close();
                }
            });
        } catch (IOException e) {
            return new Result<>(method, requestUri, e);
        }
    }

    /**
     * Executes a HTTP request.
     * 
     * - Assumes deserialization of the response message body to a string.
     * 
     * @param request
     *            The request.
     * @param successCodes
     *            The success codes.
     * @param expectedCodes
     *            The expected codes.
     * @param isReauthenticationSupported
     *            The given request supports reauthentication if an unauthorized response is returned.
     * @param isMigratable
     *            The given request can yield a migrated response when enabled and false otherwise.
     * @return Returns the result.
     */
    protected Result<String> execute(ClassicHttpRequest request,
            Set<Integer> successCodes,
            Set<Integer> expectedCodes,
            boolean isReauthenticationSupported,
            boolean isMigratable) {
        Objects.requireNonNull(request, "Request must be initialized.");
        Objects.requireNonNull(successCodes, "Success response codes must be initialized.");
        Preconditions.checkArgument(!successCodes.isEmpty(), "Success response codes must not be empty.");
        Objects.requireNonNull(expectedCodes, "Expected response codes must be initialized.");
        Preconditions.checkArgument(!expectedCodes.isEmpty(), "Expected response codes must not be empty.");

        String method = request.getMethod();
        String requestUri = request.getRequestUri();

        try {
            return httpClient.execute(request, (response) -> {
                try {
                    // HTTP response yielded response code.
                    int code = response.getCode();

                    // HTTP 401 Unauthorized handling.
                    // If a HTTP 401 Unauthorized response is received, then the original request was sent with invalid
                    // or expired authorization. If this HTTP client supports an authorization provider and the request
                    // supports re-authentication, then re-authenticate to yield a fresh authorization token.
                    if (((isReauthenticationSupported)) && (getAuthorizationProvider().isPresent()) && (HttpStatus.SC_UNAUTHORIZED == code)) {
                        // Clear the existing authentication object and attempt to re-authenticate.
                        AuthorizationProvider authorizationProvider = getAuthorizationProvider().get();
                        if (authorizationProvider.clearAndAuthenticate().isPresent()) {
                            // Re-authentication is successful. Repeat the original request but do not support
                            // re-authentication if a HTTP 401 Unauthorized response is received on the subsequent
                            // request.
                            return execute(request, successCodes, expectedCodes, false, isMigratable);
                        } else {
                            // Re-authentication failed. Return the HTTP 401 Unauthorized response from the original
                            // request.
                            HttpResponse<String> httpResponse = new HttpResponse<>(code, expectedCodes, null, null);

                            return new Result<>(method, requestUri, httpResponse);
                        }
                    } else {
                        // Response codes other than HTTP 401 Unauthorized or HTTP client does not support authorization
                        // or HTTP request does not support re-authentication.
                        String messageBody = null;
                        Meta migratedMeta = null;

                        if (isMigratable && MIGRATION_CODES.contains(code)) {
                            // Request can receive migrated responses and actual response code is contained within the
                            // set of expected, migration response codes. This means the response message body should
                            // contain expected contents and requires deserialization.
                            MetaWrapper migratedMetaWrapper = readValueAsObject(response, MetaWrapper.class);
                            migratedMeta = migratedMetaWrapper.getMeta().orElse(null);
                        } else if (successCodes.contains(code)) {
                            // Received response code is contained within the set of expected, successful response
                            // codes. This means the response message body should contain expected contents and requires
                            // deserialization.
                            HttpEntity httpEntity = response.getEntity();
                            if (httpEntity != null) {
                                messageBody = EntityUtils.toString(httpEntity, "UTF-8");
                            }
                        } // Unexpected response code.

                        HttpResponse<String> httpResponse = new HttpResponse<>(code, expectedCodes, messageBody, migratedMeta);

                        return new Result<>(method, requestUri, httpResponse);
                    }
                } finally {
                    response.close();
                }
            });
        } catch (IOException e) {
            return new Result<>(method, requestUri, e);
        }
    }

    private String writeValueAsString(Object value) throws JsonProcessingException {
        return objectMapper.writeValueAsString(value);
    }

    @Nullable
    private <T> T readValueAsObject(ClassicHttpResponse response, Class<T> clazz) throws UnsupportedOperationException, IOException {
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            InputStream content = entity.getContent();

            return objectMapper.readValue(content, clazz);
        }

        return null;
    }

    @Nullable
    private <T> T readValueAsObject(ClassicHttpResponse response, TypeReference<T> typeReference) throws StreamReadException, DatabindException, IOException {
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            InputStream content = entity.getContent();

            return objectMapper.readValue(content, typeReference);
        }

        return null;
    }

    private URI constructUri(String path, Map<String, String> parameters) {
        String fullPath = kbConfiguration.getHref() + path;

        try {
            URIBuilder builder = new URIBuilder(fullPath);

            if (parameters != null && !parameters.isEmpty()) {
                for (Entry<String, String> entry : parameters.entrySet()) {
                    String parameterName = entry.getKey();
                    String parameterValue = entry.getValue();
                    builder = builder.addParameter(parameterName, parameterValue);
                }
            }

            return builder.build();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Unable to create URI from provided path.", e);
        }
    }

    private ClassicHttpRequest constructHttpRequest(String path,
            Map<String, String> parameters,
            Function<URI, ClassicRequestBuilder> classicRequestBuilderFunction,
            Collection<Header> headers,
            HttpEntity entity) {
        URI uri = constructUri(path, parameters);

        ClassicRequestBuilder builder = classicRequestBuilderFunction.apply(uri);

        if (headers != null && !headers.isEmpty()) {
            for (Header header : headers) {
                builder = builder.addHeader(header);
            }
        }

        if (entity != null) {
            builder = builder.setEntity(entity);
        }

        return builder.build();
    }
}
