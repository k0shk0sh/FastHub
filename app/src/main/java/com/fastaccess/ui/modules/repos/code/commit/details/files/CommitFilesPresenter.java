package com.fastaccess.ui.modules.repos.code.commit.details.files;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MenuInflater;
import android.view.View;
import android.widget.PopupMenu;

import com.fastaccess.R;
import com.fastaccess.data.dao.CommentRequestModel;
import com.fastaccess.data.dao.CommitFileChanges;
import com.fastaccess.data.dao.CommitFileListModel;
import com.fastaccess.data.dao.CommitFileModel;
import com.fastaccess.data.dao.CommitLinesModel;
import com.fastaccess.data.dao.NameParser;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.code.CodeViewerActivity;

import java.util.ArrayList;

import io.reactivex.Observable;

/**
 * Created by Kosh on 15 Feb 2017, 10:10 PM
 */

class CommitFilesPresenter extends BasePresenter<CommitFilesMvp.View> implements CommitFilesMvp.Presenter {
    @com.evernote.android.state.State String sha;
    ArrayList<CommitFileChanges> changes = new ArrayList<>();

    @Override public void onItemClick(int position, View v, CommitFileChanges model) {
        if (v.getId() == R.id.patchList) {
            sendToView(view -> view.onOpenForResult(position, model));
        } else if (v.getId() == R.id.open) {
            CommitFileModel item = model.getCommitFileModel();
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.commit_row_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item1 -> {
                switch (item1.getItemId()) {
                    case R.id.open:
                        v.getContext().startActivity(CodeViewerActivity.createIntent(v.getContext(), item.getContentsUrl(), item.getBlobUrl()));
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

    @Override public void onItemLongClick(int position, View v, CommitFileChanges item) {}

    @Override public void onFragmentCreated(@Nullable Bundle bundle) {
        if (sha == null) {
            if (bundle != null) {
                sha = bundle.getString(BundleConstant.ID);
            }
        }
        if (!InputHelper.isEmpty(sha)) {
            CommitFileListModel commitFiles = CommitFilesSingleton.getInstance().getByCommitId(sha);
            if (commitFiles != null) {
                manageObservable(Observable.just(commitFiles)
                        .map(CommitFileChanges::construct)
                        .doOnSubscribe(disposable -> sendToView(CommitFilesMvp.View::clearAdapter))
                        .doOnNext(commitFileChanges -> {
                            sendToView(view -> view.onNotifyAdapter(commitFileChanges));
                        })
                        .doOnComplete(() -> sendToView(BaseMvp.FAView::hideProgress)));
            }
        } else {
            throw new NullPointerException("Bundle is null");
        }
    }

    @Override public void onSubmitComment(@NonNull String comment, @NonNull CommitLinesModel item, @Nullable Bundle bundle) {
        if (bundle != null) {
            String blob = bundle.getString(BundleConstant.ITEM);
            String path = bundle.getString(BundleConstant.EXTRA);
            if (path == null || sha == null) return;
            CommentRequestModel commentRequestModel = new CommentRequestModel();
            commentRequestModel.setBody(comment);
            commentRequestModel.setPath(path);
            commentRequestModel.setPosition(item.getPosition());
            commentRequestModel.setLine(item.getRightLineNo() > 0 ? item.getRightLineNo() : item.getLeftLineNo());
            NameParser nameParser = new NameParser(blob);
            onSubmit(nameParser.getUsername(), nameParser.getName(), commentRequestModel);
        }
    }

    @Override public void onSubmit(String username, String name, CommentRequestModel commentRequestModel) {
        makeRestCall(RestProvider.getRepoService(isEnterprise()).postCommitComment(username, name, sha,
                commentRequestModel), newComment -> sendToView(view -> view.onCommentAdded(newComment)));
    }

    @Override protected void onDestroy() {
        CommitFilesSingleton.getInstance().clear();
        super.onDestroy();
    }
}
