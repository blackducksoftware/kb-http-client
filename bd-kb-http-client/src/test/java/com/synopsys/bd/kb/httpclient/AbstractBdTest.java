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
package com.synopsys.bd.kb.httpclient;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import org.testng.Assert;

import com.synopsys.bd.kb.httpclient.api.MigratableHttpResponse;
import com.synopsys.bd.kb.httpclient.api.MigratableHttpResult;
import com.synopsys.kb.httpclient.api.HttpResponse;
import com.synopsys.kb.httpclient.api.HttpResult;
import com.synopsys.kb.httpclient.api.Relationship;
import com.synopsys.kb.httpclient.model.ActivityTrend;
import com.synopsys.kb.httpclient.model.BdsaVulnerability;
import com.synopsys.kb.httpclient.model.CodeBaseMaturity;
import com.synopsys.kb.httpclient.model.Component;
import com.synopsys.kb.httpclient.model.ComponentVariant;
import com.synopsys.kb.httpclient.model.ComponentVersion;
import com.synopsys.kb.httpclient.model.ComponentVersionSummary;
import com.synopsys.kb.httpclient.model.CveVulnerability;
import com.synopsys.kb.httpclient.model.Cvss2AccessComplexity;
import com.synopsys.kb.httpclient.model.Cvss2AccessVector;
import com.synopsys.kb.httpclient.model.Cvss2Authentication;
import com.synopsys.kb.httpclient.model.Cvss2AvailabilityImpact;
import com.synopsys.kb.httpclient.model.Cvss2ConfidentialityImpact;
import com.synopsys.kb.httpclient.model.Cvss2Exploitability;
import com.synopsys.kb.httpclient.model.Cvss2IntegrityImpact;
import com.synopsys.kb.httpclient.model.Cvss2RemediationLevel;
import com.synopsys.kb.httpclient.model.Cvss2ReportConfidence;
import com.synopsys.kb.httpclient.model.Cvss2Score;
import com.synopsys.kb.httpclient.model.Cvss2TemporalMetrics;
import com.synopsys.kb.httpclient.model.Cvss3AttackComplexity;
import com.synopsys.kb.httpclient.model.Cvss3AttackVector;
import com.synopsys.kb.httpclient.model.Cvss3AvailabilityImpact;
import com.synopsys.kb.httpclient.model.Cvss3ConfidentialityImpact;
import com.synopsys.kb.httpclient.model.Cvss3ExploitCodeMaturity;
import com.synopsys.kb.httpclient.model.Cvss3IntegrityImpact;
import com.synopsys.kb.httpclient.model.Cvss3PrivilegesRequired;
import com.synopsys.kb.httpclient.model.Cvss3RemediationLevel;
import com.synopsys.kb.httpclient.model.Cvss3ReportConfidence;
import com.synopsys.kb.httpclient.model.Cvss3Scope;
import com.synopsys.kb.httpclient.model.Cvss3Score;
import com.synopsys.kb.httpclient.model.Cvss3TemporalMetrics;
import com.synopsys.kb.httpclient.model.Cvss3UserInteraction;
import com.synopsys.kb.httpclient.model.License;
import com.synopsys.kb.httpclient.model.LicenseCodeSharing;
import com.synopsys.kb.httpclient.model.LicenseDefinition;
import com.synopsys.kb.httpclient.model.LicenseDefinitionItem;
import com.synopsys.kb.httpclient.model.LicenseDefinitionType;
import com.synopsys.kb.httpclient.model.LicenseOwnership;
import com.synopsys.kb.httpclient.model.LicenseRestriction;
import com.synopsys.kb.httpclient.model.Link;
import com.synopsys.kb.httpclient.model.MainLanguage;
import com.synopsys.kb.httpclient.model.Meta;
import com.synopsys.kb.httpclient.model.NextVersion;
import com.synopsys.kb.httpclient.model.OngoingVersion;
import com.synopsys.kb.httpclient.model.RiskProfile;
import com.synopsys.kb.httpclient.model.TeamSize;
import com.synopsys.kb.httpclient.model.VulnerabilitySeverity;
import com.synopsys.kb.httpclient.model.VulnerabilitySource;
import com.synopsys.kb.httpclient.model.VulnerabilityStatus;

/**
 * Abstract Black Duck-centric test.
 * 
 * @author skatzman
 */
public abstract class AbstractBdTest {
    protected static final String BASE_HREF = "https://kbtest.blackducksoftware.com";

    protected Meta constructMeta(String href) {
        List<Link> links = new ArrayList<>();

        return constructMeta(href, links);
    }

    protected Meta constructMeta(String href, List<Link> links) {
        return new Meta(href, links);
    }

    protected <T> HttpResponse<T> constructMergeMigratedHttpResponse(String sourceRequestUri,
            String mergeMigratedRequestUri) {
        Link movedLink = new Link(Relationship.MOVED, mergeMigratedRequestUri);
        List<Link> links = List.of(movedLink);
        Meta migratedMeta = new Meta(sourceRequestUri, links);

        return constructHttpResponse(301, Set.of(200, 404), null, migratedMeta);
    }

    protected <T> HttpResponse<T> constructSplitMigratedHttpResponse(String sourceRequestUri, List<String> splitMigratedRequestUris) {
        List<Link> links = new ArrayList<>();
        for (String splitMigratedRequestUri : splitMigratedRequestUris) {
            Link movedLink = new Link(Relationship.MOVED, splitMigratedRequestUri);
            links.add(movedLink);
        }

        Meta migratedMeta = new Meta(sourceRequestUri, links);

        return constructHttpResponse(300, Set.of(200, 404), null, migratedMeta);
    }

    protected <T> HttpResponse<T> constructHttpResponse(@Nullable T messageBody) {
        return new HttpResponse<>(200, Set.of(200, 404), messageBody, null);
    }

    protected <T> HttpResponse<T> constructHttpResponse(int code,
            Set<Integer> expectedCodes,
            @Nullable T messageBody,
            @Nullable Meta migratedMeta) {
        return new HttpResponse<>(code, expectedCodes, messageBody, migratedMeta);
    }

    protected <T> HttpResult<T> constructHttpResult(String requestMethod, String requestUri, HttpResponse<T> httpResponse) {
        return new HttpResult<>(requestMethod, requestUri, httpResponse);
    }

    protected <S, T> void assertHttpResult(HttpResult<S> sourceHttpResult, HttpResult<T> destinationHttpResult) {
        Assert.assertNotNull(sourceHttpResult, "Source HTTP result should be initialized.");
        Assert.assertNotNull(destinationHttpResult, "Destination HTTP result should be initialized.");
        Assert.assertEquals(destinationHttpResult.getRequestMethod(), sourceHttpResult.getRequestMethod(), "Request methods should be equal.");
        Assert.assertEquals(destinationHttpResult.getRequestUri(), sourceHttpResult.getRequestUri(), "Request URIs should be equal.");

        HttpResponse<S> sourceHttpResponse = sourceHttpResult.getHttpResponse().orElse(null);
        HttpResponse<T> destinationHttpResponse = destinationHttpResult.getHttpResponse().orElse(null);
        if (sourceHttpResponse != null) {
            Assert.assertNotNull(destinationHttpResponse, "Destination HTTP response should be initialized.");

            Assert.assertEquals(destinationHttpResponse.getCode(), sourceHttpResponse.getCode(), "Codes should be equal.");
            Assert.assertEquals(destinationHttpResponse.getExpectedCodes(), sourceHttpResponse.getExpectedCodes(), "Expected codes should be equal.");

            S sourceMessageBody = sourceHttpResponse.getMessageBody().orElse(null);
            if (sourceMessageBody != null) {
                Assert.assertNotNull(destinationHttpResponse.getMessageBody().orElse(null), "Destination message body should be initialized.");
            } else {
                Assert.assertNull(destinationHttpResponse.getMessageBody().orElse(null), "Destination message body should be null.");
            }

            Meta sourceMigratedMeta = sourceHttpResponse.getMigratedMeta().orElse(null);
            if (sourceMigratedMeta != null) {
                Assert.assertEquals(destinationHttpResponse.getMigratedMeta().orElse(null), sourceMigratedMeta, "Migrated metas should be equal.");
            } else {
                Assert.assertNull(destinationHttpResponse.getMigratedMeta().orElse(null), "Destination migrated meta should be null.");
            }
        } else {
            Assert.assertNull(destinationHttpResponse, "Destination HTTP response should be null.");
        }
    }

    protected <S, T> void assertHttpResult(HttpResult<S> sourceHttpResult,
            MigratableHttpResult<T> actualHttpResult,
            List<Meta> expectedMigratedMetaHistory) {
        Assert.assertNotNull(sourceHttpResult, "Source HTTP result should be initialized.");
        Assert.assertNotNull(actualHttpResult, "Actual HTTP result should be initialized.");
        Assert.assertEquals(actualHttpResult.getRequestMethod(), sourceHttpResult.getRequestMethod(), "Request methods should be equal.");
        Assert.assertEquals(actualHttpResult.getRequestUri(), sourceHttpResult.getRequestUri(), "Request URIs should be equal.");

        HttpResponse<S> sourceHttpResponse = sourceHttpResult.getHttpResponse().orElse(null);
        MigratableHttpResponse<T> actualMigratableHttpResponse = actualHttpResult.getMigratableHttpResponse().orElse(null);
        if (sourceHttpResponse != null) {
            Assert.assertNotNull(actualMigratableHttpResponse, "Actual migratable HTTP response should be initialized.");

            Assert.assertEquals(actualMigratableHttpResponse.getCode(), sourceHttpResponse.getCode(), "Codes should be equal.");
            Assert.assertEquals(actualMigratableHttpResponse.getExpectedCodes(), sourceHttpResponse.getExpectedCodes(), "Expected codes should be equal.");

            S sourceMessageBody = sourceHttpResponse.getMessageBody().orElse(null);
            if (sourceMessageBody != null) {
                Assert.assertNotNull(actualMigratableHttpResponse.getMessageBody().orElse(null), "Actual message body should be initialized.");
            } else {
                Assert.assertNull(actualMigratableHttpResponse.getMessageBody().orElse(null), "Actual message body should be null.");
            }

            Meta sourceMigratedMeta = sourceHttpResponse.getMigratedMeta().orElse(null);
            if (sourceMigratedMeta != null) {
                Assert.assertEquals(actualMigratableHttpResponse.getMigratedMeta().orElse(null), sourceMigratedMeta, "Migrated metas should be equal.");
            } else {
                Assert.assertNull(actualMigratableHttpResponse.getMigratedMeta().orElse(null), "Actual migrated meta should be null.");
            }

            List<Meta> actualMigratedMetaHistory = actualMigratableHttpResponse.getMigratedMetaHistory();
            Assert.assertEquals(actualMigratedMetaHistory, expectedMigratedMetaHistory, "Migrated meta histories should be equal.");
        } else {
            Assert.assertNull(actualMigratableHttpResponse, "Actual migratable HTTP response should be null.");
        }
    }

    protected Component constructComponent(UUID componentId, String name) {
        Meta meta = new Meta(BASE_HREF + "/api/components/" + componentId, null);

        return new Component(name, "This is a description.", null, Collections.emptySet(), Collections.emptyList(), Boolean.FALSE, meta);
    }

    protected ComponentVersion constructComponentVersion(UUID componentId, UUID componentVersionId, String version) {
        LicenseDefinition licenseDefinition = new LicenseDefinition(LicenseDefinitionType.CONJUNCTIVE,
                List.of(new LicenseDefinitionItem(BASE_HREF + "/api/licenses/" + UUID.randomUUID(), null)));
        RiskProfile riskProfile = new RiskProfile(0, 0, 0, 0, 0);
        Meta meta = new Meta(BASE_HREF + "/api/versions/" + componentVersionId,
                List.of(new Link(Relationship.COMPONENT, BASE_HREF + "/api/components/" + componentId)));

        return new ComponentVersion(version, OffsetDateTime.now(), licenseDefinition, riskProfile, Boolean.FALSE, Boolean.FALSE, meta);
    }

    protected ComponentVersionSummary constructComponentVersionSummary(UUID componentId, UUID componentVersionId, String version) {
        Meta meta = new Meta(BASE_HREF + "/api/versions/" + componentVersionId,
                List.of(new Link(Relationship.COMPONENT, BASE_HREF + "/api/components/" + componentId)));

        return new ComponentVersionSummary(version, OffsetDateTime.now(), Boolean.FALSE, meta);
    }

    protected ComponentVariant constructComponentVariant(UUID componentId,
            UUID componentVersionId,
            UUID componentVariantId,
            String version,
            String externalNamespace,
            String externalId) {
        LicenseDefinition licenseDefinition = new LicenseDefinition(LicenseDefinitionType.CONJUNCTIVE,
                List.of(new LicenseDefinitionItem(BASE_HREF + "/api/licenses/" + UUID.randomUUID(), null)));
        Meta meta = new Meta(BASE_HREF + "/api/variants/" + componentVariantId,
                List.of(new Link(Relationship.COMPONENT, BASE_HREF + "/api/components/" + componentId),
                        new Link(Relationship.VERSION, BASE_HREF + "/api/versions/" + componentVersionId)));

        return new ComponentVariant(version, externalNamespace, externalId, Boolean.FALSE, externalNamespace, externalId, licenseDefinition, Boolean.FALSE,
                Boolean.FALSE, meta);
    }

    protected OngoingVersion constructOngoingVersion(UUID componentId) {
        MainLanguage mainLanguage = new MainLanguage("Java");
        Meta meta = new Meta(BASE_HREF + "/api/components/" + componentId + "/ongoing-version", Collections.emptyList());

        return new OngoingVersion(ActivityTrend.DECREASING, TeamSize.AVERAGE_SIZE_DEVELOPMENT_TEAM, CodeBaseMaturity.MATURE, mainLanguage,
                OffsetDateTime.now(), 1000, 90, 5, 100, 200, 10, 20, 5, meta);
    }

    protected NextVersion constructNextVersion(UUID componentVersionId) {
        Meta meta = new Meta(BASE_HREF + "/api/versions/" + componentVersionId + "/next", Collections.emptyList());

        return new NextVersion(100, 90, meta);
    }

    protected License constructLicense(UUID licenseId, String name) {
        Meta meta = new Meta(BASE_HREF + "/api/licenses/" + licenseId, Collections.emptyList());

        return new License(name, LicenseCodeSharing.PERMISSIVE, LicenseOwnership.OPEN_SOURCE, OffsetDateTime.now(), null, Boolean.FALSE,
                LicenseRestriction.UNRESTRICTED, meta);
    }

    protected LicenseDefinitionItem constructLicenseDefinitionItem(@Nullable UUID licenseId, @Nullable LicenseDefinition licenseDefinition) {
        String href = (licenseId != null) ? BASE_HREF + "/api/licenses/" + licenseId : null;

        return new LicenseDefinitionItem(href, licenseDefinition);
    }

    protected LicenseDefinition constructLicenseDefinition(LicenseDefinitionType type, List<LicenseDefinitionItem> items) {
        return new LicenseDefinition(type, items);
    }

    protected CveVulnerability constructCveVulnerability(String id, VulnerabilityStatus status) {
        return constructCveVulnerability(id, status, true, true);
    }

    protected CveVulnerability constructCveVulnerability(String id,
            VulnerabilityStatus status,
            boolean isCvss2ScorePresent,
            boolean isCvss3ScorePresent) {
        Cvss2Score cvss2Score = null;
        if (isCvss2ScorePresent) {
            Cvss2TemporalMetrics cvss2TemporalMetrics = new Cvss2TemporalMetrics(4.0d, Cvss2Exploitability.FUNCTIONAL, Cvss2RemediationLevel.NOT_DEFINED,
                    Cvss2ReportConfidence.CONFIRMED);
            cvss2Score = new Cvss2Score(1.0d, 2.0d, 3.0d, VulnerabilitySeverity.LOW, Cvss2AccessVector.ADJACENT_NETWORK, Cvss2AccessComplexity.HIGH,
                    Cvss2Authentication.MULTIPLE_INSTANCES, Cvss2ConfidentialityImpact.COMPLETE, Cvss2IntegrityImpact.COMPLETE,
                    Cvss2AvailabilityImpact.COMPLETE,
                    VulnerabilitySource.NVD, "vector", cvss2TemporalMetrics);
        }

        Cvss3Score cvss3Score = null;
        if (isCvss3ScorePresent) {
            Cvss3TemporalMetrics cvss3TemporalMetrics = new Cvss3TemporalMetrics(7.0d, Cvss3ExploitCodeMaturity.FUNCTIONAL, Cvss3RemediationLevel.NOT_DEFINED,
                    Cvss3ReportConfidence.CONFIRMED);
            cvss3Score = new Cvss3Score(4.0d, 5.0d, 6.0d, VulnerabilitySeverity.MEDIUM, Cvss3AttackVector.ADJACENT, Cvss3AttackComplexity.HIGH,
                    Cvss3ConfidentialityImpact.HIGH, Cvss3IntegrityImpact.HIGH, Cvss3AvailabilityImpact.HIGH, Cvss3PrivilegesRequired.HIGH, Cvss3Scope.CHANGED,
                    Cvss3UserInteraction.NONE, VulnerabilitySource.NVD, "vector", cvss3TemporalMetrics);
        }

        Meta meta = new Meta(BASE_HREF + "/api/vulnerabilities/cve/" + id, null);

        return new CveVulnerability("description", OffsetDateTime.now(), OffsetDateTime.now(), cvss2Score, cvss3Score, Collections.emptyList(), status, meta);
    }

    protected BdsaVulnerability constructBdsaVulnerability(String id, VulnerabilityStatus status) {
        return constructBdsaVulnerability(id, status, true, true, null);
    }

    protected BdsaVulnerability constructBdsaVulnerability(String id,
            VulnerabilityStatus status,
            boolean isCvss2ScorePresent,
            boolean isCvss3ScorePresent,
            @Nullable String relatedCveVulnerabilityId) {
        Cvss2Score cvss2Score = null;
        if (isCvss2ScorePresent) {
            Cvss2TemporalMetrics cvss2TemporalMetrics = new Cvss2TemporalMetrics(4.0d, Cvss2Exploitability.FUNCTIONAL, Cvss2RemediationLevel.NOT_DEFINED,
                    Cvss2ReportConfidence.CONFIRMED);
            cvss2Score = new Cvss2Score(1.0d, 2.0d, 3.0d, VulnerabilitySeverity.LOW, Cvss2AccessVector.ADJACENT_NETWORK, Cvss2AccessComplexity.HIGH,
                    Cvss2Authentication.MULTIPLE_INSTANCES, Cvss2ConfidentialityImpact.COMPLETE, Cvss2IntegrityImpact.COMPLETE,
                    Cvss2AvailabilityImpact.COMPLETE,
                    VulnerabilitySource.NVD, "vector", cvss2TemporalMetrics);
        }

        Cvss3Score cvss3Score = null;
        if (isCvss3ScorePresent) {
            Cvss3TemporalMetrics cvss3TemporalMetrics = new Cvss3TemporalMetrics(7.0d, Cvss3ExploitCodeMaturity.FUNCTIONAL, Cvss3RemediationLevel.NOT_DEFINED,
                    Cvss3ReportConfidence.CONFIRMED);
            cvss3Score = new Cvss3Score(4.0d, 5.0d, 6.0d, VulnerabilitySeverity.MEDIUM, Cvss3AttackVector.ADJACENT, Cvss3AttackComplexity.HIGH,
                    Cvss3ConfidentialityImpact.HIGH, Cvss3IntegrityImpact.HIGH, Cvss3AvailabilityImpact.HIGH, Cvss3PrivilegesRequired.HIGH, Cvss3Scope.CHANGED,
                    Cvss3UserInteraction.NONE, VulnerabilitySource.NVD, "vector", cvss3TemporalMetrics);
        }

        List<Link> links = new ArrayList<>();
        if (relatedCveVulnerabilityId != null) {
            Link link = new Link(Relationship.CVE, BASE_HREF + "/api/vulnerabilities/cve/" + relatedCveVulnerabilityId);
            links.add(link);
        }
        Meta meta = new Meta(BASE_HREF + "/api/vulnerabilities/bdsa/" + id, links);

        return new BdsaVulnerability("title", "description", "technical description", "workaround", "solution", "credit", OffsetDateTime.now(),
                OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now(),
                Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, cvss2Score, cvss3Score, Collections.emptyList(), status, Collections.emptySet(), meta);
    }
}
