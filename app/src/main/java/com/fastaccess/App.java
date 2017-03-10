package com.fastaccess;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;

import com.fastaccess.helper.TypeFaceHelper;
import com.fastaccess.provider.tasks.notification.NotificationSchedulerJobTask;
import com.fastaccess.provider.uil.UILProvider;
import com.siimkinks.sqlitemagic.SqliteMagic;


/**
 * Created by Kosh on 03 Feb 2017, 12:07 AM
 */

public class App extends Application {
    private static App instance;

    @Override public void onCreate() {
        super.onCreate();
        instance = this;
        PreferenceManager.setDefaultValues(this, R.xml.fasthub_settings, false);
        SqliteMagic.setLoggingEnabled(BuildConfig.DEBUG);
        SqliteMagic.init(this);
        UILProvider.initUIL(this);
        TypeFaceHelper.generateTypeface(this);
        NotificationSchedulerJobTask.scheduleJob(this);//schedule the job for the notifications
    }

    @NonNull public static App getInstance() {
        return instance;
    }

}
