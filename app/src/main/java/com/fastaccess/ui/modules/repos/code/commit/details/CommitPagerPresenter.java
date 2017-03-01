package com.fastaccess.ui.modules.repos.code.commit.details;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.CommitModel;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import rx.Observable;

/**
 * Created by Kosh on 10 Dec 2016, 9:23 AM
 */

class CommitPagerPresenter extends BasePresenter<CommitPagerMvp.View> implements CommitPagerMvp.Presenter {
    private CommitModel commitModel;
    private String sha;
    private String login;
    private String repoId;

    @Nullable @Override public CommitModel getCommit() {
        return commitModel;
    }

    @Override public <T> T onError(@NonNull Throwable throwable, @NonNull Observable<T> observable) {
        onWorkOffline(sha, repoId, login);
        return super.onError(throwable, observable);
    }

    @Override public void onActivityCreated(@Nullable Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            sha = intent.getExtras().getString(BundleConstant.ID);
            login = intent.getExtras().getString(BundleConstant.EXTRA);
            repoId = intent.getExtras().getString(BundleConstant.EXTRA_TWO);
            if (commitModel != null) {
                sendToView(CommitPagerMvp.View::onSetup);
                return;
            } else if (!InputHelper.isEmpty(sha) && !InputHelper.isEmpty(login) && !InputHelper.isEmpty(repoId)) {
                makeRestCall(RestProvider.getRepoService().getCommit(login, repoId, sha),
                        commit -> {
                            commitModel = commit;
                            commitModel.setRepoId(repoId);
                            commitModel.setLogin(login);
                            sendToView(CommitPagerMvp.View::onSetup);
                            manageSubscription(commitModel.save().subscribe());
                        });
                return;
            }
        }
        sendToView(CommitPagerMvp.View::onSetup);
    }

    @Override public void onWorkOffline(@NonNull String sha, @NonNull String repoId, @NonNull String login) {
        manageSubscription(CommitModel.getCommit(sha, repoId, login)
                .subscribe(commit -> {
                    commitModel = commit;
                    sendToView(CommitPagerMvp.View::onSetup);
                }));
    }

    @Override public String getLogin() {
        return login;
    }

    @Override public String getRepoId() {
        return repoId;
    }

}
