package com.fastaccess.ui.modules.notification;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import com.fastaccess.R;
import com.fastaccess.data.dao.FragmentPagerAdapterModel;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.ui.adapter.FragmentsPagerAdapter;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.main.MainActivity;
import com.fastaccess.ui.modules.notification.all.AllNotificationsFragment;
import com.fastaccess.ui.modules.notification.unread.UnreadNotificationsFragment;
import com.fastaccess.ui.widgets.ViewPagerView;

import net.grandcentrix.thirtyinch.TiPresenter;

import butterknife.BindView;

/**
 * Created by Kosh on 27 Feb 2017, 12:36 PM
 */

public class NotificationActivity extends BaseActivity {

    @BindView(R.id.tabs) TabLayout tabs;
    @BindView(R.id.notificationContainer)
    ViewPagerView pager;

    @Override protected int layout() {
        return R.layout.notification_activity_layout;
    }

    @Override protected boolean isTransparent() {
        return true;
    }

    @Override protected boolean canBack() {
        return true;
    }

    @Override protected boolean isSecured() {
        return false;
    }

    @NonNull @Override public TiPresenter providePresenter() {
        return new BasePresenter();
    }

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppHelper.cancelNotification(this);
        onSelectNotifications();
        setupTabs(savedInstanceState);
    }

    @Override public void onBackPressed() {
        if (isTaskRoot()) {
            startActivity(new Intent(this, MainActivity.class));
        }
        super.onBackPressed();
    }

    private void setupTabs(Bundle savedInstanceState) {
        TabLayout.Tab tab1 = getTab(R.string.unread);
        TabLayout.Tab tab2 = getTab(R.string.all);
        tabs.addTab(tab1);
        tabs.addTab(tab2);
        pager.setAdapter(new FragmentsPagerAdapter(getSupportFragmentManager(),
                FragmentPagerAdapterModel.buildForNotifications(this)));
        tabs.setupWithViewPager(pager);
        /*if (savedInstanceState == null) {
            replaceWithAll(0);
        }
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (AppHelper.getFragmentByTag(getSupportFragmentManager(), String.valueOf(position)) == null) {
                    replaceWithAll(position);
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override public void onTabReselected(TabLayout.Tab tab) {

            }
        });*/
    }

    private void replaceWithAll(int position) {
        Fragment fragment;
        if (position == 0) {
            fragment = new UnreadNotificationsFragment();
        } else {
            fragment = new AllNotificationsFragment();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment, String.valueOf(position))
                .commit();
    }

    private TabLayout.Tab getTab(int titleId) {
        return tabs.newTab().setText(titleId);
    }
}
