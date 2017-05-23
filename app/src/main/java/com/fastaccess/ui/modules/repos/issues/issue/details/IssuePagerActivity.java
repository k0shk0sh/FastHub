package com.fastaccess.ui.modules.repos.issues.issue.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.FragmentPagerAdapterModel;
import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.data.dao.MilestoneModel;
import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.data.dao.types.IssueState;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.ui.adapter.FragmentsPagerAdapter;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.modules.repos.RepoPagerActivity;
import com.fastaccess.ui.modules.repos.RepoPagerMvp;
import com.fastaccess.ui.modules.repos.extras.assignees.AssigneesDialogFragment;
import com.fastaccess.ui.modules.repos.extras.labels.LabelsDialogFragment;
import com.fastaccess.ui.modules.repos.extras.milestone.create.MilestoneDialogFragment;
import com.fastaccess.ui.modules.repos.issues.create.CreateIssueActivity;
import com.fastaccess.ui.modules.repos.issues.issue.details.timeline.IssueTimelineFragment;
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

public class IssuePagerActivity extends BaseActivity<IssuePagerMvp.View, IssuePagerPresenter> implements IssuePagerMvp.View {

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

    public static Intent createIntent(@NonNull Context context, @NonNull String repoId, @NonNull String login, int number, boolean showToRepoBtn) {
        Intent intent = new Intent(context, IssuePagerActivity.class);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.ID, number)
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.EXTRA_TWO, repoId)
                .put(BundleConstant.EXTRA_THREE, showToRepoBtn)
                .end());
        return intent;

    }

    @OnClick(R.id.detailsIcon) void onTitleClick() {
        if (getPresenter().getIssue() != null && !InputHelper.isEmpty(getPresenter().getIssue().getTitle()))
            MessageDialogView.newInstance(getString(R.string.details), getPresenter().getIssue().getTitle(), false, true)
                    .show(getSupportFragmentManager(), MessageDialogView.TAG);
    }

    @OnClick(R.id.fab) void onAddComment() {
        if (pager != null && pager.getAdapter() != null) {
            IssueTimelineFragment view = (IssueTimelineFragment) pager.getAdapter().instantiateItem(pager, 0);
            if (view != null) {
                view.onStartNewComment();
            }
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

    @NonNull @Override public IssuePagerPresenter providePresenter() {
        return new IssuePagerPresenter();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tabs.setVisibility(View.GONE);
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
                Issue issueModel = bundle.getParcelable(BundleConstant.ITEM);
                if (issueModel != null) getPresenter().onUpdateIssue(issueModel);
            }
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.issue_menu, menu);
        menu.findItem(R.id.closeIssue).setVisible(getPresenter().isOwner());
        menu.findItem(R.id.lockIssue).setVisible(getPresenter().isOwner());
        menu.findItem(R.id.labels).setVisible(getPresenter().isRepoOwner());
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onNavToRepoClicked();
            return true;
        }
        Issue issueModel = getPresenter().getIssue();
        if (issueModel == null) return false;
        if (item.getItemId() == R.id.share) {
            ActivityHelper.shareUrl(this, getPresenter().getIssue().getHtmlUrl());
            return true;
        } else if (item.getItemId() == R.id.closeIssue) {
            MessageDialogView.newInstance(
                    issueModel.getState() == IssueState.open ? getString(R.string.close_issue) : getString(R.string.re_open_issue),
                    getString(R.string.confirm_message), Bundler.start().put(BundleConstant.EXTRA, true)
                            .put(BundleConstant.YES_NO_EXTRA, true).end())
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
            CreateIssueActivity.startForResult(this, getPresenter().getLogin(), getPresenter().getRepoId(), getPresenter().getIssue());
            return true;
        } else if (item.getItemId() == R.id.milestone) {
            MilestoneDialogFragment.newInstance(getPresenter().getLogin(), getPresenter().getRepoId())
                    .show(getSupportFragmentManager(), "MilestoneDialogFragment");
            return true;
        } else if (item.getItemId() == R.id.assignees) {
            AssigneesDialogFragment.newInstance(getPresenter().getLogin(), getPresenter().getRepoId(), true)
                    .show(getSupportFragmentManager(), "AssigneesDialogFragment");
            return true;
        } else if (item.getItemId() == R.id.subscribe) {
            getPresenter().onSubscribeOrMute(false);
            return true;
        } else if (item.getItemId() == R.id.mute) {
            getPresenter().onSubscribeOrMute(true);
            return true;
        } else if (item.getItemId() == R.id.browser) {
            ActivityHelper.startCustomTab(this, issueModel.getHtmlUrl());
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
        boolean isOwner = getPresenter().isOwner();
        boolean isLocked = getPresenter().isLocked();
        boolean isCollaborator = getPresenter().isCollaborator();
        boolean isRepoOwner = getPresenter().isRepoOwner();
        editMenu.setVisible(isOwner || isCollaborator || isRepoOwner);
        milestone.setVisible(isCollaborator || isRepoOwner);
        labels.setVisible(isCollaborator || isRepoOwner);
        assignees.setVisible(isCollaborator || isRepoOwner);
        edit.setVisible(isCollaborator || isRepoOwner || isOwner);
        menu.findItem(R.id.closeIssue).setVisible(isOwner || isCollaborator);
        menu.findItem(R.id.lockIssue).setVisible(isOwner || isCollaborator);
        menu.findItem(R.id.labels).setVisible(getPresenter().isRepoOwner() || isCollaborator);
        if (isOwner) {
            if (getPresenter().getIssue() == null) return super.onPrepareOptionsMenu(menu);
            closeIssue.setTitle(getPresenter().getIssue().getState() == IssueState.closed ? getString(R.string.re_open) : getString(R.string.close));
            lockIssue.setTitle(isLocked ? getString(R.string.unlock_issue) : getString(R.string.lock_issue));
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override public void onSetupIssue() {
        hideProgress();
        if (getPresenter().getIssue() == null) {
            return;
        }
        supportInvalidateOptionsMenu();
        Issue issueModel = getPresenter().getIssue();
        setTitle(String.format("#%s", issueModel.getNumber()));
        User userModel = issueModel.getUser();
        title.setText(issueModel.getTitle());
        detailsIcon.setVisibility(InputHelper.isEmpty(issueModel.getTitle()) || !ViewHelper.isEllipsed(title) ? View.GONE : View.VISIBLE);
        if (userModel != null) {
            size.setVisibility(View.GONE);
            String username;
            CharSequence parsedDate;
            if (issueModel.getState() == IssueState.closed) {
                username = issueModel.getClosedBy() != null ? issueModel.getClosedBy().getLogin() : "N/A";
                parsedDate = issueModel.getClosedAt() != null ? ParseDateFormat.getTimeAgo(issueModel.getClosedAt()) : "N/A";
            } else {
                parsedDate = ParseDateFormat.getTimeAgo(issueModel.getCreatedAt());
                username = issueModel.getUser() != null ? issueModel.getUser().getLogin() : "N/A";
            }
            date.setText(SpannableBuilder.builder()
                    .append(ContextCompat.getDrawable(this,
                            issueModel.getState() == IssueState.open ? R.drawable.ic_issue_opened_small : R.drawable.ic_issue_closed_small))
                    .append(" ")
                    .append(getString(issueModel.getState().getStatus()))
                    .append(" ").append(getString(R.string.by)).append(" ").append(username).append(" ")
                    .append(parsedDate).append("\n").append(issueModel.getRepoId()));
            avatarLayout.setUrl(userModel.getAvatarUrl(), userModel.getLogin());
        }
        pager.setAdapter(new FragmentsPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapterModel.buildForIssues(this, issueModel)));
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

    @Override public void onLabelsRetrieved(@NonNull List<LabelModel> items) {
        hideProgress();
        LabelsDialogFragment.newInstance(items, getPresenter().getIssue() != null ? getPresenter().getIssue().getLabels() : null,
                getPresenter().getRepoId(), getPresenter().getLogin())
                .show(getSupportFragmentManager(), "LabelsDialogFragment");
    }

    @Override public void onUpdateTimeline() {
        showMessage(R.string.success, R.string.labels_added_successfully);
        IssueTimelineFragment issueDetailsView = (IssueTimelineFragment) pager.getAdapter().instantiateItem(pager, 0);
        if (issueDetailsView != null) {
            issueDetailsView.onRefresh();
        }
    }

    @Override public void onUpdateMenu() {
        supportInvalidateOptionsMenu();
    }

    @Override public void onMileStoneSelected(@NonNull MilestoneModel milestoneModel) {
        getPresenter().onPutMilestones(milestoneModel);
    }

    @Override public void onFinishActivity() {
        hideProgress();
        finish();
    }

    @Override public void onMessageDialogActionClicked(boolean isOk, @Nullable Bundle bundle) {
        super.onMessageDialogActionClicked(isOk, bundle);
        if (isOk) {
            getPresenter().onHandleConfirmDialog(bundle);
        }
    }

    @Override public void onSelectedLabels(@NonNull ArrayList<LabelModel> labels) {
        getPresenter().onPutLabels(labels);
    }

    @Override public void onSelectedAssignees(@NonNull ArrayList<User> users, boolean isAssignee) {
        getPresenter().onPutAssignees(users);
    }

    @Override protected void onNavToRepoClicked() {
        startActivity(RepoPagerActivity.createIntent(this, getPresenter().getRepoId(), getPresenter().getLogin(), RepoPagerMvp.ISSUES));
        finish();
    }

    @Override public void onBackPressed() {
        super.onBackPressed();
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

    private void hideShowFab() {
        if (getPresenter().isLocked() && !getPresenter().isOwner()) {
            fab.hide();
            return;
        }
        fab.show();
    }
}
