package com.fastaccess.ui.modules.gists.gist.files;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.FilesListModel;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

/**
 * Created by Kosh on 13 Nov 2016, 1:35 PM
 */

interface GistFilesListMvp {

    interface View extends BaseMvp.FAView {
        void onOpenFile(@NonNull FilesListModel item);
    }

    interface Presenter extends BaseMvp.FAPresenter, BaseViewHolder.OnItemClickListener<FilesListModel> {}
}
