package com.fastaccess.ui.modules.gists.gist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.FragmentPagerAdapterModel;
import com.fastaccess.data.dao.GistsModel;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.ui.adapter.FragmentsPagerAdapter;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.modules.gists.gist.comments.GistCommentsView;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.ForegroundImageView;
import com.fastaccess.ui.widgets.ViewPagerView;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Kosh on 12 Nov 2016, 12:18 PM
 */

public class GistView extends BaseActivity<GistMvp.View, GistPresenter>
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

    public static Intent createIntent(@NonNull Context context, @NonNull String gistId) {
        Intent intent = new Intent(context, GistView.class);
        intent.putExtras(Bundler.start().put(BundleConstant.EXTRA, gistId).end());
        return intent;
    }

    @OnClick(R.id.fab) void onAddComment() {
        GistCommentsView view = (GistCommentsView) pager.getAdapter().instantiateItem(pager, 1);
        if (view != null) {
            view.onStartNewComment();
        }
    }

    @OnClick(R.id.headerTitle) void onTitleClick() {
        if (getPresenter().getGist() != null && !InputHelper.isEmpty(getPresenter().getGist().getDescription()))
            MessageDialogView.newInstance(getString(R.string.details), getPresenter().getGist().getDescription())
                    .show(getSupportFragmentManager(), MessageDialogView.TAG);
    }

    @OnClick({R.id.startGist, R.id.forkGist}) public void onGistActions(View view) {
        view.setEnabled(false);
        switch (view.getId()) {
            case R.id.startGist:
                getPresenter().onStarGist();
                break;
            case R.id.forkGist:
                getPresenter().onForkGist();
                break;
        }
    }

    @Override protected int layout() {
        return R.layout.gists_pager_layout;
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

    @NonNull @Override public GistPresenter providePresenter() {
        return new GistPresenter();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        menu.findItem(R.id.deleteGist).setVisible(getPresenter().isOwner());
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.share) {
            if (getPresenter().getGist() != null) ActivityHelper.shareUrl(this, getPresenter().getGist().getHtmlUrl());
            return true;
        } else if (item.getItemId() == R.id.deleteGist) {
            MessageDialogView.newInstance(
                    getString(R.string.delete_gist), getString(R.string.confirm_message),
                    Bundler.start().put(BundleConstant.EXTRA, true).end())
                    .show(getSupportFragmentManager(), MessageDialogView.TAG);
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    @Override public void onSuccessDeleted() {
        hideProgress();
        finish();
    }

    @Override public void onErrorDeleting() {
        showErrorMessage(getString(R.string.error_deleting_gist));
    }

    @Override public void onGistStarred(boolean isStarred) {
        startGist.tintDrawable(isStarred ? R.color.accent : R.color.black);
        startGist.setEnabled(true);
    }

    @Override public void onGistForked(boolean isForked) {
        forkGist.tintDrawable(isForked ? R.color.accent : R.color.black);
        forkGist.setEnabled(true);
    }

    @Override public void onSetupDetails() {
        hideProgress();
        GistsModel gistsModel = getPresenter().getGist();
        if (gistsModel == null) {
            finish();
            return;
        }
        String url = gistsModel.getOwner() != null ? gistsModel.getOwner().getAvatarUrl() :
                     gistsModel.getUser() != null ? gistsModel.getUser().getAvatarUrl() : "";
        String login = gistsModel.getOwner() != null ? gistsModel.getOwner().getLogin() :
                       gistsModel.getUser() != null ? gistsModel.getUser().getLogin() : "";
        avatarLayout.setUrl(url, login);
        title.setText(gistsModel.getDisplayTitle(false));
        date.setText(ParseDateFormat.getTimeAgo(gistsModel.getCreatedAt()));
        size.setText(Formatter.formatFileSize(this, gistsModel.getSize()));
        pager.setAdapter(new FragmentsPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapterModel.buildForGist(this, gistsModel)));
        tabs.setupWithViewPager(pager);
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override public void onPageSelected(int position) {
                super.onPageSelected(position);
                hideShowFab();
            }
        });
        onGistForked(getPresenter().isForked());
        onGistStarred(getPresenter().isStarred());
        hideShowFab();
    }

    private void hideShowFab() {
        if (pager.getCurrentItem() == 1) {
            fab.show();
        } else {
            fab.hide();
        }
    }
}
