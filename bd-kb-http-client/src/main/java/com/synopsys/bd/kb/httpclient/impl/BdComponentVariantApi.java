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

import com.synopsys.bd.kb.httpclient.api.IBdComponentVariantApi;
import com.synopsys.bd.kb.httpclient.model.BdComponentVariant;
import com.synopsys.kb.httpclient.api.HttpResult;
import com.synopsys.kb.httpclient.api.IComponentVariantApi;
import com.synopsys.kb.httpclient.model.ComponentVariant;

/**
 * Black Duck-centric component API implementation.
 * 
 * Primarily used to follow component migration paths in a Black Duck-specific manner.
 * 
 * @author skatzman
 */
public class BdComponentVariantApi extends AbstractBdApi implements IBdComponentVariantApi {
    private final IComponentVariantApi componentVariantApi;

    public BdComponentVariantApi(IComponentVariantApi componentVariantApi) {
        super();

        this.componentVariantApi = Objects.requireNonNull(componentVariantApi, "Component variant API must be initialized.");
    }

    @Override
    public HttpResult<BdComponentVariant> findComponentVariantV4(UUID componentVariantId) {
        Objects.requireNonNull(componentVariantId, "Component variant id must be initialized.");

        HttpResult<ComponentVariant> httpResult = componentVariantApi.findComponentVariantV4(componentVariantId);

        // Convert a component variant to a BD component variant.
        Function<ComponentVariant, BdComponentVariant> conversionFunction = (componentVariant) -> {
            return new BdComponentVariant(componentVariant);
        };

        return convert(httpResult, conversionFunction);
    }
}
