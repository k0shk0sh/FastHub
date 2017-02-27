package com.fastaccess.ui.modules.repos;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.NameParser;
import com.fastaccess.data.dao.RepoModel;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.helper.TypeFaceHelper;
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
import hugo.weaving.DebugLog;
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
    @BindView(R.id.bottomNavigation) BottomNavigation bottomNavigation;
    @BindView(R.id.fab) FloatingActionButton fab;
    @State @RepoPagerMvp.RepoNavigationType int navType;
    private NumberFormat numberFormat = NumberFormat.getNumberInstance();

    @DebugLog public static void startRepoPager(@NonNull Context context, @NonNull NameParser nameParser) {
        if (!InputHelper.isEmpty(nameParser.getName()) && !InputHelper.isEmpty(nameParser.getUsername())) {
            context.startActivity(createIntent(context, nameParser.getName(), nameParser.getUsername()));
        }
    }

    @DebugLog public static Intent createIntent(@NonNull Context context, @NonNull String repoId, @NonNull String login) {
        Intent intent = new Intent(context, RepoPagerView.class);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA_TWO, login)
                .end());
        return intent;
    }

    @OnClick(R.id.fab) public void onAddIssue() {
        if (navType == RepoPagerMvp.ISSUES) {
            RepoIssuesPagerView pagerView = (RepoIssuesPagerView) AppHelper.getFragmentByTag(getSupportFragmentManager(), RepoIssuesPagerView.TAG);
            if (pagerView != null) {
                pagerView.onAddIssue();
            }
        }
    }

    @OnClick(R.id.headerTitle) void onTitleClick() {
        RepoModel repoModel = getPresenter().getRepo();
        if (repoModel != null && !InputHelper.isEmpty(repoModel.getDescription())) {
            MessageDialogView.newInstance(getString(R.string.details), repoModel.getDescription())
                    .show(getSupportFragmentManager(), MessageDialogView.TAG);
        }
    }

    @OnClick({R.id.forkRepo, R.id.starRepo, R.id.watchRepo}) public void onClick(View view) {
        switch (view.getId()) {
            case R.id.forkRepo:
                getPresenter().onFork();
                break;
            case R.id.starRepo:
                getPresenter().onStar();
                break;
            case R.id.watchRepo:
                getPresenter().onWatch();
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
        return new RepoPagerPresenter();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        Typeface myTypeface = TypeFaceHelper.getTypeface();
        bottomNavigation.setDefaultTypeface(myTypeface);
        if (savedInstanceState == null) {
            getPresenter().onActivityCreated(getIntent());
            bottomNavigation.setDefaultSelectedIndex(0);
        } else {
            if (getPresenter().getRepo() != null) {
                onInitRepo();
            }
        }
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
            finish();
            return;
        }
        bottomNavigation.setOnMenuItemClickListener(getPresenter());
        RepoModel repoModel = getPresenter().getRepo();
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
                                                    .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                                                        @Override public void onHidePrompt(MotionEvent event, boolean tappedTarget) {
                                                            new MaterialTapTargetPrompt.Builder(RepoPagerView.this)
                                                                    .setTarget(date)
                                                                    .setPrimaryText(R.string.creation_date)
                                                                    .setSecondaryText(R.string.creation_date_hint)
                                                                    .setCaptureTouchEventOutsidePrompt(true)
                                                                    .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                                                                        @Override public void onHidePrompt(MotionEvent event, boolean tappedTarget) {}

                                                                        @Override public void onHidePromptComplete() {
                                                                            new MaterialTapTargetPrompt.Builder(RepoPagerView.this)
                                                                                    .setTarget(size)
                                                                                    .setPrimaryText(R.string.last_updated)
                                                                                    .setSecondaryText(R.string.last_updated_hint)
                                                                                    .setCaptureTouchEventOutsidePrompt(true)
                                                                                    .show();
                                                                        }
                                                                    });
                                                        }

                                                        @Override public void onHidePromptComplete() {

                                                        }
                                                    })
                                                    .show();
                                        }
                                    })
                                    .show();
                        }
                    })
                    .show();
        }
    }

    @Override public void onRepoWatched(boolean isWatched) {
        watchRepo.tintDrawables(isWatched ? accentColor : Color.BLACK);
        onEnableDisableWatch(true);
    }

    @Override public void onRepoStarred(boolean isStarred) {
        starRepo.tintDrawables(isStarred ? accentColor : Color.BLACK);
        onEnableDisableStar(true);
    }

    @Override public void onRepoForked(boolean isForked) {
        forkRepo.tintDrawables(isForked ? accentColor : Color.BLACK);
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

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.repo_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onPrepareOptionsMenu(Menu menu) {
        RepoModel repoModel = getPresenter().getRepo();
        if (repoModel != null && repoModel.isFork() && repoModel.getParent() != null) {
            MenuItem menuItem = menu.findItem(R.id.originalRepo);
            menuItem.setVisible(true);
            menuItem.setTitle(repoModel.getParent().getFullName());
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.share) {
            if (getPresenter().getRepo() != null) ActivityHelper.shareUrl(this, getPresenter().getRepo().getHtmlUrl());
            return true;
        } else if (item.getItemId() == R.id.originalRepo) {
            if (getPresenter().getRepo() != null && getPresenter().getRepo().getParent() != null) {
                RepoModel parent = getPresenter().getRepo().getParent();
                RepoPagerView.startRepoPager(this, new NameParser(parent.getHtmlUrl()));
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
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
