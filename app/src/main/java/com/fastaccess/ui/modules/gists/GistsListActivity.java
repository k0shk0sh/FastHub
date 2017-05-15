package com.fastaccess.ui.modules.gists;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;

import com.fastaccess.R;
import com.fastaccess.data.dao.FragmentPagerAdapterModel;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.ui.adapter.FragmentsPagerAdapter;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.gists.create.CreateGistActivity;
import com.fastaccess.ui.modules.profile.gists.ProfileGistsFragment;
import com.fastaccess.ui.widgets.ViewPagerView;

import net.grandcentrix.thirtyinch.TiPresenter;

import butterknife.BindView;
import butterknife.OnClick;
import icepick.State;

/**
 * Created by Kosh on 25 Mar 2017, 11:28 PM
 */

public class GistsListActivity extends BaseActivity {

    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.gistsContainer)
    ViewPagerView pager;

    public static void startActivity(@NonNull Context context, boolean myGists) {
        Intent intent = new Intent(context, GistsListActivity.class);
        intent.putExtras(Bundler.start().put(BundleConstant.EXTRA, myGists).end());
        context.startActivity(intent);
    }

    @State boolean myGists;

    @BindView(R.id.fab) FloatingActionButton fab;

    @Override protected int layout() {
        return R.layout.gists_activity_layout;
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

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.gists);
        setupTabs();
        fab.show();
    }

    private void setupTabs() {
        TabLayout.Tab tab1 = getTab(R.string.my_gists);
        TabLayout.Tab tab2 = getTab(R.string.public_gists);
        tabs.addTab(tab1);
        tabs.addTab(tab2);
        pager.setAdapter(new FragmentsPagerAdapter(getSupportFragmentManager(),
                FragmentPagerAdapterModel.buildForGists(this)));
        tabs.setupWithViewPager(pager);
    }

    private TabLayout.Tab getTab(int titleId) {
        return tabs.newTab().setText(titleId);
    }

    @OnClick(R.id.fab) public void onViewClicked() {
        ActivityHelper.startReveal(this, new Intent(this, CreateGistActivity.class), fab);
    }
}
