package com.fastaccess.ui.modules.filter.issues;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.evernote.android.state.State;
import com.fastaccess.App;
import com.fastaccess.R;
import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.data.dao.MilestoneModel;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.AnimHelper;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.provider.timeline.CommentsHelper;
import com.fastaccess.ui.adapter.LabelsAdapter;
import com.fastaccess.ui.adapter.MilestonesAdapter;
import com.fastaccess.ui.adapter.SimpleListAdapter;
import com.fastaccess.ui.adapter.UsersAdapter;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.modules.filter.issues.fragment.FilterIssueFragment;
import com.fastaccess.ui.widgets.FontEditText;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.ForegroundImageView;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnTextChanged;
import es.dmoral.toasty.Toasty;

/**
 * Created by Kosh on 09 Apr 2017, 6:23 PM
 */

public class FilterIssuesActivity extends BaseActivity<FilterIssuesActivityMvp.View, FilterIssuesActivityPresenter> implements
        FilterIssuesActivityMvp.View {
    @BindView(R.id.back) ForegroundImageView back;
    @BindView(R.id.open) FontTextView open;
    @BindView(R.id.close) FontTextView close;
    @BindView(R.id.author) FontTextView author;
    @BindView(R.id.labels) FontTextView labels;
    @BindView(R.id.milestone) FontTextView milestone;
    @BindView(R.id.assignee) FontTextView assignee;
    @BindView(R.id.sort) FontTextView sort;
    @BindView(R.id.searchEditText) FontEditText searchEditText;
    @BindView(R.id.clear) View clear;
    @State boolean isIssue;
    @State boolean isOpen;
    @State String login;
    @State String repoId;
    @State String criteria;

    private FilterIssueFragment filterFragment;
    private MilestonesAdapter milestonesAdapter;
    private LabelsAdapter labelsAdapter;
    private UsersAdapter assigneesAdapter;
    private PopupWindow popupWindow;


    public static Intent getIntent(@NonNull Context context, @NonNull String login, @NonNull String repoId, @NonNull String criteria) {
        Intent intent = new Intent(context, FilterIssuesActivity.class);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA_FOUR, criteria)
                .put(BundleConstant.EXTRA_TWO, true)
                .put(BundleConstant.EXTRA_THREE, true)
                .end());
        return intent;
    }

    public static void startActivity(@NonNull Activity context, @NonNull String login, @NonNull String repoId,
                                     boolean isIssue, boolean isOpen, boolean isEnterprise) {
        Intent intent = new Intent(context, FilterIssuesActivity.class);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA_TWO, isIssue)
                .put(BundleConstant.EXTRA_THREE, isOpen)
                .put(BundleConstant.IS_ENTERPRISE, isEnterprise)
                .end());
        View view = context.findViewById(R.id.fab);
        if (view != null) {
            ActivityHelper.startReveal(context, intent, view);
        } else {
            context.startActivity(intent);
        }
    }

    public static void startActivity(@NonNull View view, @NonNull String login, @NonNull String repoId,
                                     boolean isIssue, boolean isOpen, boolean isEnterprise, @NonNull String criteria) {
        Intent intent = new Intent(view.getContext(), FilterIssuesActivity.class);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA_TWO, isIssue)
                .put(BundleConstant.EXTRA_THREE, isOpen)
                .put(BundleConstant.IS_ENTERPRISE, isEnterprise)
                .put(BundleConstant.EXTRA_FOUR, criteria)
                .end());
        //noinspection ConstantConditions
        ActivityHelper.startReveal(ActivityHelper.getActivity(view.getContext()), intent, view);
    }

    @Override protected int layout() {
        return R.layout.filter_issues_prs_layout;
    }

    @Override protected boolean isTransparent() {
        return true;
    }

    @Override protected boolean canBack() {
        return true;
    }

    @Override protected boolean isSecured() {
        return false;
    }

    @NonNull @Override public FilterIssuesActivityPresenter providePresenter() {
        return new FilterIssuesActivityPresenter();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Bundle bundle = getIntent().getExtras();
            isIssue = bundle.getBoolean(BundleConstant.EXTRA_TWO);
            isOpen = bundle.getBoolean(BundleConstant.EXTRA_THREE);
            repoId = bundle.getString(BundleConstant.ID);
            login = bundle.getString(BundleConstant.EXTRA);
            criteria = bundle.getString(BundleConstant.EXTRA_FOUR);
            getPresenter().onStart(login, repoId);
            if (isOpen) {
                onOpenClicked();
            } else {
                onCloseClicked();
            }
        }
    }

    @OnClick(R.id.back) public void onBackClicked() {
        onBackPressed();
    }

    @OnClick(R.id.open) public void onOpenClicked() {
        if (!open.isSelected()) {
            open.setSelected(true);
            close.setSelected(false);
            String text = InputHelper.toString(searchEditText);
            if (!InputHelper.isEmpty(text)) {
                text = text.replace("is:closed", "is:open");
                searchEditText.setText(text);
                onSearch();
            } else {
                searchEditText.setText(String.format("%s %s ", isOpen ? "is:open" : "is:closed", isIssue ? "is:issue" : "is:pr"));
                if (!InputHelper.isEmpty(criteria)) {
                    searchEditText.setText(String.format("%s%s", InputHelper.toString(searchEditText), criteria));
                    criteria = null;
                }
                onSearch();
            }
        }
    }

    @OnClick(R.id.close) public void onCloseClicked() {
        if (!close.isSelected()) {
            open.setSelected(false);
            close.setSelected(true);
            String text = InputHelper.toString(searchEditText);
            if (!InputHelper.isEmpty(text)) {
                text = text.replace("is:open", "is:closed");
                searchEditText.setText(text);
                onSearch();
            } else {
                searchEditText.setText(String.format("%s %s ", isOpen ? "is:open" : "is:closed", isIssue ? "is:issue" : "is:pr"));
                onSearch();
            }
        }
    }

    @OnClick(R.id.author) public void onAuthorClicked() {
        Toasty.info(App.getInstance(), "GitHub doesn't have this API yet!\nYou can try typing it yourself for example author:k0shk0sh",
                Toast.LENGTH_LONG).show();
    }

    @SuppressLint("InflateParams") @OnClick(R.id.labels) public void onLabelsClicked() {
        if (labels.getTag() != null) return;
        labels.setTag(true);
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(this).inflate(R.layout.simple_list_dialog, null));
        setupPopupWindow(viewHolder);
        viewHolder.recycler.setAdapter(getLabelsAdapter());
        AnimHelper.revealPopupWindow(popupWindow, labels);
    }

    @SuppressLint("InflateParams") @OnClick(R.id.milestone) public void onMilestoneClicked() {
        if (milestone.getTag() != null) return;
        milestone.setTag(true);
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(this).inflate(R.layout.simple_list_dialog, null));
        setupPopupWindow(viewHolder);
        viewHolder.recycler.setAdapter(getMilestonesAdapter());
        AnimHelper.revealPopupWindow(popupWindow, milestone);
    }

    @SuppressLint("InflateParams") @OnClick(R.id.assignee) public void onAssigneeClicked() {
        if (assignee.getTag() != null) return;
        assignee.setTag(true);
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(this).inflate(R.layout.simple_list_dialog, null));
        setupPopupWindow(viewHolder);
        viewHolder.recycler.setAdapter(getAssigneesAdapter());
        AnimHelper.revealPopupWindow(popupWindow, assignee);
    }

    @SuppressLint("InflateParams") @OnClick(R.id.sort) public void onSortClicked() {
        if (sort.getTag() != null) return;
        sort.setTag(true);
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(this).inflate(R.layout.simple_list_dialog, null));
        setupPopupWindow(viewHolder);
        ArrayList<String> lists = new ArrayList<>();
        Collections.addAll(lists, getResources().getStringArray(R.array.sort_prs_issues));
        lists.add(CommentsHelper.getThumbsUp());
        lists.add(CommentsHelper.getThumbsDown());
        lists.add(CommentsHelper.getLaugh());
        lists.add(CommentsHelper.getHooray());
        lists.add(CommentsHelper.getSad());
        lists.add(CommentsHelper.getHeart());
        viewHolder.recycler.setAdapter(new SimpleListAdapter<>(lists, new BaseViewHolder.OnItemClickListener<String>() {
            @Override public void onItemClick(int position, View v, String item) {
                appendSort(item);
            }

            @Override public void onItemLongClick(int position, View v, String item) {}
        }));
        AnimHelper.revealPopupWindow(popupWindow, sort);
    }

    @OnClick(value = {R.id.clear}) void onClear(View view) {
        if (view.getId() == R.id.clear) {
            AppHelper.hideKeyboard(searchEditText);
            searchEditText.setText("");
        }
    }

    @OnClick(R.id.search) void onSearchClicked() {
        onSearch();
    }

    @OnTextChanged(value = R.id.searchEditText, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED) void onTextChange(Editable s) {
        String text = s.toString();
        if (text.length() == 0) {
            AnimHelper.animateVisibility(clear, false);
        } else {
            AnimHelper.animateVisibility(clear, true);
        }
    }

    @OnEditorAction(R.id.searchEditText) protected boolean onEditor() {
        onSearchClicked();
        return true;
    }

    @Override public void onSetCount(int count, boolean isOpen) {
        if (isOpen) {
            open.setText(SpannableBuilder.builder()
                    .append(getString(R.string.open))
                    .append("(")
                    .append(String.valueOf(count))
                    .append(")"));
            close.setText(R.string.closed);
        } else {
            close.setText(SpannableBuilder.builder()
                    .append(getString(R.string.closed))
                    .append("(")
                    .append(String.valueOf(count))
                    .append(")"));
            open.setText(R.string.open);
        }
    }

    @Override public void showProgress(int resId) {
        super.showProgress(resId);
    }

    @Override public void hideProgress() {
        super.hideProgress();
    }

    @NonNull private String getRepoName() {
        return "repo:" + login + "/" + repoId + " ";
    }// let users stay within selected repo context.

    @Override public void onBackPressed() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            super.onBackPressed();
        }
    }

    private void setupPopupWindow(@NonNull ViewHolder viewHolder) {
        if (popupWindow == null) {
            popupWindow = new PopupWindow(this);
            popupWindow.setElevation(getResources().getDimension(R.dimen.spacing_micro));
            popupWindow.setOutsideTouchable(true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(ViewHelper.getWindowBackground(this)));
            popupWindow.setElevation(getResources().getDimension(R.dimen.spacing_normal));
            popupWindow.setOnDismissListener(() -> new Handler().postDelayed(() -> {
                //hacky way to dismiss on re-selecting tab.
                if (assignee == null || milestone == null || sort == null || labels == null) return;
                assignee.setTag(null);
                milestone.setTag(null);
                sort.setTag(null);
                labels.setTag(null);
            }, 100));
        }
        popupWindow.setContentView(viewHolder.view);
    }

    private void onSearch() {
        if (!InputHelper.isEmpty(searchEditText)) {
            getFilterFragment().onSearch(getRepoName() + InputHelper.toString(searchEditText),
                    open.isSelected(), isIssue, isEnterprise());
            searchEditText.setSelection(searchEditText.getEditableText().length());
        } else {
            getFilterFragment().onClear();
            showErrorMessage(getString(R.string.empty_search_error));
        }
    }

    private FilterIssueFragment getFilterFragment() {
        if (filterFragment == null) {
            filterFragment = (FilterIssueFragment) getSupportFragmentManager().findFragmentById(R.id.filterFragment);
        }
        return filterFragment;
    }

    private MilestonesAdapter getMilestonesAdapter() {
        if (milestonesAdapter == null) {
            if (!getPresenter().getMilestones().isEmpty()) {
                MilestoneModel milestone = new MilestoneModel();
                milestone.setTitle(getString(R.string.clear));
                getPresenter().getMilestones().add(0, milestone);
            }
            milestonesAdapter = new MilestonesAdapter(getPresenter().getMilestones());
            milestonesAdapter.setListener(new BaseViewHolder.OnItemClickListener<MilestoneModel>() {
                @Override public void onItemClick(int position, View v, MilestoneModel item) {
                    appendMilestone(item);
                }

                @Override public void onItemLongClick(int position, View v, MilestoneModel item) {

                }
            });
        }
        return milestonesAdapter;
    }

    private LabelsAdapter getLabelsAdapter() {
        if (labelsAdapter == null) {
            if (!getPresenter().getLabels().isEmpty()) {
                LabelModel label = new LabelModel();
                label.setName(getString(R.string.clear));
                getPresenter().getLabels().add(0, label);
            }
            labelsAdapter = new LabelsAdapter(getPresenter().getLabels(), null);
            labelsAdapter.setListener(new BaseViewHolder.OnItemClickListener<LabelModel>() {
                @Override public void onItemClick(int position, View v, LabelModel item) {
                    appendLabel(item);
                }

                @Override public void onItemLongClick(int position, View v, LabelModel item) {

                }
            });
        }
        return labelsAdapter;
    }

    private UsersAdapter getAssigneesAdapter() {
        if (assigneesAdapter == null) {
            if (!getPresenter().getAssignees().isEmpty()) {
                User user = new User();
                user.setLogin(getString(R.string.clear));
                getPresenter().getAssignees().add(0, user);
            }
            assigneesAdapter = new UsersAdapter(getPresenter().getAssignees(), false, true);
            assigneesAdapter.setListener(new BaseViewHolder.OnItemClickListener<User>() {
                @Override public void onItemClick(int position, View v, User item) {
                    appendAssignee(item);
                }

                @Override public void onItemLongClick(int position, View v, User item) {}
            });
        }
        return assigneesAdapter;
    }

    private void appendIfEmpty() {
        if (InputHelper.isEmpty(searchEditText))
            if (open.isSelected()) {
                searchEditText.setText(isIssue ? "is:issue is:open " : "is:pr is:open ");
            } else if (close.isSelected()) {
                searchEditText.setText(isIssue ? "is:issue is:close " : "is:pr is:close ");
            } else {
                searchEditText.setText(isIssue ? "is:issue is:open " : "is:pr is:open ");
            }
    }

    private void appendMilestone(MilestoneModel item) {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
        appendIfEmpty();
        String text = InputHelper.toString(searchEditText);
        String regex = "milestone:(\".+\"|\\S+)";
        if (item.getTitle().equalsIgnoreCase(getString(R.string.clear))) {
            text = text.replaceAll(regex, "");
            searchEditText.setText(text);
            onSearch();
            return;
        }
        if (!text.replaceAll(regex, "milestone:\"" + item.getTitle() + "\"").equalsIgnoreCase(text)) {
            String space = text.endsWith(" ") ? "" : " ";
            text = text.replaceAll(regex, space + "milestone:\"" + item.getTitle() + "\"");
        } else {
            text += text.endsWith(" ") ? "" : " ";
            text += "milestone:\"" + item.getTitle() + "\"";
        }
        searchEditText.setText(text);
        onSearch();
    }

    private void appendLabel(LabelModel item) {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
        appendIfEmpty();
        String text = InputHelper.toString(searchEditText);
        String regex = "label:(\".+\"|\\S+)";
        if (item.getName().equalsIgnoreCase(getString(R.string.clear))) {
            text = text.replaceAll(regex, "");
            searchEditText.setText(text);
            onSearch();
            return;
        }
        if (!text.replaceAll(regex, "label:\"" + item.getName() + "\"").equalsIgnoreCase(text)) {
            String space = text.endsWith(" ") ? "" : " ";
            text = text.replaceAll(regex, space + "label:\"" + item.getName() + "\"");
        } else {
            text += text.endsWith(" ") ? "" : " ";
            text += "label:\"" + item.getName() + "\"";
        }
        searchEditText.setText(text);
        onSearch();
    }

    private void appendAssignee(User item) {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
        appendIfEmpty();
        String text = InputHelper.toString(searchEditText);
        String regex = "assignee:(\".+\"|\\S+)";
        if (item.getLogin().equalsIgnoreCase(getString(R.string.clear))) {
            text = text.replaceAll(regex, "");
            searchEditText.setText(text);
            onSearch();
            return;
        }
        if (!text.replaceAll(regex, "assignee:\"" + item.getLogin() + "\"").equalsIgnoreCase(text)) {
            String space = text.endsWith(" ") ? "" : " ";
            text = text.replaceAll(regex, space + "assignee:\"" + item.getLogin() + "\"");
        } else {
            text += text.endsWith(" ") ? "" : " ";
            text += "assignee:\"" + item.getLogin() + "\"";
        }
        searchEditText.setText(text);
        onSearch();
    }

    private void appendSort(String item) {
        dismissPopup();
        appendIfEmpty();
        Resources resources = getResources();
        String regex = "sort:(\".+\"|\\S+)";
        String oldestQuery = "created-asc";
        String mostCommentedQuery = "comments-desc";
        String leastCommentedQuery = "comments-asc";
        String recentlyUpdatedQuery = "updated-desc";
        String leastRecentUpdatedQuery = "updated-asc";
        String sortThumbUp = "reactions-%2B1-desc";
        String sortThumbDown = "reactions--1-desc";
        String sortThumbLaugh = "reactions-smile-desc";
        String sortThumbHooray = "reactions-tada-desc";
        String sortThumbConfused = "reactions-thinking_face-desc";
        String sortThumbHeart = "reactions-heart-desc";
        String toQuery = "";
        String text = InputHelper.toString(searchEditText);
        if (item.equalsIgnoreCase(resources.getString(R.string.newest))) {
            text = text.replaceAll(regex, "");
            if (!InputHelper.toString(searchEditText).equalsIgnoreCase(text)) {
                searchEditText.setText(text);
                onSearch();
            }
            return;
        }
        if (item.equalsIgnoreCase(resources.getString(R.string.oldest))) {
            toQuery = oldestQuery;
        } else if (item.equalsIgnoreCase(resources.getString(R.string.most_commented))) {
            toQuery = mostCommentedQuery;
        } else if (item.equalsIgnoreCase(resources.getString(R.string.least_commented))) {
            toQuery = leastCommentedQuery;
        } else if (item.equalsIgnoreCase(resources.getString(R.string.recently_updated))) {
            toQuery = recentlyUpdatedQuery;
        } else if (item.equalsIgnoreCase(resources.getString(R.string.least_recent_updated))) {
            toQuery = leastRecentUpdatedQuery;
        } else if (item.equalsIgnoreCase(CommentsHelper.getThumbsUp())) {
            toQuery = sortThumbUp;
        } else if (item.equalsIgnoreCase(CommentsHelper.getThumbsDown())) {
            toQuery = sortThumbDown;
        } else if (item.equalsIgnoreCase(CommentsHelper.getLaugh())) {
            toQuery = sortThumbLaugh;
        } else if (item.equalsIgnoreCase(CommentsHelper.getHooray())) {
            toQuery = sortThumbHooray;
        } else if (item.equalsIgnoreCase(CommentsHelper.getSad())) {
            toQuery = sortThumbConfused;
        } else if (item.equalsIgnoreCase(CommentsHelper.getHeart())) {
            toQuery = sortThumbHeart;
        }
        if (!text.replaceAll(regex, "sort:\"" + toQuery + "\"").equalsIgnoreCase(text)) {
            String space = text.endsWith(" ") ? "" : " ";
            text = text.replaceAll(regex, space + "sort:\"" + toQuery + "\"");
        } else {
            text += text.endsWith(" ") ? "" : " ";
            text += "sort:\"" + toQuery + "\"";
        }
        if (!InputHelper.toString(searchEditText).equalsIgnoreCase(text)) {
            searchEditText.setText(text);
            onSearch();
        }
    }

    private void dismissPopup() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    static class ViewHolder {
        @BindView(R.id.title) FontTextView title;
        @BindView(R.id.recycler) DynamicRecyclerView recycler;
        View view;

        ViewHolder(View view) {
            this.view = view;
            ButterKnife.bind(this, view);
            title.setVisibility(View.GONE);
        }
    }
}
