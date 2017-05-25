package com.fastaccess.ui.modules.profile.banner;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.model.PinnedRepos;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JediB on 5/25/2017.
 */

public interface BannerInfoMvp {

	interface View extends BaseMvp.FAView {
	}

	interface Presenter extends BaseViewHolder.OnItemClickListener<PinnedRepos> {
	}

}
