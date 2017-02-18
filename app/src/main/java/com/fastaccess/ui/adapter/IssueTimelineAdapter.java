package com.fastaccess.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.ViewGroup;

import com.fastaccess.data.dao.IssueEventAdapterModel;
import com.fastaccess.ui.adapter.viewholder.IssueDetailsViewHolder;
import com.fastaccess.ui.adapter.viewholder.IssueTimelineViewHolder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.List;

/**
 * Created by Kosh on 13 Dec 2016, 1:44 AM
 */

public class IssueTimelineAdapter extends BaseRecyclerAdapter<IssueEventAdapterModel, BaseViewHolder,
        BaseViewHolder.OnItemClickListener<IssueEventAdapterModel>> {

    public IssueTimelineAdapter(@NonNull List<IssueEventAdapterModel> data) {
        super(data);
    }

    @Override protected BaseViewHolder viewHolder(ViewGroup parent, int viewType) {
        if (viewType == IssueEventAdapterModel.HEADER) {
            return IssueDetailsViewHolder.newInstance(parent, this);
        }
        return IssueTimelineViewHolder.newInstance(parent, this);
    }

    @Override protected void onBindView(BaseViewHolder holder, int position) {
        IssueEventAdapterModel model = getItem(position);
        if (model.getType() == IssueEventAdapterModel.HEADER) {
            ((IssueDetailsViewHolder) holder).bind(model);
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setFullSpan(true);
        } else {
            ((IssueTimelineViewHolder) holder).bind(model);
        }
    }

    @Override public int getItemViewType(int position) {
        return getData().get(position).getType();
    }
}

