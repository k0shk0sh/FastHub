package com.fastaccess.ui.modules.gists.gist.files;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.FilesListModel;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.modules.gists.create.dialog.AddGistMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Kosh on 13 Nov 2016, 1:35 PM
 */

public interface GistFilesListMvp {

    interface View extends BaseMvp.FAView, AddGistMvp.AddGistFileListener {
        void onOpenFile(@NonNull FilesListModel item, int position);

        void onDeleteFile(@NonNull FilesListModel item, int position);

        void onEditFile(@NonNull FilesListModel item, int position);

        void onInitFiles(@Nullable ArrayList<FilesListModel> file, boolean isOwner);

        void onAddNewFile();

        @NonNull HashMap<String, FilesListModel> getFiles();
    }

    interface Presenter extends BaseMvp.FAPresenter, BaseViewHolder.OnItemClickListener<FilesListModel> {
        void onSetList(@Nullable ArrayList<FilesListModel> files);

        @NonNull ArrayList<FilesListModel> getFiles();
    }
}
