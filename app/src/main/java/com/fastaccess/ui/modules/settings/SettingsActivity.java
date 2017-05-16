package com.fastaccess.ui.modules.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.model.Setting;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.PrefHelper;
import com.fastaccess.ui.adapter.SettingsAdapter;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.modules.changelog.ChangelogBottomSheetDialog;
import com.fastaccess.ui.modules.settings.category.SettingsCategoryActivity;

import net.grandcentrix.thirtyinch.TiPresenter;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnItemClick;

public class SettingsActivity extends BaseActivity {

    @BindView(R.id.settingsList) ListView settingsList;

    private static int THEME_CHANGE = 32;
    private Setting[] settings;

    @OnItemClick(R.id.settingsList) void onItemClick(int position) {
        Intent intent = new Intent(this, SettingsCategoryActivity.class);
        intent.putExtra("settings", position);
        intent.putExtra("title", settings[position].getTitle());
        switch (position) {
            case 1:
                ActivityHelper.startReveal(this, intent, settingsList, THEME_CHANGE);
                break;
            case 5:
                showLanguageList();
                break;
            default:
                ActivityHelper.startReveal(this, intent, settingsList);
                break;
        }
    }

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
        setResult(RESULT_CANCELED);
        settings = new Setting[]{
                Setting.newInstance(R.drawable.ic_ring, getString(R.string.notifications), ""),
                Setting.newInstance(R.drawable.ic_settings, getString(R.string.behavior), ""),
                Setting.newInstance(R.drawable.ic_brush, getString(R.string.customization), ""),
                Setting.newInstance(R.drawable.ic_info, getString(R.string.about), ""),
                Setting.newInstance(R.drawable.ic_backup, getString(R.string.backup), ""),
                Setting.newInstance(R.drawable.ic_language, getString(R.string.app_language), "")
        };

        settingsList.setAdapter(new SettingsAdapter(this, settings));
    }

    private void showLanguageList() {
        LanguageBottomSheetDialog languageBottomSheetDialog = new LanguageBottomSheetDialog();
        languageBottomSheetDialog.onAttach((Context) this);
        languageBottomSheetDialog.show(getSupportFragmentManager(), "LanguageBottomSheetDialog");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == THEME_CHANGE)
            setResult(resultCode);
    }

    @NonNull
    @Override
    public TiPresenter providePresenter() {
        return new SettingsPresenter();
    }
}
