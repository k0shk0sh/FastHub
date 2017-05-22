package com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.timeline.timeline;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.EditReviewCommentModel;
import com.fastaccess.data.dao.ReviewCommentModel;
import com.fastaccess.data.dao.TimelineModel;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.data.dao.types.ReactionTypes;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.provider.timeline.CommentsHelper;
import com.fastaccess.ui.adapter.IssuePullsTimelineAdapter;
import com.fastaccess.ui.adapter.viewholder.TimelineCommentsViewHolder;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.editor.EditorActivity;
import com.fastaccess.ui.modules.repos.reactions.ReactionsDialogFragment;
import com.fastaccess.ui.widgets.AppbarRefreshLayout;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerFastScroller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import icepick.State;

/**
 * Created by Kosh on 31 Mar 2017, 7:35 PM
 */

public class PullRequestTimelineFragment extends BaseFragment<PullRequestTimelineMvp.View, PullRequestTimelinePresenter>
        implements PullRequestTimelineMvp.View {

    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) AppbarRefreshLayout refresh;
    @BindView(R.id.fastScroller) RecyclerFastScroller fastScroller;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    @State HashMap<Long, Boolean> toggleMap = new LinkedHashMap<>();
    private IssuePullsTimelineAdapter adapter;

    public static PullRequestTimelineFragment newInstance(@NonNull PullRequest pullRequest) {
        PullRequestTimelineFragment view = new PullRequestTimelineFragment();
        view.setArguments(Bundler.start().put(BundleConstant.ITEM, pullRequest).end());//TODO fix this
        return view;
    }

    @Override public void onRefresh() {
        getPresenter().onCallApi();
    }

    @Override public void onNotifyAdapter(@Nullable List<TimelineModel> items) {
        hideProgress();
        if (items == null || items.isEmpty()) {
            adapter.clear();
            return;
        }
        adapter.insertItems(items);
    }

    @Override protected int fragmentLayout() {
        return R.layout.fab_small_grid_refresh_list;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recycler.setVerticalScrollBarEnabled(false);
        if (savedInstanceState == null) {
            getPresenter().onFragmentCreated(getArguments());
        } else if (getPresenter().getEvents().size() == 1 && !getPresenter().isApiCalled()) {
            onRefresh();
        }
        stateLayout.setEmptyText(R.string.no_events);
        recycler.setEmptyView(stateLayout, refresh);
        refresh.setOnRefreshListener(this);
        stateLayout.setOnReloadListener(this);
        boolean isMerged = getPresenter().isMerged();
        adapter = new IssuePullsTimelineAdapter(getPresenter().getEvents(), this, true, this,
                isMerged, getPresenter());
        adapter.setListener(getPresenter());
        fastScroller.setVisibility(View.VISIBLE);
        fastScroller.attachRecyclerView(recycler);
        recycler.setAdapter(adapter);
        recycler.addDivider(TimelineCommentsViewHolder.class);
    }

    @NonNull @Override public PullRequestTimelinePresenter providePresenter() {
        return new PullRequestTimelinePresenter();
    }

    @Override public void showProgress(@StringRes int resId) {

        refresh.setRefreshing(true);

        stateLayout.showProgress();
    }

    @Override public void hideProgress() {
        refresh.setRefreshing(false);
        stateLayout.hideProgress();
    }

    @Override public void showErrorMessage(@NonNull String message) {
        showReload();
        super.showErrorMessage(message);
    }

    @Override public void showMessage(int titleRes, int msgRes) {
        showReload();
        super.showMessage(titleRes, msgRes);
    }

    @Override public void onClick(View view) {
        onRefresh();
    }

    @Override public void onToggle(long position, boolean isCollapsed) {
        toggleMap.put(position, isCollapsed);
    }

    @Override public boolean isCollapsed(long position) {
        return toggleMap.get(position) != null && toggleMap.get(position);
    }

    @Override public void onEditComment(@NonNull Comment item) {
        Intent intent = new Intent(getContext(), EditorActivity.class);
        intent.putExtras(Bundler
                .start()
                .put(BundleConstant.ID, getPresenter().repoId())
                .put(BundleConstant.EXTRA_TWO, getPresenter().login())
                .put(BundleConstant.EXTRA_THREE, getPresenter().number())
                .put(BundleConstant.EXTRA_FOUR, item.getId())
                .put(BundleConstant.EXTRA, item.getBody())
                .put(BundleConstant.EXTRA_TYPE, BundleConstant.ExtraTYpe.EDIT_ISSUE_COMMENT_EXTRA)
                .putStringArrayList("participants", CommentsHelper.getUsersByTimeline(adapter.getData()))
                .end());
        View view = getFromView();
        ActivityHelper.startReveal(this, intent, view, BundleConstant.REQUEST_CODE);
    }

    @Override public void onEditReviewComment(@NonNull ReviewCommentModel item, int groupPosition, int childPosition) {
        EditReviewCommentModel model = new EditReviewCommentModel();
        model.setCommentPosition(childPosition);
        model.setGroupPosition(groupPosition);
        model.setInReplyTo(item.getId());
        Intent intent = new Intent(getContext(), EditorActivity.class);
        intent.putExtras(Bundler
                .start()
                .put(BundleConstant.ID, getPresenter().repoId())
                .put(BundleConstant.EXTRA_TWO, getPresenter().login())
                .put(BundleConstant.EXTRA_THREE, getPresenter().number())
                .put(BundleConstant.EXTRA_FOUR, item.getId())
                .put(BundleConstant.EXTRA, item.getBody())
                .put(BundleConstant.REVIEW_EXTRA, model)
                .put(BundleConstant.EXTRA_TYPE, BundleConstant.ExtraTYpe.EDIT_REVIEW_COMMENT_EXTRA)
                .putStringArrayList("participants", CommentsHelper.getUsersByTimeline(adapter.getData()))
                .end());
        View view = getFromView();
        ActivityHelper.startReveal(this, intent, view, BundleConstant.REVIEW_REQUEST_CODE);
    }


    @Override public void onRemove(@NonNull TimelineModel timelineModel) {
        hideProgress();
        adapter.removeItem(timelineModel);
    }

    @Override public void onStartNewComment() {
        Intent intent = new Intent(getContext(), EditorActivity.class);
        intent.putExtras(Bundler
                .start()
                .put(BundleConstant.ID, getPresenter().repoId())
                .put(BundleConstant.EXTRA_TWO, getPresenter().login())
                .put(BundleConstant.EXTRA_THREE, getPresenter().number())
                .put(BundleConstant.EXTRA_TYPE, BundleConstant.ExtraTYpe.NEW_ISSUE_COMMENT_EXTRA)
                .putStringArrayList("participants", CommentsHelper.getUsersByTimeline(adapter.getData()))
                .end());
        View view = getFromView();
        ActivityHelper.startReveal(this, intent, view, BundleConstant.REQUEST_CODE);
    }

    @Override public void onShowDeleteMsg(long id) {
        MessageDialogView.newInstance(getString(R.string.delete), getString(R.string.confirm_message),
                Bundler.start()
                        .put(BundleConstant.EXTRA, id)
                        .put(BundleConstant.YES_NO_EXTRA, false)
                        .end())
                .show(getChildFragmentManager(), MessageDialogView.TAG);
    }

    @Override public void onReply(User user, String message) {
        Intent intent = new Intent(getContext(), EditorActivity.class);
        intent.putExtras(Bundler
                .start()
                .put(BundleConstant.ID, getPresenter().repoId())
                .put(BundleConstant.EXTRA_TWO, getPresenter().login())
                .put(BundleConstant.EXTRA_THREE, getPresenter().number())
                .put(BundleConstant.EXTRA, "@" + user.getLogin())
                .put(BundleConstant.EXTRA_TYPE, BundleConstant.ExtraTYpe.NEW_ISSUE_COMMENT_EXTRA)
                .putStringArrayList("participants", CommentsHelper.getUsersByTimeline(adapter.getData()))
                .put("message", message)
                .end());
        View view = getFromView();
        ActivityHelper.startReveal(this, intent, view, BundleConstant.REQUEST_CODE);
    }

    @Override public void onReplyOrCreateReview(@Nullable User user, String message, int groupPosition, int childPosition,
                                                @NonNull EditReviewCommentModel model) {
        Intent intent = new Intent(getContext(), EditorActivity.class);
        intent.putExtras(Bundler
                .start()
                .put(BundleConstant.ID, getPresenter().repoId())
                .put(BundleConstant.EXTRA_TWO, getPresenter().login())
                .put(BundleConstant.EXTRA_THREE, getPresenter().number())
                .put(BundleConstant.EXTRA, user != null ? "@" + user.getLogin() : "")
                .put(BundleConstant.REVIEW_EXTRA, model)
                .put(BundleConstant.EXTRA_TYPE, BundleConstant.ExtraTYpe.NEW_REVIEW_COMMENT_EXTRA)
                .putStringArrayList("participants", CommentsHelper.getUsersByTimeline(adapter.getData()))
                .put("message", message)
                .end());
        View view = getFromView();
        ActivityHelper.startReveal(this, intent, view, BundleConstant.REVIEW_REQUEST_CODE);
    }

    @Override public void showReactionsPopup(@NonNull ReactionTypes type, @NonNull String login, @NonNull String repoId,
                                             long idOrNumber, int reactionType) {
        ReactionsDialogFragment.newInstance(login, repoId, type, idOrNumber, reactionType).show(getChildFragmentManager(), "ReactionsDialogFragment");
    }

    @Override public void onShowReviewDeleteMsg(long commentId, int groupPosition, int commentPosition) {
        MessageDialogView.newInstance(getString(R.string.delete), getString(R.string.confirm_message),
                Bundler.start()
                        .put(BundleConstant.EXTRA, commentId)
                        .put(BundleConstant.YES_NO_EXTRA, true)
                        .put(BundleConstant.EXTRA_TWO, groupPosition)
                        .put(BundleConstant.EXTRA_THREE, commentPosition)
                        .end())
                .show(getChildFragmentManager(), MessageDialogView.TAG);
    }

    @Override public void onRemoveReviewComment(int groupPosition, int commentPosition) {
        hideProgress();
        TimelineModel timelineModel = adapter.getItem(groupPosition);
        if (timelineModel != null && timelineModel.getGroupedReview() != null) {
            if (timelineModel.getGroupedReview().getComments() != null) {
                timelineModel.getGroupedReview().getComments().remove(commentPosition);
                if (timelineModel.getGroupedReview().getComments().isEmpty()) {
                    adapter.removeItem(groupPosition);
                } else {
                    adapter.notifyItemChanged(groupPosition);
                }
            }
        }
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                onRefresh();
                return;
            }
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                boolean isNew = bundle.getBoolean(BundleConstant.EXTRA);
                if (requestCode == BundleConstant.REQUEST_CODE) {
                    Comment commentsModel = bundle.getParcelable(BundleConstant.ITEM);
                    if (commentsModel == null) {
                        onRefresh(); // bundle size is too large? refresh the api
                        return;
                    }
                    if (isNew) {
                        adapter.addItem(TimelineModel.constructComment(commentsModel));
                        recycler.smoothScrollToPosition(adapter.getItemCount());
                    } else {
                        int position = adapter.getItem(TimelineModel.constructComment(commentsModel));
                        if (position != -1) {
                            adapter.swapItem(TimelineModel.constructComment(commentsModel), position);
                            recycler.smoothScrollToPosition(position);
                        } else {
                            adapter.addItem(TimelineModel.constructComment(commentsModel));
                            recycler.smoothScrollToPosition(adapter.getItemCount());
                        }
                    }
                } else if (requestCode == BundleConstant.REVIEW_REQUEST_CODE) {
                    EditReviewCommentModel commentModel = bundle.getParcelable(BundleConstant.ITEM);
                    if (commentModel == null) {
                        onRefresh(); // bundle size is too large? refresh the api
                        return;
                    }
                    TimelineModel timelineModel = adapter.getItem(commentModel.getGroupPosition());
                    if (isNew) {
                        if (timelineModel.getGroupedReview() != null && timelineModel.getGroupedReview().getComments() != null) {
                            timelineModel.getGroupedReview().getComments().add(commentModel.getCommentModel());
                            adapter.notifyItemChanged(commentModel.getGroupPosition());
                        } else {
                            onRefresh();
                        }
                    } else {
                        if (timelineModel.getGroupedReview() != null && timelineModel.getGroupedReview().getComments() != null) {
                            timelineModel.getGroupedReview().getComments().set(commentModel.getCommentPosition(), commentModel.getCommentModel());
                            adapter.notifyItemChanged(commentModel.getGroupPosition());
                        } else {
                            onRefresh();
                        }
                    }
                }
            } else {
                onRefresh(); // bundle size is too large? refresh the api
            }
        }
    }

    @Override public void onMessageDialogActionClicked(boolean isOk, @Nullable Bundle bundle) {
        super.onMessageDialogActionClicked(isOk, bundle);
        if (isOk) {
            getPresenter().onHandleDeletion(bundle);
        }
    }

    @Override public boolean isPreviouslyReacted(long id, int vId) {
        return getPresenter().isPreviouslyReacted(id, vId);
    }

    @Override public boolean isCallingApi(long id, int vId) {
        return getPresenter().isCallingApi(id, vId);
    }

    private View getFromView() {
        return getActivity() != null && getActivity().findViewById(R.id.fab) != null ? getActivity().findViewById(R.id.fab) : recycler;
    }

    private void showReload() {
        hideProgress();
        stateLayout.showReload(adapter.getItemCount());
    }
}
