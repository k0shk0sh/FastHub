package com.fastaccess.data.dao.timeline;

import com.fastaccess.data.dao.model.PullRequest;

import pr.PullRequestTimelineQuery;

/**
 * Created by kosh on 02/08/2017.
 */

public class PullRequestTimelineModel {

    public static final int HEADER = 0;
    public static final int LINE_COMMENT = 1;
    public static final int EVENT = 2;
    public static final int COMMENT = 3;
    public static final int STATUS = 4;
    public static final int REVIEW = 5;

    private PullRequestTimelineQuery.Node node;
    private PullRequest pullRequest;

    public PullRequestTimelineModel(PullRequest pullRequest) {
        this.pullRequest = pullRequest;
    }

    public PullRequestTimelineModel(PullRequestTimelineQuery.Node node) {
        this.node = node;
    }

    public int getType() {
        if (pullRequest != null) return HEADER;
        if (node.asAssignedEvent() != null || node.asClosedEvent() != null
                || node.asDemilestonedEvent() != null || node.asHeadRefDeletedEvent() != null
                || node.asLabeledEvent() != null || node.asLockedEvent() != null
                || node.asMergedEvent() != null || node.asMilestonedEvent() != null
                || node.asReferencedEvent() != null || node.asRenamedTitleEvent() != null
                || node.asReopenedEvent() != null || node.asUnassignedEvent() != null
                || node.asUnlabeledEvent() != null || node.asUnlockedEvent() != null
                || node.asCommit() != null) {
            return EVENT;
        } else if (node.asIssueComment() != null) {
            return COMMENT;
        } else if (node.asDeployedEvent() != null) {
            return STATUS;
        } else if (node.asPullRequestReview() != null || node.asReviewDismissedEvent() != null
                || node.asReviewRequestedEvent() != null || node.asReviewRequestRemovedEvent() != null) {
            return REVIEW;
        }
        return EVENT;
    }

    @Override public String toString() {
        return String.valueOf(getType());
    }
}
