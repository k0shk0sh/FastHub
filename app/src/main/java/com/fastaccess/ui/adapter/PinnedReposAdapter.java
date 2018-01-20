package com.fastaccess.ui.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.fastaccess.data.dao.model.PinnedRepos;
import com.fastaccess.ui.adapter.viewholder.PinnedReposViewHolder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.List;

/**
 * Created by Kosh on 11 Nov 2016, 2:07 PM
 */

public class PinnedReposAdapter extends BaseRecyclerAdapter<PinnedRepos, PinnedReposViewHolder, BaseViewHolder.OnItemClickListener<PinnedRepos>> {

    private boolean singleLine;

    public PinnedReposAdapter(boolean singleLine) {
        this.singleLine = singleLine;
    }

    public PinnedReposAdapter(@NonNull List<PinnedRepos> data, @Nullable BaseViewHolder.OnItemClickListener<PinnedRepos> listener) {
        super(data, listener);
    }

    @Override protected PinnedReposViewHolder viewHolder(ViewGroup parent, int viewType) {
        return PinnedReposViewHolder.newInstance(parent, this, singleLine);
    }

    @Override protected void onBindView(PinnedReposViewHolder holder, int position) {
        holder.bind(getItem(position));
    }
}
