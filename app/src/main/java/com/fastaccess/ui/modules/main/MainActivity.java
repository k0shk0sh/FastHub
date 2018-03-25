package com.fastaccess.ui.modules.main;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.evernote.android.state.State;
import com.fastaccess.App;
import com.fastaccess.R;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.data.dao.model.Notification;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.helper.TypeFaceHelper;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.modules.feeds.FeedsFragment;
import com.fastaccess.ui.modules.main.issues.pager.MyIssuesPagerFragment;
import com.fastaccess.ui.modules.main.pullrequests.pager.MyPullsPagerFragment;
import com.fastaccess.ui.modules.notification.NotificationActivity;
import com.fastaccess.ui.modules.search.SearchActivity;
import com.fastaccess.ui.modules.settings.SlackBottomSheetDialog;
import com.fastaccess.ui.modules.user.UserPagerActivity;

import butterknife.BindView;
import butterknife.OnClick;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;
import shortbread.Shortcut;

import static com.fastaccess.helper.AppHelper.getFragmentByTag;

public class MainActivity extends BaseActivity<MainMvp.View, MainPresenter> implements MainMvp.View {

    @State @MainMvp.NavigationType int navType = MainMvp.FEEDS;
    @BindView(R.id.bottomNavigation) BottomNavigation bottomNavigation;
    @BindView(R.id.fab) FloatingActionButton fab;

    @OnClick(R.id.fab) void onFilter() {}

    @NonNull @Override public MainPresenter providePresenter() {
        return new MainPresenter();
    }

    @Override protected int layout() {
        return R.layout.activity_main_view;
    }

    @Override protected boolean isTransparent() {
        return true;
    }

    @Override protected boolean canBack() {
        return false;
    }

    @Override protected boolean isSecured() {
        return false;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getBooleanExtra(SlackBottomSheetDialog.TAG, false)) {
                new SlackBottomSheetDialog().show(getSupportFragmentManager(), SlackBottomSheetDialog.TAG);
            }
        }
        getPresenter().setEnterprise(PrefGetter.isEnterprise());
        selectHome(false);
        hideShowShadow(navType == MainMvp.FEEDS);
        setToolbarIcon(R.drawable.ic_menu);
        onInit(savedInstanceState);
        fab.setImageResource(R.drawable.ic_filter);
        onNewIntent(getIntent());
    }

    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && intent.getExtras() != null) {
            boolean recreate = intent.getExtras().getBoolean(BundleConstant.YES_NO_EXTRA);
            if (recreate) recreate();
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (drawer != null) drawer.openDrawer(GravityCompat.START);
            return true;
        } else if (item.getItemId() == R.id.search) {
            startActivity(new Intent(this, SearchActivity.class));
            return true;
        } else if (item.getItemId() == R.id.notifications) {
            ViewHelper.tintDrawable(item.setIcon(R.drawable.ic_notifications_none).getIcon(), ViewHelper.getIconColor(this));
            startActivity(new Intent(this, NotificationActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public boolean onPrepareOptionsMenu(Menu menu) {
        if (isLoggedIn() && Notification.hasUnreadNotifications()) {
            ViewHelper.tintDrawable(menu.findItem(R.id.notifications).setIcon(R.drawable.ic_ring).getIcon(), ViewHelper.getAccentColor(this));
        } else {
            ViewHelper.tintDrawable(menu.findItem(R.id.notifications)
                    .setIcon(R.drawable.ic_notifications_none).getIcon(), ViewHelper.getIconColor(this));
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override public void onNavigationChanged(@MainMvp.NavigationType int navType) {
        if (navType == MainMvp.PROFILE) {
            getPresenter().onModuleChanged(getSupportFragmentManager(), navType);
            bottomNavigation.setSelectedIndex(this.navType, true);
            return;
        }
        this.navType = navType;
        //noinspection WrongConstant
        if (bottomNavigation.getSelectedIndex() != navType) bottomNavigation.setSelectedIndex(navType, true);
        hideShowShadow(navType == MainMvp.FEEDS);
        getPresenter().onModuleChanged(getSupportFragmentManager(), navType);
    }

    @Override public void onUpdateDrawerMenuHeader() {
        setupNavigationView();
    }

    @Override public void onOpenProfile() {
        UserPagerActivity.startActivity(this, Login.getUser().getLogin(), false, PrefGetter.isEnterprise(), -1);
    }

    @Override public void onInvalidateNotification() {
        invalidateOptionsMenu();
    }

    @Override public void onUserIsBlackListed() {
        Toast.makeText(App.getInstance(), "You are blacklisted, please contact the dev", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override public void onScrollTop(int index) {
        super.onScrollTop(index);
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (index == 0) {
            FeedsFragment homeView = (FeedsFragment) getFragmentByTag(fragmentManager, FeedsFragment.TAG);
            if (homeView != null) {
                homeView.onScrollTop(index);
            }
        } else if (index == 1) {
            MyIssuesPagerFragment issuesView = (MyIssuesPagerFragment) getFragmentByTag
                    (fragmentManager, MyIssuesPagerFragment.TAG);
            if (issuesView != null) {
                issuesView.onScrollTop(index);
            }
        } else if (index == 2) {
            MyPullsPagerFragment pullRequestView = (MyPullsPagerFragment) getFragmentByTag
                    (fragmentManager, MyPullsPagerFragment.TAG);
            if (pullRequestView != null) {
                pullRequestView.onScrollTop(0);
            }
        }
    }

    @Shortcut(id = "myIssues", icon = R.drawable.ic_app_shortcut_issues, shortLabelRes = R.string.issues, rank = 2, action = "myIssues")
    public void myIssues() {}//do nothing

    @Shortcut(id = "myPulls", icon = R.drawable.ic_app_shortcut_pull_requests, shortLabelRes = R.string.pull_requests, rank = 3, action = "myPulls")
    public void myPulls() {}//do nothing

    private void onInit(@Nullable Bundle savedInstanceState) {
        if (isLoggedIn()) {
            if (savedInstanceState == null) {
                boolean attachFeeds = true;
                if (getIntent().getAction() != null) {
                    if (getIntent().getAction().equalsIgnoreCase("myPulls")) {
                        navType = MainMvp.PULL_REQUESTS;
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, MyPullsPagerFragment.newInstance(), MyPullsPagerFragment.TAG)
                                .commit();
                        bottomNavigation.setSelectedIndex(2, true);
                        attachFeeds = false;
                    } else if (getIntent().getAction().equalsIgnoreCase("myIssues")) {
                        navType = MainMvp.ISSUES;
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, MyIssuesPagerFragment.newInstance(), MyIssuesPagerFragment.TAG)
                                .commit();
                        bottomNavigation.setSelectedIndex(1, true);
                        attachFeeds = false;
                    }
                }
                hideShowShadow(navType == MainMvp.FEEDS);
                if (attachFeeds) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, FeedsFragment.newInstance(null), FeedsFragment.TAG)
                            .commit();
                }
            }
            Typeface myTypeface = TypeFaceHelper.getTypeface();
            bottomNavigation.setDefaultTypeface(myTypeface);
            bottomNavigation.setOnMenuItemClickListener(getPresenter());
        }
    }
}
