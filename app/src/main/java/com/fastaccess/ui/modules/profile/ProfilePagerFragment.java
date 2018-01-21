package com.fastaccess.ui.modules.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.FragmentPagerAdapterModel;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.Logger;
import com.fastaccess.ui.adapter.FragmentsPagerAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.widgets.ViewPagerView;

import butterknife.BindView;

/**
 * Created by Kosh on 03 Dec 2016, 8:00 AM
 */

public class ProfilePagerFragment extends BaseFragment<ProfilePagerMvp.View, ProfilePagerPresenter> implements ProfilePagerMvp.View {

    public static final String TAG = ProfilePagerFragment.class.getSimpleName();

    @BindView(R.id.tabs) TabLayout tabs;
    @BindView(R.id.pager) ViewPagerView pager;

    public static ProfilePagerFragment newInstance(@NonNull String login) {
        ProfilePagerFragment profileView = new ProfilePagerFragment();
        profileView.setArguments(Bundler.start().put(BundleConstant.EXTRA, login).end());
        return profileView;
    }

    @Override protected int fragmentLayout() {
        return R.layout.tabbed_viewpager;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() == null) {
            throw new RuntimeException("Bundle is null?");
        }
        String login = getArguments().getString(BundleConstant.EXTRA);
        if (login == null) {
            throw new RuntimeException("user is null?");
        }
        FragmentsPagerAdapter adapter = new FragmentsPagerAdapter(getChildFragmentManager(),
                FragmentPagerAdapterModel.buildForProfile(getContext(), login));
        tabs.setTabGravity(TabLayout.GRAVITY_FILL);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);
    }

    @NonNull @Override public ProfilePagerPresenter providePresenter() {
        return new ProfilePagerPresenter();
    }

    @Override public void onNavigateToFollowers() {
        pager.setCurrentItem(4);
    }

    @Override public void onNavigateToFollowing() {
        pager.setCurrentItem(5);
    }

    @Override public void onCheckType(boolean isOrg) {}
}
