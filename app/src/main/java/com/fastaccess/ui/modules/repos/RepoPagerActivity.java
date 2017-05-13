package com.fastaccess.ui.modules.repos;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;

import com.fastaccess.R;
import com.fastaccess.data.dao.LicenseModel;
import com.fastaccess.data.dao.NameParser;
import com.fastaccess.data.dao.model.AbstractPinnedRepos;
import com.fastaccess.data.dao.model.Repo;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.AnimHelper;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.helper.TypeFaceHelper;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.provider.tasks.git.GithubActionService;
import com.fastaccess.ui.adapter.TopicsAdapter;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.modules.filter.issues.FilterIssuesActivity;
import com.fastaccess.ui.modules.main.MainActivity;
import com.fastaccess.ui.modules.repos.code.RepoCodePagerFragment;
import com.fastaccess.ui.modules.repos.extras.misc.RepoMiscDialogFragment;
import com.fastaccess.ui.modules.repos.extras.misc.RepoMiscMVp;
import com.fastaccess.ui.modules.repos.issues.RepoIssuesPagerFragment;
import com.fastaccess.ui.modules.repos.pull_requests.RepoPullRequestPagerFragment;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.ForegroundImageView;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.color.ColorGenerator;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;

import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnLongClick;
import icepick.State;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

/**
 * Created by Kosh on 09 Dec 2016, 4:17 PM
 */

public class RepoPagerActivity extends BaseActivity<RepoPagerMvp.View, RepoPagerPresenter> implements RepoPagerMvp.View {

    @BindView(R.id.avatarLayout) AvatarLayout avatarLayout;
    @BindView(R.id.headerTitle) FontTextView title;
    @BindView(R.id.size) FontTextView size;
    @BindView(R.id.date) FontTextView date;
    @BindView(R.id.forkRepo) FontTextView forkRepo;
    @BindView(R.id.starRepo) FontTextView starRepo;
    @BindView(R.id.watchRepo) FontTextView watchRepo;
    @BindView(R.id.license) FontTextView license;
    @BindView(R.id.bottomNavigation) BottomNavigation bottomNavigation;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.language) FontTextView language;
    @BindView(R.id.detailsIcon) View detailsIcon;
    @BindView(R.id.tagsIcon) View tagsIcon;
    @BindView(R.id.watchRepoImage) ForegroundImageView watchRepoImage;
    @BindView(R.id.starRepoImage) ForegroundImageView starRepoImage;
    @BindView(R.id.forkRepoImage) ForegroundImageView forkRepoImage;
    @BindView(R.id.licenseLayout) View licenseLayout;
    @BindView(R.id.watchRepoLayout) View watchRepoLayout;
    @BindView(R.id.starRepoLayout) View starRepoLayout;
    @BindView(R.id.forkRepoLayout) View forkRepoLayout;
    @BindView(R.id.pinImage) ForegroundImageView pinImage;
    @BindView(R.id.pinLayout) View pinLayout;
    @BindView(R.id.pinText) FontTextView pinText;
    @BindView(R.id.filterLayout) View filterLayout;
    @BindView(R.id.topicsList) RecyclerView topicsList;
    @BindView(R.id.sortByUpdated) CheckBox sortByUpdated;
    @State @RepoPagerMvp.RepoNavigationType int navType;
    @State String login;
    @State String repoId;

    private NumberFormat numberFormat = NumberFormat.getNumberInstance();
    private boolean userInteracted;
    private int accentColor;
    private int iconColor;

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
        Intent intent = new Intent(context, RepoPagerActivity.class);
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

    @OnLongClick(R.id.fab) boolean onFabLongClick() {
        if (navType == RepoPagerMvp.ISSUES) {
            onAddSelected();
            return true;
        }
        return false;
    }

    @OnClick(R.id.fab) void onFabClicked() {
        if (navType == RepoPagerMvp.ISSUES) {
            fab.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                @Override public void onHidden(FloatingActionButton fab) {
                    super.onHidden(fab);
                    if (appbar != null) appbar.setExpanded(false, true);
                    bottomNavigation.setExpanded(false, true);
                    AnimHelper.mimicFabVisibility(true, filterLayout, null);
                }
            });
        } else if (navType == RepoPagerMvp.PULL_REQUEST) {
            RepoPullRequestPagerFragment pullRequestPagerView = (RepoPullRequestPagerFragment) AppHelper.getFragmentByTag(getSupportFragmentManager(),
                    RepoPullRequestPagerFragment.TAG);
            if (pullRequestPagerView != null) {
                FilterIssuesActivity.startActivity(this, getPresenter().login(), getPresenter().repoId(), false,
                        pullRequestPagerView.getCurrentItem() == 0);
            }
        } else {
            fab.hide();
        }
    }

    @OnClick(R.id.add) void onAddIssues() {
        hideFilterLayout();
        onAddSelected();
    }

    @OnClick(R.id.search) void onSearch() {
        hideFilterLayout();
        onSearchSelected();
    }

    @Override public boolean dispatchTouchEvent(MotionEvent ev) {
        if (navType == RepoPagerMvp.ISSUES && filterLayout.isShown()) {
            Rect viewRect = ViewHelper.getLayoutPosition(filterLayout);
            if (!viewRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                hideFilterLayout();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @OnClick(R.id.detailsIcon) void onTitleClick() {
        Repo repoModel = getPresenter().getRepo();
        if (repoModel != null && !InputHelper.isEmpty(repoModel.getDescription())) {
            MessageDialogView.newInstance(getString(R.string.details), repoModel.getDescription())
                    .show(getSupportFragmentManager(), MessageDialogView.TAG);
        }
    }

    @OnClick(R.id.tagsIcon) void onTagsClick() {
        if(topicsList.getAdapter().getItemCount()>0)
            if(topicsList.getVisibility()==View.VISIBLE)
                AnimHelper.collapse(topicsList);
            else
                AnimHelper.expand(topicsList);
    }

    @OnClick({R.id.forkRepoLayout, R.id.starRepoLayout, R.id.watchRepoLayout, R.id.pinLayout})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.forkRepoLayout:
                MessageDialogView.newInstance(getString(R.string.fork), getString(R.string.confirm_message),
                        Bundler.start().put(BundleConstant.EXTRA, true)
                                .put(BundleConstant.YES_NO_EXTRA, true).end())
                        .show(getSupportFragmentManager(), MessageDialogView.TAG);
                break;
            case R.id.starRepoLayout:
                if (!InputHelper.isEmpty(getPresenter().login()) && !InputHelper.isEmpty(getPresenter().repoId())) {
                    GithubActionService.startForRepo(this, getPresenter().login(), getPresenter().repoId(),
                            getPresenter().isStarred() ? GithubActionService.UNSTAR_REPO : GithubActionService.STAR_REPO);
                    getPresenter().onStar();
                }
                break;
            case R.id.watchRepoLayout:
                if (!InputHelper.isEmpty(getPresenter().login()) && !InputHelper.isEmpty(getPresenter().repoId())) {
                    GithubActionService.startForRepo(this, getPresenter().login(), getPresenter().repoId(),
                            getPresenter().isWatched() ? GithubActionService.UNWATCH_REPO : GithubActionService.WATCH_REPO);
                    getPresenter().onWatch();
                }
                break;
            case R.id.pinLayout:
                pinLayout.setEnabled(false);
                getPresenter().onPinUnpinRepo();
                break;
        }
    }

    @OnLongClick({R.id.forkRepoLayout, R.id.starRepoLayout, R.id.watchRepoLayout, R.id.pinLayout})
    boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.forkRepoLayout:
                RepoMiscDialogFragment.show(getSupportFragmentManager(), getPresenter().login(), getPresenter().repoId(), RepoMiscMVp.FORKS);
                return true;
            case R.id.starRepoLayout:
                RepoMiscDialogFragment.show(getSupportFragmentManager(), getPresenter().login(), getPresenter().repoId(), RepoMiscMVp.STARS);
                return true;
            case R.id.watchRepoLayout:
                RepoMiscDialogFragment.show(getSupportFragmentManager(), getPresenter().login(), getPresenter().repoId(), RepoMiscMVp.WATCHERS);
                return true;
        }
        return false;
    }

    @OnCheckedChanged(R.id.sortByUpdated) void onSortIssues(boolean isChecked) {
        RepoIssuesPagerFragment pagerView = (RepoIssuesPagerFragment) AppHelper.getFragmentByTag(getSupportFragmentManager(),
                RepoIssuesPagerFragment.TAG);
        if (pagerView != null) {
            pagerView.onChangeIssueSort(isChecked);
        }
        hideFilterLayout();
    }

    @Override protected int layout() {
        return R.layout.repo_pager_activity;
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

    @NonNull @Override public RepoPagerPresenter providePresenter() {
        return new RepoPagerPresenter();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            if (getIntent() == null || getIntent().getExtras() == null) {
                finish();
                return;
            }
            final Bundle extras = getIntent().getExtras();
            repoId = extras.getString(BundleConstant.ID);
            login = extras.getString(BundleConstant.EXTRA_TWO);
            navType = extras.getInt(BundleConstant.EXTRA_TYPE);
        }
        getPresenter().onActivityCreate(repoId, login, navType);
        setTitle("");
        accentColor = ViewHelper.getAccentColor(this);
        iconColor = ViewHelper.getIconColor(this);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new DummyFragment(), "DummyFragment")
                    .commit();
        }
        Typeface myTypeface = TypeFaceHelper.getTypeface();
        bottomNavigation.setDefaultTypeface(myTypeface);
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
        hideProgress();
        if (getPresenter().getRepo() == null) {
            return;
        }
        bottomNavigation.setOnMenuItemClickListener(getPresenter());
        Repo repoModel = getPresenter().getRepo();
        if (repoModel.getTopics() != null && !repoModel.getTopics().isEmpty()) {
            tagsIcon.setVisibility(View.VISIBLE);
            topicsList.setAdapter(new TopicsAdapter(repoModel.getTopics()));
        } else {
            topicsList.setVisibility(View.GONE);
        }
        onRepoPinned(AbstractPinnedRepos.isPinned(repoModel.getFullName()));
        pinText.setText(R.string.pin);
        detailsIcon.setVisibility(InputHelper.isEmpty(repoModel.getDescription()) ? View.GONE : View.VISIBLE);
        language.setVisibility(InputHelper.isEmpty(repoModel.getLanguage()) ? View.GONE : View.VISIBLE);
        if (!InputHelper.isEmpty(repoModel.getLanguage())) language.setText(repoModel.getLanguage());
        language.setTextColor(ColorGenerator.getColor(this, repoModel.getLanguage()));
        forkRepo.setText(numberFormat.format(repoModel.getForksCount()));
        starRepo.setText(numberFormat.format(repoModel.getStargazersCount()));
        watchRepo.setText(numberFormat.format(repoModel.getSubsCount()));
        if (repoModel.getOwner() != null) {
            avatarLayout.setUrl(repoModel.getOwner().getAvatarUrl(), repoModel.getOwner().getLogin(), repoModel.getOwner().isOrganizationType());
        } else if (repoModel.getOrganization() != null) {
            avatarLayout.setUrl(repoModel.getOrganization().getAvatarUrl(), repoModel.getOrganization().getLogin(), true);
        }
        long repoSize = repoModel.getSize() > 0 ? (repoModel.getSize() * 1000) : repoModel.getSize();
        date.setText(SpannableBuilder.builder()
                .append(ParseDateFormat.getTimeAgo(repoModel.getUpdatedAt()))
                .append(" ,")
                .append(" ")
                .append(Formatter.formatFileSize(this, repoSize)));
        size.setVisibility(View.GONE);
        title.setText(repoModel.getFullName());
        TextViewCompat.setTextAppearance(title, R.style.TextAppearance_AppCompat_Medium);
        title.setTextColor(ViewHelper.getPrimaryTextColor(this));
        if (repoModel.getLicense() != null) {
            licenseLayout.setVisibility(View.VISIBLE);
            LicenseModel licenseModel = repoModel.getLicense();
            license.setText(!InputHelper.isEmpty(licenseModel.getSpdxId()) ? licenseModel.getSpdxId() : licenseModel.getName());
        }
        supportInvalidateOptionsMenu();
        if (!PrefGetter.isRepoGuideShowed()) {// the mother of nesting. #dontjudgeme.
            final boolean[] dismissed = {false};
            new MaterialTapTargetPrompt.Builder(this)
                    .setTarget(watchRepoLayout)
                    .setPrimaryText(R.string.watch)
                    .setSecondaryText(R.string.watch_hint)
                    .setCaptureTouchEventOutsidePrompt(true)
                    .setBackgroundColourAlpha(244)
                    .setBackgroundColour(ViewHelper.getAccentColor(RepoPagerActivity.this))
                    .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                        @Override public void onHidePrompt(MotionEvent event, boolean tappedTarget) {}

                        @Override public void onHidePromptComplete() {
                            if(!dismissed[0])
                            new MaterialTapTargetPrompt.Builder(RepoPagerActivity.this)
                                    .setTarget(starRepoLayout)
                                    .setPrimaryText(R.string.star)
                                    .setSecondaryText(R.string.star_hint)
                                    .setCaptureTouchEventOutsidePrompt(true)
                                    .setBackgroundColourAlpha(244)
                                    .setBackgroundColour(ViewHelper.getAccentColor(RepoPagerActivity.this))
                                    .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                                        @Override public void onHidePrompt(MotionEvent event, boolean tappedTarget) {}

                                        @Override public void onHidePromptComplete() {
                                            if(!dismissed[0])
                                            new MaterialTapTargetPrompt.Builder(RepoPagerActivity.this)
                                                    .setTarget(forkRepoLayout)
                                                    .setPrimaryText(R.string.fork)
                                                    .setSecondaryText(R.string.fork_repo_hint)
                                                    .setCaptureTouchEventOutsidePrompt(true)
                                                    .setBackgroundColourAlpha(244)
                                                    .setBackgroundColour(ViewHelper.getAccentColor(RepoPagerActivity.this))
                                                    .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                                                        @Override public void onHidePrompt(MotionEvent event, boolean tappedTarget) {
                                                            if(!dismissed[0])
                                                            new MaterialTapTargetPrompt.Builder(RepoPagerActivity.this)
                                                                    .setTarget(pinLayout)
                                                                    .setPrimaryText(R.string.pin)
                                                                    .setSecondaryText(R.string.pin_repo_hint)
                                                                    .setCaptureTouchEventOutsidePrompt(true)
                                                                    .setBackgroundColourAlpha(244)
                                                                    .setBackgroundColour(ViewHelper.getAccentColor(RepoPagerActivity.this))
                                                                    .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                                                                        @Override
                                                                        public void onHidePrompt(MotionEvent motionEvent, boolean b) {
                                                                            ActivityHelper.hideDismissHints(RepoPagerActivity.this);
                                                                        }

                                                                        @Override
                                                                        public void onHidePromptComplete() {

                                                                        }
                                                                    })
                                                                    .show();
                                                            ActivityHelper.bringDismissAllToFront(RepoPagerActivity.this);
                                                        }

                                                        @Override public void onHidePromptComplete() {

                                                        }
                                                    })
                                                    .show();
                                            ActivityHelper.bringDismissAllToFront(RepoPagerActivity.this);
                                        }
                                    }).show();
                            ActivityHelper.bringDismissAllToFront(RepoPagerActivity.this);
                        }
                    }).show();
            ActivityHelper.showDismissHints(this, () -> { dismissed[0] = true; });
        }
        onRepoWatched(getPresenter().isWatched());
        onRepoStarred(getPresenter().isStarred());
        onRepoForked(getPresenter().isForked());
    }

    @Override public void onRepoWatched(boolean isWatched) {
        watchRepoImage.tintDrawableColor(isWatched ? accentColor : iconColor);
        onEnableDisableWatch(true);
    }

    @Override public void onRepoStarred(boolean isStarred) {
        starRepoImage.setImageResource(isStarred ? R.drawable.ic_star_filled : R.drawable.ic_star);
        starRepoImage.tintDrawableColor(isStarred ? accentColor : iconColor);
        onEnableDisableStar(true);
    }

    @Override public void onRepoForked(boolean isForked) {
        forkRepoImage.tintDrawableColor(isForked ? accentColor : iconColor);
        onEnableDisableFork(true);
    }

    @Override public void onRepoPinned(boolean isPinned) {
        pinImage.setImageResource(isPinned ? R.drawable.ic_pin_filled : R.drawable.ic_pin);
        pinLayout.setEnabled(true);
    }

    @Override public void onEnableDisableWatch(boolean isEnabled) {
        watchRepoLayout.setEnabled(isEnabled);
    }

    @Override public void onEnableDisableStar(boolean isEnabled) {
        starRepoLayout.setEnabled(isEnabled);
    }

    @Override public void onEnableDisableFork(boolean isEnabled) {
        forkRepoLayout.setEnabled(isEnabled);
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
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else if (item.getItemId() == R.id.share) {
            if (getPresenter().getRepo() != null) ActivityHelper.shareUrl(this, getPresenter().getRepo().getHtmlUrl());
            return true;
        } else if (item.getItemId() == R.id.originalRepo) {
            if (getPresenter().getRepo() != null && getPresenter().getRepo().getParent() != null) {
                Repo parent = getPresenter().getRepo().getParent();
                RepoPagerActivity.startRepoPager(this, new NameParser(parent.getHtmlUrl()));
            }
            return true;
        } else if (item.getItemId() == R.id.deleteRepo) {
            MessageDialogView.newInstance(getString(R.string.delete_repo), getString(R.string.delete_repo_warning),
                    Bundler.start().put(BundleConstant.EXTRA_TWO, true)
                            .put(BundleConstant.YES_NO_EXTRA, true)
                            .end()).show(getSupportFragmentManager(), MessageDialogView.TAG);
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
            RepoCodePagerFragment codePagerView = (RepoCodePagerFragment) AppHelper.getFragmentByTag(getSupportFragmentManager(),
                    RepoCodePagerFragment.TAG);
            if (codePagerView != null) {
                if (codePagerView.canPressBack()) {
                    super.onBackPressed();
                } else {
                    codePagerView.onBackPressed();
                    return;
                }
            }
        } else if (navType == RepoPagerMvp.ISSUES) {
            if (filterLayout.isShown()) {
                hideFilterLayout();
                return;
            }
        }
        super.onBackPressed();
    }

    @Override public void onAddSelected() {
        RepoIssuesPagerFragment pagerView = (RepoIssuesPagerFragment) AppHelper.getFragmentByTag(getSupportFragmentManager(),
                RepoIssuesPagerFragment.TAG);
        if (pagerView != null) {
            pagerView.onAddIssue();
        }
    }

    @Override public void onSearchSelected() {
        boolean isOpen = true;
        RepoIssuesPagerFragment pagerView = (RepoIssuesPagerFragment) AppHelper.getFragmentByTag(getSupportFragmentManager(),
                RepoIssuesPagerFragment.TAG);
        if (pagerView != null) {
            isOpen = pagerView.getCurrentItem() == 0;
        }
        FilterIssuesActivity.startActivity(this, getPresenter().login(), getPresenter().repoId(), true, isOpen);
    }

    private void showHideFab() {
        if (navType == RepoPagerMvp.ISSUES) {
            fab.setImageResource(R.drawable.ic_menu);
            fab.show();
            if (!PrefGetter.isRepoFabHintShowed()) {
                new MaterialTapTargetPrompt.Builder(this)
                        .setTarget(fab)
                        .setPrimaryText(R.string.create_issue)
                        .setSecondaryText(R.string.long_press_repo_fab_hint)
                        .setCaptureTouchEventOutsidePrompt(true)
                        .setBackgroundColourAlpha(244)
                        .setBackgroundColour(ViewHelper.getAccentColor(RepoPagerActivity.this))
                        .show();
            }
        } else if (navType == RepoPagerMvp.PULL_REQUEST) {
            fab.setImageResource(R.drawable.ic_search);
            fab.show();
        } else {
            fab.hide();
        }
    }

    private void hideFilterLayout() {
        AnimHelper.mimicFabVisibility(false, filterLayout, new FloatingActionButton.OnVisibilityChangedListener() {
            @Override public void onHidden(FloatingActionButton actionButton) {
                fab.show();
            }
        });
    }
}
