package com.fastaccess.data.dao.timeline;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.ReactionsModel;
import com.fastaccess.helper.Logger;
import com.fastaccess.helper.ParseDateFormat;

import java.util.ArrayList;
import java.util.List;

import github.PullRequestTimelineQuery;
import github.type.PullRequestReviewState;

/**
 * Created by kosh on 20/08/2017.
 */

public class PullRequestReviewModel {
    private PullRequestTimelineQuery.AsReviewDismissedEvent reviewDismissedEvent;
    private PullRequestTimelineQuery.AsReviewRequestedEvent reviewRequestedEvent;
    private PullRequestTimelineQuery.AsReviewRequestRemovedEvent reviewRequestRemovedEvent;
    private PullRequestTimelineQuery.Node2 node;
    private List<ReactionsModel> reaction;
    private List<PullRequestReviewModel> comments;
    private String id;
    private String url;
    private PullRequestTimelineQuery.Author2 author;
    private String bodyHTML;
    private String createdAt;
    private PullRequestReviewState state;

    @Nullable public static PullRequestReviewModel build(@NonNull PullRequestTimelineQuery.Node node) {
        PullRequestReviewModel model = new PullRequestReviewModel();
        if (node.asReviewRequestRemovedEvent() != null) {
            model.reviewRequestRemovedEvent = node.asReviewRequestRemovedEvent();
        } else if (node.asReviewDismissedEvent() != null) {
            model.reviewDismissedEvent = node.asReviewDismissedEvent();
        } else if (node.asReviewRequestedEvent() != null) {
            model.reviewRequestedEvent = node.asReviewRequestedEvent();
        } else {
            PullRequestTimelineQuery.AsPullRequestReview pullRequestReview = node.asPullRequestReview();
            if (pullRequestReview != null) {
                model.state = pullRequestReview.state();
                model.url = pullRequestReview.url().toString();
                model.author = pullRequestReview.author();
                model.bodyHTML = pullRequestReview.bodyHTML().toString();
                model.createdAt = ParseDateFormat.getTimeAgo(pullRequestReview.createdAt().toString()).toString();
                model.id = pullRequestReview.id();
                model.url = pullRequestReview.url().toString();
                List<PullRequestTimelineQuery.Edge2> edges = pullRequestReview.comments().edges();
                if (edges != null && !edges.isEmpty()) {
                    List<PullRequestReviewModel> comments = new ArrayList<>();
                    for (PullRequestTimelineQuery.Edge2 edge : edges) {
                        PullRequestTimelineQuery.Node2 node2 = edge.node();
                        if (node2 != null) {
                            PullRequestReviewModel comment = new PullRequestReviewModel();
                            comment.node = node2;
                            comment.reaction = ReactionsModel.getReaction(node2.reactionGroups());
                            comments.add(comment);
                        }
                    }
                    Logger.e(comments.size());
                    model.comments = comments;
                }
            } else {
                return null;
            }
        }
        return model;
    }

    public PullRequestTimelineQuery.AsReviewDismissedEvent getReviewDismissedEvent() {
        return reviewDismissedEvent;
    }

    public void setReviewDismissedEvent(PullRequestTimelineQuery.AsReviewDismissedEvent reviewDismissedEvent) {
        this.reviewDismissedEvent = reviewDismissedEvent;
    }

    public PullRequestTimelineQuery.AsReviewRequestedEvent getReviewRequestedEvent() {
        return reviewRequestedEvent;
    }

    public void setReviewRequestedEvent(PullRequestTimelineQuery.AsReviewRequestedEvent reviewRequestedEvent) {
        this.reviewRequestedEvent = reviewRequestedEvent;
    }

    public PullRequestTimelineQuery.AsReviewRequestRemovedEvent getReviewRequestRemovedEvent() {
        return reviewRequestRemovedEvent;
    }

    public void setReviewRequestRemovedEvent(PullRequestTimelineQuery.AsReviewRequestRemovedEvent reviewRequestRemovedEvent) {
        this.reviewRequestRemovedEvent = reviewRequestRemovedEvent;
    }

    public PullRequestTimelineQuery.Node2 getNode() {
        return node;
    }

    public void setNode(PullRequestTimelineQuery.Node2 node) {
        this.node = node;
    }

    public List<ReactionsModel> getReaction() {
        return reaction;
    }

    public void setReaction(List<ReactionsModel> reaction) {
        this.reaction = reaction;
    }

    public List<PullRequestReviewModel> getComments() {
        return comments;
    }

    public void setComments(List<PullRequestReviewModel> comments) {
        this.comments = comments;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public PullRequestTimelineQuery.Author2 getAuthor() {
        return author;
    }

    public void setAuthor(PullRequestTimelineQuery.Author2 author) {
        this.author = author;
    }

    public String getBodyHTML() {
        return bodyHTML;
    }

    public void setBodyHTML(String bodyHTML) {
        this.bodyHTML = bodyHTML;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public PullRequestReviewState getState() {
        return state;
    }

    public void setState(PullRequestReviewState state) {
        this.state = state;
    }
}
