package com.fastaccess.ui.modules.repos.code.commit.details.comments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.evernote.android.state.State;
import com.fastaccess.R;
import com.fastaccess.data.dao.TimelineModel;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.data.dao.types.ReactionTypes;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.provider.timeline.CommentsHelper;
import com.fastaccess.provider.timeline.ReactionsProvider;
import com.fastaccess.ui.adapter.IssuesTimelineAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.editor.EditorActivity;
import com.fastaccess.ui.modules.editor.comment.CommentEditorFragment;
import com.fastaccess.ui.modules.repos.reactions.ReactionsDialogFragment;
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
 * Created by Kosh on 11 Nov 2016, 12:36 PM
 */

public class CommitCommentsFragment extends BaseFragment<CommitCommentsMvp.View, CommitCommentsPresenter> implements CommitCommentsMvp.View {

    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) SwipeRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    @BindView(R.id.fastScroller) RecyclerViewFastScroller fastScroller;
    @State HashMap<Long, Boolean> toggleMap = new LinkedHashMap<>();
    private CommentEditorFragment.CommentListener commentsCallback;
    private IssuesTimelineAdapter adapter;
    private OnLoadMore onLoadMore;

    public static CommitCommentsFragment newInstance(@NonNull String login, @NonNull String repoId, @NonNull String sha) {
        CommitCommentsFragment view = new CommitCommentsFragment();
        view.setArguments(Bundler.start()
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.EXTRA_TWO, sha)
                .end());
        return view;
    }

    @SuppressWarnings("unchecked") @Override public void onAttach(Context context) {
        super.onAttach(context);
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
        commentsCallback = null;
        super.onDetach();
    }

    @Override protected int fragmentLayout() {
        return R.layout.fab_micro_grid_refresh_list;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) getPresenter().onFragmentCreated(getArguments());
        stateLayout.setEmptyText(R.string.no_comments);
        recycler.setEmptyView(stateLayout, refresh);
        recycler.setItemViewCacheSize(30);
        refresh.setOnRefreshListener(this);
        stateLayout.setOnReloadListener(this);
        adapter = new IssuesTimelineAdapter(getPresenter().getComments(), this, true,
                this, getArguments().getString(BundleConstant.EXTRA), null);
        adapter.setListener(getPresenter());
        getLoadMore().initialize(getPresenter().getCurrentPage(), getPresenter().getPreviousTotal());
        recycler.setAdapter(adapter);
        recycler.addOnScrollListener(getLoadMore());
        recycler.addNormalSpacingDivider();
        if (getPresenter().getComments().isEmpty() && !getPresenter().isApiCalled()) {
            onRefresh();
        }
        fastScroller.attachRecyclerView(recycler);
    }

    @Override public void onRefresh() {
        getPresenter().onCallApi(1, null);
    }

    @Override public void onNotifyAdapter(@Nullable List<TimelineModel> items, int page) {
        hideProgress();
        if (items == null || items.isEmpty()) {
            adapter.clear();
            return;
        }
        if (page <= 1) {
            adapter.insertItems(items);
        } else {
            adapter.addItems(items);
        }
    }

    @Override public void onRemove(@NonNull TimelineModel comment) {
        hideProgress();
        adapter.removeItem(comment);
    }

    @Override public void hideProgress() {
        super.hideProgress();
        refresh.setRefreshing(false);
        stateLayout.hideProgress();
    }

    @Override public void showProgress(@StringRes int resId) {

        refresh.setRefreshing(true);

        stateLayout.showProgress();
    }

    @Override public void showErrorMessage(@NonNull String message) {
        showReload();
        super.showErrorMessage(message);
    }

    @Override public void showMessage(int titleRes, int msgRes) {
        showReload();
        super.showMessage(titleRes, msgRes);
    }

    @NonNull @Override public CommitCommentsPresenter providePresenter() {
        return new CommitCommentsPresenter();
    }

    @NonNull @Override public OnLoadMore getLoadMore() {
        if (onLoadMore == null) {
            onLoadMore = new OnLoadMore<>(getPresenter());
        }
        return onLoadMore;
    }

    @Override public void onEditComment(@NonNull Comment item) {
        Intent intent = new Intent(getContext(), EditorActivity.class);
        intent.putExtras(Bundler
                .start()
                .put(BundleConstant.ID, getPresenter().repoId())
                .put(BundleConstant.EXTRA_TWO, getPresenter().login())
                .put(BundleConstant.EXTRA_THREE, getPresenter().sha())
                .put(BundleConstant.EXTRA_FOUR, item.getId())
                .put(BundleConstant.EXTRA, item.getBody())
                .put(BundleConstant.EXTRA_TYPE, BundleConstant.ExtraType.EDIT_COMMIT_COMMENT_EXTRA)
                .putStringArrayList("participants", CommentsHelper.getUsersByTimeline(adapter.getData()))
                .put(BundleConstant.IS_ENTERPRISE, isEnterprise())
                .end());
        View view = getActivity() != null && getActivity().findViewById(R.id.fab) != null ? getActivity().findViewById(R.id.fab) : recycler;
        ActivityHelper.startReveal(this, intent, view, BundleConstant.REQUEST_CODE);
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
        Intent intent = new Intent(getContext(), EditorActivity.class);
        intent.putExtras(Bundler
                .start()
                .put(BundleConstant.ID, getPresenter().repoId())
                .put(BundleConstant.EXTRA_TWO, getPresenter().login())
                .put(BundleConstant.EXTRA_THREE, getPresenter().sha())
                .put(BundleConstant.EXTRA, user != null ? "@" + user.getLogin() : "")
                .put(BundleConstant.EXTRA_TYPE, BundleConstant.ExtraType.NEW_COMMIT_COMMENT_EXTRA)
                .putStringArrayList("participants", CommentsHelper.getUsersByTimeline(adapter.getData()))
                .put(BundleConstant.IS_ENTERPRISE, isEnterprise())
                .end());
        View view = getActivity() != null && getActivity().findViewById(R.id.fab) != null ? getActivity().findViewById(R.id.fab) : recycler;
        ActivityHelper.startReveal(this, intent, view, BundleConstant.REQUEST_CODE);

    }

    @Override public void onReply(User user, String message) {
        onTagUser(user);
    }

    @Override public void showReactionsPopup(@NonNull ReactionTypes reactionTypes, @NonNull String login, @NonNull String repoId, long commentId) {
        ReactionsDialogFragment.newInstance(login, repoId, reactionTypes, commentId, ReactionsProvider.COMMIT)
                .show(getChildFragmentManager(), "ReactionsDialogFragment");
    }

    @Override public void addComment(@NonNull Comment newComment) {
        hideBlockingProgress();
        if (adapter != null) {
            adapter.addItem(TimelineModel.constructComment(newComment));
        }
        if (commentsCallback != null) commentsCallback.onClearEditText();
    }

    @Override public void onDestroyView() {
        recycler.removeOnScrollListener(getLoadMore());
        super.onDestroyView();
    }

    @Override public void onClick(View view) {
        onRefresh();
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

    @Override public void onScrollTop(int index) {
        super.onScrollTop(index);
        if (recycler != null) recycler.scrollToPosition(0);
    }

    @Override public void showReload() {
        hideProgress();
        stateLayout.showReload(adapter.getItemCount());
    }

    @Override public void onHandleComment(@NonNull String text, @Nullable Bundle bundle) {
        getPresenter().onHandleComment(text, bundle);
    }

    @NonNull @Override public ArrayList<String> getNamesToTags() {
        return CommentsHelper.getUsersByTimeline(adapter.getData());
    }

    @Override public void hideBlockingProgress() {
        hideProgress();
        super.hideProgress();
    }
}
