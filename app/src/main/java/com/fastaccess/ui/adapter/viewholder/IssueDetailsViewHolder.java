package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.TimelineModel;
import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;
import com.prettifier.pretty.PrettifyWebView;

import butterknife.BindView;

/**
 * Created by Kosh on 13 Dec 2016, 1:03 AM
 */

public class IssueDetailsViewHolder extends BaseViewHolder<TimelineModel> {

    @BindView(R.id.avatarView) AvatarLayout avatarView;
    @BindView(R.id.name) FontTextView name;
    @BindView(R.id.date) FontTextView date;
    @BindView(R.id.description) PrettifyWebView description;

    private IssueDetailsViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter) {
        super(itemView, adapter);
    }

    public static IssueDetailsViewHolder newInstance(ViewGroup viewGroup, BaseRecyclerAdapter adapter) {
        return new IssueDetailsViewHolder(getView(viewGroup, R.layout.issue_detail_header_row_item), adapter);
    }

    @Override public void bind(@NonNull TimelineModel timelineModel) {
        if (timelineModel.getIssue() != null) {
            bind(timelineModel.getIssue());
        } else if (timelineModel.getPullRequest() != null) {
            bind(timelineModel.getPullRequest());
        }
    }

    private void bind(@NonNull Issue issueModel) {
        avatarView.setUrl(issueModel.getUser().getAvatarUrl(), issueModel.getUser().getLogin());
        name.setText(issueModel.getUser().getLogin());
        date.setText(ParseDateFormat.getTimeAgo(issueModel.getCreatedAt()));
        if (!InputHelper.isEmpty(issueModel.getBodyHtml())) {
            description.setNestedScrollingEnabled(false);
            description.setGithubContent(issueModel.getBodyHtml(), issueModel.getHtmlUrl(), true);
        } else {
            description.setGithubContent(itemView.getResources().getString(R.string.no_description_provided), null, true);
        }
    }

    private void bind(@NonNull PullRequest issueModel) {
        avatarView.setUrl(issueModel.getUser().getAvatarUrl(), issueModel.getUser().getLogin());
        name.setText(issueModel.getUser().getLogin());
        date.setText(ParseDateFormat.getTimeAgo(issueModel.getCreatedAt()));
        if (!InputHelper.isEmpty(issueModel.getBodyHtml())) {
            description.setNestedScrollingEnabled(false);
            description.setGithubContent(issueModel.getBodyHtml(), issueModel.getHtmlUrl(), true);
        } else {
            description.setGithubContent(itemView.getResources().getString(R.string.no_description_provided), null, true);
        }
    }
}
