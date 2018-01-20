package com.fastaccess.ui.modules.settings.category;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.evernote.android.state.State;
import com.fastaccess.R;
import com.fastaccess.data.dao.SettingsModel;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.ui.base.BaseActivity;

import net.grandcentrix.thirtyinch.TiPresenter;

public class SettingsCategoryActivity extends BaseActivity implements SettingsCategoryFragment.SettingsCallback {

    @State String title;
    @SettingsModel.SettingsType @State int settingsType;
    @State boolean needRecreation;

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
            Bundle bundle = getIntent().getExtras();
            title = bundle.getString(BundleConstant.EXTRA);
            settingsType = bundle.getInt(BundleConstant.ITEM);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settingsContainer, new SettingsCategoryFragment(), SettingsCategoryFragment.TAG)
                    .commit();
        }
        setTitle(title);
    }

    @NonNull @Override public TiPresenter providePresenter() {
        return new SettingsCategoryPresenter();
    }

    @SettingsModel.SettingsType @Override public int getSettingsType() {
        return settingsType;
    }

    @Override public void onThemeChanged() {
        needRecreation = true;
    }

    @Override public void onBackPressed() {
        if (needRecreation) {
            super.onThemeChanged();
            return;
        }
        super.onBackPressed();
    }
}
