package com.fastaccess.github.platform.deeplink

import com.airbnb.deeplinkdispatch.DeepLinkSpec

@DeepLinkSpec(prefix = ["http://github.com", "https://github.com"])
annotation class WebDeepLink(vararg val value: String)