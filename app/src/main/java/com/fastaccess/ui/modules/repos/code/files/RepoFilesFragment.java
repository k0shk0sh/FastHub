package com.fastaccess.ui.modules.repos.code.files;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupMenu;

import com.fastaccess.R;
import com.fastaccess.data.dao.model.RepoFile;
import com.fastaccess.data.dao.types.FilesType;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.FileHelper;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.Logger;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.provider.markdown.MarkDownProvider;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.adapter.RepoFilesAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.code.CodeViewerActivity;
import com.fastaccess.ui.modules.repos.RepoPagerActivity;
import com.fastaccess.ui.modules.repos.code.files.paths.RepoFilePathFragment;
import com.fastaccess.ui.widgets.AppbarRefreshLayout;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;

import butterknife.BindView;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

/**
 * Created by Kosh on 18 Feb 2017, 2:10 AM
 */

public class RepoFilesFragment extends BaseFragment<RepoFilesMvp.View, RepoFilesPresenter> implements RepoFilesMvp.View {

    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) AppbarRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    private RepoFilesAdapter adapter;
    private RepoFilePathFragment parentFragment;

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
            String url = InputHelper.isEmpty(model.getDownloadUrl()) ? model.getUrl() : model.getDownloadUrl();
            if (InputHelper.isEmpty(url)) return;
            if (model.getSize() > FileHelper.ONE_MB && !MarkDownProvider.isImage(url)) {
                MessageDialogView.newInstance(getString(R.string.big_file), getString(R.string.big_file_description),
                        Bundler.start().put(BundleConstant.EXTRA, model.getDownloadUrl())
                                .put(BundleConstant.YES_NO_EXTRA, true)
                                .end())
                        .show(getChildFragmentManager(), "MessageDialogView");
            } else {
                CodeViewerActivity.startActivity(getContext(), url, model.getHtmlUrl());
            }
        }
    }

    @Override public void onMenuClicked(@NonNull RepoFile item, View v) {
        if (refresh.isRefreshing()) return;
        PopupMenu popup = new PopupMenu(getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.download_share_menu, popup.getMenu());
        popup.getMenu().findItem(R.id.download).setVisible(item.getType() == FilesType.file);
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
        recycler.addKeyLineDivider();
        adapter = new RepoFilesAdapter(getPresenter().getFiles());
        adapter.setListener(getPresenter());
        recycler.setAdapter(adapter);
    }

    @Override public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Logger.e(hidden);
        if (!hidden && adapter != null && isSafe()) {
            if (!PrefGetter.isFileOptionHintShow()) {
                ActivityHelper.showDismissHints(getContext(), () -> {});
                adapter.setGuideListener((itemView, model) ->
                        new MaterialTapTargetPrompt.Builder(getActivity())
                                .setTarget(itemView.findViewById(R.id.menu))
                                .setPrimaryText(R.string.options)
                                .setSecondaryText(R.string.click_file_option_hint)
                                .setCaptureTouchEventOutsidePrompt(true)
                                .setBackgroundColourAlpha(244)
                                .setBackgroundColour(ViewHelper.getAccentColor(getContext()))
                                .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                                    @Override
                                    public void onHidePrompt(MotionEvent motionEvent, boolean b) {
                                        ActivityHelper.hideDismissHints(RepoFilesFragment.this.getContext());
                                    }

                                    @Override
                                    public void onHidePromptComplete() {

                                    }
                                })
                                .show());
                adapter.notifyDataSetChanged();// call it notify the adapter to show the guide immediately.
            }
        }
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
}
