package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.model.Commit;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.provider.scheme.LinkParserHelper;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.Date;

import butterknife.BindView;

/**
 * Created by Kosh on 11 Nov 2016, 2:08 PM
 */

public class CommitsViewHolder extends BaseViewHolder<Commit> {

    @BindView(R.id.title) FontTextView title;
    @BindView(R.id.avatarLayout) AvatarLayout avatarLayout;
    @BindView(R.id.details) FontTextView details;
    @BindView(R.id.commentsNo) FontTextView commentsNo;

    private CommitsViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter) {
        super(itemView, adapter);
    }

    public static CommitsViewHolder newInstance(ViewGroup viewGroup, BaseRecyclerAdapter adapter) {
        return new CommitsViewHolder(getView(viewGroup, R.layout.issue_row_item), adapter);
    }

    @Override public void bind(@NonNull Commit commit) {
        title.setText(commit.getGitCommit().getMessage());
        String login = commit.getAuthor() != null ? commit.getAuthor().getLogin() : commit.getGitCommit().getAuthor().getLogin();
        String avatar = commit.getAuthor() != null ? commit.getAuthor().getAvatarUrl() : null;
        Date date = commit.getGitCommit().getAuthor().getDate();
        details.setText(SpannableBuilder.builder()
                .bold(InputHelper.toNA(login))
                .append(" ")
                .append(ParseDateFormat.getTimeAgo(date)));
        avatarLayout.setUrl(avatar, login, false, LinkParserHelper
                .isEnterprise(commit.getAuthor() != null ? commit.getAuthor().getUrl() : commit.getGitCommit().getAuthor().getHtmlUrl()));
        avatarLayout.setVisibility(View.VISIBLE);
        if (commit.getGitCommit() != null && commit.getGitCommit().getCommentCount() > 0) {
            commentsNo.setText(String.valueOf(commit.getGitCommit() != null ? commit.getGitCommit().getCommentCount() : 0));
            commentsNo.setVisibility(View.VISIBLE);
        } else {
            commentsNo.setVisibility(View.GONE);
        }
    }
}
