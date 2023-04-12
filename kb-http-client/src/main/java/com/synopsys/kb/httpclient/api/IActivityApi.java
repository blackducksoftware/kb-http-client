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
package com.synopsys.kb.httpclient.api;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import com.synopsys.kb.httpclient.model.BdsaVulnerabilityActivity;
import com.synopsys.kb.httpclient.model.ComponentActivity;
import com.synopsys.kb.httpclient.model.ComponentVariantActivity;
import com.synopsys.kb.httpclient.model.ComponentVersionActivity;
import com.synopsys.kb.httpclient.model.CveVulnerabilityActivity;
import com.synopsys.kb.httpclient.model.LicenseActivity;
import com.synopsys.kb.httpclient.model.ListHolder;

/**
 * Activity API interface.
 * 
 * @author skatzman
 */
public interface IActivityApi {
    /**
     * Finds component activities.
     * 
     * Determines if one or more of the provided components has an associated update for component metadata since the
     * given timestamp.
     * 
     * Migrated and soft deleted component ids will always return an associated activity regardless of provided
     * timestamp.
     * 
     * Version: 3
     * 
     * Expected response codes
     * 200 OK
     * 
     * @param componentIds
     *            The set of component ids. Must be not null, not empty, and less than or equal to 1000 ids.
     * @param activitySince
     *            The activity since date. Must be not null.
     * @return Returns the component activity result.
     */
    Result<ListHolder<ComponentActivity>> findComponentActivities(Set<UUID> componentIds, OffsetDateTime activitySince);

    /**
     * Finds component ongoing version activities.
     * 
     * Determines if one or more of the provided components has an associated update for ongoing version metadata since
     * the given timestamp.
     * 
     * Migrated and soft deleted component ids will always return an associated activity regardless of provided
     * timestamp.
     * 
     * Version: 3
     * 
     * Expected response codes
     * 200 OK
     * 
     * @param componentIds
     *            The set of component ids. Must be not null, not empty, and less than or equal to 1000 ids.
     * @param activitySince
     *            The activity since date. Must be not null.
     * @return Returns the component activity result.
     */
    Result<ListHolder<ComponentActivity>> findComponentOngoingVersionActivities(Set<UUID> componentIds, OffsetDateTime activitySince);

    /**
     * Finds component version activities.
     * 
     * Determines if one or more of the provided component versions has an associated update for component version
     * metadata since the given timestamp.
     * 
     * Migrated and soft deleted component version ids will always return an associated activity regardless of provided
     * timestamp.
     *
     * Version: 3
     * 
     * Expected response codes
     * 200 OK
     * 
     * @param componentVersionIds
     *            The set of component version ids. Must be not null, not empty, and less than or equal to 1000 ids.
     * @param activitySince
     *            The activity since date. Must be not null.
     * @return Returns the component version activity result.
     */
    Result<ListHolder<ComponentVersionActivity>> findComponentVersionActivities(Set<UUID> componentVersionIds, OffsetDateTime activitySince);

    /**
     * Finds component version license activities.
     * 
     * Determines if one or more of the provided component versions has an associated update for component version
     * declared license definition metadata since the given timestamp.
     * 
     * Migrated and soft deleted component version ids will always return an associated activity regardless of provided
     * timestamp.
     * 
     * Version: 3
     * 
     * Expected response codes
     * 200 OK
     * 
     * @param componentVersionIds
     *            The set of component version ids. Must be not null, not empty, and less than or equal to 1000 ids.
     * @param activitySince
     *            The activity since date. Must be not null.
     * @return Returns the component version activity result.
     */
    Result<ListHolder<ComponentVersionActivity>> findComponentVersionLicenseActivities(Set<UUID> componentVersionIds, OffsetDateTime activitySince);

    /**
     * Finds component version CVE vulnerability activities.
     * 
     * Determines if one or more of the provided component versions has an associated update for component version CVE
     * vulnerabilities since the given timestamp. Useful for identifying added or removed CVE vulnerabilities for a
     * component version.
     * 
     * Migrated and soft deleted component version ids will always return an associated activity regardless of provided
     * timestamp.
     * 
     * Version: 3
     * 
     * Expected response codes
     * 200 OK
     * 
     * @param componentVersionIds
     *            The set of component version ids. Must be not null, not empty, and less than or equal to 1000 ids.
     * @param activitySince
     *            The activity since date. Must be not null.
     * @return Returns the component version activity result.
     */
    Result<ListHolder<ComponentVersionActivity>> findComponentVersionCveVulnerabilityActivities(Set<UUID> componentVersionIds, OffsetDateTime activitySince);

    /**
     * Finds component version BDSA vulnerability activities.
     * 
     * Determines if one or more of the provided component versions has an associated update for component version BDSA
     * vulnerabilities since the given timestamp. Useful for identifying added or removed BDSA vulnerabilities for a
     * component version.
     * 
     * Migrated and soft deleted component version ids will always return an associated activity regardless of provided
     * timestamp.
     * 
     * Version: 3
     * 
     * Expected response codes
     * 200 OK
     * 402 Payment Required
     * 403 Forbidden
     * 
     * @param componentVersionIds
     *            The set of component version ids. Must be not null, not empty, and less than or equal to 1000 ids.
     * @param activitySince
     *            The activity since date. Must be not null.
     * @return Returns the component version activity result.
     */
    Result<ListHolder<ComponentVersionActivity>> findComponentVersionBdsaVulnerabilityActivities(Set<UUID> componentVersionIds, OffsetDateTime activitySince);

    /**
     * Finds component version upgrade guidance activities.
     * 
     * Determines if one or more of the provided component versions has an associated update for component version
     * upgrade guidance since the given timestamp.
     * 
     * Migrated and soft deleted component version ids will always return an associated activity regardless of provided
     * timestamp.
     * 
     * Version: 3
     * 
     * Expected response codes
     * 200 OK
     * 
     * @param componentVersionIds
     *            The set of component version ids. Must be not null, not empty, and less than or equal to 1000 ids.
     * @param activitySince
     *            The activity since date. Must be not null.
     * @return Returns the component version activity result.
     */
    Result<ListHolder<ComponentVersionActivity>> findComponentVersionUpgradeGuidanceActivities(Set<UUID> componentVersionIds, OffsetDateTime activitySince);

    /**
     * Finds component variant activities.
     * 
     * Determines if one or more of the provided component variants has an associated update for component variant
     * metadata since the given timestamp.
     * 
     * Soft deleted component variant ids will always return an associated activity regardless of provided timestamp.
     * 
     * Version: 3
     * 
     * Expected response codes
     * 200 OK
     * 
     * @param componentVariantIds
     *            The set of component variant ids. Must be not null, not empty, and less than or equal to 1000 ids.
     * @param activitySince
     *            The activity since date. Must be not null.
     * @return Returns the component variant activity result.
     */
    Result<ListHolder<ComponentVariantActivity>> findComponentVariantActivities(Set<UUID> componentVariantIds, OffsetDateTime activitySince);

    /**
     * Finds component variant CVE vulnerability activities.
     * 
     * Determines if one or more of the provided component variants has an associated update for component variant CVE
     * vulnerabilities since the given timestamp. Useful for identifying added or removed CVE vulnerabilities for a
     * component variant.
     * 
     * Soft deleted component variant ids will always return an associated activity regardless of provided timestamp.
     * 
     * Version: 3
     * 
     * Expected response codes
     * 200 OK
     * 
     * @param componentVariantIds
     *            The set of component variant ids. Must be not null, not empty, and less than or equal to 1000 ids.
     * @param activitySince
     *            The activity since date. Must be not null.
     * @return Returns the component variant activity result.
     */
    Result<ListHolder<ComponentVariantActivity>> findComponentVariantCveVulnerabilityActivities(Set<UUID> componentVariantIds, OffsetDateTime activitySince);

    /**
     * Finds component variant BDSA vulnerability activities.
     * 
     * Determines if one or more of the provided component variants has an associated update for component variant BDSA
     * vulnerabilities since the given timestamp. Useful for identifying added or removed BDSA vulnerabilities for a
     * component variant.
     * 
     * Soft deleted component variant ids will always return an associated activity regardless of provided timestamp.
     * 
     * Version: 3
     * 
     * Expected response codes
     * 200 OK
     * 402 Payment Required
     * 403 Forbidden
     * 
     * @param componentVariantIds
     *            The set of component variant ids. Must be not null, not empty, and less than or equal to 1000 ids.
     * @param activitySince
     *            The activity since date. Must be not null.
     * @return Returns the component variant activity result.
     */
    Result<ListHolder<ComponentVariantActivity>> findComponentVariantBdsaVulnerabilityActivities(Set<UUID> componentVariantIds, OffsetDateTime activitySince);

    /**
     * Finds component variant upgrade guidance activities.
     * 
     * Determines if one or more of the provided component variants has an associated update for component variant
     * upgrade guidance since the given timestamp.
     * 
     * Soft deleted component variant ids will always return an associated activity regardless of provided timestamp.
     * 
     * Version: 3
     * 
     * Expected response codes
     * 200 OK
     * 
     * @param componentVariantIds
     *            The set of component variant ids. Must be not null, not empty, and less than or equal to 1000 ids.
     * @param activitySince
     *            The activity since date. Must be not null.
     * @return Returns the component variant activity result.
     */
    Result<ListHolder<ComponentVariantActivity>> findComponentVariantUpgradeGuidanceActivities(Set<UUID> componentVariantIds,
            OffsetDateTime activitySince);

    /**
     * Finds component variant transitive upgrade guidance activities.
     * 
     * Determines if one or more of the provided component variants has an associated update for component variant
     * transitive upgrade guidance since the given timestamp.
     * 
     * Soft deleted component variant ids will always return an associated activity regardless of provided timestamp.
     * 
     * Version: 3
     * 
     * Expected response codes
     * 200 OK
     * 
     * @param componentVariantIds
     *            The set of component variant ids. Must be not null, not empty, and less than or equal to 1000 ids.
     * @param activitySince
     *            The activity since date. Must be not null.
     * @return Returns the component variant activity result.
     */
    Result<ListHolder<ComponentVariantActivity>> findComponentVariantTransitiveUpgradeGuidanceActivities(Set<UUID> componentVariantIds,
            OffsetDateTime activitySince);

    /**
     * Finds license activities.
     * 
     * Determines if one or more of the provided licenses has an associated update for license metadata since the given
     * timestamp.
     * 
     * Version: 3
     * 
     * Expected response codes
     * 200 OK
     * 
     * @param licenseIds
     *            The set of license ids. Must be not null, not empty, and less than or equal to 1000 ids.
     * @param activitySince
     *            The activity since date. Must be not null.
     * @return Returns the license activity result.
     */
    Result<ListHolder<LicenseActivity>> findLicenseActivities(Set<UUID> licenseIds, OffsetDateTime activitySince);

    /**
     * Finds license's license term activities.
     * 
     * Determines if one or more of the provided licenses has an associated update for its license term metadata since
     * the given timestamp.
     * 
     * Version: 3
     * 
     * @param licenseIds
     *            The set of license ids. Must be not null, not empty, and less than or equal to 1000 ids.
     * @param activitySince
     *            The activity since date. Must be not null.
     * @return Returns the license activity result.
     */
    Result<ListHolder<LicenseActivity>> findLicenseLicenseTermActivities(Set<UUID> licenseIds, OffsetDateTime activitySince);

    /**
     * Finds CVE vulnerability activities.
     * 
     * Determines if one or more of the provided CVE vulnerabilities has an associated update for CVE vulnerability
     * metadata since the given timestamp.
     * 
     * Version: 3
     * 
     * Expected response codes
     * 200 OK
     * 
     * @param cveVulnerabilityIds
     *            The set of CVE vulnerability ids. Must be not null, not empty, and less than or equal to 1000 ids.
     * @param activitySince
     *            The activity since date. Must be not null.
     * @return Returns the CVE vulnerability activity result.
     */
    Result<ListHolder<CveVulnerabilityActivity>> findCveVulnerabilityActivities(Set<String> cveVulnerabilityIds, OffsetDateTime activitySince);

    /**
     * Finds BDSA vulnerability activities.
     * 
     * Determines if one or more of the provided BDSA vulnerabilities has an associated update for BDSA vulnerability
     * metadata since the given timestamp.
     * 
     * Version: 3
     * 
     * Expected response codes
     * 200 OK
     * 402 Payment Required
     * 403 Forbidden
     * 
     * @param bdsaVulnerabilityIds
     *            The set of BDSA vulnerability ids. Must be not null, not empty, and less than or equal to 1000 ids.
     * @param activitySince
     *            The activity since date. Must be not null.
     * @return Returns the BDSA vulnerability activity result.
     */
    Result<ListHolder<BdsaVulnerabilityActivity>> findBdsaVulnerabilityActivities(Set<String> bdsaVulnerabilityIds, OffsetDateTime activitySince);
}
