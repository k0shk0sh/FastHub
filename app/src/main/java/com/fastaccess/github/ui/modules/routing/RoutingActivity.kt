package com.fastaccess.github.ui.modules.routing

import android.app.Activity
import android.os.Bundle
import com.airbnb.deeplinkdispatch.DeepLinkHandler
import com.fastaccess.fasthub.deeplink.CommitDeepLinkModule
import com.fastaccess.fasthub.deeplink.CommitDeepLinkModuleLoader
import timber.log.Timber

/**
 * Created by Kosh on 26.09.18.
 */
@DeepLinkHandler(value = [RoutingModule::class, CommitDeepLinkModule::class])
class RoutingActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val delegate = DeepLinkDelegate(RoutingModuleLoader(), CommitDeepLinkModuleLoader())
            .dispatchFrom(this)
        Timber.e("$delegate")
        if (!delegate.isSuccessful) {
            //TODO FIXME
        }
        finish()
    }
}