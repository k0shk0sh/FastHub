package com.fastaccess.ui.modules.login;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fastaccess.R;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.modules.main.MainView;
import com.fastaccess.ui.widgets.AppbarRefreshLayout;

import butterknife.BindView;

/**
 * Created by Kosh on 08 Feb 2017, 9:10 PM
 */

public class LoginView extends BaseActivity<LoginMvp.View, LoginPresenter> implements LoginMvp.View {


    @BindView(R.id.webView) WebView webView;
    @BindView(R.id.refresh) AppbarRefreshLayout refresh;

    @Override protected int layout() {
        return R.layout.login_layout;
    }

    @Override protected boolean isTransparent() {
        return true;
    }

    @Override protected boolean canBack() {
        return false;
    }

    @Override protected boolean isSecured() {
        return true;
    }

    @NonNull @Override public LoginPresenter providePresenter() {
        return new LoginPresenter();
    }

    @Override public void onRefresh() {
        webView.loadUrl(getPresenter().getAuthorizationUrl().toString());
    }

    @Override public void onSuccessfullyLoggedIn() {
        hideProgress();
        startActivity(new Intent(this, MainView.class));
        finish();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refresh.setOnRefreshListener(this);
        webView.getSettings().setSaveFormData(false);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override public void onProgressChanged(WebView view, int progress) {
                super.onProgressChanged(view, progress);
                if (progress == 100) {
                    refresh.setRefreshing(false);
                } else if (progress < 100) {
                    refresh.setRefreshing(true);
                }
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            webView.setWebViewClient(new WebViewClient() {
                @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    String code = getPresenter().getCode(request.getUrl().toString());
                    if (code != null) {
                        getPresenter().onGetToken(code);
                    }
                    return false;
                }
            });
        } else {
            webView.setWebViewClient(new WebViewClient() {
                @SuppressWarnings("deprecation") @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    String code = getPresenter().getCode(url);
                    if (code != null) {
                        getPresenter().onGetToken(code);
                    }
                    return false;
                }
            });
        }
        onRefresh();
    }
}
