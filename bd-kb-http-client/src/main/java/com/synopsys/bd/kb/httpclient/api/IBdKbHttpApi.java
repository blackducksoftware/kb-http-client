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
package com.synopsys.bd.kb.httpclient.api;

import com.synopsys.bd.kb.httpclient.impl.BdComponentFinder;
import com.synopsys.bd.kb.httpclient.impl.BdLicenseDefinitionFinder;
import com.synopsys.bd.kb.httpclient.impl.BdVulnerabilityMerger;
import com.synopsys.kb.httpclient.api.IKbHttpApi;

/**
 * Black Duck-centric KB HTTP API interface.
 * 
 * @author skatzman
 */
public interface IBdKbHttpApi {
    /**
     * Gets the Black Duck-centric component API.
     * 
     * Contains a subset of total KB API component operations that require Black Duck-centric conversion.
     * 
     * @return Returns the Black Duck-centric component API.
     */
    IBdComponentApi getBdComponentApi();

    /**
     * Gets the Black Duck-centric component version API.
     * 
     * Contains a subset of total KB API component version operations that require Black Duck-centric conversion.
     * 
     * @return Returns the Black Duck-centric component version API.
     */
    IBdComponentVersionApi getBdComponentVersionApi();

    /**
     * Gets the Black Duck-centric component variant API.
     * 
     * Contains a subset of total KB API component variant operations that require Black Duck-centric conversion.
     * 
     * @return Returns the Black Duck-centric component variant API.
     */
    IBdComponentVariantApi getBdComponentVariantApi();

    /**
     * Gets the Black Duck-centric license API.
     * 
     * Contains a subset of total KB API license operations that require Black Duck-centric conversion.
     * 
     * @return Returns the Black Duck-centric license API.
     */
    IBdLicenseApi getBdLicenseApi();

    /**
     * Gets the Black Duck-centric component finder.
     * 
     * The Black Duck-centric component finder contains utility methods for finding hierarchies of component version or
     * component variant entities. This is useful for finding tuples of related entities and managing migration handling
     * consistency across related entities.
     * 
     * @return Returns the Black Duck-centric component finder.
     */
    BdComponentFinder getBdComponentFinder();

    /**
     * Gets the Black Duck-centric license definition finder.
     * 
     * The Black Duck-centric license definition finder contains utility methods for finding license definitions with
     * complete license metadata.
     * 
     * @return Returns the Black Duck-centric license definition finder.
     */
    BdLicenseDefinitionFinder getBdLicenseDefinitionFinder();

    /**
     * Gets the Black Duck-centric vulnerability merger.
     * 
     * The Black Duck-centric vulnerability merger contains utility methods for merging collections of NVD and BDSA
     * vulnerabilities together (presumably for the same component version or component variant).
     * 
     * @return Returns the Black Duck-centric vulnerability merger.
     */
    BdVulnerabilityMerger getBdVulnerabilityMerger();

    /**
     * Gets the KnowledgeBase (KB) HTTP API.
     * 
     * Enables raw access to the suite of KB API operations.
     * 
     * @return Returns the KnowledgeBase (KB) HTTP API.
     */
    IKbHttpApi getKbHttpApi();
}
