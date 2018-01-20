package com.fastaccess.ui.modules.repos.code.commit.details.files;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.CommentRequestModel;
import com.fastaccess.data.dao.CommitFileChanges;
import com.fastaccess.data.dao.CommitLinesModel;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.ui.adapter.callback.OnToggleView;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.files.PullRequestFilesMvp;
import com.fastaccess.ui.modules.reviews.callback.ReviewCommentListener;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.List;

/**
 * Created by Kosh on 20 Nov 2016, 11:10 AM
 */

interface CommitFilesMvp {

    interface View extends BaseMvp.FAView, OnToggleView, PullRequestFilesMvp.OnPatchClickListener, ReviewCommentListener {

        void onNotifyAdapter(@Nullable List<CommitFileChanges> items);

        void onCommentAdded(@NonNull Comment newComment);

        void clearAdapter();

        void onOpenForResult(int position, CommitFileChanges model);
    }

    interface Presenter extends BaseMvp.FAPresenter, BaseViewHolder.OnItemClickListener<CommitFileChanges> {

        void onFragmentCreated(@Nullable Bundle bundle);

        void onSubmitComment(@NonNull String comment, @NonNull CommitLinesModel item, @Nullable Bundle bundle);

        void onSubmit(String username, String name, CommentRequestModel commentRequestModel);
    }


}
