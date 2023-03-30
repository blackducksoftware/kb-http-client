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

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.synopsys.kb.httpclient.model.LicenseDefinitionItem.LicenseDefinitionItemDeserializer;

/**
 * License definition item representation.
 * 
 * @author skatzman
 */
@JsonDeserialize(using = LicenseDefinitionItemDeserializer.class)
public class LicenseDefinitionItem extends AbstractEntity {
    private final String href;

    private final LicenseDefinition licenseDefinition;

    @JsonCreator
    public LicenseDefinitionItem(@JsonProperty("href") String href,
            @JsonProperty("license") LicenseDefinition licenseDefinition) {
        this.href = href;
        this.licenseDefinition = licenseDefinition;
    }

    public Optional<String> getHref() {
        return Optional.ofNullable(href);
    }

    @JsonIgnore
    public Optional<UUID> getLicenseId() {
        return extractId(href, "licenses").map(UUID::fromString);
    }

    @JsonIgnore
    public Optional<LicenseDefinition> getLicenseDefinition() {
        return Optional.ofNullable(licenseDefinition);
    }

    /**
     * Used for serialization.
     * 
     * @return Returns the license definition.
     */
    @Nullable
    public LicenseDefinition getLicense() {
        return getLicenseDefinition().orElse(null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHref(), getLicenseDefinition());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof LicenseDefinitionItem) {
            LicenseDefinitionItem otherLicenseDefinitionItem = (LicenseDefinitionItem) otherObject;

            return Objects.equals(getHref(), otherLicenseDefinitionItem.getHref())
                    && Objects.equals(getLicenseDefinition(), otherLicenseDefinitionItem.getLicenseDefinition());
        }

        return false;
    }

    /**
     * Custom deserializer for LicenseDefinitionItem in order to handle either relationship.
     *
     * @author skatzman
     */
    protected static class LicenseDefinitionItemDeserializer extends JsonDeserializer<LicenseDefinitionItem> {
        private static final String HREF = "href";

        private static final String LICENSE = "license";

        @Override
        public LicenseDefinitionItem deserialize(final JsonParser jsonParser, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
            String href = null;
            LicenseDefinition licenseDefinition = null;

            ObjectCodec codec = jsonParser.getCodec();
            TreeNode rootTreeNode = codec.readTree(jsonParser);

            TreeNode hrefTreeNode = rootTreeNode.get(HREF);
            if (hrefTreeNode instanceof TextNode) {
                TextNode hrefTextNode = (TextNode) hrefTreeNode;
                href = hrefTextNode.asText();
            }

            TreeNode licenseTreeNode = rootTreeNode.get(LICENSE);
            if (licenseTreeNode instanceof ObjectNode) {
                licenseDefinition = codec.treeToValue(licenseTreeNode, LicenseDefinition.class);
            }

            return new LicenseDefinitionItem(href, licenseDefinition);
        }
    }
}
