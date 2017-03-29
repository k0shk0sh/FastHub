package com.fastaccess.ui.adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.fastaccess.data.dao.CommitFileModel;
import com.fastaccess.ui.adapter.callback.OnToggleView;
import com.fastaccess.ui.adapter.viewholder.CommitFilesViewHolder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;

/**
 * Created by Kosh on 11 Nov 2016, 2:07 PM
 */

public class CommitFilesAdapter extends BaseRecyclerAdapter<CommitFileModel, CommitFilesViewHolder, BaseViewHolder
        .OnItemClickListener<CommitFileModel>> {


    @NonNull private OnToggleView onToggleView;

    public CommitFilesAdapter(@NonNull ArrayList<CommitFileModel> eventsModels, @NonNull OnToggleView onToggleView) {
        super(eventsModels);
        this.onToggleView = onToggleView;
    }

    @Override protected CommitFilesViewHolder viewHolder(ViewGroup parent, int viewType) {
        return CommitFilesViewHolder.newInstance(parent, this, onToggleView);
    }

    @Override protected void onBindView(CommitFilesViewHolder holder, int position) {
        holder.bind(getItem(position));
    }
}
