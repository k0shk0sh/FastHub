package com.fastaccess.ui.modules.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.TabLayout;

import com.fastaccess.R;
import com.fastaccess.data.dao.FragmentPagerAdapterModel;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.ui.adapter.FragmentsPagerAdapter;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.widgets.ViewPagerView;

import butterknife.BindView;
import icepick.State;

/**
 * Created by Kosh on 03 Dec 2016, 8:00 AM
 */

public class UserPagerView extends BaseActivity<UserPagerMvp.View, UserPagerPresenter> implements UserPagerMvp.View {


    @BindView(R.id.tabs) TabLayout tabs;
    @BindView(R.id.pager) ViewPagerView pager;
    @State String login;

    public static void startActivity(@NonNull Context context, @NonNull String login) {
        context.startActivity(createIntent(context, login));
    }

    public static Intent createIntent(@NonNull Context context, @NonNull String login) {
        Intent intent = new Intent(context, UserPagerView.class);
        intent.putExtras(Bundler.start().put(BundleConstant.EXTRA, login).end());
        return intent;
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

    @NonNull @Override public UserPagerPresenter providePresenter() {
        return new UserPagerPresenter();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            login = getIntent().getExtras().getString(BundleConstant.EXTRA);
        }
        if (InputHelper.isEmpty(login)) {
            finish();
            return;
        }
        setTitle(login);
        FragmentsPagerAdapter adapter = new FragmentsPagerAdapter(getSupportFragmentManager(),
                FragmentPagerAdapterModel.buildForProfile(this, login));
        tabs.setTabGravity(TabLayout.GRAVITY_FILL);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);
    }

    @Override public void showProgress(@StringRes int resId) {

    }

    @Override public void hideProgress() {
        super.hideProgress();
    }

    @Override public void onNavigateToFollowers() {
        pager.setCurrentItem(4);
    }

    @Override public void onNavigateToFollowing() {
        pager.setCurrentItem(5);
    }
}
