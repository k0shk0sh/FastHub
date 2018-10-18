package com.fastaccess.ui.modules.gists.create;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.evernote.android.state.State;
import com.fastaccess.R;
import com.fastaccess.data.dao.FilesListModel;
import com.fastaccess.data.dao.model.Gist;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.Logger;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.modules.gists.gist.files.GistFilesListFragment;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Kosh on 30 Nov 2016, 11:02 AM
 */

public class CreateGistActivity extends BaseActivity<CreateGistMvp.View, CreateGistPresenter> implements CreateGistMvp.View {

    @BindView(R.id.description) TextInputLayout description;
    @BindView(R.id.buttonsHolder) View buttonsHolder;
    @State String id;

    private GistFilesListFragment filesListFragment;


    public static void start(@NonNull Activity context, @NonNull Gist gistsModel) {
        Intent starter = new Intent(context, CreateGistActivity.class);
        putBundle(gistsModel, starter);
        context.startActivityForResult(starter, BundleConstant.REQUEST_CODE);
    }

    public static void start(@NonNull Fragment context, @NonNull Gist gistsModel) {
        Intent starter = new Intent(context.getContext(), CreateGistActivity.class);
        putBundle(gistsModel, starter);
        context.startActivityForResult(starter, BundleConstant.REQUEST_CODE);
    }

    private static void putBundle(@NonNull Gist gistsModel, @NonNull Intent starter) {
        String login = gistsModel.getOwner() != null ? gistsModel.getOwner().getLogin() :
                       gistsModel.getUser() != null ? gistsModel.getUser().getLogin() : "";
        starter.putExtras(Bundler.start()
                .putParcelableArrayList(BundleConstant.ITEM, gistsModel.getFilesAsList())
                .put(BundleConstant.EXTRA, Login.getUser().getLogin().equalsIgnoreCase(login))
                .put(BundleConstant.ID, gistsModel.getGistId())
                .put(BundleConstant.EXTRA_TWO, gistsModel.getDescription())
                .end());
    }

    @OnClick(value = {R.id.createPublicGist, R.id.createSecretGist}) void onClick(View view) {
        if (view.getId() == R.id.createSecretGist) {
            ActivityHelper.startCustomTab(this, "https://blog.github.com/2018-02-18-deprecation-notice-removing-anonymous-gist-creation/");
            return;
        }
        getPresenter().onSubmit(InputHelper.toString(description),
                getFilesFragment().getFiles(), view.getId() == R.id.createPublicGist);
    }

    @OnClick(R.id.addFile) public void onViewClicked() {
        Logger.e(getFilesFragment());
        getFilesFragment().onAddNewFile();
    }

    @Override public void onDescriptionError(boolean isEmptyDesc) {
        description.setError(isEmptyDesc ? getString(R.string.required_field) : null);
    }

    @Override public void onFileNameError(boolean isEmptyDesc) {
//        fileName.setError(isEmptyDesc ? getString(R.string.required_field) : null);
    }

    @Override public void onFileContentError(boolean isEmptyDesc) {
//        fileContent.setError(isEmptyDesc ? getString(R.string.required_field) : null);
    }

    @Override public void onSuccessSubmission(Gist gistsModel) {
        hideProgress();
        setResult(RESULT_OK);
        finish();
        showMessage(R.string.success, R.string.successfully_submitted);
    }

    @Override protected int layout() {
        return R.layout.create_gist_layout;
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

    @NonNull @Override public CreateGistPresenter providePresenter() {
        return new CreateGistPresenter();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPresenter().setEnterprise(PrefGetter.isEnterprise());
        setTaskName(getString(R.string.create_gist));
        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getExtras() != null) {
                Bundle bundle = getIntent().getExtras();
                ArrayList<FilesListModel> models = bundle.getParcelableArrayList(BundleConstant.ITEM);
                boolean isOwner = bundle.getBoolean(BundleConstant.EXTRA);
                id = bundle.getString(BundleConstant.ID);
                String descriptionText = bundle.getString(BundleConstant.EXTRA_TWO);
                if (description.getEditText() != null) description.getEditText().setText(descriptionText);
                getFilesFragment().onInitFiles(models, isOwner);
            } else {
                getFilesFragment().onInitFiles(new ArrayList<>(), true);
            }
        }
        buttonsHolder.setVisibility(!InputHelper.isEmpty(id) ? View.GONE : View.VISIBLE);
    }

    @Override public void onMessageDialogActionClicked(boolean isOk, @Nullable Bundle bundle) {
        super.onMessageDialogActionClicked(isOk, bundle);
        if (isOk && bundle != null) {
            finish();
        }
    }

    @Override public void onBackPressed() {
        if (InputHelper.isEmpty(description)) {
            super.onBackPressed();
        } else {
            ViewHelper.hideKeyboard(description);
            MessageDialogView.newInstance(getString(R.string.close), getString(R.string.unsaved_data_warning),
                    Bundler.start().put("primary_extra", getString(R.string.discard)).put("secondary_extra", getString(R.string.cancel))
                            .put(BundleConstant.EXTRA, true).end()).show(getSupportFragmentManager(), MessageDialogView.TAG);
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        if (!InputHelper.isEmpty(id)) {
            getMenuInflater().inflate(R.menu.done_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.submit) {
            getPresenter().onSubmitUpdate(id, InputHelper.toString(description), getFilesFragment().getFiles());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private GistFilesListFragment getFilesFragment() {
        if (filesListFragment == null) {
            filesListFragment = (GistFilesListFragment) getSupportFragmentManager().findFragmentById(R.id.files);
        }
        return filesListFragment;
    }
}
