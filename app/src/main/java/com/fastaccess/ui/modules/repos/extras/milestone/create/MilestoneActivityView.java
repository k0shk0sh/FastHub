package com.fastaccess.ui.modules.repos.extras.milestone.create;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.R;
import com.fastaccess.data.dao.MilestoneModel;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.repos.extras.milestone.MilestoneMvp;
import com.fastaccess.ui.modules.repos.extras.milestone.MilestoneView;

import net.grandcentrix.thirtyinch.TiPresenter;

/**
 * Created by Kosh on 04 Mar 2017, 10:58 PM
 */

public class MilestoneActivityView extends BaseActivity implements MilestoneMvp.OnMilestoneSelected {

    public static final int CREATE_MILESTONE_RQ = 200;

    public static void startActivity(@NonNull Activity activity, @NonNull String login, @NonNull String repo) {
        Intent intent = new Intent(activity, MilestoneActivityView.class);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.ID, repo)
                .end());
        activity.startActivityForResult(intent, CREATE_MILESTONE_RQ);
    }

    @Override protected int layout() {
        return R.layout.single_container_layout;
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
            Bundle bundle = getIntent().getExtras();
            MilestoneView milestoneView = new MilestoneView();
            milestoneView.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.singleContainer, milestoneView, MilestoneView.TAG)
                    .commit();
        }
    }

    @Override public void onMilestoneSelected(@NonNull MilestoneModel milestoneModel) {
        Intent intent = new Intent();
        intent.putExtras(Bundler.start().put(BundleConstant.ITEM, milestoneModel).end());
        setResult(RESULT_OK, intent);
        finish();
    }
}
