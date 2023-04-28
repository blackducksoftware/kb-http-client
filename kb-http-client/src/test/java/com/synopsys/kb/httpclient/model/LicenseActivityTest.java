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

import java.time.OffsetDateTime;
import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synopsys.kb.httpclient.AbstractTest;

/**
 * License activity test.
 * 
 * @author skatzman
 */
public class LicenseActivityTest extends AbstractTest {
    private static final UUID LICENSE_ID = UUID.randomUUID();

    private static final String LICENSE = BASE_HREF + "/api/licenses/" + LICENSE_ID;

    private static final OffsetDateTime UPDATED_DATE = OffsetDateTime.now();

    @Test
    public void testConstructor() {
        LicenseActivity activity = new LicenseActivity(LICENSE, UPDATED_DATE);

        Assert.assertEquals(activity.getLicense(), LICENSE, "Licenses should be equal.");
        Assert.assertEquals(activity.getUpdatedDate(), UPDATED_DATE, "Updated dates should be equal.");

        Assert.assertEquals(activity.getLicenseId(), LICENSE_ID, "License ids should be equal.");
    }

    @Test
    public void testDeserialization() throws JsonProcessingException {
        LicenseActivity activity = new LicenseActivity(LICENSE, UPDATED_DATE);

        String json = serialize(activity);
        LicenseActivity result = deserialize(json, LicenseActivity.class);

        Assert.assertEquals(result.getLicense(), LICENSE, "Licenses should be equal.");
        Assert.assertNotNull(result.getUpdatedDate(), "Updated date should be initialized.");
    }

    @Test
    public void testHashCode() {
        LicenseActivity activity = new LicenseActivity(LICENSE, UPDATED_DATE);
        LicenseActivity copyActivity = new LicenseActivity(LICENSE, UPDATED_DATE);
        LicenseActivity differentActivity = new LicenseActivity(LICENSE, OffsetDateTime.now().plusDays(1L));

        assertHashCode(activity, copyActivity, differentActivity);
    }

    @Test
    public void testEquals() {
        LicenseActivity activity = new LicenseActivity(LICENSE, UPDATED_DATE);
        LicenseActivity copyActivity = new LicenseActivity(LICENSE, UPDATED_DATE);
        LicenseActivity differentActivity = new LicenseActivity(LICENSE, OffsetDateTime.now().plusDays(1L));

        assertEquals(activity, copyActivity, differentActivity);
    }
}
