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
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synopsys.kb.httpclient.AbstractTest;

/**
 * Meta test.
 * 
 * @author skatzman
 */
public class MetaTest extends AbstractTest {
    private static final UUID LICENSE_ID = UUID.randomUUID();

    private static final String HREF = BASE_HREF + "/api/licenses/" + LICENSE_ID;

    private static final List<Link> LINKS = List.of(new Link("text", HREF + "/text"),
            new Link("license-terms", HREF + "/license-terms"));

    @Test
    public void testConstructor() {
        Meta meta = new Meta(HREF, LINKS);

        Assert.assertEquals(meta.getHref().orElse(null), HREF, "HREFs should be equal.");
        Assert.assertEquals(meta.getLinks(), LINKS, "Links should be equal.");
    }

    @Test
    public void testGetHrefIdWhenAbsent() {
        Meta meta = new Meta(HREF, LINKS);

        Optional<String> hrefId = meta.getHrefId("components");

        Assert.assertFalse(hrefId.isPresent(), "HREF id should not be present.");
    }

    @Test
    public void testGetHrefIdWhenPresent() {
        Meta meta = new Meta(HREF, LINKS);

        String hrefId = meta.getHrefId("licenses").orElse(null);

        Assert.assertEquals(hrefId, LICENSE_ID.toString(), "HREF ids should be equal.");
    }

    @Test
    public void testFindNumberOfLinksWhenAbsent() {
        Link link = new Link("apples", BASE_HREF + "/api/apples/" + UUID.randomUUID());
        List<Link> links = List.of(link);
        Meta meta = new Meta(HREF, links);

        long numberOfLinks = meta.findNumberOfLinks("pears");

        Assert.assertEquals(numberOfLinks, 0L, "Number of links should be equal.");
    }

    @Test
    public void testFindNumberOfLinksWithSinglePresentLink() {
        Link link = new Link("apples", BASE_HREF + "/api/apples/" + UUID.randomUUID());
        List<Link> links = List.of(link);
        Meta meta = new Meta(HREF, links);

        long numberOfLinks = meta.findNumberOfLinks("apples");

        Assert.assertEquals(numberOfLinks, 1L, "Number of links should be equal.");
    }

    @Test
    public void testFindNumberOfLinksWithMultiplePresentLinks() {
        Link link1 = new Link("apples", BASE_HREF + "/api/apples/" + UUID.randomUUID());
        Link link2 = new Link("apples", BASE_HREF + "/api/apples/" + UUID.randomUUID());
        Link link3 = new Link("apples", BASE_HREF + "/api/apples/" + UUID.randomUUID());
        List<Link> links = List.of(link1, link2, link3);
        Meta meta = new Meta(HREF, links);

        long numberOfLinks = meta.findNumberOfLinks("apples");

        Assert.assertEquals(numberOfLinks, 3L, "Number of links should be equal.");
    }

    @Test
    public void testIsLinkPresentWhenAbsent() {
        Link link = new Link("apples", BASE_HREF + "/api/apples/" + UUID.randomUUID());
        List<Link> links = List.of(link);
        Meta meta = new Meta(HREF, links);

        Assert.assertFalse(meta.isLinkPresent("pears"), "Link should not be present.");
    }

    @Test
    public void testIsLinkPresentWithSinglePresentLink() {
        Link link = new Link("apples", BASE_HREF + "/api/apples/" + UUID.randomUUID());
        List<Link> links = List.of(link);
        Meta meta = new Meta(HREF, links);

        Assert.assertTrue(meta.isLinkPresent("apples"), "Link should be present.");
    }

    @Test
    public void testIsLinkPresentWithMultiplePresentLinks() {
        Link link1 = new Link("apples", BASE_HREF + "/api/apples/" + UUID.randomUUID());
        Link link2 = new Link("apples", BASE_HREF + "/api/apples/" + UUID.randomUUID());
        Link link3 = new Link("apples", BASE_HREF + "/api/apples/" + UUID.randomUUID());
        List<Link> links = List.of(link1, link2, link3);
        Meta meta = new Meta(HREF, links);

        Assert.assertTrue(meta.isLinkPresent("apples"), "Link should be present.");
    }

    @Test
    public void testFindLinksWhenAbsent() {
        Link link = new Link("apples", BASE_HREF + "/api/apples/" + UUID.randomUUID());
        List<Link> links = List.of(link);
        Meta meta = new Meta(HREF, links);

        List<Link> foundLinks = meta.findLinks("pears");

        Assert.assertTrue(foundLinks.isEmpty(), "Links should be empty.");
    }

    @Test
    public void testFindLinksWithSinglePresentLink() {
        Link link = new Link("apples", BASE_HREF + "/api/apples/" + UUID.randomUUID());
        List<Link> links = List.of(link);
        Meta meta = new Meta(HREF, links);

        List<Link> foundLinks = meta.findLinks("apples");

        Assert.assertEquals(foundLinks.size(), 1, "Number of links should be equal.");
        Assert.assertTrue(foundLinks.contains(link), "Link should be present.");
    }

    @Test
    public void testFindLinksWithMultiplePresentLinks() {
        Link link1 = new Link("apples", BASE_HREF + "/api/apples/" + UUID.randomUUID());
        Link link2 = new Link("apples", BASE_HREF + "/api/apples/" + UUID.randomUUID());
        Link link3 = new Link("apples", BASE_HREF + "/api/apples/" + UUID.randomUUID());
        List<Link> links = List.of(link1, link2, link3);
        Meta meta = new Meta(HREF, links);

        List<Link> foundLinks = meta.findLinks("apples");

        Assert.assertEquals(foundLinks.size(), 3, "Number of links should be equal.");
        Assert.assertTrue(foundLinks.contains(link1), "Link should be present.");
        Assert.assertTrue(foundLinks.contains(link2), "Link should be present.");
        Assert.assertTrue(foundLinks.contains(link3), "Link should be present.");
    }

    @Test
    public void testFindLinkIdsWhenAbsent() {
        Link link = new Link("apples", BASE_HREF + "/api/apples/" + UUID.randomUUID());
        List<Link> links = List.of(link);
        Meta meta = new Meta(HREF, links);

        Set<String> ids = meta.findLinkIds("pears", "pears");

        Assert.assertTrue(ids.isEmpty(), "Ids should be empty.");
    }

    @Test
    public void testFindLinkIdsWithSinglePresentLink() {
        UUID id = UUID.randomUUID();
        Link link = new Link("apples", BASE_HREF + "/api/apples/" + id);
        List<Link> links = List.of(link);
        Meta meta = new Meta(HREF, links);

        Set<String> ids = meta.findLinkIds("apples", "apples");

        Assert.assertEquals(ids.size(), 1, "Number of ids should be equal.");
        Assert.assertTrue(ids.contains(id.toString()), "Id should be present.");
    }

    @Test
    public void testFindLinkIdsWithMultiplePresentLinks() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID id3 = UUID.randomUUID();
        Link link1 = new Link("apples", BASE_HREF + "/api/apples/" + id1);
        Link link2 = new Link("apples", BASE_HREF + "/api/apples/" + id2);
        Link link3 = new Link("apples", BASE_HREF + "/api/apples/" + id3);
        List<Link> links = List.of(link1, link2, link3);
        Meta meta = new Meta(HREF, links);

        Set<String> ids = meta.findLinkIds("apples", "apples");

        Assert.assertEquals(ids.size(), 3, "Number of ids should be equal.");
        Assert.assertTrue(ids.contains(id1.toString()), "Id should be present.");
        Assert.assertTrue(ids.contains(id2.toString()), "Id should be present.");
        Assert.assertTrue(ids.contains(id3.toString()), "Id should be present.");
    }

    @Test
    public void testFindUniqueLinkWhenAbsent() {
        Link link = new Link("apples", BASE_HREF + "/api/apples/" + UUID.randomUUID());
        List<Link> links = List.of(link);
        Meta meta = new Meta(HREF, links);

        Optional<Link> uniqueLink = meta.findUniqueLink("pears");

        Assert.assertFalse(uniqueLink.isPresent(), "Unique link should not be present.");
    }

    @Test
    public void testFindUniqueLinkWithSinglePresentLink() {
        Link link = new Link("apples", BASE_HREF + "/api/apples/" + UUID.randomUUID());
        List<Link> links = List.of(link);
        Meta meta = new Meta(HREF, links);

        Link uniqueLink = meta.findUniqueLink("apples").orElse(null);

        Assert.assertEquals(uniqueLink, link, "Unique links should be equal.");
    }

    @Test
    public void testFindUniqueLinkWithMultiplePresentLinks() {
        Link link1 = new Link("apples", BASE_HREF + "/api/apples/" + UUID.randomUUID());
        Link link2 = new Link("apples", BASE_HREF + "/api/apples/" + UUID.randomUUID());
        Link link3 = new Link("apples", BASE_HREF + "/api/apples/" + UUID.randomUUID());
        List<Link> links = List.of(link1, link2, link3);
        Meta meta = new Meta(HREF, links);

        Optional<Link> uniqueLink = meta.findUniqueLink("apples");

        Assert.assertFalse(uniqueLink.isPresent(), "Unique link should not be present.");
    }

    @Test
    public void testFindUniqueLinkIdWhenAbsent() {
        Link link = new Link("apples", BASE_HREF + "/api/apples/" + UUID.randomUUID());
        List<Link> links = List.of(link);
        Meta meta = new Meta(HREF, links);

        Optional<String> optionalId = meta.findUniqueLinkId("pears", "pears");

        Assert.assertFalse(optionalId.isPresent(), "Id should not be present.");
    }

    @Test
    public void testFindUniqueLinkIdWithSinglePresentLink() {
        UUID id = UUID.randomUUID();
        Link link = new Link("apples", BASE_HREF + "/api/apples/" + id);
        List<Link> links = List.of(link);
        Meta meta = new Meta(HREF, links);

        Optional<String> optionalId = meta.findUniqueLinkId("apples", "apples");

        Assert.assertTrue(optionalId.isPresent(), "Id should be present.");
        Assert.assertEquals(optionalId.orElse(null), id.toString(), "Ids should be equal.");
    }

    @Test
    public void testFindUniqueLinkIdWithMultiplePresentLinks() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID id3 = UUID.randomUUID();
        Link link1 = new Link("apples", BASE_HREF + "/api/apples/" + id1);
        Link link2 = new Link("apples", BASE_HREF + "/api/apples/" + id2);
        Link link3 = new Link("apples", BASE_HREF + "/api/apples/" + id3);
        List<Link> links = List.of(link1, link2, link3);
        Meta meta = new Meta(HREF, links);

        Optional<String> optionalId = meta.findUniqueLinkId("apples", "apples");

        Assert.assertFalse(optionalId.isPresent(), "Id should not be present.");
    }

    @Test
    public void testDeserialization() throws JsonProcessingException {
        Meta meta = new Meta(HREF, LINKS);

        String json = serialize(meta);
        Meta result = deserialize(json, Meta.class);

        Assert.assertEquals(result.getHref().orElse(null), HREF, "HREFs should be equal.");
        Assert.assertEquals(result.getLinks(), LINKS, "Links should be equal.");
    }
}
