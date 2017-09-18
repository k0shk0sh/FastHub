package com.fastaccess.ui.modules.code;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;

import com.annimon.stream.Objects;
import com.evernote.android.state.State;
import com.fastaccess.R;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.provider.scheme.LinkParserHelper;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.gists.gist.GistActivity;
import com.fastaccess.ui.modules.repos.code.files.activity.RepoFilesActivity;
import com.fastaccess.ui.modules.repos.code.prettifier.ViewerFragment;

import net.grandcentrix.thirtyinch.TiPresenter;

/**
 * Created by Kosh on 27 Nov 2016, 3:43 PM
 */

public class CodeViewerActivity extends BaseActivity {

    @State String url;
    @State String htmlUrl;

    public static void startActivity(@NonNull Context context, @NonNull String url, @NonNull String htmlUrl) {
        if (!InputHelper.isEmpty(url)) {
            Intent intent = ActivityHelper.editBundle(createIntent(context, url, htmlUrl), LinkParserHelper.isEnterprise(htmlUrl));
            context.startActivity(intent);
        }
    }

    public static Intent createIntent(@NonNull Context context, @NonNull String url, @NonNull String htmlUrl) {
        Intent intent = new Intent(context, CodeViewerActivity.class);
        boolean isEnterprise = LinkParserHelper.isEnterprise(htmlUrl);
        url = LinkParserHelper.getEnterpriseGistUrl(url, isEnterprise);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.EXTRA_TWO, htmlUrl)
                .put(BundleConstant.EXTRA, url)
                .put(BundleConstant.IS_ENTERPRISE, isEnterprise)
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
                    .replace(R.id.container, ViewerFragment.newInstance(url, htmlUrl), ViewerFragment.TAG)
                    .commit();
        }
        String title = Uri.parse(url).getLastPathSegment();
        setTitle(title);
        if (toolbar != null) toolbar.setSubtitle(MimeTypeMap.getFileExtensionFromUrl(url));
        setTaskName(title);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.download_browser_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (InputHelper.isEmpty(url)) return super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.viewAsCode) {
            ViewerFragment viewerFragment = (ViewerFragment) AppHelper.getFragmentByTag(getSupportFragmentManager(), ViewerFragment.TAG);
            if (viewerFragment != null) {
                viewerFragment.onViewAsCode();
            }
            return true;
        } else if (item.getItemId() == R.id.download) {
            if (ActivityHelper.checkAndRequestReadWritePermission(this)) {
                RestProvider.downloadFile(this, url);
            }
            return true;
        } else if (item.getItemId() == R.id.browser) {
            ActivityHelper.openChooser(this, htmlUrl != null ? htmlUrl : url);
            return true;
        } else if (item.getItemId() == R.id.copy) {
            AppHelper.copyToClipboard(this, htmlUrl != null ? htmlUrl : url);
            return true;
        } else if (item.getItemId() == R.id.share) {
            ActivityHelper.shareUrl(this, htmlUrl != null ? htmlUrl : url);
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            Uri uri = Uri.parse(url);
            if (uri == null) {
                finish();
                return true;
            }
            String gistId = LinkParserHelper.getGistId(uri);
            if (!InputHelper.isEmpty(gistId)) {
                startActivity(GistActivity.createIntent(this, gistId, isEnterprise()));
            } else {
                RepoFilesActivity.startActivity(this, url, isEnterprise());
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
