package com.fastaccess.ui.modules.profile.repos;

import com.fastaccess.data.dao.model.FilterOptionsModel;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.List;

/**
 * Created by adibk on 5/29/17.
 */

public class ProfileRepoFilterPresenter extends BasePresenter<ProfileRepoFilterMvp.View> implements ProfileRepoFilterMvp.Presenter {

    private FilterOptionsModel filterOptions = new FilterOptionsModel();

    @Override
    public void onTypeSelected(String selectedType) {
        filterOptions.setType(selectedType);
    }

    @Override
    public void onSortOptionSelected(String selectedSortOption) {
        filterOptions.setSort(selectedSortOption);
    }

    @Override
    public void onSortDirectionSelected(String selectedSortDirection) {
        filterOptions.setsortDirection(selectedSortDirection);
    }

    public FilterOptionsModel getFilterOptions() {
        return filterOptions;
    }

    @Override
    public int getTypePosition() {
        return filterOptions.getSelectedTypeIndex();
    }

    @Override
    public int getSortOptionPosition() {
        return filterOptions.getSelectedSortOptionIndex();
    }

    @Override
    public int getSortDirectionPosition() {
        return filterOptions.getSelectedSortDirectionIndex();
    }

    @Override
    public List<String> getTypesList() {
        return filterOptions.getTypesList();
    }

    @Override
    public List<String> getSortOptionList() {
        return filterOptions.getSortOptionList();
    }

    @Override
    public List<String> getSortDirectionList() {
        return filterOptions.getSortDirectionList();
    }
}
