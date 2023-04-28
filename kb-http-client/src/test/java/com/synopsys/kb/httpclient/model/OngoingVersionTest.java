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

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synopsys.kb.httpclient.AbstractTest;
import com.synopsys.kb.httpclient.api.Relationship;

/**
 * Ongoing version test.
 * 
 * @author skatzman
 */
public class OngoingVersionTest extends AbstractTest {
    private static final UUID COMPONENT_ID = UUID.randomUUID();

    private static final ActivityTrend ACTIVITY_TREND = ActivityTrend.STABLE;

    private static final TeamSize TEAM_SIZE = TeamSize.LARGE_DEVELOPMENT_TEAM;

    private static final CodeBaseMaturity CODE_BASE_MATURITY = CodeBaseMaturity.SHORT_HISTORY;

    private static final MainLanguage MAIN_LANGUAGE = new MainLanguage("Java");

    private static final OffsetDateTime LAST_COMMIT = OffsetDateTime.now();

    private static final Integer LINES_OF_CODE = Integer.valueOf(1234567);

    private static final Integer RATING_AVERAGE = Integer.valueOf(91);

    private static final Integer RATING_COUNT = Integer.valueOf(149);

    private static final Integer COMMIT_COUNT_12_MONTH = Integer.valueOf(345);

    private static final Integer COMMIT_COUNT_TOTAL = Integer.valueOf(1589);

    private static final Integer COMMITTER_COUNT_12_MONTH = Integer.valueOf(27);

    private static final Integer COMMITTER_COUNT_TOTAL = Integer.valueOf(77);

    private static final Integer REVIEW_COUNT = Integer.valueOf(56);

    private static final Meta META = new Meta(BASE_HREF + "/api/components/" + COMPONENT_ID + "/ongoing-version",
            List.of(new Link(Relationship.OPEN_HUB, "https://www.openhub.net/p/3564")));

    @Test
    public void testConstructor() {
        OngoingVersion ongoingVersion = new OngoingVersion(ACTIVITY_TREND, TEAM_SIZE, CODE_BASE_MATURITY, MAIN_LANGUAGE, LAST_COMMIT,
                LINES_OF_CODE, RATING_AVERAGE, RATING_COUNT, COMMIT_COUNT_12_MONTH, COMMIT_COUNT_TOTAL, COMMITTER_COUNT_12_MONTH, COMMITTER_COUNT_TOTAL,
                REVIEW_COUNT, META);

        Assert.assertEquals(ongoingVersion.getActivityTrend().orElse(null), ACTIVITY_TREND, "Activity trends should be equal.");
        Assert.assertEquals(ongoingVersion.getTeamSize().orElse(null), TEAM_SIZE, "Team sizes should be equal.");
        Assert.assertEquals(ongoingVersion.getCodeBaseMaturity().orElse(null), CODE_BASE_MATURITY, "Code base maturity counts should be equal.");
        Assert.assertEquals(ongoingVersion.getMainLanguage().orElse(null), MAIN_LANGUAGE, "Main languages should be equal.");
        Assert.assertEquals(ongoingVersion.getLastCommit().orElse(null), LAST_COMMIT, "Last commits should be equal.");
        Assert.assertEquals(ongoingVersion.getLinesOfCode().orElse(null), LINES_OF_CODE, "Lines of code should be equal.");
        Assert.assertEquals(ongoingVersion.getRatingAverage().orElse(null), RATING_AVERAGE, "Rating averages should be equal.");
        Assert.assertEquals(ongoingVersion.getRatingCount(), RATING_COUNT.intValue(), "Rating counts should be equal.");
        Assert.assertEquals(ongoingVersion.getCommitCount12Month().orElse(null), COMMIT_COUNT_12_MONTH,
                "Commit counts over 12 months should be equal.");
        Assert.assertEquals(ongoingVersion.getCommitCountTotal().orElse(null), COMMIT_COUNT_TOTAL, "Commit counts in total should be equal.");
        Assert.assertEquals(ongoingVersion.getCommitterCount12Month().orElse(null), COMMITTER_COUNT_12_MONTH,
                "Committer counts over 12 months should be equal.");
        Assert.assertEquals(ongoingVersion.getCommitterCountTotal().orElse(null), COMMITTER_COUNT_TOTAL,
                "Committer counts in total should be equal.");
        Assert.assertEquals(ongoingVersion.getReviewCount(), REVIEW_COUNT, "Review counts should be equal.");
        Assert.assertEquals(ongoingVersion.getMeta(), META, "Metas should be equal.");

        Assert.assertEquals(ongoingVersion.getComponentId(), COMPONENT_ID, "Component ids should be equal.");
    }

    @Test
    public void testConstructorWithNullValues() {
        OngoingVersion ongoingVersion = new OngoingVersion(ACTIVITY_TREND, TEAM_SIZE, CODE_BASE_MATURITY, MAIN_LANGUAGE, LAST_COMMIT,
                LINES_OF_CODE, RATING_AVERAGE, null, COMMIT_COUNT_12_MONTH, COMMIT_COUNT_TOTAL, COMMITTER_COUNT_12_MONTH, COMMITTER_COUNT_TOTAL,
                0, META);

        Assert.assertEquals(ongoingVersion.getActivityTrend().orElse(null), ACTIVITY_TREND, "Activity trends should be equal.");
        Assert.assertEquals(ongoingVersion.getTeamSize().orElse(null), TEAM_SIZE, "Team sizes should be equal.");
        Assert.assertEquals(ongoingVersion.getCodeBaseMaturity().orElse(null), CODE_BASE_MATURITY, "Code base maturity counts should be equal.");
        Assert.assertEquals(ongoingVersion.getMainLanguage().orElse(null), MAIN_LANGUAGE, "Main languages should be equal.");
        Assert.assertEquals(ongoingVersion.getLastCommit().orElse(null), LAST_COMMIT, "Last commits should be equal.");
        Assert.assertEquals(ongoingVersion.getLinesOfCode().orElse(null), LINES_OF_CODE, "Lines of code should be equal.");
        Assert.assertEquals(ongoingVersion.getRatingAverage().orElse(null), RATING_AVERAGE, "Rating averages should be equal.");
        Assert.assertEquals(ongoingVersion.getRatingCount(), 0, "Rating counts should be equal.");
        Assert.assertEquals(ongoingVersion.getCommitCount12Month().orElse(null), COMMIT_COUNT_12_MONTH,
                "Commit counts over 12 months should be equal.");
        Assert.assertEquals(ongoingVersion.getCommitCountTotal().orElse(null), COMMIT_COUNT_TOTAL, "Commit counts in total should be equal.");
        Assert.assertEquals(ongoingVersion.getCommitterCount12Month().orElse(null), COMMITTER_COUNT_12_MONTH,
                "Committer counts over 12 months should be equal.");
        Assert.assertEquals(ongoingVersion.getCommitterCountTotal().orElse(null), COMMITTER_COUNT_TOTAL,
                "Committer counts in total should be equal.");
        Assert.assertEquals(ongoingVersion.getReviewCount(), 0, "Review counts should be equal.");
        Assert.assertEquals(ongoingVersion.getMeta(), META, "Metas should be equal.");

        Assert.assertEquals(ongoingVersion.getComponentId(), COMPONENT_ID, "Component ids should be equal.");
    }

    @Test
    public void testDeserialization() throws JsonProcessingException {
        OngoingVersion ongoingVersion = new OngoingVersion(ACTIVITY_TREND, TEAM_SIZE, CODE_BASE_MATURITY, MAIN_LANGUAGE, LAST_COMMIT,
                LINES_OF_CODE, RATING_AVERAGE, RATING_COUNT, COMMIT_COUNT_12_MONTH, COMMIT_COUNT_TOTAL, COMMITTER_COUNT_12_MONTH, COMMITTER_COUNT_TOTAL,
                REVIEW_COUNT, META);

        String json = serialize(ongoingVersion);
        OngoingVersion result = deserialize(json, OngoingVersion.class);

        Assert.assertEquals(result.getActivityTrend().orElse(null), ACTIVITY_TREND, "Activity trends should be equal.");
        Assert.assertEquals(result.getTeamSize().orElse(null), TEAM_SIZE, "Team sizes should be equal.");
        Assert.assertEquals(result.getCodeBaseMaturity().orElse(null), CODE_BASE_MATURITY, "Code base maturity counts should be equal.");
        Assert.assertEquals(result.getMainLanguage().orElse(null), MAIN_LANGUAGE, "Main languages should be equal.");
        Assert.assertNotNull(result.getLastCommit().orElse(null), "Last commit should be initialized.");
        Assert.assertEquals(result.getLinesOfCode().orElse(null), LINES_OF_CODE, "Lines of code should be equal.");
        Assert.assertEquals(result.getRatingAverage().orElse(null), RATING_AVERAGE, "Rating averages should be equal.");
        Assert.assertEquals(result.getRatingCount(), RATING_COUNT.intValue(), "Rating counts should be equal.");
        Assert.assertEquals(result.getCommitCount12Month().orElse(null), COMMIT_COUNT_12_MONTH,
                "Commit counts over 12 months should be equal.");
        Assert.assertEquals(result.getCommitCountTotal().orElse(null), COMMIT_COUNT_TOTAL, "Commit counts in total should be equal.");
        Assert.assertEquals(result.getCommitterCount12Month().orElse(null), COMMITTER_COUNT_12_MONTH,
                "Committer counts over 12 months should be equal.");
        Assert.assertEquals(result.getCommitterCountTotal().orElse(null), COMMITTER_COUNT_TOTAL,
                "Committer counts in total should be equal.");
        Assert.assertEquals(result.getMeta(), META, "Metas should be equal.");
    }

    @Test
    public void testHashCode() {
        OngoingVersion ongoingVersion = new OngoingVersion(ACTIVITY_TREND, TEAM_SIZE, CODE_BASE_MATURITY, MAIN_LANGUAGE, LAST_COMMIT,
                LINES_OF_CODE, RATING_AVERAGE, RATING_COUNT, COMMIT_COUNT_12_MONTH, COMMIT_COUNT_TOTAL, COMMITTER_COUNT_12_MONTH, COMMITTER_COUNT_TOTAL,
                REVIEW_COUNT, META);
        OngoingVersion copyOngoingVersion = new OngoingVersion(ACTIVITY_TREND, TEAM_SIZE, CODE_BASE_MATURITY, MAIN_LANGUAGE, LAST_COMMIT,
                LINES_OF_CODE, RATING_AVERAGE, RATING_COUNT, COMMIT_COUNT_12_MONTH, COMMIT_COUNT_TOTAL, COMMITTER_COUNT_12_MONTH, COMMITTER_COUNT_TOTAL,
                REVIEW_COUNT, META);
        OngoingVersion differentOngoingVersion = new OngoingVersion(ACTIVITY_TREND, TEAM_SIZE, CODE_BASE_MATURITY, MAIN_LANGUAGE, LAST_COMMIT,
                LINES_OF_CODE, RATING_AVERAGE, RATING_COUNT, COMMIT_COUNT_12_MONTH, COMMIT_COUNT_TOTAL, COMMITTER_COUNT_12_MONTH, COMMITTER_COUNT_TOTAL,
                99999, META);

        assertHashCode(ongoingVersion, copyOngoingVersion, differentOngoingVersion);
    }

    @Test
    public void testEquals() {
        OngoingVersion ongoingVersion = new OngoingVersion(ACTIVITY_TREND, TEAM_SIZE, CODE_BASE_MATURITY, MAIN_LANGUAGE, LAST_COMMIT,
                LINES_OF_CODE, RATING_AVERAGE, RATING_COUNT, COMMIT_COUNT_12_MONTH, COMMIT_COUNT_TOTAL, COMMITTER_COUNT_12_MONTH, COMMITTER_COUNT_TOTAL,
                REVIEW_COUNT, META);
        OngoingVersion copyOngoingVersion = new OngoingVersion(ACTIVITY_TREND, TEAM_SIZE, CODE_BASE_MATURITY, MAIN_LANGUAGE, LAST_COMMIT,
                LINES_OF_CODE, RATING_AVERAGE, RATING_COUNT, COMMIT_COUNT_12_MONTH, COMMIT_COUNT_TOTAL, COMMITTER_COUNT_12_MONTH, COMMITTER_COUNT_TOTAL,
                REVIEW_COUNT, META);
        OngoingVersion differentOngoingVersion = new OngoingVersion(ACTIVITY_TREND, TEAM_SIZE, CODE_BASE_MATURITY, MAIN_LANGUAGE, LAST_COMMIT,
                LINES_OF_CODE, RATING_AVERAGE, RATING_COUNT, COMMIT_COUNT_12_MONTH, COMMIT_COUNT_TOTAL, COMMITTER_COUNT_12_MONTH, COMMITTER_COUNT_TOTAL,
                99999, META);

        assertEquals(ongoingVersion, copyOngoingVersion, differentOngoingVersion);
    }
}
