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

public class LicenseDefinitionItemTest extends AbstractTest {
    private static final UUID LICENSE_ID_1 = UUID.randomUUID();

    private static final UUID LICENSE_ID_2 = UUID.randomUUID();

    private static final UUID LICENSE_ID_3 = UUID.randomUUID();

    private static final String BASE_HREF = "https://kbtest.blackducksoftware.com/api/licenses/";

    @Test
    public void testConstructorForSingleLicense() {
        String href = BASE_HREF + LICENSE_ID_1;
        LicenseDefinitionItem licenseDefinitionItem = new LicenseDefinitionItem(href, null);

        Assert.assertEquals(licenseDefinitionItem.getHref().orElse(null), href, "HREFs should be equal.");
        Assert.assertFalse(licenseDefinitionItem.getLicenseDefinition().isPresent(), "License definition should not be present.");

        Assert.assertEquals(licenseDefinitionItem.getLicenseId().orElse(null), LICENSE_ID_1, "License ids should be equal.");
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
        LicenseDefinition licenseDefinition = new LicenseDefinition(LicenseDefinitionType.CONJUNCTIVE, items);
        LicenseDefinitionItem licenseDefinitionItem = new LicenseDefinitionItem(null, licenseDefinition);

        Assert.assertFalse(licenseDefinitionItem.getHref().isPresent(), "HREF should not be present.");
        Assert.assertEquals(licenseDefinitionItem.getLicenseDefinition().orElse(null), licenseDefinition, "License definitions should be equal.");
    }

    @Test
    public void testDeserializationForSingleLicense() throws JsonProcessingException {
        String href = BASE_HREF + LICENSE_ID_1;
        LicenseDefinitionItem licenseDefinitionItem = new LicenseDefinitionItem(href, null);

        String json = serialize(licenseDefinitionItem);
        LicenseDefinitionItem result = deserialize(json, LicenseDefinitionItem.class);

        Assert.assertEquals(result.getHref().orElse(null), href, "HREFs should be equal.");
        Assert.assertFalse(result.getLicenseDefinition().isPresent(), "License definition should not be present.");
    }

    @Test
    public void testDeserializationForManyLicenses() throws JsonProcessingException {
        String href1 = BASE_HREF + LICENSE_ID_1;
        String href2 = BASE_HREF + LICENSE_ID_2;
        String href3 = BASE_HREF + LICENSE_ID_3;
        LicenseDefinitionItem childLicenseDefinitionItem1 = new LicenseDefinitionItem(href1, null);
        LicenseDefinitionItem childLicenseDefinitionItem2 = new LicenseDefinitionItem(href2, null);
        LicenseDefinitionItem childLicenseDefinitionItem3 = new LicenseDefinitionItem(href3, null);
        List<LicenseDefinitionItem> childLicenseDefinitionItems = List.of(childLicenseDefinitionItem1, childLicenseDefinitionItem2,
                childLicenseDefinitionItem3);
        LicenseDefinition licenseDefinition = new LicenseDefinition(LicenseDefinitionType.CONJUNCTIVE, childLicenseDefinitionItems);
        LicenseDefinitionItem licenseDefinitionItem = new LicenseDefinitionItem(null, licenseDefinition);

        String json = serialize(licenseDefinitionItem);
        LicenseDefinitionItem result = deserialize(json, LicenseDefinitionItem.class);

        Assert.assertFalse(result.getHref().isPresent(), "HREF should not be present.");
        Assert.assertEquals(result.getLicenseDefinition().orElse(null), licenseDefinition, "License definitions should be equal.");
    }
}
