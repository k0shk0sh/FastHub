package com.prettifier.pretty;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fastaccess.R;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.Logger;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.provider.markdown.MarkDownProvider;
import com.fastaccess.provider.scheme.SchemeParser;
import com.fastaccess.ui.modules.code.CodeViewerActivity;
import com.prettifier.pretty.callback.MarkDownInterceptorInterface;
import com.prettifier.pretty.helper.GithubHelper;
import com.prettifier.pretty.helper.PrettifyHelper;


public class PrettifyWebView extends NestedWebView {
    private OnContentChangedListener onContentChangedListener;
    private boolean interceptTouch;
    private boolean enableNestedScrolling;

    public interface OnContentChangedListener {
        void onContentChanged(int progress);

        void onScrollChanged(boolean reachedTop, int scroll);
    }

    public PrettifyWebView(Context context) {
        super(context);
        if (isInEditMode()) return;
        initView(null);
    }

    public PrettifyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public PrettifyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    @Override public boolean onInterceptTouchEvent(MotionEvent p) {
        return true;
    }

    @SuppressLint("ClickableViewAccessibility") @Override public boolean onTouchEvent(MotionEvent event) {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(interceptTouch);
        }
        return super.onTouchEvent(event);
    }

    @SuppressLint("SetJavaScriptEnabled") private void initView(@Nullable AttributeSet attrs) {
        if (isInEditMode()) return;
        if (attrs != null) {
            TypedArray tp = getContext().obtainStyledAttributes(attrs, R.styleable.PrettifyWebView);
            try {
                int color = tp.getColor(R.styleable.PrettifyWebView_webview_background, ViewHelper.getWindowBackground(getContext()));
                setBackgroundColor(color);
            } finally {
                tp.recycle();
            }
        }
        setWebChromeClient(new ChromeClient());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setWebViewClient(new WebClient());
        } else {
            setWebViewClient(new WebClientCompat());
        }
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAppCachePath(getContext().getCacheDir().getPath());
        settings.setAppCacheEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setDefaultTextEncodingName("utf-8");
        settings.setLoadsImagesAutomatically(true);
        settings.setBlockNetworkImage(false);
        setOnLongClickListener((view) -> {
            WebView.HitTestResult result = getHitTestResult();
            if (hitLinkResult(result) && !InputHelper.isEmpty(result.getExtra())) {
                AppHelper.copyToClipboard(getContext(), result.getExtra());
                return true;
            }
            return false;
        });
    }

    @Override protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onContentChangedListener != null) {
            onContentChangedListener.onScrollChanged(t == 0, t);
        }
    }

    @Override protected void onDetachedFromWindow() {
        onContentChangedListener = null;
        super.onDetachedFromWindow();
    }

    private boolean hitLinkResult(WebView.HitTestResult result) {
        return result.getType() == WebView.HitTestResult.SRC_ANCHOR_TYPE || result.getType() == HitTestResult.IMAGE_TYPE ||
                result.getType() == HitTestResult.SRC_IMAGE_ANCHOR_TYPE;
    }

    public void setOnContentChangedListener(@NonNull OnContentChangedListener onContentChangedListener) {
        this.onContentChangedListener = onContentChangedListener;
    }

    public void setThemeSource(@NonNull String source, @Nullable String theme) {
        if (!InputHelper.isEmpty(source)) {
            WebSettings settings = getSettings();
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
            setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            settings.setSupportZoom(true);
            settings.setBuiltInZoomControls(true);
            settings.setDisplayZoomControls(false);
            String page = PrettifyHelper.generateContent(source, theme);
            loadCode(page);
        }
    }

    public void setSource(@NonNull String source, boolean wrap) {
        if (!InputHelper.isEmpty(source)) {
            WebSettings settings = getSettings();
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
            setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            settings.setSupportZoom(!wrap);
            settings.setBuiltInZoomControls(!wrap);
            if (!wrap) settings.setDisplayZoomControls(false);
            String page = PrettifyHelper.generateContent(source, AppHelper.isNightMode(getResources()), wrap);
            loadCode(page);
        }
    }

    private void loadCode(String page) {
        post(() -> loadDataWithBaseURL("file:///android_asset/highlight/", page, "text/html", "utf-8", null));
    }

    public void scrollToLine(@NonNull String url) {
        String[] lineNo = getLineNo(url);
        if (lineNo != null && lineNo.length > 1) {
            loadUrl("javascript:scrollToLineNumber('" + lineNo[0] + "', '" + lineNo[1] + "')");
        } else if (lineNo != null) {
            loadUrl("javascript:scrollToLineNumber('" + lineNo[0] + "', '0')");
        }
    }

    public static String[] getLineNo(@Nullable String url) {
        String lineNo[] = null;
        if (url != null) {
            try {
                Uri uri = Uri.parse(url);
                String lineNumber = uri.getEncodedFragment();
                if (lineNumber != null) {
                    lineNo = lineNumber.replaceAll("L", "").split("-");
                }
            } catch (Exception ignored) {}
        }
        return lineNo;
    }

    public void setGithubContentWithReplace(@NonNull String source, @Nullable String baseUrl, boolean replace) {
        setGithubContent(source, baseUrl, false);
        addJavascriptInterface(new MarkDownInterceptorInterface(this, false), "Android");
        String page = GithubHelper.generateContent(getContext(), source, baseUrl, AppHelper.isNightMode(getResources()), false, replace);
        post(() -> loadDataWithBaseURL("file:///android_asset/md/", page, "text/html", "utf-8", null));
    }

    public void setGithubContent(@NonNull String source, @Nullable String baseUrl, boolean toggleNestScrolling) {
        setGithubContent(source, baseUrl, toggleNestScrolling, true);
    }

    public void setWikiContent(@NonNull String source, @Nullable String baseUrl) {
        addJavascriptInterface(new MarkDownInterceptorInterface(this, true), "Android");
        String page = GithubHelper.generateContent(getContext(), source, baseUrl, AppHelper.isNightMode(getResources()), AppHelper.isNightMode
                (getResources()), true);
        post(() -> loadDataWithBaseURL("file:///android_asset/md/", page, "text/html", "utf-8", null));
    }

    public void setGithubContent(@NonNull String source, @Nullable String baseUrl, boolean toggleNestScrolling, boolean enableBridge) {
        if (enableBridge) addJavascriptInterface(new MarkDownInterceptorInterface(this, toggleNestScrolling), "Android");
        String page = GithubHelper.generateContent(getContext(), source, baseUrl, AppHelper.isNightMode(getResources()),
                AppHelper.isNightMode(getResources()), false);
        post(() -> loadDataWithBaseURL("file:///android_asset/md/", page, "text/html", "utf-8", null));
    }

    public void loadImage(@NonNull String url, boolean isSvg) {
        WebSettings settings = getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        String html;
        if (isSvg) {
            html = url;
        } else {
            html = "<html><head><style>img{display: inline; height: auto; max-width: 100%;}</style></head><body>" +
                    "<img src=\"" + url + "\"/></body></html>";
        }
        Logger.e(html);
        loadData(html, "text/html", null);
    }

    public void setInterceptTouch(boolean interceptTouch) {
        this.interceptTouch = interceptTouch;
    }

    public void setEnableNestedScrolling(boolean enableNestedScrolling) {
        if (this.enableNestedScrolling != enableNestedScrolling) {
            setNestedScrollingEnabled(enableNestedScrolling);
            this.enableNestedScrolling = enableNestedScrolling;
        }
    }

    private void startActivity(@Nullable Uri url) {
        if (url == null) return;
        if (MarkDownProvider.isImage(url.toString())) {
            CodeViewerActivity.startActivity(getContext(), url.toString(), url.toString());
        } else {
            String lastSegment = url.getEncodedFragment();
            if (lastSegment != null || url.toString().startsWith("#") || url.toString().indexOf('#') != -1) {
                return;
            }
            SchemeParser.launchUri(getContext(), url, true);
        }
    }

    private class ChromeClient extends WebChromeClient {
        @Override public void onProgressChanged(WebView view, int progress) {
            super.onProgressChanged(view, progress);
            if (onContentChangedListener != null) {
                onContentChangedListener.onContentChanged(progress);
            }
        }
    }

    private class WebClient extends WebViewClient {
        @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            startActivity(request.getUrl());
            return true;
        }
    }

    private class WebClientCompat extends WebViewClient {
        @SuppressWarnings("deprecation") @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
            startActivity(Uri.parse(url));
            return true;
        }

    }
}