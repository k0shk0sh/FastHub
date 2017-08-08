package com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.timeline.timeline;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;

import com.evernote.android.state.State;
import com.fastaccess.R;
import com.fastaccess.data.dao.EditReviewCommentModel;
import com.fastaccess.data.dao.ReviewCommentModel;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.data.dao.timeline.PullRequestTimelineModel;
import com.fastaccess.data.dao.types.ReactionTypes;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.adapter.PullRequestTimelineAdapter;
import com.fastaccess.ui.adapter.viewholder.TimelineCommentsViewHolder;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.editor.EditorActivity;
import com.fastaccess.ui.modules.repos.issues.issue.details.IssuePagerMvp;
import com.fastaccess.ui.modules.repos.reactions.ReactionsDialogFragment;
import com.fastaccess.ui.widgets.AppbarRefreshLayout;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Kosh on 31 Mar 2017, 7:35 PM
 */

public class PullRequestTimelineFragment extends BaseFragment<PullRequestTimelineMvp.View, PullRequestTimelinePresenter>
        implements PullRequestTimelineMvp.View {

    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) AppbarRefreshLayout refresh;
    @BindView(R.id.fastScroller) RecyclerViewFastScroller fastScroller;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    @State HashMap<Long, Boolean> toggleMap = new LinkedHashMap<>();
    private PullRequestTimelineAdapter adapter;
    private OnLoadMore<PullRequest> onLoadMore;

    private IssuePagerMvp.IssuePrCallback<PullRequest> issueCallback;

    @NonNull public static PullRequestTimelineFragment newInstance() {
        return new PullRequestTimelineFragment();
    }

    @SuppressWarnings("unchecked") @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof IssuePagerMvp.IssuePrCallback) {
            issueCallback = (IssuePagerMvp.IssuePrCallback) getParentFragment();
        } else if (context instanceof IssuePagerMvp.IssuePrCallback) {
            issueCallback = (IssuePagerMvp.IssuePrCallback) context;
        } else {
            throw new IllegalArgumentException(String.format("%s or parent fragment must implement IssuePagerMvp.IssuePrCallback", context.getClass()
                    .getSimpleName()));
        }
    }

    @Override public void onDetach() {
        issueCallback = null;
        super.onDetach();
    }

    @Override public void onRefresh() {
        getPresenter().onCallApi(1, getPullRequest());
    }

    @Override protected int fragmentLayout() {
        return R.layout.fab_micro_grid_refresh_list;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getPullRequest() == null) {
            throw new NullPointerException("PullRequest went missing!!!");
        }
        boolean isMerged = getPresenter().isMerged(getPullRequest());
        adapter = new PullRequestTimelineAdapter(getPresenter().getEvents(), this, this, isMerged, "null", "null");
        stateLayout.setEmptyText(R.string.no_events);
        recycler.setEmptyView(stateLayout, refresh);
        refresh.setOnRefreshListener(this);
        stateLayout.setOnReloadListener(this);
        adapter.setListener(getPresenter());
        recycler.setAdapter(adapter);
        fastScroller.setVisibility(View.VISIBLE);
        fastScroller.attachRecyclerView(recycler);
        recycler.addDivider(TimelineCommentsViewHolder.class);
        getLoadMore().initialize(getPresenter().getCurrentPage(), getPresenter().getPreviousTotal());
        recycler.addOnScrollListener(getLoadMore());
        if (savedInstanceState == null) {
            onSetHeader(new PullRequestTimelineModel(getPullRequest()));
            onRefresh();
        } else if (getPresenter().getEvents().isEmpty() || getPresenter().getEvents().size() == 1) {
            onRefresh();
        }
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

    @Override public void onNotifyAdapter(@Nullable List<PullRequestTimelineModel> items, int page) {
        hideProgress();
        if (items == null) {
            adapter.subList(1, adapter.getItemCount());
            return;
        }
        if (page == 1) {
            adapter.subList(1, adapter.getItemCount());
        }
        adapter.addItems(items);
    }

    @NonNull @Override public OnLoadMore<PullRequest> getLoadMore() {
        if (onLoadMore == null) {
            onLoadMore = new OnLoadMore<>(getPresenter());
        }
        onLoadMore.setParameter(getPullRequest());
        return onLoadMore;
    }

    @Override public void onEditComment(@NonNull Comment item) {
        Intent intent = new Intent(getContext(), EditorActivity.class);
        if (getPullRequest() == null) return;
        intent.putExtras(Bundler
                .start()
                .put(BundleConstant.ID, getPullRequest().getRepoId())
                .put(BundleConstant.EXTRA_TWO, getPullRequest().getLogin())
                .put(BundleConstant.EXTRA_THREE, getPullRequest().getNumber())
                .put(BundleConstant.EXTRA_FOUR, item.getId())
                .put(BundleConstant.EXTRA, item.getBody())
                .put(BundleConstant.EXTRA_TYPE, BundleConstant.ExtraTYpe.EDIT_ISSUE_COMMENT_EXTRA)
//                .putStringArrayList("participants", CommentsHelper.getUsersByTimeline(adapter.getData()))
                .put(BundleConstant.IS_ENTERPRISE, isEnterprise())
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
        if (getPullRequest() == null) return;
        intent.putExtras(Bundler
                .start()
                .put(BundleConstant.ID, getPullRequest().getRepoId())
                .put(BundleConstant.EXTRA_TWO, getPullRequest().getLogin())
                .put(BundleConstant.EXTRA_THREE, getPullRequest().getNumber())
                .put(BundleConstant.EXTRA_FOUR, item.getId())
                .put(BundleConstant.EXTRA, item.getBody())
                .put(BundleConstant.REVIEW_EXTRA, model)
                .put(BundleConstant.EXTRA_TYPE, BundleConstant.ExtraTYpe.EDIT_REVIEW_COMMENT_EXTRA)
//                .putStringArrayList("participants", CommentsHelper.getUsersByTimeline(adapter.getData()))
                .put(BundleConstant.IS_ENTERPRISE, isEnterprise())
                .end());
        View view = getFromView();
        ActivityHelper.startReveal(this, intent, view, BundleConstant.REVIEW_REQUEST_CODE);
    }

    @Override public void onRemove(@NonNull PullRequestTimelineModel timelineModel) {
        hideProgress();
        adapter.removeItem(timelineModel);
    }

    @Override public void onStartNewComment() {
        Intent intent = new Intent(getContext(), EditorActivity.class);
        if (getPullRequest() == null) return;
        intent.putExtras(Bundler
                .start()
                .put(BundleConstant.ID, getPullRequest().getRepoId())
                .put(BundleConstant.EXTRA_TWO, getPullRequest().getLogin())
                .put(BundleConstant.EXTRA_THREE, getPullRequest().getNumber())
                .put(BundleConstant.EXTRA_TYPE, BundleConstant.ExtraTYpe.NEW_ISSUE_COMMENT_EXTRA)
//                .putStringArrayList("participants", CommentsHelper.getUsersByTimeline(adapter.getData()))
                .put(BundleConstant.IS_ENTERPRISE, isEnterprise())
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
        if (getPullRequest() == null) return;
        intent.putExtras(Bundler
                .start()
                .put(BundleConstant.ID, getPullRequest().getRepoId())
                .put(BundleConstant.EXTRA_TWO, getPullRequest().getLogin())
                .put(BundleConstant.EXTRA_THREE, getPullRequest().getNumber())
                .put(BundleConstant.EXTRA, "@" + user.getLogin())
                .put(BundleConstant.EXTRA_TYPE, BundleConstant.ExtraTYpe.NEW_ISSUE_COMMENT_EXTRA)
//                .putStringArrayList("participants", CommentsHelper.getUsersByTimeline(adapter.getData()))
                .put(BundleConstant.IS_ENTERPRISE, isEnterprise())
                .put("message", message)
                .end());
        View view = getFromView();
        ActivityHelper.startReveal(this, intent, view, BundleConstant.REQUEST_CODE);
    }

    @Override public void onReplyOrCreateReview(@Nullable User user, String message, int groupPosition, int childPosition,
                                                @NonNull EditReviewCommentModel model) {
        Intent intent = new Intent(getContext(), EditorActivity.class);
        if (getPullRequest() == null) return;
        intent.putExtras(Bundler
                .start()
                .put(BundleConstant.ID, getPullRequest().getRepoId())
                .put(BundleConstant.EXTRA_TWO, getPullRequest().getLogin())
                .put(BundleConstant.EXTRA_THREE, getPullRequest().getNumber())
                .put(BundleConstant.EXTRA, user != null ? "@" + user.getLogin() : "")
                .put(BundleConstant.REVIEW_EXTRA, model)
                .put(BundleConstant.EXTRA_TYPE, BundleConstant.ExtraTYpe.NEW_REVIEW_COMMENT_EXTRA)
//                .putStringArrayList("participants", CommentsHelper.getUsersByTimeline(adapter.getData()))
                .put(BundleConstant.IS_ENTERPRISE, isEnterprise())
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
        PullRequestTimelineModel timelineModel = adapter.getItem(groupPosition);
//        if (timelineModel != null && timelineModel.getGroupedReview() != null) {
//            if (timelineModel.getGroupedReview().getComments() != null) {
//                timelineModel.getGroupedReview().getComments().remove(commentPosition);
//                if (timelineModel.getGroupedReview().getComments().isEmpty()) {
//                    adapter.removeItem(groupPosition);
//                } else {
//                    adapter.notifyItemChanged(groupPosition);
//                }
//            }
//        }
    }

    @Override public void onSetHeader(@NonNull PullRequestTimelineModel timelineModel) {
        if (adapter != null) {
            if (adapter.isEmpty()) {
                adapter.addItem(timelineModel, 0);
            } else {
                adapter.swapItem(timelineModel, 0);
            }
        }
    }

    @Nullable @Override public PullRequest getPullRequest() {
        return issueCallback.getData();
    }

    @Override public void onUpdateHeader() {
        if (getPullRequest() == null) return;
        onSetHeader(new PullRequestTimelineModel(getPullRequest()));
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
//                        adapter.addItem(PullRequestTimelineModel.constructComment(commentsModel));
//                        recycler.smoothScrollToPosition(adapter.getItemCount());
                    } else {
//                        int position = adapter.getItem(PullRequestTimelineModel.constructComment(commentsModel));
//                        if (position != -1) {
//                            adapter.swapItem(PullRequestTimelineModel.constructComment(commentsModel), position);
//                            recycler.smoothScrollToPosition(position);
//                        } else {
//                            adapter.addItem(PullRequestTimelineModel.constructComment(commentsModel));
//                            recycler.smoothScrollToPosition(adapter.getItemCount());
//                        }
                    }
                } else if (requestCode == BundleConstant.REVIEW_REQUEST_CODE) {
                    EditReviewCommentModel commentModel = bundle.getParcelable(BundleConstant.ITEM);
                    if (commentModel == null) {
                        onRefresh(); // bundle size is too large? refresh the api
                        return;
                    }
                    PullRequestTimelineModel timelineModel = adapter.getItem(commentModel.getGroupPosition());
//                    if (isNew) {
//                        if (timelineModel.getGroupedReview() != null && timelineModel.getGroupedReview().getComments() != null) {
//                            timelineModel.getGroupedReview().getComments().add(commentModel.getCommentModel());
//                            adapter.notifyItemChanged(commentModel.getGroupPosition());
//                        } else {
//                            onRefresh();
//                        }
//                    } else {
//                        if (timelineModel.getGroupedReview() != null && timelineModel.getGroupedReview().getComments() != null) {
//                            timelineModel.getGroupedReview().getComments().set(commentModel.getCommentPosition(), commentModel.getCommentModel());
//                            adapter.notifyItemChanged(commentModel.getGroupPosition());
//                        } else {
//                            onRefresh();
//                        }
//                    }
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

    @Override public void onScrollTop(int index) {
        super.onScrollTop(index);
        if (recycler != null) recycler.scrollToPosition(0);
    }

    @Override public void showReload() {
        hideProgress();
        stateLayout.showReload(adapter.getItemCount());
    }

    private View getFromView() {
        return getActivity() != null && getActivity().findViewById(R.id.fab) != null ? getActivity().findViewById(R.id.fab) : recycler;
    }
}
