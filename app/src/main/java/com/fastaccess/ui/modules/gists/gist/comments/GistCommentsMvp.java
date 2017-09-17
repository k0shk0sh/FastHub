package com.fastaccess.ui.modules.gists.gist.comments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kosh on 20 Nov 2016, 11:10 AM
 */

interface GistCommentsMvp {

    interface View extends BaseMvp.FAView, SwipeRefreshLayout.OnRefreshListener,
            android.view.View.OnClickListener {

        void onNotifyAdapter(@Nullable List<Comment> items, int page);

        void onRemove(@NonNull Comment comment);

        @NonNull OnLoadMore<String> getLoadMore();

        void onEditComment(@NonNull Comment item);

        void onShowDeleteMsg(long id);

        void onTagUser(@NonNull User user);

        void onReply(User user, String message);

        void onHandleComment(@NonNull String text, @Nullable Bundle bundle);

        void onAddNewComment(@NonNull Comment comment);

        @NonNull ArrayList<String> getNamesToTag();

        void hideBlockingProgress();
    }

    interface Presenter extends BaseMvp.FAPresenter,
            BaseMvp.PaginationListener<String>, BaseViewHolder.OnItemClickListener<Comment> {

        @NonNull ArrayList<Comment> getComments();


        void onHandleDeletion(@Nullable Bundle bundle);

        void onWorkOffline(@NonNull String gistId);

        void onHandleComment(@NonNull String text, @Nullable Bundle bundle, String gistId);
    }


}
