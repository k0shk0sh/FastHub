package com.fastaccess.ui.modules.repos.code.files.paths;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.RepoFilesModel;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;

/**
 * Created by Kosh on 20 Nov 2016, 11:10 AM
 */

interface RepoFilePathMvp {

    interface View extends BaseMvp.FAView {
        void onNotifyAdapter();

        void onItemClicked(@NonNull RepoFilesModel model, int position);

        void onAppendPath(@NonNull RepoFilesModel model);

        void onSendData();
    }

    interface Presenter extends BaseMvp.FAPresenter,
            BaseViewHolder.OnItemClickListener<RepoFilesModel> {

        void onFragmentCreated(@Nullable Bundle bundle);

        @NonNull String getRepoId();

        @NonNull String getLogin();

        @Nullable String getPath();

        @NonNull ArrayList<RepoFilesModel> getPaths();
    }


}
