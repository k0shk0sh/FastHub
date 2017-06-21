package com.fastaccess.ui.modules.repos.issues.create;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;

import com.evernote.android.state.State;
import com.fastaccess.BuildConfig;
import com.fastaccess.R;
import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.markdown.MarkDownProvider;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.modules.editor.EditorActivity;
import com.fastaccess.ui.widgets.FontTextView;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTouch;

/**
 * Created by Kosh on 19 Feb 2017, 12:33 PM
 */

public class CreateIssueActivity extends BaseActivity<CreateIssueMvp.View, CreateIssuePresenter> implements CreateIssueMvp.View {

    @BindView(R.id.title) TextInputLayout title;
    @BindView(R.id.description) FontTextView description;
    @BindView(R.id.submit) View submit;
    @State String repoId;
    @State String login;
    @State Issue issue;
    @State PullRequest pullRequest;
    @State boolean isFeedback;

    private CharSequence savedText;

    public static void startForResult(@NonNull Fragment fragment, @NonNull String login, @NonNull String repoId) {
        Intent intent = new Intent(fragment.getContext(), CreateIssueActivity.class);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA_TWO, login.equalsIgnoreCase("k0shk0sh") && repoId.equalsIgnoreCase("FastHub"))
                .end());
        View view = fragment.getActivity() != null ? fragment.getActivity().findViewById(R.id.fab) : null;
        if (view != null) {
            ActivityHelper.startReveal(fragment, intent, view, BundleConstant.REQUEST_CODE);
        } else {
            fragment.startActivityForResult(intent, BundleConstant.REQUEST_CODE);
        }
    }


    public static void startForResult(@NonNull Activity activity, @NonNull String login, @NonNull String repoId,
                                      @Nullable Issue issueModel) {
        if (issueModel != null) {
            Intent intent = new Intent(activity, CreateIssueActivity.class);
            intent.putExtras(Bundler.start()
                    .put(BundleConstant.EXTRA, login)
                    .put(BundleConstant.ID, repoId)
                    .put(BundleConstant.ITEM, issueModel)
                    .end());
            View view = activity.findViewById(R.id.fab);
            if (view != null) {
                startForResult(activity, intent, view);
            } else {
                activity.startActivityForResult(intent, BundleConstant.REQUEST_CODE);
            }
        }
    }

    public static void startForResult(@NonNull Activity activity, @NonNull String login, @NonNull String repoId,
                                      @Nullable PullRequest pullRequestModel) {
        if (pullRequestModel != null) {
            Intent intent = new Intent(activity, CreateIssueActivity.class);
            intent.putExtras(Bundler.start()
                    .put(BundleConstant.EXTRA, login)
                    .put(BundleConstant.ID, repoId)
                    .put(BundleConstant.ITEM, pullRequestModel)
                    .end());
            View view = activity.findViewById(R.id.fab);
            if (view != null) {
                startForResult(activity, intent, view);
            } else {
                activity.startActivityForResult(intent, BundleConstant.REQUEST_CODE);
            }
        }
    }

    @NonNull public static Intent getIntent(@NonNull Context context, @NonNull String login, @NonNull String repoId, boolean isFeedback) {
        Intent intent = new Intent(context, CreateIssueActivity.class);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA_TWO, isFeedback)
                .end());
        return intent;
    }

    @NonNull public static Intent startForResult(@NonNull Activity activity) {
        String login = "k0shk0sh"; // FIXME: 23/02/2017 hardcoded
        String repoId = "FastHub";// FIXME: 23/02/2017 hardcoded
        Intent intent = new Intent(activity, CreateIssueActivity.class);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA_TWO, true)
                .end());
        return intent;
    }

    public static void startForResult(@NonNull Activity activity, @NonNull Intent intent, @NonNull View view) {
        ActivityHelper.startReveal(activity, intent, view, BundleConstant.REQUEST_CODE);
    }

    @Override public void onSetCode(@NonNull CharSequence charSequence) {
        this.savedText = charSequence;
        MarkDownProvider.setMdText(description, InputHelper.toString(savedText));
    }

    @Override public void onTitleError(boolean isEmptyTitle) {
        title.setError(isEmptyTitle ? getString(R.string.required_field) : null);
    }

    @Override public void onDescriptionError(boolean isEmptyDesc) {
        description.setError(isEmptyDesc ? getString(R.string.required_field) : null);
    }

    @Override public void onSuccessSubmission(Issue issueModel) {
        hideProgress();
        Intent intent = new Intent();
        intent.putExtras(Bundler.start()
                .put(BundleConstant.ITEM, issueModel)
                .end());
        setResult(RESULT_OK, intent);
        finish();
        showMessage(R.string.success, R.string.successfully_submitted);
    }

    @Override public void onSuccessSubmission(PullRequest issueModel) {
        hideProgress();
        Intent intent = new Intent();
        intent.putExtras(Bundler.start()
                .put(BundleConstant.ITEM, issueModel)
                .end());
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
            Bundle bundle = getIntent().getExtras();
            login = bundle.getString(BundleConstant.EXTRA);
            repoId = bundle.getString(BundleConstant.ID);
            isFeedback = bundle.getBoolean(BundleConstant.EXTRA_TWO);
            if (bundle.getParcelable(BundleConstant.ITEM) != null) {
                if (bundle.getParcelable(BundleConstant.ITEM) instanceof Issue) {
                    issue = bundle.getParcelable(BundleConstant.ITEM);
                    setTitle(getString(R.string.update_issue));
                } else if (bundle.getParcelable(BundleConstant.ITEM) instanceof PullRequest) {
                    pullRequest = bundle.getParcelable(BundleConstant.ITEM);
                    setTitle(getString(R.string.update_pull_request));
                }
            }
            if (issue != null) {
                if (!InputHelper.isEmpty(issue.getTitle())) {
                    if (title.getEditText() != null) title.getEditText().setText(issue.getTitle());
                }
                if (!InputHelper.isEmpty(issue.getBody())) {
                    onSetCode(issue.getBody());
                }
            }
            if (pullRequest != null) {
                if (!InputHelper.isEmpty(pullRequest.getTitle())) {
                    if (title.getEditText() != null) title.getEditText().setText(pullRequest.getTitle());
                }
                if (!InputHelper.isEmpty(pullRequest.getBody())) {
                    onSetCode(pullRequest.getBody());
                }
            }
        }
        if (isFeedback) setTitle(R.string.submit_feedback);
        if (BuildConfig.DEBUG && isFeedback) {
            new AlertDialog.Builder(this)
                    .setTitle("You are currently using a debug build")
                    .setMessage("If you have found a bug, please report it on slack." + "\n" +
                            "Feature requests can be submitted here." + "\n" + "Happy Testing")
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        }
        if (toolbar != null) toolbar.setSubtitle(login + "/" + repoId);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AppHelper.hideKeyboard(title);
        getPresenter().onActivityForResult(resultCode, requestCode, data);
    }

    @OnTouch(R.id.description) boolean onTouch(MotionEvent event) {
        if (isFeedback && InputHelper.isEmpty(savedText)) {
            savedText = AppHelper.getFastHubIssueTemplate();
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            Intent intent = new Intent(this, EditorActivity.class);
            intent.putExtras(Bundler.start()
                    .put(BundleConstant.EXTRA, InputHelper.toString(savedText))
                    .put(BundleConstant.EXTRA_TYPE, BundleConstant.ExtraTYpe.FOR_RESULT_EXTRA)
                    .end());
            ActivityHelper.startReveal(this, intent, submit, BundleConstant.REQUEST_CODE);
            return true;
        }
        return false;
    }

    @OnClick(R.id.submit) public void onClick() {
        getPresenter().onSubmit(InputHelper.toString(title), savedText, login, repoId, issue, pullRequest);
    }
}
