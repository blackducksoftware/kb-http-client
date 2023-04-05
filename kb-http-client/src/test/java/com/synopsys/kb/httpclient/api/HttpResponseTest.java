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

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import org.apache.hc.core5.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.synopsys.kb.httpclient.AbstractTest;
import com.synopsys.kb.httpclient.model.Link;
import com.synopsys.kb.httpclient.model.Meta;

/**
 * HTTP response test.
 * 
 * @author skatzman
 */
public class HttpResponseTest extends AbstractTest {
    private static final Set<Integer> EXPECTED_CODES = Set.of(HttpStatus.SC_OK, HttpStatus.SC_NOT_FOUND);

    private static final String MESSAGE_BODY = "This is a value.";

    private static final Meta MERGE_MIGRATED_META = new Meta(BASE_HREF + "/api/components/" + UUID.randomUUID(),
            List.of(new Link("moved", BASE_HREF + "/api/components/" + UUID.randomUUID())));

    private static final Meta SPLIT_MIGRATED_META = new Meta(BASE_HREF + "/api/components/" + UUID.randomUUID(),
            List.of(new Link("moved", BASE_HREF + "/api/components/" + UUID.randomUUID()),
                    new Link("moved", BASE_HREF + "/api/components/" + UUID.randomUUID()),
                    new Link("moved", BASE_HREF + "/api/components/" + UUID.randomUUID())));

    @Test
    public void testConstructor() {
        HttpResponse<String> httpResponse = new HttpResponse<>(HttpStatus.SC_OK, EXPECTED_CODES, MESSAGE_BODY, null);

        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_OK, "Codes should be equal.");
        Assert.assertEquals(httpResponse.getExpectedCodes(), EXPECTED_CODES, "Expected codes should be equal.");
        Assert.assertTrue(httpResponse.isMessageBodyPresent(), "Message body should be present.");
        Assert.assertEquals(httpResponse.getMessageBody().orElse(null), MESSAGE_BODY, "Message bodies should be equal.");
        Assert.assertFalse(httpResponse.isMigratedMetaPresent(), "Migrated meta should not be present.");
    }

    @Test
    public void testConstructorForMigration() {
        HttpResponse<String> httpResponse = new HttpResponse<>(HttpStatus.SC_MULTIPLE_CHOICES, EXPECTED_CODES, null, SPLIT_MIGRATED_META);

        Assert.assertEquals(httpResponse.getCode(), HttpStatus.SC_MULTIPLE_CHOICES, "Codes should be equal.");
        Assert.assertEquals(httpResponse.getExpectedCodes(), EXPECTED_CODES, "Expected codes should be equal.");
        Assert.assertFalse(httpResponse.isMessageBodyPresent(), "Message body should not be present.");
        Assert.assertTrue(httpResponse.isMigratedMetaPresent(), "Migrated meta should be present.");
        Assert.assertEquals(httpResponse.getMigratedMeta().orElse(null), SPLIT_MIGRATED_META, "Migrated metas should be equal.");
    }

    @Test
    public void testGetMessageBodyOrElseThrowWhenExpectedCode() {
        HttpResponse<String> httpResponse = new HttpResponse<>(HttpStatus.SC_OK, EXPECTED_CODES, MESSAGE_BODY, null);

        Supplier<RuntimeException> exceptionSupplier = () -> new RuntimeException("This is a runtime exception.");
        String messageBody = httpResponse.getMessageBodyOrElseThrow(exceptionSupplier).orElse(null);

        Assert.assertEquals(messageBody, MESSAGE_BODY, "Message bodies should be equal.");
    }

    @Test(expectedExceptions = { RuntimeException.class })
    public void testGetMessageBodyOrElseThrowWhenNotExpectedCode() {
        HttpResponse<String> httpResponse = new HttpResponse<>(HttpStatus.SC_UNAUTHORIZED, EXPECTED_CODES, MESSAGE_BODY, null);

        Supplier<RuntimeException> exceptionSupplier = () -> new RuntimeException("This is a runtime exception.");
        httpResponse.getMessageBodyOrElseThrow(exceptionSupplier).orElse(null);
    }

    @Test
    public void testIsMigratedWithMigratedMetaAndWithoutMovedLinks() {
        HttpResponse<String> httpResponse = new HttpResponse<>(HttpStatus.SC_OK, EXPECTED_CODES, MESSAGE_BODY, null);

        Assert.assertFalse(httpResponse.isMigrated(), "Entity should not be migrated.");
    }

    @Test
    public void testIsMigratedWithMigratedMetaAndSingleMovedLink() {
        HttpResponse<String> httpResponse = new HttpResponse<>(HttpStatus.SC_MULTIPLE_CHOICES, EXPECTED_CODES, null, MERGE_MIGRATED_META);

        Assert.assertTrue(httpResponse.isMigrated(), "Entity should be migrated.");
    }

    @Test
    public void testIsMigratedWithMigratedMetaAndMultipleMovedLinks() {
        HttpResponse<String> httpResponse = new HttpResponse<>(HttpStatus.SC_MULTIPLE_CHOICES, EXPECTED_CODES, null, SPLIT_MIGRATED_META);

        Assert.assertTrue(httpResponse.isMigrated(), "Entity should be migrated.");
    }

    @Test
    public void testIsMergeMigratedWithMigratedMetaAndWithoutMovedLinks() {
        HttpResponse<String> httpResponse = new HttpResponse<>(HttpStatus.SC_OK, EXPECTED_CODES, MESSAGE_BODY, null);

        Assert.assertFalse(httpResponse.isMergeMigrated(), "Entity should not be merge migrated.");
    }

    @Test
    public void testIsMergeMigratedWithMigratedMetaAndSingleMovedLink() {
        HttpResponse<String> httpResponse = new HttpResponse<>(HttpStatus.SC_MULTIPLE_CHOICES, EXPECTED_CODES, null, MERGE_MIGRATED_META);

        Assert.assertTrue(httpResponse.isMergeMigrated(), "Entity should be merge migrated.");
    }

    @Test
    public void testIsMergeMigratedWithMigratedMetaAndMultipleMovedLinks() {
        HttpResponse<String> httpResponse = new HttpResponse<>(HttpStatus.SC_MULTIPLE_CHOICES, EXPECTED_CODES, null, SPLIT_MIGRATED_META);

        Assert.assertFalse(httpResponse.isMergeMigrated(), "Entity should not be merge migrated.");
    }

    @Test
    public void testIsSplitMigratedWithMigratedMetaAndWithoutMovedLinks() {
        HttpResponse<String> httpResponse = new HttpResponse<>(HttpStatus.SC_OK, EXPECTED_CODES, MESSAGE_BODY, null);

        Assert.assertFalse(httpResponse.isSplitMigrated(), "Entity should not be split migrated.");
    }

    @Test
    public void testIsSplitMigratedWithMigratedMetaAndSingleMovedLink() {
        HttpResponse<String> httpResponse = new HttpResponse<>(HttpStatus.SC_MULTIPLE_CHOICES, EXPECTED_CODES, null, MERGE_MIGRATED_META);

        Assert.assertFalse(httpResponse.isSplitMigrated(), "Entity should not be split migrated.");
    }

    @Test
    public void testIsSplitMigratedWithMigratedMetaAndMultipleMovedLinks() {
        HttpResponse<String> httpResponse = new HttpResponse<>(HttpStatus.SC_MULTIPLE_CHOICES, EXPECTED_CODES, null, SPLIT_MIGRATED_META);

        Assert.assertTrue(httpResponse.isSplitMigrated(), "Entity should be split migrated.");
    }
}
