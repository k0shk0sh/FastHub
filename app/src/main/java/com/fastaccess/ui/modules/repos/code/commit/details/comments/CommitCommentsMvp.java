package com.fastaccess.ui.modules.repos.code.commit.details.comments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.adapter.callback.OnToggleView;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;

/**
 * Created by Kosh on 20 Nov 2016, 11:10 AM
 */

interface CommitCommentsMvp {

    interface View extends BaseMvp.FAView, SwipeRefreshLayout.OnRefreshListener,
            android.view.View.OnClickListener, OnToggleView {

        void onNotifyAdapter();

        @NonNull OnLoadMore getLoadMore();

        void onEditComment(@NonNull Comment item);

        void onStartNewComment();

        void onShowDeleteMsg(long id);

        void onTagUser(@Nullable User user);

    }

    interface Presenter extends BaseMvp.FAPresenter,
            BaseMvp.PaginationListener<String>, BaseViewHolder.OnItemClickListener<Comment> {

        void onFragmentCreated(@Nullable Bundle bundle);

        @NonNull ArrayList<Comment> getComments();

        void onHandleDeletion(@Nullable Bundle bundle);

        void onWorkOffline();

        @NonNull String repoId();

        @NonNull String login();

        String sha();
    }


}
