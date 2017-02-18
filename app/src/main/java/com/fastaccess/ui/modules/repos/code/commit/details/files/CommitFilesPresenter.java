package com.fastaccess.ui.modules.repos.code.commit.details.files;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.data.dao.CommitFileListModel;
import com.fastaccess.data.dao.CommitFileModel;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

/**
 * Created by Kosh on 15 Feb 2017, 10:10 PM
 */

class CommitFilesPresenter extends BasePresenter<CommitFilesMvp.View> implements CommitFilesMvp.Presenter {

    private CommitFileListModel files = new CommitFileListModel();

    @Override public void onItemClick(int position, View v, CommitFileModel item) {

    }

    @Override public void onItemLongClick(int position, View v, CommitFileModel item) {

    }

    @Override public void onFragmentCreated(@Nullable Bundle bundle) {
        if (bundle != null) {
            CommitFileListModel files = (CommitFileListModel) bundle.get(BundleConstant.EXTRA);
            if (files != null) {
                this.files.addAll(files);
            }
            sendToView(CommitFilesMvp.View::onNotifyAdapter);
        } else {
            throw new NullPointerException("Bundle is null");
        }
    }

    @NonNull @Override public CommitFileListModel getFiles() {
        return files;
    }

    @Override public int getCurrentPage() {
        return 0;
    }

    @Override public int getPreviousTotal() {
        return 0;
    }

    @Override public void setCurrentPage(int page) {

    }

    @Override public void setPreviousTotal(int previousTotal) {

    }

    @Override public void onCallApi(int page, @Nullable String parameter) {

    }
}
