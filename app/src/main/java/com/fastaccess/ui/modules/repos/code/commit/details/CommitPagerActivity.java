package com.fastaccess.ui.modules.repos.code.commit.details;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.FragmentPagerAdapterModel;
import com.fastaccess.data.dao.NameParser;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.Commit;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.provider.scheme.LinkParserHelper;
import com.fastaccess.provider.scheme.SchemeParser;
import com.fastaccess.provider.timeline.HtmlHelper;
import com.fastaccess.ui.adapter.FragmentsPagerAdapter;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.editor.comment.CommentEditorFragment;
import com.fastaccess.ui.modules.repos.RepoPagerActivity;
import com.fastaccess.ui.modules.repos.code.commit.details.comments.CommitCommentsFragment;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.ViewPagerView;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import kotlin.text.StringsKt;

/**
 * Created by Kosh on 10 Dec 2016, 9:23 AM
 */

public class CommitPagerActivity extends BaseActivity<CommitPagerMvp.View, CommitPagerPresenter> implements CommitPagerMvp.View {

    @BindView(R.id.avatarLayout) AvatarLayout avatarLayout;
    @BindView(R.id.headerTitle) FontTextView title;
    @BindView(R.id.size) FontTextView size;
    @BindView(R.id.date) FontTextView date;
    @BindView(R.id.tabs) TabLayout tabs;
    @BindView(R.id.pager) ViewPagerView pager;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.changes) FontTextView changes;
    @BindView(R.id.addition) FontTextView addition;
    @BindView(R.id.deletion) FontTextView deletion;
    @BindView(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.detailsIcon) View detailsIcon;
    private CommentEditorFragment commentEditorFragment;

    public static Intent createIntent(@NonNull Context context, @NonNull String repoId, @NonNull String login, @NonNull String sha) {
        return createIntent(context, repoId, login, sha, false);
    }

    public static Intent createIntent(@NonNull Context context, @NonNull String repoId, @NonNull String login,
                                      @NonNull String sha, boolean showRepoBtn) {
        return createIntent(context, repoId, login, sha, showRepoBtn, false);
    }

    public static Intent createIntent(@NonNull Context context, @NonNull String repoId, @NonNull String login,
                                      @NonNull String sha, boolean showRepoBtn,
                                      boolean isEnterprise) {
        Intent intent = new Intent(context, CommitPagerActivity.class);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.ID, sha)
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.EXTRA_TWO, repoId)
                .put(BundleConstant.EXTRA_THREE, showRepoBtn)
                .put(BundleConstant.IS_ENTERPRISE, isEnterprise)
                .end());
        return intent;
    }

    public static void createIntentForOffline(@NonNull Context context, @NonNull Commit commitModel) {
        SchemeParser.launchUri(context, Uri.parse(commitModel.getHtmlUrl()));
    }

    @OnClick(R.id.detailsIcon) void onTitleClick() {
        if (getPresenter().getCommit() != null && !InputHelper.isEmpty(getPresenter().getCommit().getGitCommit().getMessage()))
            MessageDialogView.newInstance(String.format("%s/%s", getPresenter().getLogin(), getPresenter().getRepoId()),
                    getPresenter().getCommit().getGitCommit().getMessage(), true, false)
                    .show(getSupportFragmentManager(), MessageDialogView.TAG);
    }

    @Override protected int layout() {
        return R.layout.commit_pager_activity;
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

    @NonNull @Override public CommitPagerPresenter providePresenter() {
        return new CommitPagerPresenter();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fab.hide();
        commentEditorFragment = (CommentEditorFragment) getSupportFragmentManager().findFragmentById(R.id.commentFragment);
        setTitle("");
        if (savedInstanceState == null) {
            getPresenter().onActivityCreated(getIntent());
        } else {
            if (getPresenter().isApiCalled()) onSetup();
        }
        if (getPresenter().showToRepoBtn()) showNavToRepoItem();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        menu.findItem(R.id.browser).setVisible(true).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        menu.findItem(R.id.copyUrl).setVisible(true).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        menu.findItem(R.id.copySha).setVisible(true).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        menu.findItem(R.id.share).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onNavToRepoClicked();
            return true;
        } else if (item.getItemId() == R.id.share) {
            if (getPresenter().getCommit() != null) ActivityHelper.shareUrl(this, getPresenter().getCommit().getHtmlUrl());
            return true;
        } else if (item.getItemId() == R.id.browser) {
            if (getPresenter().getCommit() != null) ActivityHelper.startCustomTab(this, getPresenter().getCommit().getHtmlUrl());
            return true;
        } else if (item.getItemId() == R.id.copyUrl) {
            if (getPresenter().getCommit() != null) AppHelper.copyToClipboard(this, getPresenter().getCommit().getHtmlUrl());
            return true;
        } else if (item.getItemId() == R.id.copySha) {
            if (getPresenter().getCommit() != null) AppHelper.copyToClipboard(this, getPresenter().getCommit().getSha());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onSetup() {
        hideProgress();
        if (getPresenter().getCommit() == null) {
            return;
        }
        supportInvalidateOptionsMenu();
        Commit commit = getPresenter().getCommit();
        String login = commit.getAuthor() != null ? commit.getAuthor().getLogin() : commit.getGitCommit().getAuthor().getName();
        String avatar = commit.getAuthor() != null ? commit.getAuthor().getAvatarUrl() : null;
        Date dateValue = commit.getGitCommit().getAuthor().getDate();
        HtmlHelper.htmlIntoTextView(title, commit.getGitCommit().getMessage(), title.getWidth());
        setTaskName(commit.getLogin() + "/" + commit.getRepoId() + " - Commit " + StringsKt.take(commit.getSha(), 5));
        detailsIcon.setVisibility(View.VISIBLE);
        size.setVisibility(View.GONE);
        date.setText(SpannableBuilder.builder()
                .bold(getPresenter().repoId)
                .append(" ")
                .append(" ")
                .append(ParseDateFormat.getTimeAgo(dateValue)));
        avatarLayout.setUrl(avatar, login, false, LinkParserHelper.isEnterprise(commit.getHtmlUrl()));
        addition.setText(String.valueOf(commit.getStats() != null ? commit.getStats().getAdditions() : 0));
        deletion.setText(String.valueOf(commit.getStats() != null ? commit.getStats().getDeletions() : 0));
        changes.setText(String.valueOf(commit.getFiles() != null ? commit.getFiles().size() : 0));
        pager.setAdapter(new FragmentsPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapterModel.buildForCommit(this, commit)));
        tabs.setupWithViewPager(pager);
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override public void onPageSelected(int position) {
                super.onPageSelected(position);
                hideShowFab();
            }
        });
        hideShowFab();
        TabLayout.Tab tabOne = tabs.getTabAt(0);
        TabLayout.Tab tabTwo = tabs.getTabAt(1);
        if (tabOne != null && commit.getFiles() != null) {
            tabOne.setText(getString(R.string.files) + " (" + commit.getFiles().size() + ")");
        }
        if (tabTwo != null && commit.getGitCommit() != null && commit.getGitCommit().getCommentCount() > 0) {
            tabTwo.setText(getString(R.string.comments) + " (" + commit.getGitCommit().getCommentCount() + ")");
        }
        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager) {
            @Override public void onTabReselected(TabLayout.Tab tab) {
                super.onTabReselected(tab);
                onScrollTop(tab.getPosition());
            }
        });
    }

    @Override public void onScrollTop(int index) {
        if (pager == null || pager.getAdapter() == null) return;
        Fragment fragment = (BaseFragment) pager.getAdapter().instantiateItem(pager, index);
        if (fragment instanceof BaseFragment) {
            ((BaseFragment) fragment).onScrollTop(index);
        }
    }

    @Override public void onFinishActivity() {
        hideProgress();
        finish();
    }

    @Override public void onAddComment(@NonNull Comment newComment) {
        CommitCommentsFragment fragment = getCommitCommentsFragment();
        if (fragment != null) {
            fragment.addComment(newComment);
        }
    }

    @Override public String getLogin() {
        return getPresenter().getLogin();
    }

    @Override public String getRepoId() {
        return getPresenter().getRepoId();
    }

    @Override public void onBackPressed() {
        super.onBackPressed();
    }

    @Override protected void onNavToRepoClicked() {
        NameParser nameParser = new NameParser("");
        nameParser.setName(getPresenter().getRepoId());
        nameParser.setUsername(getPresenter().getLogin());
        nameParser.setEnterprise(isEnterprise());
        RepoPagerActivity.startRepoPager(this, nameParser);
        finish();
    }

    @Override public void onSendActionClicked(@NonNull String text, Bundle bundle) {
        CommitCommentsFragment fragment = getCommitCommentsFragment();
        if (fragment != null) {
            fragment.onHandleComment(text, bundle);
        }
    }

    @Override public void onTagUser(@NonNull String username) {
        commentEditorFragment.onAddUserName(username);
    }

    @Override public void onCreateComment(String text, Bundle bundle) {

    }

    @SuppressWarnings("ConstantConditions") @Override public void onClearEditText() {
        if (commentEditorFragment != null && commentEditorFragment.commentText != null) commentEditorFragment.commentText.setText(null);
    }

    @NonNull @Override public ArrayList<String> getNamesToTag() {
        CommitCommentsFragment fragment = getCommitCommentsFragment();
        if (fragment != null) {
            return fragment.getNamesToTags();
        }
        return new ArrayList<>();
    }

    private void hideShowFab() {
        if (pager.getCurrentItem() == 1) {
            getSupportFragmentManager().beginTransaction().show(commentEditorFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().hide(commentEditorFragment).commit();
        }
    }

    private CommitCommentsFragment getCommitCommentsFragment() {
        if (pager != null & pager.getAdapter() != null)
            return (CommitCommentsFragment) pager.getAdapter().instantiateItem(pager, 1);
        return null;
    }
}
