package com.fastaccess.github.platform.deeplink

import com.airbnb.deeplinkdispatch.DeepLinkSpec


const val WEB_EDITOR_PATH = "editor"

const val APP_DEEPLINK = "app://fasthub"
const val WEB_EDITOR_DEEPLINK = "$APP_DEEPLINK/$WEB_EDITOR_PATH"

@DeepLinkSpec(prefix = [APP_DEEPLINK])
annotation class AppDeepLink(vararg val value: String)
