package com.prettifier.pretty.callback;

import android.webkit.JavascriptInterface;

import com.prettifier.pretty.PrettifyWebView;

/**
 * Created by Kosh on 13 Dec 2016, 3:01 PM
 */

public class MarkDownInterceptorInterface {
    private PrettifyWebView prettifyWebView;

    public MarkDownInterceptorInterface(PrettifyWebView prettifyWebView) {
        this.prettifyWebView = prettifyWebView;
    }

    @JavascriptInterface public void startIntercept() {
        if (prettifyWebView != null) {
            prettifyWebView.setInterceptTouch(true);
            prettifyWebView.setEnableNestedScrolling(false);
        }
    }

    @JavascriptInterface public void stopIntercept() {
        if (prettifyWebView != null) {
            prettifyWebView.setInterceptTouch(false);
            prettifyWebView.setEnableNestedScrolling(true);
        }
    }
}
