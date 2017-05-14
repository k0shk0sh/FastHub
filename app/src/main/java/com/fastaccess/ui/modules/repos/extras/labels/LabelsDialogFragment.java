package com.fastaccess.ui.modules.repos.extras.labels;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.fastaccess.R;
import com.fastaccess.data.dao.LabelListModel;
import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.ui.adapter.LabelsAdapter;
import com.fastaccess.ui.base.BaseDialogFragment;
import com.fastaccess.ui.modules.repos.extras.labels.create.CreateLabelDialogFragment;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import icepick.State;

/**
 * Created by Kosh on 22 Feb 2017, 7:23 PM
 */

public class LabelsDialogFragment extends BaseDialogFragment<LabelsMvp.View, LabelsPresenter> implements LabelsMvp.View {

    @BindView(R.id.title) FontTextView title;
    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.add) View add;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    @State HashMap<Integer, LabelModel> selectionMap;
    private LabelsAdapter adapter;
    private LabelsMvp.SelectedLabelsListener callback;

    public static LabelsDialogFragment newInstance(@NonNull List<LabelModel> models, @Nullable LabelListModel selectedLabels,
                                                   @NonNull String repo, @NonNull String login) {
        LabelsDialogFragment fragment = new LabelsDialogFragment();
        fragment.setArguments(Bundler.start()
                .putParcelableArrayList(BundleConstant.ITEM, (ArrayList<? extends Parcelable>) models)
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
        } else {
            throw new IllegalArgumentException("Parent Fragment or Activity must implement LabelsMvp.SelectedLabelsListener");
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
        recycler.setEmptyView(stateLayout);
        recycler.addKeyLineDivider();
        title.setText(R.string.labels);
        add.setVisibility(View.VISIBLE);
        List<LabelModel> list = getArguments().getParcelableArrayList(BundleConstant.ITEM);
        List<LabelModel> selectedLabels = getArguments().getParcelableArrayList(BundleConstant.EXTRA);
        if (list != null) {
            adapter = new LabelsAdapter(list, this);
            recycler.setAdapter(adapter);
            if (savedInstanceState == null) {
                if (selectedLabels != null && !selectedLabels.isEmpty()) {
                    Stream.of(selectedLabels)
                            .map(list::indexOf)
                            .filter(value -> value != -1)
                            .forEach(integer -> onToggleSelection(integer, true));
                }
            }
        }
    }

    @NonNull @Override public LabelsPresenter providePresenter() {
        return new LabelsPresenter();
    }

    @Override public boolean isLabelSelected(int position) {
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
                ArrayList<LabelModel> labels = Stream.of(selectionMap)
                        .filter(value -> value.getValue() != null)
                        .map(Map.Entry::getValue)
                        .collect(Collectors.toCollection(ArrayList::new));
                if (labels != null && !labels.isEmpty()) {
                    callback.onSelectedLabels(labels);
                }
                dismiss();
                break;
        }
    }

    public HashMap<Integer, LabelModel> getSelectionMap() {
        if (selectionMap == null) {
            selectionMap = new LinkedHashMap<>();
        }
        return selectionMap;
    }
}
