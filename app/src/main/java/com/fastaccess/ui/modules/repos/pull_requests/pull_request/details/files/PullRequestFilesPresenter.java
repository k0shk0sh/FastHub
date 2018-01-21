package com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.files;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MenuInflater;
import android.view.View;
import android.widget.PopupMenu;

import com.fastaccess.R;
import com.fastaccess.data.dao.CommitFileChanges;
import com.fastaccess.data.dao.CommitFileModel;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.code.CodeViewerActivity;
import com.fastaccess.ui.modules.repos.code.commit.details.CommitPagerActivity;

import java.util.ArrayList;

import io.reactivex.Observable;

/**
 * Created by Kosh on 03 Dec 2016, 3:48 PM
 */

class PullRequestFilesPresenter extends BasePresenter<PullRequestFilesMvp.View> implements PullRequestFilesMvp.Presenter {

    @com.evernote.android.state.State String login;
    @com.evernote.android.state.State String repoId;
    @com.evernote.android.state.State long number;
    private ArrayList<CommitFileChanges> files = new ArrayList<>();
    private int page;
    private int previousTotal;
    private int lastPage = Integer.MAX_VALUE;

    @Override public int getCurrentPage() {
        return page;
    }

    @Override public int getPreviousTotal() {
        return previousTotal;
    }

    @Override public void setCurrentPage(int page) {
        this.page = page;
    }

    @Override public void setPreviousTotal(int previousTotal) {
        this.previousTotal = previousTotal;
    }

    @Override public void onError(@NonNull Throwable throwable) {
        onWorkOffline();
        super.onError(throwable);
    }

    @Override public boolean onCallApi(int page, @Nullable Object parameter) {
        if (page == 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        setCurrentPage(page);
        if (page > lastPage || lastPage == 0) {
            sendToView(PullRequestFilesMvp.View::hideProgress);
            return false;
        }
        if (repoId == null || login == null) return false;
        makeRestCall(RestProvider.getPullRequestService(isEnterprise()).getPullRequestFiles(login, repoId, number, page)
                        .flatMap(commitFileModelPageable -> {
                            if (commitFileModelPageable != null) {
                                lastPage = commitFileModelPageable.getLast();
                                if (commitFileModelPageable.getItems() != null) {
                                    return Observable.just(CommitFileChanges.construct(commitFileModelPageable.getItems()));
                                }
                            }
                            return Observable.empty();
                        }),
                response -> sendToView(view -> view.onNotifyAdapter(response, page)));
        return true;
    }

    @Override public void onFragmentCreated(@NonNull Bundle bundle) {
        repoId = bundle.getString(BundleConstant.ID);
        login = bundle.getString(BundleConstant.EXTRA);
        number = bundle.getLong(BundleConstant.EXTRA_TWO);
        if (!InputHelper.isEmpty(login) && !InputHelper.isEmpty(repoId)) {
            onCallApi(1, null);
        }
    }

    @NonNull @Override public ArrayList<CommitFileChanges> getFiles() {
        return files;
    }

    @Override public void onWorkOffline() {
        sendToView(BaseMvp.FAView::hideProgress);
    }

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

    @Override public void onItemLongClick(int position, View v, CommitFileChanges item) {
        v.getContext().startActivity(CommitPagerActivity.createIntent(v.getContext(), repoId, login,
                Uri.parse(item.getCommitFileModel().getContentsUrl()).getQueryParameter("ref")));
    }
}
