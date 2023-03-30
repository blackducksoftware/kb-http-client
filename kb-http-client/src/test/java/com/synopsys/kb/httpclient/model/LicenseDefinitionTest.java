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

    private static final String BASE_HREF = "https://kbtest.blackducksoftware.com/api/licenses/";

    @Test
    public void testConstructorForSingleLicense() {
        String href = BASE_HREF + LICENSE_ID_1;
        LicenseDefinitionItem licenseDefinitionItem = new LicenseDefinitionItem(href, null);
        List<LicenseDefinitionItem> items = List.of(licenseDefinitionItem);
        LicenseDefinition licenseDefinition = new LicenseDefinition(TYPE, items);

        Assert.assertEquals(licenseDefinition.getType(), TYPE, "Types should be equal.");
        Assert.assertEquals(licenseDefinition.getItems(), items, "Items should be equal.");
    }

    @Test
    public void testConstructorForManyLicenses() {
        String href1 = BASE_HREF + LICENSE_ID_1;
        String href2 = BASE_HREF + LICENSE_ID_2;
        String href3 = BASE_HREF + LICENSE_ID_3;
        LicenseDefinitionItem licenseDefinitionItem1 = new LicenseDefinitionItem(href1, null);
        LicenseDefinitionItem licenseDefinitionItem2 = new LicenseDefinitionItem(href2, null);
        LicenseDefinitionItem licenseDefinitionItem3 = new LicenseDefinitionItem(href3, null);
        List<LicenseDefinitionItem> items = List.of(licenseDefinitionItem1, licenseDefinitionItem2, licenseDefinitionItem3);
        LicenseDefinition licenseDefinition = new LicenseDefinition(TYPE, items);

        Assert.assertEquals(licenseDefinition.getType(), TYPE, "Types should be equal.");
        Assert.assertEquals(licenseDefinition.getItems(), items, "Items should be equal.");
    }

    @Test
    public void testDeserializationForSingleLicense() throws JsonProcessingException {
        String href = BASE_HREF + LICENSE_ID_1;
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
        String href1 = BASE_HREF + LICENSE_ID_1;
        String href2 = BASE_HREF + LICENSE_ID_2;
        String href3 = BASE_HREF + LICENSE_ID_3;
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
