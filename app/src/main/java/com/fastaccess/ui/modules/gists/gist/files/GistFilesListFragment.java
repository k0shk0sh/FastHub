package com.fastaccess.ui.modules.gists.gist.files;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.FilesListModel;
import com.fastaccess.data.dao.GithubFileModel;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.FileHelper;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.markdown.MarkDownProvider;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.adapter.GistFilesAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.code.CodeViewerActivity;
import com.fastaccess.ui.modules.gists.gist.files.GistFilesListMvp.UpdateGistCallback;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by Kosh on 13 Nov 2016, 1:36 PM
 */

public class GistFilesListFragment extends BaseFragment<GistFilesListMvp.View, GistFilesListPresenter> implements
        GistFilesListMvp.View {

    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) SwipeRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    @BindView(R.id.fastScroller) RecyclerViewFastScroller fastScroller;
    private GistFilesAdapter adapter;
    private UpdateGistCallback updateGistCallback;

    public static GistFilesListFragment newInstance(@NonNull GithubFileModel gistsModel, boolean isOwner) {
        GistFilesListFragment view = new GistFilesListFragment();
        view.setArguments(Bundler.start()
                .putParcelableArrayList(BundleConstant.ITEM, new ArrayList<>(gistsModel.values()))
                .put(BundleConstant.EXTRA_TYPE, isOwner)
                .end());
        return view;
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof UpdateGistCallback) {
            updateGistCallback = (UpdateGistCallback) getParentFragment();
        } else if (context instanceof UpdateGistCallback) {
            updateGistCallback = (UpdateGistCallback) context;
        }
    }

    @Override public void onDetach() {
        updateGistCallback = null;
        super.onDetach();
    }

    @Override protected int fragmentLayout() {
        return R.layout.small_grid_refresh_list;
    }

    @NonNull @Override public GistFilesListPresenter providePresenter() {
        return new GistFilesListPresenter();
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ArrayList<FilesListModel> filesListModel = getArguments().getParcelableArrayList(BundleConstant.ITEM);
        boolean isOwner = getArguments().getBoolean(BundleConstant.EXTRA_TYPE);
        stateLayout.hideReload();
        stateLayout.setEmptyText(R.string.no_files);
        recycler.setEmptyView(stateLayout);
        refresh.setEnabled(false);
        if (filesListModel == null) {
            return;
        }
        if (!filesListModel.isEmpty()) {
            adapter = new GistFilesAdapter(filesListModel, getPresenter(), isOwner);
            recycler.setAdapter(adapter);
        }
        fastScroller.attachRecyclerView(recycler);
    }

    @Override public void onOpenFile(@NonNull FilesListModel item) {
        if (canOpen(item)) {
            CodeViewerActivity.startActivity(getContext(), item.getRawUrl(), item.getRawUrl());
        }
    }

    @Override public void onDeleteFile(@NonNull FilesListModel item, int position) {
        MessageDialogView.newInstance(getString(R.string.delete), getString(R.string.confirm_message), false,
                Bundler.start()
                        .put(BundleConstant.ID, position)
                        .put(BundleConstant.YES_NO_EXTRA, true)
                        .end())
                .show(getChildFragmentManager(), MessageDialogView.TAG);
    }

    @Override public void onEditFile(@NonNull FilesListModel item, int position) {

    }

    @Override public void onMessageDialogActionClicked(boolean isOk, @Nullable Bundle bundle) {
        super.onMessageDialogActionClicked(isOk, bundle);
        if (isOk && bundle != null) {
            String url = bundle.getString(BundleConstant.EXTRA);
            if (!InputHelper.isEmpty(url)) {
                if (ActivityHelper.checkAndRequestReadWritePermission(getActivity())) {
                    RestProvider.downloadFile(getContext(), url);
                }
            } else if (bundle.getBoolean(BundleConstant.YES_NO_EXTRA)) {
                if (adapter != null) {
                    int position = bundle.getInt(BundleConstant.ID);
                    String filename = adapter.getItem(position).getFilename();
                    adapter.removeItem(position);
                    if (updateGistCallback != null) {
                        updateGistCallback.onUpdateGist(adapter.getData(), filename);
                    }
                }
            }
        }
    }

    @Override public void onScrollTop(int index) {
        super.onScrollTop(index);
        if (recycler != null) recycler.scrollToPosition(0);
    }

    private boolean canOpen(@NonNull FilesListModel item) {
        if (item.getRawUrl() == null) return false;
        if (item.getSize() > FileHelper.ONE_MB && !MarkDownProvider.isImage(item.getRawUrl())) {
            MessageDialogView.newInstance(getString(R.string.big_file), getString(R.string.big_file_description), false, true,
                    Bundler.start().put(BundleConstant.YES_NO_EXTRA, true).put(BundleConstant.EXTRA, item.getRawUrl()).end())
                    .show(getChildFragmentManager(), "MessageDialogView");
            return false;
        }
        return true;
    }
}
