package com.fastaccess.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.TimelineModel;
import com.fastaccess.ui.adapter.callback.OnToggleView;
import com.fastaccess.ui.adapter.callback.ReactionsCallback;
import com.fastaccess.ui.adapter.viewholder.CommitThreadViewHolder;
import com.fastaccess.ui.adapter.viewholder.GroupedReviewsViewHolder;
import com.fastaccess.ui.adapter.viewholder.IssueDetailsViewHolder;
import com.fastaccess.ui.adapter.viewholder.IssueTimelineViewHolder;
import com.fastaccess.ui.adapter.viewholder.PullStatusViewHolder;
import com.fastaccess.ui.adapter.viewholder.ReviewsViewHolder;
import com.fastaccess.ui.adapter.viewholder.TimelineCommentsViewHolder;
import com.fastaccess.ui.adapter.viewholder.UnknownTypeViewHolder;
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.timeline.timeline.PullRequestTimelineMvp.ReviewCommentCallback;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.List;

/**
 * Created by Kosh on 13 Dec 2016, 1:44 AM
 */

public class IssuesTimelineAdapter extends BaseRecyclerAdapter<TimelineModel, BaseViewHolder,
        BaseViewHolder.OnItemClickListener<TimelineModel>> {

    private final OnToggleView onToggleView;
    private final boolean showEmojies;
    private final ReactionsCallback reactionsCallback;
    private final boolean isMerged;
    private final ReviewCommentCallback reviewCommentCallback;
    private final String repoOwner;
    private final String poster;

    public IssuesTimelineAdapter(@NonNull List<TimelineModel> data, OnToggleView onToggleView, boolean showEmojies,
                                 ReactionsCallback reactionsCallback, boolean isMerged,
                                 ReviewCommentCallback reviewCommentCallback, String repoOwner, String poster) {
        super(data);
        this.onToggleView = onToggleView;
        this.showEmojies = showEmojies;
        this.reactionsCallback = reactionsCallback;
        this.isMerged = isMerged;
        this.reviewCommentCallback = reviewCommentCallback;
        this.repoOwner = repoOwner;
        this.poster = poster;
    }

    public IssuesTimelineAdapter(@NonNull List<TimelineModel> data, OnToggleView onToggleView, boolean showEmojies,
                                 ReactionsCallback reactionsCallback, String repoOwner, String poster) {
        this(data, onToggleView, showEmojies, reactionsCallback, false, null, repoOwner, poster);
    }

    @Override protected BaseViewHolder viewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new UnknownTypeViewHolder(BaseViewHolder.getView(parent, R.layout.unknown_row_item));
        } else if (viewType == TimelineModel.HEADER) {
            return IssueDetailsViewHolder.newInstance(parent, this, onToggleView, reactionsCallback, repoOwner, poster);
        } else if (viewType == TimelineModel.EVENT) {
            return IssueTimelineViewHolder.newInstance(parent, this, isMerged);
        } else if (viewType == TimelineModel.REVIEW) {
            return ReviewsViewHolder.Companion.newInstance(parent, this);
        } else if (viewType == TimelineModel.GROUP) {
            return GroupedReviewsViewHolder.newInstance(parent, this, onToggleView, reactionsCallback,
                    reviewCommentCallback, repoOwner, poster);
        } else if (viewType == TimelineModel.COMMIT_COMMENTS) {
            return CommitThreadViewHolder.Companion.newInstance(parent, this, onToggleView);
        } else if (viewType == TimelineModel.STATUS) {
            return PullStatusViewHolder.newInstance(parent);
        }
        return TimelineCommentsViewHolder.newInstance(parent, this, onToggleView, showEmojies,
                reactionsCallback, repoOwner, poster);
    }

    @Override protected void onBindView(BaseViewHolder holder, int position) {
        TimelineModel model = getItem(position);
        if (model.getType() == TimelineModel.HEADER) {
            ((IssueDetailsViewHolder) holder).bind(model);
        } else if (model.getType() == TimelineModel.EVENT) {
            ((IssueTimelineViewHolder) holder).bind(model);
        } else if (model.getType() == TimelineModel.COMMENT) {
            ((TimelineCommentsViewHolder) holder).bind(model);
        } else if (model.getType() == TimelineModel.GROUP) {
            ((GroupedReviewsViewHolder) holder).bind(model);
        } else if (model.getType() == TimelineModel.REVIEW) {
            ((ReviewsViewHolder) holder).bind(model);
        } else if (model.getType() == TimelineModel.COMMIT_COMMENTS) {
            ((CommitThreadViewHolder) holder).bind(model);
        } else if (model.getType() == TimelineModel.STATUS && model.getStatus() != null) {
            ((PullStatusViewHolder) holder).bind(model.getStatus());
        }
        if (model.getType() != TimelineModel.COMMENT) {
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setFullSpan(true);
        }
    }

    @Override public int getItemViewType(int position) {
        TimelineModel timelineModel = getData().get(position);
        return timelineModel != null ? timelineModel.getType() : super.getItemViewType(position);
    }


}

