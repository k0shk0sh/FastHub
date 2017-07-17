package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.model.Gist;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.provider.scheme.LinkParserHelper;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import butterknife.BindView;

/**
 * Created by Kosh on 11 Nov 2016, 2:08 PM
 */

public class GistsViewHolder extends BaseViewHolder<Gist> {

    @Nullable @BindView(R.id.avatarLayout) AvatarLayout avatar;
    @BindView(R.id.title) FontTextView title;
    @BindView(R.id.date) FontTextView date;
    private boolean isFromProfile;


    private GistsViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter, boolean isFromProfile) {
        super(itemView, adapter);
        title.setMaxLines(2);
        this.isFromProfile = isFromProfile;
    }

    public static GistsViewHolder newInstance(@NonNull ViewGroup viewGroup, @Nullable BaseRecyclerAdapter adapter, boolean isFromProfile) {
        if (!isFromProfile) {
            return new GistsViewHolder(getView(viewGroup, R.layout.feeds_row_item), adapter, false);
        } else {
            return new GistsViewHolder(getView(viewGroup, R.layout.feeds_row_no_image_item), adapter, true);
        }
    }

    @Override public void bind(@NonNull Gist item) {
        if (!isFromProfile) {
            if (avatar != null) {
                String url = item.getOwner() != null ? item.getOwner().getAvatarUrl() : item.getUser() != null ? item.getUser().getAvatarUrl() : null;
                String login = item.getOwner() != null ? item.getOwner().getLogin() : item.getUser() != null ? item.getUser().getLogin() : null;
                avatar.setUrl(url, login, false, LinkParserHelper.isEnterprise(
                        item.getOwner() != null ? item.getOwner().getHtmlUrl() : item.getUser() != null ? item.getUser().getHtmlUrl() : null));
            }
        }
        title.setText(item.getDisplayTitle(isFromProfile));
        date.setText(ParseDateFormat.getTimeAgo(item.getCreatedAt()));
    }
}
