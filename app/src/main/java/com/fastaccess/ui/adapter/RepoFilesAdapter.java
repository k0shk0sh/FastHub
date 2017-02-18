package com.fastaccess.ui.adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.fastaccess.data.dao.RepoFilesModel;
import com.fastaccess.ui.adapter.viewholder.RepoFilesViewHolder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;

/**
 * Created by Kosh on 11 Nov 2016, 2:07 PM
 */

public class RepoFilesAdapter extends BaseRecyclerAdapter<RepoFilesModel, RepoFilesViewHolder, BaseViewHolder
        .OnItemClickListener<RepoFilesModel>> {


    public RepoFilesAdapter(@NonNull ArrayList<RepoFilesModel> eventsModels) {
        super(eventsModels);
    }

    @Override protected RepoFilesViewHolder viewHolder(ViewGroup parent, int viewType) {
        return RepoFilesViewHolder.newInstance(parent, this);
    }

    @Override protected void onBindView(RepoFilesViewHolder holder, int position) {
        holder.bind(getItem(position));
    }
}
