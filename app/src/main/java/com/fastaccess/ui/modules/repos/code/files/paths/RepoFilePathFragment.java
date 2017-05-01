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
import com.fastaccess.ui.modules.repos.code.files.RepoFilesFragment;
import com.fastaccess.ui.modules.search.repos.files.SearchFileActivity;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTouch;
import icepick.State;

/**
 * Created by Kosh on 18 Feb 2017, 2:10 AM
 */

public class RepoFilePathFragment extends BaseFragment<RepoFilePathMvp.View, RepoFilePathPresenter> implements RepoFilePathMvp.View {

    @BindView(R.id.recycler) RecyclerView recycler;
    @BindView(R.id.toParentFolder) View toParentFolder;
    @BindView(R.id.branches) Spinner branches;
    @BindView(R.id.branchesProgress) ProgressBar branchesProgress;

    @State String ref;

    private RepoFilePathsAdapter adapter;
    private RepoFilesFragment repoFilesView;
    private boolean canSelectSpinner;

    public static RepoFilePathFragment newInstance(@NonNull String login, @NonNull String repoId, @Nullable String path,
                                                   @NonNull String defaultBranch) {
        return newInstance(login, repoId, path, defaultBranch, false);
    }

    public static RepoFilePathFragment newInstance(@NonNull String login, @NonNull String repoId,
                                                   @Nullable String path, @NonNull String defaultBranch,
                                                   boolean forceAppendPath) {
        RepoFilePathFragment view = new RepoFilePathFragment();
        view.setArguments(Bundler.start()
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.EXTRA_TWO, path)
                .put(BundleConstant.EXTRA_THREE, defaultBranch)
                .put(BundleConstant.EXTRA_FOUR, forceAppendPath)
                .end());
        return view;
    }

    @OnClick(R.id.downloadRepoFiles) void onDownloadRepoFiles() {
        if (InputHelper.isEmpty(ref)) {
            ref = getPresenter().getDefaultBranch();
        }
        if (ActivityHelper.checkAndRequestReadWritePermission(getActivity())) {
            MessageDialogView.newInstance(getString(R.string.download), getString(R.string.confirm_message),
                    Bundler.start()
                            .put(BundleConstant.YES_NO_EXTRA, true)
                            .end())
                    .show(getChildFragmentManager(), MessageDialogView.TAG);
        }
    }

    @OnClick(R.id.searchRepoFiles) void onSearchClicked() {
        startActivity(SearchFileActivity.createIntent(getContext(), getPresenter().getLogin(), getPresenter().getRepoId()));
    }

    @OnClick(R.id.toParentFolder) void onBackClicked() {
        if (adapter.getItemCount() > 0) {
            adapter.clear();
            getRepoFilesView().onSetData(getPresenter().getLogin(), getPresenter().getRepoId(), "", ref, false, null);
        }
    }

    @OnTouch(R.id.branches) boolean onTouchSpinner() {
        canSelectSpinner = true;
        return false;
    }

    @OnItemSelected(R.id.branches) void onBranchSelected(int position) {
        if (canSelectSpinner) {
            ref = ((BranchesModel) branches.getItemAtPosition(position)).getName();
            getRepoFilesView().onSetData(getPresenter().getLogin(), getPresenter().getRepoId(), "", ref, true, null);
            onBackClicked();
        }
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override public void onDetach() {
        super.onDetach();
    }

    @Override public void onNotifyAdapter(@Nullable List<RepoFile> items, int page) {
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
        recycler.smoothScrollToPosition(adapter.getItemCount() - 1);
    }

    @Override public void onItemClicked(@NonNull RepoFile model, int position) {
        if (getRepoFilesView().isRefreshing()) return;
        getRepoFilesView().onSetData(getPresenter().getLogin(), getPresenter().getRepoId(),
                Objects.toString(model.getPath(), ""), ref, false, null);
        if ((position + 1) < adapter.getItemCount()) {
            adapter.subList(position + 1, adapter.getItemCount());
        }
        recycler.scrollToPosition(adapter.getItemCount() - 1);
    }

    @Override public void onAppendPath(@NonNull RepoFile model) {
        getRepoFilesView().onSetData(getPresenter().getLogin(), getPresenter().getRepoId(),
                Objects.toString(model.getPath(), ""), ref, false, model);
    }

    @Override public void onAppenedtab(@Nullable RepoFile repoFile) {
        if (repoFile != null) {
            adapter.addItem(repoFile);
            recycler.scrollToPosition(adapter.getItemCount() - 1);
        }
    }

    @Override public void onSendData() {
        if (InputHelper.isEmpty(ref)) {
            ref = getPresenter().getDefaultBranch();
        }
        getRepoFilesView().onSetData(getPresenter().getLogin(), getPresenter().getRepoId(),
                Objects.toString(getPresenter().getPath(), ""), ref, false, null);
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

    @Override public void setBranchesData(@Nullable List<BranchesModel> branchesData, boolean firstTime) {
        branchesProgress.setVisibility(View.GONE);
        if (branchesData != null) {
            branches.setAdapter(new BranchesAdapter(getContext(), branchesData));
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

    @Override public void onMessageDialogActionClicked(boolean isOk, @Nullable Bundle bundle) {
        super.onMessageDialogActionClicked(isOk, bundle);
        if (isOk && bundle != null) {
            boolean isDownload = bundle.getBoolean(BundleConstant.YES_NO_EXTRA);
            if (isDownload) {
                Uri uri = new Uri.Builder()
                        .scheme("https")
                        .authority("github.com")
                        .appendPath(getPresenter().getLogin())
                        .appendPath(getPresenter().getRepoId())
                        .appendPath("archive")
                        .appendPath(ref + ".zip")
                        .build();
                RestProvider.downloadFile(getContext(), uri.toString());
            }
        }
    }

    @NonNull @Override public RepoFilePathPresenter providePresenter() {
        return new RepoFilePathPresenter();
    }

    @Override public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //noinspection ConstantConditions (for this state, it is still null!!!)
        if (isSafe() && getRepoFilesView() != null) getRepoFilesView().onHiddenChanged(!isVisibleToUser);
    }

    @NonNull public RepoFilesFragment getRepoFilesView() {
        if (repoFilesView == null) {
            repoFilesView = (RepoFilesFragment) getChildFragmentManager().findFragmentById(R.id.filesFragment);
        }
        return repoFilesView;
    }

    private void showReload() {
        branchesProgress.setVisibility(View.GONE);
        hideProgress();
    }
}
