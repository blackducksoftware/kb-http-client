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
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.synopsys.bd.kb.httpclient.AbstractBdTest;
import com.synopsys.bd.kb.httpclient.api.IBdLicenseApi;
import com.synopsys.bd.kb.httpclient.model.BdLicense;
import com.synopsys.bd.kb.httpclient.model.BdLicenseDefinition;
import com.synopsys.kb.httpclient.api.HttpResponse;
import com.synopsys.kb.httpclient.api.HttpResult;
import com.synopsys.kb.httpclient.model.License;
import com.synopsys.kb.httpclient.model.LicenseDefinition;
import com.synopsys.kb.httpclient.model.LicenseDefinitionItem;
import com.synopsys.kb.httpclient.model.LicenseDefinitionType;

/**
 * Black Duck-centric license definition finder test.
 * 
 * @author skatzman
 */
public class BdLicenseDefinitionFinderTest extends AbstractBdTest {
    private static final UUID LICENSE_ID_1 = UUID.randomUUID();

    private static final UUID LICENSE_ID_2 = UUID.randomUUID();

    private static final UUID LICENSE_ID_3 = UUID.randomUUID();

    private static final UUID LICENSE_ID_4 = UUID.randomUUID();

    private static final UUID LICENSE_ID_5 = UUID.randomUUID();

    @Mock
    private IBdLicenseApi bdLicenseApi;

    private BdLicenseDefinitionFinder licenseDefinitionFinder;

    private BdLicense bdLicense1;

    private BdLicense bdLicense2;

    private BdLicense bdLicense3;

    private BdLicense bdLicense4;

    private BdLicense bdLicense5;

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.openMocks(this);

        // Use smaller max depth to enable exhaustion testing.
        // 0-indexed so maximum depth of 3 allows for up-to-4 levels of depth retrieval.
        this.licenseDefinitionFinder = new BdLicenseDefinitionFinder(bdLicenseApi, 3);

        License license1 = constructLicense(LICENSE_ID_1, "License1");
        this.bdLicense1 = new BdLicense(license1);

        License license2 = constructLicense(LICENSE_ID_2, "License2");
        this.bdLicense2 = new BdLicense(license2);

        License license3 = constructLicense(LICENSE_ID_3, "License3");
        this.bdLicense3 = new BdLicense(license3);

        License license4 = constructLicense(LICENSE_ID_4, "License4");
        this.bdLicense4 = new BdLicense(license4);

        License license5 = constructLicense(LICENSE_ID_5, "License5");
        this.bdLicense5 = new BdLicense(license5);
    }

    @Test
    public void testFindWithSingleLicenseWhenAbsent() {
        LicenseDefinitionItem licenseDefinitionItem = constructLicenseDefinitionItem(LICENSE_ID_1, null);
        LicenseDefinition licenseDefinition = constructLicenseDefinition(LicenseDefinitionType.CONJUNCTIVE, List.of(licenseDefinitionItem));

        HttpResponse<BdLicense> httpResponse1 = new HttpResponse<>(404, Set.of(200, 404), null, null);
        HttpResult<BdLicense> bdLicenseResult1 = new HttpResult<>("GET", BASE_HREF + "/api/licenses/" + LICENSE_ID_1, httpResponse1);

        Mockito.when(bdLicenseApi.findLicenseV4(LICENSE_ID_1)).thenReturn(bdLicenseResult1);

        Optional<BdLicenseDefinition> httpResult = licenseDefinitionFinder.find(licenseDefinition);

        Assert.assertFalse(httpResult.isPresent(), "HTTP result should not be present.");
    }

    @Test
    public void testFindWithSingleLicenseWhenPresent() {
        LicenseDefinitionItem licenseDefinitionItem = constructLicenseDefinitionItem(LICENSE_ID_1, null);
        LicenseDefinition licenseDefinition = constructLicenseDefinition(LicenseDefinitionType.CONJUNCTIVE, List.of(licenseDefinitionItem));

        HttpResponse<BdLicense> httpResponse1 = new HttpResponse<>(200, Set.of(200, 404), bdLicense1, null);
        HttpResult<BdLicense> bdLicenseResult1 = new HttpResult<>("GET", BASE_HREF + "/api/licenses/" + LICENSE_ID_1, httpResponse1);

        Mockito.when(bdLicenseApi.findLicenseV4(LICENSE_ID_1)).thenReturn(bdLicenseResult1);

        BdLicenseDefinition bdLicenseDefinition = licenseDefinitionFinder.find(licenseDefinition).orElse(null);

        // (License1)
        assertBdLicenseDefinition(bdLicenseDefinition, bdLicense1, null, 0);
    }

    @Test
    public void testFindWithMultipleLicensesWhenAbsent() {
        LicenseDefinitionItem licenseDefinitionItem1a = constructLicenseDefinitionItem(LICENSE_ID_1, null);
        LicenseDefinitionItem licenseDefinitionItem1b = constructLicenseDefinitionItem(LICENSE_ID_2, null);
        LicenseDefinition licenseDefinition = constructLicenseDefinition(LicenseDefinitionType.CONJUNCTIVE,
                List.of(licenseDefinitionItem1a, licenseDefinitionItem1b));

        HttpResponse<BdLicense> httpResponse1 = new HttpResponse<>(200, Set.of(200, 404), bdLicense1, null);
        HttpResult<BdLicense> bdLicenseResult1 = new HttpResult<>("GET", BASE_HREF + "/api/licenses/" + LICENSE_ID_1, httpResponse1);

        HttpResponse<BdLicense> httpResponse2 = new HttpResponse<>(404, Set.of(200, 404), null, null);
        HttpResult<BdLicense> bdLicenseResult2 = new HttpResult<>("GET", BASE_HREF + "/api/licenses/" + LICENSE_ID_2, httpResponse2);

        Mockito.when(bdLicenseApi.findLicenseV4(LICENSE_ID_1)).thenReturn(bdLicenseResult1);
        Mockito.when(bdLicenseApi.findLicenseV4(LICENSE_ID_2)).thenReturn(bdLicenseResult2);

        Optional<BdLicenseDefinition> httpResult = licenseDefinitionFinder.find(licenseDefinition);

        Assert.assertFalse(httpResult.isPresent(), "HTTP result should not be present.");
    }

    @Test
    public void testFindWithMultipleLicensesWhenPresent() {
        LicenseDefinitionItem licenseDefinitionItem1a = constructLicenseDefinitionItem(LICENSE_ID_1, null);
        LicenseDefinitionItem licenseDefinitionItem1b = constructLicenseDefinitionItem(LICENSE_ID_2, null);
        LicenseDefinition licenseDefinition = constructLicenseDefinition(LicenseDefinitionType.CONJUNCTIVE,
                List.of(licenseDefinitionItem1a, licenseDefinitionItem1b));

        HttpResponse<BdLicense> httpResponse1 = new HttpResponse<>(200, Set.of(200, 404), bdLicense1, null);
        HttpResult<BdLicense> bdLicenseResult1 = new HttpResult<>("GET", BASE_HREF + "/api/licenses/" + LICENSE_ID_1, httpResponse1);

        HttpResponse<BdLicense> httpResponse2 = new HttpResponse<>(200, Set.of(200, 404), bdLicense2, null);
        HttpResult<BdLicense> bdLicenseResult2 = new HttpResult<>("GET", BASE_HREF + "/api/licenses/" + LICENSE_ID_2, httpResponse2);

        Mockito.when(bdLicenseApi.findLicenseV4(LICENSE_ID_1)).thenReturn(bdLicenseResult1);
        Mockito.when(bdLicenseApi.findLicenseV4(LICENSE_ID_2)).thenReturn(bdLicenseResult2);

        BdLicenseDefinition bdLicenseDefinition = licenseDefinitionFinder.find(licenseDefinition).orElse(null);

        // (License1 AND License2)
        assertBdLicenseDefinition(bdLicenseDefinition, null, LicenseDefinitionType.CONJUNCTIVE, 2);
        List<BdLicenseDefinition> bdLicenseDefinitions = bdLicenseDefinition.getLicenseDefinitions();

        // (License1)
        assertBdLicenseDefinition(bdLicenseDefinitions.get(0), bdLicense1, null, 0);
        // (License2)
        assertBdLicenseDefinition(bdLicenseDefinitions.get(1), bdLicense2, null, 0);
    }

    @Test
    public void testFindWithMultipleLicensesWithDepthWhenAbsent() {
        LicenseDefinitionItem licenseDefinitionItem1a = constructLicenseDefinitionItem(LICENSE_ID_1, null);
        LicenseDefinitionItem licenseDefinitionItem1b = constructLicenseDefinitionItem(LICENSE_ID_2, null);
        LicenseDefinition licenseDefinition1 = constructLicenseDefinition(LicenseDefinitionType.CONJUNCTIVE,
                List.of(licenseDefinitionItem1a, licenseDefinitionItem1b));

        LicenseDefinitionItem licenseDefinitionItem2a = constructLicenseDefinitionItem(LICENSE_ID_3, null);
        LicenseDefinitionItem licenseDefinitionItem2b = constructLicenseDefinitionItem(LICENSE_ID_4, null);
        LicenseDefinition licenseDefinition2 = constructLicenseDefinition(LicenseDefinitionType.CONJUNCTIVE,
                List.of(licenseDefinitionItem2a, licenseDefinitionItem2b));

        LicenseDefinitionItem licenseDefinitionItem3a = constructLicenseDefinitionItem(null, licenseDefinition1);
        LicenseDefinitionItem licenseDefinitionItem3b = constructLicenseDefinitionItem(null, licenseDefinition2);
        LicenseDefinition licenseDefinition = constructLicenseDefinition(LicenseDefinitionType.CONJUNCTIVE,
                List.of(licenseDefinitionItem3a, licenseDefinitionItem3b));

        HttpResponse<BdLicense> httpResponse1 = new HttpResponse<>(200, Set.of(200, 404), bdLicense1, null);
        HttpResult<BdLicense> bdLicenseResult1 = new HttpResult<>("GET", BASE_HREF + "/api/licenses/" + LICENSE_ID_1, httpResponse1);

        HttpResponse<BdLicense> httpResponse2 = new HttpResponse<>(200, Set.of(200, 404), bdLicense2, null);
        HttpResult<BdLicense> bdLicenseResult2 = new HttpResult<>("GET", BASE_HREF + "/api/licenses/" + LICENSE_ID_1, httpResponse2);

        HttpResponse<BdLicense> httpResponse3 = new HttpResponse<>(200, Set.of(200, 404), bdLicense3, null);
        HttpResult<BdLicense> bdLicenseResult3 = new HttpResult<>("GET", BASE_HREF + "/api/licenses/" + LICENSE_ID_1, httpResponse3);

        HttpResponse<BdLicense> httpResponse4 = new HttpResponse<>(404, Set.of(200, 404), null, null);
        HttpResult<BdLicense> bdLicenseResult4 = new HttpResult<>("GET", BASE_HREF + "/api/licenses/" + LICENSE_ID_4, httpResponse4);

        Mockito.when(bdLicenseApi.findLicenseV4(LICENSE_ID_1)).thenReturn(bdLicenseResult1);
        Mockito.when(bdLicenseApi.findLicenseV4(LICENSE_ID_2)).thenReturn(bdLicenseResult2);
        Mockito.when(bdLicenseApi.findLicenseV4(LICENSE_ID_3)).thenReturn(bdLicenseResult3);
        Mockito.when(bdLicenseApi.findLicenseV4(LICENSE_ID_4)).thenReturn(bdLicenseResult4);

        Optional<BdLicenseDefinition> httpResult = licenseDefinitionFinder.find(licenseDefinition);

        Assert.assertFalse(httpResult.isPresent(), "HTTP result should not be present.");
    }

    @Test
    public void testFindWithMultipleLicensesWithDepthWhenPresent() {
        LicenseDefinitionItem licenseDefinitionItem1a = constructLicenseDefinitionItem(LICENSE_ID_1, null);
        LicenseDefinitionItem licenseDefinitionItem1b = constructLicenseDefinitionItem(LICENSE_ID_2, null);
        LicenseDefinition licenseDefinition1 = constructLicenseDefinition(LicenseDefinitionType.CONJUNCTIVE,
                List.of(licenseDefinitionItem1a, licenseDefinitionItem1b));

        LicenseDefinitionItem licenseDefinitionItem2a = constructLicenseDefinitionItem(LICENSE_ID_3, null);
        LicenseDefinitionItem licenseDefinitionItem2b = constructLicenseDefinitionItem(LICENSE_ID_4, null);
        LicenseDefinition licenseDefinition2 = constructLicenseDefinition(LicenseDefinitionType.CONJUNCTIVE,
                List.of(licenseDefinitionItem2a, licenseDefinitionItem2b));

        LicenseDefinitionItem licenseDefinitionItem3a = constructLicenseDefinitionItem(null, licenseDefinition1);
        LicenseDefinitionItem licenseDefinitionItem3b = constructLicenseDefinitionItem(null, licenseDefinition2);
        LicenseDefinition licenseDefinition = constructLicenseDefinition(LicenseDefinitionType.DISJUNCTIVE,
                List.of(licenseDefinitionItem3a, licenseDefinitionItem3b));

        HttpResponse<BdLicense> httpResponse1 = new HttpResponse<>(200, Set.of(200, 404), bdLicense1, null);
        HttpResult<BdLicense> bdLicenseResult1 = new HttpResult<>("GET", BASE_HREF + "/api/licenses/" + LICENSE_ID_1, httpResponse1);

        HttpResponse<BdLicense> httpResponse2 = new HttpResponse<>(200, Set.of(200, 404), bdLicense2, null);
        HttpResult<BdLicense> bdLicenseResult2 = new HttpResult<>("GET", BASE_HREF + "/api/licenses/" + LICENSE_ID_1, httpResponse2);

        HttpResponse<BdLicense> httpResponse3 = new HttpResponse<>(200, Set.of(200, 404), bdLicense3, null);
        HttpResult<BdLicense> bdLicenseResult3 = new HttpResult<>("GET", BASE_HREF + "/api/licenses/" + LICENSE_ID_1, httpResponse3);

        HttpResponse<BdLicense> httpResponse4 = new HttpResponse<>(200, Set.of(200, 404), bdLicense4, null);
        HttpResult<BdLicense> bdLicenseResult4 = new HttpResult<>("GET", BASE_HREF + "/api/licenses/" + LICENSE_ID_4, httpResponse4);

        Mockito.when(bdLicenseApi.findLicenseV4(LICENSE_ID_1)).thenReturn(bdLicenseResult1);
        Mockito.when(bdLicenseApi.findLicenseV4(LICENSE_ID_2)).thenReturn(bdLicenseResult2);
        Mockito.when(bdLicenseApi.findLicenseV4(LICENSE_ID_3)).thenReturn(bdLicenseResult3);
        Mockito.when(bdLicenseApi.findLicenseV4(LICENSE_ID_4)).thenReturn(bdLicenseResult4);

        BdLicenseDefinition bdLicenseDefinition = licenseDefinitionFinder.find(licenseDefinition).orElse(null);

        // ((License1 AND License2) OR (License3 AND License4))
        assertBdLicenseDefinition(bdLicenseDefinition, null, LicenseDefinitionType.DISJUNCTIVE, 2);

        List<BdLicenseDefinition> bdLicenseDefinitions1 = bdLicenseDefinition.getLicenseDefinitions();
        // (License1 AND License2)
        assertBdLicenseDefinition(bdLicenseDefinitions1.get(0), null, LicenseDefinitionType.CONJUNCTIVE, 2);
        // (License3 AND License4)
        assertBdLicenseDefinition(bdLicenseDefinitions1.get(1), null, LicenseDefinitionType.CONJUNCTIVE, 2);

        List<BdLicenseDefinition> bdLicenseDefinitions2a = bdLicenseDefinitions1.get(0).getLicenseDefinitions();
        // (License1)
        assertBdLicenseDefinition(bdLicenseDefinitions2a.get(0), bdLicense1, null, 0);
        // (License2)
        assertBdLicenseDefinition(bdLicenseDefinitions2a.get(1), bdLicense2, null, 0);

        List<BdLicenseDefinition> bdLicenseDefinitions2b = bdLicenseDefinitions1.get(1).getLicenseDefinitions();
        // (License3)
        assertBdLicenseDefinition(bdLicenseDefinitions2b.get(0), bdLicense3, null, 0);
        // (License4)
        assertBdLicenseDefinition(bdLicenseDefinitions2b.get(1), bdLicense4, null, 0);
    }

    @Test
    public void testFindWithMultipleLicensesWithDepthWithExhaustion() {
        // ((License1 AND (License2 AND (License3 AND (License4 AND License5)))))
        LicenseDefinitionItem licenseDefinitionItem4 = constructLicenseDefinitionItem(LICENSE_ID_4, null);
        LicenseDefinitionItem licenseDefinitionItem5 = constructLicenseDefinitionItem(LICENSE_ID_5, null);
        LicenseDefinition licenseDefinition4And = constructLicenseDefinition(LicenseDefinitionType.CONJUNCTIVE,
                List.of(licenseDefinitionItem4, licenseDefinitionItem5));

        LicenseDefinitionItem licenseDefinitionItem3 = constructLicenseDefinitionItem(LICENSE_ID_3, null);
        LicenseDefinitionItem licenseDefinitionItem4And = constructLicenseDefinitionItem(null, licenseDefinition4And);
        LicenseDefinition licenseDefinition3And = constructLicenseDefinition(LicenseDefinitionType.CONJUNCTIVE,
                List.of(licenseDefinitionItem3, licenseDefinitionItem4And));

        LicenseDefinitionItem licenseDefinitionItem2 = constructLicenseDefinitionItem(LICENSE_ID_2, null);
        LicenseDefinitionItem licenseDefinitionItem3And = constructLicenseDefinitionItem(null, licenseDefinition3And);
        LicenseDefinition licenseDefinition2And = constructLicenseDefinition(LicenseDefinitionType.CONJUNCTIVE,
                List.of(licenseDefinitionItem2, licenseDefinitionItem3And));

        LicenseDefinitionItem licenseDefinitionItem1 = constructLicenseDefinitionItem(LICENSE_ID_1, null);
        LicenseDefinitionItem licenseDefinitionItem2And = constructLicenseDefinitionItem(null, licenseDefinition2And);
        LicenseDefinition licenseDefinition1And = constructLicenseDefinition(LicenseDefinitionType.CONJUNCTIVE,
                List.of(licenseDefinitionItem1, licenseDefinitionItem2And));

        HttpResponse<BdLicense> httpResponse1 = new HttpResponse<>(200, Set.of(200, 404), bdLicense1, null);
        HttpResult<BdLicense> bdLicenseResult1 = new HttpResult<>("GET", BASE_HREF + "/api/licenses/" + LICENSE_ID_1, httpResponse1);

        HttpResponse<BdLicense> httpResponse2 = new HttpResponse<>(200, Set.of(200, 404), bdLicense2, null);
        HttpResult<BdLicense> bdLicenseResult2 = new HttpResult<>("GET", BASE_HREF + "/api/licenses/" + LICENSE_ID_1, httpResponse2);

        HttpResponse<BdLicense> httpResponse3 = new HttpResponse<>(200, Set.of(200, 404), bdLicense3, null);
        HttpResult<BdLicense> bdLicenseResult3 = new HttpResult<>("GET", BASE_HREF + "/api/licenses/" + LICENSE_ID_1, httpResponse3);

        HttpResponse<BdLicense> httpResponse4 = new HttpResponse<>(200, Set.of(200, 404), bdLicense4, null);
        HttpResult<BdLicense> bdLicenseResult4 = new HttpResult<>("GET", BASE_HREF + "/api/licenses/" + LICENSE_ID_4, httpResponse4);

        HttpResponse<BdLicense> httpResponse5 = new HttpResponse<>(200, Set.of(200, 404), bdLicense5, null);
        HttpResult<BdLicense> bdLicenseResult5 = new HttpResult<>("GET", BASE_HREF + "/api/licenses/" + LICENSE_ID_5, httpResponse5);

        Mockito.when(bdLicenseApi.findLicenseV4(LICENSE_ID_1)).thenReturn(bdLicenseResult1);
        Mockito.when(bdLicenseApi.findLicenseV4(LICENSE_ID_2)).thenReturn(bdLicenseResult2);
        Mockito.when(bdLicenseApi.findLicenseV4(LICENSE_ID_3)).thenReturn(bdLicenseResult3);
        Mockito.when(bdLicenseApi.findLicenseV4(LICENSE_ID_4)).thenReturn(bdLicenseResult4);
        Mockito.when(bdLicenseApi.findLicenseV4(LICENSE_ID_5)).thenReturn(bdLicenseResult5);

        Optional<BdLicenseDefinition> httpResult = licenseDefinitionFinder.find(licenseDefinition1And);

        Assert.assertFalse(httpResult.isPresent(), "HTTP result should not be present.");
    }

    private void assertBdLicenseDefinition(BdLicenseDefinition actualLicenseDefinition,
            @Nullable BdLicense expectedLicense,
            @Nullable LicenseDefinitionType expectedType,
            int numberOfExpectedLicenseDefinitions) {
        Assert.assertNotNull(actualLicenseDefinition, "License definition should be initialized.");

        BdLicense actualLicense = actualLicenseDefinition.getLicense().orElse(null);
        if (expectedLicense != null) {
            Assert.assertEquals(actualLicense, expectedLicense, "Licenses should be equal.");
        } else {
            Assert.assertNull(actualLicense, "License should be null.");
        }

        LicenseDefinitionType actualType = actualLicenseDefinition.getType().orElse(null);
        if (expectedType != null) {
            Assert.assertEquals(actualType, expectedType, "Types should be equal.");
        } else {
            Assert.assertNull(actualType, "Type should be null.");
        }

        List<BdLicenseDefinition> actualLicenseDefinitions = actualLicenseDefinition.getLicenseDefinitions();
        Assert.assertEquals(actualLicenseDefinitions.size(), numberOfExpectedLicenseDefinitions, "Number of license definitions should be equal.");
    }
}
