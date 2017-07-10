package com.fastaccess.ui.modules.notification;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;

import com.fastaccess.R;
import com.fastaccess.data.dao.FragmentPagerAdapterModel;
import com.fastaccess.data.dao.GroupedNotificationModel;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.ui.adapter.FragmentsPagerAdapter;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.main.MainActivity;
import com.fastaccess.ui.modules.notification.all.AllNotificationsFragment;
import com.fastaccess.ui.modules.notification.callback.OnNotificationChangedListener;
import com.fastaccess.ui.modules.notification.unread.UnreadNotificationsFragment;
import com.fastaccess.ui.widgets.ViewPagerView;

import net.grandcentrix.thirtyinch.TiPresenter;

import butterknife.BindView;

/**
 * Created by Kosh on 27 Feb 2017, 12:36 PM
 */

public class NotificationActivity extends BaseActivity implements OnNotificationChangedListener {

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
        setupTabs();
        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager) {
            @Override public void onTabReselected(TabLayout.Tab tab) {
                super.onTabReselected(tab);
                onScrollTop(tab.getPosition());
            }
        });
    }

    @Override public void onScrollTop(int index) {
        if (pager == null || pager.getAdapter() == null) return;
        Fragment fragment = (BaseFragment) pager.getAdapter().instantiateItem(pager, index);
        if (fragment instanceof BaseFragment) {
            ((BaseFragment) fragment).onScrollTop(index);
        }
    }

    @Override public void onBackPressed() {
        if (isTaskRoot()) {
            startActivity(new Intent(this, MainActivity.class));
        }
        super.onBackPressed();
    }

    @Override public void onNotificationChanged(@NonNull GroupedNotificationModel notification, int index) {
        if (pager != null && pager.getAdapter() != null) {
            if (index == 0) {
                UnreadNotificationsFragment fragment = (UnreadNotificationsFragment) pager.getAdapter().instantiateItem(pager, 0);
                fragment.onNotifyNotificationChanged(notification);
            } else {
                AllNotificationsFragment fragment = (AllNotificationsFragment) pager.getAdapter().instantiateItem(pager, 1);
                fragment.onNotifyNotificationChanged(notification);
            }
        }
    }

    private void setupTabs() {
        pager.setAdapter(new FragmentsPagerAdapter(getSupportFragmentManager(),
                FragmentPagerAdapterModel.buildForNotifications(this)));
        tabs.setupWithViewPager(pager);
    }
}
