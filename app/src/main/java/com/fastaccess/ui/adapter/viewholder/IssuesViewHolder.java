package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.PullsIssuesParser;
import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.data.dao.types.IssueState;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import butterknife.BindString;
import butterknife.BindView;

/**
 * Created by Kosh on 11 Nov 2016, 2:08 PM
 */

public class IssuesViewHolder extends BaseViewHolder<Issue> {

    @BindView(R.id.title) FontTextView title;
    @Nullable @BindView(R.id.avatarLayout) AvatarLayout avatarLayout;
    @BindView(R.id.details) FontTextView details;
    @BindView(R.id.commentsNo) FontTextView commentsNo;
    @BindString(R.string.by) String by;

    private boolean withAvatar;
    private boolean showRepoName;

    private IssuesViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter,
                             boolean withAvatar, boolean showRepoName) {
        super(itemView, adapter);
        this.withAvatar = withAvatar;
        this.showRepoName = showRepoName;
    }

    public static IssuesViewHolder newInstance(ViewGroup viewGroup, BaseRecyclerAdapter adapter, boolean withAvatar, boolean showRepoName) {
        if (withAvatar) {
            return new IssuesViewHolder(getView(viewGroup, R.layout.issue_row_item), adapter, true, showRepoName);
        } else {
            return new IssuesViewHolder(getView(viewGroup, R.layout.issue_no_image_row_item), adapter, false, showRepoName);
        }
    }

    @Override public void bind(@NonNull Issue issueModel) {
        title.setText(issueModel.getTitle());
        if (issueModel.getState() != null) {
            CharSequence data = ParseDateFormat.getTimeAgo(issueModel.getState() == IssueState.open
                                                           ? issueModel.getCreatedAt() : issueModel.getClosedAt());
            SpannableBuilder builder = SpannableBuilder.builder();
            if (showRepoName) {
                PullsIssuesParser parser = PullsIssuesParser.getForIssue(issueModel.getHtmlUrl());
                if (parser != null) builder.bold(parser.getLogin())
                        .append("/")
                        .bold(parser.getRepoId())
                        .bold("#")
                        .bold(String.valueOf(issueModel.getNumber())).append(" ")
                        .append(" ");
            }
            if (!showRepoName) {
                if (issueModel.getState() == IssueState.closed) {
                    if (issueModel.getClosedBy() == null) {
                        builder.bold("#")
                                .bold(String.valueOf(issueModel.getNumber())).append(" ")
                                .append(" ");
                    } else {
                        builder.append("#")
                                .append(String.valueOf(issueModel.getNumber())).append(" ")
                                .append(issueModel.getClosedBy().getLogin())
                                .append(" ");
                    }
                } else {
                    builder.bold("#")
                            .bold(String.valueOf(issueModel.getNumber())).append(" ")
                            .append(issueModel.getUser().getLogin())
                            .append(" ");
                }
            }
            details.setText(builder
                    .append(itemView.getResources().getString(issueModel.getState().getStatus()).toLowerCase())
                    .append(" ")
                    .append(data));
            if (issueModel.getComments() > 0) {
                commentsNo.setText(String.valueOf(issueModel.getComments()));
                commentsNo.setVisibility(View.VISIBLE);
            } else {
                commentsNo.setVisibility(View.GONE);
            }
        }
        if (withAvatar && avatarLayout != null) {
            avatarLayout.setUrl(issueModel.getUser().getAvatarUrl(), issueModel.getUser().getLogin());
            avatarLayout.setVisibility(View.VISIBLE);
        }
    }
}
