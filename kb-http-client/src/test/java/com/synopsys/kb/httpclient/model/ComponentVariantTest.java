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

/**
 * Component variant test.
 * 
 * @author skatzman
 */
public class ComponentVariantTest extends AbstractTest {
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
            List.of(new LicenseDefinitionItem("https://kbtest.blackducksoftware.com/api/licenses/" + UUID.randomUUID(), null)));

    private static final Boolean DELETED = Boolean.FALSE;

    private static final Boolean COMPONENT_INTELLIGENCE = Boolean.FALSE;

    private static final String HREF = "https://kbtest.blackducksoftware.com/api/variants/" + ID;

    private static final Meta META = new Meta(HREF,
            List.of(new Link("component", "https://kbtest.blackducksoftware.com/api/components/" + COMPONENT_ID),
                    new Link("version", "https://kbtest.blackducksoftware.com/api/versions/" + COMPONENT_VERSION_ID)));

    @Test
    public void testConstructor() {
        ComponentVariant componentVariant = new ComponentVariant(VERSION, EXTERNAL_NAMESPACE, EXTERNAL_ID, EXTERNAL_NAMESPACE_DISTRIBUTION, PACKAGE_URL, TYPE,
                LICENSE_DEFINITION, DELETED, COMPONENT_INTELLIGENCE, META);

        Assert.assertEquals(componentVariant.getVersion(), VERSION, "Versions should be equal.");
        Assert.assertEquals(componentVariant.getExternalNamespace().orElse(null), EXTERNAL_NAMESPACE, "External namespaces should be equal.");
        Assert.assertEquals(componentVariant.getExternalId().orElse(null), EXTERNAL_ID, "External ids should be equal.");
        Assert.assertFalse(componentVariant.isExternalNamespaceDistribution(), "External namespace should not be a distribution.");
        Assert.assertEquals(componentVariant.getPackageUrl().orElse(null), PACKAGE_URL, "Package URLs should be equal.");
        Assert.assertEquals(componentVariant.getType(), TYPE, "Types should be equal.");
        Assert.assertEquals(componentVariant.getLicenseDefinition().orElse(null), LICENSE_DEFINITION, "License definitions should be equal.");
        Assert.assertFalse(componentVariant.isDeleted(), "Component variant should not be deleted.");
        Assert.assertFalse(componentVariant.isComponentIntelligencePresent(), "Component intelligence should not be present.");
        Assert.assertEquals(componentVariant.getMeta(), META, "Metas should be equal.");

        Assert.assertEquals(componentVariant.getId(), ID, "Ids should be equal.");
        Assert.assertEquals(componentVariant.getComponentId(), COMPONENT_ID, "Component ids should be equal.");
        Assert.assertEquals(componentVariant.getComponentVersionId(), COMPONENT_VERSION_ID, "Component version ids should be equal.");
    }

    @Test
    public void testDeserialization() throws JsonProcessingException {
        ComponentVariant componentVariant = new ComponentVariant(VERSION, EXTERNAL_NAMESPACE, EXTERNAL_ID, EXTERNAL_NAMESPACE_DISTRIBUTION, PACKAGE_URL, TYPE,
                LICENSE_DEFINITION, DELETED, COMPONENT_INTELLIGENCE, META);

        String json = serialize(componentVariant);
        ComponentVariant result = deserialize(json, ComponentVariant.class);

        Assert.assertEquals(result.getVersion(), VERSION, "Versions should be equal.");
        Assert.assertEquals(result.getExternalNamespace().orElse(null), EXTERNAL_NAMESPACE, "External namespaces should be equal.");
        Assert.assertEquals(result.getExternalId().orElse(null), EXTERNAL_ID, "External ids should be equal.");
        Assert.assertFalse(result.isExternalNamespaceDistribution(), "External namespace should not be a distribution.");
        Assert.assertEquals(result.getPackageUrl().orElse(null), PACKAGE_URL, "Package URLs should be equal.");
        Assert.assertEquals(result.getType(), TYPE, "Types should be equal.");
        Assert.assertEquals(result.getLicenseDefinition().orElse(null), LICENSE_DEFINITION, "License definitions should be equal.");
        Assert.assertFalse(result.isDeleted(), "Component variant should not be deleted.");
        Assert.assertFalse(result.isComponentIntelligencePresent(), "Component intelligence should not be present.");
        Assert.assertEquals(result.getMeta(), META, "Metas should be equal.");
    }
}
