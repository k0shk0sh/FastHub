package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.App;
import com.fastaccess.R;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.provider.timeline.HtmlHelper;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import butterknife.BindView;

/**
 * Created by Kosh on 11 Nov 2016, 2:08 PM
 */

public class CommentsViewHolder extends BaseViewHolder<Comment> {

    @BindView(R.id.avatarView) AvatarLayout avatar;
    @BindView(R.id.date) FontTextView date;
    @BindView(R.id.name) FontTextView name;
    @BindView(R.id.comment) FontTextView comment;
    @BindView(R.id.toggleHolder) View toggleHolder;
    @BindView(R.id.toggle) AppCompatImageView toggle;

    @Override public void onClick(View v) {
        if (v.getId() == R.id.toggleHolder) {
            toggle.callOnClick();
        } else {
            super.onClick(v);
        }
    }

    private CommentsViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter) {
        super(itemView, adapter);
        itemView.setOnClickListener(null);
        itemView.setOnLongClickListener(null);
        toggleHolder.setOnClickListener(this);
        toggle.setOnClickListener(this);
    }

    public static CommentsViewHolder newInstance(@NonNull ViewGroup viewGroup, @Nullable BaseRecyclerAdapter adapter) {
        return new CommentsViewHolder(getView(viewGroup, R.layout.no_emojies_comments_row_item), adapter);
    }

    @Override public void bind(@NonNull Comment commentsModel) {
        if (commentsModel.getUser() != null) {
            avatar.setUrl(commentsModel.getUser().getAvatarUrl(), commentsModel.getUser().getLogin(), commentsModel.getUser().isOrganizationType());
        } else {
            avatar.setUrl(null, null);
        }
        if (!InputHelper.isEmpty(commentsModel.getBodyHtml())) {
            HtmlHelper.htmlIntoTextView(comment, commentsModel.getBodyHtml());
        } else {
            comment.setText("");
        }
        name.setText(commentsModel.getUser() != null ? commentsModel.getUser().getLogin() : "Anonymous");
        if (commentsModel.getCreatedAt().before(commentsModel.getUpdatedAt())) {
            date.setText(ParseDateFormat.getTimeAgo(commentsModel.getCreatedAt()) + " " + App.getInstance().getResources().getString(R.string.edited));
        } else {
            date.setText(ParseDateFormat.getTimeAgo(commentsModel.getCreatedAt()));
        }
    }

}
