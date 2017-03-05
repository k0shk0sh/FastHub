package com.fastaccess.ui.modules.repos.extras.assignees;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.fastaccess.R;
import com.fastaccess.data.dao.UserModel;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.ui.AssigneesAdapter;
import com.fastaccess.ui.base.BaseDialogFragment;
import com.fastaccess.ui.widgets.FontTextView;
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

public class AssigneesView extends BaseDialogFragment<AssigneesMvp.View, AssigneesPresenter> implements AssigneesMvp.View {

    @BindView(R.id.title) FontTextView title;
    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @State HashMap<Integer, UserModel> selectionMap;
    private AssigneesAdapter adapter;
    private AssigneesMvp.SelectedAssigneesListener callback;

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

    public static AssigneesView newInstance(@NonNull List<UserModel> models) {
        AssigneesView fragment = new AssigneesView();
        fragment.setArguments(Bundler.start().putParcelableArrayList(BundleConstant.ITEM, (ArrayList<? extends Parcelable>) models).end());
        return fragment;
    }

    @Override protected int fragmentLayout() {
        return R.layout.simple_footer_list_dialog;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        title.setText(R.string.assignees);
        List<UserModel> list = getArguments().getParcelableArrayList(BundleConstant.ITEM);
        if (list != null) {
            adapter = new AssigneesAdapter(list, this);
            recycler.setAdapter(adapter);
        }
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
                ArrayList<UserModel> labels = Stream.of(selectionMap)
                        .filter(value -> value.getValue() != null)
                        .map(Map.Entry::getValue)
                        .collect(Collectors.toCollection(ArrayList::new));
                if (labels != null && !labels.isEmpty()) {
                    callback.onSelectedAssignees(labels);
                }
                dismiss();
                break;
        }
    }

    public HashMap<Integer, UserModel> getSelectionMap() {
        if (selectionMap == null) {
            selectionMap = new LinkedHashMap<>();
        }
        return selectionMap;
    }
}
