package com.fastaccess.ui.adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.ui.adapter.viewholder.PullRequestViewHolder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.List;

/**
 * Created by Kosh on 11 Nov 2016, 2:07 PM
 */

public class PullRequestAdapter extends BaseRecyclerAdapter<PullRequest, PullRequestViewHolder, BaseViewHolder
        .OnItemClickListener<PullRequest>> {

    private boolean showRepoName;
    private boolean withAvatar;

    public PullRequestAdapter(@NonNull List<PullRequest> data) {
        this(data, false);
    }

    public PullRequestAdapter(@NonNull List<PullRequest> data, boolean withAvatar) {
        super(data);
        this.withAvatar = withAvatar;
    }

    public PullRequestAdapter(@NonNull List<PullRequest> data, boolean withAvatar, boolean showRepoName) {
        super(data);
        this.withAvatar = withAvatar;
        this.showRepoName = showRepoName;
    }

    @Override protected PullRequestViewHolder viewHolder(ViewGroup parent, int viewType) {
        return PullRequestViewHolder.newInstance(parent, this, withAvatar, showRepoName);
    }

    @Override protected void onBindView(PullRequestViewHolder holder, int position) {
        holder.bind(getItem(position));
    }


}
