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

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.synopsys.bd.kb.httpclient.AbstractBdTest;
import com.synopsys.kb.httpclient.model.License;
import com.synopsys.kb.httpclient.model.LicenseDefinitionType;

/**
 * Black Duck-centric license definition test.
 * 
 * @author skatzman
 */
public class BdLicenseDefinitionTest extends AbstractBdTest {
    private BdLicense bdLicense1;

    private BdLicense bdLicense2;

    private BdLicense bdLicense3;

    private BdLicense bdLicense4;

    @BeforeMethod
    public void beforeMethod() {
        License license1 = constructLicense(UUID.randomUUID(), "License1");
        this.bdLicense1 = new BdLicense(license1);

        License license2 = constructLicense(UUID.randomUUID(), "License2");
        this.bdLicense2 = new BdLicense(license2);

        License license3 = constructLicense(UUID.randomUUID(), "License3");
        this.bdLicense3 = new BdLicense(license3);

        License license4 = constructLicense(UUID.randomUUID(), "License4");
        this.bdLicense4 = new BdLicense(license4);
    }

    @Test
    public void testConstructorForSingleLicense() {
        BdLicenseDefinition licenseDefinition = new BdLicenseDefinition(bdLicense1);

        Assert.assertEquals(licenseDefinition.getLicense().orElse(null), bdLicense1, "Licenses should be equal.");
        Assert.assertFalse(licenseDefinition.getType().isPresent(), "Type should not be present.");
        Assert.assertTrue(licenseDefinition.getLicenseDefinitions().isEmpty(), "License definitions should be empty.");
    }

    @Test
    public void testConstructorForMultipleLicenses() {
        BdLicenseDefinition licenseDefinition1a = new BdLicenseDefinition(bdLicense1);
        BdLicenseDefinition licenseDefinition1b = new BdLicenseDefinition(bdLicense2);
        List<BdLicenseDefinition> licenseDefinitions = List.of(licenseDefinition1a, licenseDefinition1b);
        BdLicenseDefinition licenseDefinition = new BdLicenseDefinition(LicenseDefinitionType.CONJUNCTIVE, licenseDefinitions);

        Assert.assertFalse(licenseDefinition.getLicense().isPresent(), "License should not be present.");
        Assert.assertEquals(licenseDefinition.getType().orElse(null), LicenseDefinitionType.CONJUNCTIVE, "Types should be equal.");
        Assert.assertEquals(licenseDefinition.getLicenseDefinitions(), licenseDefinitions, "License definitions should be equal.");
    }

    @Test
    public void testGetLicenseIdsForSingleLicense() {
        BdLicenseDefinition licenseDefinition = new BdLicenseDefinition(bdLicense1);

        Set<UUID> licenseIds = licenseDefinition.getLicenseIds();

        Assert.assertEquals(licenseIds.size(), 1, "Number of license ids should be equal.");
        Assert.assertTrue(licenseIds.contains(bdLicense1.getId()), "License id should be present.");
    }

    @Test
    public void testGetLicenseIdsForMultipleLicenses() {
        BdLicenseDefinition licenseDefinition1a = new BdLicenseDefinition(bdLicense1);
        BdLicenseDefinition licenseDefinition1b = new BdLicenseDefinition(bdLicense2);
        List<BdLicenseDefinition> licenseDefinitions1 = List.of(licenseDefinition1a, licenseDefinition1b);
        BdLicenseDefinition licenseDefinition = new BdLicenseDefinition(LicenseDefinitionType.CONJUNCTIVE, licenseDefinitions1);

        Set<UUID> licenseIds = licenseDefinition.getLicenseIds();

        Assert.assertEquals(licenseIds.size(), 2, "Number of license ids should be equal.");
        Assert.assertTrue(licenseIds.contains(bdLicense1.getId()), "License id should be present.");
        Assert.assertTrue(licenseIds.contains(bdLicense2.getId()), "License id should be present.");
    }

    @Test
    public void testGetLicenseIdsForMultipleLicensesWithDepth() {
        BdLicenseDefinition licenseDefinition1a = new BdLicenseDefinition(bdLicense1);
        BdLicenseDefinition licenseDefinition1b = new BdLicenseDefinition(bdLicense2);
        List<BdLicenseDefinition> licenseDefinitions1 = List.of(licenseDefinition1a, licenseDefinition1b);
        BdLicenseDefinition licenseDefinition1 = new BdLicenseDefinition(LicenseDefinitionType.CONJUNCTIVE, licenseDefinitions1);

        BdLicenseDefinition licenseDefinition2a = new BdLicenseDefinition(bdLicense3);
        BdLicenseDefinition licenseDefinition2b = new BdLicenseDefinition(bdLicense4);
        List<BdLicenseDefinition> licenseDefinitions2 = List.of(licenseDefinition2a, licenseDefinition2b);
        BdLicenseDefinition licenseDefinition2 = new BdLicenseDefinition(LicenseDefinitionType.CONJUNCTIVE, licenseDefinitions2);

        List<BdLicenseDefinition> licenseDefinitions = List.of(licenseDefinition1, licenseDefinition2);
        BdLicenseDefinition licenseDefinition = new BdLicenseDefinition(LicenseDefinitionType.DISJUNCTIVE, licenseDefinitions);

        Set<UUID> licenseIds = licenseDefinition.getLicenseIds();

        Assert.assertEquals(licenseIds.size(), 4, "Number of license ids should be equal.");
        Assert.assertTrue(licenseIds.contains(bdLicense1.getId()), "License id should be present.");
        Assert.assertTrue(licenseIds.contains(bdLicense2.getId()), "License id should be present.");
        Assert.assertTrue(licenseIds.contains(bdLicense3.getId()), "License id should be present.");
        Assert.assertTrue(licenseIds.contains(bdLicense4.getId()), "License id should be present.");
    }

    @Test
    public void testGetLicenseIdsForMultipleLicensesWithDepthAndDuplicates() {
        BdLicenseDefinition licenseDefinition1a = new BdLicenseDefinition(bdLicense1);
        BdLicenseDefinition licenseDefinition1b = new BdLicenseDefinition(bdLicense2);
        List<BdLicenseDefinition> licenseDefinitions1 = List.of(licenseDefinition1a, licenseDefinition1b);
        BdLicenseDefinition licenseDefinition1 = new BdLicenseDefinition(LicenseDefinitionType.CONJUNCTIVE, licenseDefinitions1);

        BdLicenseDefinition licenseDefinition2a = new BdLicenseDefinition(bdLicense3);
        BdLicenseDefinition licenseDefinition2b = new BdLicenseDefinition(bdLicense1); // duplicate
        List<BdLicenseDefinition> licenseDefinitions2 = List.of(licenseDefinition2a, licenseDefinition2b);
        BdLicenseDefinition licenseDefinition2 = new BdLicenseDefinition(LicenseDefinitionType.CONJUNCTIVE, licenseDefinitions2);

        List<BdLicenseDefinition> licenseDefinitions = List.of(licenseDefinition1, licenseDefinition2);
        BdLicenseDefinition licenseDefinition = new BdLicenseDefinition(LicenseDefinitionType.DISJUNCTIVE, licenseDefinitions);

        Set<UUID> licenseIds = licenseDefinition.getLicenseIds();

        Assert.assertEquals(licenseIds.size(), 3, "Number of license ids should be equal.");
        Assert.assertTrue(licenseIds.contains(bdLicense1.getId()), "License id should be present.");
        Assert.assertTrue(licenseIds.contains(bdLicense2.getId()), "License id should be present.");
        Assert.assertTrue(licenseIds.contains(bdLicense3.getId()), "License id should be present.");
    }
}
