package com.fastaccess.ui.modules.gists.gist.files;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.FilesListModel;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;

/**
 * Created by Kosh on 13 Nov 2016, 1:35 PM
 */

public class GistFilesListPresenter extends BasePresenter<GistFilesListMvp.View> implements GistFilesListMvp.Presenter {
    private ArrayList<FilesListModel> listModels;
    @Getter private HashMap<String, FilesListModel> filesMap = new HashMap<>();

    @Override public void onItemClick(int position, View v, FilesListModel item) {
        if (getView() != null) {
            if (v.getId() == R.id.delete) {
                getView().onDeleteFile(item, position);
            } else if (v.getId() == R.id.edit) {
                getView().onEditFile(item, position);
            } else {
                getView().onOpenFile(item, position);
            }
        }
    }

    @Override public void onItemLongClick(int position, View v, FilesListModel item) {}

    @Override public void onSetList(@Nullable ArrayList<FilesListModel> files) {
        this.listModels = files;
    }

    @NonNull @Override public ArrayList<FilesListModel> getFiles() {
        if (listModels == null) {
            return new ArrayList<>();
        }
        return listModels;
    }
}
