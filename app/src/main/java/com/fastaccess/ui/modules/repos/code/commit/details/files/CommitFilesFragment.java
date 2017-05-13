package com.fastaccess.ui.modules.repos.code.commit.details.files;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.CommitFileListModel;
import com.fastaccess.data.dao.CommitFileModel;
import com.fastaccess.data.dao.SparseBooleanArrayParcelable;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.ui.adapter.CommitFilesAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.widgets.AppbarRefreshLayout;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;

import java.util.List;

import butterknife.BindView;
import icepick.State;

/**
 * Created by Kosh on 15 Feb 2017, 10:16 PM
 */

public class CommitFilesFragment extends BaseFragment<CommitFilesMvp.View, CommitFilesPresenter> implements CommitFilesMvp.View {

    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) AppbarRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    @State SparseBooleanArrayParcelable sparseBooleanArray;

    private CommitFilesAdapter adapter;

    public static CommitFilesFragment newInstance(@NonNull String sha, @Nullable CommitFileListModel commitFileModels) {//TODO fix this
        CommitFilesFragment view = new CommitFilesFragment();
        if (commitFileModels != null) {
            CommitFilesSingleton.getInstance().putFiles(sha, commitFileModels);
        }
        Bundle bundle = Bundler.start().put(BundleConstant.ID, sha).end();
        view.setArguments(bundle);
        return view;
    }

    @Override public void onNotifyAdapter(@Nullable List<CommitFileModel> items) {
        stateLayout.hideReload();
        if (items == null || items.isEmpty()) {
            adapter.clear();
            return;
        }
        adapter.insertItems(items);
    }

    @Override protected int fragmentLayout() {
        return R.layout.small_grid_refresh_list;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        refresh.setEnabled(false);
        stateLayout.setEmptyText(R.string.no_files);
        recycler.setEmptyView(stateLayout, refresh);
        recycler.addKeyLineDivider();
        adapter = new CommitFilesAdapter(getPresenter().getFiles(), this);
        adapter.setListener(getPresenter());
        recycler.setAdapter(adapter);
        if (savedInstanceState == null) {
            sparseBooleanArray = new SparseBooleanArrayParcelable();
            getPresenter().onFragmentCreated(getArguments());
        }
    }

    @NonNull @Override public CommitFilesPresenter providePresenter() {
        return new CommitFilesPresenter();
    }

    @Override public void onToggle(int position, boolean isCollapsed) {
        if (adapter.getItem(position).getPatch() == null) {
            ActivityHelper.openChooser(getContext(), adapter.getItem(position).getRawUrl());
        }
        getSparseBooleanArray().put(position, isCollapsed);
    }

    @Override public boolean isCollapsed(int position) {
        return getSparseBooleanArray().get(position);
    }

    public SparseBooleanArrayParcelable getSparseBooleanArray() {
        if (sparseBooleanArray == null) {
            sparseBooleanArray = new SparseBooleanArrayParcelable();
        }
        return sparseBooleanArray;
    }
}
