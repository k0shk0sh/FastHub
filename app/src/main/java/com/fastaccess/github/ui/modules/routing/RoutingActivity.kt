package com.fastaccess.github.ui.modules.routing

import android.app.Activity
import android.os.Bundle
import com.airbnb.deeplinkdispatch.DeepLinkHandler

/**
 * Created by Kosh on 26.09.18.
 */
@DeepLinkHandler(value = [RoutingModule::class])
class RoutingActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DeepLinkDelegate(RoutingModuleLoader())
                .dispatchFrom(this) // route
    }

    override fun onStart() {
        super.onStart()
        setVisible(true)
    }
}