package com.fastaccess.ui.modules.repos.extras.milestone;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.MilestoneModel;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.ui.adapter.MilestonesAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.repos.extras.milestone.create.CreateMilestoneDialogFragment;
import com.fastaccess.ui.widgets.AppbarRefreshLayout;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Kosh on 04 Mar 2017, 9:45 PM
 */

public class MilestoneDialogFragment extends BaseFragment<MilestoneMvp.View, MilestonePresenter> implements MilestoneMvp.View {
    public static final String TAG = MilestoneDialogFragment.class.getSimpleName();

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.appbar) AppBarLayout appbar;
    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) AppbarRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    private MilestonesAdapter adapter;
    private MilestoneMvp.OnMilestoneSelected onMilestoneSelected;

    public static MilestoneDialogFragment newInstance(@NonNull String login, @NonNull String repo) {
        MilestoneDialogFragment fragment = new MilestoneDialogFragment();
        fragment.setArguments(Bundler.start()
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.ID, repo)
                .end());
        return fragment;
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() != null && getParentFragment() instanceof MilestoneMvp.OnMilestoneSelected) {
            onMilestoneSelected = (MilestoneMvp.OnMilestoneSelected) getParentFragment();
        } else if (context instanceof MilestoneMvp.OnMilestoneSelected) {
            onMilestoneSelected = (MilestoneMvp.OnMilestoneSelected) context;
        } else {
            throw new IllegalArgumentException(context.getClass().getSimpleName() + " must implement onMilestoneSelected");
        }
    }

    @Override public void onDetach() {
        onMilestoneSelected = null;
        super.onDetach();
    }

    @Override public void onNotifyAdapter(@Nullable List<MilestoneModel> items) {
        hideProgress();
        if (items == null || items.isEmpty()) {
            adapter.clear();
            return;
        }
        adapter.insertItems(items);
    }

    @Override public void onMilestoneSelected(@NonNull MilestoneModel milestoneModel) {
        onMilestoneSelected.onMilestoneSelected(milestoneModel);
    }

    @Override protected int fragmentLayout() {
        return R.layout.milestone_dialog_layout;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() == null) {
            return;
        }
        String login = getArguments().getString(BundleConstant.EXTRA);
        String repo = getArguments().getString(BundleConstant.ID);
        if (login == null || repo == null) {
            return;
        }
        stateLayout.setEmptyText(R.string.no_milestones);
        toolbar.setTitle(R.string.milestone);
        toolbar.setOnMenuItemClickListener(item -> onAddMilestone());
        toolbar.inflateMenu(R.menu.add_menu);
        toolbar.setNavigationIcon(R.drawable.ic_clear);
        toolbar.setNavigationOnClickListener(v -> ((DialogFragment) getParentFragment()).dismiss());
        recycler.addDivider();
        adapter = new MilestonesAdapter(getPresenter().getMilestones());
        adapter.setListener(getPresenter());
        recycler.setEmptyView(stateLayout, refresh);
        recycler.setAdapter(adapter);
        recycler.addKeyLineDivider();
        if (savedInstanceState == null || (getPresenter().getMilestones().isEmpty() && !getPresenter().isApiCalled())) {
            getPresenter().onLoadMilestones(login, repo);
        }
        stateLayout.setOnReloadListener(v -> getPresenter().onLoadMilestones(login, repo));
        refresh.setOnRefreshListener(() -> getPresenter().onLoadMilestones(login, repo));
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

    @NonNull @Override public MilestonePresenter providePresenter() {
        return new MilestonePresenter();
    }

    @Override public void onMilestoneAdded(@NonNull MilestoneModel milestoneModel) {
        adapter.addItem(milestoneModel, 0);
    }

    private void showReload() {
        hideProgress();
        stateLayout.showReload(adapter.getItemCount());
    }

    private boolean onAddMilestone() {
        //noinspection ConstantConditions
        CreateMilestoneDialogFragment.newInstance(getArguments().getString(BundleConstant.EXTRA), getArguments().getString(BundleConstant.ID))
                .show(getChildFragmentManager(), CreateMilestoneDialogFragment.TAG);
        return true;
    }
}
