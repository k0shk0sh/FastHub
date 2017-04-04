package com.fastaccess.ui.base;

import android.content.Intent;
import android.os.Bundle;
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
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.Logger;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.ui.adapter.UsersAdapter;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.changelog.ChangelogView;
import com.fastaccess.ui.modules.gists.GistsListActivity;
import com.fastaccess.ui.modules.login.LoginView;
import com.fastaccess.ui.modules.main.MainView;
import com.fastaccess.ui.modules.main.donation.DonationView;
import com.fastaccess.ui.modules.pinned.PinnedReposActivity;
import com.fastaccess.ui.modules.repos.RepoPagerView;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.dialog.ProgressDialogFragment;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.grandcentrix.thirtyinch.TiActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import icepick.Icepick;
import icepick.State;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

/**
 * Created by Kosh on 24 May 2016, 8:48 PM
 */

public abstract class BaseActivity<V extends BaseMvp.FAView, P extends BasePresenter<V>> extends TiActivity<P, V> implements
        BaseMvp.FAView, NavigationView.OnNavigationItemSelectedListener {

    @State boolean isProgressShowing;
    @Nullable @BindView(R.id.toolbar) Toolbar toolbar;
    @Nullable @BindView(R.id.appbar) AppBarLayout shadowView;
    @Nullable @BindView(R.id.adView) AdView adView;
    @Nullable @BindView(R.id.drawer) DrawerLayout drawer;
    @Nullable @BindView(R.id.extrasNav) NavigationView extraNav;
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
        super.onCreate(savedInstanceState);
        if (layout() != 0) {
            setContentView(layout());
            ButterKnife.bind(this);
        }
        if (!isSecured()) {
            if (!isLoggedIn()) {
                startActivity(new Intent(this, LoginView.class));
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
            new ChangelogView().show(getSupportFragmentManager(), "ChangelogView");
        }
        setupNavigationView(extraNav);
        setupDrawer();
    }

    @Override protected void onResume() {
        super.onResume();
        if (adView != null && adView.isShown()) {
            adView.resume();
        }
    }

    @Override protected void onPause() {
        if (adView != null && adView.isShown()) {
            adView.pause();
        }
        super.onPause();
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

    @Override protected void onDestroy() {
        if (adView != null && adView.isShown()) {
            adView.destroy();
        }
        super.onDestroy();
    }

    @Override public void onDialogDismissed() {

    }//pass

    @Override public void onMessageDialogActionClicked(boolean isOk, @Nullable Bundle bundle) {

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
        recreate();
    }

    @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if (item.getItemId() == R.id.navToRepo) {
            onNavToRepoClicked();
            return true;
        } else if (item.getItemId() == R.id.fhRepo) {
            startActivity(RepoPagerView.createIntent(this, "FastHub", "k0shk0sh"));
        } else if (item.getItemId() == R.id.supportDev) {
            new DonationView().show(getSupportFragmentManager(), "DonationView");
        } else if (item.getItemId() == R.id.gists) {
            GistsListActivity.startActivity(this, false);
            return true;
        } else if (item.getItemId() == R.id.myGists) {
            GistsListActivity.startActivity(this, true);
            return true;
        } else if (item.getItemId() == R.id.pinnedMenu) {
            PinnedReposActivity.startActivity(this);
            return true;
        } else if (item.getItemId() == R.id.mainView) {
            Intent intent = new Intent(this, MainView.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finishAffinity();
        }
        return false;
    }

    @Override public void onBackPressed() {
        if (drawer == null || !drawer.isDrawerOpen(GravityCompat.START)) {
            super.onBackPressed();
        } else if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
                            if (!PrefGetter.isHomeButoonHintShowed()) {
                                new MaterialTapTargetPrompt.Builder(this)
                                        .setTarget(navIcon)
                                        .setPrimaryText(R.string.home)
                                        .setSecondaryText(R.string.home_long_click_hint)
                                        .setCaptureTouchEventOutsidePrompt(true)
                                        .show();
                            }
                            navIcon.setOnLongClickListener(v -> {
                                Intent intent = new Intent(this, MainView.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finishAffinity();
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

    protected void hideShowShadow(boolean show) {
        if (shadowView != null) {
            shadowView.setElevation(show ? getResources().getDimension(R.dimen.spacing_micro) : 0.0f);
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
                getPresenter().onLoadOrgs();
                View view = extraNav.getHeaderView(0);
                if (view != null) {
                    ((AvatarLayout) view.findViewById(R.id.avatarLayout)).setUrl(userModel.getAvatarUrl(), userModel.getLogin());
                    ((TextView) view.findViewById(R.id.username)).setText(userModel.getLogin());
                    if (!InputHelper.isEmpty(userModel.getName())) {
                        ((TextView) view.findViewById(R.id.email)).setText(userModel.getName());
                    } else {
                        view.findViewById(R.id.email).setVisibility(View.GONE);
                    }
                    setupOrg(view);
                }
            }
        }
    }

    private void setupOrg(@NonNull View view) {
        View dropDownIcon = view.findViewById(R.id.dropDownIcon);
        View orgLayoutHolder = view.findViewById(R.id.orgLayoutHolder);
        view.findViewById(R.id.userHolder).setOnClickListener(v -> dropDownIcon.performClick());
        DynamicRecyclerView orgRecycler = (DynamicRecyclerView) view.findViewById(R.id.orgRecycler);
        dropDownIcon.findViewById(R.id.dropDownIcon)
                .setOnClickListener(v -> {
                    if (!getPresenter().getOrgList().isEmpty()) {
                        if (dropDownIcon.getTag() == null) {
                            dropDownIcon.setRotation(180F);
                            dropDownIcon.setTag("dropDownIcon");
                            orgLayoutHolder.setVisibility(View.VISIBLE);
                            Logger.e(getPresenter().getOrgList());
                            if (orgRecycler.getAdapter() == null) {
                                orgRecycler.addKeyLineDivider();
                                orgRecycler.setAdapter(new UsersAdapter(getPresenter().getOrgList()));
                            }
                        } else {
                            orgLayoutHolder.setVisibility(View.GONE);
                            dropDownIcon.setTag(null);
                            dropDownIcon.setRotation(0.0F);
                        }
                    } else {
                        showErrorMessage(getString(R.string.no_orgs));
                    }
                });
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
