package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.ui.AssigneesAdapter;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import butterknife.BindColor;
import butterknife.BindView;

/**
 * Created by Kosh on 11 Nov 2016, 2:08 PM
 */

public class AssigneesViewHolder extends BaseViewHolder<User> {

    @BindView(R.id.avatarLayout) AvatarLayout avatar;
    @BindView(R.id.title) FontTextView title;
    @BindView(R.id.date) FontTextView date;
    @BindColor(R.color.light_gray) int lightGray;
    private final AssigneesAdapter.OnSelectAssignee onSelectAssignee;

    @Override public void onClick(View v) {
        int position = getAdapterPosition();
        onSelectAssignee.onToggleSelection(position, !onSelectAssignee.isAssigneeSelected(position));
    }

    private AssigneesViewHolder(@NonNull View itemView, @NonNull AssigneesAdapter.OnSelectAssignee onSelectAssignee) {
        super(itemView);
        this.onSelectAssignee = onSelectAssignee;
    }

    public static AssigneesViewHolder newInstance(@NonNull ViewGroup viewGroup, @NonNull AssigneesAdapter.OnSelectAssignee onSelectAssignee) {
        return new AssigneesViewHolder(getView(viewGroup, R.layout.feeds_row_item), onSelectAssignee);
    }

    @Override public void bind(@NonNull User user) {
        avatar.setUrl(user.getAvatarUrl(), user.getLogin(), user.isOrganizationType());
        title.setText(user.getLogin());
        date.setVisibility(View.GONE);
        itemView.setBackgroundColor(onSelectAssignee.isAssigneeSelected(getAdapterPosition()) ? lightGray : ViewHelper.getWindowBackground(itemView
                .getContext()));
    }
}
