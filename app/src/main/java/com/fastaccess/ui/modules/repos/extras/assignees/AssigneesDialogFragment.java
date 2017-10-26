package com.fastaccess.ui.modules.repos.extras.assignees;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.evernote.android.state.State;
import com.fastaccess.R;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.ui.adapter.AssigneesAdapter;
import com.fastaccess.ui.base.BaseDialogFragment;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Kosh on 22 Feb 2017, 7:23 PM
 */

public class AssigneesDialogFragment extends BaseDialogFragment<AssigneesMvp.View, AssigneesPresenter> implements AssigneesMvp.View {

    @BindView(R.id.title) FontTextView title;
    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    @BindView(R.id.refresh) SwipeRefreshLayout refresh;
    @BindView(R.id.fastScroller) RecyclerViewFastScroller fastScroller;
    @State HashMap<Integer, User> selectionMap;

    private AssigneesAdapter adapter;
    private AssigneesMvp.SelectedAssigneesListener callback;

    public static AssigneesDialogFragment newInstance(@NonNull String login, @NonNull String repoId, boolean isAssignees) {
        AssigneesDialogFragment fragment = new AssigneesDialogFragment();
        fragment.setArguments(Bundler.start()
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.EXTRA_TWO, isAssignees)
                .end());
        return fragment;
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof AssigneesMvp.SelectedAssigneesListener) {
            callback = (AssigneesMvp.SelectedAssigneesListener) getParentFragment();
        } else if (context instanceof AssigneesMvp.SelectedAssigneesListener) {
            callback = (AssigneesMvp.SelectedAssigneesListener) context;
        } else {
            throw new IllegalArgumentException("Parent Fragment or Activity must implement AssigneesMvp.SelectedAssigneesListener");
        }
    }

    @Override public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override protected int fragmentLayout() {
        return R.layout.simple_footer_list_dialog;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            callApi();
        }
        refresh.setOnRefreshListener(this::callApi);
        stateLayout.setOnReloadListener(v -> callApi());
        boolean isAssinees = getArguments().getBoolean(BundleConstant.EXTRA_TWO);
        stateLayout.setEmptyText(isAssinees ? R.string.no_assignees : R.string.no_reviewers);
        recycler.setEmptyView(stateLayout, refresh);
        recycler.addKeyLineDivider();
        title.setText(isAssinees ? R.string.assignees : R.string.reviewers);
        adapter = new AssigneesAdapter(getPresenter().getList(), this);
        recycler.setAdapter(adapter);
        fastScroller.attachRecyclerView(recycler);
    }

    @NonNull @Override public AssigneesPresenter providePresenter() {
        return new AssigneesPresenter();
    }

    @Override public boolean isAssigneeSelected(int position) {
        return getSelectionMap().get(position) != null;
    }

    @Override public void onToggleSelection(int position, boolean select) {
        if (select) {
            getSelectionMap().put(position, adapter.getItem(position));
        } else {
            getSelectionMap().remove(position);
        }
        adapter.notifyDataSetChanged();
    }

    @OnClick({R.id.cancel, R.id.ok}) public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                dismiss();
                break;
            case R.id.ok:
                ArrayList<User> labels = Stream.of(selectionMap)
                        .filter(value -> value.getValue() != null)
                        .map(Map.Entry::getValue)
                        .collect(Collectors.toCollection(ArrayList::new));
                callback.onSelectedAssignees(labels != null ? labels : new ArrayList<>(), getArguments().getBoolean(BundleConstant.EXTRA_TWO));
                dismiss();
                break;
        }
    }

    @Override public void onNotifyAdapter(@Nullable List<User> items) {
        hideProgress();
        if (items == null || items.isEmpty()) {
            adapter.clear();
            return;
        }
        adapter.insertItems(items);
    }

    @Override public void showProgress(@StringRes int resId) {
        stateLayout.showProgress();
        refresh.setRefreshing(true);
    }

    @Override public void hideProgress() {
        stateLayout.hideProgress();
        refresh.setRefreshing(false);
    }

    @Override public void showErrorMessage(@NonNull String message) {
        showReload();
        super.showErrorMessage(message);
    }

    @Override public void showMessage(int titleRes, int msgRes) {
        showReload();
        super.showMessage(titleRes, msgRes);
    }

    private void showReload() {
        hideProgress();
        stateLayout.showReload(adapter.getItemCount());
    }

    public HashMap<Integer, User> getSelectionMap() {
        if (selectionMap == null) {
            selectionMap = new LinkedHashMap<>();
        }
        return selectionMap;
    }

    private void callApi() {
        //noinspection ConstantConditions
        getPresenter().onCallApi(getArguments().getString(BundleConstant.EXTRA),
                getArguments().getString(BundleConstant.ID),
                getArguments().getBoolean(BundleConstant.EXTRA_TWO));
    }
}
