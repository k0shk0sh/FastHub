package com.fastaccess.ui.modules.repos.code.files;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.annimon.stream.Objects;
import com.annimon.stream.Stream;
import com.fastaccess.R;
import com.fastaccess.data.dao.RepoFilesModel;
import com.fastaccess.data.dao.types.FilesType;
import com.fastaccess.helper.Logger;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import rx.Observable;

/**
 * Created by Kosh on 15 Feb 2017, 10:10 PM
 */

class RepoFilesPresenter extends BasePresenter<RepoFilesMvp.View> implements RepoFilesMvp.Presenter {
    private ArrayList<RepoFilesModel> files = new ArrayList<>();
    private HashMap<String, ArrayList<RepoFilesModel>> cachedFiles = new LinkedHashMap<>();
    private String repoId;
    private String login;
    private String path;

    @Override public void onItemClick(int position, View v, RepoFilesModel item) {
        if (getView() == null) return;
        if (v.getId() != R.id.menu) {
            getView().onItemClicked(item);
        } else {
            getView().onMenuClicked(item, v);
        }
    }

    @Override public void onItemLongClick(int position, View v, RepoFilesModel item) {
        onItemClick(position, v, item);
    }

    @Override public <T> T onError(@NonNull Throwable throwable, @NonNull Observable<T> observable) {
        onWorkOffline();
        return super.onError(throwable, observable);
    }

    @NonNull @Override public ArrayList<RepoFilesModel> getFiles() {
        return files;
    }

    @Override public void onWorkOffline() {
        if ((repoId == null || login == null) || !files.isEmpty()) return;
        manageSubscription(RepoFilesModel.getFiles(login, repoId).subscribe(
                models -> {
                    files.addAll(models);
                    sendToView(RepoFilesMvp.View::onNotifyAdapter);
                }
        ));
    }

    @Override public void onCallApi() {
        if (repoId == null || login == null) return;
        makeRestCall(RestProvider.getRepoService().getRepoFiles(login, repoId, path),
                response -> {
                    files.clear();
                    manageSubscription(RepoFilesModel.save(response.getItems(), login, repoId).subscribe());
                    ArrayList<RepoFilesModel> repoFilesModels = Stream.of(response.getItems())
                            .sortBy(model -> model.getType() == FilesType.file)
                            .collect(com.annimon.stream.Collectors.toCollection(ArrayList::new));
                    cachedFiles.put(path, repoFilesModels);
                    files.addAll(repoFilesModels);
                    sendToView(RepoFilesMvp.View::onNotifyAdapter);
                });

    }

    @Override public void onInitDataAndRequest(@NonNull String login, @NonNull String repoId, @Nullable String path) {
        ArrayList<RepoFilesModel> cachedFiles = getCachedFiles(Objects.toString(path, ""));
        if (cachedFiles != null && !cachedFiles.isEmpty()) {
            Logger.e(files.size());
            files.clear();
            files.addAll(cachedFiles);
            Logger.e(files.size(), cachedFiles.size());
            sendToView(RepoFilesMvp.View::onNotifyAdapter);
        } else {
            this.login = login;
            this.repoId = repoId;
            if (!Objects.toString(path, "").equalsIgnoreCase(this.path)) {
                this.path = Objects.toString(path, "");
                onCallApi();
            }
        }
    }

    @Nullable @Override public ArrayList<RepoFilesModel> getCachedFiles(@NonNull String url) {
        return cachedFiles.get(url);
    }

    @Override public void cacheFiles(@NonNull String url, @NonNull ArrayList<RepoFilesModel> files) {
        Logger.e(url, files);
        cachedFiles.put(url, files);
    }
}
