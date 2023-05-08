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
import com.synopsys.kb.httpclient.api.Relationship;

public class LicenseTermTest extends AbstractTest {
    private static final UUID LICENSE_TERM_ID = UUID.randomUUID();

    private static final String NAME = "Compensate Damages";

    private static final String DESCRIPTION = "If software is part of a commercial product, license states you cannot defend and compensate project contributor from lawsuits & damages caused by your commercial offering";

    private static final LicenseTermResponsibility RESPONSIBILITY = LicenseTermResponsibility.FORBIDDEN;

    private static final Meta META = new Meta(BASE_HREF + "/api/license-terms/" + LICENSE_TERM_ID,
            List.of(new Link(Relationship.INCOMPATIBLE_LICENSE_TERM, BASE_HREF + "/api/license-terms/" + UUID.randomUUID())));

    @Test
    public void testConstructor() {
        LicenseTerm licenseTerm = new LicenseTerm(NAME, DESCRIPTION, RESPONSIBILITY, META);

        Assert.assertEquals(licenseTerm.getName(), NAME, "Names should be equal.");
        Assert.assertEquals(licenseTerm.getDescription(), DESCRIPTION, "Descriptions should be equal.");
        Assert.assertEquals(licenseTerm.getResponsibility(), RESPONSIBILITY, "Responsibilities should be equal.");
        Assert.assertEquals(licenseTerm.getMeta(), META, "Metas should be equal.");

        Assert.assertEquals(licenseTerm.getId(), LICENSE_TERM_ID, "Ids should be equal.");
    }

    @Test
    public void testDeserialization() throws JsonProcessingException {
        LicenseTerm licenseTerm = new LicenseTerm(NAME, DESCRIPTION, RESPONSIBILITY, META);

        String json = serialize(licenseTerm);
        LicenseTerm result = deserialize(json, LicenseTerm.class);

        Assert.assertEquals(result.getName(), NAME, "Names should be equal.");
        Assert.assertEquals(result.getDescription(), DESCRIPTION, "Descriptions should be equal.");
        Assert.assertEquals(result.getResponsibility(), RESPONSIBILITY, "Responsibilities should be equal.");
        Assert.assertEquals(result.getMeta(), META, "Metas should be equal.");
    }

    @Test
    public void testHashCode() {
        LicenseTerm licenseTerm = new LicenseTerm(NAME, DESCRIPTION, RESPONSIBILITY, META);
        LicenseTerm copyLicenseTerm = new LicenseTerm(NAME, DESCRIPTION, RESPONSIBILITY, META);
        LicenseTerm differentLicenseTerm = new LicenseTerm(NAME, DESCRIPTION, LicenseTermResponsibility.REQUIRED, META);

        assertHashCode(licenseTerm, copyLicenseTerm, differentLicenseTerm);
    }

    @Test
    public void testEquals() {
        LicenseTerm licenseTerm = new LicenseTerm(NAME, DESCRIPTION, RESPONSIBILITY, META);
        LicenseTerm copyLicenseTerm = new LicenseTerm(NAME, DESCRIPTION, RESPONSIBILITY, META);
        LicenseTerm differentLicenseTerm = new LicenseTerm(NAME, DESCRIPTION, LicenseTermResponsibility.REQUIRED, META);

        assertEquals(licenseTerm, copyLicenseTerm, differentLicenseTerm);
    }
}
