package com.fastaccess.data.dao.timeline;

import android.support.annotation.Nullable;

import com.fastaccess.data.dao.ReactionsModel;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import pr.PullRequestTimelineQuery;

/**
 * Created by kosh on 15/08/2017.
 */

@Getter public class PullRequestCommitModel {
    public String path;
    public int position;
    public PullRequestTimelineQuery.Commit1 commit;
    public List<PullRequestCommitModel> comments;
    public PullRequestTimelineQuery.Node1 node1;
    public List<ReactionsModel> reaction;

    public PullRequestCommitModel() {}

    @Nullable public static PullRequestCommitModel getThread(@Nullable PullRequestTimelineQuery.AsCommitCommentThread thread) {
        if (thread != null) {
            PullRequestCommitModel model = new PullRequestCommitModel();
            model.path = thread.path();
            Integer pathPosition = thread.position();
            model.position = pathPosition != null ? pathPosition : 0;
            model.commit = thread.commit();
            PullRequestTimelineQuery.Comments comments = thread.comments();
            List<PullRequestTimelineQuery.Edge1> edges = comments.edges();
            if (edges != null && !edges.isEmpty()) {
                List<PullRequestCommitModel> models = new ArrayList<>();
                for (PullRequestTimelineQuery.Edge1 edge : edges) {
                    PullRequestCommitModel comment = new PullRequestCommitModel();
                    comment.node1 = edge.node();
                    if (comment.node1 != null && comment.node1.reactionGroups() != null) {
                        comment.reaction = ReactionsModel.getReactionGroup(comment.node1.reactionGroups());
                    }
                    models.add(comment);
                }
                model.comments = models;
            }
            return model;
        }
        return null;
    }
}
