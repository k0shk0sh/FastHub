package com.fastaccess.github

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

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> = AppComponent.getComponent(this)

    override fun onCreate() {
        super.onCreate()
        initConfigs()
    }

    private fun initConfigs() {
        FabricProvider.initFabric(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(FastHubTree())
        } else {
            Timber.plant(FabricTree())
        }
    }
}