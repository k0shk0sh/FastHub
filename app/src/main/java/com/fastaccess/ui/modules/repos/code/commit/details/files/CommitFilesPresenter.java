package com.fastaccess.ui.modules.repos.code.commit.details.files;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MenuInflater;
import android.view.View;
import android.widget.PopupMenu;

import com.fastaccess.R;
import com.fastaccess.data.dao.CommitFileListModel;
import com.fastaccess.data.dao.CommitFileModel;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.repos.code.commit.viewer.FullCommitFileActivity;

import java.util.ArrayList;

/**
 * Created by Kosh on 15 Feb 2017, 10:10 PM
 */

class CommitFilesPresenter extends BasePresenter<CommitFilesMvp.View> implements CommitFilesMvp.Presenter {

    private CommitFileListModel files = new CommitFileListModel();

    @Override public void onItemClick(int position, View v, CommitFileModel item) {
        if (v.getId() == R.id.open) {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.commit_row_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item1 -> {
                switch (item1.getItemId()) {
                    case R.id.open:
                        FullCommitFileActivity.start(v.getContext(), item);
                        break;
                    case R.id.share:
                        ActivityHelper.shareUrl(v.getContext(), item.getBlobUrl());
                        break;
                    case R.id.download:
                        Activity activity = ActivityHelper.getActivity(v.getContext());
                        if (activity == null) break;
                        if (ActivityHelper.checkAndRequestReadWritePermission(activity)) {
                            RestProvider.downloadFile(v.getContext(), item.getRawUrl());
                        }
                        break;
                    case R.id.copy:
                        AppHelper.copyToClipboard(v.getContext(), item.getBlobUrl());
                        break;
                }
                return true;
            });
            popup.show();
        }
    }

    @Override public void onItemLongClick(int position, View v, CommitFileModel item) {}

    @Override public void onFragmentCreated(@Nullable Bundle bundle) {
        if (bundle != null) {
            String sha = bundle.getString(BundleConstant.ID);
            if (!InputHelper.isEmpty(sha)) {
                CommitFileListModel commitFiles = CommitFilesSingleton.getInstance().getByCommitId(sha);
                ArrayList<CommitFileModel> fileModels = new ArrayList<>();
                if (commitFiles != null) {
                    fileModels.addAll(commitFiles);
                    CommitFilesSingleton.getInstance().clear();
                }
                sendToView(view -> view.onNotifyAdapter(fileModels));
            }
        } else {
            throw new NullPointerException("Bundle is null");
        }
    }

    @NonNull @Override public CommitFileListModel getFiles() {
        return files;
    }
}
