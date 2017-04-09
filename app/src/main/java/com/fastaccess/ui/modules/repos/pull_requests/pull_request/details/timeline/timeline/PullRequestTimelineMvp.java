package com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.timeline.timeline;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import com.fastaccess.data.dao.TimelineModel;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.adapter.callback.OnToggleView;
import com.fastaccess.ui.adapter.callback.ReactionsCallback;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;

/**
 * Created by Kosh on 31 Mar 2017, 7:15 PM
 */

public interface PullRequestTimelineMvp {

    interface View extends BaseMvp.FAView, SwipeRefreshLayout.OnRefreshListener, android.view.View.OnClickListener,
            OnToggleView, ReactionsCallback {

        void onNotifyAdapter();

        @NonNull OnLoadMore getLoadMore();

        void onEditComment(@NonNull Comment item);

        void onStartNewComment();

        void onShowDeleteMsg(long id);

        void onTagUser(@Nullable User user);
    }

    interface Presenter extends BaseMvp.FAPresenter, BaseViewHolder.OnItemClickListener<TimelineModel>,
            BaseMvp.PaginationListener {

        @NonNull ArrayList<TimelineModel> getEvents();

        void onFragmentCreated(@Nullable Bundle bundle);

        void onWorkOffline();

        void onHandleDeletion(@Nullable Bundle bundle);

        @Nullable String repoId();

        @Nullable String login();

        int number();

        boolean isPreviouslyReacted(long commentId, int vId);

        void onHandleReaction(int id, long commentId);
    }
}
