package com.fastaccess;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;
import com.fastaccess.data.dao.model.Models;
import com.fastaccess.helper.TypeFaceHelper;
import com.fastaccess.provider.colors.ColorsProvider;
import com.fastaccess.provider.emoji.EmojiManager;
import com.fastaccess.provider.tasks.notification.NotificationSchedulerJobTask;
import com.fastaccess.provider.uil.UILProvider;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.miguelbcr.io.rx_billing_service.RxBillingService;

import io.fabric.sdk.android.Fabric;
import io.requery.Persistable;
import io.requery.android.sqlite.DatabaseSource;
import io.requery.meta.EntityModel;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveSupport;
import io.requery.sql.Configuration;
import io.requery.sql.EntityDataStore;
import io.requery.sql.TableCreationMode;
import shortbread.Shortbread;


/**
 * Created by Kosh on 03 Feb 2017, 12:07 AM
 */

public class App extends Application {
    private static App instance;
    private ReactiveEntityStore<Persistable> dataStore;
    private static GoogleApiClient googleApiClient;

    @Override public void onCreate() {
        super.onCreate();
//        final EmojiCompat.Config config;
//        // Use a downloadable font for EmojiCompat
//        final FontRequest fontRequest = new FontRequest(
//                "com.google.android.gms.fonts",
//                "com.google.android.gms",
//                "Noto Color Emoji Compat",
//                R.array.fonts_certificate);
//        config = new FontRequestEmojiCompatConfig(getApplicationContext(), fontRequest)
//                .setReplaceAll(true)
//                .registerInitCallback(new EmojiCompat.InitCallback() {
//                    @Override
//                    public void onInitialized() {
//                        Log.i(getClass().getSimpleName(), "EmojiCompat initialized");
//                    }
//                    @Override
//                    public void onFailed(@Nullable Throwable throwable) {
//                        Log.e(getClass().getSimpleName(), "EmojiCompat initialization failed", throwable);
//                    }
//                });
//        EmojiCompat.init(config);
        instance = this;
        init();
    }

    @NonNull public static App getInstance() {
        return instance;
    }

    private void init() {
        Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(BuildConfig.DEBUG)
                .build();
        Fabric.with(fabric);
        RxBillingService.register(this);
        deleteDatabase("database.db");
        getDataStore();//init requery before anything.
        setupPreference();
        UILProvider.initUIL(this);
        TypeFaceHelper.generateTypeface(this);
        NotificationSchedulerJobTask.scheduleJob(this);
        Shortbread.create(this);
        EmojiManager.load();
        ColorsProvider.load();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.CREDENTIALS_API)
                .build();
        googleApiClient.connect();
    }

    private void setupPreference() {
        PreferenceManager.setDefaultValues(this, R.xml.fasthub_settings, false);
        PreferenceManager.setDefaultValues(this, R.xml.about_settings, false);
        PreferenceManager.setDefaultValues(this, R.xml.behaviour_settings, false);
        PreferenceManager.setDefaultValues(this, R.xml.customization_settings, false);
        PreferenceManager.setDefaultValues(this, R.xml.language_settings, false);
        PreferenceManager.setDefaultValues(this, R.xml.notification_settings, false);
    }

    public ReactiveEntityStore<Persistable> getDataStore() {
        if (dataStore == null) {
            EntityModel model = Models.DEFAULT;
            DatabaseSource source = new DatabaseSource(this, model, "FastHub-DB", 9);
            Configuration configuration = source.getConfiguration();
            if (BuildConfig.DEBUG) {
                source.setTableCreationMode(TableCreationMode.CREATE_NOT_EXISTS);
            }
            dataStore = ReactiveSupport.toReactiveStore(new EntityDataStore<Persistable>(configuration));
        }
        return dataStore;
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }
}