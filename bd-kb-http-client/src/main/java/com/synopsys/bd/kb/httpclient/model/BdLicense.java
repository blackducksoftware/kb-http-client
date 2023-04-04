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
import java.util.Objects;

import com.synopsys.kb.httpclient.model.License;
import com.synopsys.kb.httpclient.model.LicenseCodeSharing;
import com.synopsys.kb.httpclient.model.LicenseOwnership;
import com.synopsys.kb.httpclient.model.LicenseRestriction;
import com.synopsys.kb.httpclient.model.Meta;

/**
 * Black Duck-centric license representation.
 * 
 * Used to represent the 'RESTRICTED_PROPRIETARY' license family.
 * 
 * @author skatzman
 */
public class BdLicense extends License {
    private final LicenseFamily licenseFamily;

    public BdLicense(String name,
            LicenseCodeSharing codeSharing,
            LicenseOwnership ownership,
            OffsetDateTime lastUpdatedAt,
            String spdxId,
            Boolean parentDeleted,
            LicenseRestriction restriction,
            Meta meta) {
        super(name, codeSharing, ownership, lastUpdatedAt, spdxId, parentDeleted, restriction, meta);

        if (isRestrictedProprietaryLicenseFamily(ownership, restriction)) {
            this.licenseFamily = LicenseFamily.RESTRICTED_PROPRIETARY;
        } else {
            String codeSharingString = codeSharing.name();
            this.licenseFamily = LicenseFamily.valueOf(codeSharingString);
        }
    }

    public BdLicense(License license) {
        super(license);

        LicenseOwnership ownership = license.getOwnership();
        LicenseRestriction restriction = license.getRestriction();
        if (isRestrictedProprietaryLicenseFamily(ownership, restriction)) {
            this.licenseFamily = LicenseFamily.RESTRICTED_PROPRIETARY;
        } else {
            LicenseCodeSharing codeSharing = license.getCodeSharing();
            String codeSharingString = codeSharing.name();
            this.licenseFamily = LicenseFamily.valueOf(codeSharingString);
        }
    }

    public LicenseFamily getLicenseFamily() {
        return licenseFamily;
    }

    @Override
    public int hashCode() {
        int superHashCode = super.hashCode();

        return Objects.hash(superHashCode, getLicenseFamily());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof BdLicense) {
            BdLicense otherBDLicense = (BdLicense) otherObject;

            return super.equals(otherObject)
                    && Objects.equals(getLicenseFamily(), otherBDLicense.getLicenseFamily());
        }

        return false;
    }

    private boolean isRestrictedProprietaryLicenseFamily(LicenseOwnership ownership, LicenseRestriction restriction) {
        Objects.requireNonNull(ownership, "Ownership must be initialized.");
        Objects.requireNonNull(restriction, "Restriction must be initialized.");

        return ((LicenseOwnership.PROPRIETARY.equals(ownership))
                && (LicenseRestriction.RESTRICTED.equals(restriction) || LicenseRestriction.UNKNOWN.equals(restriction)));
    }
}
