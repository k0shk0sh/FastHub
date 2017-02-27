package com.fastaccess.ui.modules.notification;

import android.support.annotation.NonNull;

import com.fastaccess.R;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import net.grandcentrix.thirtyinch.TiPresenter;

/**
 * Created by Kosh on 27 Feb 2017, 12:36 PM
 */

public class NotificationActivityView extends BaseActivity {
    @Override protected int layout() {
        return R.layout.notification_activity_layout;
    }

    @Override protected boolean isTransparent() {
        return false;
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
}
