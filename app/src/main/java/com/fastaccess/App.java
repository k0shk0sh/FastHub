package com.fastaccess;

import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;
import android.support.v7.preference.PreferenceManager;

import com.fastaccess.data.dao.model.Models;
import com.fastaccess.helper.TypeFaceHelper;
import com.fastaccess.provider.tasks.notification.NotificationSchedulerJobTask;
import com.fastaccess.provider.uil.UILProvider;

import io.requery.Persistable;
import io.requery.android.sqlite.DatabaseSource;
import io.requery.meta.EntityModel;
import io.requery.rx.RxSupport;
import io.requery.rx.SingleEntityStore;
import io.requery.sql.Configuration;
import io.requery.sql.EntityDataStore;
import io.requery.sql.TableCreationMode;


/**
 * Created by Kosh on 03 Feb 2017, 12:07 AM
 */

public class App extends MultiDexApplication {
    private static App instance;
    private SingleEntityStore<Persistable> dataStore;

    @Override public void onCreate() {
        super.onCreate();
        instance = this;
        deleteDatabase("database.db");
        PreferenceManager.setDefaultValues(this, R.xml.fasthub_settings, false);
        UILProvider.initUIL(this);
        TypeFaceHelper.generateTypeface(this);
        NotificationSchedulerJobTask.scheduleJob(this);//schedule the job for the notifications
    }

    @NonNull public static App getInstance() {
        return instance;
    }

    public SingleEntityStore<Persistable> getDataStore() {
        if (dataStore == null) {
            EntityModel model = Models.DEFAULT;
            DatabaseSource source = new DatabaseSource(this, model, "FastHub-DB", 4);
            Configuration configuration = source.getConfiguration();
            if (BuildConfig.DEBUG) {
                source.setTableCreationMode(TableCreationMode.CREATE_NOT_EXISTS);
            }
            dataStore = RxSupport.toReactiveStore(new EntityDataStore<Persistable>(configuration));
        }
        return dataStore;
    }
}