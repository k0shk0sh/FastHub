package com.fastaccess.ui.modules.repos.code.commit.viewer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;

import com.fastaccess.R;
import com.fastaccess.data.dao.CommitFileModel;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.code.CodeViewerActivity;
import com.fastaccess.ui.widgets.DiffLineSpan;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.SpannableBuilder;

import net.grandcentrix.thirtyinch.TiPresenter;

import butterknife.BindString;
import butterknife.BindView;
import com.evernote.android.state.State;

/**
 * Created by Kosh on 24 Apr 2017, 2:53 PM
 */

public class FullCommitFileActivity extends BaseActivity {
    @BindView(R.id.textView) FontTextView textView;


    @State CommitFileModel commitFileModel;
    @BindView(R.id.changes) FontTextView changes;
    @BindView(R.id.addition) FontTextView addition;
    @BindView(R.id.deletion) FontTextView deletion;
    @BindView(R.id.status) FontTextView status;
    @BindString(R.string.changes) String changesText;
    @BindString(R.string.addition) String additionText;
    @BindString(R.string.deletion) String deletionText;
    @BindString(R.string.status) String statusText;

    public static void start(@NonNull Context context, @NonNull CommitFileModel fileModel) {
        Intent starter = new Intent(context, FullCommitFileActivity.class);
        starter.putExtras(Bundler.start()
                .put(BundleConstant.ITEM, fileModel)
                .end());
        context.startActivity(starter);
    }

    @Override protected int layout() {
        return R.layout.commit_file_full_layout;
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

    @NonNull @Override public TiPresenter providePresenter() {
        return new BasePresenter();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            commitFileModel = getIntent().getExtras().getParcelable(BundleConstant.ITEM);
        }
        if (commitFileModel == null || commitFileModel.getPatch() == null) {
            finish();
            return;
        }
        changes.setText(SpannableBuilder.builder()
                .append(changesText)
                .append("\n")
                .bold(String.valueOf(commitFileModel.getChanges())));
        addition.setText(SpannableBuilder.builder()
                .append(additionText)
                .append("\n")
                .bold(String.valueOf(commitFileModel.getAdditions())));
        deletion.setText(SpannableBuilder.builder()
                .append(deletionText)
                .append("\n")
                .bold(String.valueOf(commitFileModel.getDeletions())));
        status.setText(SpannableBuilder.builder()
                .append(statusText)
                .append("\n")
                .bold(String.valueOf(commitFileModel.getStatus())));
        setTitle(Uri.parse(commitFileModel.getFilename()).getLastPathSegment());
        textView.setText(DiffLineSpan.getSpannable(commitFileModel.getPatch(),
                ViewHelper.getPatchAdditionColor(this),
                ViewHelper.getPatchDeletionColor(this),
                ViewHelper.getPatchRefColor(this)));
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.commit_row_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.open:
                startActivity(CodeViewerActivity.createIntent(this, commitFileModel.getContentsUrl(), commitFileModel.getBlobUrl()));
                return true;
            case R.id.share:
                ActivityHelper.shareUrl(this, commitFileModel.getBlobUrl());
                return true;
            case R.id.download:
                if (ActivityHelper.checkAndRequestReadWritePermission(this)) {
                    RestProvider.downloadFile(this, commitFileModel.getRawUrl());
                }
                return true;
            case R.id.copy:
                AppHelper.copyToClipboard(this, commitFileModel.getBlobUrl());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
