package com.fastaccess.ui.modules.main.pullrequests.pager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.TextView;

import com.fastaccess.R;
import com.fastaccess.data.dao.FragmentPagerAdapterModel;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.ui.adapter.FragmentsPagerAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.ViewPagerView;

import butterknife.BindView;
import icepick.State;

/**
 * Created by Kosh on 26 Mar 2017, 12:14 AM
 */

public class MyPullsPagerView extends BaseFragment<MyPullsPagerMvp.View, MyPullsPagerPresenter> implements MyPullsPagerMvp.View {

    public static final String TAG = MyPullsPagerView.class.getSimpleName();

    @BindView(R.id.tabs) TabLayout tabs;
    @BindView(R.id.pager) ViewPagerView pager;
    @State int openCount = -1;
    @State int closeCount = -1;

    public static MyPullsPagerView newInstance() {
        return new MyPullsPagerView();
    }

    @Override protected int fragmentLayout() {
        return R.layout.centered_tabbed_viewpager;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        pager.setAdapter(new FragmentsPagerAdapter(getChildFragmentManager(), FragmentPagerAdapterModel.buildForMyPulls(getContext())));
        tabs.setupWithViewPager(pager);
        if (savedInstanceState != null && openCount != -1 && closeCount != -1) {
            onSetBadge(0, openCount);
            onSetBadge(1, closeCount);
        }
    }

    @NonNull @Override public MyPullsPagerPresenter providePresenter() {
        return new MyPullsPagerPresenter();
    }

    @Override public void onSetBadge(int tabIndex, int count) {
        if (tabIndex == 0) {
            openCount = count;
        } else {
            closeCount = count;
        }
        if (tabs != null) {
            TextView tv = ViewHelper.getTabTextView(tabs, tabIndex);
            tv.setText(SpannableBuilder.builder()
                    .append(tabIndex == 0 ? getString(R.string.opened) : getString(R.string.closed))
                    .append("   ")
                    .append("(")
                    .bold(String.valueOf(count))
                    .append(")"));
        }
    }
}
