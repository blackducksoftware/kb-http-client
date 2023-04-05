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

/**
 * Relationships.
 * 
 * @author skatzman
 */
public final class Relationship {
    // Designates a related BDSA vulnerability association.
    public static final String BDSA = "bdsa";

    // Designates a related child BDSA vulnerability association.
    public static final String CHILD_BDSA = "child-bdsa";

    // Designates a related child CVE vulnerability association.
    public static final String CHILD_CVE = "child-cve";

    // Designates the internal API for an entity's associated component.
    public static final String COMPONENT = "component";

    // Designates the internal API for a component version's crypto algorithms when available.
    public static final String CRYPTO_ALGORITMHS = "crypto-algorithms";

    // Designates a related CVE vulnerability association.
    public static final String CVE = "cve";

    // Designates the common weakness enumeration (CWE) value.
    public static final String CWE = "cwe";

    // Designates the external homepage URI for a given entity such as an OSS component.
    public static final String HOMEPAGE = "homepage";

    // Designates an internal migration of a source entity to a destination entity.
    public static final String MOVED = "moved";

    // Designates the external URI for the next content section.
    public static final String NEXT = "next";

    // Designates the internal API for a component's ongoing version when available. An ongoing version is
    // defined and sourced from OpenHub.
    public static final String ONGOING_VERSIONS = "ongoing-versions";

    // Designates the external OpenHub URI for a given OSS component.
    public static final String OPEN_HUB = "openHub";

    // Designates the external URI for the previous content section.
    public static final String PREV = "prev";

    // Designates the external project URI for a given entity such as an OSS component.
    public static final String PROJECT = "project";

    // Designates a related reference.
    public static final String RELATED = "related";

    // Designates the external release URI for a given entity such as an OSS component version.
    public static final String RELEASE = "release";

    // Designates the internal API for an entity's associated component version.
    public static final String VERSION = "version";

    private Relationship() {
    }
}
