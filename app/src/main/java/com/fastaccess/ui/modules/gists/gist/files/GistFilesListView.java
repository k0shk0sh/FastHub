package com.fastaccess.ui.modules.gists.gist.files;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.FilesListModel;
import com.fastaccess.data.dao.GithubFileModel;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.FileHelper;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.markdown.MarkDownProvider;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.adapter.GistFilesAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.code.CodeViewerView;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by Kosh on 13 Nov 2016, 1:36 PM
 */

public class GistFilesListView extends BaseFragment<GistFilesListMvp.View, GistFilesListPresenter> implements
        GistFilesListMvp.View {

    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) SwipeRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;

    public static GistFilesListView newInstance(@NonNull GithubFileModel gistsModel) {
        GistFilesListView view = new GistFilesListView();
        view.setArguments(Bundler.start().put(BundleConstant.ITEM, gistsModel).end());
        return view;
    }

    @Override protected int fragmentLayout() {
        return R.layout.small_grid_refresh_list;
    }

    @NonNull @Override public GistFilesListPresenter providePresenter() {
        return new GistFilesListPresenter();
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        GithubFileModel gistsModel = (GithubFileModel) getArguments().getSerializable(BundleConstant.ITEM);
        stateLayout.hideReload();
        stateLayout.setEmptyText(R.string.no_files);
        recycler.setEmptyView(stateLayout);
        refresh.setEnabled(false);
        if (gistsModel == null) {
            return;
        }
        if (!gistsModel.values().isEmpty()) {
            recycler.setAdapter(new GistFilesAdapter(new ArrayList<>(gistsModel.values()), getPresenter()));
        }
    }

    @Override public void onOpenFile(@NonNull FilesListModel item) {
        if (item.getRawUrl() != null) {
            if (item.getSize() > FileHelper.ONE_MB && !MarkDownProvider.isImage(item.getRawUrl())) {
                MessageDialogView.newInstance(getString(R.string.big_file), getString(R.string.big_file_description),
                        Bundler.start().put(BundleConstant.YES_NO_EXTRA, true).put(BundleConstant.EXTRA, item.getRawUrl()).end())
                        .show(getChildFragmentManager(), "MessageDialogView");
            } else {
                CodeViewerView.startActivity(getContext(), item.getRawUrl());
            }
        } else {
            showErrorMessage(getString(R.string.no_url));
        }
    }

    @Override public void onMessageDialogActionClicked(boolean isOk, @Nullable Bundle bundle) {
        super.onMessageDialogActionClicked(isOk, bundle);
        if (isOk && bundle != null) {
            String url = bundle.getString(BundleConstant.EXTRA);
            if (!InputHelper.isEmpty(url)) {
                RestProvider.downloadFile(getContext(), url);
            }
        }
    }
}
