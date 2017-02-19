package com.fastaccess.ui.modules.repos.issues.create;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;

import com.fastaccess.R;
import com.fastaccess.data.dao.IssueModel;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.markdown.MarkDownProvider;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.modules.editor.EditorView;
import com.fastaccess.ui.widgets.FontTextView;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTouch;
import icepick.State;

/**
 * Created by Kosh on 19 Feb 2017, 12:33 PM
 */

public class CreateIssueView extends BaseActivity<CreateIssueMvp.View, CreateIssuePresenter> implements CreateIssueMvp.View {

    @BindView(R.id.title) TextInputLayout title;
    @BindView(R.id.description) FontTextView description;
    @State String repoId;
    @State String login;

    private CharSequence savedText;

    public static void startForResult(@NonNull Fragment fragment, @NonNull String login, @NonNull String repoId) {
        Intent intent = new Intent(fragment.getContext(), CreateIssueView.class);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.ID, repoId)
                .end());
        fragment.startActivityForResult(intent, BundleConstant.REQUEST_CODE);
    }

    @Override public void onSetCode(@NonNull CharSequence charSequence) {
        this.savedText = charSequence;
        MarkDownProvider.setMdText(description, InputHelper.toString(charSequence));
    }

    @Override public void onTitleError(boolean isEmptyTitle) {
        title.setError(isEmptyTitle ? getString(R.string.required_field) : null);
    }

    @Override public void onDescriptionError(boolean isEmptyDesc) {
        description.setError(isEmptyDesc ? getString(R.string.required_field) : null);
    }

    @Override public void onSuccessSubmission(IssueModel issueModel) {
        hideProgress();
        Intent intent = new Intent();
        intent.putExtras(Bundler.start().put(BundleConstant.ITEM, issueModel).end());
        setResult(RESULT_OK, intent);
        finish();
        showMessage(R.string.success, R.string.successfully_submitted);
    }

    @NonNull @Override public CreateIssuePresenter providePresenter() {
        return new CreateIssuePresenter();
    }

    @Override protected int layout() {
        return R.layout.create_issue_layout;
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

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            login = getIntent().getExtras().getString(BundleConstant.EXTRA);
            repoId = getIntent().getExtras().getString(BundleConstant.ID);
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AppHelper.hideKeyboard(title);
        getPresenter().onActivityForResult(resultCode, requestCode, data);
    }

    @OnTouch(R.id.description) boolean onTouch(MotionEvent event) {
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

    @OnClick(R.id.createSecretGist) public void onClick() {
        getPresenter().onSubmit(InputHelper.toString(title), savedText, login, repoId);
    }
}
