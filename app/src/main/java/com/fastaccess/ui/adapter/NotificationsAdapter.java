package com.fastaccess.ui.adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.fastaccess.data.dao.model.Notification;
import com.fastaccess.ui.adapter.viewholder.NotificationsViewHolder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;

/**
 * Created by Kosh on 11 Nov 2016, 2:07 PM
 */

public class NotificationsAdapter extends BaseRecyclerAdapter<Notification, NotificationsViewHolder,
        BaseViewHolder.OnItemClickListener<Notification>> {

    public NotificationsAdapter(@NonNull ArrayList<Notification> eventsModels) {
        super(eventsModels);
    }

    @Override protected NotificationsViewHolder viewHolder(ViewGroup parent, int viewType) {
        return NotificationsViewHolder.newInstance(parent, this);
    }

    @Override protected void onBindView(NotificationsViewHolder holder, int position) {
        holder.bind(getItem(position));
    }
}
