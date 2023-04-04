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
import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.synopsys.bd.kb.httpclient.AbstractBdTest;
import com.synopsys.kb.httpclient.model.LicenseDefinition;
import com.synopsys.kb.httpclient.model.LicenseDefinitionItem;
import com.synopsys.kb.httpclient.model.LicenseDefinitionType;
import com.synopsys.kb.httpclient.model.Link;
import com.synopsys.kb.httpclient.model.Meta;

/**
 * Black Duck-centric component variant test.
 * 
 * @author skatzman
 */
public class BdComponentVariantTest extends AbstractBdTest {
    private static final UUID ID = UUID.randomUUID();

    private static final UUID COMPONENT_ID = UUID.randomUUID();

    private static final UUID COMPONENT_VERSION_ID = UUID.randomUUID();

    private static final String VERSION = "0.3.27-4.fc24/x86_64";

    private static final String EXTERNAL_NAMESPACE = "fedora";

    private static final String EXTERNAL_ID = "GarminPlugin/0.3.27-4.fc24/x86_64";

    private static final Boolean EXTERNAL_NAMESPACE_DISTRIBUTION = Boolean.FALSE;

    private static final String PACKAGE_URL = "pkg:rpm/fedora/GarminPlugin@0.3.27-4.fc24?arch=x86_64";

    private static final String TYPE = "versioned_download";

    private static final LicenseDefinition LICENSE_DEFINITION = new LicenseDefinition(LicenseDefinitionType.CONJUNCTIVE,
            List.of(new LicenseDefinitionItem(BASE_HREF + "/api/licenses/" + UUID.randomUUID(), null)));

    private static final Boolean DELETED = Boolean.FALSE;

    private static final Boolean COMPONENT_INTELLIGENCE = Boolean.FALSE;

    private static final String HREF = BASE_HREF + "/api/variants/" + ID;

    private static final Meta META = new Meta(HREF,
            List.of(new Link("component", BASE_HREF + "/api/components/" + COMPONENT_ID),
                    new Link("version", BASE_HREF + "/api/versions/" + COMPONENT_VERSION_ID)));

    @Test
    public void testConstructor() {
        BdComponentVariant bdComponentVariant = new BdComponentVariant(VERSION, EXTERNAL_NAMESPACE, EXTERNAL_ID, EXTERNAL_NAMESPACE_DISTRIBUTION, PACKAGE_URL,
                TYPE, LICENSE_DEFINITION, DELETED, COMPONENT_INTELLIGENCE, META);

        Assert.assertEquals(bdComponentVariant.getVersion(), VERSION, "Versions should be equal.");
        Assert.assertEquals(bdComponentVariant.getExternalNamespace().orElse(null), EXTERNAL_NAMESPACE, "External namespaces should be equal.");
        Assert.assertEquals(bdComponentVariant.getExternalId().orElse(null), EXTERNAL_ID, "External ids should be equal.");
        Assert.assertFalse(bdComponentVariant.isExternalNamespaceDistribution(), "External namespace should not be a distribution.");
        Assert.assertEquals(bdComponentVariant.getPackageUrl().orElse(null), PACKAGE_URL, "Package URLs should be equal.");
        Assert.assertEquals(bdComponentVariant.getType(), TYPE, "Types should be equal.");
        Assert.assertEquals(bdComponentVariant.getLicenseDefinition().orElse(null), LICENSE_DEFINITION, "License definitions should be equal.");
        Assert.assertFalse(bdComponentVariant.isDeleted(), "Component variant should not be deleted.");
        Assert.assertFalse(bdComponentVariant.isComponentIntelligencePresent(), "Component intelligence should not be present.");
        Assert.assertEquals(bdComponentVariant.getMeta(), META, "Metas should be equal.");

        Assert.assertEquals(bdComponentVariant.getRequiredExternalNamespace(), EXTERNAL_NAMESPACE, "External namespaces should be equal.");
        Assert.assertEquals(bdComponentVariant.getRequiredExternalId(), EXTERNAL_ID, "External ids should be equal.");
        Assert.assertEquals(bdComponentVariant.getId(), ID, "Ids should be equal.");
        Assert.assertEquals(bdComponentVariant.getComponentId(), COMPONENT_ID, "Component ids should be equal.");
        Assert.assertEquals(bdComponentVariant.getComponentVersionId(), COMPONENT_VERSION_ID, "Component version ids should be equal.");
    }

    @Test
    public void testGetExternalNamespaceWhenAbsent() {
        BdComponentVariant bdComponentVariant = new BdComponentVariant(VERSION, null, EXTERNAL_ID, EXTERNAL_NAMESPACE_DISTRIBUTION, PACKAGE_URL, TYPE,
                LICENSE_DEFINITION, DELETED, COMPONENT_INTELLIGENCE, META);

        Assert.assertEquals(bdComponentVariant.getExternalNamespace().orElse(null), BdComponentVariant.DEFAULT_EXTERNAL_NAMESPACE,
                "External namespaces should be equal.");
        Assert.assertEquals(bdComponentVariant.getRequiredExternalNamespace(), BdComponentVariant.DEFAULT_EXTERNAL_NAMESPACE,
                "External namespaces should be equal.");
    }

    @Test
    public void testGetExternalIdWhenAbsent() {
        BdComponentVariant bdComponentVariant = new BdComponentVariant(VERSION, EXTERNAL_NAMESPACE, null, EXTERNAL_NAMESPACE_DISTRIBUTION, PACKAGE_URL, TYPE,
                LICENSE_DEFINITION, DELETED, COMPONENT_INTELLIGENCE, META);

        Assert.assertEquals(bdComponentVariant.getExternalId().orElse(null), BdComponentVariant.DEFAULT_EXTERNAL_ID, "External ids should be equal.");
        Assert.assertEquals(bdComponentVariant.getRequiredExternalId(), BdComponentVariant.DEFAULT_EXTERNAL_NAMESPACE, "External ids should be equal.");
    }
}
