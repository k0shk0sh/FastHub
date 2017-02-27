package com.fastaccess.ui.modules.repos.issues.issue.details.comments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import com.fastaccess.data.dao.CommentsModel;
import com.fastaccess.data.dao.UserModel;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;

import retrofit2.Response;

/**
 * Created by Kosh on 20 Nov 2016, 11:10 AM
 */

interface IssueCommentsMvp {

    interface View extends BaseMvp.FAView, SwipeRefreshLayout.OnRefreshListener,
            android.view.View.OnClickListener {

        void onNotifyAdapter();


        @NonNull OnLoadMore getLoadMore();

        void onEditComment(@NonNull CommentsModel item);

        void onStartNewComment();

        void onHandleCommentDelete(@NonNull Response<Boolean> booleanResponse, long commId);

        void onShowDeleteMsg(long id);

        void onShowProgressDialog();

        void onTagUser(@Nullable UserModel user);

    }

    interface Presenter extends BaseMvp.FAPresenter,
            BaseMvp.PaginationListener<String>, BaseViewHolder.OnItemClickListener<CommentsModel> {

        void onFragmentCreated(@Nullable Bundle bundle);

        @NonNull ArrayList<CommentsModel> getComments();

        void onWorkOffline();

        void onHandleDeletion(@Nullable Bundle bundle);

        @NonNull String repoId();

        @NonNull String login();

        int number();
    }


}
