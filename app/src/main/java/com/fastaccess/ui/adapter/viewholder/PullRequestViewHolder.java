package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import butterknife.BindString;
import butterknife.BindView;

/**
 * Created by Kosh on 11 Nov 2016, 2:08 PM
 */

public class PullRequestViewHolder extends BaseViewHolder<PullRequest> {

    @BindView(R.id.title) FontTextView title;
    @Nullable @BindView(R.id.avatarLayout) AvatarLayout avatarLayout;
    @BindView(R.id.details) FontTextView details;
    @BindString(R.string.by) String by;
    private boolean withAvatar;

    private PullRequestViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter, boolean withAvatar) {
        super(itemView, adapter);
        this.withAvatar = withAvatar;
    }

    public static PullRequestViewHolder newInstance(ViewGroup viewGroup, BaseRecyclerAdapter adapter, boolean withAvatar) {
        if (withAvatar) {
            return new PullRequestViewHolder(getView(viewGroup, R.layout.issue_row_item), adapter, true);
        }
        return new PullRequestViewHolder(getView(viewGroup, R.layout.issue_no_image_row_item), adapter, false);
    }

    @Override public void bind(@NonNull PullRequest pullRequest) {
        title.setText(pullRequest.getTitle());
        details.setText(PullRequest.getMergeBy(pullRequest, details.getContext()));
        if (withAvatar && avatarLayout != null) {
            avatarLayout.setUrl(pullRequest.getUser().getAvatarUrl(), pullRequest.getUser().getLogin());
            avatarLayout.setVisibility(View.VISIBLE);
        }
    }
}
