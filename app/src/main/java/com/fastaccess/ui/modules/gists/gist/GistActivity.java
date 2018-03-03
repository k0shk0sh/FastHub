package com.fastaccess.ui.modules.gists.gist;

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
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.FragmentPagerAdapterModel;
import com.fastaccess.data.dao.model.Gist;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.data.dao.model.PinnedGists;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.provider.scheme.LinkParserHelper;
import com.fastaccess.provider.tasks.git.GithubActionService;
import com.fastaccess.ui.adapter.FragmentsPagerAdapter;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.editor.comment.CommentEditorFragment;
import com.fastaccess.ui.modules.gists.GistsListActivity;
import com.fastaccess.ui.modules.gists.create.CreateGistActivity;
import com.fastaccess.ui.modules.gists.gist.comments.GistCommentsFragment;
import com.fastaccess.ui.modules.main.premium.PremiumActivity;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.ForegroundImageView;
import com.fastaccess.ui.widgets.ViewPagerView;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Kosh on 12 Nov 2016, 12:18 PM
 */

public class GistActivity extends BaseActivity<GistMvp.View, GistPresenter>
        implements GistMvp.View {

    @BindView(R.id.avatarLayout) AvatarLayout avatarLayout;
    @BindView(R.id.headerTitle) FontTextView title;
    @BindView(R.id.size) FontTextView size;
    @BindView(R.id.date) FontTextView date;
    @BindView(R.id.pager) ViewPagerView pager;
    @BindView(R.id.tabs) TabLayout tabs;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.startGist) ForegroundImageView startGist;
    @BindView(R.id.forkGist) ForegroundImageView forkGist;
    @BindView(R.id.detailsIcon) View detailsIcon;
    @BindView(R.id.edit) View edit;
    @BindView(R.id.pinUnpin) ForegroundImageView pinUnpin;
    private int accentColor;
    private int iconColor;
    private CommentEditorFragment commentEditorFragment;

    public static Intent createIntent(@NonNull Context context, @NonNull String gistId, boolean isEnterprise) {
        Intent intent = new Intent(context, GistActivity.class);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.EXTRA, gistId)
                .put(BundleConstant.IS_ENTERPRISE, isEnterprise)
                .end());
        return intent;
    }

    @OnClick(R.id.detailsIcon) void onTitleClick() {
        if (getPresenter().getGist() != null && !InputHelper.isEmpty(getPresenter().getGist().getDescription()))
            MessageDialogView.newInstance(getString(R.string.details), getPresenter().getGist().getDescription(), false, true)
                    .show(getSupportFragmentManager(), MessageDialogView.TAG);
    }

    @OnClick({R.id.startGist, R.id.forkGist, R.id.browser}) public void onGistActions(View view) {
        if (getPresenter().getGist() == null) return;
        if (view.getId() != R.id.browser) {
            view.setEnabled(false);
        }
        switch (view.getId()) {
            case R.id.startGist:
                GithubActionService.startForGist(this, getPresenter().getGist().getGistId(),
                        getPresenter().isStarred() ? GithubActionService.UNSTAR_GIST : GithubActionService.STAR_GIST, isEnterprise());
                getPresenter().onStarGist();
                break;
            case R.id.forkGist:
                GithubActionService.startForGist(this, getPresenter().getGist().getGistId(),
                        GithubActionService.FORK_GIST, isEnterprise());
                getPresenter().onForkGist();
                break;
            case R.id.browser:
                ActivityHelper.startCustomTab(this, getPresenter().getGist().getHtmlUrl());
                break;
        }
    }

    @OnClick(R.id.edit) void onEdit() {
        if (PrefGetter.isProEnabled() || PrefGetter.isAllFeaturesUnlocked()) {
            if (getPresenter().getGist() != null) CreateGistActivity.start(this, getPresenter().getGist());
        } else {
            PremiumActivity.Companion.startActivity(this);
        }
    }

    @OnClick(R.id.pinUnpin) void pinUpin() {
        if (PrefGetter.isProEnabled()) {
            getPresenter().onPinUnpinGist();
        } else {
            PremiumActivity.Companion.startActivity(this);
        }
    }

    @Override protected int layout() {
        return R.layout.gists_pager_layout;
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

    @NonNull @Override public GistPresenter providePresenter() {
        return new GistPresenter();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fab.hide();
        commentEditorFragment = (CommentEditorFragment) getSupportFragmentManager().findFragmentById(R.id.commentFragment);
        accentColor = ViewHelper.getAccentColor(this);
        iconColor = ViewHelper.getIconColor(this);
        if (savedInstanceState == null) {
            getPresenter().onActivityCreated(getIntent());
        } else {
            if (getPresenter().getGist() != null) {
                onSetupDetails();
            }
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gist_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.share) {
            if (getPresenter().getGist() != null) ActivityHelper.shareUrl(this, getPresenter().getGist().getHtmlUrl());
            return true;
        } else if (item.getItemId() == R.id.deleteGist) {
            MessageDialogView.newInstance(
                    getString(R.string.delete_gist), getString(R.string.confirm_message),
                    Bundler.start()
                            .put(BundleConstant.YES_NO_EXTRA, true)
                            .put(BundleConstant.EXTRA, true).end())
                    .show(getSupportFragmentManager(), MessageDialogView.TAG);
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            GistsListActivity.startActivity(this);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.deleteGist).setVisible(getPresenter().isOwner());
        return super.onPrepareOptionsMenu(menu);
    }

    @Override public void onMessageDialogActionClicked(boolean isOk, @Nullable Bundle bundle) {
        super.onMessageDialogActionClicked(isOk, bundle);
        if (bundle != null) {
            boolean isDelete = bundle.getBoolean(BundleConstant.EXTRA) && isOk;
            if (isDelete) {
                getPresenter().onDeleteGist();
            }
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == BundleConstant.REQUEST_CODE) {
                getPresenter().callApi();
            }
        }
    }

    @Override public void onSuccessDeleted() {
        hideProgress();
        if (getPresenter().getGist() != null) {
            Intent intent = new Intent();
            Gist gistsModel = new Gist();
            gistsModel.setUrl(getPresenter().getGist().getHtmlUrl());
            intent.putExtras(Bundler.start().put(BundleConstant.ITEM, gistsModel).end());
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    @Override public void onErrorDeleting() {
        showErrorMessage(getString(R.string.error_deleting_gist));
    }

    @Override public void onGistStarred(boolean isStarred) {
        startGist.setImageResource(isStarred ? R.drawable.ic_star_filled : R.drawable.ic_star);
        startGist.tintDrawableColor(isStarred ? accentColor : iconColor);
        startGist.setEnabled(true);
    }

    @Override public void onGistForked(boolean isForked) {
        forkGist.tintDrawableColor(isForked ? accentColor : iconColor);
        forkGist.setEnabled(true);
    }

    @Override public void onSetupDetails() {
        hideProgress();
        Gist gistsModel = getPresenter().getGist();
        if (gistsModel == null) {
            return;
        }
        onUpdatePinIcon(gistsModel);
        String url = gistsModel.getOwner() != null ? gistsModel.getOwner().getAvatarUrl() :
                     gistsModel.getUser() != null ? gistsModel.getUser().getAvatarUrl() : "";
        String login = gistsModel.getOwner() != null ? gistsModel.getOwner().getLogin() :
                       gistsModel.getUser() != null ? gistsModel.getUser().getLogin() : "";
        avatarLayout.setUrl(url, login, false, LinkParserHelper.isEnterprise(gistsModel.getHtmlUrl()));
        title.setText(gistsModel.getDisplayTitle(false, true));
        setTaskName(gistsModel.getDisplayTitle(false, true).toString());
        edit.setVisibility(Login.getUser().getLogin().equals(login) ? View.VISIBLE : View.GONE);
        detailsIcon.setVisibility(InputHelper.isEmpty(gistsModel.getDescription()) || !ViewHelper.isEllipsed(title) ? View.GONE : View.VISIBLE);
        if (gistsModel.getCreatedAt().before(gistsModel.getUpdatedAt())) {
            date.setText(String.format("%s %s", ParseDateFormat.getTimeAgo(gistsModel.getCreatedAt()), getString(R.string.edited)));
        } else {
            date.setText(ParseDateFormat.getTimeAgo(gistsModel.getCreatedAt()));
        }
        size.setText(Formatter.formatFileSize(this, gistsModel.getSize()));
        pager.setAdapter(new FragmentsPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapterModel.buildForGist(this, gistsModel)));
        tabs.setupWithViewPager(pager);
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override public void onPageSelected(int position) {
                super.onPageSelected(position);
                hideShowFab();
            }
        });
        supportInvalidateOptionsMenu();
        onGistForked(getPresenter().isForked());
        onGistStarred(getPresenter().isStarred());
        hideShowFab();
        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager) {
            @Override public void onTabReselected(TabLayout.Tab tab) {
                super.onTabReselected(tab);
                onScrollTop(tab.getPosition());
            }
        });
    }

    @Override public void onUpdatePinIcon(@NonNull Gist gist) {
        pinUnpin.setImageDrawable(PinnedGists.isPinned(gist.getGistId().hashCode())
                                  ? ContextCompat.getDrawable(this, R.drawable.ic_pin_filled)
                                  : ContextCompat.getDrawable(this, R.drawable.ic_pin));
    }

    @Override public void onScrollTop(int index) {
        if (pager == null || pager.getAdapter() == null) return;
        Fragment fragment = (BaseFragment) pager.getAdapter().instantiateItem(pager, index);
        if (fragment instanceof BaseFragment) {
            ((BaseFragment) fragment).onScrollTop(index);
        }
    }

    @Override public void onSendActionClicked(@NonNull String text, Bundle bundle) {
        GistCommentsFragment view = getGistCommentsFragment();
        if (view != null) {
            view.onHandleComment(text, bundle);
        }
    }

    @Override public void onTagUser(@NonNull String username) {
        commentEditorFragment.onAddUserName(username);
    }

    @Override public void onCreateComment(String text, Bundle bundle) {

    }

    @SuppressWarnings("ConstantConditions") @Override public void onClearEditText() {
        if (commentEditorFragment != null && commentEditorFragment.commentText != null) commentEditorFragment.commentText.setText("");
    }

    @NonNull @Override public ArrayList<String> getNamesToTag() {
        GistCommentsFragment view = getGistCommentsFragment();
        if (view != null) return view.getNamesToTag();
        return new ArrayList<>();
    }

    @Nullable private GistCommentsFragment getGistCommentsFragment() {
        if (pager == null || pager.getAdapter() == null) return null;
        return (GistCommentsFragment) pager.getAdapter().instantiateItem(pager, 1);
    }

    private void hideShowFab() {
        if (pager.getCurrentItem() == 1) {
            getSupportFragmentManager().beginTransaction().show(commentEditorFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().hide(commentEditorFragment).commit();
        }
    }
}
