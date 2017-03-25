package com.fastaccess.ui.modules.gists;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;

import com.fastaccess.R;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.gists.create.CreateGistView;

import net.grandcentrix.thirtyinch.TiPresenter;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Kosh on 25 Mar 2017, 11:28 PM
 */

public class PublicGistsActivity extends BaseActivity {

    public static void startActivity(@NonNull Context context) {
        context.startActivity(new Intent(context, PublicGistsActivity.class));
    }

    @BindView(R.id.fab) FloatingActionButton fab;

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

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, GistsView.newInstance(), GistsView.TAG)
                    .commit();
        }
        fab.show();
    }

    @OnClick(R.id.fab) public void onViewClicked() {
        startActivity(new Intent(this, CreateGistView.class));
    }
}
