package com.fastaccess.ui.modules.gists.gist.files;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.evernote.android.state.State;
import com.fastaccess.R;
import com.fastaccess.data.dao.FilesListModel;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.FileHelper;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.Logger;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.provider.markdown.MarkDownProvider;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.adapter.GistFilesAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.code.CodeViewerActivity;
import com.fastaccess.ui.modules.gists.create.dialog.AddGistBottomSheetDialog;
import com.fastaccess.ui.modules.main.premium.PremiumActivity;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller;

import java.util.ArrayList;
import java.util.HashMap;

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
    @State boolean isOwner;
    private GistFilesAdapter adapter;

    public static GistFilesListFragment newInstance(@NonNull ArrayList<FilesListModel> files, boolean isOwner) {
        GistFilesListFragment view = new GistFilesListFragment();
        view.setArguments(Bundler.start()
                .putParcelableArrayList(BundleConstant.ITEM, files)
                .put(BundleConstant.EXTRA_TYPE, isOwner)
                .end());
        return view;
    }

    @Override protected int fragmentLayout() {
        return R.layout.small_grid_refresh_list;
    }

    @NonNull @Override public GistFilesListPresenter providePresenter() {
        return new GistFilesListPresenter();
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        stateLayout.setEmptyText(R.string.no_files);
        stateLayout.showEmptyState();
        recycler.setEmptyView(stateLayout, refresh);
        refresh.setEnabled(false);
        adapter = new GistFilesAdapter(getPresenter().getFiles(), getPresenter(), isOwner);
        recycler.setAdapter(adapter);
        if (getArguments() != null && savedInstanceState == null) {
            ArrayList<FilesListModel> filesListModel = getArguments().getParcelableArrayList(BundleConstant.ITEM);
            isOwner = getArguments().getBoolean(BundleConstant.EXTRA_TYPE);
            onInitFiles(filesListModel, isOwner);
            setArguments(null);//CLEAR
        } else {
            onInitFiles(getPresenter().getFiles(), isOwner);
        }
        fastScroller.attachRecyclerView(recycler);
    }

    @Override public void onOpenFile(@NonNull FilesListModel item, int position) {
        if (canOpen(item) && !isOwner) {
            CodeViewerActivity.startActivity(getContext(), item.getRawUrl(), item.getRawUrl());
        } else if (isOwner && canOpen(item)) {
            onEditFile(item, position);
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
        AddGistBottomSheetDialog.Companion.newInstance(item, position).show(getChildFragmentManager(), AddGistBottomSheetDialog.Companion.getTAG());
    }

    @Override public void onInitFiles(@Nullable ArrayList<FilesListModel> filesListModel, boolean isOwner) {
        if (filesListModel == null) {
            filesListModel = new ArrayList<>();//DO NOT PASS NULL TO ADAPTER
        }
        if (getPresenter().getFilesMap().isEmpty()) {
            for (FilesListModel listModel : filesListModel) {
                getPresenter().getFilesMap().put(listModel.getFilename(), listModel);
            }
        }
        adapter.setOwner(isOwner);
        getPresenter().onSetList(filesListModel);
        adapter.insertItems(filesListModel);
    }

    @Override public void onAddNewFile() {
        Logger.e("Hello world");
        if (adapter.getItemCount() == 0 || (PrefGetter.isProEnabled() || PrefGetter.isAllFeaturesUnlocked())) {
            AddGistBottomSheetDialog.Companion.newInstance(null, -1)
                    .show(getChildFragmentManager(), AddGistBottomSheetDialog.Companion.getTAG());
        } else {
            PremiumActivity.Companion.startActivity(getContext());
        }
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
                    FilesListModel file = adapter.getItem(position);
                    if (file != null) {
                        if (getPresenter().getFilesMap().get(file.getFilename()) != null) {
                            file = getPresenter().getFilesMap().get(file.getFilename());
                            file.setContent(null);
                            getPresenter().getFilesMap().put(file.getFilename(), file);
                        }
                    }
                    adapter.removeItem(position);
                }
            }
        }
    }

    @Override public void onScrollTop(int index) {
        super.onScrollTop(index);
        if (recycler != null) recycler.scrollToPosition(0);
    }

    @Override public void onFileAdded(@NonNull FilesListModel file, Integer position) {
        if (position == null || position == -1) {
            adapter.addItem(file);
            getPresenter().getFilesMap().put(file.getFilename(), file);
        } else {
            FilesListModel current = adapter.getItem(position);
            if (getPresenter().getFilesMap().get(current.getFilename()) != null) {
                FilesListModel toUpdate = getPresenter().getFilesMap().get(current.getFilename());
                toUpdate.setFilename(file.getFilename());
                toUpdate.setContent(file.getContent());
                getPresenter().getFilesMap().put(current.getFilename(), toUpdate);
            }
            adapter.swapItem(file, position);

        }
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

    @NonNull @Override public HashMap<String, FilesListModel> getFiles() {
        return getPresenter().getFilesMap();
    }
}
