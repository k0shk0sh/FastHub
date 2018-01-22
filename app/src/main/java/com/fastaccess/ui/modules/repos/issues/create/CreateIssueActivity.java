package com.fastaccess.ui.modules.repos.issues.create;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.danielstone.materialaboutlibrary.ConvenienceBuilder;
import com.evernote.android.state.State;
import com.fastaccess.App;
import com.fastaccess.BuildConfig;
import com.fastaccess.R;
import com.fastaccess.data.dao.LabelListModel;
import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.data.dao.MilestoneModel;
import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.Logger;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.provider.markdown.MarkDownProvider;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.modules.editor.EditorActivity;
import com.fastaccess.ui.modules.repos.extras.assignees.AssigneesDialogFragment;
import com.fastaccess.ui.modules.repos.extras.labels.LabelsDialogFragment;
import com.fastaccess.ui.modules.repos.extras.milestone.create.MilestoneDialogFragment;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.LabelSpan;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTouch;
import es.dmoral.toasty.Toasty;

/**
 * Created by Kosh on 19 Feb 2017, 12:33 PM
 */

public class CreateIssueActivity extends BaseActivity<CreateIssueMvp.View, CreateIssuePresenter> implements CreateIssueMvp.View {

    @BindView(R.id.title) TextInputLayout title;
    @BindView(R.id.description) FontTextView description;
    @BindView(R.id.submit) View submit;
    @BindView(R.id.issueMiscLayout) LinearLayout issueMiscLayout;
    @BindView(R.id.assignee) FontTextView assignee;
    @BindView(R.id.labels) FontTextView labels;
    @BindView(R.id.milestoneTitle) FontTextView milestoneTitle;
    @BindView(R.id.milestoneDescription) FontTextView milestoneDescription;

    @State String repoId;
    @State String login;
    @State Issue issue;
    @State PullRequest pullRequest;
    @State boolean isFeedback;
    @State ArrayList<LabelModel> labelModels = new ArrayList<>();
    @State MilestoneModel milestoneModel;
    @State ArrayList<User> users = new ArrayList<>();

    private AlertDialog alertDialog;
    private CharSequence savedText;

    public static void startForResult(@NonNull Fragment fragment, @NonNull String login, @NonNull String repoId, boolean isEnterprise) {
        Intent intent = new Intent(fragment.getContext(), CreateIssueActivity.class);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA_TWO, login.equalsIgnoreCase("k0shk0sh") && repoId.equalsIgnoreCase("FastHub"))
                .put(BundleConstant.IS_ENTERPRISE, isEnterprise)
                .end());
        View view = fragment.getActivity() != null ? fragment.getActivity().findViewById(R.id.fab) : null;
        if (view != null) {
            ActivityHelper.startReveal(fragment, intent, view, BundleConstant.REQUEST_CODE);
        } else {
            fragment.startActivityForResult(intent, BundleConstant.REQUEST_CODE);
        }
    }


    public static void startForResult(@NonNull Activity activity, @NonNull String login, @NonNull String repoId,
                                      @Nullable Issue issueModel, boolean isEnterprise) {
        if (issueModel != null) {
            Intent intent = new Intent(activity, CreateIssueActivity.class);
            intent.putExtras(Bundler.start()
                    .put(BundleConstant.EXTRA, login)
                    .put(BundleConstant.ID, repoId)
                    .put(BundleConstant.ITEM, issueModel)
                    .put(BundleConstant.IS_ENTERPRISE, isEnterprise)
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
                                      @Nullable PullRequest pullRequestModel, boolean isEnterprise) {
        if (pullRequestModel != null) {
            Intent intent = new Intent(activity, CreateIssueActivity.class);
            intent.putExtras(Bundler.start()
                    .put(BundleConstant.EXTRA, login)
                    .put(BundleConstant.ID, repoId)
                    .put(BundleConstant.ITEM, pullRequestModel)
                    .put(BundleConstant.IS_ENTERPRISE, isEnterprise)
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

    @Override public void onShowUpdate() {
        hideProgress();
        Toasty.error(App.getInstance(), getString(R.string.new_version)).show();
        ConvenienceBuilder.createRateOnClickAction(this).onClick();
        finish();
    }

    @Override public void onShowIssueMisc() {
        TransitionManager.beginDelayedTransition(findViewById(R.id.parent));
        issueMiscLayout.setVisibility(getPresenter().isCollaborator() ? View.VISIBLE : View.GONE);
        //TODO
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
                Logger.e(issue.getLabels(), issue.getMilestone(), issue.getAssignees());
                if (issue.getLabels() != null) {
                    onSelectedLabels(new ArrayList<>(issue.getLabels()));
                }
                if (issue.getAssignees() != null) {
                    onSelectedAssignees(new ArrayList<>(issue.getAssignees()), false);
                }
                if (issue.getMilestone() != null) {
                    milestoneModel = issue.getMilestone();
                    onMilestoneSelected(milestoneModel);
                }
                if (!InputHelper.isEmpty(issue.getTitle())) {
                    if (title.getEditText() != null) title.getEditText().setText(issue.getTitle());
                }
                if (!InputHelper.isEmpty(issue.getBody())) {
                    onSetCode(issue.getBody());
                }
            }
            if (pullRequest != null) {
                if (pullRequest.getLabels() != null) {
                    onSelectedLabels(new ArrayList<>(pullRequest.getLabels()));
                }
                if (pullRequest.getAssignees() != null) {
                    users.addAll(pullRequest.getAssignees());
                    onSelectedAssignees(new ArrayList<>(pullRequest.getAssignees()), false);
                }
                if (pullRequest.getMilestone() != null) {
                    milestoneModel = pullRequest.getMilestone();
                    onMilestoneSelected(milestoneModel);
                }
                if (!InputHelper.isEmpty(pullRequest.getTitle())) {
                    if (title.getEditText() != null) title.getEditText().setText(pullRequest.getTitle());
                }
                if (!InputHelper.isEmpty(pullRequest.getBody())) {
                    onSetCode(pullRequest.getBody());
                }
            }
        }
        getPresenter().checkAuthority(login, repoId);
        if (isFeedback || ("k0shk0sh".equalsIgnoreCase(login) && repoId.equalsIgnoreCase("FastHub"))) {
            Toasty.info(App.getInstance(), getString(R.string.report_issue_warning), Toast.LENGTH_LONG).show();
            setTitle(R.string.submit_feedback);
            getPresenter().onCheckAppVersion();
        }
        if (BuildConfig.DEBUG && isFeedback) {
            alertDialog = new AlertDialog.Builder(this)
                    .setTitle("You are currently using a debug build")
                    .setMessage("If you have found a bug, please report it on slack." + "\n" +
                            "Feature requests can be submitted here." + "\n" + "Happy Testing")
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        }
        if (toolbar != null) toolbar.setSubtitle(login + "/" + repoId);
        setTaskName(login + "/" + repoId + " - " + (isFeedback ? getString(R.string.submit_feedback) : getString(R.string.create_issue)));
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AppHelper.hideKeyboard(title);
        getPresenter().onActivityForResult(resultCode, requestCode, data);
    }

    @Override public void onBackPressed() {
        if (InputHelper.isEmpty(title)) {
            super.onBackPressed();
        } else {
            ViewHelper.hideKeyboard(title);
            MessageDialogView.newInstance(getString(R.string.close), getString(R.string.unsaved_data_warning),
                    Bundler.start().put("primary_extra", getString(R.string.discard)).put("secondary_extra", getString(R.string.cancel))
                            .put(BundleConstant.EXTRA, true).end()).show(getSupportFragmentManager(), MessageDialogView.TAG);
        }
    }

    @Override protected void onDestroy() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override public void onMessageDialogActionClicked(boolean isOk, @Nullable Bundle bundle) {
        super.onMessageDialogActionClicked(isOk, bundle);
        if (isOk && bundle != null) {
            finish();
        }
    }

    @OnTouch(R.id.description) boolean onTouch(MotionEvent event) {
        if (isFeedback && InputHelper.isEmpty(savedText)) {
            savedText = AppHelper.getFastHubIssueTemplate(isEnterprise());
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            Intent intent = new Intent(this, EditorActivity.class);
            intent.putExtras(Bundler.start()
                    .put(BundleConstant.EXTRA, InputHelper.toString(savedText))
                    .put(BundleConstant.EXTRA_TYPE, BundleConstant.ExtraType.FOR_RESULT_EXTRA)
                    .put(BundleConstant.IS_ENTERPRISE, isEnterprise())
                    .end());
            ActivityHelper.startReveal(this, intent, submit, BundleConstant.REQUEST_CODE);
            return true;
        }
        return false;
    }

    @OnClick(R.id.submit) public void onClick() {
        getPresenter().onSubmit(InputHelper.toString(title), savedText, login, repoId, issue, pullRequest, labelModels, milestoneModel, users);
    }

    @OnClick({R.id.addAssignee, R.id.addLabels, R.id.addMilestone}) public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.addAssignee:
                AssigneesDialogFragment.newInstance(login, repoId, false)
                        .show(getSupportFragmentManager(), "AssigneesDialogFragment");
                break;
            case R.id.addLabels:
                LabelListModel labelModels = new LabelListModel();
                labelModels.addAll(this.labelModels);
                LabelsDialogFragment.newInstance(labelModels, repoId, login)
                        .show(getSupportFragmentManager(), "LabelsDialogFragment");
                break;
            case R.id.addMilestone:
                MilestoneDialogFragment.newInstance(login, repoId)
                        .show(getSupportFragmentManager(), "MilestoneDialogFragment");
                break;
        }
    }

    @Override public void onSelectedLabels(@NonNull ArrayList<LabelModel> labelModels) {
        this.labelModels.clear();
        this.labelModels.addAll(labelModels);
        SpannableBuilder builder = SpannableBuilder.builder();
        for (int i = 0; i < labelModels.size(); i++) {
            LabelModel labelModel = labelModels.get(i);
            int color = Color.parseColor("#" + labelModel.getColor());
            if (i > 0) {
                builder.append(" ").append(" " + labelModel.getName() + " ", new LabelSpan(color));
            } else {
                builder.append(labelModel.getName() + " ", new LabelSpan(color));
            }
        }
        this.labels.setText(builder);
    }

    @Override public void onMilestoneSelected(@NonNull MilestoneModel milestoneModel) {
        Logger.e(milestoneModel.getTitle(), milestoneModel.getDescription(), milestoneModel.getNumber());
        this.milestoneModel = milestoneModel;
        milestoneTitle.setText(milestoneModel.getTitle());
        if (!InputHelper.isEmpty(milestoneModel.getDescription())) {
            milestoneDescription.setText(milestoneModel.getDescription());
            milestoneDescription.setVisibility(View.VISIBLE);
        } else {
            milestoneDescription.setText("");
            milestoneDescription.setVisibility(View.GONE);
        }
    }

    @Override public void onSelectedAssignees(@NonNull ArrayList<User> users, boolean isAssignees) {
        this.users.clear();
        this.users.addAll(users);
        SpannableBuilder builder = SpannableBuilder.builder();
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            builder.append(user.getLogin());
            if (i != users.size() - 1) {
                builder.append(", ");
            }
        }
        assignee.setText(builder);
    }
}
