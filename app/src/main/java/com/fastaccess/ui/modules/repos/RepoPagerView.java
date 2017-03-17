package com.fastaccess.ui.modules.repos;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.NameParser;
import com.fastaccess.data.dao.model.Repo;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.helper.TypeFaceHelper;
import com.fastaccess.provider.tasks.git.GithubActionService;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.modules.repos.code.RepoCodePagerView;
import com.fastaccess.ui.modules.repos.issues.RepoIssuesPagerView;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;

import java.text.NumberFormat;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import icepick.State;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

/**
 * Created by Kosh on 09 Dec 2016, 4:17 PM
 */

public class RepoPagerView extends BaseActivity<RepoPagerMvp.View, RepoPagerPresenter> implements RepoPagerMvp.View {

    @BindView(R.id.avatarLayout) AvatarLayout avatarLayout;
    @BindView(R.id.headerTitle) FontTextView title;
    @BindView(R.id.size) FontTextView size;
    @BindView(R.id.date) FontTextView date;
    @BindView(R.id.forkRepo) FontTextView forkRepo;
    @BindView(R.id.starRepo) FontTextView starRepo;
    @BindView(R.id.watchRepo) FontTextView watchRepo;
    @BindView(R.id.license) FontTextView license;
    @BindColor(R.color.accent) int accentColor;
    @BindColor(R.color.primary_text) int blackColor;
    @BindView(R.id.bottomNavigation) BottomNavigation bottomNavigation;
    @BindView(R.id.fab) FloatingActionButton fab;
    @State @RepoPagerMvp.RepoNavigationType int navType;
    @State String login;
    @State String repoId;
    private NumberFormat numberFormat = NumberFormat.getNumberInstance();
    private boolean userInteracted;

    public static void startRepoPager(@NonNull Context context, @NonNull NameParser nameParser) {
        if (!InputHelper.isEmpty(nameParser.getName()) && !InputHelper.isEmpty(nameParser.getUsername())) {
            context.startActivity(createIntent(context, nameParser.getName(), nameParser.getUsername()));
        }
    }

    public static Intent createIntent(@NonNull Context context, @NonNull String repoId, @NonNull String login) {
        return createIntent(context, repoId, login, RepoPagerMvp.CODE);
    }

    public static Intent createIntent(@NonNull Context context, @NonNull String repoId, @NonNull String login,
                                      @RepoPagerMvp.RepoNavigationType int navType) {
        Intent intent = new Intent(context, RepoPagerView.class);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA_TWO, login)
                .put(BundleConstant.EXTRA_TYPE, navType)
                .end());
        return intent;
    }

    @OnLongClick(R.id.date) boolean onShowDateHint() {
        showMessage(R.string.creation_date, R.string.creation_date_hint);
        return true;
    }

    @OnLongClick(R.id.size) boolean onShowLastUpdateDateHint() {
        showMessage(R.string.last_updated, R.string.last_updated_hint);
        return true;
    }

    @OnClick(R.id.fab) void onAddIssue() {
        if (navType == RepoPagerMvp.ISSUES) {
            RepoIssuesPagerView pagerView = (RepoIssuesPagerView) AppHelper.getFragmentByTag(getSupportFragmentManager(), RepoIssuesPagerView.TAG);
            if (pagerView != null) {
                pagerView.onAddIssue();
            }
        }
    }

    @OnClick(R.id.headerTitle) void onTitleClick() {
        Repo repoModel = getPresenter().getRepo();
        if (repoModel != null && !InputHelper.isEmpty(repoModel.getDescription())) {
            MessageDialogView.newInstance(getString(R.string.details), repoModel.getDescription())
                    .show(getSupportFragmentManager(), MessageDialogView.TAG);
        }
    }

    @SuppressWarnings("ConstantConditions") @OnClick({R.id.forkRepo, R.id.starRepo, R.id.watchRepo}) void onClick(View view) {
        switch (view.getId()) {
            case R.id.forkRepo:
                MessageDialogView.newInstance(getString(R.string.fork), getString(R.string.confirm_message),
                        Bundler.start().put(BundleConstant.EXTRA, true).end())
                        .show(getSupportFragmentManager(), MessageDialogView.TAG);
                break;
            case R.id.starRepo:
                if (getPresenter().login() != null && getPresenter().repoId() != null) {
                    GithubActionService.startForRepo(this, getPresenter().login(), getPresenter().repoId(),
                            getPresenter().isStarred() ? GithubActionService.UNSTAR_REPO : GithubActionService.STAR_REPO);
                    getPresenter().onStar();
                }
                break;
            case R.id.watchRepo:
                if (getPresenter().login() != null && getPresenter().repoId() != null) {
                    GithubActionService.startForRepo(this, getPresenter().login(), getPresenter().repoId(),
                            getPresenter().isWatched() ? GithubActionService.UNWATCH_REPO : GithubActionService.WATCH_REPO);
                    getPresenter().onWatch();
                }
                break;
        }
    }

    @Override protected int layout() {
        return R.layout.repo_pager_activity;
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

    @NonNull @Override public RepoPagerPresenter providePresenter() {
        if (getIntent() == null) {
            throw new IllegalArgumentException("intent is null, WTF");
        }
        if (getIntent().getExtras() == null) {
            throw new IllegalArgumentException("no intent extras provided");
        }
        final Bundle extras = getIntent().getExtras();
        repoId = extras.getString(BundleConstant.ID);
        login = extras.getString(BundleConstant.EXTRA_TWO);
        navType = extras.getInt(BundleConstant.EXTRA_TYPE);
        return new RepoPagerPresenter(repoId, login, navType);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new DummyFragment(), "DummyFragment")
                    .commit();
        }
        Typeface myTypeface = TypeFaceHelper.getTypeface();
        bottomNavigation.setDefaultTypeface(myTypeface);
        fab.setImageResource(R.drawable.ic_add);
        fab.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        showHideFab();
    }

    @Override public void onNavigationChanged(@RepoPagerMvp.RepoNavigationType int navType) {
        this.navType = navType;
        showHideFab();
        //noinspection WrongConstant
        if (bottomNavigation.getSelectedIndex() != navType) bottomNavigation.setSelectedIndex(navType, true);

        getPresenter().onModuleChanged(getSupportFragmentManager(), navType);
    }

    @Override public void onFinishActivity() {
        finish();
    }

    @Override public void onInitRepo() {
        if (getPresenter().getRepo() == null) {
            return;
        }
        bottomNavigation.setOnMenuItemClickListener(getPresenter());
        Repo repoModel = getPresenter().getRepo();
        hideProgress();
        forkRepo.setText(numberFormat.format(repoModel.getForksCount()));
        starRepo.setText(numberFormat.format(repoModel.getStargazersCount()));
        watchRepo.setText(numberFormat.format(repoModel.getSubsCount()));
        if (repoModel.getOwner() != null) {
            avatarLayout.setUrl(repoModel.getOwner().getAvatarUrl(), repoModel.getOwner().getLogin());
        } else if (repoModel.getOrganization() != null) {
            avatarLayout.setUrl(repoModel.getOrganization().getAvatarUrl(), repoModel.getOrganization().getLogin());
        }
        date.setText(ParseDateFormat.getTimeAgo(repoModel.getCreatedAt()));
        size.setText(ParseDateFormat.getTimeAgo(repoModel.getUpdatedAt()));
        title.setText(repoModel.getFullName());
        TextViewCompat.setTextAppearance(title, R.style.TextAppearance_AppCompat_Medium);
        title.setTextColor(ContextCompat.getColor(this, R.color.primary_text));
        license.setVisibility(repoModel.getLicense() != null ? View.VISIBLE : View.GONE);
        if (repoModel.getLicense() != null) license.setText(repoModel.getLicense().getSpdxId());
        supportInvalidateOptionsMenu();
        if (!PrefGetter.isRepoGuideShowed()) {// the mother of nesting. #dontjudgeme.
            new MaterialTapTargetPrompt.Builder(this)
                    .setTarget(watchRepo)
                    .setPrimaryText(R.string.watch)
                    .setSecondaryText(R.string.watch_hint)
                    .setCaptureTouchEventOutsidePrompt(true)
                    .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                        @Override public void onHidePrompt(MotionEvent event, boolean tappedTarget) {}

                        @Override public void onHidePromptComplete() {
                            new MaterialTapTargetPrompt.Builder(RepoPagerView.this)
                                    .setTarget(starRepo)
                                    .setPrimaryText(R.string.star)
                                    .setSecondaryText(R.string.star_hint)
                                    .setCaptureTouchEventOutsidePrompt(true)
                                    .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                                        @Override public void onHidePrompt(MotionEvent event, boolean tappedTarget) {}

                                        @Override public void onHidePromptComplete() {
                                            new MaterialTapTargetPrompt.Builder(RepoPagerView.this)
                                                    .setTarget(forkRepo)
                                                    .setPrimaryText(R.string.fork)
                                                    .setSecondaryText(R.string.fork_repo_hint)
                                                    .setCaptureTouchEventOutsidePrompt(true)
                                                    .show();
                                        }
                                    }).show();
                        }
                    }).show();
        }
    }

    @Override public void onRepoWatched(boolean isWatched) {
        watchRepo.setTopDrawable(R.drawable.ic_eye, isWatched ? accentColor : blackColor);
        onEnableDisableWatch(true);
    }

    @Override public void onRepoStarred(boolean isStarred) {
        starRepo.setTopDrawable(isStarred ? R.drawable.ic_star_filled : R.drawable.ic_star, isStarred ? accentColor : blackColor);
        onEnableDisableStar(true);
    }

    @Override public void onRepoForked(boolean isForked) {
        forkRepo.setTopDrawable(R.drawable.ic_fork, isForked ? accentColor : blackColor);
        onEnableDisableFork(true);
    }

    @Override public void onEnableDisableWatch(boolean isEnabled) {
        watchRepo.setEnabled(isEnabled);
    }

    @Override public void onEnableDisableStar(boolean isEnabled) {
        starRepo.setEnabled(isEnabled);
    }

    @Override public void onEnableDisableFork(boolean isEnabled) {
        forkRepo.setEnabled(isEnabled);
    }

    @Override public void onChangeWatchedCount(boolean isWatched) {
        long count = InputHelper.toLong(watchRepo);
        watchRepo.setText(numberFormat.format(isWatched ? (count + 1) : (count > 0 ? (count - 1) : 0)));
    }

    @Override public void onChangeStarCount(boolean isStarred) {
        long count = InputHelper.toLong(starRepo);
        starRepo.setText(numberFormat.format(isStarred ? (count + 1) : (count > 0 ? (count - 1) : 0)));
    }

    @Override public void onChangeForkCount(boolean isForked) {
        long count = InputHelper.toLong(forkRepo);
        forkRepo.setText(numberFormat.format(isForked ? (count + 1) : (count > 0 ? (count - 1) : 0)));
    }

    @Override public void onUserInteraction() {
        super.onUserInteraction();
        userInteracted = true;
    }

    @Override public boolean hasUserInteractedWithView() {
        return userInteracted;
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.repo_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onPrepareOptionsMenu(Menu menu) {
        Repo repoModel = getPresenter().getRepo();
        if (repoModel != null && repoModel.isFork() && repoModel.getParent() != null) {
            MenuItem menuItem = menu.findItem(R.id.originalRepo);
            menuItem.setVisible(true);
            menuItem.setTitle(repoModel.getParent().getFullName());
        }
//        menu.findItem(R.id.deleteRepo).setVisible(getPresenter().isRepoOwner());
        menu.findItem(R.id.deleteRepo).setVisible(false);//removing delete permission.
        return super.onPrepareOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.share) {
            if (getPresenter().getRepo() != null) ActivityHelper.shareUrl(this, getPresenter().getRepo().getHtmlUrl());
            return true;
        } else if (item.getItemId() == R.id.originalRepo) {
            if (getPresenter().getRepo() != null && getPresenter().getRepo().getParent() != null) {
                Repo parent = getPresenter().getRepo().getParent();
                RepoPagerView.startRepoPager(this, new NameParser(parent.getHtmlUrl()));
            }
            return true;
        } else if (item.getItemId() == R.id.deleteRepo) {
            MessageDialogView.newInstance(getString(R.string.delete_repo), getString(R.string.delete_repo_warning),
                    Bundler.start().put(BundleConstant.EXTRA_TWO, true).end()).show(getSupportFragmentManager(), MessageDialogView.TAG);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("ConstantConditions") @Override public void onMessageDialogActionClicked(boolean isOk, @Nullable Bundle bundle) {
        super.onMessageDialogActionClicked(isOk, bundle);
        if (isOk && bundle != null) {
            boolean isDelete = bundle.getBoolean(BundleConstant.EXTRA_TWO);
            boolean fork = bundle.getBoolean(BundleConstant.EXTRA);
            if (fork) {
                if (getPresenter().login() != null && getPresenter().repoId() != null && !getPresenter().isForked()) {
                    GithubActionService.startForRepo(this, getPresenter().login(), getPresenter().repoId(), GithubActionService.FORK_REPO);
                    getPresenter().onFork();
                }
            }
            if (isDelete) getPresenter().onDeleteRepo();
        }
    }

    @Override public void onBackPressed() {
        if (navType == RepoPagerMvp.CODE) {
            RepoCodePagerView codePagerView = (RepoCodePagerView) AppHelper.getFragmentByTag(getSupportFragmentManager(), RepoCodePagerView.TAG);
            if (codePagerView != null) {
                if (codePagerView.canPressBack()) {
                    super.onBackPressed();
                } else {
                    codePagerView.onBackPressed();
                    return;
                }
            }
        }
        super.onBackPressed();
    }

    private void showHideFab() {
        if (navType == RepoPagerMvp.ISSUES) {
            fab.show();
        } else {
            fab.hide();
        }
    }
}
