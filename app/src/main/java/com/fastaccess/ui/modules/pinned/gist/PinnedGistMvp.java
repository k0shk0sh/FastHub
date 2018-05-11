package com.fastaccess.ui.modules.pinned.gist;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.model.Gist;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kosh on 25 Mar 2017, 7:57 PM
 */

public interface PinnedGistMvp {

    interface View extends BaseMvp.FAView {
        void onNotifyAdapter(@Nullable List<Gist> items);

        void onDeletePinnedGist(long id, int position);
    }

    interface Presenter extends BaseViewHolder.OnItemClickListener<Gist> {
        @NonNull ArrayList<Gist> getPinnedGists();

        void onReload();
    }
}
