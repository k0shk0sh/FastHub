package com.fastaccess.github

import com.evernote.android.state.StateSaver
import com.fastaccess.github.di.components.AppComponent
import com.fastaccess.github.platform.fabric.FabricProvider
import com.fastaccess.github.platform.timber.FabricTree
import com.fastaccess.github.platform.timber.FastHubTree
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import tech.linjiang.pandora.Pandora
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
        StateSaver.setEnabledForAllActivitiesAndSupportFragments(this, true)
        if (BuildConfig.DEBUG) {
            Pandora.init(this).enableShakeOpen()
            Timber.plant(FastHubTree())
        } else {
            Timber.plant(FabricTree())
        }
    }
}