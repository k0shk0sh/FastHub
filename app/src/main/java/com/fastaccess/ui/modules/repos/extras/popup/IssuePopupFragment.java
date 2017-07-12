package com.fastaccess.ui.modules.repos.extras.popup;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.fastaccess.R;
import com.fastaccess.data.dao.LabelListModel;
import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.data.dao.MilestoneModel;
import com.fastaccess.data.dao.PullsIssuesParser;
import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.helper.AnimHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.markdown.MarkDownProvider;
import com.fastaccess.provider.scheme.LinkParserHelper;
import com.fastaccess.ui.base.BaseMvpBottomSheetDialogFragment;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontEditText;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.LabelSpan;
import com.fastaccess.ui.widgets.SpannableBuilder;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Kosh on 27 May 2017, 12:54 PM
 */

public class IssuePopupFragment extends BaseMvpBottomSheetDialogFragment<IssuePopupMvp.View, IssuePopupPresenter>
        implements IssuePopupMvp.View {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.appbar) AppBarLayout appbar;
    @BindView(R.id.avatarLayout) AvatarLayout avatarLayout;
    @BindView(R.id.name) FontTextView name;
    @BindView(R.id.body) FontTextView body;
    @BindView(R.id.assignee) FontTextView assignee;
    @BindView(R.id.assigneeLayout) LinearLayout assigneeLayout;
    @BindView(R.id.title) FontTextView title;
    @BindView(R.id.labels) FontTextView labels;
    @BindView(R.id.labelsLayout) LinearLayout labelsLayout;
    @BindView(R.id.milestoneTitle) FontTextView milestoneTitle;
    @BindView(R.id.milestoneDescription) FontTextView milestoneDescription;
    @BindView(R.id.milestoneLayout) LinearLayout milestoneLayout;
    @BindView(R.id.comment) FontEditText comment;
    @BindView(R.id.submit) FloatingActionButton submit;
    @BindView(R.id.commentSection) LinearLayout commentSection;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    public static void showPopup(@NonNull FragmentManager manager, @NonNull Issue issue) {
        IssuePopupFragment fragment = new IssuePopupFragment();
        PullsIssuesParser parser = PullsIssuesParser.getForIssue(issue.getHtmlUrl());
        if (parser == null) {
            parser = PullsIssuesParser.getForPullRequest(issue.getHtmlUrl());
        }
        if (parser == null) return;
        fragment.setArguments(getBundle(parser.getLogin(), parser.getRepoId(), issue.getNumber(), issue.getTitle(), issue.getBody(), issue.getUser(),
                issue.getAssignee(), issue.getLabels(), issue.getMilestone(), !issue.isLocked()));
        fragment.show(manager, "");
    }

    public static void showPopup(@NonNull FragmentManager manager, @NonNull PullRequest pullRequest) {
        IssuePopupFragment fragment = new IssuePopupFragment();
        PullsIssuesParser parser = PullsIssuesParser.getForPullRequest(pullRequest.getHtmlUrl());
        if (parser == null) return;
        fragment.setArguments(getBundle(parser.getLogin(), parser.getRepoId(), pullRequest.getNumber(),
                pullRequest.getTitle(), pullRequest.getBody(), pullRequest.getUser(),
                pullRequest.getAssignee(), pullRequest.getLabels(), pullRequest.getMilestone(), !pullRequest.isLocked()));
        fragment.show(manager, "");
    }

    @NonNull private static Bundle getBundle(@NonNull String login, @NonNull String repoId,
                                             int number, @NonNull String title, @NonNull String body,
                                             @NonNull User user, @Nullable User assignee,
                                             @Nullable LabelListModel labels, @Nullable MilestoneModel milestone,
                                             boolean canComment) {
        return Bundler.start()
                .put(BundleConstant.EXTRA_SEVEN, login)
                .put(BundleConstant.EXTRA_EIGHT, repoId)
                .put(BundleConstant.ID, number)
                .put(BundleConstant.EXTRA, title)
                .put(BundleConstant.EXTRA_TWO, body)
                .put(BundleConstant.EXTRA_THREE, user)
                .put(BundleConstant.EXTRA_FOUR, assignee)
                .putParcelableArrayList(BundleConstant.EXTRA_FIVE, labels)
                .put(BundleConstant.EXTRA_SIX, milestone)
                .put(BundleConstant.YES_NO_EXTRA, canComment)
                .end();
    }

    @OnClick(R.id.submit) void onSubmit() {
        boolean isEmpty = InputHelper.isEmpty(comment);
        if (!isEmpty) {
            //noinspection ConstantConditions
            getPresenter().onSubmit(getArguments().getString(BundleConstant.EXTRA_SEVEN), getArguments().getString(BundleConstant.EXTRA_EIGHT),
                    getArguments().getInt(BundleConstant.ID), InputHelper.toString(comment));
        } else {
            showMessage(R.string.error, R.string.required_field);
        }
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setNavigationIcon(R.drawable.ic_clear);
        toolbar.setNavigationOnClickListener(view1 -> dismiss());
        Bundle bundle = getArguments();
        String titleString = bundle.getString(BundleConstant.EXTRA);
        String bodyString = bundle.getString(BundleConstant.EXTRA_TWO);
        User user = bundle.getParcelable(BundleConstant.EXTRA_THREE);
        User assigneeModel = bundle.getParcelable(BundleConstant.EXTRA_FOUR);
        ArrayList<LabelModel> labelsList = bundle.getParcelableArrayList(BundleConstant.EXTRA_FIVE);
        MilestoneModel milestoneModel = bundle.getParcelable(BundleConstant.EXTRA_SIX);
        boolean canComment = bundle.getBoolean(BundleConstant.YES_NO_EXTRA);
        commentSection.setVisibility(canComment ? View.VISIBLE : View.GONE);
        toolbar.setTitle(String.format("#%s", bundle.getInt(BundleConstant.ID)));
        title.setText(titleString);
        MarkDownProvider.setMdText(body, bodyString);
        if (user != null) {
            name.setText(user.getLogin());
            avatarLayout.setUrl(user.getAvatarUrl(), user.getLogin(), false, LinkParserHelper.isEnterprise(user.getUrl()));
        }
        if (assigneeModel == null) {
            assigneeLayout.setVisibility(View.GONE);
        } else {
            assignee.setText(assigneeModel.getLogin());
        }
        if (labelsList == null || labelsList.isEmpty()) {
            labelsLayout.setVisibility(View.GONE);
        } else {
            SpannableBuilder builder = SpannableBuilder.builder();
            for (LabelModel label : labelsList) {
                int color = Color.parseColor("#" + label.getColor());
                builder.append(" ").append(" " + label.getName() + " ", new LabelSpan(color));
            }
            labels.setText(builder);
        }
        if (milestoneModel == null) {
            milestoneLayout.setVisibility(View.GONE);
        } else {
            milestoneTitle.setText(milestoneModel.getTitle());
            milestoneDescription.setText(milestoneModel.getDescription());
            if (milestoneModel.getDescription() == null) {
                milestoneDescription.setVisibility(View.GONE);
            }
        }
    }

    @Override protected int fragmentLayout() {
        return R.layout.issue_popup_layout;
    }

    @NonNull @Override public IssuePopupPresenter providePresenter() {
        return new IssuePopupPresenter();
    }

    @Override public void showMessage(int titleRes, int msgRes) {
        hideProgress();
        super.showMessage(titleRes, msgRes);
    }

    @Override public void showMessage(@NonNull String titleRes, @NonNull String msgRes) {
        hideProgress();
        super.showMessage(titleRes, msgRes);
    }

    @Override public void showErrorMessage(@NonNull String msgRes) {
        hideProgress();
        super.showErrorMessage(msgRes);
    }

    @Override public void showProgress(int resId) {
        submit.hide();
        AnimHelper.mimicFabVisibility(true, progressBar, null);
    }

    @Override public void hideProgress() {
        AnimHelper.mimicFabVisibility(false, progressBar, null);
        submit.show();
    }

    @Override public void onSuccessfullySubmitted() {
        showMessage(R.string.success, R.string.successfully_submitted);
        hideProgress();
        comment.setText("");
    }
}
