package com.fastaccess.ui.modules.profile.org.teams.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;

import com.fastaccess.R;
import com.fastaccess.data.dao.FragmentPagerAdapterModel;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.ui.adapter.FragmentsPagerAdapter;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.widgets.ViewPagerView;

import net.grandcentrix.thirtyinch.TiPresenter;

import butterknife.BindView;
import icepick.State;

/**
 * Created by Kosh on 03 Apr 2017, 10:08 PM
 */

public class TeamPagerActivity extends BaseActivity {

    @State long id;
    @State String name;

    @BindView(R.id.tabs) TabLayout tabs;
    @BindView(R.id.tabbedPager) ViewPagerView pager;

    public static void startActivity(@NonNull Context context, long id, @NonNull String name) {
        Intent intent = new Intent(context, TeamPagerActivity.class);
        intent.putExtras(Bundler
                .start()
                .put(BundleConstant.ID, id)
                .put(BundleConstant.EXTRA, name)
                .end());
        context.startActivity(intent);
    }

    @Override protected int layout() {
        return R.layout.tabbed_pager_layout;
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
            id = getIntent().getExtras().getLong(BundleConstant.ID);
            name = getIntent().getExtras().getString(BundleConstant.EXTRA);
        }
        setTitle(name);
        if (id <= 0) {
            finish();
            return;
        }
        FragmentsPagerAdapter adapter = new FragmentsPagerAdapter(getSupportFragmentManager(),
                FragmentPagerAdapterModel.buildForTeam(this, id));
        pager.setAdapter(adapter);
        tabs.setTabGravity(TabLayout.GRAVITY_FILL);
        tabs.setTabMode(TabLayout.MODE_FIXED);
        tabs.setupWithViewPager(pager);
        tabs.setPaddingRelative(0, 0, 0, 0);
    }
}
