package com.fastaccess.ui.modules.main;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import com.fastaccess.R;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.feeds.FeedsView;
import com.fastaccess.ui.modules.main.issues.pager.MyIssuesPagerView;
import com.fastaccess.ui.modules.main.pullrequests.pager.MyPullsPagerView;

import static com.fastaccess.helper.ActivityHelper.getVisibleFragment;
import static com.fastaccess.helper.AppHelper.getFragmentByTag;

/**
 * Created by Kosh on 09 Nov 2016, 7:53 PM
 */

class MainPresenter extends BasePresenter<MainMvp.View> implements MainMvp.Presenter {

    @Override public boolean canBackPress(@NonNull DrawerLayout drawerLayout) {
        return !drawerLayout.isDrawerOpen(GravityCompat.START);
    }

    @SuppressWarnings("ConstantConditions")
    @Override public void onModuleChanged(@NonNull FragmentManager fragmentManager, @MainMvp.NavigationType int type) {
        Fragment currentVisible = getVisibleFragment(fragmentManager);
        FeedsView homeView = (FeedsView) getFragmentByTag(fragmentManager, FeedsView.TAG);
        MyPullsPagerView pullRequestView = (MyPullsPagerView) getFragmentByTag(fragmentManager, MyPullsPagerView.TAG);
        MyIssuesPagerView issuesView = (MyIssuesPagerView) getFragmentByTag(fragmentManager, MyIssuesPagerView.TAG);
        switch (type) {
            case MainMvp.FEEDS:
                if (homeView == null) {
                    onAddAndHide(fragmentManager, FeedsView.newInstance(), currentVisible);
                } else {
                    onShowHideFragment(fragmentManager, homeView, currentVisible);
                }
                break;
            case MainMvp.PULL_REQUESTS:
                if (pullRequestView == null) {
                    onAddAndHide(fragmentManager, MyPullsPagerView.newInstance(), currentVisible);
                } else {
                    onShowHideFragment(fragmentManager, pullRequestView, currentVisible);
                }
                break;
            case MainMvp.ISSUES:
                if (issuesView == null) {
                    onAddAndHide(fragmentManager, MyIssuesPagerView.newInstance(), currentVisible);
                } else {
                    onShowHideFragment(fragmentManager, issuesView, currentVisible);
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

    @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (getView() != null) {
            getView().onCloseDrawer();
            if (item.getItemId() == R.id.logout) {
                getView().onLogout();
                return true;
            } else if (item.getItemId() == R.id.fhRepo) {
                getView().openFasHubRepo();
            } else if (item.getItemId() == R.id.settings) {
                getView().onOpenSettings();
            } else if (item.getItemId() == R.id.supportDev) {
                getView().onSupportDevelopment();
            } else if (item.getItemId() == R.id.enableAds) {
                getView().onEnableAds();
                return true;
            } else if (item.getItemId() == R.id.gists) {
                getView().onOpenGists(false);
                return true;
            } else if (item.getItemId() == R.id.myGists) {
                getView().onOpenGists(true);
                return true;
            } else if (item.getItemId() == R.id.pinnedMenu) {
                getView().onOpenPinnedRepos();
                return true;
            } else if (item.getItemId() == R.id.profile) {
                getView().onOpenProfile();
                return true;
            }
        }
        return false;
    }

    @Override public void onMenuItemSelect(@IdRes int id, int position, boolean fromUser) {
        if (getView() != null) {
            getView().onNavigationChanged(position);
        }
    }

    @Override public void onMenuItemReselect(@IdRes int id, int position, boolean fromUser) {}
}
