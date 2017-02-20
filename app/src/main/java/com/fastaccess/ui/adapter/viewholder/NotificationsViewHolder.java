package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.NotificationThreadModel;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import butterknife.BindView;

/**
 * Created by Kosh on 11 Nov 2016, 2:08 PM
 */

public class NotificationsViewHolder extends BaseViewHolder<NotificationThreadModel> {

    @BindView(R.id.avatarLayout) AvatarLayout avatarLayout;
    @BindView(R.id.title) FontTextView title;
    @BindView(R.id.stars) FontTextView stars;
    @BindView(R.id.forks) FontTextView forks;
    @BindView(R.id.date) FontTextView date;

    @Override public void onClick(View v) {
        super.onClick(v);
    }

    private NotificationsViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter) {
        super(itemView, adapter);
    }

    public static NotificationsViewHolder newInstance(@NonNull ViewGroup viewGroup, @Nullable BaseRecyclerAdapter adapter) {
        return new NotificationsViewHolder(getView(viewGroup, R.layout.repos_row_item), adapter);
    }

    @Override public void bind(@NonNull NotificationThreadModel thread) {
        avatarLayout.setVisibility(View.GONE);
        forks.setVisibility(View.GONE);
        stars.setCompoundDrawables(null, null, null, null);
        if (thread.getSubject() != null) {
            title.setText(thread.getSubject().getTitle());
            stars.setText(thread.getSubject().getType());
        }
        date.setText(ParseDateFormat.getTimeAgo(thread.getUpdatedAt()));
    }
}
