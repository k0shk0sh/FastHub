package com.fastaccess.github.platform.deeplink

import com.airbnb.deeplinkdispatch.DeepLinkSpec

@DeepLinkSpec(prefix = ["http://github.com", "https://github.com",
    "https://api.github.com", "http://api.github.com",
    "https://raw.githubusercontent.com"])
annotation class WebDeepLink(vararg val value: String)