package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageButton;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.GroupedNotificationModel;
import com.fastaccess.data.dao.model.Repo;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import butterknife.BindView;

/**
 * Created by Kosh on 11 Nov 2016, 2:08 PM
 */

public class NotificationsHeaderViewHolder extends BaseViewHolder<GroupedNotificationModel> {

    @Nullable @BindView(R.id.headerTitle) FontTextView headerTitle;
    @BindView(R.id.markAsRead) AppCompatImageButton markAsRead;

    private NotificationsHeaderViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter) {
        super(itemView, adapter);
        markAsRead.setOnClickListener(this);
    }

    public static NotificationsHeaderViewHolder newInstance(@NonNull ViewGroup viewGroup, @Nullable BaseRecyclerAdapter adapter) {
        return new NotificationsHeaderViewHolder(getView(viewGroup, R.layout.notification_header_row_item), adapter);
    }

    @Override public void bind(@NonNull GroupedNotificationModel model) {
        Repo repo = model.getRepo();
        if (repo != null && headerTitle != null) {
            headerTitle.setText(repo.getFullName());
        }
    }
}