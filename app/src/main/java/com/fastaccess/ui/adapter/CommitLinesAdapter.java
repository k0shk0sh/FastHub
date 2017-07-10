package com.fastaccess.ui.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.fastaccess.data.dao.CommitLinesModel;
import com.fastaccess.ui.adapter.viewholder.CommitLinesViewHolder;
import com.fastaccess.ui.adapter.viewholder.SimpleViewHolder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;

import java.util.List;

public class CommitLinesAdapter extends BaseRecyclerAdapter<CommitLinesModel, CommitLinesViewHolder,
        SimpleViewHolder.OnItemClickListener<CommitLinesModel>> {

    public CommitLinesAdapter(@NonNull List<CommitLinesModel> data, @Nullable CommitLinesViewHolder.OnItemClickListener<CommitLinesModel> listener) {
        super(data, listener);
    }

    @Override protected CommitLinesViewHolder viewHolder(ViewGroup parent, int viewType) {
        return CommitLinesViewHolder.newInstance(parent, this);
    }

    @Override protected void onBindView(CommitLinesViewHolder holder, int position) {
        holder.bind(getItem(position));
    }
}