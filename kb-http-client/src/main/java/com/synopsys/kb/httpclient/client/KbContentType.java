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
package com.synopsys.kb.httpclient.client;

/**
 * KB content types.
 * 
 * Package protected.
 * 
 * @author skatzman
 */
final class KbContentType {
    public static final String KB_ACTIVITY_V3_JSON = "application/vnd.blackducksoftware.kb.activity-3+json";

    public static final String KB_AUTHENTICATE_V1_JSON = "application/vnd.blackducksoftware.kb-authenticate-1+json";

    public static final String KB_COMPONENT_DETAILS_V3_JSON = "application/vnd.blackducksoftware.kb.component.details-3+json";

    public static final String KB_COMPONENT_DETAILS_V4_JSON = "application/vnd.blackducksoftware.kb.component.details-4+json";

    public static final String KB_LICENSE_TEXT_ORIGINAL_V1 = "application/vnd.blackducksoftware.license-text-original-1";

    public static final String KB_VULNERABILITY_V7_JSON = "application/vnd.blackducksoftware.kb.vulnerability-7+json";

    private KbContentType() {
    }
}
