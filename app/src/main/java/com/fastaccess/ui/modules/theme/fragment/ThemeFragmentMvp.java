package com.fastaccess.ui.modules.theme.fragment;

import android.support.annotation.ColorInt;

import com.fastaccess.ui.base.mvp.BaseMvp;

/**
 * Created by Kosh on 08 Jun 2017, 10:52 PM
 */

public interface ThemeFragmentMvp {

    interface ThemeListener {
        void onChangePrimaryDarkColor(@ColorInt int color, boolean darkIcons);
    }

    interface View extends BaseMvp.FAView {}

    interface Presenter {}
}
