package com.fastaccess.ui.modules.repos.code.files;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.MenuInflater;
import android.view.View;
import android.widget.PopupMenu;

import com.fastaccess.R;
import com.fastaccess.data.dao.EditRepoFileModel;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.data.dao.model.RepoFile;
import com.fastaccess.data.dao.types.FilesType;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.FileHelper;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.provider.markdown.MarkDownProvider;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.adapter.RepoFilesAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.code.CodeViewerActivity;
import com.fastaccess.ui.modules.main.premium.PremiumActivity;
import com.fastaccess.ui.modules.repos.RepoPagerMvp;
import com.fastaccess.ui.modules.repos.code.files.activity.RepoFilesActivity;
import com.fastaccess.ui.modules.repos.code.files.paths.RepoFilePathFragment;
import com.fastaccess.ui.modules.repos.git.EditRepoFileActivity;
import com.fastaccess.ui.modules.repos.git.delete.DeleteFileBottomSheetFragment;
import com.fastaccess.ui.widgets.AppbarRefreshLayout;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller;

import butterknife.BindView;

/**
 * Created by Kosh on 18 Feb 2017, 2:10 AM
 */

public class RepoFilesFragment extends BaseFragment<RepoFilesMvp.View, RepoFilesPresenter> implements RepoFilesMvp.View {

    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) AppbarRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    @BindView(R.id.fastScroller) RecyclerViewFastScroller fastScroller;
    private RepoFilesAdapter adapter;
    private Login login;
    private RepoFilePathFragment parentFragment;
    private RepoPagerMvp.View repoCallback;

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

    @Override public void onNotifyAdapter() {
        hideProgress();
        adapter.notifyDataSetChanged();
    }

    @Override public void onItemClicked(@NonNull RepoFile model) {
        if (refresh.isRefreshing()) return;
        if (model.getType() == FilesType.dir) {
            if (getParent() != null) {
                getParent().onAppendPath(model);
            }
        } else {
            if (model.getSize() == 0 && InputHelper.isEmpty(model.getDownloadUrl()) && !InputHelper.isEmpty(model.getGitUrl())) {
                RepoFilesActivity.startActivity(getContext(), model.getGitUrl().replace("trees/", ""), isEnterprise());
            } else {
                String url = InputHelper.isEmpty(model.getDownloadUrl()) ? model.getUrl() : model.getDownloadUrl();
                if (InputHelper.isEmpty(url)) return;
                if (model.getSize() > FileHelper.ONE_MB && !MarkDownProvider.isImage(url)) {
                    MessageDialogView.newInstance(getString(R.string.big_file), getString(R.string.big_file_description),
                            false, true, Bundler.start()
                                    .put(BundleConstant.EXTRA, model.getDownloadUrl())
                                    .put(BundleConstant.YES_NO_EXTRA, true)
                                    .end())
                            .show(getChildFragmentManager(), "MessageDialogView");
                } else {
                    CodeViewerActivity.startActivity(getContext(), url, model.getHtmlUrl());
                }
            }
        }
    }

    @Override public void onMenuClicked(int position, @NonNull RepoFile item, View v) {
        if (login == null) {
            login = Login.getUser();
        }
        if (refresh.isRefreshing()) return;
        boolean isOwner = login.getLogin().equals(getPresenter().login) || (repoCallback != null && repoCallback.isCollaborator());
        PopupMenu popup = new PopupMenu(getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.download_share_menu, popup.getMenu());
        popup.getMenu().findItem(R.id.download).setVisible(item.getType() == FilesType.file);
        boolean canOpen = canOpen(item);
        popup.getMenu().findItem(R.id.editFile).setVisible(isOwner && item.getType() == FilesType.file && canOpen);
        popup.getMenu().findItem(R.id.deleteFile).setVisible(isOwner && item.getType() == FilesType.file);
        popup.setOnMenuItemClickListener(item1 -> {
            switch (item1.getItemId()) {
                case R.id.share:
                    ActivityHelper.shareUrl(v.getContext(), item.getHtmlUrl());
                    break;
                case R.id.download:
                    if (ActivityHelper.checkAndRequestReadWritePermission(getActivity())) {
                        RestProvider.downloadFile(getContext(), item.getDownloadUrl());
                    }
                    break;
                case R.id.copy:
                    AppHelper.copyToClipboard(v.getContext(), !InputHelper.isEmpty(item.getHtmlUrl()) ? item.getHtmlUrl() : item.getUrl());
                    break;
                case R.id.editFile:
                    if (PrefGetter.isProEnabled() || PrefGetter.isAllFeaturesUnlocked()) {
                        if (canOpen) {
                            EditRepoFileModel fileModel = new EditRepoFileModel(getPresenter().login, getPresenter().repoId,
                                    item.getPath(), getPresenter().ref, item.getSha(), item.getDownloadUrl(), item.getName(), true);
                            EditRepoFileActivity.Companion.startForResult(this, fileModel, isEnterprise());
                        }
                    } else {
                        PremiumActivity.Companion.startActivity(getContext());
                    }
                    break;
                case R.id.deleteFile:
                    if (PrefGetter.isProEnabled() || PrefGetter.isAllFeaturesUnlocked()) {
                        DeleteFileBottomSheetFragment.Companion.newInstance(position, item.getName())
                                .show(getChildFragmentManager(), DeleteFileBottomSheetFragment.class.getSimpleName());
                    } else {
                        PremiumActivity.Companion.startActivity(getContext());
                    }
                    break;
            }
            return true;
        });
        popup.show();
    }

    @Override public void onSetData(@NonNull String login, @NonNull String repoId, @NonNull String path,
                                    @NonNull String ref, boolean clear, @Nullable RepoFile toAppend) {
        getPresenter().onInitDataAndRequest(login, repoId, path, ref, clear, toAppend);
    }

    @Override public boolean isRefreshing() {
        return refresh.isRefreshing();
    }

    @Override public void onUpdateTab(@Nullable RepoFile toAppend) {
        getParent().onAppenedtab(toAppend);
    }

    @Override protected int fragmentLayout() {
        return R.layout.vertical_refresh_list;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        stateLayout.setEmptyText(R.string.no_files);
        refresh.setOnRefreshListener(this);
        stateLayout.setOnReloadListener(v -> onRefresh());
        recycler.setEmptyView(stateLayout, refresh);
        adapter = new RepoFilesAdapter(getPresenter().getFiles());
        adapter.setListener(getPresenter());
        recycler.setAdapter(adapter);
        fastScroller.attachRecyclerView(recycler);
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

    @NonNull @Override public RepoFilesPresenter providePresenter() {
        return new RepoFilesPresenter();
    }

    @Override public void onRefresh() {
        getPresenter().onCallApi(null);
    }

    @Override public void onMessageDialogActionClicked(boolean isOk, @Nullable Bundle bundle) {
        super.onMessageDialogActionClicked(isOk, bundle);
        if (isOk && bundle != null) {
            String url = bundle.getString(BundleConstant.EXTRA);
            if (!InputHelper.isEmpty(url)) {
                if (ActivityHelper.checkAndRequestReadWritePermission(getActivity())) {
                    RestProvider.downloadFile(getContext(), url);
                }
            }
        }
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == EditRepoFileActivity.EDIT_RQ) {
            onRefresh();
        }
    }

    @Override public void onDelete(@NonNull String message, int position) {
        getPresenter().onDeleteFile(message, adapter.getItem(position), getParent() != null ? getParent().getRef() : "master");
    }

    private void showReload() {
        hideProgress();
        stateLayout.showReload(adapter.getItemCount());
    }

    private RepoFilePathFragment getParent() {
        if (parentFragment == null) {
            parentFragment = (RepoFilePathFragment) getParentFragment();
        }
        return parentFragment;
    }

    private boolean canOpen(@NonNull RepoFile item) {
        return item.getDownloadUrl() != null && !MarkDownProvider.isImage(item.getDownloadUrl())
                && !MarkDownProvider.isArchive(item.getDownloadUrl());
    }
}
