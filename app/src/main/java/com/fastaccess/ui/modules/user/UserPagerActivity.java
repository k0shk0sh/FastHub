package com.fastaccess.ui.modules.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;

import com.fastaccess.R;
import com.fastaccess.data.dao.FragmentPagerAdapterModel;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.ui.adapter.FragmentsPagerAdapter;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.modules.main.MainActivity;
import com.fastaccess.ui.widgets.ViewPagerView;

import butterknife.BindView;
import icepick.State;
import shortbread.Shortcut;

/**
 * Created by Kosh on 03 Dec 2016, 8:00 AM
 */

@Shortcut(id = "profile", icon = R.drawable.ic_profile_shortcut, shortLabelRes = R.string.profile, backStack = {MainActivity.class}, rank = 4)
public class UserPagerActivity extends BaseActivity<UserPagerMvp.View, UserPagerPresenter> implements UserPagerMvp.View {


    @BindView(R.id.tabs) TabLayout tabs;
    @BindView(R.id.tabbedPager) ViewPagerView pager;
    @State String login;
    @State boolean isOrg;

    public static void startActivity(@NonNull Context context, @NonNull String login) {
        startActivity(context, login, false);
    }

    public static void startActivity(@NonNull Context context, @NonNull String login, boolean isOrg) {
        context.startActivity(createIntent(context, login, isOrg));
    }

    public static Intent createIntent(@NonNull Context context, @NonNull String login) {
        return createIntent(context, login, false);
    }

    public static Intent createIntent(@NonNull Context context, @NonNull String login, boolean isOrg) {
        Intent intent = new Intent(context, UserPagerActivity.class);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.EXTRA_TYPE, isOrg)
                .end());
        return intent;
    }

    @Override protected int layout() {
        return R.layout.tabbed_pager_layout;
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

    @NonNull @Override public UserPagerPresenter providePresenter() {
        return new UserPagerPresenter();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getExtras() != null) {
                login = getIntent().getExtras().getString(BundleConstant.EXTRA);
                isOrg = getIntent().getExtras().getBoolean(BundleConstant.EXTRA_TYPE);
                if (!InputHelper.isEmpty(login) && isOrg) {
                    getPresenter().checkOrgMembership(login);
                }
            } else {
                login = Login.getUser().getLogin();
            }
        }
        if (InputHelper.isEmpty(login)) {
            finish();
            return;
        }
        setTitle(login);
        if (login.equalsIgnoreCase(Login.getUser().getLogin())) {
            selectProfile();
        }
        if (!isOrg) {
            FragmentsPagerAdapter adapter = new FragmentsPagerAdapter(getSupportFragmentManager(),
                    FragmentPagerAdapterModel.buildForProfile(this, login));
            pager.setAdapter(adapter);
            tabs.setTabGravity(TabLayout.GRAVITY_FILL);
            tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
            tabs.setupWithViewPager(pager);
        } else {
            if (getPresenter().getIsMember() == -1) {
                getPresenter().checkOrgMembership(login);
            } else {
                onInitOrg(getPresenter().isMember == 1);
            }
        }
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

    @Override public void onInitOrg(boolean isMember) {
        hideProgress();
        FragmentsPagerAdapter adapter = new FragmentsPagerAdapter(getSupportFragmentManager(),
                FragmentPagerAdapterModel.buildForOrg(this, login, isMember));
        pager.setAdapter(adapter);
        tabs.setTabGravity(TabLayout.GRAVITY_FILL);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs.setupWithViewPager(pager);
    }
}
