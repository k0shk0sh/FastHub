package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.GistsModel;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import butterknife.BindView;

/**
 * Created by Kosh on 11 Nov 2016, 2:08 PM
 */

public class GistsViewHolder extends BaseViewHolder<GistsModel> {

    @BindView(R.id.avatarLayout) AvatarLayout avatar;
    @BindView(R.id.title) FontTextView title;
    @BindView(R.id.date) FontTextView date;

    public GistsViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter) {
        super(itemView, adapter);
        title.setMaxLines(2);
    }

    public static View getView(@NonNull ViewGroup viewGroup) {
        return getView(viewGroup, R.layout.feeds_row_item);
    }

    public void bind(@NonNull GistsModel item, boolean isFromProfile) {
        if (!isFromProfile) {
            avatar.setVisibility(View.VISIBLE);
            String url = item.getOwner() != null ? item.getOwner().getAvatarUrl() : item.getUser() != null ? item.getUser().getAvatarUrl() : null;
            String login = item.getOwner() != null ? item.getOwner().getLogin() : item.getUser() != null ? item.getUser().getLogin() : null;
            avatar.setUrl(url, login);
        } else {
            avatar.setVisibility(View.GONE);
        }
        title.setText(item.getDisplayTitle(isFromProfile));
        date.setText(ParseDateFormat.getTimeAgo(item.getCreatedAt()));
    }

    @Override public void bind(@NonNull GistsModel item) {}
}
