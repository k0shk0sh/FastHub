package com.fastaccess.fasthub.diff

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.webkit.WebViewClientCompat
import timber.log.Timber


class DiffWebView : WebView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @SuppressLint("SetJavaScriptEnabled")
    fun loadDiff(diff: String) {
        settings.apply {
            javaScriptEnabled = true
            defaultTextEncodingName = "utf-8"
            webChromeClient = WebChromeClient()
        }
        post {
            webViewClient = object : WebViewClientCompat() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    Timber.e("here!")
                    loadUrl("javascript:loadDiff('$diff')")
                }
            }
            loadUrl("file:///android_asset/index.html")
        }
    }
}