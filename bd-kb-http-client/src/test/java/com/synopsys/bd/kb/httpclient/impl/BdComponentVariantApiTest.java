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

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.synopsys.bd.kb.httpclient.AbstractBdTest;
import com.synopsys.bd.kb.httpclient.api.IBdComponentVariantApi;
import com.synopsys.bd.kb.httpclient.model.BdComponentVariant;
import com.synopsys.kb.httpclient.api.HttpResponse;
import com.synopsys.kb.httpclient.api.HttpResult;
import com.synopsys.kb.httpclient.api.IComponentVariantApi;
import com.synopsys.kb.httpclient.model.ComponentVariant;
import com.synopsys.kb.httpclient.model.LicenseDefinition;
import com.synopsys.kb.httpclient.model.LicenseDefinitionItem;
import com.synopsys.kb.httpclient.model.LicenseDefinitionType;
import com.synopsys.kb.httpclient.model.Link;
import com.synopsys.kb.httpclient.model.Meta;

/**
 * Black Duck-centric component variant API test.
 * 
 * @author skatzman
 */
public class BdComponentVariantApiTest extends AbstractBdTest {
    private static final UUID COMPONENT_VARIANT_ID = UUID.randomUUID();

    private static final UUID COMPONENT_ID = UUID.randomUUID();

    private static final UUID COMPONENT_VERSION_ID = UUID.randomUUID();

    private static final String VERSION = "0.3.27-4.fc24/x86_64";

    private static final String EXTERNAL_NAMESPACE = "fedora";

    private static final String EXTERNAL_ID = "GarminPlugin/0.3.27-4.fc24/x86_64";

    private static final Boolean EXTERNAL_NAMESPACE_DISTRIBUTION = Boolean.FALSE;

    private static final String PACKAGE_URL = "pkg:rpm/fedora/GarminPlugin@0.3.27-4.fc24?arch=x86_64";

    private static final String TYPE = "versioned_download";

    private static final LicenseDefinition LICENSE_DEFINITION = new LicenseDefinition(LicenseDefinitionType.CONJUNCTIVE,
            List.of(new LicenseDefinitionItem(BASE_HREF + "/api/licenses/" + UUID.randomUUID(), null)));

    private static final Boolean DELETED = Boolean.FALSE;

    private static final Boolean COMPONENT_INTELLIGENCE = Boolean.FALSE;

    private static final String HREF = BASE_HREF + "/api/variants/" + COMPONENT_VARIANT_ID;

    private static final Meta META = new Meta(HREF,
            List.of(new Link("component", BASE_HREF + "/api/components/" + COMPONENT_ID),
                    new Link("version", BASE_HREF + "/api/versions/" + COMPONENT_VERSION_ID)));

    @Mock
    private IComponentVariantApi componentVariantApi;

    private IBdComponentVariantApi bdComponentVariantApi;

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.openMocks(this);

        bdComponentVariantApi = new BdComponentVariantApi(componentVariantApi);
    }

    @Test
    public void testFindComponentVariantV4() {
        ComponentVariant componentVariant = new ComponentVariant(VERSION, EXTERNAL_NAMESPACE, EXTERNAL_ID, EXTERNAL_NAMESPACE_DISTRIBUTION, PACKAGE_URL, TYPE,
                LICENSE_DEFINITION, DELETED, COMPONENT_INTELLIGENCE, META);
        HttpResponse<ComponentVariant> sourceHttpResponse = new HttpResponse<>(200, Set.of(200, 404), componentVariant, null);
        HttpResult<ComponentVariant> sourceResult = new HttpResult<>("GET", HREF, sourceHttpResponse);
        Mockito.when(componentVariantApi.findComponentVariantV4(COMPONENT_VARIANT_ID)).thenReturn(sourceResult);

        HttpResult<BdComponentVariant> httpResult = bdComponentVariantApi.findComponentVariantV4(COMPONENT_VARIANT_ID);

        assertHttpResult(sourceResult, httpResult);
    }
}
