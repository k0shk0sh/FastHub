package com.fastaccess.ui.modules.repos.pull_requests;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.evernote.android.state.State;
import com.fastaccess.R;
import com.fastaccess.data.dao.FragmentPagerAdapterModel;
import com.fastaccess.data.dao.TabsCountStateModel;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.ui.adapter.FragmentsPagerAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.repos.RepoPagerMvp;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.ViewPagerView;

import java.util.HashSet;

import butterknife.BindView;

/**
 * Created by Kosh on 31 Dec 2016, 1:36 AM
 */

public class RepoPullRequestPagerFragment extends BaseFragment<RepoPullRequestPagerMvp.View, RepoPullRequestPagerPresenter> implements
        RepoPullRequestPagerMvp.View {

    public static final String TAG = RepoPullRequestPagerFragment.class.getSimpleName();

    @BindView(R.id.tabs) TabLayout tabs;
    @BindView(R.id.pager) ViewPagerView pager;
    @State HashSet<TabsCountStateModel> counts = new HashSet<>();
    private RepoPagerMvp.View repoCallback;

    public static RepoPullRequestPagerFragment newInstance(@NonNull String repoId, @NonNull String login) {
        RepoPullRequestPagerFragment view = new RepoPullRequestPagerFragment();
        view.setArguments(Bundler.start()
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA, login)
                .end());
        return view;
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof RepoPagerMvp.View) {
            repoCallback = (RepoPagerMvp.View) getParentFragment();
        } else if (context instanceof RepoPagerMvp.View) {
            repoCallback = (RepoPagerMvp.View) context;
        }
    }

    @Override public void onDetach() {
        repoCallback = null;
        super.onDetach();
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

    @NonNull @Override public RepoPullRequestPagerPresenter providePresenter() {
        return new RepoPullRequestPagerPresenter();
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

    @Override public int getCurrentItem() {
        return pager != null ? pager.getCurrentItem() : 0;
    }

    @Override public void onScrolled(boolean isUp) {
        if (repoCallback != null) repoCallback.onScrolled(isUp);
    }

    private void updateCount(@NonNull TabsCountStateModel model) {
        TextView tv = ViewHelper.getTabTextView(tabs, model.getTabIndex());
        tv.setText(SpannableBuilder.builder()
                .append(model.getTabIndex() == 0 ? getString(R.string.opened) : getString(R.string.closed))
                .append("   ")
                .append("(")
                .bold(String.valueOf(model.getCount()))
                .append(")"));
    }
}
