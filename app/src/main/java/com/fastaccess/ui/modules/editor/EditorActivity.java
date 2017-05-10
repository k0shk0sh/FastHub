package com.fastaccess.ui.modules.editor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import com.fastaccess.R;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.helper.AnimHelper;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.provider.markdown.MarkDownProvider;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.modules.editor.popup.EditorLinkImageDialogFragment;
import com.fastaccess.ui.widgets.FontEditText;
import com.fastaccess.ui.widgets.ForegroundImageView;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import icepick.State;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

/**
 * Created by Kosh on 27 Nov 2016, 1:32 AM
 */

public class EditorActivity extends BaseActivity<EditorMvp.View, EditorPresenter> implements EditorMvp.View {

    private CharSequence savedText = "";
    @BindView(R.id.view) ForegroundImageView viewCode;
    @BindView(R.id.editText) FontEditText editText;
    @BindView(R.id.editorIconsHolder) View editorIconsHolder;
    @BindView(R.id.sentVia) CheckBox sentVia;

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
        if (editText.isEnabled()) {
            savedText = charSequence;
        }
    }

    @OnClick(R.id.view) void onViewMarkDown() {
        if (editText.isEnabled() && !InputHelper.isEmpty(editText)) {
            editText.setEnabled(false);
            sentVia.setEnabled(false);
            MarkDownProvider.setMdText(editText, InputHelper.toString(editText));
            ViewHelper.hideKeyboard(editText);
            AnimHelper.animateVisibility(editorIconsHolder, false);
        } else {
            editText.setText(savedText);
            editText.setSelection(savedText.length());
            editText.setEnabled(true);
            sentVia.setEnabled(true);
            ViewHelper.showKeyboard(editText);
            AnimHelper.animateVisibility(editorIconsHolder, true);
        }
    }

    @OnClick({R.id.headerOne, R.id.headerTwo, R.id.headerThree, R.id.bold, R.id.italic,
            R.id.strikethrough, R.id.bullet, R.id.header, R.id.code, R.id.numbered,
            R.id.quote, R.id.link, R.id.image, R.id.unCheckbox, R.id.checkbox}) void onActions(View v) {
        if (!editText.isEnabled()) {
            Snackbar.make(editText, R.string.error_highlighting_editor, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (v.getId() == R.id.link) {
            EditorLinkImageDialogFragment.newInstance(true).show(getSupportFragmentManager(), "EditorLinkImageDialogFragment");
        } else if (v.getId() == R.id.image) {
            EditorLinkImageDialogFragment.newInstance(false).show(getSupportFragmentManager(), "EditorLinkImageDialogFragment");
        } else {
            getPresenter().onActionClicked(editText, v.getId());
        }
    }

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sentVia.setChecked(PrefGetter.isSentViaEnabled());
        sentVia.setText(getString(R.string.sent_from_fasthub, AppHelper.getDeviceName(), getString(R.string.app_name)));
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
                String textToUpdate = bundle.getString(BundleConstant.EXTRA);
                if (!InputHelper.isEmpty(textToUpdate)) {
                    editText.setText(String.format("%s ", textToUpdate));
                    editText.setSelection(InputHelper.toString(editText).length());
                }
            }
        }
        if (!PrefGetter.isEditorHintShowed()) {
            new MaterialTapTargetPrompt.Builder(this)
                    .setTarget(viewCode)
                    .setPrimaryText(R.string.view_code)
                    .setSecondaryText(R.string.click_to_toggle_highlighting)
                    .setCaptureTouchEventOutsidePrompt(true)
                    .setBackgroundColourAlpha(244)
                    .setBackgroundColour(ViewHelper.getAccentColor(EditorActivity.this))
                    .show();
        }
    }

    @Override public void onSendResultAndFinish(@NonNull Comment commentModel, boolean isNew) {
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

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.submit) {
            if (sentVia.isChecked()) {
                String sentFromFastHub = getString(R.string.sent_from_fasthub, AppHelper.getDeviceName(),
                        "[" + getString(R.string.app_name) + "](https://play.google.com/store/apps/details?id=com.fastaccess.github)");
                String temp = savedText.toString();
                String signature = "\n\n_" + sentFromFastHub + "_";
                if (!temp.contains(signature) || !temp.contains(sentFromFastHub)) {
                    savedText = savedText + signature;
                }
            }
            getPresenter().onHandleSubmission(savedText, extraType, itemId, commentId, login, issueNumber, sha);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public boolean onPrepareOptionsMenu(Menu menu) {
        if (menu.findItem(R.id.submit) != null) {
            menu.findItem(R.id.submit).setEnabled(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override public void showProgress(@StringRes int resId) {
        super.showProgress(resId);
        supportInvalidateOptionsMenu();
    }

    @Override public void hideProgress() {
        supportInvalidateOptionsMenu();
        super.hideProgress();
    }

    @Override public void onBackPressed() {
        if (InputHelper.isEmpty(editText)) {
            super.onBackPressed();
        } else {
            ViewHelper.hideKeyboard(editText);
            MessageDialogView.newInstance(getString(R.string.close), getString(R.string.unsaved_data_warning),
                    Bundler.start().put(BundleConstant.YES_NO_EXTRA, true).put(BundleConstant.EXTRA, true).end())
                    .show(getSupportFragmentManager(), MessageDialogView.TAG);
        }
    }

    @Override public void onMessageDialogActionClicked(boolean isOk, @Nullable Bundle bundle) {
        super.onMessageDialogActionClicked(isOk, bundle);
        if (isOk && bundle != null) {
            finish();
        }
    }

    @Override public void onAppendLink(@Nullable String title, @Nullable String link, boolean isLink) {
        if (isLink) {
            MarkDownProvider.addLink(editText, InputHelper.toString(title), InputHelper.toString(link));
        } else {
            MarkDownProvider.addPhoto(editText, InputHelper.toString(title), InputHelper.toString(link));
        }
    }
}
