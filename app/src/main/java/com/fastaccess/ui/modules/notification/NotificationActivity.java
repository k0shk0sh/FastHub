package com.fastaccess.ui.modules.notification;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.R;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.main.MainActivity;

import net.grandcentrix.thirtyinch.TiPresenter;

/**
 * Created by Kosh on 27 Feb 2017, 12:36 PM
 */

public class NotificationActivity extends BaseActivity {

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
    }

    @Override public void onBackPressed() {
        if (isTaskRoot()) {
            startActivity(new Intent(this, MainActivity.class));
        }
        super.onBackPressed();
    }
}
