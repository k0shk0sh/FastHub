package com.fastaccess.ui.modules.code;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import com.annimon.stream.Objects;
import com.fastaccess.R;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.repos.code.prettifier.ViewerView;

import net.grandcentrix.thirtyinch.TiPresenter;

import icepick.State;

/**
 * Created by Kosh on 27 Nov 2016, 3:43 PM
 */

public class CodeViewerView extends BaseActivity {

    @State String url;

    public static void startActivity(@NonNull Context context, @NonNull String url) {
        context.startActivity(createIntent(context, url));
    }

    public static Intent createIntent(@NonNull Context context, @NonNull String url) {
        Intent intent = new Intent(context, CodeViewerView.class);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.EXTRA, url)
                .end());
        return intent;
    }

    @Override protected int layout() {
        return R.layout.activity_fragment_layout;
    }

    @Override protected boolean isTransparent() {
        return false;
    }

    @Override protected boolean canBack() {
        return true;
    }

    @Override protected boolean isSecured() {
        return false;
    }

    @NonNull @Override public TiPresenter providePresenter() {
        return new BasePresenter();
    }

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Intent intent = Objects.requireNonNull(getIntent(), "Intent is null");
            Bundle bundle = Objects.requireNonNull(intent.getExtras());
            //noinspection ConstantConditions
            url = Objects.requireNonNull(bundle.getString(BundleConstant.EXTRA), "Url is null");
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, ViewerView.newInstance(url), ViewerView.TAG)
                    .commit();
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.download_browser_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.download) {
            if (!InputHelper.isEmpty(url)) {
                RestProvider.downloadFile(this, url);
            }
            return true;
        } else if (item.getItemId() == R.id.browser) {
            ActivityHelper.forceOpenInBrowser(this, url);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
