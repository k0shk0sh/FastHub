package com.fastaccess.ui.modules.repos.code.files;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.CommitRequestModel;
import com.fastaccess.data.dao.RepoPathsManager;
import com.fastaccess.data.dao.model.RepoFile;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.repos.code.commit.history.FileCommitHistoryActivity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Kosh on 15 Feb 2017, 10:10 PM
 */

class RepoFilesPresenter extends BasePresenter<RepoFilesMvp.View> implements RepoFilesMvp.Presenter {
    private ArrayList<RepoFile> files = new ArrayList<>();
    private RepoPathsManager pathsModel = new RepoPathsManager();
    @com.evernote.android.state.State String repoId;
    @com.evernote.android.state.State String login;
    @com.evernote.android.state.State String path;
    @com.evernote.android.state.State String ref;

    @Override public void onItemClick(int position, View v, RepoFile item) {
        if (getView() == null) return;
        if (v.getId() != R.id.menu) {
            getView().onItemClicked(item);
        } else {
            getView().onMenuClicked(position, item, v);
        }
    }

    @Override public void onItemLongClick(int position, View v, RepoFile item) {
        FileCommitHistoryActivity.Companion.startActivity(v.getContext(), login, repoId, ref, item.getPath(), isEnterprise());
    }

    @Override public void onError(@NonNull Throwable throwable) {
        onWorkOffline();
        super.onError(throwable);
    }

    @NonNull @Override public ArrayList<RepoFile> getFiles() {
        return files;
    }

    @Override public void onWorkOffline() {
        if ((repoId == null || login == null) || !files.isEmpty()) return;
        manageDisposable(RxHelper.getObservable(RepoFile.getFiles(login, repoId).toObservable())
                .flatMap(response -> {
                    if (response != null) {
                        return Observable.fromIterable(response)
                                .filter(repoFile -> repoFile != null && repoFile.getType() != null)
                                .sorted((repoFile, repoFile2) -> repoFile2.getType().compareTo(repoFile.getType()));
                    }
                    return Observable.empty();
                })
                .toList()
                .subscribe(models -> {
                    files.addAll(models);
                    sendToView(RepoFilesMvp.View::onNotifyAdapter);
                }));
    }

    @Override public void onCallApi(@Nullable RepoFile toAppend) {
        if (repoId == null || login == null) return;
        makeRestCall(RestProvider.getRepoService(isEnterprise()).getRepoFiles(login, repoId, path, ref)
                .flatMap(response -> {
                    if (response != null && response.getItems() != null) {
                        return Observable.fromIterable(response.getItems())
                                .filter(repoFile -> repoFile.getType() != null)
                                .sorted((repoFile, repoFile2) -> repoFile2.getType().compareTo(repoFile.getType()));
                    }
                    return Observable.empty();
                })
                .toList().toObservable(), response -> {
            files.clear();
            if (response != null) {
                manageObservable(RepoFile.save(response, login, repoId));
                pathsModel.setFiles(ref, path, response);
                files.addAll(response);
            }
            sendToView(view -> {
                view.onNotifyAdapter();
                view.onUpdateTab(toAppend);
            });
        });

    }

    @Override public void onInitDataAndRequest(@NonNull String login, @NonNull String repoId, @NonNull String path,
                                               @NonNull String ref, boolean clear, @Nullable RepoFile toAppend) {
        if (clear) pathsModel.clear();
        this.login = login;
        this.repoId = repoId;
        this.ref = ref;
        this.path = path;
        List<RepoFile> cachedFiles = getCachedFiles(path, ref);
        if (cachedFiles != null && !cachedFiles.isEmpty()) {
            files.clear();
            files.addAll(cachedFiles);
            sendToView(view -> {
                view.onNotifyAdapter();
                view.onUpdateTab(toAppend);
            });
        } else {
            onCallApi(toAppend);
        }
    }

    @Nullable @Override public List<RepoFile> getCachedFiles(@NonNull String url, @NonNull String ref) {
        return pathsModel.getPaths(url, ref);
    }

    @Override public void onDeleteFile(@NonNull String message, @NonNull RepoFile item) {
        CommitRequestModel body = new CommitRequestModel(message, null, item.getSha());
        makeRestCall(RestProvider.getContentService(isEnterprise())
                        .deleteFile(login, repoId, item.getPath(), ref, body),
                gitCommitModel -> sendToView(SwipeRefreshLayout.OnRefreshListener::onRefresh));
    }
}
