package com.fastaccess.ui.modules.login;

import android.os.Bundle;

import com.fastaccess.R;

import butterknife.OnClick;

/**
 * Created by Kosh on 28 Apr 2017, 9:03 PM
 */

public class LoginChooserActivity extends LoginActivity {

    @Override protected int layout() {
        return R.layout.login_chooser_layout;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @OnClick(R.id.basicAuth) public void onBasicAuthClicked() {
        LoginActivity.start(this, true);
    }

    @Override public void onBackPressed() {
        finish();
    }

    @OnClick(R.id.accessToken) public void onAccessTokenClicked() {
        LoginActivity.start(this, false);
    }

}
