package com.fastaccess.github

import androidx.work.Configuration
import androidx.work.WorkManager
import com.evernote.android.state.StateSaver
import com.fastaccess.github.di.components.AppComponent
import com.fastaccess.github.platform.fabric.FabricProvider
import com.fastaccess.github.platform.timber.FabricTree
import com.fastaccess.github.platform.timber.FastHubTree
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import timber.log.Timber

/**
 * Created by Kosh on 12.05.18.
 */
class App : DaggerApplication() {

    private val appComponent by lazy { AppComponent.getComponent(this) }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> = appComponent

    override fun onCreate() {
        super.onCreate()
        initConfigs()
    }

    private fun initConfigs() {
        WorkManager.initialize(this, Configuration.Builder()
            .setWorkerFactory(appComponent.daggerWorkerFactory())
            .build())
        FabricProvider.initFabric(this)
        StateSaver.setEnabledForAllActivitiesAndSupportFragments(this, true)
        if (BuildConfig.DEBUG) {
            Timber.plant(FastHubTree())
        } else {
            Timber.plant(FabricTree())
        }
    }
}