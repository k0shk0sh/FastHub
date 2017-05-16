package com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.timeline.timeline;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import com.fastaccess.data.dao.ReviewCommentModel;
import com.fastaccess.data.dao.TimelineModel;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.data.dao.types.ReactionTypes;
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

public interface PullRequestTimelineMvp {

    interface ReviewCommentCallback {
        void onClick(int groupPosition, int commentPosition, @NonNull android.view.View view, @NonNull ReviewCommentModel model);

        void onLongClick(int groupPosition, int commentPosition, @NonNull android.view.View view, @NonNull ReviewCommentModel model);
    }

    interface View extends BaseMvp.FAView, SwipeRefreshLayout.OnRefreshListener, android.view.View.OnClickListener,
            OnToggleView, ReactionsCallback {
        void onNotifyAdapter(@Nullable List<TimelineModel> items);

        void onEditComment(@NonNull Comment item);

        void onEditReviewComment(@NonNull ReviewCommentModel item);

        void onRemove(@NonNull TimelineModel timelineModel);

        void onStartNewComment();

        void onShowDeleteMsg(long id, boolean isReviewComment);

        void onTagUser(@Nullable User user);

        void onReply(User user, String message);

        void showReactionsPopup(@NonNull ReactionTypes type, @NonNull String login, @NonNull String repoId, long idOrNumber, @ReactionsProvider
                .ReactionType int reactionType);
    }

    interface Presenter extends BaseMvp.FAPresenter, BaseViewHolder.OnItemClickListener<TimelineModel>, ReviewCommentCallback {


        void onCallApi();

        @NonNull ArrayList<TimelineModel> getEvents();

        void onFragmentCreated(@Nullable Bundle bundle);

        void onWorkOffline();

        void onHandleDeletion(@Nullable Bundle bundle);

        @Nullable String repoId();

        @Nullable String login();

        int number();

        boolean isPreviouslyReacted(long commentId, int vId);

        void onHandleReaction(@IdRes int vId, long idOrNumber, @ReactionsProvider.ReactionType int reactionType);

        boolean isMerged();

        boolean isCallingApi(long id, int vId);
    }
}
