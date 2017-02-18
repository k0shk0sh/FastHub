package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.PullRequestAdapterModel;
import com.fastaccess.data.dao.PullRequestModel;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.provider.markdown.MarkDownProvider;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import butterknife.BindView;

/**
 * Created by Kosh on 13 Dec 2016, 1:03 AM
 */

public class PullRequestDetailsViewHolder extends BaseViewHolder<PullRequestAdapterModel> {

    @BindView(R.id.avatarView) AvatarLayout avatarView;
    @BindView(R.id.name) FontTextView name;
    @BindView(R.id.date) FontTextView date;
    @BindView(R.id.description) FontTextView description;

    private PullRequestDetailsViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter) {
        super(itemView, adapter);
    }

    public static PullRequestDetailsViewHolder newInstance(ViewGroup viewGroup, BaseRecyclerAdapter adapter) {
        return new PullRequestDetailsViewHolder(getView(viewGroup, R.layout.issue_detail_header_row_item), adapter);
    }

    @Override public void bind(@NonNull PullRequestAdapterModel model) {
        PullRequestModel issueModel = model.getPullRequest();
        avatarView.setUrl(issueModel.getUser().getAvatarUrl(), issueModel.getUser().getLogin());
        name.setText(issueModel.getUser().getLogin());
        date.setText(ParseDateFormat.getTimeAgo(issueModel.getCreatedAt()));
        description.setNestedScrollingEnabled(false);
        if (!InputHelper.isEmpty(issueModel.getBody())) {
            MarkDownProvider.setMdText(description, issueModel.getBody());
        }
    }
}
