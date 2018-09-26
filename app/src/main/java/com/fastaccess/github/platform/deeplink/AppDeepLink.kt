package com.fastaccess.github.platform.deeplink

import com.airbnb.deeplinkdispatch.DeepLinkSpec

@DeepLinkSpec(prefix = ["app://fasthub"])
annotation class AppDeepLink(vararg val value: String)
