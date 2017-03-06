package com.fastaccess.ui.modules.repos;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fastaccess.R;
import com.fastaccess.data.dao.LoginModel;
import com.fastaccess.data.dao.RepoModel;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.repos.code.RepoCodePagerView;
import com.fastaccess.ui.modules.repos.issues.RepoIssuesPagerView;
import com.fastaccess.ui.modules.repos.pull_requests.RepoPullRequestPagerView;

import retrofit2.Response;
import rx.Observable;

import static com.fastaccess.helper.ActivityHelper.getVisibleFragment;

/**
 * Created by Kosh on 09 Dec 2016, 4:17 PM
 */

class RepoPagerPresenter extends BasePresenter<RepoPagerMvp.View> implements RepoPagerMvp.Presenter {
    private boolean isWatched;
    private boolean isStarred;
    private boolean isForked;
    private String login;
    private String repoId;
    private RepoModel repo;

    @Override public <T> T onError(@NonNull Throwable throwable, @NonNull Observable<T> observable) {
        onWorkOffline();
        return super.onError(throwable, observable);
    }

    @Override public void onActivityCreated(@Nullable Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            repoId = bundle.getString(BundleConstant.ID);
            login = bundle.getString(BundleConstant.EXTRA_TWO);
            if (!InputHelper.isEmpty(login()) && !InputHelper.isEmpty(repoId())) {
                makeRestCall(RestProvider.getRepoService().getRepo(login(), repoId()),
                        repoModel -> {
                            this.repo = repoModel;
                            manageSubscription(this.repo.persist().observe().subscribe());
                            sendToView(view -> {
                                view.onInitRepo();
                                view.onNavigationChanged(RepoPagerMvp.CODE);
                            });
                            onCheckStarring();
                            onCheckWatching();
                        });
                return;
            }
        }
        sendToView(RepoPagerMvp.View::onFinishActivity);
    }

    @NonNull @Override public String repoId() {
        return repoId;
    }

    @NonNull @Override public String login() {
        return login;
    }

    @Nullable @Override public RepoModel getRepo() {
        return repo;
    }

    @Override public boolean isWatched() {
        return isWatched;
    }

    @Override public boolean isStarred() {
        return isStarred;
    }

    @Override public boolean isForked() {
        return isForked;
    }

    @Override public boolean isRepoOwner() {
        return (getRepo() != null && getRepo().getOwner() != null) && getRepo().getOwner().getLogin().equals(LoginModel.getUser().getLogin());
    }

    @Override public void onWatch() {
        if (getRepo() == null) return;
        String login = getRepo().getOwner().getLogin();
        String name = getRepo().getName();
        Observable<Response<Boolean>> observable = RxHelper
                .getObserver(!isWatched ? RestProvider.getRepoService().watchRepo(login, name)
                                        : RestProvider.getRepoService().unwatchRepo(login, name));
        manageSubscription(observable
                .doOnSubscribe(() -> sendToView(view -> view.onEnableDisableWatch(false)))
                .doOnNext(booleanResponse -> {
                    if (!isWatched) {
                        isWatched = booleanResponse.code() == 204;
                    } else {
                        isWatched = booleanResponse.code() != 204;
                    }
                    sendToView(view -> {
                        view.onRepoWatched(isWatched);
                        view.onChangeWatchedCount(isWatched);
                    });
                })
                .onErrorReturn(throwable -> {
                    sendToView(view -> view.onEnableDisableWatch(true));
                    return null;
                })
                .subscribe());
    }

    @Override public void onStar() {
        if (getRepo() == null) return;
        String login = getRepo().getOwner().getLogin();
        String name = getRepo().getName();
        Observable<Response<Boolean>> observable = RxHelper
                .getObserver(!isStarred ? RestProvider.getRepoService().starRepo(login, name)
                                        : RestProvider.getRepoService().unstarRepo(login, name));
        manageSubscription(observable
                .doOnSubscribe(() -> sendToView(view -> view.onEnableDisableStar(false)))
                .doOnNext(booleanResponse -> {
                    if (!isStarred) {
                        isStarred = booleanResponse.code() == 204;
                    } else {
                        isStarred = booleanResponse.code() != 204;
                    }
                    sendToView(view -> {
                        view.onRepoStarred(isStarred);
                        view.onChangeStarCount(isStarred);
                    });
                })
                .onErrorReturn(throwable -> {
                    sendToView(view -> view.onEnableDisableStar(true));
                    return null;
                })
                .subscribe());
    }

    @Override public void onFork() {
        if (!isForked && getRepo() != null) {
            String login = login();
            String name = repoId();
            manageSubscription(RxHelper.getObserver(RestProvider.getRepoService().forkRepo(login, name))
                    .doOnSubscribe(() -> sendToView(view -> view.onEnableDisableFork(false)))
                    .doOnNext(repoModel -> sendToView(view -> {
                        view.onRepoForked(isForked = repoModel != null);
                        view.onChangeForkCount(isForked);
                    }))
                    .onErrorReturn(throwable -> {
                        sendToView(view -> view.onEnableDisableFork(true));
                        return null;
                    })
                    .subscribe());
        }
    }

    @Override public void onCheckWatching() {
        if (getRepo() != null) {
            String login = login();
            String name = repoId();
            manageSubscription(RxHelper.getObserver(RestProvider.getRepoService().isWatchingRepo(login, name))
                    .doOnSubscribe(() -> sendToView(view -> view.onEnableDisableWatch(false)))
                    .doOnNext(subscriptionModel -> sendToView(view -> view.onRepoWatched(isWatched = subscriptionModel.code() == 204)))
                    .onErrorReturn(throwable -> {
                        isWatched = false;
                        sendToView(view -> view.onRepoWatched(isWatched));
                        return null;
                    })
                    .subscribe());
        }
    }

    @Override public void onCheckStarring() {
        if (getRepo() != null) {
            String login = login();
            String name = repoId();
            manageSubscription(RxHelper.getObserver(RestProvider.getRepoService().checkStarring(login, name))
                    .doOnSubscribe(() -> sendToView(view -> view.onEnableDisableStar(false)))
                    .doOnNext(response -> sendToView(view -> view.onRepoStarred(isStarred = response.code() == 204)))
                    .onErrorReturn(throwable -> {
                        isStarred = false;
                        sendToView(view -> view.onRepoStarred(isStarred));
                        return null;
                    })
                    .subscribe());
        }
    }

    @Override public void onWorkOffline() {
        if (!InputHelper.isEmpty(login()) && !InputHelper.isEmpty(repoId())) {
            manageSubscription(RxHelper.getObserver(RepoModel.getRepo(repoId))
                    .subscribe(repoModel -> {
                        repo = repoModel;
                        sendToView(view -> {
                            view.onInitRepo();
                            view.onNavigationChanged(RepoPagerMvp.CODE);
                        });
                    }));
        } else {
            sendToView(RepoPagerMvp.View::onFinishActivity);
        }
    }

    @Override public void onModuleChanged(@NonNull FragmentManager fragmentManager, @RepoPagerMvp.RepoNavigationType int type) {
        Fragment currentVisible = getVisibleFragment(fragmentManager);
        RepoCodePagerView codePagerView = (RepoCodePagerView) AppHelper.getFragmentByTag(fragmentManager, RepoCodePagerView.TAG);
        RepoIssuesPagerView repoIssuesPagerView = (RepoIssuesPagerView) AppHelper.getFragmentByTag(fragmentManager, RepoIssuesPagerView.TAG);
        RepoPullRequestPagerView pullRequestPagerView = (RepoPullRequestPagerView) AppHelper.getFragmentByTag(fragmentManager,
                RepoPullRequestPagerView.TAG);
        if (getRepo() == null) {
            sendToView(RepoPagerMvp.View::onFinishActivity);
            return;
        }
        if (currentVisible == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.container, RepoCodePagerView.newInstance(repoId(), login(), getRepo().getUrl(),
                            getRepo().getDefaultBranch()), RepoCodePagerView.TAG)
                    .commit();
            return;
        }
        switch (type) {
            case RepoPagerMvp.CODE:
                if (codePagerView == null) {
                    onAddAndHide(fragmentManager, RepoCodePagerView.newInstance(repoId(), login(),
                            getRepo().getHtmlUrl(), getRepo().getDefaultBranch()), currentVisible);
                } else {
                    onShowHideFragment(fragmentManager, codePagerView, currentVisible);
                }
                break;
            case RepoPagerMvp.ISSUES:
                if ((getRepo() != null && !getRepo().isHasIssues())) return;
                if (repoIssuesPagerView == null) {
                    onAddAndHide(fragmentManager, RepoIssuesPagerView.newInstance(repoId(), login()), currentVisible);
                } else {
                    onShowHideFragment(fragmentManager, repoIssuesPagerView, currentVisible);
                }
                break;
            case RepoPagerMvp.PULL_REQUEST:
                if (pullRequestPagerView == null) {
                    onAddAndHide(fragmentManager, RepoPullRequestPagerView.newInstance(repoId(), login()), currentVisible);
                } else {
                    onShowHideFragment(fragmentManager, pullRequestPagerView, currentVisible);
                }
                break;
        }
    }

    @Override public void onShowHideFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment toShow, @NonNull Fragment toHide) {
        toHide.onHiddenChanged(true);
        fragmentManager
                .beginTransaction()
                .hide(toHide)
                .show(toShow)
                .commit();
        toShow.onHiddenChanged(false);
    }

    @Override public void onAddAndHide(@NonNull FragmentManager fragmentManager, @NonNull Fragment toAdd, @NonNull Fragment toHide) {
        toHide.onHiddenChanged(true);
        fragmentManager
                .beginTransaction()
                .hide(toHide)
                .add(R.id.container, toAdd, toAdd.getClass().getSimpleName())
                .commit();
        toAdd.onHiddenChanged(false);
    }

    @Override public void onDeleteRepo() {
        if (isRepoOwner()) {
            makeRestCall(RestProvider.getRepoService().deleteRepo(login, repoId),
                    booleanResponse -> {
                        if (booleanResponse.code() == 204) {
                            if (repo != null) repo.delete().execute();
                            repo = null;
                            sendToView(RepoPagerMvp.View::onInitRepo);
                        }
                    });
        }
    }

    @Override public void onMenuItemSelect(@IdRes int id, int position, boolean fromUser) {
        if (id == R.id.issues && (getRepo() != null && !getRepo().isHasIssues())) {
            return;
        }
        if (getView() != null && isViewAttached()) {
            getView().onNavigationChanged(position);
        }
    }

    @Override public void onMenuItemReselect(@IdRes int id, int position, boolean fromUser) {}
}
