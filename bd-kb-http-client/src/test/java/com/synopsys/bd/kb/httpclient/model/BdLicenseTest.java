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
package com.synopsys.bd.kb.httpclient.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.synopsys.bd.kb.httpclient.AbstractBdTest;
import com.synopsys.kb.httpclient.model.LicenseCodeSharing;
import com.synopsys.kb.httpclient.model.LicenseOwnership;
import com.synopsys.kb.httpclient.model.LicenseRestriction;
import com.synopsys.kb.httpclient.model.Link;
import com.synopsys.kb.httpclient.model.Meta;

/**
 * Black Duck-centric license test.
 * 
 * @author skatzman
 */
public class BdLicenseTest extends AbstractBdTest {
    private static final UUID ID = UUID.randomUUID();

    private static final String NAME = "Apache License 2.0";

    private static final LicenseCodeSharing CODE_SHARING = LicenseCodeSharing.PERMISSIVE;

    private static final LicenseOwnership OWNERSHIP = LicenseOwnership.OPEN_SOURCE;

    private static final OffsetDateTime LAST_UPDATED_AT = OffsetDateTime.now();

    private static final String SPDX_ID = "Apache-2.0";

    private static final Boolean PARENT_DELETED = Boolean.FALSE;

    private static final LicenseRestriction RESTRICTION = LicenseRestriction.UNRESTRICTED;

    private static final String HREF = BASE_HREF + "/api/licenses/" + ID;

    private static final Meta META = new Meta(HREF,
            List.of(new Link("text", HREF + "/text"),
                    new Link("license-terms", HREF + "/license-terms")));

    @Test
    public void testConstructor() {
        BdLicense license = new BdLicense(NAME, CODE_SHARING, OWNERSHIP, LAST_UPDATED_AT, SPDX_ID, PARENT_DELETED, RESTRICTION, META);

        Assert.assertEquals(license.getId(), ID, "Ids should be equal.");
        Assert.assertEquals(license.getName(), NAME, "Names should be equal.");
        Assert.assertEquals(license.getCodeSharing(), CODE_SHARING, "Code sharings should be equal.");
        Assert.assertEquals(license.getOwnership(), OWNERSHIP, "Ownerships should be equal.");
        Assert.assertEquals(license.getLastUpdatedAt(), LAST_UPDATED_AT, "Last updated ats should be equal.");
        Assert.assertEquals(license.getSpdxId().orElse(null), SPDX_ID, "SPDX ids should be equal.");
        Assert.assertEquals(license.isParentDeleted(), PARENT_DELETED.booleanValue(), "Is parent deleted flags should be equal.");
        Assert.assertEquals(license.getRestriction(), RESTRICTION, "Restrictions should be equal.");
        Assert.assertEquals(license.getMeta(), META, "Metas should be equal.");
        Assert.assertEquals(license.getLicenseFamily(), LicenseFamily.valueOf(CODE_SHARING.name()), "License families should be equal.");
    }

    @Test
    public void testGetLicenseFamily() {
        LicenseOwnership[] ownerships = LicenseOwnership.values();
        LicenseRestriction[] restrictions = LicenseRestriction.values();
        for (LicenseOwnership ownership : ownerships) {
            for (LicenseRestriction restriction : restrictions) {
                BdLicense license = new BdLicense(NAME, CODE_SHARING, ownership, LAST_UPDATED_AT, SPDX_ID, PARENT_DELETED, restriction, META);

                boolean isRestrictedProprietary = ((LicenseOwnership.PROPRIETARY.equals(ownership))
                        && (LicenseRestriction.RESTRICTED.equals(restriction) || LicenseRestriction.UNKNOWN.equals(restriction)));
                if (isRestrictedProprietary) {
                    Assert.assertEquals(license.getLicenseFamily(), LicenseFamily.RESTRICTED_PROPRIETARY, "License families should be equal.");
                } else {
                    Assert.assertEquals(license.getLicenseFamily(), LicenseFamily.valueOf(CODE_SHARING.name()), "License families should be equal.");
                }
            }
        }
    }
}
