package com.fastaccess;

import android.app.Application;
import android.support.annotation.NonNull;

import com.commonsware.cwac.anddown.AndDown;
import com.fastaccess.helper.TypeFaceHelper;
import com.fastaccess.provider.uil.UILProvider;
import com.siimkinks.sqlitemagic.SqliteMagic;


/**
 * Created by Kosh on 03 Feb 2017, 12:07 AM
 */

public class App extends Application {
    private static App instance;

    private AndDown andDown;

    @Override public void onCreate() {
        super.onCreate();
        instance = this;
        SqliteMagic.setLoggingEnabled(BuildConfig.DEBUG);
        SqliteMagic.init(this);
        UILProvider.initUIL(this);
        TypeFaceHelper.generateTypeface(this);
    }

    @NonNull public static App getInstance() {
        return instance;
    }


    @NonNull public AndDown getAndDown() {
        if (andDown == null) {
            andDown = new AndDown();
        }
        return andDown;
    }
}
