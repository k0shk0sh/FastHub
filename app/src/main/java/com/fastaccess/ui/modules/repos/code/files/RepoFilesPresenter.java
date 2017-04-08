package com.fastaccess.ui.modules.repos.code.files;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.fastaccess.R;
import com.fastaccess.data.dao.RepoPathsManager;
import com.fastaccess.data.dao.model.RepoFile;
import com.fastaccess.data.dao.types.FilesType;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

/**
 * Created by Kosh on 15 Feb 2017, 10:10 PM
 */

class RepoFilesPresenter extends BasePresenter<RepoFilesMvp.View> implements RepoFilesMvp.Presenter {
    private ArrayList<RepoFile> files = new ArrayList<>();
    private RepoPathsManager pathsModel = new RepoPathsManager();
    private String repoId;
    private String login;
    private String path;
    private String ref;

    @Override public void onItemClick(int position, View v, RepoFile item) {
        if (getView() == null) return;
        if (v.getId() != R.id.menu) {
            getView().onItemClicked(item);
        } else {
            getView().onMenuClicked(item, v);
        }
    }

    @Override public void onItemLongClick(int position, View v, RepoFile item) {
        onItemClick(position, v, item);
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
        manageSubscription(RxHelper.getObserver(RepoFile.getFiles(login, repoId)).subscribe(
                models -> {
                    files.addAll(models);
                    sendToView(RepoFilesMvp.View::onNotifyAdapter);
                }
        ));
    }

    @Override public void onCallApi() {
        if (repoId == null || login == null) return;
        makeRestCall(RestProvider.getRepoService().getRepoFiles(login, repoId, path, ref),
                response -> {
                    files.clear();
                    if (response != null && response.getItems() != null) {
                        ArrayList<RepoFile> repoFilesModels = Stream.of(response.getItems())
                                .sortBy(model -> model.getType() == FilesType.file)
                                .collect(Collectors.toCollection(ArrayList::new));
                        manageSubscription(RepoFile.save(repoFilesModels, login, repoId).subscribe());
                        pathsModel.setFiles(ref, path, repoFilesModels);
                        files.addAll(repoFilesModels);
                    }
                    sendToView(RepoFilesMvp.View::onNotifyAdapter);
                });

    }

    @Override public void onInitDataAndRequest(@NonNull String login, @NonNull String repoId, @NonNull String path,
                                               @NonNull String ref, boolean clear) {
        if (clear) pathsModel.clear();
        this.login = login;
        this.repoId = repoId;
        this.ref = ref;
        this.path = path;
        ArrayList<RepoFile> cachedFiles = getCachedFiles(path, ref);
        if (cachedFiles != null && !cachedFiles.isEmpty()) {
            files.clear();
            files.addAll(cachedFiles);
            sendToView(RepoFilesMvp.View::onNotifyAdapter);
        } else {
            onCallApi();
        }
    }

    @Nullable @Override public ArrayList<RepoFile> getCachedFiles(@NonNull String url, @NonNull String ref) {
        return pathsModel.getPaths(url, ref);
    }
}
