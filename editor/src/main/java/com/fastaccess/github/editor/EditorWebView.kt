package com.fastaccess.github.editor

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebSettings
import android.webkit.WebView


/**
 * Created by Kosh on 2019-07-20.
 */
class EditorWebView : WebView {

    constructor(context: Context?) : super(context)
    constructor(
        context: Context?,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        init()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init()
    }


    fun init() {
        if (isInEditMode) return
        val settings = settings
        settings.javaScriptEnabled = true
        settings.setAppCachePath(context.cacheDir.path)
        settings.setAppCacheEnabled(true)
        settings.cacheMode = WebSettings.LOAD_NO_CACHE
        settings.defaultTextEncodingName = "utf-8"
        settings.loadsImagesAutomatically = true
        settings.blockNetworkImage = false
        post { loadUrl("file:///android_asset/index.html") }

    }
}