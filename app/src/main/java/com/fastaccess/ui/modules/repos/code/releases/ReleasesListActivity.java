package com.fastaccess.ui.modules.repos.code.releases;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import com.evernote.android.state.State;
import com.fastaccess.R;
import com.fastaccess.data.dao.NameParser;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.repos.RepoPagerActivity;

/**
 * Created by Kosh on 25 May 2017, 7:13 PM
 */

public class ReleasesListActivity extends BaseActivity {

    @State String repoId;
    @State String login;


    public static Intent getIntent(@NonNull Context context, @NonNull String username, @NonNull String repoId) {
        Intent intent = new Intent(context, ReleasesListActivity.class);
        intent.putExtras(Bundler.start().put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA, username)
                .end());
        return intent;
    }

    public static Intent getIntent(@NonNull Context context, @NonNull String username, @NonNull String repoId,
                                   @NonNull String tag, boolean isEnterprise) {
        Intent intent = new Intent(context, ReleasesListActivity.class);
        intent.putExtras(Bundler.start().put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA, username)
                .put(BundleConstant.EXTRA_THREE, tag)
                .put(BundleConstant.IS_ENTERPRISE, isEnterprise)
                .end());
        return intent;
    }

    public static Intent getIntent(@NonNull Context context, @NonNull String username, @NonNull String repoId,
                                   long id, boolean isEnterprise) {
        Intent intent = new Intent(context, ReleasesListActivity.class);
        intent.putExtras(Bundler.start().put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA, username)
                .put(BundleConstant.EXTRA_TWO, id)
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

    @NonNull @Override public BasePresenter providePresenter() {
        return new BasePresenter();
    }

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            if (getIntent() == null || getIntent().getExtras() == null) {
                finish();
            } else {
                Bundle bundle = getIntent().getExtras();
                repoId = bundle.getString(BundleConstant.ID);
                login = bundle.getString(BundleConstant.EXTRA);
                //noinspection ConstantConditions
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, RepoReleasesFragment
                                .newInstance(repoId, login, bundle.getString(BundleConstant.EXTRA_THREE),
                                        bundle.getLong(BundleConstant.EXTRA_TWO)))
                        .commit();

                setTaskName(repoId + "/" + login + " " + getString(R.string.releases));
            }
        }
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NameParser parser = new NameParser("");
            parser.setName(repoId);
            parser.setUsername(login);
            parser.setEnterprise(isEnterprise());
            RepoPagerActivity.startRepoPager(this, parser);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
