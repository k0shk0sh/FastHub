package com.fastaccess.ui.base;

import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
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
import com.fastaccess.ui.modules.about.FastHubAboutActivity;
import com.fastaccess.ui.modules.changelog.ChangelogBottomSheetDialog;
import com.fastaccess.ui.modules.gists.GistsListActivity;
import com.fastaccess.ui.modules.login.LoginChooserActivity;
import com.fastaccess.ui.modules.main.MainActivity;
import com.fastaccess.ui.modules.main.donation.DonationActivity;
import com.fastaccess.ui.modules.main.orgs.OrgListDialogFragment;
import com.fastaccess.ui.modules.notification.NotificationActivity;
import com.fastaccess.ui.modules.pinned.PinnedReposActivity;
import com.fastaccess.ui.modules.repos.RepoPagerActivity;
import com.fastaccess.ui.modules.settings.SettingsActivity;
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
    @Nullable @BindView(R.id.extrasNav) public NavigationView extraNav;

    private static int REFRESH_CODE = 64;

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
                onRequireLogin();
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
        ImageLoader.getInstance().clearDiskCache();
        ImageLoader.getInstance().clearMemoryCache();
        PrefGetter.clear();
        App.getInstance().getDataStore()
                .delete(Login.class)
                .get()
                .value();
        Intent intent = new Intent(this, LoginChooserActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finishAffinity();
    }

    @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if (item.isChecked()) return false;
        new Handler().postDelayed(() -> {
            if (isFinishing()) return;
            if (item.getItemId() == R.id.navToRepo) {
                onNavToRepoClicked();
            } else if (item.getItemId() == R.id.supportDev) {
                startActivity(new Intent(this, DonationActivity.class));
            } else if (item.getItemId() == R.id.gists) {
                GistsListActivity.startActivity(this, false);
            } else if (item.getItemId() == R.id.pinnedMenu) {
                PinnedReposActivity.startActivity(this);
            } else if (item.getItemId() == R.id.mainView) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else if (item.getItemId() == R.id.profile) {
                startActivity(UserPagerActivity.createIntent(this, Login.getUser().getLogin()));
            } else if (item.getItemId() == R.id.logout) {
                onLogoutPressed();
            } else if (item.getItemId() == R.id.settings) {
                onOpenSettings();
            } else if (item.getItemId() == R.id.about) {
                startActivity(new Intent(this, FastHubAboutActivity.class));
            } else if (item.getItemId() == R.id.orgs) {
                onOpenOrgsDialog();
            } else if (item.getItemId() == R.id.notifications) {
                startActivity(new Intent(this, NotificationActivity.class));
            }
        }, 250);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REFRESH_CODE)
            if(resultCode==RESULT_OK)
                recreate();
        super.onActivityResult(requestCode, resultCode, data);
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
        if (extraNav != null) {
            extraNav.getMenu().findItem(R.id.profile).setCheckable(true);
            extraNav.getMenu().findItem(R.id.profile).setChecked(true);
        }
    }

    protected void selectPinned() {
        if (extraNav != null) {
            extraNav.getMenu().findItem(R.id.pinnedMenu).setCheckable(true);
            extraNav.getMenu().findItem(R.id.pinnedMenu).setChecked(true);
        }
    }

    protected void onSelectNotifications() {
        if (extraNav != null) {
            extraNav.getMenu().findItem(R.id.notifications).setCheckable(true);
            extraNav.getMenu().findItem(R.id.notifications).setChecked(true);
        }
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
        int themeColor = PrefGetter.getThemeColor(getApplicationContext());
        if (themeMode == PrefGetter.LIGHT) {
            switch (themeColor) {
                case PrefGetter.RED:
                    setTheme(R.style.ThemeLight_Red);
                    break;
                case PrefGetter.PINK:
                    setTheme(R.style.ThemeLight_Pink);
                    break;
                case PrefGetter.PURPLE:
                    setTheme(R.style.ThemeLight_Purple);
                    break;
                case PrefGetter.DEEP_PURPLE:
                    setTheme(R.style.ThemeLight_DeepPurple);
                    break;
                case PrefGetter.INDIGO:
                    setTheme(R.style.ThemeLight_Indigo);
                    break;
                case PrefGetter.BLUE:
                    setTheme(R.style.ThemeLight);
                    break;
                case PrefGetter.LIGHT_BLUE:
                    setTheme(R.style.ThemeLight_LightBlue);
                    break;
                case PrefGetter.CYAN:
                    setTheme(R.style.ThemeLight_Cyan);
                    break;
                case PrefGetter.TEAL:
                    setTheme(R.style.ThemeLight_Teal);
                    break;
                case PrefGetter.GREEN:
                    setTheme(R.style.ThemeLight_Green);
                    break;
                case PrefGetter.LIGHT_GREEN:
                    setTheme(R.style.ThemeLight_LightGreen);
                    break;
                case PrefGetter.LIME:
                    setTheme(R.style.ThemeLight_Lime);
                    break;
                case PrefGetter.YELLOW:
                    setTheme(R.style.ThemeLight_Yellow);
                    break;
                case PrefGetter.AMBER:
                    setTheme(R.style.ThemeLight_Amber);
                    break;
                case PrefGetter.ORANGE:
                    setTheme(R.style.ThemeLight_Orange);
                    break;
                case PrefGetter.DEEP_ORANGE:
                    setTheme(R.style.ThemeLight_DeepOrange);
                    break;
                default:
                    setTheme(R.style.ThemeLight);
            }
        } else if (themeMode == PrefGetter.DARK) {
            switch (themeColor) {
                case PrefGetter.RED:
                    setTheme(R.style.ThemeDark_Red);
                    break;
                case PrefGetter.PINK:
                    setTheme(R.style.ThemeDark_Pink);
                    break;
                case PrefGetter.PURPLE:
                    setTheme(R.style.ThemeDark_Purple);
                    break;
                case PrefGetter.DEEP_PURPLE:
                    setTheme(R.style.ThemeDark_DeepPurple);
                    break;
                case PrefGetter.INDIGO:
                    setTheme(R.style.ThemeDark_Indigo);
                    break;
                case PrefGetter.BLUE:
                    setTheme(R.style.ThemeDark);
                    break;
                case PrefGetter.LIGHT_BLUE:
                    setTheme(R.style.ThemeDark_LightBlue);
                    break;
                case PrefGetter.CYAN:
                    setTheme(R.style.ThemeDark_Cyan);
                    break;
                case PrefGetter.TEAL:
                    setTheme(R.style.ThemeDark_Teal);
                    break;
                case PrefGetter.GREEN:
                    setTheme(R.style.ThemeDark_Green);
                    break;
                case PrefGetter.LIGHT_GREEN:
                    setTheme(R.style.ThemeDark_LightGreen);
                    break;
                case PrefGetter.LIME:
                    setTheme(R.style.ThemeDark_Lime);
                    break;
                case PrefGetter.YELLOW:
                    setTheme(R.style.ThemeDark_Yellow);
                    break;
                case PrefGetter.AMBER:
                    setTheme(R.style.ThemeDark_Amber);
                    break;
                case PrefGetter.ORANGE:
                    setTheme(R.style.ThemeDark_Orange);
                    break;
                case PrefGetter.DEEP_ORANGE:
                    setTheme(R.style.ThemeDark_DeepOrange);
                    break;
                default:
                    setTheme(R.style.ThemeDark);
            }
        }
        setTaskDescription(new ActivityManager.TaskDescription(getString(R.string.app_name),
                BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher), ViewHelper.getPrimaryColor(this)));
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
        }
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
