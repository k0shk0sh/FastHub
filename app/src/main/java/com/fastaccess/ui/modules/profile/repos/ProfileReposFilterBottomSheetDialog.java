package com.fastaccess.ui.modules.profile.repos;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.fastaccess.R;
import com.fastaccess.data.dao.model.FilterOptionsModel;
import com.fastaccess.ui.base.BaseBottomSheetDialog;

import butterknife.BindView;
import butterknife.OnClick;

public class ProfileReposFilterBottomSheetDialog extends BaseBottomSheetDialog {

    private static final String FILTER = "filter";

    @BindView(R.id.type_selection) Spinner typeSelectionSpinner;
    @BindView(R.id.sort_selection) Spinner sortSelectionSpinner;
    @BindView(R.id.filter_sheet_apply_btn) View applyBtn;
    @BindView(R.id.sort_direction_selection) Spinner sortDirectionSpinner;

    private ProfileReposFilterChangeListener listener;
    private FilterOptionsModel currentFilterOptions;

    @Override
    protected int layoutRes() {
        return R.layout.filter_bottom_sheet;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayAdapter<String> typesAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, currentFilterOptions.getTypesList());
        ArrayAdapter<String> sortOptionsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, currentFilterOptions.getSortOptionList());
        ArrayAdapter<String> sortDirectionAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, currentFilterOptions.getSortDirectionList());
        typeSelectionSpinner.setAdapter(typesAdapter);
        sortSelectionSpinner.setAdapter(sortOptionsAdapter);
        sortDirectionSpinner.setAdapter(sortDirectionAdapter);

        typeSelectionSpinner.setSelection(currentFilterOptions.getSelectedTypeIndex());
        sortSelectionSpinner.setSelection(currentFilterOptions.getSelectedSortOptionIndex());
        sortDirectionSpinner.setSelection(currentFilterOptions.getSelectedSortDirectionIndex());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = ((ProfileReposFragment) getParentFragment());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            setCurrentFilterOptions(((FilterOptionsModel) savedInstanceState.get(FILTER)));
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(FILTER, currentFilterOptions);
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

    @Override
    public void dismiss() {
        currentFilterOptions = null;
        super.dismiss();
    }

    public void setCurrentFilterOptions(FilterOptionsModel currentFilterOptions) {
        this.currentFilterOptions = currentFilterOptions;
    }

    public interface ProfileReposFilterChangeListener {
        void onFilterApply();
        void onTypeSelected(String selectedType);
        void onSortOptionSelected(String selectedSortOption);
        void onSortDirectionSelected(String selectedSortDirection);
    }
}
