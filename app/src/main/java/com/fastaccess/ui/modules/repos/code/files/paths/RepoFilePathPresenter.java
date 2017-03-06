package com.fastaccess.ui.modules.repos.code.files.paths;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.annimon.stream.Objects;
import com.fastaccess.data.dao.BranchesModel;
import com.fastaccess.data.dao.RepoFilesModel;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

/**
 * Created by Kosh on 15 Feb 2017, 10:10 PM
 */

class RepoFilePathPresenter extends BasePresenter<RepoFilePathMvp.View> implements RepoFilePathMvp.Presenter {
    private String repoId;
    private String login;
    private String path;
    private String defaultBranch;
    private ArrayList<RepoFilesModel> paths = new ArrayList<>();
    private ArrayList<BranchesModel> branches = new ArrayList<>();

    @Override public void onItemClick(int position, View v, RepoFilesModel item) {
        if (!item.getPath().equalsIgnoreCase(path)) if (getView() != null) getView().onItemClicked(item, position);
    }

    @Override public void onItemLongClick(int position, View v, RepoFilesModel item) {
        onItemClick(position, v, item);
    }

    @Override public void onFragmentCreated(@Nullable Bundle bundle) {
        if (bundle != null) {
            repoId = bundle.getString(BundleConstant.ID);
            login = bundle.getString(BundleConstant.EXTRA);
            path = Objects.toString(bundle.getString(BundleConstant.EXTRA_TWO), "");
            defaultBranch = Objects.toString(bundle.getString(BundleConstant.EXTRA_THREE), "master");
            if (InputHelper.isEmpty(repoId) || InputHelper.isEmpty(login)) {
                throw new NullPointerException(String.format("error, repoId(%s) or login(%s) is null", repoId, login));
            }
            sendToView(RepoFilePathMvp.View::onSendData);
            if (branches.isEmpty()) {
                makeRestCall(RestProvider.getRepoService().getBranches(login, repoId),
                        response -> {
                            if (response != null && response.getItems() != null) {
                                branches.clear();
                                branches.addAll(response.getItems());
                                sendToView(view -> {
                                    view.setBranchesData(branches, true);
                                    view.hideProgress();
                                });
                            }
                        });
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

    @NonNull @Override public ArrayList<RepoFilesModel> getPaths() {
        return paths;
    }

    @NonNull @Override public ArrayList<BranchesModel> getBranches() {
        return branches;
    }

    @Override public String getDefaultBranch() {
        return defaultBranch;
    }
}
