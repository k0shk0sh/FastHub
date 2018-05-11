package com.fastaccess.ui.modules.repos;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fastaccess.R;
import com.fastaccess.data.dao.model.AbstractPinnedRepos;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.data.dao.model.PinnedRepos;
import com.fastaccess.data.dao.model.Repo;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.repos.code.RepoCodePagerFragment;
import com.fastaccess.ui.modules.repos.issues.RepoIssuesPagerFragment;
import com.fastaccess.ui.modules.repos.projects.RepoProjectsFragmentPager;
import com.fastaccess.ui.modules.repos.pull_requests.RepoPullRequestPagerFragment;

import io.reactivex.Observable;

import static com.fastaccess.helper.ActivityHelper.getVisibleFragment;

/**
 * Created by Kosh on 09 Dec 2016, 4:17 PM
 */

class RepoPagerPresenter extends BasePresenter<RepoPagerMvp.View> implements RepoPagerMvp.Presenter {
    @com.evernote.android.state.State boolean isWatched;
    @com.evernote.android.state.State boolean isStarred;
    @com.evernote.android.state.State boolean isForked;
    @com.evernote.android.state.State String login;
    @com.evernote.android.state.State String repoId;
    @com.evernote.android.state.State Repo repo;
    @com.evernote.android.state.State int navTyp;
    @com.evernote.android.state.State boolean isCollaborator;

    private void callApi(int navTyp) {
        if (InputHelper.isEmpty(login) || InputHelper.isEmpty(repoId)) return;
        makeRestCall(Observable.zip(RestProvider.getRepoService(isEnterprise()).getRepo(login(), repoId()),
                RestProvider.getRepoService(isEnterprise()).isCollaborator(login, repoId, Login.getUser().getLogin()),
                (repo1, booleanResponse) -> {
                    isCollaborator = booleanResponse.code() == 204;
                    return repo1;
                }),
                repoModel -> {
                    this.repo = repoModel;
                    manageDisposable(this.repo.save(repo));
                    updatePinned(repoModel);
                    sendToView(view -> {
                        view.onInitRepo();
                        view.onNavigationChanged(navTyp);
                    });
                    onCheckStarring();
                    onCheckWatching();
                });
    }

    @Override public void onError(@NonNull Throwable throwable) {
        int code = RestProvider.getErrorCode(throwable);
        if (code == 404) {
            sendToView(BaseMvp.FAView::onOpenUrlInBrowser);
        } else {
            onWorkOffline();
        }
        super.onError(throwable);
    }

    @Override public void onUpdatePinnedEntry(@NonNull String repoId, @NonNull String login) {
        manageDisposable(PinnedRepos.updateEntry(login + "/" + repoId));
    }

    @Override public void onActivityCreate(@NonNull String repoId, @NonNull String login, int navTyp) {
        this.login = login;
        this.repoId = repoId;
        this.navTyp = navTyp;
        if (getRepo() == null || !isApiCalled()) {
            callApi(navTyp);
        } else {
            sendToView(RepoPagerMvp.View::onInitRepo);
        }
    }

    @NonNull @Override public String repoId() {
        return repoId;
    }

    @NonNull @Override public String login() {
        return login;
    }

    @Nullable @Override public Repo getRepo() {
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
        if (getRepo() != null && getRepo().getOwner() != null) {
            return getRepo().getOwner().getLogin().equals(Login.getUser().getLogin()) || isCollaborator;
        }
        return false;
    }

    @Override public void onWatch() {
        if (getRepo() == null) return;
        isWatched = !isWatched;
        sendToView(view -> {
            view.onRepoWatched(isWatched);
            view.onChangeWatchedCount(isWatched);
        });
    }

    @Override public void onStar() {
        if (getRepo() == null) return;
        isStarred = !isStarred;
        sendToView(view -> {
            view.onRepoStarred(isStarred);
            view.onChangeStarCount(isStarred);
        });
    }

    @Override public void onFork() {
        if (!isForked && getRepo() != null) {
            isForked = true;
            sendToView(view -> {
                view.onRepoForked(isForked);
                view.onChangeForkCount(isForked);
            });
        }
    }

    @Override public void onCheckWatching() {
        if (getRepo() != null) {
            String login = login();
            String name = repoId();
            manageDisposable(RxHelper.getObservable(RestProvider.getRepoService(isEnterprise()).isWatchingRepo(login, name))
                    .doOnSubscribe(disposable -> sendToView(view -> view.onEnableDisableWatch(false)))
                    .doOnNext(subscriptionModel -> sendToView(view -> view.onRepoWatched(isWatched = subscriptionModel.isSubscribed())))
                    .subscribe(o -> {/**/}, throwable -> {
                        isWatched = false;
                        sendToView(view -> view.onRepoWatched(isWatched));
                    }));
        }
    }

    @Override public void onCheckStarring() {
        if (getRepo() != null) {
            String login = login();
            String name = repoId();
            manageDisposable(RxHelper.getObservable(RestProvider.getRepoService(isEnterprise()).checkStarring(login, name))
                    .doOnSubscribe(disposable -> sendToView(view -> view.onEnableDisableStar(false)))
                    .doOnNext(response -> sendToView(view -> view.onRepoStarred(isStarred = response.code() == 204)))
                    .subscribe(booleanResponse -> {/**/}, throwable -> {
                        isStarred = false;
                        sendToView(view -> view.onRepoStarred(isStarred));
                    }));
        }
    }

    @Override public void onWorkOffline() {
        if (!InputHelper.isEmpty(login()) && !InputHelper.isEmpty(repoId())) {
            manageDisposable(RxHelper.getObservable(Repo.getRepo(repoId, login).toObservable())
                    .subscribe(repoModel -> {
                        repo = repoModel;
                        if (repo != null) {
                            sendToView(view -> {
                                view.onInitRepo();
                                view.onNavigationChanged(RepoPagerMvp.CODE);
                            });
                        } else {
                            callApi(navTyp);
                        }
                    }, Throwable::printStackTrace));
        } else {
            sendToView(RepoPagerMvp.View::onFinishActivity);
        }
    }

    @Override public void onModuleChanged(@NonNull FragmentManager fragmentManager, @RepoPagerMvp.RepoNavigationType int type) {
        Fragment currentVisible = getVisibleFragment(fragmentManager);
        RepoCodePagerFragment codePagerView = (RepoCodePagerFragment) AppHelper.getFragmentByTag(fragmentManager, RepoCodePagerFragment.TAG);
        RepoIssuesPagerFragment repoIssuesPagerView = (RepoIssuesPagerFragment)
                AppHelper.getFragmentByTag(fragmentManager, RepoIssuesPagerFragment.TAG);
        RepoPullRequestPagerFragment pullRequestPagerView = (RepoPullRequestPagerFragment)
                AppHelper.getFragmentByTag(fragmentManager, RepoPullRequestPagerFragment.TAG);
        RepoProjectsFragmentPager projectsFragmentPager = (RepoProjectsFragmentPager) AppHelper.getFragmentByTag(fragmentManager,
                RepoProjectsFragmentPager.Companion.getTAG());
        if (getRepo() == null) {
            sendToView(RepoPagerMvp.View::onFinishActivity);
            return;
        }
        if (currentVisible == null) return;
        switch (type) {
            case RepoPagerMvp.PROFILE:
                sendToView(RepoPagerMvp.View::openUserProfile);
            case RepoPagerMvp.CODE:
                if (codePagerView == null) {
                    onAddAndHide(fragmentManager, RepoCodePagerFragment.newInstance(repoId(), login(),
                            getRepo().getHtmlUrl(), getRepo().getUrl(), getRepo().getDefaultBranch()), currentVisible);
                } else {
                    onShowHideFragment(fragmentManager, codePagerView, currentVisible);
                }
                break;
            case RepoPagerMvp.ISSUES:
                if ((!getRepo().isHasIssues())) {
                    sendToView(view -> view.showMessage(R.string.error, R.string.repo_issues_is_disabled));
                    break;
                }
                if (repoIssuesPagerView == null) {
                    onAddAndHide(fragmentManager, RepoIssuesPagerFragment.newInstance(repoId(), login()), currentVisible);
                } else {
                    onShowHideFragment(fragmentManager, repoIssuesPagerView, currentVisible);
                }
                break;
            case RepoPagerMvp.PULL_REQUEST:
                if (pullRequestPagerView == null) {
                    onAddAndHide(fragmentManager, RepoPullRequestPagerFragment.newInstance(repoId(), login()), currentVisible);
                } else {
                    onShowHideFragment(fragmentManager, pullRequestPagerView, currentVisible);
                }
                break;
            case RepoPagerMvp.PROJECTS:
                if (projectsFragmentPager == null) {
                    onAddAndHide(fragmentManager, RepoProjectsFragmentPager.Companion.newInstance(login(), repoId()), currentVisible);
                } else {
                    onShowHideFragment(fragmentManager, projectsFragmentPager, currentVisible);
                }
                break;
        }
    }

    @Override public void onShowHideFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment toShow, @NonNull Fragment toHide) {
        fragmentManager
                .beginTransaction()
                .hide(toHide)
                .show(toShow)
                .commit();
        toHide.onHiddenChanged(true);
        toShow.onHiddenChanged(false);
    }

    @Override public void onAddAndHide(@NonNull FragmentManager fragmentManager, @NonNull Fragment toAdd, @NonNull Fragment toHide) {
        fragmentManager
                .beginTransaction()
                .hide(toHide)
                .add(R.id.container, toAdd, toAdd.getClass().getSimpleName())
                .commit();

        //noinspection ConstantConditions really android?
        if (toHide != null) toHide.onHiddenChanged(true);
        //noinspection ConstantConditions really android?
        if (toAdd != null) toAdd.onHiddenChanged(false);
    }

    @Override public void onDeleteRepo() {
        if (isRepoOwner()) {
            makeRestCall(RestProvider.getRepoService(isEnterprise()).deleteRepo(login, repoId),
                    booleanResponse -> {
                        if (booleanResponse.code() == 204) {
//                            if (repo != null) repo.delete().execute();
                            repo = null;
                            sendToView(RepoPagerMvp.View::onInitRepo);
                        }
                    });
        }
    }

    @Override public void onPinUnpinRepo() {
        if (getRepo() == null) return;
        boolean isPinned = AbstractPinnedRepos.pinUpin(getRepo());
        sendToView(view -> view.onRepoPinned(isPinned));
    }

    @Override public void updatePinned(int forks, int stars, int watching) {
        this.repo.setStargazersCount(stars);
        this.repo.setForksCount(forks);
        this.repo.setSubsCount(watching);
        updatePinned(repo);
    }

    @Override public void onMenuItemSelect(@IdRes int id, int position, boolean fromUser) {
        if (id == R.id.issues && (getRepo() != null && !getRepo().isHasIssues())) {
            sendToView(RepoPagerMvp.View::disableIssueTab);
            return;
        }
        if (getView() != null && isViewAttached() && fromUser) {
            getView().onNavigationChanged(position);
        }
    }

    @Override public void onMenuItemReselect(@IdRes int id, int position, boolean fromUser) {}

    private void updatePinned(Repo repoModel) {
        PinnedRepos pinnedRepos = PinnedRepos.get(repoModel.getFullName());
        if (pinnedRepos != null) {
            pinnedRepos.setPinnedRepo(repoModel);
            manageObservable(PinnedRepos.update(pinnedRepos).toObservable());
        }
    }
}
