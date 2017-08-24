package com.fastaccess;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;

import com.apollographql.apollo.ApolloClient;
import com.fastaccess.data.dao.model.Models;
import com.fastaccess.helper.DeviceNameGetter;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.helper.TypeFaceHelper;
import com.fastaccess.provider.colors.ColorsProvider;
import com.fastaccess.provider.emoji.EmojiManager;
import com.fastaccess.provider.fabric.FabricProvider;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.provider.tasks.notification.NotificationSchedulerJobTask;
import com.miguelbcr.io.rx_billing_service.RxBillingService;

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
    private ApolloClient apolloClient;

    @Override public void onCreate() {
        super.onCreate();
        instance = this;
        init();
    }

    @NonNull public static App getInstance() {
        return instance;
    }

    private void init() {
        FabricProvider.initFabric(this);
        RxBillingService.register(this);
        deleteDatabase("database.db");
        getDataStore();//init requery before anything.
        setupPreference();
        TypeFaceHelper.generateTypeface(this);
        NotificationSchedulerJobTask.scheduleJob(this);
        Shortbread.create(this);
        EmojiManager.load();
        ColorsProvider.load();
        DeviceNameGetter.getInstance().loadDevice();
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
            DatabaseSource source = new DatabaseSource(this, model, "FastHub-DB", 11);
            Configuration configuration = source.getConfiguration();
            if (BuildConfig.DEBUG) {
                source.setTableCreationMode(TableCreationMode.CREATE_NOT_EXISTS);
            }
            dataStore = ReactiveSupport.toReactiveStore(new EntityDataStore<Persistable>(configuration));
        }
        return dataStore;
    }

    public ApolloClient getApolloClient() {
        if (apolloClient == null) {
            apolloClient = ApolloClient.builder()
                    .serverUrl("https://" + (PrefGetter.isEnterprise() ? PrefGetter.getEnterpriseUrl() : "api.github.com") + "/graphql")
                    .okHttpClient(RestProvider.provideOkHttpClient())
                    .build();
        }
        return apolloClient;
    }
}