package com.fastaccess.ui.modules.repos.code.files.paths;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.annimon.stream.Objects;
import com.evernote.android.state.State;
import com.fastaccess.R;
import com.fastaccess.data.dao.BranchesModel;
import com.fastaccess.data.dao.EditRepoFileModel;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.data.dao.model.RepoFile;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.adapter.RepoFilePathsAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.main.premium.PremiumActivity;
import com.fastaccess.ui.modules.repos.RepoPagerMvp;
import com.fastaccess.ui.modules.repos.code.files.RepoFilesFragment;
import com.fastaccess.ui.modules.repos.extras.branches.pager.BranchesPagerFragment;
import com.fastaccess.ui.modules.repos.git.EditRepoFileActivity;
import com.fastaccess.ui.modules.search.repos.files.SearchFileActivity;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Kosh on 18 Feb 2017, 2:10 AM
 */

public class RepoFilePathFragment extends BaseFragment<RepoFilePathMvp.View, RepoFilePathPresenter> implements RepoFilePathMvp.View {

    @BindView(R.id.recycler) RecyclerView recycler;
    @BindView(R.id.toParentFolder) View toParentFolder;
    @BindView(R.id.branches) FontTextView branches;
    @BindView(R.id.addFile) View addFile;
    private RepoPagerMvp.View repoCallback;

    @State String ref;

    private RepoFilePathsAdapter adapter;
    private RepoFilesFragment repoFilesView;

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

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof RepoPagerMvp.View) {
            repoCallback = (RepoPagerMvp.View) getParentFragment();
        } else if (context instanceof RepoPagerMvp.View) {
            repoCallback = (RepoPagerMvp.View) context;
        }
    }

    @Override public void onDetach() {
        repoCallback = null;
        super.onDetach();
    }

    @OnClick(R.id.addFile) void onAddFile() {
        if (PrefGetter.isProEnabled() || PrefGetter.isAllFeaturesUnlocked()) {
            RepoFile repoFile = !adapter.isEmpty() ? adapter.getItem(adapter.getItemCount() - 1) : null;
            EditRepoFileModel fileModel = new EditRepoFileModel(getPresenter().login, getPresenter().repoId,
                    repoFile != null ? repoFile.getPath() : "", ref, repoFile != null ? repoFile.getSha() : "", null, null, false);
            EditRepoFileActivity.Companion.startForResult(this, fileModel, isEnterprise());
        } else {
            PremiumActivity.Companion.startActivity(getContext());
        }
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
        startActivity(SearchFileActivity.createIntent(getContext(), getPresenter().getLogin(), getPresenter().getRepoId(), isEnterprise()));
    }

    @OnClick(R.id.toParentFolder) void onBackClicked() {
        if (adapter.getItemCount() > 0) {
            adapter.clear();
            getRepoFilesView().onSetData(getPresenter().getLogin(), getPresenter().getRepoId(), "", ref, false, null);
        }
    }

    @OnClick(R.id.branches) void onBranchesClicked() {
        BranchesPagerFragment.Companion.newInstance(getPresenter().login, getPresenter().repoId)
                .show(getChildFragmentManager(), "BranchesFragment");
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
        if (getRepoFilesView().isRefreshing()) return;
        if (adapter.getItemCount() > 1) {
            adapter.removeItem(adapter.getItemCount() - 1);
            RepoFile model = adapter.getItem(adapter.getItemCount() - 1);
            getRepoFilesView().onSetData(getPresenter().getLogin(), getPresenter().getRepoId(),
                    Objects.toString(model.getPath(), ""), ref, false, null);
            recycler.scrollToPosition(adapter.getItemCount() - 1);
        } else {
            onBackClicked();
        }
    }

    @Override public void showProgress(@StringRes int resId) {}

    @Override public void hideProgress() {}

    @Override public void showErrorMessage(@NonNull String message) {
        showReload();
        super.showErrorMessage(message);
    }

    @Override public void showMessage(int titleRes, int msgRes) {
        showReload();
        super.showMessage(titleRes, msgRes);
    }

    @Override protected int fragmentLayout() {
        return R.layout.repo_file_layout;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        adapter = new RepoFilePathsAdapter(getPresenter().getPaths());
        adapter.setListener(getPresenter());
        recycler.setAdapter(adapter);
        branches.setText(ref);
        if (savedInstanceState == null) {
            getPresenter().onFragmentCreated(getArguments());
        } else if (getPresenter().getPaths().isEmpty() && !getPresenter().isApiCalled()) {
            getPresenter().onFragmentCreated(getArguments());
        }
        branches.setText(getPresenter().getDefaultBranch());
        if (Login.getUser().getLogin().equalsIgnoreCase(getPresenter().login) || (repoCallback != null && repoCallback.isCollaborator())) {
            addFile.setVisibility(View.VISIBLE);
        }
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

    @Override public void onScrollTop(int index) {
        super.onScrollTop(index);
        if (repoFilesView != null) repoFilesView.onScrollTop(index);
    }

    @Override public void onBranchSelected(@NonNull BranchesModel branch) {
        ref = branch.getName();
        branches.setText(ref);
        getRepoFilesView().onSetData(getPresenter().getLogin(), getPresenter().getRepoId(), "", ref, true, null);
        onBackClicked();
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == EditRepoFileActivity.Companion.getEDIT_RQ()) {
            getRepoFilesView().onRefresh();
        }
    }

    private void showReload() {
        hideProgress();
    }
}
