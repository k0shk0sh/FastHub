package com.fastaccess.ui.modules.gists.gist.comments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import com.fastaccess.data.dao.CommentsModel;
import com.fastaccess.data.dao.UserModel;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.adapter.CommentsAdapter;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;

import java.util.ArrayList;

import retrofit2.Response;

/**
 * Created by Kosh on 20 Nov 2016, 11:10 AM
 */

interface GistCommentsMvp {

    interface View extends BaseMvp.FAView, SwipeRefreshLayout.OnRefreshListener,
            android.view.View.OnClickListener {

        void onNotifyAdapter();

        @NonNull OnLoadMore<String> getLoadMore();

        void onEditComment(@NonNull CommentsModel item);

        void onStartNewComment();

        void onHandleCommentDelete(@NonNull Response<Boolean> booleanResponse, long commId);

        void onShowDeleteMsg(long id);

        void onShowProgressDialog();

        void onTagUser(@NonNull UserModel user);
    }

    interface Presenter extends BaseMvp.FAPresenter,
            BaseMvp.PaginationListener<String>, BaseViewHolder.OnItemClickListener<CommentsModel> {

        @NonNull ArrayList<CommentsModel> getComments();

        void onActivityResult(int requestCode, int resultCode, @Nullable Intent data,
                              @NonNull DynamicRecyclerView recycler, @NonNull CommentsAdapter adapter);

        void onHandleDeletion(@Nullable Bundle bundle);

        void onWorkOffline(@NonNull String gistId);
    }


}
