package com.fastaccess.ui.modules.gists;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;

import com.fastaccess.R;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.gists.create.CreateGistView;
import com.fastaccess.ui.modules.profile.gists.ProfileGistsView;

import net.grandcentrix.thirtyinch.TiPresenter;

import butterknife.BindView;
import butterknife.OnClick;
import icepick.State;

/**
 * Created by Kosh on 25 Mar 2017, 11:28 PM
 */

public class GistsListActivity extends BaseActivity {

    public static void startActivity(@NonNull Context context, boolean myGists) {
        Intent intent = new Intent(context, GistsListActivity.class);
        intent.putExtras(Bundler.start().put(BundleConstant.EXTRA, myGists).end());
        context.startActivity(intent);
    }

    @State boolean myGists;

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
            myGists = getIntent().getExtras().getBoolean(BundleConstant.EXTRA);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, myGists ? ProfileGistsView.newInstance(Login.getUser().getLogin())
                                                             : GistsView.newInstance(), GistsView.TAG)
                    .commit();
        }
        setTitle(myGists ? R.string.my_gists : R.string.public_gists);
        fab.show();
    }

    @OnClick(R.id.fab) public void onViewClicked() {
        ActivityHelper.startReveal(this, new Intent(this, CreateGistView.class), fab);
    }
}
