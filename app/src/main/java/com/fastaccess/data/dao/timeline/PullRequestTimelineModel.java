package com.fastaccess.data.dao.timeline;

import com.fastaccess.data.dao.ReactionsModel;
import com.fastaccess.data.dao.model.PullRequest;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import github.PullRequestTimelineQuery;

/**
 * Created by kosh on 02/08/2017.
 */

@Getter @Setter public class PullRequestTimelineModel {

    public static final int HEADER = 1;
    public static final int EVENT = 2;
    public static final int COMMENT = 3;
    public static final int STATUS = 4;
    public static final int REVIEW = 5;
    public static final int COMMIT_COMMENTS = 6;
    public PullRequestTimelineQuery.Node node;
    public PullRequest pullRequest;
    public PullRequestTimelineQuery.Status status;
    public List<ReactionsModel> reactions;
    public boolean isMergeable;
    public PullRequestCommitModel commitThread;
    public PullRequestReviewModel reviewModel;

    public PullRequestTimelineModel(PullRequest pullRequest) {
        this.pullRequest = pullRequest;
    }

    public PullRequestTimelineModel(PullRequestTimelineQuery.Node node) {
        this.node = node;
        if (this.node.asCommitCommentThread() != null) {

        } else {
            if (node.asPullRequestReview() != null || node.asReviewDismissedEvent() != null
                    || node.asReviewRequestedEvent() != null || node.asReviewRequestRemovedEvent() != null) {
                reviewModel = PullRequestReviewModel.build(node);
            }
        }
    }

    public PullRequestTimelineModel(PullRequestTimelineQuery.Status status, boolean isMergeable) {
        this.status = status;
        this.isMergeable = isMergeable;
    }

    public int getType() {
        if (pullRequest != null) return HEADER;
        if (node != null) {
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
                if (reactions == null) {
                    //noinspection ConstantConditions
                    setReactions(ReactionsModel.getReaction2(node.asIssueComment().reactionGroups()));
                }
                return COMMENT;
            } else if (reviewModel != null) {
                return REVIEW;
            } else if (commitThread != null) {
                return COMMIT_COMMENTS;
            }
        } else if (status != null) {
            return STATUS;
        }
        return 0;
    }

    @Override public String toString() {
        return String.valueOf(getType());
    }
}
