package com.fastaccess.ui.modules.repos.extras.labels;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.evernote.android.state.State;
import com.fastaccess.R;
import com.fastaccess.data.dao.LabelListModel;
import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.adapter.LabelsAdapter;
import com.fastaccess.ui.base.BaseDialogFragment;
import com.fastaccess.ui.modules.repos.extras.labels.create.CreateLabelDialogFragment;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Kosh on 22 Feb 2017, 7:23 PM
 */

public class LabelsDialogFragment extends BaseDialogFragment<LabelsMvp.View, LabelsPresenter> implements LabelsMvp.View {

    @BindView(R.id.title) FontTextView title;
    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) SwipeRefreshLayout refresh;
    @BindView(R.id.add) View add;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    @BindView(R.id.fastScroller) RecyclerViewFastScroller fastScroller;
    @State ArrayList<LabelModel> labelModels = new ArrayList<>();

    private OnLoadMore onLoadMore;
    private LabelsAdapter adapter;
    private LabelsMvp.SelectedLabelsListener callback;

    public static LabelsDialogFragment newInstance(@Nullable LabelListModel selectedLabels, @NonNull String repo, @NonNull String login) {
        LabelsDialogFragment fragment = new LabelsDialogFragment();
        fragment.setArguments(Bundler.start()
                .putParcelableArrayList(BundleConstant.EXTRA, selectedLabels)
                .put(BundleConstant.EXTRA_TWO, repo)
                .put(BundleConstant.EXTRA_THREE, login)
                .end());
        return fragment;
    }

    @OnClick(R.id.add) void onAddLabel() {
        String repo = getArguments().getString(BundleConstant.EXTRA_TWO);
        String login = getArguments().getString(BundleConstant.EXTRA_THREE);
        if (!InputHelper.isEmpty(repo) && !InputHelper.isEmpty(login)) {
            CreateLabelDialogFragment.newInstance(login, repo).show(getChildFragmentManager(), "CreateLabelDialogFragment");
        }
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof LabelsMvp.SelectedLabelsListener) {
            callback = (LabelsMvp.SelectedLabelsListener) getParentFragment();
        } else if (context instanceof LabelsMvp.SelectedLabelsListener) {
            callback = (LabelsMvp.SelectedLabelsListener) context;
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
        stateLayout.setEmptyText(R.string.no_labels);
        recycler.setEmptyView(stateLayout, refresh);
        refresh.setOnRefreshListener(() -> getPresenter().onCallApi(1, null));
        stateLayout.setOnReloadListener(v -> getPresenter().onCallApi(1, null));
        recycler.addDivider();
        title.setText(R.string.labels);
        add.setVisibility(View.VISIBLE);
        labelModels = getArguments().getParcelableArrayList(BundleConstant.EXTRA);
        if (labelModels == null) {
            labelModels = new ArrayList<>();
        }
        add.setVisibility(callback == null ? View.GONE : View.VISIBLE);
        adapter = new LabelsAdapter(getPresenter().getLabels(), this);
        recycler.setAdapter(adapter);
        fastScroller.attachRecyclerView(recycler);
        recycler.addOnScrollListener(getLoadMore());
        if (getPresenter().getLabels().isEmpty() && !getPresenter().isApiCalled()) {
            getPresenter().onCallApi(1, null);
        }
    }

    @NonNull @Override public LabelsPresenter providePresenter() {
        Bundle bundle = getArguments();
        //noinspection ConstantConditions
        return new LabelsPresenter(bundle.getString(BundleConstant.EXTRA_THREE), bundle.getString(BundleConstant.EXTRA_TWO));
    }

    @Override public boolean isLabelSelected(LabelModel labelModel) {
        return labelModels.indexOf(labelModel) != -1;
    }

    @Override public void onToggleSelection(LabelModel labelModel, boolean select) {
        if (select) {
            labelModels.add(labelModel);
        } else {
            labelModels.remove(labelModel);
        }
        adapter.notifyDataSetChanged();
    }

    @SuppressWarnings("unchecked") @NonNull @Override public OnLoadMore getLoadMore() {
        if (onLoadMore == null) {
            onLoadMore = new OnLoadMore(getPresenter());
        }
        return onLoadMore;
    }

    @Override public void onNotifyAdapter(@Nullable List<LabelModel> items, int page) {
        hideProgress();
        if (items == null || items.isEmpty()) {
            adapter.clear();
            return;
        }
        if (page <= 1) {
            adapter.insertItems(items);
        } else {
            adapter.addItems(items);
        }
    }

    @Override public void onLabelAdded(@NonNull LabelModel labelModel) {
        adapter.addItem(labelModel, 0);
        recycler.scrollToPosition(0);
    }

    @OnClick({R.id.cancel, R.id.ok}) public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                dismiss();
                break;
            case R.id.ok:
                if (callback != null) callback.onSelectedLabels(labelModels);
                dismiss();
                break;
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

    private void showReload() {
        hideProgress();
        stateLayout.showReload(adapter.getItemCount());
    }
}
