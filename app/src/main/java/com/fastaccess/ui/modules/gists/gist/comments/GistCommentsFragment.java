package com.fastaccess.ui.modules.gists.gist.comments;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.fastaccess.R;
import com.fastaccess.data.dao.SparseBooleanArrayParcelable;
import com.fastaccess.data.dao.TimelineModel;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.adapter.CommentsAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.editor.EditorActivity;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import icepick.State;

import static com.fastaccess.helper.BundleConstant.ExtraTYpe.EDIT_GIST_COMMENT_EXTRA;
import static com.fastaccess.helper.BundleConstant.ExtraTYpe.NEW_GIST_COMMENT_EXTRA;

/**
 * Created by Kosh on 11 Nov 2016, 12:36 PM
 */

public class GistCommentsFragment extends BaseFragment<GistCommentsMvp.View, GistCommentsPresenter> implements GistCommentsMvp.View {

    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) SwipeRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    @State SparseBooleanArrayParcelable sparseBooleanArray;
    private String gistId;
    private CommentsAdapter adapter;
    private OnLoadMore<String> onLoadMore;

    private ArrayList<String> participants;

    public static GistCommentsFragment newInstance(@NonNull String gistId) {
        GistCommentsFragment view = new GistCommentsFragment();
        view.setArguments(Bundler.start().put("gistId", gistId).end());
        return view;
    }

    @Override protected int fragmentLayout() {
        return R.layout.fab_small_grid_refresh_list;
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
        getLoadMore().setCurrent_page(getPresenter().getCurrentPage(), getPresenter().getPreviousTotal());
        recycler.setAdapter(adapter);
        recycler.addKeyLineDivider();
        recycler.addOnScrollListener(getLoadMore());
        recycler.addNormalSpacingDivider();
        if (getPresenter().getComments().isEmpty() && !getPresenter().isApiCalled()) {
            sparseBooleanArray = new SparseBooleanArrayParcelable();
            onRefresh();
        }
    }

    @Override public void onRefresh() {
        getPresenter().onCallApi(1, gistId);
    }

    @Override public void onNotifyAdapter(@Nullable List<Comment> items, int page) {
        hideProgress();

        participants = null;
        participants = (ArrayList<String>) Stream.of(items)
                .map(comment -> comment.getUser().getLogin())
                .collect(Collectors.toList());
        HashSet<String> hashSet = new HashSet<String>();
        hashSet.addAll(participants);
        participants.clear();
        participants.addAll(hashSet);

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
                .putStringArrayList("participants", participants)
                .end());
        View view = getActivity() != null && getActivity().findViewById(R.id.fab) != null ? getActivity().findViewById(R.id.fab) : recycler;
        ActivityHelper.startReveal(this, intent, view, BundleConstant.REQUEST_CODE);
    }

    @Override public void onStartNewComment() {
        Intent intent = new Intent(getContext(), EditorActivity.class);
        intent.putExtras(Bundler
                .start()
                .put(BundleConstant.ID, gistId)
                .put(BundleConstant.EXTRA_TYPE, NEW_GIST_COMMENT_EXTRA)
                .putStringArrayList("participants", participants)
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
                        .putStringArrayList("participants", participants)
                        .end())
                .show(getChildFragmentManager(), MessageDialogView.TAG);
    }

    @Override public void onTagUser(@NonNull User user) {
        Intent intent = new Intent(getContext(), EditorActivity.class);
        intent.putExtras(Bundler
                .start()
                .put(BundleConstant.ID, gistId)
                .put(BundleConstant.EXTRA, "@" + user.getLogin())
                .put(BundleConstant.EXTRA_TYPE, NEW_GIST_COMMENT_EXTRA)
                .putStringArrayList("participants", participants)
                .end());
        View view = getActivity() != null && getActivity().findViewById(R.id.fab) != null ? getActivity().findViewById(R.id.fab) : recycler;
        ActivityHelper.startReveal(this, intent, view, BundleConstant.REQUEST_CODE);
    }

    @Override public void onReply(User user, String message) {
        Intent intent = new Intent(getContext(), EditorActivity.class);
        intent.putExtras(Bundler
                .start()
                .put(BundleConstant.ID, gistId)
                .put(BundleConstant.EXTRA, "@" + user.getLogin())
                .put(BundleConstant.EXTRA_TYPE, NEW_GIST_COMMENT_EXTRA)
                .putStringArrayList("participants", participants)
                .put("message", message)
                .end());
        View view = getActivity() != null && getActivity().findViewById(R.id.fab) != null ? getActivity().findViewById(R.id.fab) : recycler;
        ActivityHelper.startReveal(this, intent, view, BundleConstant.REQUEST_CODE);
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

    private void showReload() {
        hideProgress();
        stateLayout.showReload(adapter.getItemCount());
    }
}
