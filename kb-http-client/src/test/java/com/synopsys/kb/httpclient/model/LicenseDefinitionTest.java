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
package com.synopsys.kb.httpclient.model;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synopsys.kb.httpclient.AbstractTest;

/**
 * License definition test.
 * 
 * @author skatzman
 */
public class LicenseDefinitionTest extends AbstractTest {
    private static final LicenseDefinitionType TYPE = LicenseDefinitionType.DISJUNCTIVE;

    private static final UUID LICENSE_ID_1 = UUID.randomUUID();

    private static final UUID LICENSE_ID_2 = UUID.randomUUID();

    private static final UUID LICENSE_ID_3 = UUID.randomUUID();

    private static final UUID LICENSE_ID_4 = UUID.randomUUID();

    private static final String LICENSES_BASE_HREF = BASE_HREF + "/api/licenses/";

    @Test
    public void testConstructorForSingleLicense() {
        String href = LICENSES_BASE_HREF + LICENSE_ID_1;
        LicenseDefinitionItem licenseDefinitionItem = new LicenseDefinitionItem(href, null);
        List<LicenseDefinitionItem> items = List.of(licenseDefinitionItem);
        LicenseDefinition licenseDefinition = new LicenseDefinition(TYPE, items);

        Assert.assertEquals(licenseDefinition.getType(), TYPE, "Types should be equal.");
        Assert.assertEquals(licenseDefinition.getItems(), items, "Items should be equal.");
    }

    @Test
    public void testConstructorForManyLicenses() {
        String href1 = LICENSES_BASE_HREF + LICENSE_ID_1;
        String href2 = LICENSES_BASE_HREF + LICENSE_ID_2;
        String href3 = LICENSES_BASE_HREF + LICENSE_ID_3;
        LicenseDefinitionItem licenseDefinitionItem1 = new LicenseDefinitionItem(href1, null);
        LicenseDefinitionItem licenseDefinitionItem2 = new LicenseDefinitionItem(href2, null);
        LicenseDefinitionItem licenseDefinitionItem3 = new LicenseDefinitionItem(href3, null);
        List<LicenseDefinitionItem> items = List.of(licenseDefinitionItem1, licenseDefinitionItem2, licenseDefinitionItem3);
        LicenseDefinition licenseDefinition = new LicenseDefinition(TYPE, items);

        Assert.assertEquals(licenseDefinition.getType(), TYPE, "Types should be equal.");
        Assert.assertEquals(licenseDefinition.getItems(), items, "Items should be equal.");
    }

    @Test
    public void testGetLicenseIdsForSingleLicense() {
        // 1
        String href = LICENSES_BASE_HREF + LICENSE_ID_1;
        LicenseDefinitionItem licenseDefinitionItem = new LicenseDefinitionItem(href, null);
        List<LicenseDefinitionItem> items = List.of(licenseDefinitionItem);
        LicenseDefinition licenseDefinition = new LicenseDefinition(TYPE, items);

        Set<UUID> licenseIds = licenseDefinition.getLicenseIds();

        Assert.assertEquals(licenseIds.size(), 1, "Number of license ids should be equal.");
        Assert.assertTrue(licenseIds.contains(LICENSE_ID_1), "License id should be present.");
    }

    @Test
    public void testGetLicenseIdsForSingleDepthComplexLicense() {
        // (1 and 2 and 3)
        String href1 = LICENSES_BASE_HREF + LICENSE_ID_1;
        String href2 = LICENSES_BASE_HREF + LICENSE_ID_2;
        String href3 = LICENSES_BASE_HREF + LICENSE_ID_3;
        LicenseDefinitionItem licenseDefinitionItem1 = new LicenseDefinitionItem(href1, null);
        LicenseDefinitionItem licenseDefinitionItem2 = new LicenseDefinitionItem(href2, null);
        LicenseDefinitionItem licenseDefinitionItem3 = new LicenseDefinitionItem(href3, null);
        List<LicenseDefinitionItem> items = List.of(licenseDefinitionItem1, licenseDefinitionItem2, licenseDefinitionItem3);
        LicenseDefinition licenseDefinition = new LicenseDefinition(TYPE, items);

        Set<UUID> licenseIds = licenseDefinition.getLicenseIds();

        Assert.assertEquals(licenseIds.size(), 3, "Number of license ids should be equal.");
        Assert.assertTrue(licenseIds.contains(LICENSE_ID_1), "License id should be present.");
        Assert.assertTrue(licenseIds.contains(LICENSE_ID_2), "License id should be present.");
        Assert.assertTrue(licenseIds.contains(LICENSE_ID_3), "License id should be present.");
    }

    @Test
    public void testGetLicenseIdsForMultiDepthComplexLicense() {
        // ((1 and 2) or (3 and 4))
        String href1a = LICENSES_BASE_HREF + LICENSE_ID_1;
        String href1b = LICENSES_BASE_HREF + LICENSE_ID_2;
        LicenseDefinitionItem licenseDefinitionItem1a = new LicenseDefinitionItem(href1a, null);
        LicenseDefinitionItem licenseDefinitionItem1b = new LicenseDefinitionItem(href1b, null);
        List<LicenseDefinitionItem> items1 = List.of(licenseDefinitionItem1a, licenseDefinitionItem1b);
        LicenseDefinition licenseDefinition1 = new LicenseDefinition(LicenseDefinitionType.CONJUNCTIVE, items1);

        String href2a = LICENSES_BASE_HREF + LICENSE_ID_3;
        String href2b = LICENSES_BASE_HREF + LICENSE_ID_4;
        LicenseDefinitionItem licenseDefinitionItem2a = new LicenseDefinitionItem(href2a, null);
        LicenseDefinitionItem licenseDefinitionItem2b = new LicenseDefinitionItem(href2b, null);
        List<LicenseDefinitionItem> items2 = List.of(licenseDefinitionItem2a, licenseDefinitionItem2b);
        LicenseDefinition licenseDefinition2 = new LicenseDefinition(LicenseDefinitionType.CONJUNCTIVE, items2);

        LicenseDefinitionItem licenseDefinitionItem3a = new LicenseDefinitionItem(null, licenseDefinition1);
        LicenseDefinitionItem licenseDefinitionItem3b = new LicenseDefinitionItem(null, licenseDefinition2);
        List<LicenseDefinitionItem> items3 = List.of(licenseDefinitionItem3a, licenseDefinitionItem3b);
        LicenseDefinition licenseDefinition3 = new LicenseDefinition(LicenseDefinitionType.DISJUNCTIVE, items3);

        Set<UUID> licenseIds = licenseDefinition3.getLicenseIds();

        Assert.assertEquals(licenseIds.size(), 4, "Number of license ids should be equal.");
        Assert.assertTrue(licenseIds.contains(LICENSE_ID_1), "License id should be present.");
        Assert.assertTrue(licenseIds.contains(LICENSE_ID_2), "License id should be present.");
        Assert.assertTrue(licenseIds.contains(LICENSE_ID_3), "License id should be present.");
        Assert.assertTrue(licenseIds.contains(LICENSE_ID_4), "License id should be present.");
    }

    @Test
    public void testGetLicenseIdsForMultiDepthComplexLicenseWithDuplicates() {
        // ((1 and 2) or (1 and 2))
        String href1a = LICENSES_BASE_HREF + LICENSE_ID_1;
        String href1b = LICENSES_BASE_HREF + LICENSE_ID_2;
        LicenseDefinitionItem licenseDefinitionItem1a = new LicenseDefinitionItem(href1a, null);
        LicenseDefinitionItem licenseDefinitionItem1b = new LicenseDefinitionItem(href1b, null);
        List<LicenseDefinitionItem> items1 = List.of(licenseDefinitionItem1a, licenseDefinitionItem1b);
        LicenseDefinition licenseDefinition1 = new LicenseDefinition(LicenseDefinitionType.CONJUNCTIVE, items1);

        String href2a = LICENSES_BASE_HREF + LICENSE_ID_1;
        String href2b = LICENSES_BASE_HREF + LICENSE_ID_2;
        LicenseDefinitionItem licenseDefinitionItem2a = new LicenseDefinitionItem(href2a, null);
        LicenseDefinitionItem licenseDefinitionItem2b = new LicenseDefinitionItem(href2b, null);
        List<LicenseDefinitionItem> items2 = List.of(licenseDefinitionItem2a, licenseDefinitionItem2b);
        LicenseDefinition licenseDefinition2 = new LicenseDefinition(LicenseDefinitionType.CONJUNCTIVE, items2);

        LicenseDefinitionItem licenseDefinitionItem3a = new LicenseDefinitionItem(null, licenseDefinition1);
        LicenseDefinitionItem licenseDefinitionItem3b = new LicenseDefinitionItem(null, licenseDefinition2);
        List<LicenseDefinitionItem> items3 = List.of(licenseDefinitionItem3a, licenseDefinitionItem3b);
        LicenseDefinition licenseDefinition3 = new LicenseDefinition(LicenseDefinitionType.DISJUNCTIVE, items3);

        Set<UUID> licenseIds = licenseDefinition3.getLicenseIds();

        Assert.assertEquals(licenseIds.size(), 2, "Number of license ids should be equal.");
        Assert.assertTrue(licenseIds.contains(LICENSE_ID_1), "License id should be present.");
        Assert.assertTrue(licenseIds.contains(LICENSE_ID_2), "License id should be present.");
    }

    @Test
    public void testDeserializationForSingleLicense() throws JsonProcessingException {
        String href = LICENSES_BASE_HREF + LICENSE_ID_1;
        LicenseDefinitionItem licenseDefinitionItem = new LicenseDefinitionItem(href, null);
        List<LicenseDefinitionItem> items = List.of(licenseDefinitionItem);
        LicenseDefinition licenseDefinition = new LicenseDefinition(TYPE, items);

        String json = serialize(licenseDefinition);
        LicenseDefinition result = deserialize(json, LicenseDefinition.class);

        Assert.assertEquals(result.getType(), TYPE, "Types should be equal.");
        Assert.assertEquals(result.getItems(), items, "Items should be equal.");
    }

    @Test
    public void testDeserializationForManyLicenses() throws JsonProcessingException {
        String href1 = LICENSES_BASE_HREF + LICENSE_ID_1;
        String href2 = LICENSES_BASE_HREF + LICENSE_ID_2;
        String href3 = LICENSES_BASE_HREF + LICENSE_ID_3;
        LicenseDefinitionItem licenseDefinitionItem1 = new LicenseDefinitionItem(href1, null);
        LicenseDefinitionItem licenseDefinitionItem2 = new LicenseDefinitionItem(href2, null);
        LicenseDefinitionItem licenseDefinitionItem3 = new LicenseDefinitionItem(href3, null);
        List<LicenseDefinitionItem> items = List.of(licenseDefinitionItem1, licenseDefinitionItem2, licenseDefinitionItem3);
        LicenseDefinition licenseDefinition = new LicenseDefinition(TYPE, items);

        String json = serialize(licenseDefinition);
        LicenseDefinition result = deserialize(json, LicenseDefinition.class);

        Assert.assertEquals(result.getType(), TYPE, "Types should be equal.");
        Assert.assertEquals(result.getItems(), items, "Items should be equal.");
    }
}
