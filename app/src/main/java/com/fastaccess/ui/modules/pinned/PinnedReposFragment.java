package com.fastaccess.ui.modules.pinned;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.model.AbstractPinnedRepos;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.ui.adapter.PinnedReposAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.widgets.AppbarRefreshLayout;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;

import butterknife.BindView;

/**
 * Created by Kosh on 25 Mar 2017, 8:04 PM
 */

public class PinnedReposFragment extends BaseFragment<PinnedReposMvp.View, PinnedReposPresenter> implements PinnedReposMvp.View {

    public static final String TAG = PinnedReposFragment.class.getSimpleName();

    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) AppbarRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    private PinnedReposAdapter adapter;

    public static PinnedReposFragment newInstance() {
        return new PinnedReposFragment();
    }

    @Override public void onNotifyAdapter() {
        refresh.setRefreshing(false);
        stateLayout.hideProgress();
        adapter.notifyDataSetChanged();
    }

    @Override public void onDeletePinnedRepo(long id, int position) {
        MessageDialogView.newInstance(getString(R.string.delete), getString(R.string.confirm_message),
                Bundler.start().put(BundleConstant.YES_NO_EXTRA, true)
                        .put(BundleConstant.EXTRA, position)
                        .put(BundleConstant.ID, id)
                        .end())
                .show(getChildFragmentManager(), MessageDialogView.TAG);
    }

    @Override protected int fragmentLayout() {
        return R.layout.small_grid_refresh_list;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        adapter = new PinnedReposAdapter(getPresenter().getPinnedRepos(), getPresenter());
        stateLayout.setEmptyText(R.string.empty_pinned_repos);
        recycler.setEmptyView(stateLayout, refresh);
        recycler.setAdapter(adapter);
        refresh.setOnRefreshListener(() -> getPresenter().onReload());
        stateLayout.setOnReloadListener(v -> getPresenter().onReload());
        if (savedInstanceState == null) {
            stateLayout.showProgress();
        }
    }

    @NonNull @Override public PinnedReposPresenter providePresenter() {
        return new PinnedReposPresenter();
    }

    @Override public void onMessageDialogActionClicked(boolean isOk, @Nullable Bundle bundle) {
        super.onMessageDialogActionClicked(isOk, bundle);
        if (bundle != null && isOk) {
            long id = bundle.getLong(BundleConstant.ID);
            int position = bundle.getInt(BundleConstant.EXTRA);
            AbstractPinnedRepos.delete(id);
            adapter.removeItem(position);
        }
    }
}
