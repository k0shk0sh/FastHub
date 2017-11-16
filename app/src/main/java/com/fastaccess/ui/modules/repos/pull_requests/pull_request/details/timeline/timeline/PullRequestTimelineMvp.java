package com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.timeline.timeline;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import com.fastaccess.data.dao.EditReviewCommentModel;
import com.fastaccess.data.dao.ReviewCommentModel;
import com.fastaccess.data.dao.TimelineModel;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.data.dao.types.ReactionTypes;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.provider.timeline.ReactionsProvider;
import com.fastaccess.ui.adapter.callback.OnToggleView;
import com.fastaccess.ui.adapter.callback.ReactionsCallback;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kosh on 31 Mar 2017, 7:15 PM
 */

public interface PullRequestTimelineMvp {

    interface ReviewCommentCallback {
        void onClick(int groupPosition, int commentPosition, @NonNull android.view.View view, @NonNull ReviewCommentModel model);

        void onLongClick(int groupPosition, int commentPosition, @NonNull android.view.View view, @NonNull ReviewCommentModel model);
    }

    interface View extends BaseMvp.FAView, SwipeRefreshLayout.OnRefreshListener, android.view.View.OnClickListener,
            OnToggleView, ReactionsCallback {

        @CallOnMainThread void onNotifyAdapter(@Nullable List<TimelineModel> items, int page);

        @NonNull OnLoadMore<PullRequest> getLoadMore();

        void onEditComment(@NonNull Comment item);

        void onEditReviewComment(@NonNull ReviewCommentModel item, int groupPosition, int childPosition);

        void onRemove(@NonNull TimelineModel timelineModel);

        void onShowDeleteMsg(long id);

        void onReply(User user, String message);

        void showReactionsPopup(@NonNull ReactionTypes type, @NonNull String login, @NonNull String repoId, long idOrNumber, @ReactionsProvider
                .ReactionType int reactionType);

        void onShowReviewDeleteMsg(long commentId, int groupPosition, int commentPosition);

        void onRemoveReviewComment(int groupPosition, int commentPosition);

        void onSetHeader(@NonNull TimelineModel timelineModel);

        @Nullable PullRequest getPullRequest();

        void onUpdateHeader();

        @CallOnMainThread void showReload();

        void onHandleComment(String text, @Nullable Bundle bundle);

        void onReplyOrCreateReview(@Nullable User user, @Nullable String message, int groupPosition, int childPosition,
                                   @NonNull EditReviewCommentModel model);

        void addComment(@NonNull TimelineModel timelineModel);

        @NonNull ArrayList<String> getNamesToTag();

        void onHideBlockingProgress();
    }

    interface Presenter extends BaseMvp.FAPresenter, BaseViewHolder.OnItemClickListener<TimelineModel>,
            ReviewCommentCallback, BaseMvp.PaginationListener<PullRequest> {

        @NonNull ArrayList<TimelineModel> getEvents();

        void onWorkOffline();

        void onHandleDeletion(@Nullable Bundle bundle);

        boolean isPreviouslyReacted(long commentId, int vId);

        void onHandleReaction(@IdRes int vId, long idOrNumber, @ReactionsProvider.ReactionType int reactionType);

        boolean isMerged(PullRequest pullRequest);

        boolean isCallingApi(long id, int vId);

        void onHandleComment(@NonNull String text, @Nullable Bundle bundle);
    }
}
