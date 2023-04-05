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

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synopsys.kb.httpclient.AbstractTest;

/**
 * Component test.
 * 
 * @author skatzman
 */
public class ComponentTest extends AbstractTest {
    private static final UUID ID = UUID.randomUUID();

    private static final String NAME = "FooComponent";

    private static final String DESCRIPTION = "This is a component description.";

    private static final String PRIMARY_LANGUAGE = "Java";

    private static final Set<String> TAGS = Set.of("kb", "http", "client");

    private static final Set<Logo> LOGOS = Set.of(new Logo("small", "http://www.small.com"),
            new Logo("medium", "http://www.medium.com"),
            new Logo("large", "http://www.large.com"));

    private static final Boolean DELETED = Boolean.FALSE;

    private static final String HREF = BASE_HREF + "/api/components/" + ID;

    private static final Meta META = new Meta(HREF, Collections.emptyList());

    @Test
    public void testConstructor() {
        Component component = new Component(NAME, DESCRIPTION, PRIMARY_LANGUAGE, TAGS, LOGOS, DELETED, META);

        Assert.assertEquals(component.getName(), NAME, "Names should be equal.");
        Assert.assertEquals(component.getDescription(), DESCRIPTION, "Descriptions should be equal.");
        Assert.assertEquals(component.getPrimaryLanguage().orElse(null), PRIMARY_LANGUAGE, "Primary languages should be equal.");
        Assert.assertEquals(component.getTags(), TAGS, "Tags should be equal.");
        Assert.assertEquals(component.getLogos(), LOGOS, "Logos should be equal.");
        Assert.assertFalse(component.isDeleted(), "Component should not be deleted.");
        Assert.assertEquals(component.getMeta(), META, "Metas should be equal.");

        Assert.assertEquals(component.getId(), ID, "Ids should be equal.");
    }

    @Test
    public void testGetHomepageLinkWhenAbsent() {
        Component component = new Component(NAME, DESCRIPTION, PRIMARY_LANGUAGE, TAGS, LOGOS, DELETED, META);

        Assert.assertFalse(component.getHomepageLink().isPresent(), "Homepage link should not be present.");
    }

    @Test
    public void testGetHomepageLinkWhenPresent() {
        Link homepageLink = new Link("homepage", "https://www.homepage.com");
        Meta meta = new Meta(DESCRIPTION, List.of(homepageLink));
        Component component = new Component(NAME, DESCRIPTION, PRIMARY_LANGUAGE, TAGS, LOGOS, DELETED, meta);

        Assert.assertEquals(component.getHomepageLink().orElse(null), homepageLink, "Homepage links should be equal.");
    }

    @Test
    public void testGetProjectLinkWhenAbsent() {
        Component component = new Component(NAME, DESCRIPTION, PRIMARY_LANGUAGE, TAGS, LOGOS, DELETED, META);

        Assert.assertFalse(component.getProjectLink().isPresent(), "Project link should not be present.");
    }

    @Test
    public void testGetProjectLinkWhenPresent() {
        Link projectLink = new Link("project", "https://www.project.com");
        Meta meta = new Meta(DESCRIPTION, List.of(projectLink));
        Component component = new Component(NAME, DESCRIPTION, PRIMARY_LANGUAGE, TAGS, LOGOS, DELETED, meta);

        Assert.assertEquals(component.getProjectLink().orElse(null), projectLink, "Project links should be equal.");
    }

    @Test
    public void testGetOngoingVersionLinkWhenAbsent() {
        Component component = new Component(NAME, DESCRIPTION, PRIMARY_LANGUAGE, TAGS, LOGOS, DELETED, META);

        Assert.assertFalse(component.getOngoingVersionLink().isPresent(), "Ongoing version should not be present.");
    }

    @Test
    public void testGetOngoingVersionLinkWhenPresent() {
        Link ongoingVersionLink = new Link("ongoing-versions", HREF + "/ongoing-version");
        Meta meta = new Meta(DESCRIPTION, List.of(ongoingVersionLink));
        Component component = new Component(NAME, DESCRIPTION, PRIMARY_LANGUAGE, TAGS, LOGOS, DELETED, meta);

        Assert.assertEquals(component.getOngoingVersionLink().orElse(null), ongoingVersionLink, "Ongoiong version links should be equal.");
    }

    @Test
    public void testGetOpenHubLinkWhenAbsent() {
        Component component = new Component(NAME, DESCRIPTION, PRIMARY_LANGUAGE, TAGS, LOGOS, DELETED, META);

        Assert.assertFalse(component.getOpenHubLink().isPresent(), "OpenHubs link should not be present.");
    }

    @Test
    public void testGetOpenHubLinkWhenPresent() {
        Link openHubLink = new Link("openHub", "https://www.openhub.com");
        Meta meta = new Meta(DESCRIPTION, List.of(openHubLink));
        Component component = new Component(NAME, DESCRIPTION, PRIMARY_LANGUAGE, TAGS, LOGOS, DELETED, meta);

        Assert.assertEquals(component.getOpenHubLink().orElse(null), openHubLink, "OpenHub links should be equal.");
    }

    @Test
    public void testDeserialization() throws JsonProcessingException {
        Component component = new Component(NAME, DESCRIPTION, PRIMARY_LANGUAGE, TAGS, LOGOS, DELETED, META);

        String json = serialize(component);
        Component result = deserialize(json, Component.class);

        Assert.assertEquals(result.getName(), NAME, "Names should be equal.");
        Assert.assertEquals(result.getDescription(), DESCRIPTION, "Descriptions should be equal.");
        Assert.assertEquals(result.getPrimaryLanguage().orElse(null), PRIMARY_LANGUAGE, "Primary languages should be equal.");
        Assert.assertEquals(result.getTags(), TAGS, "Tags should be equal.");
        Assert.assertEquals(result.getLogos(), LOGOS, "Logos should be equal.");
        Assert.assertFalse(result.isDeleted(), "Component should not be deleted.");
        Assert.assertEquals(result.getMeta(), META, "Metas should be equal.");
    }
}
