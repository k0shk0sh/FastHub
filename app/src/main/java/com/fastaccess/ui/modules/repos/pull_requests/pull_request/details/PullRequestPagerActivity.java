package com.fastaccess.ui.modules.repos.pull_requests.pull_request.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.evernote.android.state.State;
import com.fastaccess.R;
import com.fastaccess.data.dao.CommentRequestModel;
import com.fastaccess.data.dao.FragmentPagerAdapterModel;
import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.data.dao.MilestoneModel;
import com.fastaccess.data.dao.ReviewRequestModel;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.data.dao.model.PinnedPullRequests;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.data.dao.types.IssueState;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.AnimHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.Logger;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.provider.scheme.LinkParserHelper;
import com.fastaccess.ui.adapter.FragmentsPagerAdapter;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.editor.comment.CommentEditorFragment;
import com.fastaccess.ui.modules.main.premium.PremiumActivity;
import com.fastaccess.ui.modules.repos.RepoPagerActivity;
import com.fastaccess.ui.modules.repos.RepoPagerMvp;
import com.fastaccess.ui.modules.repos.extras.assignees.AssigneesDialogFragment;
import com.fastaccess.ui.modules.repos.extras.labels.LabelsDialogFragment;
import com.fastaccess.ui.modules.repos.extras.milestone.create.MilestoneDialogFragment;
import com.fastaccess.ui.modules.repos.issues.create.CreateIssueActivity;
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.files.PullRequestFilesFragment;
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.timeline.timeline.PullRequestTimelineFragment;
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.merge.MergePullRequestDialogFragment;
import com.fastaccess.ui.modules.reviews.changes.ReviewChangesActivity;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.ForegroundImageView;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.ViewPagerView;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

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
    @BindView(R.id.reviewsCount) FontTextView reviewsCount;
    @BindView(R.id.prReviewHolder) CardView prReviewHolder;
    @State boolean isClosed;
    @State boolean isOpened;
    private CommentEditorFragment commentEditorFragment;

    public static Intent createIntent(@NonNull Context context, @NonNull String repoId, @NonNull String login, int number) {
        return createIntent(context, repoId, login, number, false);
    }

    public static Intent createIntent(@NonNull Context context, @NonNull String repoId, @NonNull String login, int number, boolean showRepoBtn) {
        return createIntent(context, repoId, login, number, showRepoBtn, false);
    }

    public static Intent createIntent(@NonNull Context context, @NonNull String repoId, @NonNull String login,
                                      int number, boolean showRepoBtn, boolean isEnterprise) {
        return createIntent(context, repoId, login, number, showRepoBtn, isEnterprise, 0);
    }

    public static Intent createIntent(@NonNull Context context, @NonNull String repoId, @NonNull String login,
                                      int number, boolean showRepoBtn, boolean isEnterprise, long commentId) {
        Intent intent = new Intent(context, PullRequestPagerActivity.class);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.ID, number)
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.EXTRA_TWO, repoId)
                .put(BundleConstant.EXTRA_THREE, showRepoBtn)
                .put(BundleConstant.IS_ENTERPRISE, isEnterprise)
                .put(BundleConstant.EXTRA_SIX, commentId)
                .end());
        return intent;
    }

    @OnClick(R.id.detailsIcon) void onTitleClick() {
        if (getPresenter().getPullRequest() != null && !InputHelper.isEmpty(getPresenter().getPullRequest().getTitle()))
            MessageDialogView.newInstance(String.format("%s/%s", getPresenter().getLogin(), getPresenter().getRepoId()),
                    getPresenter().getPullRequest().getTitle(), false, true)
                    .show(getSupportFragmentManager(), MessageDialogView.TAG);
    }

    @OnClick(R.id.submitReviews) void onSubmitReviews(View view) {
        addPrReview(view);
    }

    @OnClick(R.id.cancelReview) void onCancelReviews(View view) {
        MessageDialogView.newInstance(getString(R.string.cancel_reviews), getString(R.string.confirm_message),
                false, Bundler.start()
                        .put(BundleConstant.YES_NO_EXTRA, true)
                        .put(BundleConstant.EXTRA_TYPE, true)
                        .end())
                .show(getSupportFragmentManager(), MessageDialogView.TAG);
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
        commentEditorFragment = (CommentEditorFragment) getSupportFragmentManager().findFragmentById(R.id.commentFragment);
        if (savedInstanceState == null) {
            getPresenter().onActivityCreated(getIntent());
        } else {
            if (getPresenter().getPullRequest() != null) onSetupIssue(false);
        }
        fab.hide();
        startGist.setVisibility(View.GONE);
        forkGist.setVisibility(View.GONE);
        if (getPresenter().showToRepoBtn()) showNavToRepoItem();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == BundleConstant.REQUEST_CODE) {
                if (data == null) return;
                Bundle bundle = data.getExtras();
                PullRequest pullRequest = bundle.getParcelable(BundleConstant.ITEM);
                if (pullRequest != null) {
                    getPresenter().onUpdatePullRequest(pullRequest);
                } else {
                    getPresenter().onRefresh();
                }
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
            LabelsDialogFragment.newInstance(getPresenter().getPullRequest() != null ? getPresenter().getPullRequest().getLabels() : null,
                    getPresenter().getRepoId(), getPresenter().getLogin())
                    .show(getSupportFragmentManager(), "LabelsDialogFragment");
            return true;
        } else if (item.getItemId() == R.id.edit) {
            CreateIssueActivity.startForResult(this, getPresenter().getLogin(), getPresenter().getRepoId(), pullRequest, isEnterprise());
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
        } else if (item.getItemId() == R.id.reviewChanges) {
            if (PrefGetter.isProEnabled()) {
                addPrReview(item.getActionView() == null ? title : item.getActionView());
            } else {
                PremiumActivity.Companion.startActivity(this);
            }
            return true;
        } else if (item.getItemId() == R.id.subscribe) {
            getPresenter().onSubscribeOrMute(false);
            return true;
        } else if (item.getItemId() == R.id.mute) {
            getPresenter().onSubscribeOrMute(true);
            return true;
        } else if (item.getItemId() == R.id.pinUnpin) {
            if (PrefGetter.isProEnabled()) {
                getPresenter().onPinUnpinPullRequest();
            } else {
                PremiumActivity.Companion.startActivity(this);
            }
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
        MenuItem pinUnpin = menu.findItem(R.id.pinUnpin);
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
            boolean isPinned = PinnedPullRequests.isPinned(getPresenter().getPullRequest().getId());
            pinUnpin.setIcon(isPinned ? ContextCompat.getDrawable(this, R.drawable.ic_pin_filled)
                                      : ContextCompat.getDrawable(this, R.drawable.ic_pin));
            closeIssue.setVisible(isRepoOwner || (isOwner || isCollaborator) && getPresenter().getPullRequest().getState() == IssueState.open);
            lockIssue.setVisible(isRepoOwner || isCollaborator && getPresenter().getPullRequest().getState() == IssueState.open);
            closeIssue.setTitle(getPresenter().getPullRequest().getState() == IssueState.closed ? getString(R.string.re_open) : getString(R.string
                    .close));
            lockIssue.setTitle(isLocked ? getString(R.string.unlock_issue) : getString(R.string.lock_issue));
        } else {
            closeIssue.setVisible(false);
            lockIssue.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override public void onSetupIssue(boolean update) {
        hideProgress();
        if (getPresenter().getPullRequest() == null) {
            return;
        }
        invalidateOptionsMenu();
        PullRequest pullRequest = getPresenter().getPullRequest();
        setTaskName(pullRequest.getRepoId() + " - " + pullRequest.getTitle());
        updateViews(pullRequest);
        if (update) {
            PullRequestTimelineFragment issueDetailsView = getPullRequestTimelineFragment();
            if (issueDetailsView != null && getPresenter().getPullRequest() != null) {
                issueDetailsView.onUpdateHeader();
            }
        } else {
            if (pager.getAdapter() == null) {
                pager.setAdapter(new FragmentsPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapterModel.buildForPullRequest(this,
                        pullRequest)));
                tabs.setupWithViewPager(pager);
                tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager) {
                    @Override public void onTabReselected(TabLayout.Tab tab) {
                        super.onTabReselected(tab);
                        onScrollTop(tab.getPosition());
                    }
                });
            } else {
                onUpdateTimeline();
            }
        }
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override public void onPageSelected(int position) {
                hideShowFab();
                super.onPageSelected(position);

            }
        });
        initTabs(pullRequest);
        hideShowFab();
        AnimHelper.mimicFabVisibility(getPresenter().hasReviewComments(), prReviewHolder, null);
        reviewsCount.setText(String.format("%s", getPresenter().getCommitComment().size()));
    }

    @Override public void onScrollTop(int index) {
        if (pager == null || pager.getAdapter() == null) return;
        Fragment fragment = (BaseFragment) pager.getAdapter().instantiateItem(pager, index);
        if (fragment instanceof BaseFragment) {
            ((BaseFragment) fragment).onScrollTop(index);
        }
    }

    @Override public void onMessageDialogActionClicked(boolean isOk, @Nullable Bundle bundle) {
        super.onMessageDialogActionClicked(isOk, bundle);
        if (isOk) {
            if (bundle != null) {
                if (bundle.getBoolean(BundleConstant.EXTRA_TYPE)) {
                    hideAndClearReviews();
                    return;
                }
            }
            getPresenter().onHandleConfirmDialog(bundle);
        }
    }

    @Override public void onSelectedLabels(@NonNull ArrayList<LabelModel> labels) {
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
        supportInvalidateOptionsMenu();
        PullRequestTimelineFragment pullRequestDetailsView = getPullRequestTimelineFragment();
        if (pullRequestDetailsView != null && getPresenter().getPullRequest() != null) {
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

    @Override public void onUpdateMenu() {
        invalidateOptionsMenu();
    }

    @Override public void onAddComment(CommentRequestModel comment) {
        getPresenter().onAddComment(comment);
        AnimHelper.mimicFabVisibility(getPresenter().hasReviewComments(), prReviewHolder, null);
        reviewsCount.setText(String.format("%s", getPresenter().getCommitComment().size()));
        Logger.e(reviewsCount.getText(), prReviewHolder.getVisibility());
    }

    @Override public void onMerge(@NonNull String msg, @NonNull String mergeMethod) {
        getPresenter().onMerge(msg, mergeMethod);
    }

    @Override protected void onNavToRepoClicked() {
        Intent intent = ActivityHelper.editBundle(RepoPagerActivity.createIntent(this, getPresenter().getRepoId(),
                getPresenter().getLogin(), RepoPagerMvp.PULL_REQUEST), isEnterprise());
        startActivity(intent);
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

    @Nullable @Override public PullRequest getData() {
        return getPresenter().getPullRequest();
    }

    @Override public void onSendActionClicked(@NonNull String text, Bundle bundle) {
        PullRequestTimelineFragment fragment = getPullRequestTimelineFragment();
        if (fragment != null) {
            fragment.onHandleComment(text, bundle);
        }
    }

    private PullRequestTimelineFragment getPullRequestTimelineFragment() {
        if (pager == null || pager.getAdapter() == null) return null;
        return (PullRequestTimelineFragment) pager.getAdapter().instantiateItem(pager, 0);
    }

    @Override public void onTagUser(@NonNull String username) {
        commentEditorFragment.onAddUserName(username);
    }

    @Override public void onCreateComment(@NonNull String text, @Nullable Bundle bundle) {
        commentEditorFragment.onCreateComment(text, bundle);
    }

    @Override public void onSuccessfullyReviewed() {
        hideAndClearReviews();
        pager.setCurrentItem(0);
    }

    @SuppressWarnings("ConstantConditions") @Override public void onClearEditText() {
        if (commentEditorFragment != null && commentEditorFragment.commentText != null) commentEditorFragment.commentText.setText(null);
    }

    @Override public ArrayList<String> getNamesToTag() {
        PullRequestTimelineFragment fragment = getPullRequestTimelineFragment();
        if (fragment != null) {
            return fragment.getNamesToTag();
        }
        return new ArrayList<>();
    }

    protected void hideAndClearReviews() {
        getPresenter().getCommitComment().clear();
        AnimHelper.mimicFabVisibility(false, prReviewHolder, null);
        if (pager == null || pager.getAdapter() == null) return;
        PullRequestFilesFragment fragment = (PullRequestFilesFragment) pager.getAdapter().instantiateItem(pager, 2);
        if (fragment != null) {
            fragment.onRefresh();
        }

    }

    private void addPrReview(@NonNull View view) {
        PullRequest pullRequest = getPresenter().getPullRequest();
        if (pullRequest == null) return;
        User author = pullRequest.getUser() != null ? pullRequest.getUser() :
                      pullRequest.getHead() != null && pullRequest.getHead().getAuthor() != null ?
                      pullRequest.getHead().getAuthor() : pullRequest.getUser();
        if (author == null) return;
        ReviewRequestModel requestModel = new ReviewRequestModel();
        requestModel.setComments(getPresenter().getCommitComment().isEmpty() ? null : getPresenter().getCommitComment());
        requestModel.setCommitId(pullRequest.getHead().getSha());
        boolean isAuthor = Login.getUser().getLogin().equalsIgnoreCase(author.getLogin());
        ReviewChangesActivity.Companion.startForResult(requestModel, getPresenter().getRepoId(),
                getPresenter().getLogin(), pullRequest.getNumber(), isAuthor, isEnterprise(), pullRequest.isMerged()
                        || pullRequest.getState() == IssueState.closed)
                .show(getSupportFragmentManager(), ReviewChangesActivity.class.getSimpleName());
    }

    private void initTabs(@NonNull PullRequest pullRequest) {
        TabLayout.Tab tab1 = tabs.getTabAt(0);
        TabLayout.Tab tab2 = tabs.getTabAt(1);
        TabLayout.Tab tab3 = tabs.getTabAt(2);
        if (tab3 != null) {
            tab3.setText(SpannableBuilder.builder()
                    .append(getString(R.string.files))
                    .append(" ")
                    .append("(")
                    .append(String.valueOf(pullRequest.getChangedFiles()))
                    .append(")"));
        }
        if (tab2 != null) {
            tab2.setText(SpannableBuilder.builder()
                    .append(getString(R.string.commits))
                    .append(" ")
                    .append("(")
                    .append(String.valueOf(pullRequest.getCommits()))
                    .append(")"));
        }
        if (tab1 != null) {
            tab1.setText(SpannableBuilder.builder()
                    .append(getString(R.string.details))
                    .append(" ")
                    .append("(")
                    .append(String.valueOf(pullRequest.getComments()))
                    .append(")"));
        }
    }

    private void updateViews(@NonNull PullRequest pullRequest) {
        setTitle(String.format("#%s", pullRequest.getNumber()));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(pullRequest.getRepoId());
        }
        date.setText(SpannableBuilder.builder().append(getPresenter().getMergeBy(pullRequest, getApplicationContext())));
        size.setVisibility(View.GONE);
        User userModel = pullRequest.getUser();
        if (userModel != null) {
            title.setText(SpannableBuilder.builder().append(userModel.getLogin()).append("/").append(pullRequest.getTitle()));
            avatarLayout.setUrl(userModel.getAvatarUrl(), userModel.getLogin(), false,
                    LinkParserHelper.isEnterprise(pullRequest.getUrl()));
        } else {
            title.setText(SpannableBuilder.builder().append(pullRequest.getTitle()));
        }
        detailsIcon.setVisibility(View.VISIBLE);
    }

    private void hideShowFab() {
        if (getPresenter().isLocked() && !getPresenter().isOwner()) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .hide(commentEditorFragment).commit();
            return;
        }
        if (pager.getCurrentItem() == 0) {
            getSupportFragmentManager().beginTransaction().show(commentEditorFragment)
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction().hide(commentEditorFragment)
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .commit();
        }
    }
}
