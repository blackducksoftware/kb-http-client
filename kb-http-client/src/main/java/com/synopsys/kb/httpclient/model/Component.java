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

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;
import com.synopsys.kb.httpclient.api.Relationship;

/**
 * Component representation.
 * 
 * A component can be migrated via merge or split migration mechanisms via the KnowledgeBase.
 * 
 * A component is eligible for soft deletion.
 * 
 * @author skatzman
 */
public class Component extends AbstractEntity {
    private final String name;

    private final String description;

    private final String primaryLanguage;

    private final Set<String> tags;

    private final Set<Logo> logos;

    private final boolean isDeleted;

    private final Meta meta;

    @JsonCreator
    public Component(@JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("primaryLanguage") String primaryLanguage,
            @JsonProperty("tags") Collection<String> tags,
            @JsonProperty("logos") Collection<Logo> logos,
            @JsonProperty("deleted") Boolean deleted,
            @JsonProperty("_meta") Meta meta) {
        this.name = name;
        this.description = description;
        this.primaryLanguage = primaryLanguage;
        this.tags = (tags != null) ? ImmutableSet.copyOf(tags) : ImmutableSet.of();
        this.logos = (logos != null) ? ImmutableSet.copyOf(logos) : ImmutableSet.of();
        this.isDeleted = (deleted != null) ? deleted.booleanValue() : false;
        this.meta = meta;
    }

    /**
     * Gets the id.
     *
     * @return UUID Returns the id.
     * @throws IllegalArgumentException
     *             Throws if the id is absent or not a valid UUID.
     */
    @JsonIgnore
    public final UUID getId() {
        Meta meta = getMeta();

        return meta.getHrefId("components")
                .map(UUID::fromString)
                .orElseThrow(() -> new IllegalArgumentException("Unable to get id because it is absent."));
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Optional<String> getPrimaryLanguage() {
        return Optional.ofNullable(primaryLanguage);
    }

    public Set<String> getTags() {
        return tags;
    }

    public Set<Logo> getLogos() {
        return logos;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public Meta getMeta() {
        return meta;
    }

    /**
     * Finds the component's homepage link.
     * 
     * @return Returns the homepage link if present and emptiness otherwise.
     */
    @JsonIgnore
    public Optional<Link> getHomepageLink() {
        return getMeta().findUniqueLink(Relationship.HOMEPAGE);
    }

    /**
     * Finds the component's project link.
     * 
     * @return Returns the project link if present and emptiness otherwise.
     */
    @JsonIgnore
    public Optional<Link> getProjectLink() {
        return getMeta().findUniqueLink(Relationship.PROJECT);
    }

    /**
     * Finds the component's ongoing version link.
     * 
     * @return Returns the component's ongoing version link if present and emptiness otherwise.
     */
    @JsonIgnore
    public Optional<Link> getOngoingVersionLink() {
        return getMeta().findUniqueLink(Relationship.ONGOING_VERSIONS);
    }

    /**
     * Finds the component's OpenHub link.
     * 
     * @return Returns the OpenHub link if present and emptiness otherwise.
     */
    @JsonIgnore
    public Optional<Link> getOpenHubLink() {
        return getMeta().findUniqueLink(Relationship.OPEN_HUB);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDescription(), getPrimaryLanguage(), getTags(), getLogos(), isDeleted(), getMeta());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof Component) {
            Component otherComponent = (Component) otherObject;

            return Objects.equals(getName(), otherComponent.getName())
                    && Objects.equals(getDescription(), otherComponent.getDescription())
                    && Objects.equals(getPrimaryLanguage(), otherComponent.getPrimaryLanguage())
                    && Objects.equals(getTags(), otherComponent.getTags())
                    && Objects.equals(getLogos(), otherComponent.getLogos())
                    && Objects.equals(isDeleted(), otherComponent.isDeleted())
                    && Objects.equals(getMeta(), otherComponent.getMeta());
        }

        return false;
    }
}

/*
 * MIGRATIONS
 * 
 * 
 * {
 * "_meta": {
 * "href": "https://kbtest.blackducksoftware.com/api/components/ba35fed7-b8a2-4d09-80c3-ce8fca1e2827",
 * "links": [
 * {
 * "rel": "moved",
 * "href": "https://kbtest.blackducksoftware.com/api/components/4405258f-3f38-4905-bb26-ada6b57eeac5"
 * },
 * {
 * "rel": "moved",
 * "href": "https://kbtest.blackducksoftware.com/api/components/dea06a84-d6f2-41ca-b712-aee45fe918fd"
 * },
 * {
 * "rel": "moved",
 * "href": "https://kbtest.blackducksoftware.com/api/components/edf3fe9d-f9c9-4871-a01a-bf2febe95fa5"
 * }
 * ]
 * }
 * }
 */
