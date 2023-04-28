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

import com.synopsys.bd.kb.httpclient.api.MigratableHttpResult;
import com.synopsys.kb.httpclient.model.Component;

/**
 * Black Duck-centric component version hierarchy.
 * 
 * Composes a component version and its parent component.
 * 
 * @author skatzman
 */
public class BdComponentVersionHierarchy {
    private final MigratableHttpResult<Component> componentResult;

    private final MigratableHttpResult<BdComponentVersion> componentVersionResult;

    public BdComponentVersionHierarchy(MigratableHttpResult<Component> componentResult,
            MigratableHttpResult<BdComponentVersion> componentVersionResult) {
        this.componentResult = Objects.requireNonNull(componentResult, "Component result must be initialized.");
        this.componentVersionResult = Objects.requireNonNull(componentVersionResult, "Component version result must be initialized.");
    }

    public MigratableHttpResult<Component> getComponentResult() {
        return componentResult;
    }

    public MigratableHttpResult<BdComponentVersion> getComponentVersionResult() {
        return componentVersionResult;
    }

    public boolean isHierarchyPresent() {
        boolean isComponentPresent = getComponentResult().getMigratableHttpResponse()
                .filter((migratableHttpResponse) -> migratableHttpResponse.isMessageBodyPresent()).isPresent();
        boolean isComponentVersionPresent = getComponentVersionResult().getMigratableHttpResponse()
                .filter((migratableHttpResponse) -> migratableHttpResponse.isMessageBodyPresent()).isPresent();

        return isComponentPresent && isComponentVersionPresent;
    }
}
