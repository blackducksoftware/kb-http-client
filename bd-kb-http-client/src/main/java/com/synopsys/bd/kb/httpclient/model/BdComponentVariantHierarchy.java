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
package com.synopsys.bd.kb.httpclient.model;

import java.util.Objects;

import com.synopsys.bd.kb.httpclient.api.MigratableResult;
import com.synopsys.kb.httpclient.api.Result;
import com.synopsys.kb.httpclient.model.Component;

/**
 * Black Duck-centric component variant hierarchy.
 * 
 * Composes a component variant, its parent component version, and its grandparent component.
 * 
 * @author skatzman
 */
public class BdComponentVariantHierarchy extends BdComponentVersionHierarchy {
    private final Result<BdComponentVariant> componentVariantResult;

    public BdComponentVariantHierarchy(MigratableResult<Component> componentResult,
            MigratableResult<BdComponentVersion> componentVersionResult,
            Result<BdComponentVariant> componentVariantResult) {
        super(componentResult, componentVersionResult);

        this.componentVariantResult = Objects.requireNonNull(componentVariantResult, "Component variant result must be initialized.");
    }

    public Result<BdComponentVariant> getComponentVariantResult() {
        return componentVariantResult;
    }

    @Override
    public boolean isHierarchyPresent() {
        boolean isParentHierarchyPresent = super.isHierarchyPresent();

        boolean isComponentVariantPresent = getComponentVariantResult().getHttpResponse()
                .filter((migratableHttpResponse) -> migratableHttpResponse.isMessageBodyPresent()).isPresent();

        return isParentHierarchyPresent && isComponentVariantPresent;
    }
}
