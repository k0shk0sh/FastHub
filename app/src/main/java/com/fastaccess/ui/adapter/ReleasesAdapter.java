package com.fastaccess.ui.adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.fastaccess.data.dao.model.Release;
import com.fastaccess.ui.adapter.viewholder.ReleasesViewHolder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.List;

/**
 * Created by Kosh on 11 Nov 2016, 2:07 PM
 */

public class ReleasesAdapter extends BaseRecyclerAdapter<Release, ReleasesViewHolder, BaseViewHolder.OnItemClickListener<Release>> {

    public ReleasesAdapter(@NonNull List<Release> data) {
        super(data);
    }

    @Override protected ReleasesViewHolder viewHolder(ViewGroup parent, int viewType) {
        return ReleasesViewHolder.newInstance(parent, this);
    }

    @Override protected void onBindView(ReleasesViewHolder holder, int position) {
        holder.bind(getItem(position));
    }
}
