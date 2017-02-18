package com.fastaccess.ui.adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.fastaccess.data.dao.PullRequestModel;
import com.fastaccess.ui.adapter.viewholder.PullRequestViewHolder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.List;

/**
 * Created by Kosh on 11 Nov 2016, 2:07 PM
 */

public class PullRequestAdapter extends BaseRecyclerAdapter<PullRequestModel, PullRequestViewHolder, BaseViewHolder
        .OnItemClickListener<PullRequestModel>> {

    private boolean withAvatar;

    public PullRequestAdapter(@NonNull List<PullRequestModel> data) {
        this(data, false);
    }

    public PullRequestAdapter(@NonNull List<PullRequestModel> data, boolean withAvatar) {
        super(data);
        this.withAvatar = withAvatar;
    }

    @Override protected PullRequestViewHolder viewHolder(ViewGroup parent, int viewType) {
        return PullRequestViewHolder.newInstance(parent, this);
    }

    @Override protected void onBindView(PullRequestViewHolder holder, int position) {
        holder.bind(getItem(position), withAvatar);
    }
}
