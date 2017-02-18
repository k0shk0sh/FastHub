package com.fastaccess.ui.modules.repos.code;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.FragmentPagerAdapterModel;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.ui.adapter.FragmentsPagerAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.widgets.ViewPagerView;

import butterknife.BindView;

/**
 * Created by Kosh on 31 Dec 2016, 1:36 AM
 */

public class RepoCodePagerView extends BaseFragment<RepoCodePagerMvp.View, RepoCodePagerPresenter> implements RepoCodePagerMvp.View {
    public static final String TAG = RepoCodePagerView.class.getSimpleName();

    @BindView(R.id.tabs) TabLayout tabs;
    @BindView(R.id.pager) ViewPagerView pager;

    public static RepoCodePagerView newInstance(@NonNull String repoId, @NonNull String login, @NonNull String htmlLink) {
        RepoCodePagerView view = new RepoCodePagerView();
        view.setArguments(Bundler.start()
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.EXTRA_TWO, htmlLink)
                .end());
        return view;
    }

    @Override protected int fragmentLayout() {
        return R.layout.tabbed_viewpager;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        String repoId = getArguments().getString(BundleConstant.ID);
        String login = getArguments().getString(BundleConstant.EXTRA);
        String htmlLink = getArguments().getString(BundleConstant.EXTRA_TWO);
        if (InputHelper.isEmpty(repoId) || InputHelper.isEmpty(login) || InputHelper.isEmpty(htmlLink)) {
            throw new NullPointerException(String.format("Failed Initializing (%s) %s %s %s", getClass().getSimpleName(), repoId, login, htmlLink));
        }
        pager.setAdapter(new FragmentsPagerAdapter(getChildFragmentManager(),
                FragmentPagerAdapterModel.buildForRepoCode(getContext(), repoId, login, htmlLink)));
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs.setupWithViewPager(pager);
    }

    @NonNull @Override public RepoCodePagerPresenter providePresenter() {
        return new RepoCodePagerPresenter();
    }
}
