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
import com.fastaccess.data.dao.MilestoneModel;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.data.dao.types.IssueState;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.Logger;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.ui.adapter.FragmentsPagerAdapter;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.modules.repos.RepoPagerActivity;
import com.fastaccess.ui.modules.repos.RepoPagerMvp;
import com.fastaccess.ui.modules.repos.extras.assignees.AssigneesDialogFragment;
import com.fastaccess.ui.modules.repos.extras.labels.LabelsDialogFragment;
import com.fastaccess.ui.modules.repos.extras.milestone.create.MilestoneDialogFragment;
import com.fastaccess.ui.modules.repos.issues.create.CreateIssueActivity;
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.timeline.timeline.PullRequestTimelineFragment;
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.merge.MergePullRequestDialogFragment;
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
import icepick.State;

/**
 * Created by Kosh on 10 Dec 2016, 9:23 AM
 */

public class PullRequestPagerActivity extends BaseActivity<PullRequestPagerMvp.View, PullRequestPagerPresenter>
        implements PullRequestPagerMvp.View {

    @BindView(R.id.startGist) ForegroundImageView startGist;
    @BindView(R.id.forkGist) ForegroundImageView forkGist;
    @BindView(R.id.avatarLayout) AvatarLayout avatarLayout;
    @BindView(R.id.headerTitle) FontTextView title;
    @BindView(R.id.size) FontTextView size;
    @BindView(R.id.date) FontTextView date;
    @BindView(R.id.tabs) TabLayout tabs;
    @BindView(R.id.pager) ViewPagerView pager;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.detailsIcon) View detailsIcon;
    @State boolean isClosed;
    @State boolean isOpened;

    public static Intent createIntent(@NonNull Context context, @NonNull String repoId, @NonNull String login, int number) {
        return createIntent(context, repoId, login, number, false);

    }

    public static Intent createIntent(@NonNull Context context, @NonNull String repoId, @NonNull String login, int number, boolean showRepoBtn) {
        Intent intent = new Intent(context, PullRequestPagerActivity.class);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.ID, number)
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.EXTRA_TWO, repoId)
                .put(BundleConstant.EXTRA_THREE, showRepoBtn)
                .end());
        return intent;

    }

    @OnClick(R.id.detailsIcon) void onTitleClick() {
        if (getPresenter().getPullRequest() != null && !InputHelper.isEmpty(getPresenter().getPullRequest().getTitle()))
            MessageDialogView.newInstance(getString(R.string.details), getPresenter().getPullRequest().getTitle())
                    .show(getSupportFragmentManager(), MessageDialogView.TAG);
    }

    @OnClick(R.id.fab) void onAddComment() {
        if (pager == null || pager.getAdapter() == null) return;
        PullRequestTimelineFragment view = (PullRequestTimelineFragment) pager.getAdapter().instantiateItem(pager, 0);
        if (view != null) {
            view.onStartNewComment();
        }
    }

    @Override protected int layout() {
        return R.layout.issue_pager_activity;
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

    @NonNull @Override public PullRequestPagerPresenter providePresenter() {
        return new PullRequestPagerPresenter();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getPresenter().onActivityCreated(getIntent());
        } else {
            if (getPresenter().isApiCalled()) onSetupIssue();
        }
        startGist.setVisibility(View.GONE);
        forkGist.setVisibility(View.GONE);
        if (getPresenter().showToRepoBtn()) showNavToRepoItem();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == BundleConstant.REQUEST_CODE) {
                Bundle bundle = data.getExtras();
                PullRequest pullRequest = bundle.getParcelable(BundleConstant.ITEM);
                if (pullRequest != null) getPresenter().onUpdatePullRequest(pullRequest);
            }
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pull_request_menu, menu);
        menu.findItem(R.id.merge).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onNavToRepoClicked();
            return true;
        }
        PullRequest pullRequest = getPresenter().getPullRequest();
        if (pullRequest == null) return false;
        if (item.getItemId() == R.id.share) {
            ActivityHelper.shareUrl(this, pullRequest.getHtmlUrl());
            return true;
        } else if (item.getItemId() == R.id.closeIssue) {
            MessageDialogView.newInstance(
                    pullRequest.getState() == IssueState.open ? getString(R.string.close_issue) : getString(R.string.re_open_issue),
                    getString(R.string.confirm_message), Bundler.start().put(BundleConstant.EXTRA, true).end())
                    .show(getSupportFragmentManager(), MessageDialogView.TAG);
            return true;
        } else if (item.getItemId() == R.id.lockIssue) {
            MessageDialogView.newInstance(
                    getPresenter().isLocked() ? getString(R.string.unlock_issue) : getString(R.string.lock_issue),
                    getPresenter().isLocked() ? getString(R.string.unlock_issue_details) : getString(R.string.lock_issue_details),
                    Bundler.start().put(BundleConstant.EXTRA_TWO, true)
                            .put(BundleConstant.YES_NO_EXTRA, true)
                            .end())
                    .show(getSupportFragmentManager(), MessageDialogView.TAG);
            return true;
        } else if (item.getItemId() == R.id.labels) {
            getPresenter().onLoadLabels();
            return true;
        } else if (item.getItemId() == R.id.edit) {
            CreateIssueActivity.startForResult(this, getPresenter().getLogin(), getPresenter().getRepoId(), pullRequest);
            return true;
        } else if (item.getItemId() == R.id.milestone) {
            MilestoneDialogFragment.newInstance(getPresenter().getLogin(), getPresenter().getRepoId())
                    .show(getSupportFragmentManager(), "MilestoneDialogFragment");
            return true;
        } else if (item.getItemId() == R.id.assignees) {
            AssigneesDialogFragment.newInstance(getPresenter().getLogin(), getPresenter().getRepoId(), true)
                    .show(getSupportFragmentManager(), "AssigneesDialogFragment");
            return true;
        } else if (item.getItemId() == R.id.reviewers) {
            AssigneesDialogFragment.newInstance(getPresenter().getLogin(), getPresenter().getRepoId(), false)
                    .show(getSupportFragmentManager(), "AssigneesDialogFragment");
            return true;
        } else if (item.getItemId() == R.id.merge) {
            if (getPresenter().getPullRequest() != null) {
                String msg = getPresenter().getPullRequest().getTitle();
                MergePullRequestDialogFragment.newInstance(msg).show(getSupportFragmentManager(), "MergePullRequestDialogFragment");
            }
        } else if (item.getItemId() == R.id.browser) {
            ActivityHelper.startCustomTab(this, pullRequest.getHtmlUrl());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem closeIssue = menu.findItem(R.id.closeIssue);
        MenuItem lockIssue = menu.findItem(R.id.lockIssue);
        MenuItem milestone = menu.findItem(R.id.milestone);
        MenuItem labels = menu.findItem(R.id.labels);
        MenuItem assignees = menu.findItem(R.id.assignees);
        MenuItem edit = menu.findItem(R.id.edit);
        MenuItem editMenu = menu.findItem(R.id.editMenu);
        MenuItem merge = menu.findItem(R.id.merge);
        MenuItem reviewers = menu.findItem(R.id.reviewers);
        boolean isOwner = getPresenter().isOwner();
        boolean isLocked = getPresenter().isLocked();
        boolean isCollaborator = getPresenter().isCollaborator();
        boolean isRepoOwner = getPresenter().isRepoOwner();
        boolean isMergable = getPresenter().isMergeable();
        merge.setVisible(isMergable && (isRepoOwner || isCollaborator));
        reviewers.setVisible((isRepoOwner || isCollaborator));
        editMenu.setVisible(isOwner || isCollaborator || isRepoOwner);
        milestone.setVisible(isCollaborator || isRepoOwner);
        labels.setVisible(isCollaborator || isRepoOwner);
        assignees.setVisible(isCollaborator || isRepoOwner);
        edit.setVisible(isCollaborator || isRepoOwner || isOwner);
        if (getPresenter().getPullRequest() != null) {
            closeIssue.setVisible(isRepoOwner || (isOwner || isCollaborator) && getPresenter().getPullRequest().getState() == IssueState.open);
            lockIssue.setVisible(isRepoOwner || (isOwner || isCollaborator) && getPresenter().getPullRequest().getState() == IssueState.open);
            closeIssue.setTitle(getPresenter().getPullRequest().getState() == IssueState.closed ? getString(R.string.re_open) : getString(R.string
                    .close));
            lockIssue.setTitle(isLocked ? getString(R.string.unlock_issue) : getString(R.string.lock_issue));
        } else {
            closeIssue.setVisible(false);
            lockIssue.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override public void onSetupIssue() {
        hideProgress();
        if (getPresenter().getPullRequest() == null) {
            return;
        }
        supportInvalidateOptionsMenu();
        PullRequest pullRequest = getPresenter().getPullRequest();
        setTitle(String.format("#%s", pullRequest.getNumber()));
        boolean isMerge = !InputHelper.isEmpty(pullRequest.getMergedAt());
        date.setText(getPresenter().getMergeBy(pullRequest, getApplicationContext())+"\n"+pullRequest.getRepoId());
        size.setVisibility(View.GONE);
        User userModel = pullRequest.getUser();
        if (userModel != null) {
            title.setText(SpannableBuilder.builder().append(userModel.getLogin()).append("/").append(pullRequest.getTitle()));
            avatarLayout.setUrl(userModel.getAvatarUrl(), userModel.getLogin());
        } else {
            title.setText(SpannableBuilder.builder().append(pullRequest.getTitle()));
        }
        detailsIcon.setVisibility(InputHelper.isEmpty(pullRequest.getTitle()) || !ViewHelper.isEllipsed(title) ? View.GONE : View.VISIBLE);
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
        if (tabs.getTabAt(2) != null) {
            tabs.getTabAt(2)
                    .setText(SpannableBuilder.builder()
                            .append(getString(R.string.files))
                            .append(" ")
                            .append("(")
                            .append(String.valueOf(pullRequest.getChangedFiles()))
                            .append(")"));
        }
        if (tabs.getTabAt(1) != null) {
            tabs.getTabAt(1)
                    .setText(SpannableBuilder.builder()
                            .append(getString(R.string.commits))
                            .append(" ")
                            .append("(")
                            .append(String.valueOf(pullRequest.getCommits()))
                            .append(")"));
        }
        if (tabs.getTabAt(0) != null) {
            tabs.getTabAt(0)
                    .setText(SpannableBuilder.builder()
                            .append(getString(R.string.details))
                            .append(" ")
                            .append("(")
                            .append(String.valueOf(pullRequest.getComments()))
                            .append(")"));
        }
        hideShowFab();
    }

    @Override public void onMessageDialogActionClicked(boolean isOk, @Nullable Bundle bundle) {
        super.onMessageDialogActionClicked(isOk, bundle);
        if (isOk) {
            getPresenter().onHandleConfirmDialog(bundle);
        }
    }

    @Override public void onLabelsRetrieved(@NonNull List<LabelModel> items) {
        hideProgress();
        LabelsDialogFragment.newInstance(items, getPresenter().getPullRequest() != null ? getPresenter().getPullRequest().getLabels() : null,
                getPresenter().getRepoId(), getPresenter().getLogin())
                .show(getSupportFragmentManager(), "LabelsDialogFragment");
    }

    @Override public void onUpdateMenu() {
        supportInvalidateOptionsMenu();
    }

    @Override public void onSelectedLabels(@NonNull ArrayList<LabelModel> labels) {
        Logger.e(labels, labels.size());
        getPresenter().onPutLabels(labels);
    }

    @Override public void showSuccessIssueActionMsg(boolean isClose) {
        hideProgress();
        if (isClose) {
            isOpened = false;
            isClosed = true;
            showMessage(getString(R.string.success), getString(R.string.success_closed));
        } else {
            isOpened = true;
            isClosed = false;
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

    @Override public void onUpdateTimeline() {
        showMessage(R.string.success, R.string.labels_added_successfully);
        PullRequestTimelineFragment pullRequestDetailsView = (PullRequestTimelineFragment) pager.getAdapter().instantiateItem(pager, 0);
        if (pullRequestDetailsView != null) {
            pullRequestDetailsView.onRefresh();
        }
    }

    @Override public void onMileStoneSelected(@NonNull MilestoneModel milestoneModel) {
        getPresenter().onPutMilestones(milestoneModel);
    }

    @Override public void onFinishActivity() {
        hideProgress();
        finish();
    }

    @Override public void onMerge(@NonNull String msg) {
        getPresenter().onMerge(msg);
    }

    @Override protected void onNavToRepoClicked() {
        startActivity(RepoPagerActivity.createIntent(this, getPresenter().getRepoId(), getPresenter().getLogin(), RepoPagerMvp.PULL_REQUEST));
        finish();
    }

    @Override public void finish() {
        Intent intent = new Intent();
        intent.putExtras(Bundler.start()
                .put(BundleConstant.EXTRA, isClosed)
                .put(BundleConstant.EXTRA_TWO, isOpened)
                .end());
        setResult(RESULT_OK, intent);
        super.finish();
    }

    @Override public void onSelectedAssignees(@NonNull ArrayList<User> users, boolean isAssignees) {
        hideProgress();
        getPresenter().onPutAssignees(users, isAssignees);
    }

    private void hideShowFab() {
        if (getPresenter().isLocked() && !getPresenter().isOwner()) {
            fab.hide();
            return;
        }
        if (pager.getCurrentItem() == 0) {
            fab.show();
        } else {
            fab.hide();
        }
    }
}
