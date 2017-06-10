package com.fastaccess.ui.modules.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.ListView;

import com.fastaccess.R;
import com.fastaccess.data.dao.SettingsModel;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.ui.adapter.SettingsAdapter;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.settings.category.SettingsCategoryActivity;
import com.fastaccess.ui.modules.theme.ThemeActivity;

import net.grandcentrix.thirtyinch.TiPresenter;

import butterknife.BindView;

public class SettingsActivity extends BaseActivity {

    @BindView(R.id.settingsList) ListView settingsList;

    private static int THEME_CHANGE = 32;
    private SettingsModel[] settings;

    @Override protected int layout() {
        return R.layout.activity_settings;
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
        setToolbarIcon(R.drawable.ic_back);
        setTitle(getString(R.string.settings));
        if (savedInstanceState == null) {
            setResult(RESULT_CANCELED);
        }
        settings = new SettingsModel[]{
                SettingsModel.newInstance(R.drawable.ic_ring, getString(R.string.notifications), ""),
                SettingsModel.newInstance(R.drawable.ic_settings, getString(R.string.behavior), ""),
                SettingsModel.newInstance(R.drawable.ic_edit, getString(R.string.customization), ""),
                SettingsModel.newInstance(R.drawable.ic_backup, getString(R.string.backup), ""),
                SettingsModel.newInstance(R.drawable.ic_language, getString(R.string.app_language), ""),
                SettingsModel.newInstance(R.drawable.ic_color_lens, getString(R.string.theme_title), "")
        };
        settingsList.setAdapter(new SettingsAdapter(this, settings));
        settingsList.setOnItemClickListener((parent, view, position, id) -> {
            SettingsModel settingsModel = (SettingsModel) parent.getItemAtPosition(position);
            Intent intent = new Intent(this, SettingsCategoryActivity.class);
            intent.putExtras(Bundler.start()
                    .put(BundleConstant.ITEM, position)
                    .put(BundleConstant.EXTRA, settingsModel.getTitle())
                    .end());
            if (settingsModel.getTitle().equalsIgnoreCase(getString(R.string.app_language))) {
                showLanguageList();
            } else if (settingsModel.getTitle().equalsIgnoreCase(getString(R.string.theme_title))) {
                ActivityHelper.startReveal(this, new Intent(this, ThemeActivity.class), view, THEME_CHANGE);
            } else {
                ActivityHelper.startReveal(this, intent, view);
            }
        });
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == THEME_CHANGE) {
            setResult(resultCode);
            finish();
        }
    }

    @NonNull @Override public TiPresenter providePresenter() {
        return new BasePresenter();
    }

    private void showLanguageList() {
        LanguageBottomSheetDialog languageBottomSheetDialog = new LanguageBottomSheetDialog();
        languageBottomSheetDialog.onAttach((Context) this);
        languageBottomSheetDialog.show(getSupportFragmentManager(), "LanguageBottomSheetDialog");
    }
}
