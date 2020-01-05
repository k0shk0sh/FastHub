package com.fastaccess.github.base.deeplink

import com.airbnb.deeplinkdispatch.DeepLinkSpec

@DeepLinkSpec(prefix = ["https://raw.githubusercontent.com"])
annotation class RawWebDeepLink(vararg val value: String)