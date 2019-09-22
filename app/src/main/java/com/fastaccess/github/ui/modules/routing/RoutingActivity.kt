package com.fastaccess.github.ui.modules.routing

import android.app.Activity
import android.os.Bundle
import com.airbnb.deeplinkdispatch.DeepLinkHandler
import com.fastaccess.fasthub.deeplink.CommitDeepLinkModule
import com.fastaccess.fasthub.deeplink.CommitDeepLinkModuleLoader
import com.fastaccess.fasthub.deeplink.ReviewDeepLinkModule
import com.fastaccess.fasthub.deeplink.ReviewDeepLinkModuleLoader
import com.fastaccess.github.editor.deeplink.EditorDeepLinkModule
import com.fastaccess.github.editor.deeplink.EditorDeepLinkModuleLoader
import timber.log.Timber

/**
 * Created by Kosh on 26.09.18.
 */
@DeepLinkHandler(
    value = [
        RoutingModule::class,
        CommitDeepLinkModule::class,
        EditorDeepLinkModule::class,
        ReviewDeepLinkModule::class
    ]
)
class RoutingActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val delegate = DeepLinkDelegate(
            RoutingModuleLoader(),
            CommitDeepLinkModuleLoader(),
            EditorDeepLinkModuleLoader(),
            ReviewDeepLinkModuleLoader()
        )
            .dispatchFrom(this)
        Timber.e("$delegate")
        if (!delegate.isSuccessful) {
            //TODO FIXME (launch chrometab)
        }
        finish()
    }
}