package com.fastaccess.ui.modules.repos;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fastaccess.App;
import com.fastaccess.R;
import com.fastaccess.data.dao.model.AbstractPinnedRepos;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.data.dao.model.Repo;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.repos.code.RepoCodePagerFragment;
import com.fastaccess.ui.modules.repos.issues.RepoIssuesPagerFragment;
import com.fastaccess.ui.modules.repos.pull_requests.RepoPullRequestPagerFragment;
import com.fastaccess.ui.modules.user.UserPagerActivity;

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
    private Repo repo;
    private int navTyp;

    private void callApi(int navTyp) {
        if (InputHelper.isEmpty(login) || InputHelper.isEmpty(repoId)) return;
        makeRestCall(RestProvider.getRepoService().getRepo(login(), repoId()),
                repoModel -> {
                    this.repo = repoModel;
                    manageSubscription(this.repo.save(repo).subscribe());
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
            sendToView(RepoPagerMvp.View::onFinishActivity);
        } else {
            onWorkOffline();
        }
        super.onError(throwable);
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
        return (getRepo() != null && getRepo().getOwner() != null) && getRepo().getOwner().getLogin().equals(Login.getUser().getLogin());
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
            manageSubscription(RxHelper.getObserver(RestProvider.getRepoService().isWatchingRepo(login, name))
                    .doOnSubscribe(() -> sendToView(view -> view.onEnableDisableWatch(false)))
                    .doOnNext(subscriptionModel -> sendToView(view -> view.onRepoWatched(isWatched = subscriptionModel.isSubscribed())))
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
            manageSubscription(RxHelper.getObserver(Repo.getRepo(repoId, login))
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
                    }));
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
        if (getRepo() == null) {
            sendToView(RepoPagerMvp.View::onFinishActivity);
            return;
        }
        if (currentVisible == null) return;
        switch (type) {
            case RepoPagerMvp.PROFILE:
                UserPagerActivity.startActivity(App.getInstance().getApplicationContext(), Login.getUser().getLogin());
            case RepoPagerMvp.CODE:
                if (codePagerView == null) {
                    onAddAndHide(fragmentManager, RepoCodePagerFragment.newInstance(repoId(), login(),
                            getRepo().getUrl(), getRepo().getDefaultBranch()), currentVisible);
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
        toHide.onHiddenChanged(true);
        toAdd.onHiddenChanged(false);
    }

    @Override public void onDeleteRepo() {
        if (isRepoOwner()) {
            makeRestCall(RestProvider.getRepoService().deleteRepo(login, repoId),
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

    @Override public void onMenuItemSelect(@IdRes int id, int position, boolean fromUser) {
        if (id == R.id.issues && (getRepo() != null && !getRepo().isHasIssues())) {
            sendToView(view -> view.showMessage(R.string.error, R.string.repo_issues_is_disabled));
            return;
        }
        if (getView() != null && isViewAttached() && fromUser) {
            getView().onNavigationChanged(position);
        }
    }

    @Override public void onMenuItemReselect(@IdRes int id, int position, boolean fromUser) {}
}
