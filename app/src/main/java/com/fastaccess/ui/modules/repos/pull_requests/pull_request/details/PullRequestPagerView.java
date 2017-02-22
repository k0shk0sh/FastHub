package com.fastaccess.ui.modules.repos.pull_requests.pull_request.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.FragmentPagerAdapterModel;
import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.data.dao.PullRequestModel;
import com.fastaccess.data.dao.UserModel;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.Logger;
import com.fastaccess.ui.adapter.FragmentsPagerAdapter;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.modules.repos.issues.issue.details.comments.IssueCommentsView;
import com.fastaccess.ui.modules.repos.labels.LabelsView;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.ForegroundImageView;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.ViewPagerView;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Kosh on 10 Dec 2016, 9:23 AM
 */

public class PullRequestPagerView extends BaseActivity<PullRequestPagerMvp.View, PullRequestPagerPresenter> implements PullRequestPagerMvp.View {

    @BindView(R.id.startGist) ForegroundImageView startGist;
    @BindView(R.id.forkGist) ForegroundImageView forkGist;
    @BindView(R.id.avatarLayout) AvatarLayout avatarLayout;
    @BindView(R.id.headerTitle) FontTextView title;
    @BindView(R.id.size) FontTextView size;
    @BindView(R.id.date) FontTextView date;
    @BindView(R.id.tabs) TabLayout tabs;
    @BindView(R.id.pager) ViewPagerView pager;
    @BindView(R.id.fab) FloatingActionButton fab;

    public static Intent createIntent(@NonNull Context context, @NonNull String repoId, @NonNull String login, int number) {
        Intent intent = new Intent(context, PullRequestPagerView.class);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.ID, number)
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.EXTRA_TWO, repoId)
                .end());
        return intent;

    }

    @OnClick(R.id.headerTitle) void onTitleClick() {
        if (getPresenter().getPullRequest() != null && !InputHelper.isEmpty(getPresenter().getPullRequest().getTitle()))
            showMessage(getString(R.string.details), getPresenter().getPullRequest().getTitle());
    }

    @OnClick(R.id.fab) void onAddComment() {
        IssueCommentsView view = (IssueCommentsView) pager.getAdapter().instantiateItem(pager, 2);
        if (view != null) {
            view.onStartNewComment();
        }
    }

    @Override protected int layout() {
        return R.layout.issue_pager_activity;
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

    @NonNull @Override public PullRequestPagerPresenter providePresenter() {
        return new PullRequestPagerPresenter();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getPresenter().onActivityCreated(getIntent());
        } else {
            onSetupIssue();
        }
        startGist.setVisibility(View.GONE);
        forkGist.setVisibility(View.GONE);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pull_request_menu, menu);
        menu.findItem(R.id.merge).setVisible(getPresenter().isRepoOwner() && getPresenter().isMergeable());
        menu.findItem(R.id.lockIssue).setVisible(getPresenter().isOwner() || getPresenter().isRepoOwner());
        menu.findItem(R.id.labels).setVisible(getPresenter().isRepoOwner());
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.share) {
            if (getPresenter().getPullRequest() != null) ActivityHelper.shareUrl(this, getPresenter().getPullRequest().getHtmlUrl());
            return true;
        } else if (item.getItemId() == R.id.lockIssue) {
            MessageDialogView.newInstance(
                    getPresenter().isLocked() ? getString(R.string.unlock_issue) : getString(R.string.lock_issue),
                    getPresenter().isLocked() ? getString(R.string.unlock_issue_details) : getString(R.string.lock_issue_details),
                    Bundler.start().put(BundleConstant.EXTRA, true).end())
                    .show(getSupportFragmentManager(), MessageDialogView.TAG);
            return true;
        } else if (item.getItemId() == R.id.merge) {
            getPresenter().onMerge();
        } else if (item.getItemId() == R.id.labels) {
            getPresenter().onLoadLabels();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem lockIssue = menu.findItem(R.id.lockIssue);
        boolean isRepoOwner = getPresenter().isRepoOwner();
        boolean isLocked = getPresenter().isLocked();
        boolean isMergable = getPresenter().isMergeable();
        lockIssue.setTitle(isLocked ? getString(R.string.unlock_issue) : getString(R.string.lock_issue));
        lockIssue.setVisible(isRepoOwner);
        menu.findItem(R.id.labels).setVisible(isRepoOwner);
        menu.findItem(R.id.merge).setVisible(isMergable && (isRepoOwner));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override public void onSetupIssue() {
        hideProgress();
        if (getPresenter().getPullRequest() == null) {
            finish();
            return;
        }
        supportInvalidateOptionsMenu();
        PullRequestModel pullRequest = getPresenter().getPullRequest();
        setTitle(String.format("#%s", pullRequest.getNumber()));
        UserModel userModel = pullRequest.getUser();
        if (userModel != null) {
            title.setText(SpannableBuilder.builder().append(userModel.getLogin()).append("/").append(pullRequest.getTitle()));
            size.setVisibility(View.GONE);
            boolean isMerge = !InputHelper.isEmpty(pullRequest.getMergedAt());
            int status = !isMerge ? pullRequest.getState().getStatus() : R.string.merged;
            date.setText(getPresenter().getMergeBy(pullRequest, getApplicationContext()));
            avatarLayout.setUrl(userModel.getAvatarUrl(), userModel.getLogin());
        } else {
            title.setText(SpannableBuilder.builder().append(pullRequest.getTitle()));
        }
        pager.setAdapter(new FragmentsPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapterModel.buildForPullRequest(this, pullRequest)));
        tabs.setupWithViewPager(pager);
        if (!getPresenter().isLocked() || getPresenter().isOwner()) {
            pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    hideShowFab();
                }
            });
        }
        hideShowFab();
    }

    @Override public void showSuccessIssueActionMsg(boolean isClose) {
        hideProgress();
        if (isClose) {
            showMessage(getString(R.string.success), getString(R.string.success_closed));
        } else {
            showMessage(getString(R.string.success), getString(R.string.success_re_opened));
        }
    }

    @Override public void showErrorIssueActionMsg(boolean isClose) {
        hideProgress();
        if (isClose) {
            showMessage(getString(R.string.error), getString(R.string.error_closing_issue));
        } else {
            showMessage(getString(R.string.error), getString(R.string.error_re_opening_issue));
        }
    }

    @Override public void onMessageDialogActionClicked(boolean isOk, @Nullable Bundle bundle) {
        super.onMessageDialogActionClicked(isOk, bundle);
        if (isOk) {
            getPresenter().onHandleConfirmDialog(bundle);
        }
    }

    @Override public void onLabelsRetrieved(@NonNull List<LabelModel> items) {
        hideProgress();
        LabelsView.newInstance(items)
                .show(getSupportFragmentManager(), "LabelsView");
    }

    @Override public void onSelectedLabels(@NonNull ArrayList<LabelModel> labels) {
        Logger.e(labels, labels.size());
        getPresenter().onPutLabels(labels);
    }

    private void hideShowFab() {
        if (getPresenter().isLocked() && !getPresenter().isOwner()) {
            fab.hide();
            return;
        }
        if (pager.getCurrentItem() == 2) {
            fab.show();
        } else {
            fab.hide();
        }
    }
}
