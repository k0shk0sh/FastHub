package com.fastaccess.ui.modules.parser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.fastaccess.R;
import com.fastaccess.data.dao.LoginModel;
import com.fastaccess.provider.scheme.SchemeParser;

/**
 * Created by Kosh on 09 Dec 2016, 12:31 PM
 */

public class LinksParserActivity extends Activity {

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (LoginModel.getUser() == null) {
            Toast.makeText(this, R.string.please_login, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        onCreate(getIntent());
    }

    @Override protected void onStart() {
        super.onStart();
        setVisible(true);
    }

    private void onCreate(Intent intent) {
        if (intent == null || intent.getAction() == null) {
            finish();
            return;
        }
        if (intent.getAction().equals(Intent.ACTION_VIEW)) {
            onUriReceived();
        } else {
            finish();
        }
    }

    private void onUriReceived() {
        SchemeParser.launchUri(this, getIntent());
        finish();
    }
}
