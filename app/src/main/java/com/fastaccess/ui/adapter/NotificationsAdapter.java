package com.fastaccess.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.GroupedNotificationModel;
import com.fastaccess.ui.adapter.viewholder.NotificationsHeaderViewHolder;
import com.fastaccess.ui.adapter.viewholder.NotificationsViewHolder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;

/**
 * Created by Kosh on 11 Nov 2016, 2:07 PM
 */

public class NotificationsAdapter extends BaseRecyclerAdapter<GroupedNotificationModel, BaseViewHolder,
        BaseViewHolder.OnItemClickListener<GroupedNotificationModel>> {
    private boolean showUnreadState;
    private boolean hideClear;

    public NotificationsAdapter(@NonNull ArrayList<GroupedNotificationModel> eventsModels, boolean showUnreadState) {
        super(eventsModels);
        this.showUnreadState = showUnreadState;
    }

    public NotificationsAdapter(@NonNull ArrayList<GroupedNotificationModel> eventsModels, boolean showUnreadState, boolean hideClear) {
        super(eventsModels);
        this.showUnreadState = showUnreadState;
        this.hideClear = hideClear;
    }

    @Override protected BaseViewHolder viewHolder(ViewGroup parent, int viewType) {
        if (viewType == GroupedNotificationModel.HEADER) {
            return NotificationsHeaderViewHolder.newInstance(parent, this);
        } else {
            return NotificationsViewHolder.newInstance(parent, this, showUnreadState);
        }
    }

    @Override protected void onBindView(BaseViewHolder holder, int position) {
        if (getItemViewType(position) == GroupedNotificationModel.HEADER) {
            ((NotificationsHeaderViewHolder) holder).bind(getItem(position));
            if (hideClear)
                if (getItem(Math.min(position + 1, getItemCount() - 1)).getNotification().isUnread()) {
                    (((NotificationsHeaderViewHolder) holder).itemView).findViewById(R.id.markAsRead).setVisibility(View.VISIBLE);
                }
        } else {
            ((NotificationsViewHolder) holder).bind(getItem(position));
        }
        if (getItem(position).getType() == GroupedNotificationModel.HEADER) {
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setFullSpan(true);
        }
    }

    @Override public int getItemViewType(int position) {
        return getItem(position).getType();
    }
}
