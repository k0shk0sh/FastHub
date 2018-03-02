package com.fastaccess.ui.modules.repos.issues.issue.details.timeline;

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
import com.fastaccess.data.dao.TimelineModel;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.data.dao.types.ReactionTypes;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.Logger;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.provider.timeline.CommentsHelper;
import com.fastaccess.provider.timeline.ReactionsProvider;
import com.fastaccess.ui.adapter.IssuesTimelineAdapter;
import com.fastaccess.ui.adapter.viewholder.TimelineCommentsViewHolder;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.editor.EditorActivity;
import com.fastaccess.ui.modules.editor.comment.CommentEditorFragment;
import com.fastaccess.ui.modules.repos.issues.issue.details.IssuePagerMvp;
import com.fastaccess.ui.modules.repos.reactions.ReactionsDialogFragment;
import com.fastaccess.ui.widgets.AppbarRefreshLayout;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Kosh on 31 Mar 2017, 7:35 PM
 */

public class IssueTimelineFragment extends BaseFragment<IssueTimelineMvp.View, IssueTimelinePresenter> implements IssueTimelineMvp.View {

    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) AppbarRefreshLayout refresh;
    @BindView(R.id.fastScroller) RecyclerViewFastScroller fastScroller;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    @State HashMap<Long, Boolean> toggleMap = new LinkedHashMap<>();
    private IssuesTimelineAdapter adapter;
    private OnLoadMore<Issue> onLoadMore;
    private IssuePagerMvp.IssuePrCallback<Issue> issueCallback;
    private CommentEditorFragment.CommentListener commentsCallback;

    @NonNull public static IssueTimelineFragment newInstance(long commentId) {
        IssueTimelineFragment fragment = new IssueTimelineFragment();
        fragment.setArguments(Bundler.start().put(BundleConstant.ID, commentId).end());
        return fragment;
    }

    @SuppressWarnings("unchecked") @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof IssuePagerMvp.IssuePrCallback) {
            issueCallback = (IssuePagerMvp.IssuePrCallback) getParentFragment();
        } else if (context instanceof IssuePagerMvp.IssuePrCallback) {
            issueCallback = (IssuePagerMvp.IssuePrCallback) context;
        } else {
            throw new IllegalArgumentException(String.format("%s or parent fragment must implement IssuePagerMvp.IssuePrCallback",
                    context.getClass().getSimpleName()));
        }
        if (getParentFragment() instanceof CommentEditorFragment.CommentListener) {
            commentsCallback = (CommentEditorFragment.CommentListener) getParentFragment();
        } else if (context instanceof CommentEditorFragment.CommentListener) {
            commentsCallback = (CommentEditorFragment.CommentListener) context;
        } else {
            throw new IllegalArgumentException(String.format("%s or parent fragment must implement CommentEditorFragment.CommentListener",
                    context.getClass().getSimpleName()));
        }
    }

    @Override public void onDetach() {
        issueCallback = null;
        commentsCallback = null;
        super.onDetach();
    }

    @Override public void onRefresh() {
        getPresenter().onCallApi(1, getIssue());
    }

    @Override public void onNotifyAdapter(@Nullable List<TimelineModel> items, int page) {
        hideProgress();
        if (items == null) {
            adapter.subList(1, adapter.getItemCount());
            return;
        }
        if (page == 1) {
            adapter.subList(1, adapter.getItemCount());
        }
        adapter.addItems(items);
        Logger.e(adapter.getItemCount());
    }

    @NonNull @Override public OnLoadMore<Issue> getLoadMore() {
        if (onLoadMore == null) {
            onLoadMore = new OnLoadMore<>(getPresenter());
        }
        onLoadMore.setParameter(getIssue());
        return onLoadMore;
    }

    @Override protected int fragmentLayout() {
        return R.layout.micro_grid_refresh_list;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getIssue() == null) {
            throw new NullPointerException("Issue went missing!!!");
        }

        getPresenter().setCommentId(getCommentId());
        if (issueCallback != null && issueCallback.getData() != null) {
            adapter = new IssuesTimelineAdapter(getPresenter().getEvents(), this, true,
                    this, issueCallback.getData().getLogin(), issueCallback.getData().getUser().getLogin());
        } else {
            adapter = new IssuesTimelineAdapter(getPresenter().getEvents(), this, true,
                    this, "", "");
        }
        recycler.setVerticalScrollBarEnabled(false);
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
            onSetHeader(TimelineModel.constructHeader(getIssue()));
            onRefresh();
        } else if (getPresenter().getEvents().isEmpty() || getPresenter().getEvents().size() == 1) {
            onRefresh();
        }
    }

    @NonNull @Override public IssueTimelinePresenter providePresenter() {
        return new IssueTimelinePresenter();
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

    @Override public void onEditComment(@NonNull Comment item) {
        if (getIssue() == null) return;
        Intent intent = new Intent(getContext(), EditorActivity.class);
        intent.putExtras(Bundler
                .start()
                .put(BundleConstant.ID, getIssue().getRepoId())
                .put(BundleConstant.EXTRA_TWO, getIssue().getLogin())
                .put(BundleConstant.EXTRA_THREE, getIssue().getNumber())
                .put(BundleConstant.EXTRA_FOUR, item.getId())
                .put(BundleConstant.EXTRA, item.getBody())
                .put(BundleConstant.EXTRA_TYPE, BundleConstant.ExtraType.EDIT_ISSUE_COMMENT_EXTRA)
                .putStringArrayList("participants", CommentsHelper.getUsersByTimeline(adapter.getData()))
                .put(BundleConstant.IS_ENTERPRISE, isEnterprise())
                .end());
        View view = getActivity() != null && getActivity().findViewById(R.id.fab) != null ? getActivity().findViewById(R.id.fab) : recycler;
        ActivityHelper.startReveal(this, intent, view, BundleConstant.REQUEST_CODE);
    }

    @Override public void onRemove(@NonNull TimelineModel timelineModel) {
        hideProgress();
        adapter.removeItem(timelineModel);
    }

    @Override public void onStartNewComment(String text) {
        onTagUser(null);
    }

    @Override public void onShowDeleteMsg(long id) {
        MessageDialogView.newInstance(getString(R.string.delete), getString(R.string.confirm_message),
                Bundler.start()
                        .put(BundleConstant.EXTRA, id)
                        .put(BundleConstant.YES_NO_EXTRA, true)
                        .end())
                .show(getChildFragmentManager(), MessageDialogView.TAG);
    }

    @Override public void onTagUser(@Nullable User user) {
        if (commentsCallback != null) if (user != null) {
            commentsCallback.onTagUser(user.getLogin());
        }
    }

    @Override public void onReply(User user, String message) {
        if (getIssue() == null) return;
        Intent intent = new Intent(getContext(), EditorActivity.class);
        intent.putExtras(Bundler
                .start()
                .put(BundleConstant.ID, getIssue().getRepoId())
                .put(BundleConstant.EXTRA_TWO, getIssue().getLogin())
                .put(BundleConstant.EXTRA_THREE, getIssue().getNumber())
                .put(BundleConstant.EXTRA, "@" + user.getLogin())
                .put(BundleConstant.EXTRA_TYPE, BundleConstant.ExtraType.NEW_ISSUE_COMMENT_EXTRA)
                .putStringArrayList("participants", CommentsHelper.getUsersByTimeline(adapter.getData()))
                .put(BundleConstant.IS_ENTERPRISE, isEnterprise())
                .put("message", message)
                .end());
        View view = getActivity() != null && getActivity().findViewById(R.id.fab) != null ? getActivity().findViewById(R.id.fab) : recycler;
        ActivityHelper.startReveal(this, intent, view, BundleConstant.REQUEST_CODE);

    }

    @Override public void showReactionsPopup(@NonNull ReactionTypes type, @NonNull String login,
                                             @NonNull String repoId, long idOrNumber, boolean isHeader) {
        ReactionsDialogFragment.newInstance(login, repoId, type, idOrNumber, isHeader ? ReactionsProvider.HEADER : ReactionsProvider.COMMENT)
                .show(getChildFragmentManager(), "ReactionsDialogFragment");
    }

    @Override public void onSetHeader(@NonNull TimelineModel timelineModel) {
        if (adapter != null) {
            if (adapter.isEmpty()) {
                adapter.addItem(timelineModel, 0);
            } else {
                adapter.swapItem(timelineModel, 0);
            }
        }
    }

    @Nullable @Override public Issue getIssue() {
        return issueCallback.getData();
    }

    @Override public void onUpdateHeader() {
        if (getIssue() == null) return;
        onSetHeader(TimelineModel.constructHeader(getIssue()));
    }

    @Override public void onHandleComment(@NonNull String text, @Nullable Bundle bundle) {
        getPresenter().onHandleComment(text, bundle);
    }

    @Override public void addNewComment(@NonNull TimelineModel timelineModel) {
        onHideBlockingProgress();
        adapter.addItem(timelineModel);
        if (commentsCallback != null) commentsCallback.onClearEditText();
    }

    @NonNull @Override public ArrayList<String> getNamesToTag() {
        return CommentsHelper.getUsersByTimeline(adapter.getData());
    }

    @Override public void onHideBlockingProgress() {
        hideProgress();
        super.hideProgress();
    }

    @Override public long getCommentId() {
        return getArguments() != null ? getArguments().getLong(BundleConstant.ID) : 0;
    }

    @Override public void addComment(@Nullable TimelineModel timelineModel, int index) {
        if (timelineModel != null) {
            adapter.addItem(timelineModel, 1);
            recycler.smoothScrollToPosition(1);
        } else if (index != -1) {
            recycler.smoothScrollToPosition(index + 1);
            if ((index + 1) > adapter.getItemCount()) {
                showMessage(R.string.error, R.string.comment_is_too_far_to_paginate);
            }
        }
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == BundleConstant.REQUEST_CODE) {
                if (data == null) {
                    onRefresh();
                    return;
                }
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    boolean isNew = bundle.getBoolean(BundleConstant.EXTRA);
                    Comment commentsModel = bundle.getParcelable(BundleConstant.ITEM);
                    if (commentsModel == null) {
                        onRefresh(); // shit happens, refresh()?
                        return;
                    }
                    adapter.notifyDataSetChanged();
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
                } else {
                    onRefresh(); // bundle size is too large? refresh the api
                }
            }
        }
    }

    @Override public void onMessageDialogActionClicked(boolean isOk, @Nullable Bundle bundle) {
        super.onMessageDialogActionClicked(isOk, bundle);
        if (isOk) {
            getPresenter().onHandleDeletion(bundle);
        }
    }

    @Override public void onClick(View view) {
        onRefresh();
    }

    @Override public void onToggle(long position, boolean isCollapsed) {
        toggleMap.put(position, isCollapsed);
    }

    @Override public boolean isCollapsed(long position) {
        Boolean toggle = toggleMap.get(position);
        return toggle != null && toggle;
    }

    @Override public boolean isPreviouslyReacted(long id, int vId) {
        return getPresenter().isPreviouslyReacted(id, vId);
    }

    @Override public boolean isCallingApi(long id, int vId) {
        return getPresenter().isCallingApi(id, vId);
    }

    private void showReload() {
        hideProgress();
        stateLayout.showReload(adapter.getItemCount());
    }
}
