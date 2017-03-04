package com.fastaccess.ui.adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.fastaccess.data.dao.MilestoneModel;
import com.fastaccess.ui.adapter.viewholder.MilestonesViewHolder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;

/**
 * Created by Kosh on 11 Nov 2016, 2:07 PM
 */

public class MilestonesAdapter extends BaseRecyclerAdapter<MilestoneModel, MilestonesViewHolder,
        BaseViewHolder.OnItemClickListener<MilestoneModel>> {

    public MilestonesAdapter(@NonNull ArrayList<MilestoneModel> eventsModels) {
        super(eventsModels);
    }

    @Override protected MilestonesViewHolder viewHolder(ViewGroup parent, int viewType) {
        return MilestonesViewHolder.newInstance(parent, this);
    }

    @Override protected void onBindView(MilestonesViewHolder holder, int position) {
        holder.bind(getItem(position));
    }
}
