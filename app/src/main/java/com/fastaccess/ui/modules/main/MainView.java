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

import com.fastaccess.R;
import com.fastaccess.data.dao.LoginModel;
import com.fastaccess.helper.TypeFaceHelper;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.modules.feeds.FeedsView;
import com.fastaccess.ui.modules.gists.create.CreateGistView;
import com.fastaccess.ui.modules.search.SearchView;
import com.fastaccess.ui.widgets.AvatarLayout;

import butterknife.BindView;
import butterknife.OnClick;
import icepick.State;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

public class MainView extends BaseActivity<MainMvp.View, MainPresenter> implements MainMvp.View {

    @MainMvp.NavigationType @State int navType = MainMvp.FEEDS;

    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.bottomNavigation) BottomNavigation bottomNavigation;
    @BindView(R.id.navigation) NavigationView navigationView;
    @BindView(R.id.drawerLayout) DrawerLayout drawerLayout;

    private long backPressTimer;

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
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setToolbarIcon(R.drawable.ic_menu);
        onInit(savedInstanceState);
        onHideShowFab();
        hideShowShadow(navType != MainMvp.PROFILE);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onOpenDrawer();
            return true;
        } else if (item.getItemId() == R.id.search) {
            startActivity(new Intent(this, SearchView.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onBackPressed() {
        if (drawerLayout != null) {
            if (getPresenter().canBackPress(drawerLayout)) {
                if (canExit()) super.onBackPressed();
            } else {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        } else {
            if (canExit()) super.onBackPressed();
        }
    }

    @Override public void onNavigationChanged(@MainMvp.NavigationType int navType) {
        //noinspection WrongConstant
        if (bottomNavigation.getSelectedIndex() != navType) bottomNavigation.setSelectedIndex(navType, true);
        this.navType = navType;
        hideShowShadow(navType != MainMvp.PROFILE);
        onHideShowFab();
        getPresenter().onModuleChanged(getSupportFragmentManager(), navType);
    }

    @Override public void onOpenDrawer() {
        if (drawerLayout != null && !drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override public void onCloseDrawer() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override public void onOpenSettings() {

    }

    @Override public void onHideShowFab() {
        if (navType == MainMvp.GISTS) {
            fab.show();
        } else {
            fab.hide();
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
            navigationView.setNavigationItemSelectedListener(getPresenter());
            Typeface myTypeface = TypeFaceHelper.getTypeface();
            bottomNavigation.setDefaultTypeface(myTypeface);
            bottomNavigation.setOnMenuItemClickListener(getPresenter());
            if (savedInstanceState == null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, FeedsView.newInstance(), FeedsView.TAG)
                        .commit();
                bottomNavigation.setDefaultSelectedIndex(0);
            }
            LoginModel userModel = LoginModel.getUser();
            if (userModel != null) {
                View view = navigationView.getHeaderView(0);
                if (view != null) {
                    ((AvatarLayout) view.findViewById(R.id.avatarLayout)).setUrl(userModel.getAvatarUrl(), userModel.getLogin());
                    ((TextView) view.findViewById(R.id.username)).setText(userModel.getName());
                    ((TextView) view.findViewById(R.id.email)).setText(userModel.getLogin());
                }
            }
        }
    }
}
