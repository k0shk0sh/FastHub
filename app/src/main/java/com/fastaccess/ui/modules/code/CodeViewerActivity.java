package com.fastaccess.ui.modules.code;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import com.annimon.stream.Objects;
import com.fastaccess.R;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.gists.gist.GistActivity;
import com.fastaccess.ui.modules.repos.code.files.activity.RepoFilesActivity;
import com.fastaccess.ui.modules.repos.code.prettifier.ViewerFragment;

import net.grandcentrix.thirtyinch.TiPresenter;

import icepick.State;

/**
 * Created by Kosh on 27 Nov 2016, 3:43 PM
 */

public class CodeViewerActivity extends BaseActivity {

    @State String url;
    @State String htmlUrl;

    public static void startActivity(@NonNull Context context, @NonNull String url, @NonNull String htmlUrl) {
        if (!InputHelper.isEmpty(url)) context.startActivity(createIntent(context, url, htmlUrl));
    }

    public static Intent createIntent(@NonNull Context context, @NonNull String url, @NonNull String htmlUrl) {
        Intent intent = new Intent(context, CodeViewerActivity.class);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.EXTRA_TWO, htmlUrl)
                .put(BundleConstant.EXTRA, url)
                .end());
        return intent;
    }

    @Override protected int layout() {
        return R.layout.activity_fragment_layout;
    }

    @Override protected boolean isTransparent() {
        return true;
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
            htmlUrl = bundle.getString(BundleConstant.EXTRA_TWO);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, ViewerFragment.newInstance(url), ViewerFragment.TAG)
                    .commit();
        }
        setTitle(Uri.parse(url).getLastPathSegment());
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.download_browser_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (InputHelper.isEmpty(url)) return super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.download) {
            if (ActivityHelper.checkAndRequestReadWritePermission(this)) {
                RestProvider.downloadFile(this, url);
            }
            return true;
        } else if (item.getItemId() == R.id.browser) {
            ActivityHelper.openChooser(this,  htmlUrl != null ? htmlUrl : url);
            return true;
        } else if (item.getItemId() == R.id.copy) {
            AppHelper.copyToClipboard(this, htmlUrl != null ? htmlUrl : url);
            return true;
        } else if (item.getItemId() == R.id.share) {
            ActivityHelper.shareUrl(this,  htmlUrl != null ? htmlUrl : url);
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            Uri uri = Uri.parse(url);
            if (uri.getHost().contains("gist.github")) {
                if (uri.getPathSegments() != null && !uri.getPathSegments().isEmpty() && uri.getPathSegments().size() >= 1) {
                    GistActivity.createIntent(this, uri.getPathSegments().get(1));
                }
            } else {
                RepoFilesActivity.startActivity(this, url);
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
