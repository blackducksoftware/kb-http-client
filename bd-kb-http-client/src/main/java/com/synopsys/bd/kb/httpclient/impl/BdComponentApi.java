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
import java.util.UUID;
import java.util.function.Function;

import com.synopsys.bd.kb.httpclient.api.IBdComponentApi;
import com.synopsys.bd.kb.httpclient.api.MigratableResult;
import com.synopsys.kb.httpclient.api.IComponentApi;
import com.synopsys.kb.httpclient.api.Result;
import com.synopsys.kb.httpclient.model.Component;

/**
 * Black Duck-centric component API implementation.
 * 
 * Primarily used to follow component migration paths in a Black Duck-specific manner.
 * 
 * @author skatzman
 */
public class BdComponentApi extends AbstractMigratableBdApi implements IBdComponentApi {
    private final IComponentApi componentApi;

    public BdComponentApi(IComponentApi componentApi) {
        super();

        this.componentApi = Objects.requireNonNull(componentApi, "Component API must be initialized.");
    }

    BdComponentApi(IComponentApi componentApi, int maximumAttemptNumber) {
        super(maximumAttemptNumber);

        this.componentApi = Objects.requireNonNull(componentApi, "Component API must be initialized.");
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
}
