package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.helper.Logger;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import butterknife.BindView;

/**
 * Created by Kosh on 11 Nov 2016, 2:08 PM
 */

public class UsersViewHolder extends BaseViewHolder<User> {

    @BindView(R.id.avatarLayout) AvatarLayout avatar;
    @BindView(R.id.title) FontTextView title;
    @BindView(R.id.date) FontTextView date;

    public UsersViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter) {
        super(itemView, adapter);
    }

    public static View getView(@NonNull ViewGroup viewGroup) {
        return getView(viewGroup, R.layout.feeds_row_item);
    }

    @Override public void onClick(View v) {
        avatar.findViewById(R.id.avatar).callOnClick();
    }

    @Override public void bind(@NonNull User user) {}

    public void bind(@NonNull User user, boolean isContributor) {
        Logger.e(user.isOrganizationType(), user.getType());
        avatar.setUrl(user.getAvatarUrl(), user.getLogin(), user.isOrganizationType());
        title.setText(user.getLogin());
        date.setVisibility(!isContributor ? View.GONE : View.VISIBLE);
        if (isContributor) {
            date.setText(String.format("%s (%s)", date.getResources().getString(R.string.commits), user.getContributions()));
        }
    }
}
