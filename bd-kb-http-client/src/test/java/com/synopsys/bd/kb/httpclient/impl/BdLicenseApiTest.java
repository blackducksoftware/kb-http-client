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

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.synopsys.bd.kb.httpclient.AbstractBdTest;
import com.synopsys.bd.kb.httpclient.api.IBdLicenseApi;
import com.synopsys.bd.kb.httpclient.model.BdLicense;
import com.synopsys.kb.httpclient.api.HttpResponse;
import com.synopsys.kb.httpclient.api.ILicenseApi;
import com.synopsys.kb.httpclient.api.PageRequest;
import com.synopsys.kb.httpclient.api.Result;
import com.synopsys.kb.httpclient.model.License;
import com.synopsys.kb.httpclient.model.LicenseCodeSharing;
import com.synopsys.kb.httpclient.model.LicenseOwnership;
import com.synopsys.kb.httpclient.model.LicenseRestriction;
import com.synopsys.kb.httpclient.model.Link;
import com.synopsys.kb.httpclient.model.Meta;
import com.synopsys.kb.httpclient.model.Page;

/**
 * Black Duck-centric license API test.
 * 
 * @author skatzman
 */
public class BdLicenseApiTest extends AbstractBdTest {
    private static final UUID LICENSE_ID = UUID.randomUUID();

    private static final String REQUEST_METHOD = "GET";

    private static final String LICENSE_REQUEST_URI = BASE_HREF + "/api/licenses";

    private static final Throwable CAUSE = new IOException("This is an exception.");

    private static final String NAME = "Apache License 2.0";

    private static final LicenseCodeSharing CODE_SHARING = LicenseCodeSharing.PERMISSIVE;

    private static final LicenseOwnership OWNERSHIP = LicenseOwnership.OPEN_SOURCE;

    private static final OffsetDateTime LAST_UPDATED_AT = OffsetDateTime.now();

    private static final String SPDX_ID = "Apache-2.0";

    private static final Boolean PARENT_DELETED = Boolean.FALSE;

    private static final LicenseRestriction RESTRICTION = LicenseRestriction.UNRESTRICTED;

    private static final Meta META = new Meta(LICENSE_REQUEST_URI + '/' + LICENSE_ID,
            List.of(new Link("text", LICENSE_REQUEST_URI + '/' + LICENSE_ID + "/text"),
                    new Link("license-terms", LICENSE_REQUEST_URI + '/' + LICENSE_ID + "/license-terms")));

    @Mock
    private ILicenseApi licenseApi;

    private IBdLicenseApi bdLicenseApi;

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.openMocks(this);

        bdLicenseApi = new BdLicenseApi(licenseApi);
    }

    @Test
    public void testFindWithoutHttpResponse() {
        Result<License> sourceResult = new Result<>(REQUEST_METHOD, LICENSE_REQUEST_URI + '/' + LICENSE_ID, CAUSE);

        Mockito.when(licenseApi.find(LICENSE_ID)).thenReturn(sourceResult);

        Result<BdLicense> result = bdLicenseApi.find(LICENSE_ID);

        assertResult(sourceResult, result);
    }

    @Test
    public void testFindWithoutHttpResponseMessageBody() {
        HttpResponse<License> sourceHttpResponse = new HttpResponse<>(404, Set.of(200, 404), null, null);
        Result<License> sourceResult = new Result<>(REQUEST_METHOD, LICENSE_REQUEST_URI + '/' + LICENSE_ID, sourceHttpResponse);

        Mockito.when(licenseApi.find(LICENSE_ID)).thenReturn(sourceResult);

        Result<BdLicense> result = bdLicenseApi.find(LICENSE_ID);

        assertResult(sourceResult, result);
    }

    @Test
    public void testFind() {
        License license = new License(NAME, CODE_SHARING, OWNERSHIP, LAST_UPDATED_AT, SPDX_ID, PARENT_DELETED, RESTRICTION, META);
        HttpResponse<License> sourceHttpResponse = new HttpResponse<>(200, Set.of(200, 404), license, null);
        Result<License> sourceResult = new Result<>(REQUEST_METHOD, LICENSE_REQUEST_URI + '/' + LICENSE_ID, sourceHttpResponse);

        Mockito.when(licenseApi.find(LICENSE_ID)).thenReturn(sourceResult);

        Result<BdLicense> result = bdLicenseApi.find(LICENSE_ID);

        assertResult(sourceResult, result);
    }

    @Test
    public void testFindManyWithoutHttpResponse() {
        PageRequest pageRequest = new PageRequest(0, 10, Collections.emptyList());

        Result<Page<License>> sourceResult = new Result<>(REQUEST_METHOD, LICENSE_REQUEST_URI, CAUSE);

        Mockito.when(licenseApi.findMany(pageRequest)).thenReturn(sourceResult);

        Result<Page<BdLicense>> result = bdLicenseApi.findMany(pageRequest);

        assertResult(sourceResult, result);
    }

    @Test
    public void testFindManyWithoutHttpResponseMessageBody() {
        PageRequest pageRequest = new PageRequest(0, 10, Collections.emptyList());

        HttpResponse<Page<License>> sourceHttpResponse = new HttpResponse<>(422, Set.of(200), null, null);
        Result<Page<License>> sourceResult = new Result<>(REQUEST_METHOD, LICENSE_REQUEST_URI, sourceHttpResponse);

        Mockito.when(licenseApi.findMany(pageRequest)).thenReturn(sourceResult);

        Result<Page<BdLicense>> result = bdLicenseApi.findMany(pageRequest);

        assertResult(sourceResult, result);
    }

    @Test
    public void testFindMany() {
        PageRequest pageRequest = new PageRequest(0, 10, Collections.emptyList());

        License license = new License(NAME, CODE_SHARING, OWNERSHIP, LAST_UPDATED_AT, SPDX_ID, PARENT_DELETED, RESTRICTION, META);
        Meta licensePageMeta = new Meta(LICENSE_REQUEST_URI, Collections.emptyList());
        Page<License> licensePage = new Page<>(1, List.of(license), licensePageMeta);
        HttpResponse<Page<License>> sourceHttpResponse = new HttpResponse<>(200, Set.of(200), licensePage, null);
        Result<Page<License>> sourceResult = new Result<>(REQUEST_METHOD, LICENSE_REQUEST_URI, sourceHttpResponse);

        Mockito.when(licenseApi.findMany(pageRequest)).thenReturn(sourceResult);

        Result<Page<BdLicense>> result = bdLicenseApi.findMany(pageRequest);

        assertResult(sourceResult, result);
    }
}
