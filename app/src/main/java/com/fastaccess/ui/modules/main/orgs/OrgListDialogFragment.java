package com.fastaccess.ui.modules.main.orgs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.ui.adapter.UsersAdapter;
import com.fastaccess.ui.base.BaseDialogFragment;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Kosh on 15 Apr 2017, 1:57 PM
 */

public class OrgListDialogFragment extends BaseDialogFragment<OrgListDialogMvp.View, OrgListDialogPresenter>
        implements OrgListDialogMvp.View {

    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) SwipeRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fastScroller) RecyclerViewFastScroller fastScroller;
    private UsersAdapter adapter;

    public static OrgListDialogFragment newInstance() {
        return new OrgListDialogFragment();
    }

    @Override public void onNotifyAdapter(@Nullable List<User> items) {
        hideProgress();
        if (items == null || items.isEmpty()) {
            adapter.clear();
            return;
        }
        adapter.insertItems(items);
    }

    @Override protected int fragmentLayout() {
        return R.layout.milestone_dialog_layout;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        toolbar.setTitle(R.string.organizations);
        toolbar.inflateMenu(R.menu.add_menu);
        toolbar.getMenu().findItem(R.id.add).setIcon(R.drawable.ic_info_outline).setTitle(R.string.no_orgs_dialog_title);
        toolbar.setOnMenuItemClickListener(item -> {
            MessageDialogView.newInstance(getString(R.string.no_orgs_dialog_title), getString(R.string.no_orgs_description), false, true)
                    .show(getChildFragmentManager(), MessageDialogView.TAG);
            return true;
        });
        toolbar.setNavigationIcon(R.drawable.ic_clear);
        toolbar.setNavigationOnClickListener(v -> dismiss());
        stateLayout.setEmptyText(R.string.no_orgs);
        stateLayout.setOnReloadListener(v -> getPresenter().onLoadOrgs());
        refresh.setOnRefreshListener(() -> getPresenter().onLoadOrgs());
        recycler.setEmptyView(stateLayout, refresh);
        adapter = new UsersAdapter(getPresenter().getOrgs());
        recycler.setAdapter(adapter);
        recycler.addKeyLineDivider();
        if (savedInstanceState == null) {
            getPresenter().onLoadOrgs();
        }
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

    @NonNull @Override public OrgListDialogPresenter providePresenter() {
        return new OrgListDialogPresenter();
    }

    private void showReload() {
        hideProgress();
        stateLayout.showReload(adapter.getItemCount());
    }
}
