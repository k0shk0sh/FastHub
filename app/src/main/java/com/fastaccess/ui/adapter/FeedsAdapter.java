package com.fastaccess.ui.adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.fastaccess.data.dao.EventsModel;
import com.fastaccess.ui.adapter.viewholder.FeedsViewHolder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;

/**
 * Created by Kosh on 11 Nov 2016, 2:07 PM
 */

public class FeedsAdapter extends BaseRecyclerAdapter<EventsModel, FeedsViewHolder, BaseViewHolder.OnItemClickListener<EventsModel>> {

    public FeedsAdapter(@NonNull ArrayList<EventsModel> eventsModels) {
        super(eventsModels);
    }

    @Override protected FeedsViewHolder viewHolder(ViewGroup parent, int viewType) {
        return new FeedsViewHolder(FeedsViewHolder.getView(parent), this);
    }

    @Override protected void onBindView(FeedsViewHolder holder, int position) {
        holder.bind(getItem(position));
    }
}
