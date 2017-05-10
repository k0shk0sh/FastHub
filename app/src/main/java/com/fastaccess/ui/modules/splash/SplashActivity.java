package com.fastaccess.ui.modules.splash;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fastaccess.BuildConfig;
import com.fastaccess.R;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.ui.modules.main.MainActivity;

public class SplashActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (PrefGetter.getThemeType(this)==PrefGetter.DARK) {
			PackageManager p = getPackageManager();
			ComponentName lightTheme = new ComponentName(BuildConfig.APPLICATION_ID, "com.fastaccess.ui.modules.splash.SplashActivity");
			ComponentName darkTheme = new ComponentName(BuildConfig.APPLICATION_ID, "com.fastaccess.ui.modules.splash.SplashActivityDark");

			p.setComponentEnabledSetting(lightTheme, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
			p.setComponentEnabledSetting(lightTheme, PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED, PackageManager.DONT_KILL_APP);
			p.setComponentEnabledSetting(darkTheme, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
		}

		Intent intent = new Intent(SplashActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
	}
}
