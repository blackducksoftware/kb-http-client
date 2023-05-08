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

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synopsys.kb.httpclient.AbstractTest;

/**
 * License family VSL test.
 * 
 * @author skatzman
 */
public class LicenseFamilyVslTest extends AbstractTest {
    private static final String FILTERED_VSL = "3dfxglidesourcecodegeneralpubliclicence";

    private static final String VSL = "3DFX GLIDE Source Code General Public Licence";

    private static final String LICENSE_FAMILY_NAME = "3DFX GLIDE Source Code General Public License";

    @Test
    public void testConstructor() {
        LicenseFamilyVsl licenseFamilyVsl = new LicenseFamilyVsl(FILTERED_VSL, VSL, LICENSE_FAMILY_NAME);

        Assert.assertEquals(licenseFamilyVsl.getFilteredVsl(), FILTERED_VSL, "Filtered VSLs should be equal.");
        Assert.assertEquals(licenseFamilyVsl.getVsl(), VSL, "VSLs should be equal.");
        Assert.assertEquals(licenseFamilyVsl.getLicenseFamilyName(), LICENSE_FAMILY_NAME, "License family names should be equal.");
    }

    @Test
    public void testDeserialization() throws JsonProcessingException {
        LicenseFamilyVsl licenseFamilyVsl = new LicenseFamilyVsl(FILTERED_VSL, VSL, LICENSE_FAMILY_NAME);

        String json = serialize(licenseFamilyVsl);
        LicenseFamilyVsl result = deserialize(json, LicenseFamilyVsl.class);

        Assert.assertEquals(result.getFilteredVsl(), FILTERED_VSL, "Filtered VSLs should be equal.");
        Assert.assertEquals(result.getVsl(), VSL, "VSLs should be equal.");
        Assert.assertEquals(result.getLicenseFamilyName(), LICENSE_FAMILY_NAME, "License family names should be equal.");
    }

    @Test
    public void testHashCode() {
        LicenseFamilyVsl licenseFamilyVsl = new LicenseFamilyVsl(FILTERED_VSL, VSL, LICENSE_FAMILY_NAME);
        LicenseFamilyVsl copyLicenseFamilyVsl = new LicenseFamilyVsl(FILTERED_VSL, VSL, LICENSE_FAMILY_NAME);
        LicenseFamilyVsl differentLicenseFamilyVsl = new LicenseFamilyVsl(FILTERED_VSL, VSL, "differentLicenseFamilyName");

        assertHashCode(licenseFamilyVsl, copyLicenseFamilyVsl, differentLicenseFamilyVsl);
    }

    @Test
    public void testEquals() {
        LicenseFamilyVsl licenseFamilyVsl = new LicenseFamilyVsl(FILTERED_VSL, VSL, LICENSE_FAMILY_NAME);
        LicenseFamilyVsl copyLicenseFamilyVsl = new LicenseFamilyVsl(FILTERED_VSL, VSL, LICENSE_FAMILY_NAME);
        LicenseFamilyVsl differentLicenseFamilyVsl = new LicenseFamilyVsl(FILTERED_VSL, VSL, "differentLicenseFamilyName");

        assertEquals(licenseFamilyVsl, copyLicenseFamilyVsl, differentLicenseFamilyVsl);
    }
}
