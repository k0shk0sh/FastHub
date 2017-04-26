package com.fastaccess.ui.modules.search.files;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;

import com.fastaccess.R;
import com.fastaccess.data.dao.model.SearchHistory;
import com.fastaccess.helper.AnimHelper;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.modules.search.code.SearchCodeFragment;
import com.fastaccess.ui.widgets.FontAutoCompleteEditText;
import com.fastaccess.ui.widgets.ForegroundImageView;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnTextChanged;

public class SearchFileActivity extends BaseActivity<SearchFileMvp.View, SearchFilePresenter> implements SearchFileMvp.View {

    @BindView(R.id.searchEditText) FontAutoCompleteEditText searchEditText;
    @BindView(R.id.clear) ForegroundImageView clear;

    private ArrayAdapter adapter;
    private SearchCodeFragment searchCodeFragment;

    @Override
    protected int layout() {
        return R.layout.activity_search_file;
    }

    @Override
    protected boolean isTransparent() {
        return false;
    }

    @Override
    protected boolean canBack() {
        return true;
    }

    @Override
    protected boolean isSecured() {
        return false;
    }

    @NonNull
    @Override
    public SearchFilePresenter providePresenter() {
        return new SearchFilePresenter();
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

    @OnEditorAction(R.id.searchEditText) boolean onEditor(int actionId, KeyEvent keyEvent) {
        if (keyEvent != null && keyEvent.getAction() == KeyEvent.KEYCODE_SEARCH) {
            getPresenter().onSearchClicked(searchEditText, searchCodeFragment);
        } else if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            getPresenter().onSearchClicked(searchEditText, searchCodeFragment);
        }
        return false;
    }

    @OnClick(value = {R.id.clear}) void onClear(View view) {
        if (view.getId() == R.id.clear) {
            AppHelper.hideKeyboard(searchEditText);
            searchEditText.setText("");
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchEditText.setAdapter(getAdapter());
        searchEditText.setOnItemClickListener((parent, view, position, id) -> getPresenter().onSearchClicked(searchEditText, searchCodeFragment));
        getPresenter().onActivityCreated(getIntent().getExtras());
        searchCodeFragment = ((SearchCodeFragment) getSupportFragmentManager().findFragmentById(R.id.filesFragment));
    }

    @OnClick(R.id.back) public void onBackClicked() {
        onBackPressed();
    }

    @OnTextChanged(R.id.searchEditText) public void onSearchTextChanged() {

    }

    @Override
    public void onNotifyAdapter(@Nullable SearchHistory query) {
        if (query == null) getAdapter().notifyDataSetChanged();
        else getAdapter().add(query);
    }

    private ArrayAdapter<SearchHistory> getAdapter() {
        if (adapter == null) adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getPresenter().getHints());
        return adapter;
    }
}
