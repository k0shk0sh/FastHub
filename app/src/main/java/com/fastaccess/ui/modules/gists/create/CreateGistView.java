package com.fastaccess.ui.modules.gists.create;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.fastaccess.R;
import com.fastaccess.data.dao.GistsModel;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.markdown.MarkDownProvider;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.modules.editor.EditorView;
import com.fastaccess.ui.widgets.FontButton;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTouch;

/**
 * Created by Kosh on 30 Nov 2016, 11:02 AM
 */

public class CreateGistView extends BaseActivity<CreateGistMvp.View, CreateGistPresenter> implements CreateGistMvp.View {

    @BindView(R.id.description) TextInputLayout description;
    @BindView(R.id.fileName) TextInputLayout fileName;
    @BindView(R.id.fileContent) TextView fileContent;
    private CharSequence savedText;

    @Override public void onSetCode(@NonNull CharSequence charSequence) {
        this.savedText = charSequence;
        //noinspection ConstantConditions
        MarkDownProvider.setMdText(fileContent, InputHelper.toString(charSequence));
    }

    @Override public void onDescriptionError(boolean isEmptyDesc) {
        description.setError(isEmptyDesc ? getString(R.string.required_field) : null);
    }

    @Override public void onFileNameError(boolean isEmptyDesc) {
        fileName.setError(isEmptyDesc ? getString(R.string.required_field) : null);
    }

    @Override public void onFileContentError(boolean isEmptyDesc) {
        fileContent.setError(isEmptyDesc ? getString(R.string.required_field) : null);
    }

    @Override public void onSuccessSubmission(GistsModel gistsModel) {
        hideProgress();
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
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getPresenter().onActivityForResult(resultCode, requestCode, data);
    }

    @OnTouch(R.id.fileContent) boolean onTouch(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            Intent intent = new Intent(this, EditorView.class);
            intent.putExtras(Bundler.start()
                    .put(BundleConstant.EXTRA, InputHelper.toString(savedText))
                    .put(BundleConstant.EXTRA_TYPE, BundleConstant.ExtraTYpe.FOR_RESULT_EXTRA)
                    .end());
            startActivityForResult(intent, BundleConstant.REQUEST_CODE);
            return true;
        }
        return false;
    }

    @OnClick(value = {R.id.createPublicGist, R.id.createSecretGist}) void onClick(View view) {
        getPresenter().onSubmit(description, fileName, savedText, view.getId() == R.id.createPublicGist);
    }
}
