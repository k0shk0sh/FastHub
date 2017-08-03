package com.fastaccess.data.dao.timeline;

import com.fastaccess.data.dao.PullRequestStatusModel;
import com.fastaccess.data.dao.model.PullRequest;

import lombok.Getter;
import lombok.Setter;
import pr.PullRequestTimelineQuery;

/**
 * Created by kosh on 02/08/2017.
 */

@Getter @Setter public class PullRequestTimelineModel {

    public static final int HEADER = 0;
    public static final int EVENT = 1;
    public static final int COMMENT = 2;
    public static final int STATUS = 3;
    public static final int REVIEW = 4;

    public PullRequestTimelineQuery.Node node;
    public PullRequest pullRequest;
    public PullRequestStatusModel status;

    public PullRequestTimelineModel(PullRequest pullRequest) {
        this.pullRequest = pullRequest;
    }

    public PullRequestTimelineModel(PullRequestTimelineQuery.Node node) {
        this.node = node;
    }

    public PullRequestTimelineModel(PullRequestStatusModel status) {
        this.status = status;
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
                || node.asCommit() != null || node.asHeadRefRestoredEvent() != null) {
            return EVENT;
        } else if (node.asIssueComment() != null) {
            return COMMENT;
        } else if (status != null) {
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
