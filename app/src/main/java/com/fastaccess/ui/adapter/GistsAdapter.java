package com.fastaccess.ui.adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.fastaccess.data.dao.model.Gist;
import com.fastaccess.ui.adapter.viewholder.GistsViewHolder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;

/**
 * Created by Kosh on 11 Nov 2016, 2:07 PM
 */

public class GistsAdapter extends BaseRecyclerAdapter<Gist, GistsViewHolder, BaseViewHolder.OnItemClickListener<Gist>> {

    private boolean isForProfile;

    public GistsAdapter(@NonNull ArrayList<Gist> gistModels) {
        this(gistModels, false);
    }

    public GistsAdapter(@NonNull ArrayList<Gist> gistsModels, boolean isForProfile) {
        super(gistsModels);
        this.isForProfile = isForProfile;
    }

    @Override protected GistsViewHolder viewHolder(ViewGroup parent, int viewType) {
        return GistsViewHolder.newInstance(parent, this, isForProfile);
    }

    @Override protected void onBindView(GistsViewHolder holder, int position) {
        holder.bind(getItem(position));
    }
}
