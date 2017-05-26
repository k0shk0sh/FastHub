package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.GroupedNotificationModel;
import com.fastaccess.data.dao.model.Notification;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.ForegroundImageView;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import butterknife.BindView;

/**
 * Created by Kosh on 11 Nov 2016, 2:08 PM
 */

public class NotificationsViewHolder extends BaseViewHolder<GroupedNotificationModel> {

    @BindView(R.id.title) FontTextView title;
    @BindView(R.id.date) FontTextView date;
    @BindView(R.id.unsubsribe) ForegroundImageView unSubscribe;
    @BindView(R.id.markAsRead) ForegroundImageView markAsRead;
    @BindView(R.id.notificationType) ForegroundImageView notificationType;
    @BindView(R.id.repoName) FontTextView repoName;
    private boolean showUnreadState;

    private NotificationsViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter, boolean showUnreadState) {
        super(itemView, adapter);
        unSubscribe.setOnClickListener(this);
        markAsRead.setOnClickListener(this);
        this.showUnreadState = showUnreadState;
    }

    public static NotificationsViewHolder newInstance(@NonNull ViewGroup viewGroup, @Nullable BaseRecyclerAdapter adapter, boolean showUnreadState) {
        return new NotificationsViewHolder(getView(viewGroup, R.layout.notifications_row_item), adapter, showUnreadState);
    }

    @Override public void bind(@NonNull GroupedNotificationModel model) {
        Notification thread = model.getNotification();
        if (thread != null && thread.getSubject() != null) {
            title.setText(thread.getSubject().getTitle());
            int cardBackground = ViewHelper.getCardBackground(itemView.getContext());
            int color;
            date.setText(ParseDateFormat.getTimeAgo(thread.getUpdatedAt()));
            markAsRead.setVisibility(thread.isUnread() ? View.VISIBLE : View.GONE);
//            unSubscribe.setImageResource(thread.isIsSubscribed() ? R.drawable.ic_unsubscribe : R.drawable.ic_subscribe);
            if (thread.getSubject().getType() != null) {
                notificationType.setImageResource(thread.getSubject().getType().getDrawableRes());
                notificationType.setContentDescription(thread.getSubject().getType().name());
            } else {
                notificationType.setImageResource(R.drawable.ic_info_outline);
            }
            if (showUnreadState) {
                repoName.setVisibility(View.GONE);
                if (AppHelper.isNightMode(itemView.getResources())) {
                    color = ContextCompat.getColor(itemView.getContext(), R.color.material_blue_grey_800);
                } else {
                    color = ContextCompat.getColor(itemView.getContext(), R.color.material_blue_grey_200);
                }
                ((CardView) itemView).setCardBackgroundColor(thread.isUnread() ? color : cardBackground);
            } else {
                repoName.setVisibility(View.VISIBLE);
                repoName.setText(thread.getRepository().getFullName());
            }
        }
    }
}