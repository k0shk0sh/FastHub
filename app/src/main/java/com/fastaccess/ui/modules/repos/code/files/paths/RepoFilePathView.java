package com.fastaccess.ui.modules.repos.code.files.paths;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.annimon.stream.Objects;
import com.fastaccess.R;
import com.fastaccess.data.dao.BranchesModel;
import com.fastaccess.data.dao.model.RepoFile;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.Logger;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.adapter.BranchesAdapter;
import com.fastaccess.ui.adapter.RepoFilePathsAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.repos.code.files.RepoFilesView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTouch;
import icepick.State;

/**
 * Created by Kosh on 18 Feb 2017, 2:10 AM
 */

public class RepoFilePathView extends BaseFragment<RepoFilePathMvp.View, RepoFilePathPresenter> implements RepoFilePathMvp.View {

    @BindView(R.id.recycler) RecyclerView recycler;
    @BindView(R.id.toParentFolder) View toParentFolder;
    @BindView(R.id.branches) Spinner branches;
    @BindView(R.id.branchesProgress) ProgressBar branchesProgress;

    @State String ref;

    private RepoFilePathsAdapter adapter;
    private RepoFilesView repoFilesView;
    private boolean canSelectSpinner;

    public static RepoFilePathView newInstance(@NonNull String login, @NonNull String repoId, @Nullable String path, @NonNull String defaultBranch) {
        RepoFilePathView view = new RepoFilePathView();
        view.setArguments(Bundler.start()
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.EXTRA_TWO, path)
                .put(BundleConstant.EXTRA_THREE, defaultBranch)
                .end());
        return view;
    }

    @OnClick(R.id.downloadRepoFiles) void onDownloadRepoFiles() {
        if (InputHelper.isEmpty(ref)) {
            ref = getPresenter().getDefaultBranch();
        }
        Uri uri = new Uri.Builder()
                .scheme("https")
                .authority("github.com")
                .appendPath(getPresenter().getLogin())
                .appendPath(getPresenter().getRepoId())
                .appendPath("archive")
                .appendPath(ref + ".zip")
                .build();
        if (ActivityHelper.checkAndRequestReadWritePermission(getActivity())) {
            RestProvider.downloadFile(getContext(), uri.toString());
        }
    }

    @OnClick(R.id.toParentFolder) void onBackClicked() {
        if (adapter.getItemCount() > 0) {
            getPresenter().getPaths().clear();
            onNotifyAdapter();
            getRepoFilesView().onSetData(getPresenter().getLogin(), getPresenter().getRepoId(), "", ref, false);
        }
    }

    @OnTouch(R.id.branches) boolean onTouchSpinner() {
        canSelectSpinner = true;
        return false;
    }

    @OnItemSelected(R.id.branches) void onBranchSelected(int position) {
        if (canSelectSpinner) {
            ref = ((BranchesModel) branches.getItemAtPosition(position)).getName();
            getRepoFilesView().onSetData(getPresenter().getLogin(), getPresenter().getRepoId(), "", ref, true);
            onBackClicked();
        }
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override public void onDetach() {
        super.onDetach();
    }

    @Override public void onNotifyAdapter() {
        adapter.notifyDataSetChanged();
    }

    @Override public void onItemClicked(@NonNull RepoFile model, int position) {
        if (getRepoFilesView().isRefreshing()) return;
        getRepoFilesView().onSetData(getPresenter().getLogin(), getPresenter().getRepoId(), Objects.toString(model.getPath(), ""), ref, false);
        if ((position + 1) < adapter.getItemCount()) {
            adapter.subList(position + 1, adapter.getItemCount());
        }
        recycler.scrollToPosition(adapter.getItemCount() - 1);
    }

    @Override public void onAppendPath(@NonNull RepoFile model) {
        adapter.addItem(model);
        recycler.scrollToPosition(adapter.getItemCount() - 1); //smoothScrollToPosition(index) hides the recyclerview? MIND-BLOWING??.
        getRepoFilesView().onSetData(getPresenter().getLogin(), getPresenter().getRepoId(), Objects.toString(model.getPath(), ""), ref, false);
    }

    @Override public void onSendData() {
        if (InputHelper.isEmpty(ref)) {
            ref = getPresenter().getDefaultBranch();
        }
        getRepoFilesView().onSetData(getPresenter().getLogin(), getPresenter().getRepoId(),
                Objects.toString(getPresenter().getPath(), ""), ref, false);
    }

    @Override public boolean canPressBack() {
        return adapter == null || adapter.getItemCount() == 0;
    }

    @Override public void onBackPressed() {
        int position = adapter.getItemCount() > 2 ? adapter.getItemCount() - 2 : adapter.getItemCount() - 1;
        Logger.e(position, adapter.getItemCount());
        if (position > 0 && position <= adapter.getItemCount()) {
            if (position == 1) position = 0;
            RepoFile repoFilesModel = adapter.getItem(position);
            onItemClicked(repoFilesModel, position);
        } else {
            onBackClicked();
        }
    }

    @Override public void showProgress(@StringRes int resId) {
        branchesProgress.setVisibility(View.VISIBLE);
    }

    @Override public void hideProgress() {
        branchesProgress.setVisibility(View.GONE);
    }

    @Override public void showErrorMessage(@NonNull String message) {
        showReload();
        super.showErrorMessage(message);
    }

    @Override public void showMessage(int titleRes, int msgRes) {
        showReload();
        super.showMessage(titleRes, msgRes);
    }

    private void showReload() {
        hideProgress();
    }

    @Override public void setBranchesData(@Nullable List<BranchesModel> branchesData, boolean firstTime) {
        branchesProgress.setVisibility(View.GONE);
        if (branchesData != null) {
            branches.setAdapter(new BranchesAdapter(branchesData));
            if (firstTime) {
                if (!InputHelper.isEmpty(getPresenter().getDefaultBranch())) {
                    int index = -1;
                    for (int i = 0; i < branchesData.size(); i++) {
                        if (branchesData.get(i).getName().equals(getPresenter().getDefaultBranch())) {
                            index = i;
                            break;
                        }
                    }
                    if (index != -1) {
                        branches.setSelection(index, true);
                    }
                }
            }
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
        setBranchesData(getPresenter().getBranches(), false);
    }

    @NonNull @Override public RepoFilePathPresenter providePresenter() {
        return new RepoFilePathPresenter();
    }

    @Override public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //noinspection ConstantConditions (for this state, it is still null!!!)
        if (isSafe() && getRepoFilesView() != null) getRepoFilesView().onHiddenChanged(!isVisibleToUser);
    }

    @NonNull public RepoFilesView getRepoFilesView() {
        if (repoFilesView == null) {
            repoFilesView = (RepoFilesView) getChildFragmentManager().findFragmentById(R.id.filesFragment);
        }
        return repoFilesView;
    }
}
