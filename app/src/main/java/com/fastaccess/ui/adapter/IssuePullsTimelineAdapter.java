package com.fastaccess.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.ViewGroup;

import com.fastaccess.data.dao.CommentsLabelsModel;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.ui.adapter.callback.OnToggleView;
import com.fastaccess.ui.adapter.viewholder.IssueDetailsViewHolder;
import com.fastaccess.ui.adapter.viewholder.IssueTimelineViewHolder;
import com.fastaccess.ui.adapter.viewholder.TimelineCommentsViewHolder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.List;

/**
 * Created by Kosh on 13 Dec 2016, 1:44 AM
 */

public class IssuePullsTimelineAdapter extends BaseRecyclerAdapter<CommentsLabelsModel, BaseViewHolder,
        BaseViewHolder.OnItemClickListener<CommentsLabelsModel>> {

    private final OnToggleView onToggleView;
    private final String login;
    private final boolean showEmojies;

    public IssuePullsTimelineAdapter(@NonNull List<CommentsLabelsModel> data, OnToggleView onToggleView, boolean showEmojies) {
        super(data);
        this.onToggleView = onToggleView;
        this.login = Login.getUser().getLogin();
        this.showEmojies = showEmojies;
    }

    @Override protected BaseViewHolder viewHolder(ViewGroup parent, int viewType) {
        if (viewType == CommentsLabelsModel.HEADER) {
            return IssueDetailsViewHolder.newInstance(parent, this);
        } else if (viewType == CommentsLabelsModel.EVENT) {
            return IssueTimelineViewHolder.newInstance(parent, this);
        }
        return TimelineCommentsViewHolder.newInstance(parent, this, login, onToggleView, showEmojies);
    }

    @Override protected void onBindView(BaseViewHolder holder, int position) {
        CommentsLabelsModel model = getItem(position);
        if (model.getType() == CommentsLabelsModel.HEADER) {
            if (model.getIssue() != null) {
                ((IssueDetailsViewHolder) holder).bind(model);
            } else if (model.getPullRequest() != null) {
                ((IssueDetailsViewHolder) holder).bind(model);
            }
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setFullSpan(true);
        } else if (model.getType() == CommentsLabelsModel.EVENT) {
            ((IssueTimelineViewHolder) holder).bind(model);
        } else {
            ((TimelineCommentsViewHolder) holder).bind(model);
        }
    }

    @Override public int getItemViewType(int position) {
        return getData().get(position).getType();
    }
}

