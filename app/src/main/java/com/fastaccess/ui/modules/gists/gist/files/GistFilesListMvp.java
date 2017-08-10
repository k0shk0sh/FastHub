package com.fastaccess.ui.modules.gists.gist.files;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.FilesListModel;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.List;

/**
 * Created by Kosh on 13 Nov 2016, 1:35 PM
 */

public interface GistFilesListMvp {

    interface View extends BaseMvp.FAView {
        void onOpenFile(@NonNull FilesListModel item);

        void onDeleteFile(@NonNull FilesListModel item, int position);

        void onEditFile(@NonNull FilesListModel item, int position);
    }

    interface Presenter extends BaseMvp.FAPresenter, BaseViewHolder.OnItemClickListener<FilesListModel> {}

    interface UpdateGistCallback {
        void onUpdateGist(@NonNull List<FilesListModel> files, @NonNull String filename);
    }
}
