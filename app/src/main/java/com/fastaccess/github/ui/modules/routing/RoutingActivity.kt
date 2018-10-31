package com.fastaccess.github.ui.modules.routing

import android.app.Activity
import android.os.Bundle
import com.airbnb.deeplinkdispatch.DeepLinkHandler
import timber.log.Timber

/**
 * Created by Kosh on 26.09.18.
 */
@DeepLinkHandler(value = [RoutingModule::class])
class RoutingActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val delegate = DeepLinkDelegate(RoutingModuleLoader())
                .dispatchFrom(this)
        Timber.e("${delegate.isSuccessful} ---- ${delegate.uriString()} ----- ${delegate.error()}")
        if (!delegate.isSuccessful) {
            //TODO FIXME
        }
    }

    override fun onStart() {
        super.onStart()
        setVisible(true)
    }
}