package com.fastaccess.ui.modules.main.pullrequests.pager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.fastaccess.R;
import com.fastaccess.data.dao.FragmentPagerAdapterModel;
import com.fastaccess.data.dao.TabsCountStateModel;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.ui.adapter.FragmentsPagerAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.ViewPagerView;

import java.util.HashSet;

import butterknife.BindView;
import icepick.State;

/**
 * Created by Kosh on 26 Mar 2017, 12:14 AM
 */

public class MyPullsPagerFragment extends BaseFragment<MyPullsPagerMvp.View, MyPullsPagerPresenter> implements MyPullsPagerMvp.View {

    public static final String TAG = MyPullsPagerFragment.class.getSimpleName();

    @BindView(R.id.tabs) TabLayout tabs;
    @BindView(R.id.pager) ViewPagerView pager;
    @State HashSet<TabsCountStateModel> counts = new HashSet<>();

    public static MyPullsPagerFragment newInstance() {
        return new MyPullsPagerFragment();
    }

    @Override protected int fragmentLayout() {
        return R.layout.centered_tabbed_viewpager;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        pager.setAdapter(new FragmentsPagerAdapter(getChildFragmentManager(), FragmentPagerAdapterModel.buildForMyPulls(getContext())));
        tabs.setupWithViewPager(pager);
        if (savedInstanceState != null && !counts.isEmpty()) {
            Stream.of(counts).forEach(this::updateCount);
        }
    }

    @NonNull @Override public MyPullsPagerPresenter providePresenter() {
        return new MyPullsPagerPresenter();
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
                .append(model.getTabIndex() == 0 ? getString(R.string.opened) : getString(R.string.closed))
                .append("   ")
                .append("(")
                .bold(String.valueOf(model.getCount()))
                .append(")"));
    }
}
