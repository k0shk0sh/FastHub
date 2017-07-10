package com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.files;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.evernote.android.state.State;
import com.fastaccess.R;
import com.fastaccess.data.dao.CommentRequestModel;
import com.fastaccess.data.dao.CommitFileChanges;
import com.fastaccess.data.dao.CommitFileModel;
import com.fastaccess.data.dao.CommitLinesModel;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.adapter.CommitFilesAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.reviews.AddReviewDialogFragment;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Kosh on 03 Dec 2016, 3:56 PM
 */

public class PullRequestFilesFragment extends BaseFragment<PullRequestFilesMvp.View, PullRequestFilesPresenter>
        implements PullRequestFilesMvp.View {

    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) SwipeRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    @State HashMap<Long, Boolean> toggleMap = new LinkedHashMap<>();

    private PullRequestFilesMvp.PatchCallback viewCallback;
    private OnLoadMore onLoadMore;
    private CommitFilesAdapter adapter;

    public static PullRequestFilesFragment newInstance(@NonNull String repoId, @NonNull String login, long number) {
        PullRequestFilesFragment view = new PullRequestFilesFragment();
        view.setArguments(Bundler.start()
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.EXTRA_TWO, number)
                .end());
        return view;
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof PullRequestFilesMvp.PatchCallback) {
            viewCallback = (PullRequestFilesMvp.PatchCallback) getParentFragment();
        } else if (context instanceof PullRequestFilesMvp.PatchCallback) {
            viewCallback = (PullRequestFilesMvp.PatchCallback) context;
        }
    }

    @Override public void onDetach() {
        viewCallback = null;
        super.onDetach();
    }

    @Override public void onNotifyAdapter(@Nullable List<CommitFileChanges> items, int page) {
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

    @Override protected int fragmentLayout() {
        return R.layout.micro_grid_refresh_list;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() == null) {
            throw new NullPointerException("Bundle is null, therefore, PullRequestFilesFragment can't be proceeded.");
        }
        stateLayout.setEmptyText(R.string.no_commits);
        stateLayout.setOnReloadListener(this);
        refresh.setOnRefreshListener(this);
        recycler.setEmptyView(stateLayout, refresh);
        adapter = new CommitFilesAdapter(getPresenter().getFiles(), this, this);
        adapter.setListener(getPresenter());
        getLoadMore().setCurrent_page(getPresenter().getCurrentPage(), getPresenter().getPreviousTotal());
        recycler.setAdapter(adapter);
        recycler.addOnScrollListener(getLoadMore());
        if (savedInstanceState == null) {
            getPresenter().onFragmentCreated(getArguments());
        } else if (getPresenter().getFiles().isEmpty() && !getPresenter().isApiCalled()) {
            onRefresh();
        }
    }

    @NonNull @Override public PullRequestFilesPresenter providePresenter() {
        return new PullRequestFilesPresenter();
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

    @SuppressWarnings("unchecked") @NonNull @Override public OnLoadMore getLoadMore() {
        if (onLoadMore == null) {
            onLoadMore = new OnLoadMore(getPresenter());
        }
        return onLoadMore;
    }

    @Override public void onRefresh() {
        getPresenter().onCallApi(1, null);
    }

    @Override public void onClick(View view) {
        onRefresh();
    }

    @Override public void onToggle(long position, boolean isCollapsed) {
        if (adapter.getItem((int) position).getCommitFileModel().getPatch() == null) {
            ActivityHelper.openChooser(getContext(), adapter.getItem((int) position).getCommitFileModel().getBlobUrl());
        }
        toggleMap.put(position, isCollapsed);
    }

    @Override public boolean isCollapsed(long position) {
        Boolean toggle = toggleMap.get(position);
        return toggle != null && toggle;
    }

    @Override public void onScrollTop(int index) {
        super.onScrollTop(index);
        if (recycler != null) recycler.scrollToPosition(0);
    }

    @Override public void onPatchClicked(int groupPosition, int childPosition, View v, CommitFileModel commit, CommitLinesModel item) {
        if (item.getText().startsWith("@@")) return;
        AddReviewDialogFragment.Companion.newInstance(item, Bundler.start().put(BundleConstant.ITEM, commit.getFilename()).end())
                .show(getChildFragmentManager(), "AddReviewDialogFragment");
    }

    @Override public void onCommentAdded(@NonNull String comment, @NonNull CommitLinesModel item, Bundle bundle) {
        if (bundle != null) {
            String path = bundle.getString(BundleConstant.ITEM);
            if (path == null) return;
            CommentRequestModel commentRequestModel = new CommentRequestModel();
            commentRequestModel.setBody(comment);
            commentRequestModel.setPath(path);
            commentRequestModel.setPosition(item.getRightLineNo() > 0 ? item.getRightLineNo() : item.getLeftLineNo());
            if (viewCallback != null) viewCallback.onAddComment(commentRequestModel);
        }
    }

    private void showReload() {
        hideProgress();
        stateLayout.showReload(adapter.getItemCount());
    }
}
