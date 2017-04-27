package com.fastaccess.ui.modules.search;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;

import com.fastaccess.R;
import com.fastaccess.data.dao.FragmentPagerAdapterModel;
import com.fastaccess.data.dao.TabsCountStateModel;
import com.fastaccess.data.dao.model.SearchHistory;
import com.fastaccess.helper.AnimHelper;
import com.fastaccess.ui.adapter.FragmentsPagerAdapter;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.widgets.FontAutoCompleteEditText;
import com.fastaccess.ui.widgets.ForegroundImageView;
import com.fastaccess.ui.widgets.ViewPagerView;

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

//    @State Set<TabsCountStateModel> tabsCountSet = new LinkedHashSet<>();

    private ArrayAdapter<SearchHistory> adapter;

    @OnTextChanged(value = R.id.searchEditText, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void onTextChange(Editable s) {
        String text = s.toString();
        if (text.length() == 0) {
            AnimHelper.animateVisibility(clear, false);
        } else {
            AnimHelper.animateVisibility(clear, true);
        }
    }

    @OnEditorAction(R.id.searchEditText) boolean onEditor(int actionId, KeyEvent keyEvent) {
        if (keyEvent != null && keyEvent.getAction() == KeyEvent.KEYCODE_SEARCH) {
            getPresenter().onSearchClicked(pager, searchEditText);
        } else if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            getPresenter().onSearchClicked(pager, searchEditText);
        }
        return false;
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
    }

    @Override public void onNotifyAdapter(@Nullable SearchHistory query) {
        if (query == null) getAdapter().notifyDataSetChanged();
        else getAdapter().add(query);
    }

    @Override public void onSetCount(int count, int index) {
        TabsCountStateModel model = new TabsCountStateModel();
        model.setCount(count);
        model.setTabIndex(index);
//        tabsCountSet.add(model);
    }

    private ArrayAdapter<SearchHistory> getAdapter() {
        if (adapter == null) adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getPresenter().getHints());
        return adapter;
    }
}
