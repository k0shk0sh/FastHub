package com.fastaccess.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.DrawableRes;
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
import android.webkit.CookieManager;
import android.widget.TextView;
import android.widget.Toast;

import com.fastaccess.App;
import com.fastaccess.BuildConfig;
import com.fastaccess.R;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.changelog.ChangelogBottomSheetDialog;
import com.fastaccess.ui.modules.gists.GistsListActivity;
import com.fastaccess.ui.modules.login.LoginActivity;
import com.fastaccess.ui.modules.main.MainActivity;
import com.fastaccess.ui.modules.main.donation.DonationActivity;
import com.fastaccess.ui.modules.main.orgs.OrgListDialogFragment;
import com.fastaccess.ui.modules.pinned.PinnedReposActivity;
import com.fastaccess.ui.modules.repos.RepoPagerActivity;
import com.fastaccess.ui.modules.settings.SettingsBottomSheetDialog;
import com.fastaccess.ui.modules.user.UserPagerActivity;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;
import com.fastaccess.ui.widgets.dialog.ProgressDialogFragment;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import icepick.Icepick;
import icepick.State;

/**
 * Created by Kosh on 24 May 2016, 8:48 PM
 */

public abstract class BaseActivity<V extends BaseMvp.FAView, P extends BasePresenter<V>> extends AdActivity<V, P> implements
        BaseMvp.FAView, NavigationView.OnNavigationItemSelectedListener {

    @State boolean isProgressShowing;
    @Nullable @BindView(R.id.toolbar) Toolbar toolbar;
    @Nullable @BindView(R.id.appbar) public AppBarLayout appbar;
    @Nullable @BindView(R.id.drawer) public DrawerLayout drawer;
    @Nullable @BindView(R.id.extrasNav) NavigationView extraNav;

    private long backPressTimer;
    private Toast toast;

    @LayoutRes protected abstract int layout();

    protected abstract boolean isTransparent();

    protected abstract boolean canBack();

    protected abstract boolean isSecured();

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        setupTheme();
        AppHelper.updateAppLanguage(this);
        super.onCreate(savedInstanceState);
        if (layout() != 0) {
            setContentView(layout());
            ButterKnife.bind(this);
        }
        if (!isSecured()) {
            if (!isLoggedIn()) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return;
            }
        }
        Icepick.setDebug(BuildConfig.DEBUG);
        if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
            Icepick.restoreInstanceState(this, savedInstanceState);
        }
        setupToolbarAndStatusBar(toolbar);
        showHideAds();
        if (savedInstanceState == null && PrefGetter.showWhatsNew()) {
            new ChangelogBottomSheetDialog().show(getSupportFragmentManager(), "ChangelogBottomSheetDialog");
        }
        setupNavigationView(extraNav);
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
            if (logout) onRequireLogin();
        }
    }//pass

    @Override public void showMessage(@StringRes int titleRes, @StringRes int msgRes) {
        showMessage(getString(titleRes), getString(msgRes));
    }

    @Override public void showMessage(@NonNull String titleRes, @NonNull String msgRes) {
        hideProgress();
        if (toast != null) toast.cancel();
        toast = titleRes.equals(getString(R.string.error))
                ? Toasty.error(getApplicationContext(), msgRes, Toast.LENGTH_LONG)
                : Toasty.info(getApplicationContext(), msgRes, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override public void showErrorMessage(@NonNull String msgRes) {
        showMessage(getString(R.string.error), msgRes);
    }

    @Override public boolean isLoggedIn() {
        return !InputHelper.isEmpty(PrefGetter.getToken()) && Login.getUser() != null;
    }

    @Override public void showProgress(@StringRes int resId) {
        String msg = getString(R.string.in_progress);
        if (resId != 0) {
            msg = getString(resId);
        }
        if (!isProgressShowing) {
            ProgressDialogFragment fragment = (ProgressDialogFragment) AppHelper.getFragmentByTag(getSupportFragmentManager(),
                    ProgressDialogFragment.TAG);
            if (fragment == null) {
                isProgressShowing = true;
                fragment = ProgressDialogFragment.newInstance(msg, false);
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
        Toasty.warning(this, getString(R.string.unauthorized_user), Toast.LENGTH_LONG).show();
        CookieManager.getInstance().removeAllCookies(null);
        ImageLoader.getInstance().clearDiskCache();
        ImageLoader.getInstance().clearMemoryCache();
        PrefGetter.clear();
        App.getInstance().getDataStore()
                .delete(Login.class)
                .get()
                .value();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finishAffinity();
    }

    @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        new Handler().postDelayed(() -> {
            if (isFinishing()) return;
            if (item.getItemId() == R.id.navToRepo) {
                onNavToRepoClicked();
            } else if (item.getItemId() == R.id.fhRepo) {
                startActivity(RepoPagerActivity.createIntent(this, "FastHub", "k0shk0sh"));
            } else if (item.getItemId() == R.id.supportDev) {
                startActivity(new Intent(this, DonationActivity.class));
            } else if (item.getItemId() == R.id.gists) {
                GistsListActivity.startActivity(this, false);
            } else if (item.getItemId() == R.id.myGists) {
                GistsListActivity.startActivity(this, true);
            } else if (item.getItemId() == R.id.pinnedMenu) {
                PinnedReposActivity.startActivity(this);
            } else if (item.getItemId() == R.id.mainView) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } else if (item.getItemId() == R.id.profile) {
                startActivity(UserPagerActivity.createIntent(this, Login.getUser().getLogin()));
            } else if (item.getItemId() == R.id.logout) {
                onLogoutPressed();
            } else if (item.getItemId() == R.id.settings) {
                onOpenSettings();
            } else if (item.getItemId() == R.id.orgs) {
                onOpenOrgsDialog();
            } else if (item.getItemId() == R.id.enableAds) {
                boolean isEnabled = !PrefGetter.isAdsEnabled();
                PrefGetter.setAdsEnabled(isEnabled);
                showHideAds();
                item.setChecked(isEnabled);
            }
        }, 300);
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
        recreate();
    }

    @Override public void onOpenSettings() {
        SettingsBottomSheetDialog.show(getSupportFragmentManager());
    }

    protected void hideHome() {
        if (extraNav != null) extraNav.getMenu().removeGroup(R.id.home_group);
    }

    protected void onOpenOrgsDialog() {
        OrgListDialogFragment.newInstance().show(getSupportFragmentManager(), "OrgListDialogFragment");
    }

    protected void showNavToRepoItem() {
        if (extraNav != null) {
            extraNav.getMenu().findItem(R.id.navToRepo).setVisible(true);
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
        int themeMode = PrefGetter.getThemeType(getApplicationContext());
        if (themeMode == PrefGetter.LIGHT) {
            setTheme(R.style.ThemeLight);
        } else if (themeMode == PrefGetter.DARK) {
            setTheme(R.style.ThemeDark);
        }
    }

    protected void setupNavigationView(@Nullable NavigationView extraNav) {
        if (extraNav != null) {
            extraNav.setNavigationItemSelectedListener(this);
            Login userModel = Login.getUser();
            if (userModel != null) {
                View view = extraNav.getHeaderView(0);
                if (view != null) {
                    ((AvatarLayout) view.findViewById(R.id.avatarLayout)).setUrl(userModel.getAvatarUrl(), userModel.getLogin());
                    ((TextView) view.findViewById(R.id.username)).setText(userModel.getLogin());
                    if (!InputHelper.isEmpty(userModel.getName())) {
                        ((TextView) view.findViewById(R.id.email)).setText(userModel.getName());
                    } else {
                        view.findViewById(R.id.email).setVisibility(View.GONE);
                    }
                    view.findViewById(R.id.userHolder).setOnClickListener(v -> UserPagerActivity.startActivity(this, userModel.getLogin()));

                }
            }
            if (BuildConfig.FDROID) {
                Menu menu = extraNav.getMenu();
                menu.findItem(R.id.enableAds).setVisible(false);
                menu.findItem(R.id.supportDev).setVisible(false);
            } else {
                extraNav.getMenu().findItem(R.id.enableAds).setChecked(PrefGetter.isAdsEnabled());
            }
        }
    }

    protected void hideProfileMenuItem() {
        if (extraNav != null) {
            extraNav.getMenu().findItem(R.id.profile).setVisible(false);
        }
    }

    private void setupDrawer() {
        if (drawer != null) {
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
            Toast.makeText(getBaseContext(), R.string.press_again_to_exit, Toast.LENGTH_SHORT).show();
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
}
