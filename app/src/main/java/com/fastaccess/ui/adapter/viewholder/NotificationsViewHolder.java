package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.NotificationThreadModel;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import butterknife.BindView;

/**
 * Created by Kosh on 11 Nov 2016, 2:08 PM
 */

public class NotificationsViewHolder extends BaseViewHolder<NotificationThreadModel> {

    @BindView(R.id.title) FontTextView title;
    @BindView(R.id.date) FontTextView date;
    @BindView(R.id.notificationTitle) FontTextView notificationTitle;

    @Override public void onClick(View v) {
        super.onClick(v);
    }

    private NotificationsViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter) {
        super(itemView, adapter);
    }

    public static NotificationsViewHolder newInstance(@NonNull ViewGroup viewGroup, @Nullable BaseRecyclerAdapter adapter) {
        return new NotificationsViewHolder(getView(viewGroup, R.layout.notifications_row_item), adapter);
    }

    @Override public void bind(@NonNull NotificationThreadModel thread) {
        if (thread.getSubject() != null) {
            notificationTitle.setText(thread.getSubject().getTitle());
        }
        title.setText(thread.getRepository() != null ? thread.getRepository().getFullName() : "");
        date.setText(ParseDateFormat.getTimeAgo(thread.getUpdatedAt()));
    }
}