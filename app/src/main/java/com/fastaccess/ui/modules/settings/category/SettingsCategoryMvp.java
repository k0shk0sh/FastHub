package com.fastaccess.ui.modules.settings.category;

import com.fastaccess.ui.base.mvp.BaseMvp;

/**
 * Created by JediB on 5/12/2017.
 */

public interface SettingsCategoryMvp {
	interface View extends BaseMvp.FAView {
		@Override
		void onThemeChanged();
	}

	interface Presenter extends BaseMvp.FAPresenter {

	}
}
