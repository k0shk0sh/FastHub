package com.fastaccess.ui.modules.repos.code.files.paths;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.annimon.stream.Objects;
import com.fastaccess.data.dao.model.RepoFile;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kosh on 15 Feb 2017, 10:10 PM
 */

class RepoFilePathPresenter extends BasePresenter<RepoFilePathMvp.View> implements RepoFilePathMvp.Presenter {
    @com.evernote.android.state.State String repoId;
    @com.evernote.android.state.State String login;
    @com.evernote.android.state.State String path;
    @com.evernote.android.state.State String defaultBranch;
    private ArrayList<RepoFile> paths = new ArrayList<>();

    @Override public void onItemClick(int position, View v, RepoFile item) {
        if (!item.getPath().equalsIgnoreCase(path)) if (getView() != null) getView().onItemClicked(item, position);
    }

    @Override public void onItemLongClick(int position, View v, RepoFile item) {}

    @Override public void onFragmentCreated(@Nullable Bundle bundle) {
        if (bundle != null) {
            repoId = bundle.getString(BundleConstant.ID);
            login = bundle.getString(BundleConstant.EXTRA);
            path = Objects.toString(bundle.getString(BundleConstant.EXTRA_TWO), "");
            defaultBranch = Objects.toString(bundle.getString(BundleConstant.EXTRA_THREE), "master");
            boolean forceAppend = bundle.getBoolean(BundleConstant.EXTRA_FOUR);
            if (InputHelper.isEmpty(repoId) || InputHelper.isEmpty(login)) {
                throw new NullPointerException(String.format("error, repoId(%s) or login(%s) is null", repoId, login));
            }
            if (forceAppend && paths.isEmpty()) {
                List<RepoFile> repoFiles = new ArrayList<>();
                if (!InputHelper.isEmpty(path)) {
                    Uri uri = Uri.parse(path);
                    StringBuilder builder = new StringBuilder();
                    if (uri.getPathSegments() != null && !uri.getPathSegments().isEmpty()) {
                        List<String> pathSegments = uri.getPathSegments();
                        for (int i = 0; i < pathSegments.size(); i++) {
                            String name = pathSegments.get(i);
                            RepoFile file = new RepoFile();
                            if (i == 0) {
                                builder.append(name);
                            } else {
                                builder.append("/").append(name);
                            }
                            file.setPath(builder.toString());
                            file.setName(name);
                            repoFiles.add(file);
                        }
                    }
                    if (!repoFiles.isEmpty()) {
                        sendToView(view -> view.onNotifyAdapter(repoFiles, 1));
                    }
                }
            }
            sendToView(RepoFilePathMvp.View::onSendData);
        } else {
            throw new NullPointerException("Bundle is null");
        }
    }

    @NonNull @Override public String getRepoId() {
        return repoId;
    }

    @NonNull @Override public String getLogin() {
        return login;
    }

    @Nullable @Override public String getPath() {
        return path;
    }

    @NonNull @Override public ArrayList<RepoFile> getPaths() {
        return paths;
    }

    @Override public String getDefaultBranch() {
        return defaultBranch;
    }
}
