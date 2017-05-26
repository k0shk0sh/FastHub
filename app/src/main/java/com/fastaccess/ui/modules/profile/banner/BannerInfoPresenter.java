package com.fastaccess.ui.modules.profile.banner;

import android.view.View;

import com.fastaccess.data.dao.model.PinnedRepos;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

/**
 * Created by JediB on 5/25/2017.
 */

public class BannerInfoPresenter extends BasePresenter<BannerInfoMvp.View> implements BannerInfoMvp.Presenter {
	@Override
	public void onItemClick(int position, View v, PinnedRepos item) {

	}

	@Override
	public void onItemLongClick(int position, View v, PinnedRepos item) {

	}
}
