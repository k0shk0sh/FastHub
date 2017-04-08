package com.fastaccess.ui.modules.repos.code.files.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import com.annimon.stream.Objects;
import com.fastaccess.R;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.main.MainView;
import com.fastaccess.ui.modules.repos.RepoPagerView;
import com.fastaccess.ui.modules.repos.code.files.paths.RepoFilePathView;

import net.grandcentrix.thirtyinch.TiPresenter;

import icepick.State;

/**
 * Created by Kosh on 08 Apr 2017, 4:24 PM
 */

public class RepoFilesActivity extends BaseActivity {

    @State String login;
    @State String repoId;

    public static void startActivity(@NonNull Context context, @NonNull String url) {
        if (!InputHelper.isEmpty(url)) {
            context.startActivity(getIntent(context, url));
        }
    }

    public static Intent getIntent(@NonNull Context context, @NonNull String url) {
        Uri uri = Uri.parse(url);
        if (uri.getPathSegments() != null && uri.getPathSegments().size() > 3) {
            String login = uri.getPathSegments().get(0);
            String repoId = uri.getPathSegments().get(1);
            String branch = uri.getPathSegments().get(2);
            StringBuilder path = new StringBuilder();
//            for (int i = 3; i < uri.getPathSegments().size() - 1; i++) { // disable for now.
//                String appendedPath = uri.getPathSegments().get(i);
//                path.append("/").append(appendedPath);
//            }
            Intent intent = new Intent(context, RepoFilesActivity.class);
            intent.putExtras(Bundler.start()
                    .put(BundleConstant.ID, repoId)
                    .put(BundleConstant.EXTRA, login)
                    .put(BundleConstant.EXTRA_TWO, path.toString())
                    .put(BundleConstant.EXTRA_THREE, branch)
                    .end());
            return intent;
        }
        return new Intent(context, MainView.class);
    }

    @Override protected int layout() {
        return R.layout.toolbar_activity_layout;
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
            Bundle bundle = getIntent().getExtras();
            login = bundle.getString(BundleConstant.EXTRA);
            repoId = bundle.getString(BundleConstant.ID);
            String path = bundle.getString(BundleConstant.EXTRA_TWO);
            String defaultBranch = Objects.toString(bundle.getString(BundleConstant.EXTRA_THREE), "master");
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, RepoFilePathView.newInstance(login, repoId, path, defaultBranch), "RepoFilePathView")
                    .commit();
        }
        setTitle(String.format("%s/%s", login, repoId));
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(RepoPagerView.createIntent(this, repoId, login));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onBackPressed() {
        RepoFilePathView filePathView = (RepoFilePathView) AppHelper.getFragmentByTag(getSupportFragmentManager(), "RepoFilePathView");
        if (filePathView != null) {
            if (filePathView.canPressBack()) {
                super.onBackPressed();
            } else {
                filePathView.onBackPressed();
                return;
            }
        }
        super.onBackPressed();
    }
}
