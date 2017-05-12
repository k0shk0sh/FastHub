package com.fastaccess.ui.modules.settings.category;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.fastaccess.R;
import com.fastaccess.ui.base.BaseActivity;

import net.grandcentrix.thirtyinch.TiPresenter;

import icepick.State;

public class SettingsCategoryActivity extends BaseActivity {

    @State String title;

    @Override protected int layout() {
        return R.layout.activity_settings_category;
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

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        if (savedInstanceState == null) {
            title = getIntent() != null ? getIntent().getStringExtra("title") : getString(R.string.settings);
            SettingsCategoryFragment settingsCategoryFragment = new SettingsCategoryFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settingsContainer, settingsCategoryFragment)
                    .commit();
        }
        setTitle(title);
    }

    @NonNull @Override public TiPresenter providePresenter() {
        return new SettingsCategoryPresenter();
    }
}
