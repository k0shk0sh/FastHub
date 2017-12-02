package com.fastaccess.ui.modules.pinned.gist;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.model.Gist;
import com.fastaccess.data.dao.model.PinnedGists;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.ui.adapter.GistsAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.widgets.AppbarRefreshLayout;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Kosh on 25 Mar 2017, 8:04 PM
 */

public class PinnedGistFragment extends BaseFragment<PinnedGistMvp.View, PinnedGistPresenter> implements PinnedGistMvp.View {

    public static final String TAG = PinnedGistFragment.class.getSimpleName();

    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) AppbarRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    @BindView(R.id.fastScroller) RecyclerViewFastScroller fastScroller;
    private GistsAdapter adapter;

    public static PinnedGistFragment newInstance() {
        return new PinnedGistFragment();
    }

    @Override public void onNotifyAdapter(@Nullable List<Gist> items) {
        refresh.setRefreshing(false);
        stateLayout.hideProgress();
        if (items != null) adapter.insertItems(items);
        else adapter.clear();
    }

    @Override public void onDeletePinnedGist(long id, int position) {
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
        adapter = new GistsAdapter(getPresenter().getPinnedGists());
        adapter.setListener(getPresenter());
        stateLayout.setEmptyText(R.string.no_gists);
        recycler.setEmptyView(stateLayout, refresh);
        recycler.setAdapter(adapter);
        recycler.addKeyLineDivider();
        refresh.setOnRefreshListener(() -> getPresenter().onReload());
        stateLayout.setOnReloadListener(v -> getPresenter().onReload());
        if (savedInstanceState == null) {
            stateLayout.showProgress();
        }
        fastScroller.attachRecyclerView(recycler);
    }

    @NonNull @Override public PinnedGistPresenter providePresenter() {
        return new PinnedGistPresenter();
    }

    @Override public void onMessageDialogActionClicked(boolean isOk, @Nullable Bundle bundle) {
        super.onMessageDialogActionClicked(isOk, bundle);
        if (bundle != null && isOk) {
            long id = bundle.getLong(BundleConstant.ID);
            int position = bundle.getInt(BundleConstant.EXTRA);
            PinnedGists.delete(id);
            adapter.removeItem(position);
        }
    }
}
