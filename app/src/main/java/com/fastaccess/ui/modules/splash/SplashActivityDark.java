package com.fastaccess.ui.modules.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.fastaccess.ui.modules.main.MainActivity;

public class SplashActivityDark extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = new Intent(SplashActivityDark.this, MainActivity.class);
		startActivity(intent);
		finish();
	}
}
