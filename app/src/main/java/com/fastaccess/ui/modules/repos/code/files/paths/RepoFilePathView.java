package com.fastaccess.ui.modules.repos.code.files.paths;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.RepoFilesModel;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.Logger;
import com.fastaccess.ui.adapter.RepoFilePathsAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.repos.code.files.RepoFilesView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Kosh on 18 Feb 2017, 2:10 AM
 */

public class RepoFilePathView extends BaseFragment<RepoFilePathMvp.View, RepoFilePathPresenter> implements RepoFilePathMvp.View {

    @BindView(R.id.recycler) RecyclerView recycler;
    @BindView(R.id.toParentFolder) View toParentFolder;

    private RepoFilePathsAdapter adapter;
    private RepoFilesView repoFilesView;

    public static RepoFilePathView newInstance(@NonNull String login, @NonNull String repoId, @Nullable String path) {
        RepoFilePathView view = new RepoFilePathView();
        view.setArguments(Bundler.start()
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.EXTRA_TWO, path)
                .end());
        return view;
    }

    @OnClick(R.id.toParentFolder) void onBackClicked() {
        if (adapter.getItemCount() > 0) {
            getPresenter().getPaths().clear();
            onNotifyAdapter();
            getRepoFilesView().onSetData(getPresenter().getLogin(), getPresenter().getRepoId(), null);
        }
    }

    @Override public void onNotifyAdapter() {
        adapter.notifyDataSetChanged();
    }

    @Override public void onItemClicked(@NonNull RepoFilesModel model, int position) {
        if (getRepoFilesView().isRefreshing()) return;
        if ((position + 1) < adapter.getItemCount()) {
            adapter.subList(position + 1, adapter.getItemCount());
        }
        getRepoFilesView().onSetData(getPresenter().getLogin(), getPresenter().getRepoId(), model.getPath());

    }

    @Override public void onAppendPath(@NonNull RepoFilesModel model) {
        adapter.addItem(model);
        recycler.scrollToPosition(adapter.getItemCount() - 1); //smoothScrollToPosition(index) hides the recyclerview? MIND-BLOWING??.
        getRepoFilesView().onSetData(getPresenter().getLogin(), getPresenter().getRepoId(), model.getPath());
    }

    @Override public void onSendData() {
        getRepoFilesView().onSetData(getPresenter().getLogin(), getPresenter().getRepoId(), getPresenter().getPath());
    }

    @Override public boolean canPressBack() {
        return adapter == null || adapter.getItemCount() == 0;
    }

    @Override public void onBackPressed() {
        int position = adapter.getItemCount() > 2 ? adapter.getItemCount() - 2 : adapter.getItemCount() - 1;
        Logger.e(position, adapter.getItemCount());
        if (position > 0 && position <= adapter.getItemCount()) {
            if (position == 1) position = 0;
            RepoFilesModel repoFilesModel = adapter.getItem(position);
            onItemClicked(repoFilesModel, position);
        } else {
            onBackClicked();
        }
    }

    @Override protected int fragmentLayout() {
        return R.layout.repo_file_layout;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        adapter = new RepoFilePathsAdapter(getPresenter().getPaths());
        adapter.setListener(getPresenter());
        recycler.setAdapter(adapter);
        if (savedInstanceState == null) {
            getPresenter().onFragmentCreated(getArguments());
        }
    }

    @NonNull @Override public RepoFilePathPresenter providePresenter() {
        return new RepoFilePathPresenter();
    }

    @NonNull public RepoFilesView getRepoFilesView() {
        if (repoFilesView == null) {
            repoFilesView = (RepoFilesView) getChildFragmentManager().findFragmentById(R.id.filesFragment);
        }
        return repoFilesView;
    }
}
