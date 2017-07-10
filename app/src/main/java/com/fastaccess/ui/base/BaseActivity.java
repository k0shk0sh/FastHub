package com.fastaccess.ui.base;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.evernote.android.state.State;
import com.evernote.android.state.StateSaver;
import com.fastaccess.App;
import com.fastaccess.R;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.provider.theme.ThemeEngine;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.changelog.ChangelogBottomSheetDialog;
import com.fastaccess.ui.modules.login.LoginChooserActivity;
import com.fastaccess.ui.modules.main.MainActivity;
import com.fastaccess.ui.modules.main.orgs.OrgListDialogFragment;
import com.fastaccess.ui.modules.settings.SettingsActivity;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;
import com.fastaccess.ui.widgets.dialog.ProgressDialogFragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.grandcentrix.thirtyinch.TiActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;


/**
 * Created by Kosh on 24 May 2016, 8:48 PM
 */

public abstract class BaseActivity<V extends BaseMvp.FAView, P extends BasePresenter<V>> extends TiActivity<P, V> implements
        BaseMvp.FAView, NavigationView.OnNavigationItemSelectedListener {

    @State boolean isProgressShowing;
    @Nullable @BindView(R.id.toolbar) public Toolbar toolbar;
    @Nullable @BindView(R.id.appbar) public AppBarLayout appbar;
    @Nullable @BindView(R.id.drawer) public DrawerLayout drawer;
    @Nullable @BindView(R.id.extrasNav) public NavigationView extraNav;
    @Nullable @BindView(R.id.accountsNav) NavigationView accountsNav;
    @Nullable @BindView(R.id.adView) AdView adView;
    private MainNavDrawer mainNavDrawer;

    @State Bundle presenterStateBundle = new Bundle();

    private static int REFRESH_CODE = 64;

    private long backPressTimer;
    private Toast toast;

    @LayoutRes protected abstract int layout();

    protected abstract boolean isTransparent();

    protected abstract boolean canBack();

    protected abstract boolean isSecured();

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        StateSaver.saveInstanceState(this, outState);
        getPresenter().onSaveInstanceState(presenterStateBundle);
    }

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTaskName(null);
        setupTheme();
        AppHelper.updateAppLanguage(this);
        super.onCreate(savedInstanceState);
        if (layout() != 0) {
            setContentView(layout());
            ButterKnife.bind(this);
        }
        if (!isSecured()) {
            if (!isLoggedIn()) {
                onRequireLogin();
                return;
            }
        }
        if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
            StateSaver.restoreInstanceState(this, savedInstanceState);
            getPresenter().onRestoreInstanceState(presenterStateBundle);
        }
        setupToolbarAndStatusBar(toolbar);
        showHideAds();
        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getExtras() != null) {
                getPresenter().setEnterprise(getIntent().getExtras().getBoolean(BundleConstant.IS_ENTERPRISE));
            }
            if (PrefGetter.showWhatsNew()) {
                new ChangelogBottomSheetDialog().show(getSupportFragmentManager(), "ChangelogBottomSheetDialog");
            }
        }
        mainNavDrawer = new MainNavDrawer(this, extraNav, accountsNav);
        setupNavigationView();
        setupDrawer();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (canBack()) {
            if (item.getItemId() == android.R.id.home) {
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onDialogDismissed() {

    }//pass

    @Override public void onMessageDialogActionClicked(boolean isOk, @Nullable Bundle bundle) {
        if (isOk && bundle != null) {
            boolean logout = bundle.getBoolean("logout");
            if (logout) {
                onRequireLogin();
//                if(App.getInstance().getGoogleApiClient().isConnected())
//                    Auth.CredentialsApi.disableAutoSignIn(App.getInstance().getGoogleApiClient());
            }
        }
    }//pass

    @Override public void showMessage(@StringRes int titleRes, @StringRes int msgRes) {
        showMessage(getString(titleRes), getString(msgRes));
    }

    @Override public void showMessage(@NonNull String titleRes, @NonNull String msgRes) {
        hideProgress();
        if (toast != null) toast.cancel();
        Context context = App.getInstance(); // WindowManager$BadTokenException
        toast = titleRes.equals(context.getString(R.string.error))
                ? Toasty.error(context, msgRes, Toast.LENGTH_LONG)
                : Toasty.info(context, msgRes, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override public void showErrorMessage(@NonNull String msgRes) {
        showMessage(getString(R.string.error), msgRes);
    }

    @Override public boolean isLoggedIn() {
        return Login.getUser() != null;
    }

    @Override public void showProgress(@StringRes int resId) {
        String msg = getString(R.string.in_progress);
        if (resId != 0) {
            msg = getString(resId);
        }
        if (!isProgressShowing && !isFinishing()) {
            ProgressDialogFragment fragment = (ProgressDialogFragment) AppHelper.getFragmentByTag(getSupportFragmentManager(),
                    ProgressDialogFragment.TAG);
            if (fragment == null) {
                isProgressShowing = true;
                fragment = ProgressDialogFragment.newInstance(msg, true);
                fragment.show(getSupportFragmentManager(), ProgressDialogFragment.TAG);
            }
        }
    }

    @Override public void hideProgress() {
        ProgressDialogFragment fragment = (ProgressDialogFragment) AppHelper.getFragmentByTag(getSupportFragmentManager(),
                ProgressDialogFragment.TAG);
        if (fragment != null) {
            isProgressShowing = false;
            fragment.dismiss();
        }
    }

    @Override public void onRequireLogin() {
        Toasty.warning(App.getInstance(), getString(R.string.unauthorized_user), Toast.LENGTH_LONG).show();
        ImageLoader.getInstance().clearDiskCache();
        ImageLoader.getInstance().clearMemoryCache();
        PrefGetter.setToken(null);
        PrefGetter.setEnterpriseUrl(null);
        PrefGetter.setOtpCode(null);
        PrefGetter.setEnterpriseOtpCode(null);
        Login.logout();
        Intent intent = new Intent(this, LoginChooserActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finishAffinity();
    }

    @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        mainNavDrawer.onMainNavItemClick(item);
        return false;
    }

    @Override public void onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            boolean clickTwiceToExit = !PrefGetter.isTwiceBackButtonDisabled();
            superOnBackPressed(clickTwiceToExit);
        }
    }

    @Override public void onLogoutPressed() {
        MessageDialogView.newInstance(getString(R.string.logout), getString(R.string.confirm_message),
                Bundler.start()
                        .put(BundleConstant.YES_NO_EXTRA, true)
                        .put("logout", true)
                        .end())
                .show(getSupportFragmentManager(), MessageDialogView.TAG);
    }

    @Override public void onThemeChanged() {
        if (this instanceof MainActivity) {
            recreate();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtras(Bundler.start().put(BundleConstant.YES_NO_EXTRA, true).end());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    @Override public void onOpenSettings() {
        startActivityForResult(new Intent(this, SettingsActivity.class), REFRESH_CODE);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REFRESH_CODE) {
                onThemeChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override public void onScrollTop(int index) {}

    @Override protected void onPause() {
        if (adView != null && adView.isShown()) {
            adView.pause();
        }
        super.onPause();
    }

    @Override protected void onResume() {
        super.onResume();
        if (adView != null && adView.isShown()) {
            adView.resume();
        }
    }

    @Override protected void onDestroy() {
        if (adView != null && adView.isShown()) {
            adView.destroy();
        }
        super.onDestroy();
    }

    @Override public boolean isEnterprise() {
        return getPresenter() != null && getPresenter().isEnterprise();
    }

    protected void setTaskName(@Nullable String name) {
        setTaskDescription(new ActivityManager.TaskDescription(name, null, ViewHelper.getPrimaryDarkColor(this)));
    }

    protected void showHideAds() {
        if (adView != null) {
            boolean isAdsEnabled = PrefGetter.isAdsEnabled();
            if (isAdsEnabled) {
                adView.setVisibility(View.VISIBLE);
                MobileAds.initialize(this, getString(R.string.banner_ad_unit_id));
                AdRequest adRequest = new AdRequest.Builder()
                        .addTestDevice(getString(R.string.test_device_id))
                        .build();
                adView.loadAd(adRequest);
            } else {
                adView.destroy();
                adView.setVisibility(View.GONE);
            }
        }
    }

    protected void selectHome(boolean hideRepo) {
        if (extraNav != null) {
            if (hideRepo) {
                extraNav.getMenu().findItem(R.id.navToRepo).setVisible(false);
                extraNav.getMenu().findItem(R.id.mainView).setVisible(true);
                return;
            }
            extraNav.getMenu().findItem(R.id.navToRepo).setVisible(false);
            extraNav.getMenu().findItem(R.id.mainView).setCheckable(true);
            extraNav.getMenu().findItem(R.id.mainView).setChecked(true);
        }
    }

    protected void selectProfile() {
        selectHome(true);
        selectMenuItem(R.id.profile);
    }

    protected void selectPinned() {
        selectMenuItem(R.id.pinnedMenu);
    }

    protected void onSelectNotifications() {
        selectMenuItem(R.id.notifications);
    }

    protected void onSelectTrending() {
        selectMenuItem(R.id.trending);
    }

    protected void onOpenOrgsDialog() {
        OrgListDialogFragment.newInstance().show(getSupportFragmentManager(), "OrgListDialogFragment");
    }

    protected void showNavToRepoItem() {
        if (extraNav != null) {
            extraNav.getMenu().findItem(R.id.navToRepo).setVisible(true);
        }
    }

    protected void selectMenuItem(@IdRes int id) {
        if (extraNav != null) {
            extraNav.getMenu().findItem(id).setCheckable(true);
            extraNav.getMenu().findItem(id).setChecked(true);
        }
    }

    protected void onNavToRepoClicked() {}

    private void setupToolbarAndStatusBar(@Nullable Toolbar toolbar) {
        changeStatusBarColor(isTransparent());
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (canBack()) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    if (canBack()) {
                        View navIcon = getToolbarNavigationIcon(toolbar);
                        if (navIcon != null) {
                            navIcon.setOnLongClickListener(v -> {
                                Intent intent = new Intent(this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                return true;
                            });
                        }
                    }
                }
            }
        }
    }

    protected void setToolbarIcon(@DrawableRes int res) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(res);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    protected void hideShowShadow(boolean show) {
        if (appbar != null) {
            appbar.setElevation(show ? getResources().getDimension(R.dimen.spacing_micro) : 0.0f);
        }
    }

    protected void changeStatusBarColor(boolean isTransparent) {
        if (!isTransparent) {
            getWindow().setStatusBarColor(ViewHelper.getPrimaryDarkColor(this));
        }
    }

    private void setupTheme() {
        ThemeEngine.INSTANCE.apply(this);
    }

    protected void setupNavigationView() {
        if (extraNav != null) {
            extraNav.setNavigationItemSelectedListener(this);
        }
        mainNavDrawer.setupViewDrawer();
    }

    private void setupDrawer() {
        if (drawer != null && !(this instanceof MainActivity)) {
            if (!PrefGetter.isNavDrawerHintShowed()) {
                drawer.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override public boolean onPreDraw() {
                        drawer.openDrawer(GravityCompat.START);
                        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                            @Override public void onDrawerOpened(View drawerView) {
                                super.onDrawerOpened(drawerView);
                                drawerView.postDelayed(() -> {
                                    if (drawer != null) {
                                        drawer.closeDrawer(GravityCompat.START);
                                        drawer.removeDrawerListener(this);
                                    }
                                }, 1000);
                            }
                        });
                        drawer.getViewTreeObserver().removeOnPreDrawListener(this);
                        return true;
                    }
                });
            }
        }
    }

    private void superOnBackPressed(boolean didClickTwice) {
        if (this instanceof MainActivity) {
            if (didClickTwice) {
                if (canExit()) {
                    super.onBackPressed();
                }
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    private boolean canExit() {
        if (backPressTimer + 2000 > System.currentTimeMillis()) {
            return true;
        } else {
            Toast.makeText(App.getInstance(), R.string.press_again_to_exit, Toast.LENGTH_SHORT).show();
        }
        backPressTimer = System.currentTimeMillis();
        return false;
    }

    @Nullable private View getToolbarNavigationIcon(Toolbar toolbar) {
        boolean hadContentDescription = TextUtils.isEmpty(toolbar.getNavigationContentDescription());
        String contentDescription = !hadContentDescription ? String.valueOf(toolbar.getNavigationContentDescription()) : "navigationIcon";
        toolbar.setNavigationContentDescription(contentDescription);
        ArrayList<View> potentialViews = new ArrayList<>();
        toolbar.findViewsWithText(potentialViews, contentDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        View navIcon = null;
        if (potentialViews.size() > 0) {
            navIcon = potentialViews.get(0);
        }
        if (hadContentDescription) toolbar.setNavigationContentDescription(null);
        return navIcon;
    }

    protected void onRestartApp() {
        onThemeChanged();
    }
}
