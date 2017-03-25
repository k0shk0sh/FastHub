package com.fastaccess.ui.modules.pinned;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.model.PinnedRepos;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;

/**
 * Created by Kosh on 25 Mar 2017, 7:57 PM
 */

public interface PinnedReposMvp {

    interface View extends BaseMvp.FAView {
        void onNotifyAdapter();

        void onDeletePinnedRepo(long id, int position);
    }

    interface Presenter extends BaseViewHolder.OnItemClickListener<PinnedRepos> {
        @NonNull ArrayList<PinnedRepos> getPinnedRepos();

        void onReload();
    }
}
