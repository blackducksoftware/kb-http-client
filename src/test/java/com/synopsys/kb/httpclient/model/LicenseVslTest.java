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

import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synopsys.kb.httpclient.AbstractTest;

/**
 * License VSL test.
 * 
 * @author skatzman
 */
public class LicenseVslTest extends AbstractTest {
    private static final String FILTERED_VSL = "thebeerwarelicenserevision42";

    private static final String VSL = "\"THE BEER-WARE LICENSE\" (Revision 42)";

    private static final String LICENSE_NAME = "Beerware License";

    private static final String LICENSE_FAMILY_NAME = "Beer-ware License";

    private static final UUID LICENSE_ID = UUID.randomUUID();

    private static final String LICENSE = BASE_HREF + "/api/licenses/" + LICENSE_ID;

    @Test
    public void testConstructor() {
        LicenseVsl licenseVsl = new LicenseVsl(FILTERED_VSL, VSL, LICENSE_NAME, LICENSE_FAMILY_NAME, LICENSE);

        Assert.assertEquals(licenseVsl.getFilteredVsl(), FILTERED_VSL, "Filtered VSLs should be equal.");
        Assert.assertEquals(licenseVsl.getVsl(), VSL, "VSLs should be equal.");
        Assert.assertEquals(licenseVsl.getLicenseName(), LICENSE_NAME, "License names should be equal.");
        Assert.assertEquals(licenseVsl.getLicenseFamilyName(), LICENSE_FAMILY_NAME, "License family names should be equal.");
        Assert.assertEquals(licenseVsl.getLicense(), LICENSE, "Licenses should be equal.");

        Assert.assertEquals(licenseVsl.getLicenseId(), LICENSE_ID, "License ids should be equal.");
    }

    @Test
    public void testDeserialization() throws JsonProcessingException {
        LicenseVsl licenseVsl = new LicenseVsl(FILTERED_VSL, VSL, LICENSE_NAME, LICENSE_FAMILY_NAME, LICENSE);

        String json = serialize(licenseVsl);
        LicenseVsl result = deserialize(json, LicenseVsl.class);

        Assert.assertEquals(result.getFilteredVsl(), FILTERED_VSL, "Filtered VSLs should be equal.");
        Assert.assertEquals(result.getVsl(), VSL, "VSLs should be equal.");
        Assert.assertEquals(result.getLicenseName(), LICENSE_NAME, "License names should be equal.");
        Assert.assertEquals(result.getLicenseFamilyName(), LICENSE_FAMILY_NAME, "License family names should be equal.");
        Assert.assertEquals(result.getLicense(), LICENSE, "Licenses should be equal.");
    }

    @Test
    public void testHashCode() {
        LicenseVsl licenseVsl = new LicenseVsl(FILTERED_VSL, VSL, LICENSE_NAME, LICENSE_FAMILY_NAME, LICENSE);
        LicenseVsl copyLicenseVsl = new LicenseVsl(FILTERED_VSL, VSL, LICENSE_NAME, LICENSE_FAMILY_NAME, LICENSE);
        LicenseVsl differentLicenseVsl = new LicenseVsl(FILTERED_VSL, VSL, LICENSE_NAME, "differentLicenseFamilyName", LICENSE);

        assertHashCode(licenseVsl, copyLicenseVsl, differentLicenseVsl);
    }

    @Test
    public void testEquals() {
        LicenseVsl licenseVsl = new LicenseVsl(FILTERED_VSL, VSL, LICENSE_NAME, LICENSE_FAMILY_NAME, LICENSE);
        LicenseVsl copyLicenseVsl = new LicenseVsl(FILTERED_VSL, VSL, LICENSE_NAME, LICENSE_FAMILY_NAME, LICENSE);
        LicenseVsl differentLicenseVsl = new LicenseVsl(FILTERED_VSL, VSL, LICENSE_NAME, "differentLicenseFamilyName", LICENSE);

        assertEquals(licenseVsl, copyLicenseVsl, differentLicenseVsl);
    }
}
