package com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.files;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import com.fastaccess.data.dao.CommentRequestModel;
import com.fastaccess.data.dao.CommitFileChanges;
import com.fastaccess.data.dao.CommitFileModel;
import com.fastaccess.data.dao.CommitLinesModel;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.adapter.callback.OnToggleView;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.modules.reviews.callback.ReviewCommentListener;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kosh on 03 Dec 2016, 3:45 PM
 */

public interface PullRequestFilesMvp {

    interface View extends BaseMvp.FAView, SwipeRefreshLayout.OnRefreshListener, android.view.View.OnClickListener,
            OnToggleView, OnPatchClickListener, ReviewCommentListener {
        void onNotifyAdapter(@Nullable List<CommitFileChanges> items, int page);

        @NonNull OnLoadMore getLoadMore();

        void onOpenForResult(int position, @NonNull CommitFileChanges linesModel);
    }

    interface Presenter extends BaseMvp.FAPresenter,
            BaseViewHolder.OnItemClickListener<CommitFileChanges>,
            BaseMvp.PaginationListener {
        void onFragmentCreated(@NonNull Bundle bundle);

        @NonNull ArrayList<CommitFileChanges> getFiles();

        void onWorkOffline();
    }

    interface OnPatchClickListener {
        void onPatchClicked(int groupPosition, int childPosition, android.view.View v, CommitFileModel commit, CommitLinesModel item);
    }

    interface PatchCallback {
        void onAddComment(CommentRequestModel comment);
    }

    interface CommitCommentCallback {
        @NonNull ArrayList<CommentRequestModel> getCommitComment();

        void onAddComment(@NonNull CommentRequestModel comment);

        boolean hasReviewComments();
    }
}
