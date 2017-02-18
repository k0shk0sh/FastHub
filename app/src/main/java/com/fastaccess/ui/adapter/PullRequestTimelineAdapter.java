package com.fastaccess.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.ViewGroup;

import com.fastaccess.data.dao.PullRequestAdapterModel;
import com.fastaccess.ui.adapter.viewholder.PullRequestDetailsViewHolder;
import com.fastaccess.ui.adapter.viewholder.PullRequestTimelineViewHolder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.List;

/**
 * Created by Kosh on 13 Dec 2016, 1:44 AM
 */

public class PullRequestTimelineAdapter extends BaseRecyclerAdapter<PullRequestAdapterModel, BaseViewHolder,
        BaseViewHolder.OnItemClickListener<PullRequestAdapterModel>> {

    public PullRequestTimelineAdapter(@NonNull List<PullRequestAdapterModel> data) {
        super(data);
    }

    @Override protected BaseViewHolder viewHolder(ViewGroup parent, int viewType) {
        if (viewType == PullRequestAdapterModel.HEADER) {
            return PullRequestDetailsViewHolder.newInstance(parent, this);
        }
        return PullRequestTimelineViewHolder.newInstance(parent, this);
    }

    @Override protected void onBindView(BaseViewHolder holder, int position) {
        PullRequestAdapterModel model = getItem(position);
        if (model.getType() == PullRequestAdapterModel.HEADER) {
            ((PullRequestDetailsViewHolder) holder).bind(model);
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setFullSpan(true);
        } else {
            ((PullRequestTimelineViewHolder) holder).bind(model);
        }
    }

    @Override public int getItemViewType(int position) {
        return getData().get(position).getType();
    }
}

