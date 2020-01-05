package com.fastaccess.github.base.deeplink

import com.airbnb.deeplinkdispatch.DeepLinkSpec
import com.fastaccess.github.base.utils.IN_APP_LINK

@DeepLinkSpec(prefix = [IN_APP_LINK])
annotation class AppDeepLink(vararg val value: String)
