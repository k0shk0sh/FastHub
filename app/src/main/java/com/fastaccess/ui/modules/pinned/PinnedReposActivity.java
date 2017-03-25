package com.fastaccess.ui.modules.pinned;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.R;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import net.grandcentrix.thirtyinch.TiPresenter;

/**
 * Created by Kosh on 25 Mar 2017, 11:14 PM
 */

public class PinnedReposActivity extends BaseActivity {

    public static void startActivity(@NonNull Context context) {
        context.startActivity(new Intent(context, PinnedReposActivity.class));
    }

    @Override protected int layout() {
        return R.layout.toolbar_activity_layout;
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

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, PinnedReposView.newInstance(), PinnedReposView.TAG)
                    .commit();
        }
    }
}
