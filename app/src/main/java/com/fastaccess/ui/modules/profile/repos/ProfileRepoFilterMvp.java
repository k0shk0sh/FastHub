package com.fastaccess.ui.modules.profile.repos;

import com.fastaccess.ui.base.mvp.BaseMvp;

import java.util.List;

/**
 * Created by adibk on 5/29/17.
 */

public interface ProfileRepoFilterMvp {

    interface View extends BaseMvp.FAView {

    }

    interface Presenter {

        void onTypeSelected(String selectedType);
        void onSortOptionSelected(String selectedSortOption);
        void onSortDirectionSelected(String selectedSortDirection);

        int getTypePosition();
        int getSortOptionPosition();
        int getSortDirectionPosition();

        List<String> getTypesList();
        List<String> getSortOptionList();
        List<String> getSortDirectionList();
    }
}
