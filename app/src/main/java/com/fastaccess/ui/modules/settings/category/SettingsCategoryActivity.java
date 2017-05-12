package com.fastaccess.ui.modules.settings.category;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fastaccess.R;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.modules.settings.SettingsFragment;

import net.grandcentrix.thirtyinch.TiPresenter;

public class SettingsCategoryActivity extends BaseActivity {

	@Override
	protected int layout() {
		return R.layout.activity_settings_category;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setResult(RESULT_CANCELED);

		setTitle(getIntent().getStringExtra("title"));

		SettingsCategoryFragment settingsCategoryFragment = new SettingsCategoryFragment();

		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.settingsContainer, settingsCategoryFragment)
				.commit();
	}

	@NonNull
	@Override
	public TiPresenter providePresenter() {
		return new SettingsCategoryPresenter();
	}
}
