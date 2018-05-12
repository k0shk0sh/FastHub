package com.fastaccess.github

import android.app.Activity
import android.app.Application
import android.os.StrictMode
import com.fastaccess.github.di.components.AppComponent
import com.fastaccess.github.platform.fabric.FabricProvider
import com.fastaccess.github.platform.timber.FabricTree
import com.fastaccess.github.platform.timber.FastHubTree
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import timber.log.Timber
import javax.inject.Inject


/**
 * Created by Kosh on 12.05.18.
 */

class App : Application(), HasActivityInjector {

    @Inject lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector(): AndroidInjector<Activity> = dispatchingAndroidInjector

    override fun onCreate() {
        super.onCreate()
        initInjection()
        initConfigs()
    }

    private fun initInjection() {
        AppComponent.inject(this)
    }

    private fun initConfigs() {
        FabricProvider.initFabric(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(FastHubTree())
            StrictMode.enableDefaults()
        } else
            Timber.plant(FabricTree())
    }
}