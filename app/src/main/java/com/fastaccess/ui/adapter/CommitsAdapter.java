package com.fastaccess.ui.adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.fastaccess.data.dao.CommitModel;
import com.fastaccess.ui.adapter.viewholder.CommitsViewHolder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.List;

/**
 * Created by Kosh on 11 Nov 2016, 2:07 PM
 */

public class CommitsAdapter extends BaseRecyclerAdapter<CommitModel, CommitsViewHolder, BaseViewHolder.OnItemClickListener<CommitModel>> {

    public CommitsAdapter(@NonNull List<CommitModel> data) {
        super(data);
    }

    @Override protected CommitsViewHolder viewHolder(ViewGroup parent, int viewType) {
        return CommitsViewHolder.newInstance(parent, this);
    }

    @Override protected void onBindView(CommitsViewHolder holder, int position) {
        holder.bind(getItem(position));
    }
}
