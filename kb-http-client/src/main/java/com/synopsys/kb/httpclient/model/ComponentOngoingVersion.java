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
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Component ongoing version representation.
 * 
 * @author skatzman
 */
public class ComponentOngoingVersion extends AbstractEntity {
    private final ActivityTrend activityTrend;

    private final TeamSize teamSize;

    private final CodeBaseMaturity codeBaseMaturity;

    private final MainLanguage mainLanguage;

    private final OffsetDateTime lastCommit;

    private final Integer linesOfCode;

    private final Integer ratingAverage;

    private final int ratingCount;

    private final Integer commitCount12Month;

    private final Integer commitCountTotal;

    private final Integer committerCount12Month;

    private final Integer committerCountTotal;

    private final int reviewCount;

    private final Meta meta;

    @JsonCreator
    public ComponentOngoingVersion(@JsonProperty("activityTrend") ActivityTrend activityTrend,
            @JsonProperty("teamSize") TeamSize teamSize,
            @JsonProperty("codeBaseMaturity") CodeBaseMaturity codeBaseMaturity,
            @JsonProperty("mainLanguage") MainLanguage mainLanguage,
            @JsonProperty("lastCommit") OffsetDateTime lastCommit,
            @JsonProperty("linesOfCode") Integer linesOfCode,
            @JsonProperty("ratingAverage") Integer ratingAverage,
            @JsonProperty("ratingCount") Integer ratingCount,
            @JsonProperty("commitCount12Month") Integer commitCount12Month,
            @JsonProperty("commitCountTotal") Integer commitCountTotal,
            @JsonProperty("committerCount12Month") Integer committerCount12Month,
            @JsonProperty("committerCountTotal") Integer committerCountTotal,
            @JsonProperty("reviewCount") Integer reviewCount,
            @JsonProperty("_meta") Meta meta) {
        this.activityTrend = activityTrend;
        this.teamSize = teamSize;
        this.codeBaseMaturity = codeBaseMaturity;
        this.mainLanguage = mainLanguage;
        this.lastCommit = lastCommit;
        this.linesOfCode = linesOfCode;
        this.ratingAverage = ratingAverage;
        this.ratingCount = (ratingCount != null) ? ratingCount.intValue() : 0;
        this.commitCount12Month = commitCount12Month;
        this.commitCountTotal = commitCountTotal;
        this.committerCount12Month = committerCount12Month;
        this.committerCountTotal = committerCountTotal;
        this.reviewCount = (reviewCount != null) ? reviewCount.intValue() : 0;
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
    public final UUID getComponentId() {
        Meta meta = getMeta();

        return meta.getHrefId("components")
                .map(UUID::fromString)
                .orElseThrow(() -> new IllegalArgumentException("Unable to get component id because it is absent."));
    }

    public Optional<ActivityTrend> getActivityTrend() {
        return Optional.ofNullable(activityTrend);
    }

    public Optional<TeamSize> getTeamSize() {
        return Optional.ofNullable(teamSize);
    }

    public Optional<CodeBaseMaturity> getCodeBaseMaturity() {
        return Optional.ofNullable(codeBaseMaturity);
    }

    public Optional<MainLanguage> getMainLanguage() {
        return Optional.ofNullable(mainLanguage);
    }

    public Optional<OffsetDateTime> getLastCommit() {
        return Optional.ofNullable(lastCommit);
    }

    public Optional<Integer> getLinesOfCode() {
        return Optional.ofNullable(linesOfCode);
    }

    public Optional<Integer> getRatingAverage() {
        return Optional.ofNullable(ratingAverage);
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public Optional<Integer> getCommitCount12Month() {
        return Optional.ofNullable(commitCount12Month);
    }

    public Optional<Integer> getCommitCountTotal() {
        return Optional.ofNullable(commitCountTotal);
    }

    public Optional<Integer> getCommitterCount12Month() {
        return Optional.ofNullable(committerCount12Month);
    }

    public Optional<Integer> getCommitterCountTotal() {
        return Optional.ofNullable(committerCountTotal);
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public Meta getMeta() {
        return meta;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getActivityTrend(), getTeamSize(), getCodeBaseMaturity(), getMainLanguage(), getLastCommit(), getLinesOfCode(), getRatingAverage(),
                getRatingCount(), getCommitCount12Month(), getCommitCountTotal(), getCommitterCount12Month(), getCommitterCountTotal(), getReviewCount(),
                getMeta());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof ComponentOngoingVersion) {
            ComponentOngoingVersion otherComponentOngoingVersion = (ComponentOngoingVersion) otherObject;

            return Objects.equals(getActivityTrend(), otherComponentOngoingVersion.getActivityTrend())
                    && Objects.equals(getTeamSize(), otherComponentOngoingVersion.getTeamSize())
                    && Objects.equals(getCodeBaseMaturity(), otherComponentOngoingVersion.getCodeBaseMaturity())
                    && Objects.equals(getMainLanguage(), otherComponentOngoingVersion.getMainLanguage())
                    && Objects.equals(getLastCommit(), otherComponentOngoingVersion.getLastCommit())
                    && Objects.equals(getLinesOfCode(), otherComponentOngoingVersion.getLinesOfCode())
                    && Objects.equals(getRatingAverage(), otherComponentOngoingVersion.getRatingAverage())
                    && Objects.equals(getRatingCount(), otherComponentOngoingVersion.getRatingCount())
                    && Objects.equals(getCommitCount12Month(), otherComponentOngoingVersion.getCommitCount12Month())
                    && Objects.equals(getCommitCountTotal(), otherComponentOngoingVersion.getCommitCountTotal())
                    && Objects.equals(getCommitterCount12Month(), otherComponentOngoingVersion.getCommitterCount12Month())
                    && Objects.equals(getCommitterCountTotal(), otherComponentOngoingVersion.getCommitterCountTotal())
                    && Objects.equals(getReviewCount(), otherComponentOngoingVersion.getReviewCount())
                    && Objects.equals(getMeta(), otherComponentOngoingVersion.getMeta());
        }

        return false;
    }
}
