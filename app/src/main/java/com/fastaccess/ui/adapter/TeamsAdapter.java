package com.fastaccess.ui.adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.fastaccess.data.dao.TeamsModel;
import com.fastaccess.ui.adapter.viewholder.TeamsViewHolder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;

import java.util.List;

/**
 * Created by Kosh on 03 Apr 2017, 7:52 PM
 */

public class TeamsAdapter extends BaseRecyclerAdapter<TeamsModel, TeamsViewHolder, TeamsViewHolder.OnItemClickListener<TeamsModel>> {

    public TeamsAdapter(@NonNull List<TeamsModel> data) {
        super(data);
    }

    @Override protected TeamsViewHolder viewHolder(ViewGroup parent, int viewType) {
        return TeamsViewHolder.newInstance(parent, this);
    }

    @Override protected void onBindView(TeamsViewHolder holder, int position) {
        holder.bind(getItem(position));
    }
}
