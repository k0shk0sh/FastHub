package com.fastaccess.ui.adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.fastaccess.data.dao.model.RepoFile;
import com.fastaccess.ui.adapter.viewholder.RepoFilePathsViewHolder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;

/**
 * Created by Kosh on 11 Nov 2016, 2:07 PM
 */

public class RepoFilePathsAdapter extends BaseRecyclerAdapter<RepoFile, RepoFilePathsViewHolder, BaseViewHolder
        .OnItemClickListener<RepoFile>> {


    public RepoFilePathsAdapter(@NonNull ArrayList<RepoFile> eventsModels) {
        super(eventsModels);
    }

    @Override protected RepoFilePathsViewHolder viewHolder(ViewGroup parent, int viewType) {
        return RepoFilePathsViewHolder.newInstance(parent, this);
    }

    @Override protected void onBindView(RepoFilePathsViewHolder holder, int position) {
        holder.bind(getItem(position));
    }
}
