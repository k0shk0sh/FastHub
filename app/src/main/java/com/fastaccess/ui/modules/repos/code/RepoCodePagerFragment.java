package com.fastaccess.ui.modules.repos.code;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.annimon.stream.Objects;
import com.annimon.stream.Stream;
import com.evernote.android.state.State;
import com.fastaccess.R;
import com.fastaccess.data.dao.FragmentPagerAdapterModel;
import com.fastaccess.data.dao.TabsCountStateModel;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.ui.adapter.FragmentsPagerAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.repos.code.files.paths.RepoFilePathFragment;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.ViewPagerView;

import java.util.HashSet;

import butterknife.BindView;

/**
 * Created by Kosh on 31 Dec 2016, 1:36 AM
 */

public class RepoCodePagerFragment extends BaseFragment<RepoCodePagerMvp.View, RepoCodePagerPresenter> implements RepoCodePagerMvp.View {
    public static final String TAG = RepoCodePagerFragment.class.getSimpleName();

    @BindView(R.id.tabs) TabLayout tabs;
    @BindView(R.id.pager) ViewPagerView pager;
    @State HashSet<TabsCountStateModel> counts = new HashSet<>();

    public static RepoCodePagerFragment newInstance(@NonNull String repoId, @NonNull String login,
                                                    @NonNull String htmlLink, @NonNull String url, @NonNull String defaultBranch) {
        RepoCodePagerFragment view = new RepoCodePagerFragment();
        view.setArguments(Bundler.start()
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.EXTRA_TWO, url)
                .put(BundleConstant.EXTRA_THREE, defaultBranch)
                .put(BundleConstant.EXTRA_FOUR, htmlLink)
                .end());
        return view;
    }

    @Override protected int fragmentLayout() {
        return R.layout.tabbed_viewpager;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            String repoId = getArguments().getString(BundleConstant.ID);
            String login = getArguments().getString(BundleConstant.EXTRA);
            String url = getArguments().getString(BundleConstant.EXTRA_TWO);
            String htmlUrl = getArguments().getString(BundleConstant.EXTRA_FOUR);
            String defaultBranch = getArguments().getString(BundleConstant.EXTRA_THREE);
            if (InputHelper.isEmpty(repoId) || InputHelper.isEmpty(login) || InputHelper.isEmpty(url) || InputHelper.isEmpty(htmlUrl)) {
                return;
            }
            pager.setAdapter(new FragmentsPagerAdapter(getChildFragmentManager(),
                    FragmentPagerAdapterModel.buildForRepoCode(getContext(), repoId, login, url,
                            Objects.toString(defaultBranch, "master"), htmlUrl)));
            tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
            tabs.setupWithViewPager(pager);
        }
        if (savedInstanceState != null && !counts.isEmpty()) {
            Stream.of(counts).forEach(this::updateCount);
        }
        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager) {
            @Override public void onTabReselected(TabLayout.Tab tab) {
                super.onTabReselected(tab);
                onScrollTop(tab.getPosition());
            }
        });
    }

    @Override public void onScrollTop(int index) {
        if (pager == null || pager.getAdapter() == null) return;
        Fragment fragment = (BaseFragment) pager.getAdapter().instantiateItem(pager, index);
        if (fragment instanceof BaseFragment) {
            ((BaseFragment) fragment).onScrollTop(index);
        }
    }

    @NonNull @Override public RepoCodePagerPresenter providePresenter() {
        return new RepoCodePagerPresenter();
    }

    @Override public boolean canPressBack() {
        if (pager.getCurrentItem() != 1) return true;
        RepoFilePathFragment pathView = (RepoFilePathFragment) pager.getAdapter().instantiateItem(pager, 1);
        return pathView == null || pathView.canPressBack();
    }

    @Override public void onBackPressed() {
        if (pager != null && pager.getAdapter() != null) {
            RepoFilePathFragment pathView = (RepoFilePathFragment) pager.getAdapter().instantiateItem(pager, 1);
            if (pathView != null) {
                pathView.onBackPressed();
            }
        }
    }

    @Override public void onSetBadge(int tabIndex, int count) {
        TabsCountStateModel model = new TabsCountStateModel();
        model.setTabIndex(tabIndex);
        model.setCount(count);
        counts.add(model);
        if (tabs != null) {
            updateCount(model);
        }
    }

    private void updateCount(@NonNull TabsCountStateModel model) {
        TextView tv = ViewHelper.getTabTextView(tabs, model.getTabIndex());
        tv.setText(SpannableBuilder.builder()
                .append(getString(R.string.commits))
                .append("   ")
                .append("(")
                .bold(String.valueOf(model.getCount()))
                .append(")"));
    }
}
