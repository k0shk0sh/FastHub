package com.fastaccess.ui.modules.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.evernote.android.state.State;
import com.fastaccess.R;
import com.fastaccess.data.dao.FragmentPagerAdapterModel;
import com.fastaccess.data.dao.TabsCountStateModel;
import com.fastaccess.data.dao.model.SearchHistory;
import com.fastaccess.helper.AnimHelper;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.ui.adapter.FragmentsPagerAdapter;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.widgets.FontAutoCompleteEditText;
import com.fastaccess.ui.widgets.ForegroundImageView;
import com.fastaccess.ui.widgets.ViewPagerView;

import java.text.NumberFormat;
import java.util.HashSet;
import java.util.LinkedHashSet;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnTextChanged;

/**
 * Created by Kosh on 08 Dec 2016, 8:22 PM
 */

public class SearchActivity extends BaseActivity<SearchMvp.View, SearchPresenter> implements SearchMvp.View {

    @BindView(R.id.searchEditText) FontAutoCompleteEditText searchEditText;
    @BindView(R.id.clear) ForegroundImageView clear;
    @BindView(R.id.tabs) TabLayout tabs;
    @BindView(R.id.appbar) AppBarLayout appbar;
    @BindView(R.id.pager) ViewPagerView pager;
    @State HashSet<TabsCountStateModel> tabsCountSet = new LinkedHashSet<>();

    private NumberFormat numberFormat = NumberFormat.getNumberInstance();
    private ArrayAdapter<SearchHistory> adapter;


    public static Intent getIntent(@NonNull Context context, @Nullable String query) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra("search", query);
        return intent;
    }

    @OnTextChanged(value = R.id.searchEditText, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void onTextChange(Editable s) {
        String text = s.toString();
        if (text.length() == 0) {
            AnimHelper.animateVisibility(clear, false);
        } else {
            AnimHelper.animateVisibility(clear, true);
        }
    }

    @OnClick(R.id.search) void onSearchClicked() {
        getPresenter().onSearchClicked(pager, searchEditText);
    }

    @OnEditorAction(R.id.searchEditText) boolean onEditor() {
        onSearchClicked();
        return true;
    }

    @OnClick(value = {R.id.clear}) void onClear(View view) {
        if (view.getId() == R.id.clear) {
            searchEditText.setText("");
        }
    }

    @Override protected int layout() {
        return R.layout.search_layout;
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

    @NonNull @Override public SearchPresenter providePresenter() {
        return new SearchPresenter();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        pager.setAdapter(new FragmentsPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapterModel.buildForSearch(this)));
        tabs.setupWithViewPager(pager);
        searchEditText.setAdapter(getAdapter());
        searchEditText.setOnItemClickListener((parent, view, position, id) -> getPresenter().onSearchClicked(pager, searchEditText));
        if (!tabsCountSet.isEmpty()) {
            setupTab();
        }
        if (savedInstanceState == null && getIntent() != null) {
            if (getIntent().hasExtra("search")) {
                searchEditText.setText(getIntent().getStringExtra("search"));
                getPresenter().onSearchClicked(pager, searchEditText);
            }
        }
        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager) {
            @Override public void onTabReselected(TabLayout.Tab tab) {
                super.onTabReselected(tab);
                onScrollTop(tab.getPosition());
            }
        });
    }

    @Override public void onNotifyAdapter(@Nullable SearchHistory query) {
        if (query == null) getAdapter().notifyDataSetChanged();
        else getAdapter().add(query);
    }

    @Override public void onSetCount(int count, int index) {
        TabsCountStateModel model = new TabsCountStateModel();
        model.setCount(count);
        model.setTabIndex(index);
        tabsCountSet.add(model);
        TextView textView = ViewHelper.getTabTextView(tabs, index);
        if (index == 0) {
            textView.setText(String.format("%s(%s)", getString(R.string.repos), numberFormat.format(count)));
        } else if (index == 1) {
            textView.setText(String.format("%s(%s)", getString(R.string.users), numberFormat.format(count)));
        } else if (index == 2) {
            textView.setText(String.format("%s(%s)", getString(R.string.issues), numberFormat.format(count)));
        } else if (index == 3) {
            textView.setText(String.format("%s(%s)", getString(R.string.code), numberFormat.format(count)));
        }
    }

    @Override public void onScrollTop(int index) {
        if (pager == null || pager.getAdapter() == null) return;
        Fragment fragment = (BaseFragment) pager.getAdapter().instantiateItem(pager, index);
        if (fragment instanceof BaseFragment) {
            ((BaseFragment) fragment).onScrollTop(index);
        }
    }

    private ArrayAdapter<SearchHistory> getAdapter() {
        if (adapter == null) adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getPresenter().getHints());
        return adapter;
    }

    private void setupTab() {
        for (TabsCountStateModel model : tabsCountSet) {
            int index = model.getTabIndex();
            int count = model.getCount();
            TextView textView = ViewHelper.getTabTextView(tabs, index);
            if (index == 0) {
                textView.setText(String.format("%s(%s)", getString(R.string.repos), numberFormat.format(count)));
            } else if (index == 1) {
                textView.setText(String.format("%s(%s)", getString(R.string.users), numberFormat.format(count)));
            } else if (index == 2) {
                textView.setText(String.format("%s(%s)", getString(R.string.issues), numberFormat.format(count)));
            } else if (index == 3) {
                textView.setText(String.format("%s(%s)", getString(R.string.code), numberFormat.format(count)));
            }
        }
    }
}
