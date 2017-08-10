package com.fastaccess.ui.modules.gists.gist.files;

import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.FilesListModel;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

/**
 * Created by Kosh on 13 Nov 2016, 1:35 PM
 */

class GistFilesListPresenter extends BasePresenter<GistFilesListMvp.View> implements GistFilesListMvp.Presenter {

    @Override public void onItemClick(int position, View v, FilesListModel item) {
        if (getView() != null) {
            if (v.getId() == R.id.delete) {
                getView().onDeleteFile(item, position);
            } else if (v.getId() == R.id.edit) {
                getView().onEditFile(item, position);
            } else {
                getView().onOpenFile(item);
            }
        }
    }

    @Override public void onItemLongClick(int position, View v, FilesListModel item) {}
}
