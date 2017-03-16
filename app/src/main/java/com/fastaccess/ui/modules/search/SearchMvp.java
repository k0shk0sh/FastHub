package com.fastaccess.ui.modules.search;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.widget.AutoCompleteTextView;

import com.fastaccess.data.dao.model.SearchHistory;
import com.fastaccess.ui.base.mvp.BaseMvp;

import java.util.ArrayList;

/**
 * Created by Kosh on 08 Dec 2016, 8:19 PM
 */

interface SearchMvp {

    interface View extends BaseMvp.FAView {
        void onNotifyAdapter(@Nullable SearchHistory query);
    }

    interface Presenter extends BaseMvp.FAPresenter {

        @NonNull ArrayList<SearchHistory> getHints();

        void onSearchClicked(@NonNull ViewPager viewPager, @NonNull AutoCompleteTextView editText);

    }
}
