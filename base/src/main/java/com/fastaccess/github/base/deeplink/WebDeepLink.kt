package com.fastaccess.github.base.deeplink

import com.airbnb.deeplinkdispatch.DeepLinkSpec

@DeepLinkSpec(
    prefix = [
        "http://github.com", "https://github.com",
        "https://api.github.com", "http://api.github.com"
    ]
)
annotation class WebDeepLink(vararg val value: String)