package com.fastaccess.ui.modules.repos.pull_requests;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.FragmentPagerAdapterModel;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.ui.adapter.FragmentsPagerAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.widgets.ViewPagerView;

import butterknife.BindView;

/**
 * Created by Kosh on 31 Dec 2016, 1:36 AM
 */

public class RepoPullRequestPagerView extends BaseFragment<RepoPullRequestPagerMvp.View, RepoPullRequestPagerPresenter> implements
        RepoPullRequestPagerMvp.View {

    public static final String TAG = RepoPullRequestPagerView.class.getSimpleName();

    @BindView(R.id.tabs) TabLayout tabs;
    @BindView(R.id.pager) ViewPagerView pager;

    public static RepoPullRequestPagerView newInstance(@NonNull String repoId, @NonNull String login) {
        RepoPullRequestPagerView view = new RepoPullRequestPagerView();
        view.setArguments(Bundler.start()
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA, login)
                .end());
        return view;
    }

    @Override protected int fragmentLayout() {
        return R.layout.centered_tabbed_viewpager;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        String repoId = getArguments().getString(BundleConstant.ID);
        String login = getArguments().getString(BundleConstant.EXTRA);
        if (login == null || repoId == null) throw new NullPointerException("repoId || login is null???");
        pager.setAdapter(new FragmentsPagerAdapter(getChildFragmentManager(),
                FragmentPagerAdapterModel.buildForRepoPullRequest(getContext(), login, repoId)));
        tabs.setupWithViewPager(pager);
    }

    @NonNull @Override public RepoPullRequestPagerPresenter providePresenter() {
        return new RepoPullRequestPagerPresenter();
    }
}
