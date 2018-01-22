package com.fastaccess.ui.modules.main.issues.pager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.MenuInflater;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.evernote.android.state.State;
import com.fastaccess.R;
import com.fastaccess.data.dao.FragmentPagerAdapterModel;
import com.fastaccess.data.dao.TabsCountStateModel;
import com.fastaccess.data.dao.types.IssueState;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.ui.adapter.FragmentsPagerAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.main.issues.MyIssuesFragment;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.ViewPagerView;

import java.util.HashSet;

import butterknife.BindView;

/**
 * Created by Kosh on 26 Mar 2017, 12:14 AM
 */

public class MyIssuesPagerFragment extends BaseFragment<MyIssuesPagerMvp.View, MyIssuesPagerPresenter> implements MyIssuesPagerMvp.View {

    public static final String TAG = MyIssuesPagerFragment.class.getSimpleName();

    @BindView(R.id.tabs) TabLayout tabs;
    @BindView(R.id.pager) ViewPagerView pager;
    @State HashSet<TabsCountStateModel> counts = new HashSet<>();

    public static MyIssuesPagerFragment newInstance() {
        return new MyIssuesPagerFragment();
    }

    @Override protected int fragmentLayout() {
        return R.layout.tabbed_viewpager;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        FragmentsPagerAdapter adapter = new FragmentsPagerAdapter(getChildFragmentManager(),
                FragmentPagerAdapterModel.buildForMyIssues(getContext()));
        pager.setAdapter(adapter);
        //noinspection deprecation
        tabs.setTabsFromPagerAdapter(adapter);
        tabs.setTabGravity(TabLayout.GRAVITY_FILL);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        if (savedInstanceState == null) {
            tabs.getTabAt(0).select();
        }
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override public void onPageSelected(int position) {
                super.onPageSelected(position);
                selectTab(position, true);
            }
        });
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getTag() == null) {
                    int position = tab.getPosition();
                    selectTab(position, false);
                }
                tab.setTag(null);
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}

            @Override public void onTabReselected(TabLayout.Tab tab) {
                selectTab(tab.getPosition(), false);
            }
        });
        if (savedInstanceState != null && !counts.isEmpty()) {
            Stream.of(counts).forEach(this::updateCount);
        }
    }

    @NonNull @Override public MyIssuesPagerPresenter providePresenter() {
        return new MyIssuesPagerPresenter();
    }

    @Override public void onSetBadge(int tabIndex, int count) {
        TabsCountStateModel model = getModelAtIndex(tabIndex);
        if (model == null) {
            model = new TabsCountStateModel();
        }
        model.setTabIndex(tabIndex);
        model.setCount(count);
        boolean removed = counts.remove(model);
        counts.add(model);
        if (tabs != null) {
            updateCount(model);
        }
    }

    @Nullable private TabsCountStateModel getModelAtIndex(int index) {
        return Stream.of(counts)
                .filter(model -> model.getTabIndex() == index)
                .findFirst()
                .orElse(null);
    }

    @Override public void onScrollTop(int index) {
        super.onScrollTop(index);
        if (pager != null && pager.getAdapter() != null) {
            MyIssuesFragment myIssuesFragment = (MyIssuesFragment) pager.getAdapter().instantiateItem(pager, pager.getCurrentItem());
            if (myIssuesFragment != null) {
                myIssuesFragment.onScrollTop(0);
            }
        }
    }

    private void selectTab(int position, boolean fromViewPager) {
        if (!fromViewPager) {
            onShowFilterMenu(getModelAtIndex(position), ViewHelper.getTabTextView(tabs, position));
            pager.setCurrentItem(position);
        } else {
            TabLayout.Tab tab = tabs.getTabAt(position);
            if (tab != null) {
                tab.setTag("hello");
                if (!tab.isSelected()) tab.select();
            }
        }
    }

    private void updateCount(@NonNull TabsCountStateModel model) {
        TextView tv = ViewHelper.getTabTextView(tabs, model.getTabIndex());
        String title = getString(R.string.created);
        switch (model.getTabIndex()) {
            case 0:
                title = getString(R.string.created);
                break;
            case 1:
                title = getString(R.string.assigned);
                break;
            case 2:
                title = getString(R.string.mentioned);
                break;
            case 3:
                title = getString(R.string.participated);
        }
        updateDrawable(model, tv);
        tv.setText(SpannableBuilder.builder()
                .append(title)
                .append("   ")
                .append("(")
                .bold(String.valueOf(model.getCount()))
                .append(")"));
    }

    private void onShowFilterMenu(@Nullable TabsCountStateModel model, TextView tv) {
        if (model == null) return;
        PopupMenu popup = new PopupMenu(getContext(), tv);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.filter_issue_state_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            if (pager == null || pager.getAdapter() == null) return false;
            MyIssuesFragment myIssuesFragment = (MyIssuesFragment) pager.getAdapter().instantiateItem(pager, model.getTabIndex());
            if (myIssuesFragment == null) return false;
            switch (item.getItemId()) {
                case R.id.opened:
                    counts.remove(model);
                    model.setDrawableId(R.drawable.ic_issue_opened_small);
                    counts.add(model);
                    updateDrawable(model, tv);
                    myIssuesFragment.onFilterIssue(IssueState.open);
                    return true;
                case R.id.closed:
                    counts.remove(model);
                    model.setDrawableId(R.drawable.ic_issue_closed_small);
                    counts.add(model);
                    updateDrawable(model, tv);
                    myIssuesFragment.onFilterIssue(IssueState.closed);
                    return true;
            }
            return false;
        });
        popup.show();
    }

    private void updateDrawable(@NonNull TabsCountStateModel model, TextView tv) {
        model.setDrawableId(model.getDrawableId() == 0 ? R.drawable.ic_issue_opened_small : model.getDrawableId());
        tv.setCompoundDrawablePadding(16);
        tv.setCompoundDrawablesWithIntrinsicBounds(model.getDrawableId(), 0, R.drawable.ic_arrow_drop_down, 0);
    }
}
