package com.fastaccess.ui.modules.repos.issues.issue.details.timeline;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import com.fastaccess.data.dao.TimelineModel;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.data.dao.types.ReactionTypes;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.provider.timeline.ReactionsProvider;
import com.fastaccess.ui.adapter.callback.OnToggleView;
import com.fastaccess.ui.adapter.callback.ReactionsCallback;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kosh on 31 Mar 2017, 7:15 PM
 */

public interface IssueTimelineMvp {

    interface View extends BaseMvp.FAView, SwipeRefreshLayout.OnRefreshListener, android.view.View.OnClickListener,
            OnToggleView, ReactionsCallback {

        void onNotifyAdapter(@Nullable List<TimelineModel> items, int page);

        @NonNull OnLoadMore<Issue> getLoadMore();

        void onEditComment(@NonNull Comment item);

        void onRemove(@NonNull TimelineModel timelineModel);

        void onStartNewComment(String text);

        void onShowDeleteMsg(long id);

        void onTagUser(@Nullable User user);

        void onReply(User user, String message);

        void showReactionsPopup(@NonNull ReactionTypes type, @NonNull String login, @NonNull String repoId, long idOrNumber, boolean isHeadre);

        void onSetHeader(@NonNull TimelineModel timelineModel);

        @Nullable Issue getIssue();

        void onUpdateHeader();

        void onHandleComment(String text, @Nullable Bundle bundle);

        void addNewComment(@NonNull TimelineModel timelineModel);

        @NonNull ArrayList<String> getNamesToTag();

        void onHideBlockingProgress();

        long getCommentId();

        void addComment(@Nullable TimelineModel timelineModel, int index);
    }

    interface Presenter extends BaseMvp.FAPresenter, BaseViewHolder.OnItemClickListener<TimelineModel>,
            BaseMvp.PaginationListener<Issue> {

        boolean isPreviouslyReacted(long commentId, int vId);

        @NonNull ArrayList<TimelineModel> getEvents();

        void onWorkOffline();

        void onHandleDeletion(@Nullable Bundle bundle);

        void onHandleReaction(@IdRes int viewId, long id, @ReactionsProvider.ReactionType int reactionType);

        boolean isCallingApi(long id, int vId);

        void onHandleComment(@NonNull String text, @Nullable Bundle bundle);

        void setCommentId(long commentId);
    }
}
