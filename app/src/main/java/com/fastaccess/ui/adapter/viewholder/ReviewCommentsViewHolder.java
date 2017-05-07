package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.ReviewCommentModel;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.provider.timeline.HtmlHelper;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import butterknife.BindView;

/**
 * Created by Kosh on 15 Feb 2017, 10:29 PM
 */

public class ReviewCommentsViewHolder extends BaseViewHolder<ReviewCommentModel> {

    @BindView(R.id.avatarView) AvatarLayout avatarView;
    @BindView(R.id.name) FontTextView name;
    @BindView(R.id.date) FontTextView date;
    @BindView(R.id.comment) FontTextView comment;

    private ReviewCommentsViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter) {
        super(itemView, adapter);
    }

    public static ReviewCommentsViewHolder newInstance(ViewGroup viewGroup, BaseRecyclerAdapter adapter) {
        return new ReviewCommentsViewHolder(getView(viewGroup, R.layout.review_comments_row_item), adapter);
    }

    @Override public void bind(@NonNull ReviewCommentModel commentModel) {
        avatarView.setUrl(commentModel.getUser().getAvatarUrl(), commentModel.getUser().getLogin(), commentModel.getUser().isOrganizationType());
        name.setText(commentModel.getUser().getLogin());
        date.setText(ParseDateFormat.getTimeAgo(commentModel.getCreatedAt()));
        if (!InputHelper.isEmpty(commentModel.getBodyHtml())) {
            HtmlHelper.htmlIntoTextView(comment, commentModel.getBodyHtml());
        } else {
            comment.setText("");
        }
    }
}
