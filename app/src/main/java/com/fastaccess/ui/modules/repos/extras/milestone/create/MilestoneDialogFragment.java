package com.fastaccess.ui.modules.repos.extras.milestone.create;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.MilestoneModel;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.ui.base.BaseDialogFragment;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.repos.extras.milestone.MilestoneMvp;
import com.fastaccess.ui.modules.repos.issues.issue.details.IssuePagerMvp;
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.PullRequestPagerMvp;

import net.grandcentrix.thirtyinch.TiPresenter;

/**
 * Created by Kosh on 04 Mar 2017, 10:58 PM
 */

public class MilestoneDialogFragment extends BaseDialogFragment implements MilestoneMvp.OnMilestoneSelected {

    private IssuePagerMvp.View issueCallback;
    private PullRequestPagerMvp.View pullRequestCallback;
    private MilestoneMvp.OnMilestoneSelected milestoneCallback;

    public static MilestoneDialogFragment newInstance(@NonNull String login, @NonNull String repo) {
        MilestoneDialogFragment view = new MilestoneDialogFragment();
        view.setArguments(Bundler.start()
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.ID, repo)
                .end());
        return view;
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IssuePagerMvp.View) {
            issueCallback = (IssuePagerMvp.View) context;
        } else if (getParentFragment() instanceof IssuePagerMvp.View) {
            issueCallback = (IssuePagerMvp.View) getParentFragment();
        }
        if (context instanceof PullRequestPagerMvp.View) {
            pullRequestCallback = (PullRequestPagerMvp.View) context;
        } else if (getParentFragment() instanceof PullRequestPagerMvp.View) {
            pullRequestCallback = (PullRequestPagerMvp.View) getParentFragment();
        }

        if (context instanceof MilestoneMvp.OnMilestoneSelected) {
            milestoneCallback = (MilestoneMvp.OnMilestoneSelected) context;
        } else if (getParentFragment() instanceof MilestoneMvp.OnMilestoneSelected) {
            milestoneCallback = (MilestoneMvp.OnMilestoneSelected) getParentFragment();
        }
    }

    @Override public void onDetach() {
        super.onDetach();
    }

    @NonNull @Override public TiPresenter providePresenter() {
        return new BasePresenter();
    }

    @Override protected int fragmentLayout() {
        return R.layout.single_container_layout;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Bundle bundle = getArguments();
            com.fastaccess.ui.modules.repos.extras.milestone.MilestoneDialogFragment milestoneView = new com.fastaccess.ui.modules.repos.extras
                    .milestone.MilestoneDialogFragment();
            milestoneView.setArguments(bundle);
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.singleContainer, milestoneView, com.fastaccess.ui.modules.repos.extras.milestone.MilestoneDialogFragment.TAG)
                    .commit();
        }
    }

    @Override public void onMilestoneSelected(@NonNull MilestoneModel milestoneModel) {
        if (issueCallback != null) issueCallback.onMileStoneSelected(milestoneModel);
        if (pullRequestCallback != null) pullRequestCallback.onMileStoneSelected(milestoneModel);
        if (milestoneCallback != null) milestoneCallback.onMilestoneSelected(milestoneModel);
    }
}
