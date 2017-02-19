package com.fastaccess.ui.modules.editor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.CommentsModel;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.provider.markdown.MarkDownProvider;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.widgets.FontButton;
import com.fastaccess.ui.widgets.FontEditText;
import com.fastaccess.ui.widgets.ForegroundImageView;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import icepick.State;

/**
 * Created by Kosh on 27 Nov 2016, 1:32 AM
 */

public class EditorView extends BaseActivity<EditorMvp.View, EditorPresenter> implements EditorMvp.View {

    private CharSequence savedText;
    @BindView(R.id.cancel) FontButton cancel;
    @BindView(R.id.ok) FontButton ok;
    @BindView(R.id.view) ForegroundImageView viewCode;
    @BindView(R.id.editText) FontEditText editText;

    @State @BundleConstant.ExtraTYpe String extraType;
    @State String itemId;
    @State String login;
    @State int issueNumber;
    @State long commentId = 0;
    @State String sha;

    @Override protected int layout() {
        return R.layout.editor_layout;
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

    @NonNull @Override public EditorPresenter providePresenter() {
        return new EditorPresenter();
    }

    @OnTextChanged(value = R.id.editText, callback = OnTextChanged.Callback.TEXT_CHANGED) void onEdited(CharSequence charSequence) {
        if (viewCode.getTag() != null) {
            return;
        }
        savedText = charSequence;
    }

    @OnClick(R.id.view) void onViewMarkDown(View v) {
        if (InputHelper.isEmpty(editText)) return;
        if (v.getTag() == null) {
            v.setTag("whatever");
            MarkDownProvider.setMdText(editText, InputHelper.toString(editText));
            ViewHelper.hideKeyboard(editText);
        } else {
            v.setTag(null);
            editText.setText(savedText);
            editText.setSelection(savedText.length());
        }
    }


    @OnClick({R.id.headerOne, R.id.headerTwo, R.id.headerThree, R.id.bold, R.id.italic,
            R.id.strikethrough, R.id.bullet, R.id.header, R.id.code, R.id.numbered,
            R.id.quote, R.id.link, R.id.image}) void onActions(View v) {
        if (viewCode.getTag() != null) {
            Snackbar.make(editText, R.string.error_highlighting_editor, Snackbar.LENGTH_SHORT).show();
            return;
        }
        getPresenter().onActionClicked(editText, v.getId());
    }

    @OnClick(value = {R.id.ok, R.id.cancel}) void onClick(View view) {
        if (view.getId() == R.id.ok) {
            getPresenter().onHandleSubmission(savedText, extraType, itemId, commentId, login, issueNumber, sha);
        } else {
            finish();
        }
    }

    @OnClick(R.id.back) void onBack() {
        finish();
    }

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent != null && intent.getExtras() != null) {
                Bundle bundle = intent.getExtras();
                //noinspection WrongConstant
                extraType = bundle.getString(BundleConstant.EXTRA_TYPE);
                itemId = bundle.getString(BundleConstant.ID);
                login = bundle.getString(BundleConstant.EXTRA_TWO);
                if (extraType.equalsIgnoreCase(BundleConstant.ExtraTYpe.EDIT_COMMIT_COMMENT_EXTRA) ||
                        extraType.equalsIgnoreCase(BundleConstant.ExtraTYpe.NEW_COMMIT_COMMENT_EXTRA)) {
                    sha = bundle.getString(BundleConstant.EXTRA_THREE);
                } else {
                    issueNumber = bundle.getInt(BundleConstant.EXTRA_THREE);
                }
                commentId = bundle.getLong(BundleConstant.EXTRA_FOUR);

                editText.setText(bundle.getString(BundleConstant.EXTRA));
            }
        }
    }

    @Override public void onSendResultAndFinish(@NonNull CommentsModel commentModel, boolean isNew) {
        hideProgress();
        Intent intent = new Intent();
        intent.putExtras(Bundler.start()
                .put(BundleConstant.ITEM, commentModel)
                .put(BundleConstant.EXTRA, isNew)
                .end());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override public void onSendMarkDownResult() {
        Intent intent = new Intent();
        intent.putExtras(Bundler.start().put(BundleConstant.EXTRA, savedText).end());
        setResult(RESULT_OK, intent);
        finish();
    }
}
