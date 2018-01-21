package com.fastaccess.ui.modules.gists.gist.comments;

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
import com.fastaccess.data.dao.SparseBooleanArrayParcelable;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.provider.timeline.CommentsHelper;
import com.fastaccess.ui.adapter.CommentsAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.editor.EditorActivity;
import com.fastaccess.ui.modules.editor.comment.CommentEditorFragment;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.fastaccess.helper.BundleConstant.ExtraType.EDIT_GIST_COMMENT_EXTRA;

/**
 * Created by Kosh on 11 Nov 2016, 12:36 PM
 */

public class GistCommentsFragment extends BaseFragment<GistCommentsMvp.View, GistCommentsPresenter> implements GistCommentsMvp.View {

    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) SwipeRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    @BindView(R.id.fastScroller) RecyclerViewFastScroller fastScroller;
    @State SparseBooleanArrayParcelable sparseBooleanArray;
    private CommentEditorFragment.CommentListener commentsCallback;
    private String gistId;
    private CommentsAdapter adapter;
    private OnLoadMore<String> onLoadMore;

    public static GistCommentsFragment newInstance(@NonNull String gistId) {
        GistCommentsFragment view = new GistCommentsFragment();
        view.setArguments(Bundler.start().put("gistId", gistId).end());
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
        gistId = getArguments().getString("gistId");
        recycler.setEmptyView(stateLayout, refresh);
        if (gistId == null) return;
        stateLayout.setEmptyText(R.string.no_comments);
        recycler.setItemViewCacheSize(30);
        refresh.setOnRefreshListener(this);
        stateLayout.setOnReloadListener(this);
        adapter = new CommentsAdapter(getPresenter().getComments());
        adapter.setListener(getPresenter());
        getLoadMore().initialize(getPresenter().getCurrentPage(), getPresenter().getPreviousTotal());
        recycler.setAdapter(adapter);
        recycler.addKeyLineDivider();
        recycler.addOnScrollListener(getLoadMore());
        recycler.addNormalSpacingDivider();
        if (getPresenter().getComments().isEmpty() && !getPresenter().isApiCalled()) {
            sparseBooleanArray = new SparseBooleanArrayParcelable();
            onRefresh();
        }
        fastScroller.attachRecyclerView(recycler);
    }

    @Override public void onRefresh() {
        getPresenter().onCallApi(1, gistId);
    }

    @Override public void onNotifyAdapter(@Nullable List<Comment> items, int page) {
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

    @Override public void onRemove(@NonNull Comment comment) {
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

    @NonNull @Override public GistCommentsPresenter providePresenter() {
        return new GistCommentsPresenter();
    }

    @NonNull @Override public OnLoadMore<String> getLoadMore() {
        if (onLoadMore == null) {
            onLoadMore = new OnLoadMore<>(getPresenter(), gistId);
        }
        return onLoadMore;
    }

    @Override public void onEditComment(@NonNull Comment item) {
        Intent intent = new Intent(getContext(), EditorActivity.class);
        intent.putExtras(Bundler
                .start()
                .put(BundleConstant.ID, gistId)
                .put(BundleConstant.EXTRA, item.getBody())
                .put(BundleConstant.EXTRA_FOUR, item.getId())
                .put(BundleConstant.EXTRA_TYPE, EDIT_GIST_COMMENT_EXTRA)
                .putStringArrayList("participants", CommentsHelper.getUsers(adapter.getData()))
                .put(BundleConstant.IS_ENTERPRISE, isEnterprise())
                .end());
        View view = getActivity() != null && getActivity().findViewById(R.id.fab) != null ? getActivity().findViewById(R.id.fab) : recycler;
        ActivityHelper.startReveal(this, intent, view, BundleConstant.REQUEST_CODE);
    }

    @Override public void onShowDeleteMsg(long id) {
        MessageDialogView.newInstance(getString(R.string.delete), getString(R.string.confirm_message),
                Bundler.start()
                        .put(BundleConstant.EXTRA, id)
                        .put(BundleConstant.ID, gistId)
                        .put(BundleConstant.YES_NO_EXTRA, true)
                        .putStringArrayList("participants", CommentsHelper.getUsers(adapter.getData()))
                        .end())
                .show(getChildFragmentManager(), MessageDialogView.TAG);
    }

    @Override public void onTagUser(@Nullable User user) {
        if (commentsCallback != null && user != null) {
            commentsCallback.onTagUser(user.getLogin());
        }
    }

    @Override public void onReply(User user, String message) {
        onTagUser(user);
    }

    @Override public void onHandleComment(@NonNull String text, @Nullable Bundle bundle) {
        getPresenter().onHandleComment(text, bundle, gistId);
    }

    @Override public void onAddNewComment(@NonNull Comment comment) {
        hideBlockingProgress();
        adapter.addItem(comment);
        if (commentsCallback != null) commentsCallback.onClearEditText();
    }

    @NonNull @Override public ArrayList<String> getNamesToTag() {
        return CommentsHelper.getUsers(adapter.getData());
    }

    @Override public void hideBlockingProgress() {
        hideProgress();
        super.hideProgress();
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
                    if (commentsModel == null) return;
                    if (isNew) {
                        adapter.addItem(commentsModel);
                        recycler.smoothScrollToPosition(adapter.getItemCount());
                    } else {
                        int position = adapter.getItem(commentsModel);
                        if (position != -1) {
                            adapter.swapItem(commentsModel, position);
                            recycler.smoothScrollToPosition(position);
                        } else {
                            adapter.addItem(commentsModel);
                            recycler.smoothScrollToPosition(adapter.getItemCount());
                        }
                    }
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

    @Override public void onScrollTop(int index) {
        super.onScrollTop(index);
        if (recycler != null) recycler.scrollToPosition(0);
    }

    private void showReload() {
        hideProgress();
        stateLayout.showReload(adapter.getItemCount());
    }
}
