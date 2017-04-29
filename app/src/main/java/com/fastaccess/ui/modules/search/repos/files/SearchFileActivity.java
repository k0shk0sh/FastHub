package com.fastaccess.ui.modules.search.repos.files;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.fastaccess.R;
import com.fastaccess.helper.AnimHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.modules.search.code.SearchCodeFragment;
import com.fastaccess.ui.widgets.FontEditText;
import com.fastaccess.ui.widgets.ForegroundImageView;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;
import butterknife.OnTouch;

public class SearchFileActivity extends BaseActivity<SearchFileMvp.View, SearchFilePresenter> implements SearchFileMvp.View {

    @BindView(R.id.searchEditText) FontEditText searchEditText;
    @BindView(R.id.clear) ForegroundImageView clear;
    @BindView(R.id.searchOptions) AppCompatSpinner searchOptions;
    private boolean onSpinnerTouched;

    private SearchCodeFragment searchCodeFragment;

    public static Intent createIntent(@NonNull Context context, @NonNull String login, @NonNull String repoId) {
        Intent intent = new Intent(context, SearchFileActivity.class);
        intent.putExtra(BundleConstant.ID, repoId);
        intent.putExtra(BundleConstant.EXTRA, login);
        return intent;
    }

    @OnTouch(R.id.searchOptions) boolean onTouch() {
        onSpinnerTouched = true;
        return false;
    }

    @OnItemSelected(R.id.searchOptions) void onOptionSelected(int position) {
        if (onSpinnerTouched) {
            onSearch();
        }
    }

    @Override protected int layout() {
        return R.layout.activity_search_file;
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

    @NonNull @Override public SearchFilePresenter providePresenter() {
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
            onSearch();
        } else if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            onSearch();
        }
        return false;
    }

    private void onSearch() {
        getPresenter().onSearchClicked(searchEditText, searchOptions.getSelectedItemPosition() == 0);
    }

    @OnClick(value = {R.id.clear}) void onClear(View view) {
        if (view.getId() == R.id.clear) {
            searchEditText.setText("");
        }
    }

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPresenter().onActivityCreated(getIntent().getExtras());
        searchCodeFragment = ((SearchCodeFragment) getSupportFragmentManager().findFragmentById(R.id.filesFragment));
    }

    @Override public void onValidSearchQuery(@NonNull String query) {
        searchCodeFragment.onSetSearchQuery(query, false);
    }
}
