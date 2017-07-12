package com.fastaccess.ui.modules.repos.code.files.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import com.annimon.stream.Objects;
import com.evernote.android.state.State;
import com.fastaccess.R;
import com.fastaccess.data.dao.NameParser;
import com.fastaccess.data.dao.model.AbstractRepo;
import com.fastaccess.data.dao.model.Repo;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.scheme.LinkParserHelper;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.main.MainActivity;
import com.fastaccess.ui.modules.repos.RepoPagerActivity;
import com.fastaccess.ui.modules.repos.code.files.paths.RepoFilePathFragment;

import net.grandcentrix.thirtyinch.TiPresenter;

/**
 * Created by Kosh on 08 Apr 2017, 4:24 PM
 */

public class RepoFilesActivity extends BaseActivity {

    @State String login;
    @State String repoId;

    public static void startActivity(@NonNull Context context, @NonNull String url, boolean isEnterprise) {
        if (!InputHelper.isEmpty(url)) {
            Intent intent = ActivityHelper.editBundle(getIntent(context, url), isEnterprise);
            context.startActivity(intent);
        }
    }

    public static Intent getIntent(@NonNull Context context, @NonNull String url) {
        boolean isEnterprise = LinkParserHelper.isEnterprise(url);
        if (isEnterprise) {
            url = url.replace("api/v3/", "");
            if (url.contains("/raw")) {
                url = url.replace("/raw", "");
            }
        }
        Uri uri = Uri.parse(url);
        if (uri.getPathSegments() != null && uri.getPathSegments().size() > 3) {
            String login = null;
            String repoId = null;
            String branch = null;
            StringBuilder path = new StringBuilder();
            boolean startWithRepo = false;
            if (uri.getPathSegments().get(0).equalsIgnoreCase("repos")) {
                login = uri.getPathSegments().get(1);
                repoId = uri.getPathSegments().get(2);
                branch = uri.getQueryParameter("ref");
                startWithRepo = true;
            } else if (uri.getPathSegments().get(0).equalsIgnoreCase("repositories")) {
                String id = uri.getPathSegments().get(1);
                try {
                    long longRepoId = Long.valueOf(id);
                    if (longRepoId != 0) {
                        Repo repo = AbstractRepo.getRepo(longRepoId);
                        if (repo != null) {
                            NameParser nameParser = new NameParser(repo.getHtmlUrl());
                            if (nameParser.getUsername() != null && nameParser.getName() != null) {
                                login = nameParser.getUsername();
                                repoId = nameParser.getName();
                                branch = uri.getQueryParameter("ref");
                            }
                        }
                    }
                } catch (NumberFormatException ignored) {
                    return new Intent(context, MainActivity.class);
                }
            } else {
                login = uri.getPathSegments().get(0);
                repoId = uri.getPathSegments().get(1);
                branch = uri.getPathSegments().get(2);
            }
            for (int i = startWithRepo ? 4 : 3; i < uri.getPathSegments().size() - 1; i++) {
                String appendedPath = uri.getPathSegments().get(i);
                path.append("/").append(appendedPath);
            }
            if (!InputHelper.isEmpty(repoId) && !InputHelper.isEmpty(login)) {
                Intent intent = new Intent(context, RepoFilesActivity.class);
                intent.putExtras(Bundler.start()
                        .put(BundleConstant.ID, repoId)
                        .put(BundleConstant.EXTRA, login)
                        .put(BundleConstant.EXTRA_TWO, path.toString())
                        .put(BundleConstant.EXTRA_THREE, branch)
                        .put(BundleConstant.IS_ENTERPRISE, isEnterprise)
                        .end());
                return intent;
            }
            return new Intent(context, MainActivity.class);
        }
        return new Intent(context, MainActivity.class);
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
                    .add(R.id.fragmentContainer, RepoFilePathFragment.newInstance(login, repoId, path, defaultBranch, true), "RepoFilePathFragment")
                    .commit();
        }
        setTitle(String.format("%s/%s", login, repoId));
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = ActivityHelper.editBundle(RepoPagerActivity.createIntent(this, repoId, login), isEnterprise());
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onBackPressed() {
        RepoFilePathFragment filePathView = (RepoFilePathFragment) AppHelper.getFragmentByTag(getSupportFragmentManager(), "RepoFilePathFragment");
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
