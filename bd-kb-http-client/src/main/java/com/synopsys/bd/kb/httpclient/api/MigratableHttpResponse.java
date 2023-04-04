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
package com.synopsys.bd.kb.httpclient.api;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.synopsys.kb.httpclient.api.HttpResponse;
import com.synopsys.kb.httpclient.model.Meta;

/**
 * Migratable HTTP response given an acceptable 2xx, 3xx, 4xx, or 5xx response code.
 * 
 * The migratable HTTP response contains all relevant values of the HTTP response but also tracks the history of
 * migrated meta objects given one-to-many subsequent requests given merge or split migration responses.
 * 
 * @author skatzman
 *
 * @param <T>
 *            The value type.
 */
public class MigratableHttpResponse<T> extends HttpResponse<T> {
    private final List<Meta> migratedMetaHistory;

    /**
     * Constructs a migratable HTTP response.
     * 
     * @param code
     *            The actual response code.
     * @param expectedCodes
     *            The expected response codes given the request.
     * @param messageBody
     *            The message body which typically is provided if the actual response code is an expected response code.
     * @param migratedMeta
     *            The migrated metadata.
     * @param migratedMetaHistory
     *            The history chain in order of occurrence of migrated metas encountered.
     */
    public MigratableHttpResponse(int code,
            Set<Integer> expectedCodes,
            @Nullable T messageBody,
            @Nullable Meta migratedMeta,
            @Nullable List<Meta> migratedMetaHistory) {
        super(code, expectedCodes, messageBody, migratedMeta);

        this.migratedMetaHistory = (migratedMetaHistory != null) ? ImmutableList.copyOf(migratedMetaHistory) : ImmutableList.of();
    }

    public List<Meta> getMigratedMetaHistory() {
        return migratedMetaHistory;
    }
}
