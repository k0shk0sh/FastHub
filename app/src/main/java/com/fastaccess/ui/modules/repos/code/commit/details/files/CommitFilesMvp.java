package com.fastaccess.ui.modules.repos.code.commit.details.files;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.CommitFileListModel;
import com.fastaccess.data.dao.CommitFileModel;
import com.fastaccess.ui.adapter.callback.OnToggleView;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

/**
 * Created by Kosh on 20 Nov 2016, 11:10 AM
 */

interface CommitFilesMvp {

    interface View extends BaseMvp.FAView, OnToggleView {

        void onNotifyAdapter();

    }

    interface Presenter extends BaseMvp.FAPresenter,
            BaseMvp.PaginationListener<String>, BaseViewHolder.OnItemClickListener<CommitFileModel> {

        void onFragmentCreated(@Nullable Bundle bundle);

        @NonNull CommitFileListModel getFiles();
    }


}
