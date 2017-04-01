package com.fastaccess.ui.modules.repos.issues.issue.details.timeline;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.CommentsLabelsModel;
import com.fastaccess.data.dao.SparseBooleanArrayParcelable;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.adapter.IssuePullsTimelineAdapter;
import com.fastaccess.ui.adapter.viewholder.TimelineCommentsViewHolder;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.editor.EditorView;
import com.fastaccess.ui.widgets.AppbarRefreshLayout;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;

import butterknife.BindView;
import icepick.State;

/**
 * Created by Kosh on 31 Mar 2017, 7:35 PM
 */

public class IssueTimelineView extends BaseFragment<IssueTimelineMvp.View, IssueTimelinePresenter> implements IssueTimelineMvp.View {

    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) AppbarRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    private IssuePullsTimelineAdapter adapter;
    private OnLoadMore onLoadMore;
    @State SparseBooleanArrayParcelable sparseBooleanArray;

    public static IssueTimelineView newInstance(@NonNull Issue issueModel) {
        IssueTimelineView view = new IssueTimelineView();
        view.setArguments(Bundler.start().put(BundleConstant.ITEM, issueModel).end());//TODO fix this
        return view;
    }

    @Override public void onRefresh() {
        getPresenter().onCallApi(1, null);
    }

    @Override public void onNotifyAdapter() {
        hideProgress();
        adapter.notifyDataSetChanged();
    }

    @Override protected int fragmentLayout() {
        return R.layout.small_grid_refresh_list;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        stateLayout.setEmptyText(R.string.no_events);
        recycler.setEmptyView(stateLayout, refresh);
        refresh.setOnRefreshListener(this);
        stateLayout.setOnReloadListener(this);
        adapter = new IssuePullsTimelineAdapter(getPresenter().getEvents(), this, true);
        adapter.setListener(getPresenter());
        getLoadMore().setCurrent_page(getPresenter().getCurrentPage(), getPresenter().getPreviousTotal());
        recycler.setAdapter(adapter);
        recycler.addDivider(TimelineCommentsViewHolder.class);
        recycler.addOnScrollListener(getLoadMore());
        if (savedInstanceState == null) {
            getPresenter().onFragmentCreated(getArguments());
        } else if (getPresenter().getEvents().size() == 1 && !getPresenter().isApiCalled()) {
            onRefresh();
        }

    }

    @NonNull @Override public IssueTimelinePresenter providePresenter() {
        return new IssueTimelinePresenter();
    }

    @Override public void showProgress(@StringRes int resId) {

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

    @SuppressWarnings("unchecked") @NonNull @Override public OnLoadMore getLoadMore() {
        if (onLoadMore == null) {
            onLoadMore = new OnLoadMore(getPresenter());
        }
        return onLoadMore;
    }

    @Override public void onEditComment(@NonNull Comment item) {
        Intent intent = new Intent(getContext(), EditorView.class);
        intent.putExtras(Bundler
                .start()
                .put(BundleConstant.ID, getPresenter().repoId())
                .put(BundleConstant.EXTRA_TWO, getPresenter().login())
                .put(BundleConstant.EXTRA_THREE, getPresenter().number())
                .put(BundleConstant.EXTRA_FOUR, item.getId())
                .put(BundleConstant.EXTRA, item.getBody())
                .put(BundleConstant.EXTRA_TYPE, BundleConstant.ExtraTYpe.EDIT_ISSUE_COMMENT_EXTRA)
                .end());
        startActivityForResult(intent, BundleConstant.REQUEST_CODE);
    }

    @Override public void onStartNewComment() {
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
        Intent intent = new Intent(getContext(), EditorView.class);
        intent.putExtras(Bundler
                .start()
                .put(BundleConstant.ID, getPresenter().repoId())
                .put(BundleConstant.EXTRA_TWO, getPresenter().login())
                .put(BundleConstant.EXTRA_THREE, getPresenter().number())
                .put(BundleConstant.EXTRA, user != null ? "@" + user.getLogin() : "")
                .put(BundleConstant.EXTRA_TYPE, BundleConstant.ExtraTYpe.NEW_ISSUE_COMMENT_EXTRA)
                .end());
        startActivityForResult(intent, BundleConstant.REQUEST_CODE);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == BundleConstant.REQUEST_CODE) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    boolean isNew = bundle.getBoolean(BundleConstant.EXTRA);
                    Comment commentsModel = bundle.getParcelable(BundleConstant.ITEM);
                    if (commentsModel == null) return;
                    getSparseBooleanArray().clear();
                    if (isNew) {
                        getPresenter().getEvents().add(CommentsLabelsModel.constructComment(commentsModel));
                        adapter.notifyDataSetChanged();
                        recycler.smoothScrollToPosition(adapter.getItemCount());
                    } else {
                        int position = adapter.getItem(CommentsLabelsModel.constructComment(commentsModel));
                        if (position != -1) {
                            getPresenter().getEvents().set(position, CommentsLabelsModel.constructComment(commentsModel));
                            adapter.notifyDataSetChanged();
                            recycler.smoothScrollToPosition(position);
                        } else {
                            getPresenter().getEvents().add(CommentsLabelsModel.constructComment(commentsModel));
                            adapter.notifyDataSetChanged();
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

    @Override public void onClick(View view) {
        onRefresh();
    }

    @Override public void onToggle(int position, boolean isCollapsed) {
        getSparseBooleanArray().put(position, isCollapsed);

    }

    @Override public boolean isCollapsed(int position) {
        return getSparseBooleanArray().get(position);
    }

    private void showReload() {
        hideProgress();
        stateLayout.showReload(adapter.getItemCount());
    }

    private SparseBooleanArrayParcelable getSparseBooleanArray() {
        if (sparseBooleanArray == null) {
            sparseBooleanArray = new SparseBooleanArrayParcelable();
        }
        return sparseBooleanArray;
    }
}
