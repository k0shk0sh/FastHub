package com.fastaccess.ui.modules.repos.code.files.paths;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.annimon.stream.Collectors;
import com.annimon.stream.Objects;
import com.annimon.stream.Stream;
import com.fastaccess.data.dao.BranchesModel;
import com.fastaccess.data.dao.model.RepoFile;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Created by Kosh on 15 Feb 2017, 10:10 PM
 */

class RepoFilePathPresenter extends BasePresenter<RepoFilePathMvp.View> implements RepoFilePathMvp.Presenter {
    private String repoId;
    private String login;
    private String path;
    private String defaultBranch;
    private ArrayList<RepoFile> paths = new ArrayList<>();
    private ArrayList<BranchesModel> branches = new ArrayList<>();

    @Override public void onItemClick(int position, View v, RepoFile item) {
        if (!item.getPath().equalsIgnoreCase(path)) if (getView() != null) getView().onItemClicked(item, position);
    }

    @Override public void onItemLongClick(int position, View v, RepoFile item) {
        onItemClick(position, v, item);
    }

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
                    if (uri.getPathSegments() != null && !uri.getPathSegments().isEmpty()) {
                        for (String name : uri.getPathSegments()) {
                            RepoFile file = new RepoFile();
                            file.setPath(name);
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
            if (branches.isEmpty()) {
                Observable<List<BranchesModel>> observable = RxHelper.getObserver(Observable.zip(
                        RestProvider.getRepoService().getBranches(login, repoId),
                        RestProvider.getRepoService().getTags(login, repoId),
                        (branchPageable, tags) -> {
                            ArrayList<BranchesModel> branchesModels = new ArrayList<>();
                            if (branchPageable.getItems() != null) {
                                branchesModels.addAll(Stream.of(branchPageable.getItems())
                                        .map(branchesModel -> {
                                            branchesModel.setTag(false);
                                            return branchesModel;
                                        }).collect(Collectors.toList()));
                            }
                            if (tags != null) {
                                branchesModels.addAll(Stream.of(tags.getItems())
                                        .map(branchesModel -> {
                                            branchesModel.setTag(true);
                                            return branchesModel;
                                        }).collect(Collectors.toList()));

                            }
                            return branchesModels;
                        }));
                manageSubscription(observable
                        .doOnSubscribe(() -> sendToView(view -> view.showProgress(0)))
                        .doOnNext(branchesModels -> {
                            branches.clear();
                            branches.addAll(branchesModels);
                            sendToView(view -> view.setBranchesData(branches, true));
                        })
                        .onErrorReturn(throwable -> {
                            sendToView(view -> view.setBranchesData(branches, true));
                            return null;
                        })
                        .subscribe());
            }
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

    @NonNull @Override public ArrayList<BranchesModel> getBranches() {
        return branches;
    }

    @Override public String getDefaultBranch() {
        return defaultBranch;
    }
}
