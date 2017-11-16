package com.prettifier.pretty.callback;

import android.webkit.JavascriptInterface;

import com.prettifier.pretty.PrettifyWebView;

/**
 * Created by Kosh on 13 Dec 2016, 3:01 PM
 */

public class MarkDownInterceptorInterface {
    private PrettifyWebView prettifyWebView;
    private boolean toggleNestScrolling;

    public MarkDownInterceptorInterface(PrettifyWebView prettifyWebView) {
        this(prettifyWebView, false);
    }

    public MarkDownInterceptorInterface(PrettifyWebView prettifyWebView, boolean toggleNestScrolling) {
        this.prettifyWebView = prettifyWebView;
        this.toggleNestScrolling = toggleNestScrolling;
    }

    @JavascriptInterface public void startIntercept() {
        if (prettifyWebView != null) {
            prettifyWebView.setInterceptTouch(true);
            if (toggleNestScrolling) prettifyWebView.setEnableNestedScrolling(false);
        }
    }

    @JavascriptInterface public void stopIntercept() {
        if (prettifyWebView != null) {
            prettifyWebView.setInterceptTouch(false);
            if (toggleNestScrolling) prettifyWebView.setEnableNestedScrolling(true);
        }
    }
}
