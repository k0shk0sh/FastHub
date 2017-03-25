package com.fastaccess.ui.modules.main;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fastaccess.BuildConfig;
import com.fastaccess.R;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.data.dao.model.Notification;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.helper.TypeFaceHelper;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.modules.about.FastHubAboutActivity;
import com.fastaccess.ui.modules.feeds.FeedsView;
import com.fastaccess.ui.modules.gists.create.CreateGistView;
import com.fastaccess.ui.modules.main.donation.DonationView;
import com.fastaccess.ui.modules.notification.NotificationActivityView;
import com.fastaccess.ui.modules.repos.RepoPagerView;
import com.fastaccess.ui.modules.search.SearchView;
import com.fastaccess.ui.modules.settings.SettingsBottomSheetDialog;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontSwitchView;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.SpannableBuilder;

import butterknife.BindView;
import butterknife.OnClick;
import icepick.State;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

public class MainView extends BaseActivity<MainMvp.View, MainPresenter> implements MainMvp.View {

    @State @MainMvp.NavigationType int navType = MainMvp.FEEDS;

    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.bottomNavigation) BottomNavigation bottomNavigation;
    @BindView(R.id.navigation) NavigationView navigationView;
    @BindView(R.id.drawerLayout) DrawerLayout drawerLayout;
    @BindView(R.id.versionText) FontTextView versionText;
    @BindView(R.id.enableAds) FontSwitchView enableAds;

    private long backPressTimer;

    @OnClick(R.id.enableAds) void onEnableAds(View view) {

        PrefGetter.setAdsEnabled(((FontSwitchView) view).isChecked());
        recreate();
    }

    @OnClick(R.id.fab) void onClick() {
        if (navType == MainMvp.GISTS) {
            startActivity(new Intent(this, CreateGistView.class));
        }
    }

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
        setToolbarIcon(R.drawable.ic_menu);
        onInit(savedInstanceState);
        onHideShowFab();
        enableAds.setChecked(PrefGetter.isAdsEnabled());
        AppHelper.cancelNotification(this);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == BundleConstant.REQUEST_CODE) {
            showMessage(R.string.success, R.string.thank_you_for_feedback);
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        if (isLoggedIn() && Notification.hasUnreadNotifications()) {
            ViewHelper.tintDrawable(menu.findItem(R.id.notifications).getIcon(), ViewHelper.getAccentColor(this));
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onOpenDrawer();
            return true;
        } else if (item.getItemId() == R.id.search) {
            startActivity(new Intent(this, SearchView.class));
            return true;
        } else if (item.getItemId() == R.id.notifications) {
            ViewHelper.tintDrawable(item.getIcon(), ViewHelper.getPrimaryTextColor(this));
            startActivity(new Intent(this, NotificationActivityView.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onBackPressed() {
        boolean clickTwichToExit = !PrefGetter.isTwiceBackButtonDisabled();
        if (drawerLayout != null) {
            if (getPresenter().canBackPress(drawerLayout)) {
                superOnBackPressed(clickTwichToExit);
            } else {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        } else {
            superOnBackPressed(clickTwichToExit);
        }
    }

    @Override public void onNavigationChanged(@MainMvp.NavigationType int navType) {
        this.navType = navType;
        //noinspection WrongConstant
        if (bottomNavigation.getSelectedIndex() != navType) bottomNavigation.setSelectedIndex(navType, true);
        onHideShowFab();
        getPresenter().onModuleChanged(getSupportFragmentManager(), navType);
    }

    @Override public void onOpenDrawer() {
        if (drawerLayout != null && !drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override public void onCloseDrawer() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override public void onHideShowFab() {
        if (navType == MainMvp.GISTS) {
            fab.show();
        } else {
            fab.hide();
        }
    }

    @Override public void onSubmitFeedback() {
        startActivity(new Intent(this, FastHubAboutActivity.class));
    }

    @Override public void onLogout() {
        onRequireLogin();
    }

    @Override public void openFasHubRepo() {
        startActivity(RepoPagerView.createIntent(this, "FastHub", "k0shk0sh"));
    }

    @Override public void onOpenSettings() {
        SettingsBottomSheetDialog.show(getSupportFragmentManager());
    }

    @Override public void onSupportDevelopment() {
        new DonationView().show(getSupportFragmentManager(), "DonationView");
    }

    private void superOnBackPressed(boolean didClickTwice) {
        if (didClickTwice) {
            if (canExit()) {
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

    private void onInit(@Nullable Bundle savedInstanceState) {
        if (isLoggedIn()) {
            if (savedInstanceState == null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, FeedsView.newInstance(), FeedsView.TAG)
                        .commit();
            }
            navigationView.setNavigationItemSelectedListener(getPresenter());
            Typeface myTypeface = TypeFaceHelper.getTypeface();
            bottomNavigation.setDefaultTypeface(myTypeface);
            bottomNavigation.setOnMenuItemClickListener(getPresenter());
            Login userModel = Login.getUser();
            if (userModel != null) {
                View view = navigationView.getHeaderView(0);
                if (view != null) {
                    ((AvatarLayout) view.findViewById(R.id.avatarLayout)).setUrl(userModel.getAvatarUrl(), userModel.getLogin());
                    ((TextView) view.findViewById(R.id.username)).setText(userModel.getName());
                    ((TextView) view.findViewById(R.id.email)).setText(userModel.getLogin());
                }
            }
            versionText.setText(SpannableBuilder.builder()
                    .append(getString(R.string.current_version))
                    .append("(")
                    .bold(BuildConfig.VERSION_NAME)
                    .append(")"));
        }
    }
}
