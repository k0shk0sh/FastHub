package com.fastaccess.ui.modules.repos.code.commit.details;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.CommentRequestModel;
import com.fastaccess.data.dao.MarkdownModel;
import com.fastaccess.data.dao.model.Commit;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

import io.reactivex.Observable;

/**
 * Created by Kosh on 10 Dec 2016, 9:23 AM
 */

class CommitPagerPresenter extends BasePresenter<CommitPagerMvp.View> implements CommitPagerMvp.Presenter {
    @com.evernote.android.state.State Commit commitModel;
    @com.evernote.android.state.State String sha;
    @com.evernote.android.state.State String login;
    @com.evernote.android.state.State String repoId;
    @com.evernote.android.state.State boolean showToRepoBtn;
    @com.evernote.android.state.State ArrayList<CommentRequestModel> reviewComments = new ArrayList<>();

    @Nullable @Override public Commit getCommit() {
        return commitModel;
    }

    @Override public void onError(@NonNull Throwable throwable) {
        if (RestProvider.getErrorCode(throwable) == 404) {
            sendToView(CommitPagerMvp.View::onFinishActivity);
        } else {
            onWorkOffline(sha, repoId, login);
        }
        super.onError(throwable);
    }

    @Override public void onActivityCreated(@Nullable Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            sha = intent.getExtras().getString(BundleConstant.ID);
            login = intent.getExtras().getString(BundleConstant.EXTRA);
            repoId = intent.getExtras().getString(BundleConstant.EXTRA_TWO);
            showToRepoBtn = intent.getExtras().getBoolean(BundleConstant.EXTRA_THREE);
            if (commitModel != null) {
                sendToView(CommitPagerMvp.View::onSetup);
                return;
            } else if (!InputHelper.isEmpty(sha) && !InputHelper.isEmpty(login) && !InputHelper.isEmpty(repoId)) {
                makeRestCall(RestProvider.getRepoService(isEnterprise())
                        .getCommit(login, repoId, sha)
                        .flatMap(commit -> {
                            if (commit.getGitCommit() != null && commit.getGitCommit().getMessage() != null) {
                                MarkdownModel markdownModel = new MarkdownModel();
                                markdownModel.setContext(login + "/" + repoId);
                                markdownModel.setText(commit.getGitCommit().getMessage());
                                return RestProvider.getRepoService(isEnterprise()).convertReadmeToHtml(markdownModel);
                            }
                            return Observable.just(commit);
                        }, (commit, u) -> {
                            if (!InputHelper.isEmpty(u) && u instanceof String) {
                                commit.getGitCommit().setMessage(u.toString());
                            }
                            return commit;
                        }), commit -> {
                    commitModel = commit;
                    commitModel.setRepoId(repoId);
                    commitModel.setLogin(login);
                    sendToView(CommitPagerMvp.View::onSetup);
                    manageObservable(commitModel.save(commitModel).toObservable());
                });
                return;
            }
        }
        sendToView(CommitPagerMvp.View::onSetup);
    }

    @Override public void onWorkOffline(@NonNull String sha, @NonNull String repoId, @NonNull String login) {
        manageDisposable(RxHelper.getObservable(Commit.getCommit(sha, repoId, login))
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

    @Override public boolean showToRepoBtn() {
        return showToRepoBtn;
    }

}
