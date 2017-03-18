package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;
import com.prettifier.pretty.PrettifyWebView;

import butterknife.BindView;

/**
 * Created by Kosh on 11 Nov 2016, 2:08 PM
 */

public class CommentsViewHolder extends BaseViewHolder<Comment> {

    @BindView(R.id.avatarView) AvatarLayout avatar;
    @BindView(R.id.date) FontTextView date;
    @BindView(R.id.name) FontTextView name;
    @BindView(R.id.comment) PrettifyWebView comment;

    @Override public void onClick(View v) {
        super.onClick(v);
    }

    private CommentsViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter) {
        super(itemView, adapter);
//        comment.setOnLongClickListener(this);
//        comment.setOnClickListener(this);
    }

    public static CommentsViewHolder newInstance(@NonNull ViewGroup viewGroup, @Nullable BaseRecyclerAdapter adapter) {
        return new CommentsViewHolder(getView(viewGroup, R.layout.comments_row_item), adapter);
    }

    @Override public void bind(@NonNull Comment commentsModel) {
        if (commentsModel.getUser() != null) {
            avatar.setUrl(commentsModel.getUser().getAvatarUrl(), commentsModel.getUser().getLogin());
        } else {
            avatar.setUrl(null, null);
        }
        if (!InputHelper.isEmpty(commentsModel.getBodyHtml())) {
            comment.setNestedScrollingEnabled(false);
            comment.setGithubContent(commentsModel.getBodyHtml(), null, true);
        }
        name.setText(commentsModel.getUser() != null ? commentsModel.getUser().getLogin() : "Anonymous");
        date.setText(ParseDateFormat.getTimeAgo(commentsModel.getCreatedAt()));
    }
}
