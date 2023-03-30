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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

/**
 * Meta representation.
 * 
 * @author skatzman
 */
public class Meta extends AbstractEntity {
    private final String href;

    private final List<Link> links;

    @JsonCreator
    public Meta(@JsonProperty("href") String href,
            @JsonProperty("links") List<Link> links) {
        super();

        this.href = href;
        this.links = (links != null) ? ImmutableList.copyOf(links) : ImmutableList.of();
    }

    public Optional<String> getHref() {
        return Optional.ofNullable(href);
    }

    /**
     * Finds the HREF's id using the path variable as a finding mechanism.
     *
     * @param pathVariable
     *            The path variable.
     * @return Returns the HREF id if present and absence otherwise.
     */
    public Optional<String> getHrefId(String pathVariable) {
        return extractId(getHref().orElse(null), pathVariable);
    }

    public List<Link> getLinks() {
        return links;
    }

    /**
     * Finds the number of links that match the given relationship.
     * 
     * @param rel
     *            The relationship.
     * @return Returns the number of links that match the given relationship.
     */
    public long findNumberOfLinks(String rel) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(rel), "Rel must not be null or empty.");

        return getLinks().stream().filter((link) -> {
            String linkRel = link.getRel();

            return rel.equals(linkRel);
        }).count();
    }

    /**
     * Determines if at least one link is present that matches the given relationship.
     * 
     * @param rel
     *            The relationship.
     * @return Returns true if a link is present and false otherwise.
     */
    public boolean isLinkPresent(String rel) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(rel), "Rel must not be null or empty.");

        return getLinks().stream().anyMatch((link) -> {
            String linkRel = link.getRel();

            return rel.equals(linkRel);
        });
    }

    /**
     * Finds zero-to-many links that match the given relationship.
     * 
     * @param rel
     *            The relationship.
     * @return Returns the links.
     */
    public List<Link> findLinks(String rel) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(rel), "Rel must not be null or empty.");

        return getLinks().stream().filter((link) -> {
            String linkRel = link.getRel();

            return rel.equals(linkRel);
        }).collect(Collectors.toList());
    }

    /**
     * Finds a unique link that matches the given relationship.
     * 
     * A unique link is present if there is one and only one link that matches the given relationship.
     * 
     * @param rel
     *            The relationship.
     * @return Returns the unique link if present and emptiness otherwise.
     */
    public Optional<Link> findUniqueLink(String rel) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(rel), "Rel must not be null or empty.");

        Link link = null;

        List<Link> links = findLinks(rel);
        if (1 == links.size()) {
            link = links.get(0);
        }

        return Optional.ofNullable(link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHref(), getLinks());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof Meta) {
            Meta otherMeta = (Meta) otherObject;

            return Objects.equals(getHref(), otherMeta.getHref())
                    && Objects.equals(getLinks(), otherMeta.getLinks());
        }

        return false;
    }
}
