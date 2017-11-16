package com.fastaccess.ui.modules.repos.code.commit.details.comments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import com.fastaccess.data.dao.TimelineModel;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.data.dao.types.ReactionTypes;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.adapter.callback.OnToggleView;
import com.fastaccess.ui.adapter.callback.ReactionsCallback;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kosh on 20 Nov 2016, 11:10 AM
 */

interface CommitCommentsMvp {

    interface View extends BaseMvp.FAView, SwipeRefreshLayout.OnRefreshListener,
            android.view.View.OnClickListener, OnToggleView, ReactionsCallback {

        void onNotifyAdapter(@Nullable List<TimelineModel> items, int page);

        void onRemove(@NonNull TimelineModel comment);

        @NonNull OnLoadMore getLoadMore();

        void onEditComment(@NonNull Comment item);

        void onShowDeleteMsg(long id);

        void onTagUser(@Nullable User user);

        void onReply(User user, String message);

        void showReactionsPopup(@NonNull ReactionTypes reactionTypes, @NonNull String login, @NonNull String repoId, long commentId);

        void addComment(@NonNull Comment newComment);

        void showReload();

        void onHandleComment(@NonNull String text, @Nullable Bundle bundle);

        @NonNull List<String> getNamesToTags();

        void hideBlockingProgress();
    }

    interface Presenter extends BaseMvp.FAPresenter,
            BaseMvp.PaginationListener<String>, BaseViewHolder.OnItemClickListener<TimelineModel> {

        void onFragmentCreated(@Nullable Bundle bundle);

        @NonNull ArrayList<TimelineModel> getComments();

        void onHandleDeletion(@Nullable Bundle bundle);

        void onWorkOffline();

        @NonNull String repoId();

        @NonNull String login();

        String sha();

        boolean isPreviouslyReacted(long commentId, int vId);

        boolean isCallingApi(long id, int vId);

        void onHandleComment(@NonNull String text, @Nullable Bundle bundle);
    }


}
