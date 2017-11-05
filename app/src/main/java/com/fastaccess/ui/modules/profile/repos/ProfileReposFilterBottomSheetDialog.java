package com.fastaccess.ui.modules.profile.repos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.fastaccess.R;
import com.fastaccess.data.dao.FilterOptionsModel;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.ui.base.BaseBottomSheetDialog;
import com.fastaccess.ui.modules.profile.org.repos.OrgReposFragment;
import com.fastaccess.ui.modules.profile.org.repos.OrgReposMvp;
import com.fastaccess.ui.modules.search.SearchUserActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class ProfileReposFilterBottomSheetDialog extends BaseBottomSheetDialog {

    @BindView(R.id.type_selection) Spinner typeSelectionSpinner;
    @BindView(R.id.sort_selection) Spinner sortSelectionSpinner;
    @BindView(R.id.filter_sheet_apply_btn) View applyBtn;
    @BindView(R.id.sort_direction_selection) Spinner sortDirectionSpinner;
    @BindView(R.id.sort_layout) LinearLayout sortLayout;
    @BindView(R.id.sort_direction_layout) LinearLayout sortDirectionlayout;
    private FilterOptionsModel currentFilterOptions;

    private ProfileReposFilterChangeListener listener;

    public static ProfileReposFilterBottomSheetDialog newInstance(@NonNull FilterOptionsModel currentFilterOptions) {
        ProfileReposFilterBottomSheetDialog fragment = new ProfileReposFilterBottomSheetDialog();
        fragment.setArguments(Bundler.start().put(BundleConstant.ITEM, currentFilterOptions).end());
        return fragment;
    }

    @Override protected int layoutRes() {
        return R.layout.filter_bottom_sheet;
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentFilterOptions = getArguments().getParcelable(BundleConstant.ITEM);
        if (currentFilterOptions == null) return;
        ArrayAdapter<String> typesAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,
                currentFilterOptions.getTypesList());
        ArrayAdapter<String> sortOptionsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,
                currentFilterOptions.getSortOptionList());
        ArrayAdapter<String> sortDirectionAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,
                currentFilterOptions.getSortDirectionList());
        typeSelectionSpinner.setAdapter(typesAdapter);
        sortSelectionSpinner.setAdapter(sortOptionsAdapter);
        sortDirectionSpinner.setAdapter(sortDirectionAdapter);
        typeSelectionSpinner.setSelection(currentFilterOptions.getSelectedTypeIndex());
        sortSelectionSpinner.setSelection(currentFilterOptions.getSelectedSortOptionIndex());
        sortDirectionSpinner.setSelection(currentFilterOptions.getSelectedSortDirectionIndex());
        if (currentFilterOptions.isOrg()) {
            sortLayout.setVisibility(View.GONE);
            sortDirectionlayout.setVisibility(View.GONE);
        }
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OrgReposMvp.View || getParentFragment() instanceof OrgReposMvp.View) {
            listener = ((OrgReposFragment) getParentFragment());
        } else {
            listener = ((ProfileReposFragment) getParentFragment());
        }
    }

    @Override public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @OnClick(R.id.filter_sheet_apply_btn) public void onApply() {
        if (listener != null) {
            listener.onTypeSelected((String) typeSelectionSpinner.getSelectedItem());
            listener.onSortOptionSelected((String) sortSelectionSpinner.getSelectedItem());
            listener.onSortDirectionSelected((String) sortDirectionSpinner.getSelectedItem());
            listener.onFilterApply();
            dismiss();
        }
    }

    @OnClick(R.id.filter_sheet_reset_btn) public void onReset() {
        typeSelectionSpinner.setSelection(0);
        sortDirectionSpinner.setSelection(0);
        sortSelectionSpinner.setSelection(0);
    }

    @OnClick(R.id.filter_sheet_search_btn) public void startSearch() {
        if (listener != null) {
            Intent intent = SearchUserActivity.Companion.getIntent(getContext(), listener.getLogin(), "");
            startActivity(intent);
        }
        dismiss();
    }

    public interface ProfileReposFilterChangeListener {
        void onFilterApply();

        void onTypeSelected(String selectedType);

        void onSortOptionSelected(String selectedSortOption);

        void onSortDirectionSelected(String selectedSortDirection);

        String getLogin();
    }
}
