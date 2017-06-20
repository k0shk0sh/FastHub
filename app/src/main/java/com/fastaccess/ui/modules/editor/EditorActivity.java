package com.fastaccess.ui.modules.editor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.evernote.android.state.State;
import com.fastaccess.R;
import com.fastaccess.data.dao.EditReviewCommentModel;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.AnimHelper;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.helper.PrefHelper;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.provider.markdown.MarkDownProvider;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.modules.editor.popup.EditorLinkImageDialogFragment;
import com.fastaccess.ui.widgets.FontEditText;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.ForegroundImageView;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnTextChanged;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

import static android.view.View.GONE;

/**
 * Created by Kosh on 27 Nov 2016, 1:32 AM
 */

public class EditorActivity extends BaseActivity<EditorMvp.View, EditorPresenter> implements EditorMvp.View {

    private String sentFromFastHub;

    private ArrayList<String> participants;
    private int inMentionMode = -1;
    private CharSequence savedText = "";
    @BindView(R.id.replyQuote) LinearLayout replyQuote;
    @BindView(R.id.replyQuoteText) FontTextView quote;
    @BindView(R.id.view) ForegroundImageView viewCode;
    @BindView(R.id.editText) FontEditText editText;
    @BindView(R.id.editorIconsHolder) View editorIconsHolder;
    @BindView(R.id.sentVia) CheckBox sentVia;
    @BindView(R.id.list_divider) View listDivider;
    @BindView(R.id.parentView) View parentView;
    @BindView(R.id.autocomplete) ListView mention;

    @State @BundleConstant.ExtraTYpe String extraType;
    @State String itemId;
    @State String login;
    @State int issueNumber;
    @State long commentId = 0;
    @State String sha;
    @State EditReviewCommentModel reviewComment;

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
            mention(charSequence);
        }
    }

    @OnItemClick(R.id.autocomplete) void onMentionSelection(int position) {
        try {
            String complete = mention.getAdapter().getItem(position).toString() + " ";
            int end = editText.getSelectionEnd();
            editText.getText().replace(inMentionMode, end, complete, 0, complete.length());
            inMentionMode = -1;
        } catch (Exception ignored) {}
        mention.setVisibility(GONE);
        listDivider.setVisibility(GONE);
    }

    @OnClick(R.id.replyQuoteText) void onToggleQuote() {
        TransitionManager.beginDelayedTransition((ViewGroup) parentView);
        if (quote.getMaxLines() == 3) {
            quote.setMaxLines(Integer.MAX_VALUE);
        } else {
            quote.setMaxLines(3);
        }
        quote.setCompoundDrawablesWithIntrinsicBounds(0, 0, quote.getMaxLines() == 3
                                                            ? R.drawable.ic_arrow_drop_down : R.drawable.ic_arrow_drop_up, 0);
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
            EditorLinkImageDialogFragment.newInstance(true).show(getSupportFragmentManager(), "BannerDialogFragment");
        } else if (v.getId() == R.id.image) {
            EditorLinkImageDialogFragment.newInstance(false).show(getSupportFragmentManager(), "BannerDialogFragment");
        } else {
            getPresenter().onActionClicked(editText, v.getId());
        }
    }

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarIcon(R.drawable.ic_clear);
        sentFromFastHub = "\n\n_" + getString(R.string.sent_from_fasthub, AppHelper.getDeviceName(), "",
                "[" + getString(R.string.app_name) + "](https://play.google.com/store/apps/details?id=com.fastaccess.github)") + "_";
        sentVia.setVisibility(PrefGetter.isSentViaBoxEnabled() ? View.VISIBLE : GONE);
        sentVia.setChecked(PrefGetter.isSentViaEnabled());
        sentVia.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PrefHelper.set("sent_via", isChecked);
        });
        MarkDownProvider.setMdText(sentVia, sentFromFastHub);
        if (savedInstanceState == null) {
            onCreate();
        }
        if (!PrefGetter.isEditorHintShowed()) {
            new MaterialTapTargetPrompt.Builder(this)
                    .setTarget(viewCode)
                    .setPrimaryText(R.string.view_code)
                    .setSecondaryText(R.string.click_to_toggle_highlighting)
                    .setCaptureTouchEventOutsidePrompt(true)
                    .setBackgroundColourAlpha(244)
                    .setBackgroundColour(ViewHelper.getAccentColor(EditorActivity.this))
                    .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                        @Override
                        public void onHidePrompt(MotionEvent motionEvent, boolean b) {
                            ActivityHelper.hideDismissHints(EditorActivity.this);
                        }

                        @Override
                        public void onHidePromptComplete() {

                        }
                    })
                    .show();
            ActivityHelper.showDismissHints(this, () -> {
            });
        }

        if (editText.getText().toString().contains(sentFromFastHub)) {
            editText.setText(editText.getText().toString().replace(sentFromFastHub, ""));
            sentVia.setChecked(true);
        }

        editText.requestFocus();
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

    @Override public void onSendReviewResultAndFinish(EditReviewCommentModel comment, boolean isNew) {
        hideProgress();
        Intent intent = new Intent();
        intent.putExtras(Bundler.start()
                .put(BundleConstant.ITEM, comment)
                .put(BundleConstant.EXTRA, isNew)
                .end());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.submit) {
            if (PrefGetter.isSentViaEnabled()) {
                String temp = savedText.toString();
                if (!temp.contains(sentFromFastHub)) {
                    savedText = savedText + sentFromFastHub;
                }
            }
            getPresenter().onHandleSubmission(savedText, extraType, itemId, commentId, login, issueNumber, sha, reviewComment);
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
                    Bundler.start().put("primary_extra", getString(R.string.discard)).put("secondary_extra", getString(R.string.cancel))
                            .put(BundleConstant.EXTRA, true).end()).show(getSupportFragmentManager(), MessageDialogView.TAG);
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
            editText.setText(String.format("%s\n", editText.getText()));
            MarkDownProvider.addPhoto(editText, InputHelper.toString(title), InputHelper.toString(link));
        }
    }

    private void onCreate() {
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            //noinspection WrongConstant
            extraType = bundle.getString(BundleConstant.EXTRA_TYPE);
            reviewComment = bundle.getParcelable(BundleConstant.REVIEW_EXTRA);
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
            if (bundle.getString("message", "").isEmpty())
                replyQuote.setVisibility(GONE);
            else {
                MarkDownProvider.setMdText(quote, bundle.getString("message", ""));
            }
            participants = bundle.getStringArrayList("participants");
        }
    }

    private void updateMentionList(@NonNull String mentioning) {
        if (participants != null) {
            ArrayList<String> mentions = new ArrayList<>();
            for (String participant : participants)
                if (participant.toLowerCase().startsWith(mentioning.replace("@", "").toLowerCase()))
                    mentions.add(participant);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                    android.R.id.text1, mentions.subList(0, Math.min(mentions.size(), 3)));
            mention.setAdapter(adapter);
            Log.d(getLoggingTag(), mentions.toString());
        }
    }

    private void mention(CharSequence charSequence) {
        try {
            char lastChar = 0;
            if (charSequence.length() > 0) lastChar = charSequence.charAt(charSequence.length() - 1);
            if (lastChar != 0) {
                if (lastChar == '@') {
                    inMentionMode = editText.getSelectionEnd();
                    mention.setVisibility(GONE);
                    listDivider.setVisibility(GONE);
                    return;
                } else if (lastChar == ' ')
                    inMentionMode = -1;
                else if (inMentionMode > -1)
                    updateMentionList(charSequence.toString().substring(inMentionMode, editText.getSelectionEnd()));
                else {
                    String copy = editText.getText().toString().substring(0, editText.getSelectionEnd());
                    String[] list = copy.split("\\s+");
                    String last = list[list.length - 1];
                    if (last.startsWith("@")) {
                        inMentionMode = copy.lastIndexOf("@") + 1;
                        updateMentionList(charSequence.toString().substring(inMentionMode, editText.getSelectionEnd()));
                    }
                }
            } else {
                inMentionMode = -1;
            }
            if (inMentionMode > -1)
                if (mention != null) {
                    mention.setVisibility(inMentionMode > 0 ? View.VISIBLE : GONE);
                    listDivider.setVisibility(mention.getVisibility());
                }
        } catch (Exception ignored) {}
    }

}
