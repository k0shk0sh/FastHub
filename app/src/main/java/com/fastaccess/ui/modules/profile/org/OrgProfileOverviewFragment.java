package com.fastaccess.ui.modules.profile.org;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.transition.TransitionManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.evernote.android.state.State;
import com.fastaccess.R;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.provider.emoji.EmojiParser;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.profile.org.project.OrgProjectActivity;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;

import butterknife.BindView;
import butterknife.OnClick;

import static android.view.View.GONE;

/**
 * Created by Kosh on 04 Apr 2017, 10:47 AM
 */

public class OrgProfileOverviewFragment extends BaseFragment<OrgProfileOverviewMvp.View, OrgProfileOverviewPresenter>
        implements OrgProfileOverviewMvp.View {

    @BindView(R.id.avatarLayout) AvatarLayout avatarLayout;
    @BindView(R.id.username) FontTextView username;
    @BindView(R.id.description) FontTextView description;
    @BindView(R.id.location) FontTextView location;
    @BindView(R.id.email) FontTextView email;
    @BindView(R.id.link) FontTextView link;
    @BindView(R.id.joined) FontTextView joined;
    @BindView(R.id.progress) LinearLayout progress;
    @BindView(R.id.projects) View projects;

    @State User userModel;

    public static OrgProfileOverviewFragment newInstance(@NonNull String login) {
        OrgProfileOverviewFragment view = new OrgProfileOverviewFragment();
        view.setArguments(Bundler.start().put(BundleConstant.EXTRA, login).end());
        return view;
    }

    @OnClick(R.id.userInformation) void onOpenAvatar() {
        if (userModel != null) ActivityHelper.startCustomTab(getActivity(), userModel.getAvatarUrl());
    }

    @OnClick(R.id.projects) void onOpenProjects() {
        OrgProjectActivity.Companion.startActivity(getContext(), getPresenter().getLogin(), isEnterprise());
    }

    @SuppressLint("ClickableViewAccessibility") @Override public void onInitViews(@Nullable User userModel) {
        if (getView() != null) {
            TransitionManager.beginDelayedTransition((ViewGroup) getView());
        }
        if (this.userModel != null) return;
        progress.setVisibility(View.GONE);
        if (userModel == null) return;
        this.userModel = userModel;
        username.setText(InputHelper.isEmpty(userModel.getName()) ? userModel.getLogin() : userModel.getName());
        if (userModel.getDescription() != null) {
            description.setText(EmojiParser.parseToUnicode(userModel.getDescription()));
            description.setVisibility(View.VISIBLE);
        } else {
            description.setVisibility(GONE);
        }
        avatarLayout.setUrl(userModel.getAvatarUrl(), null, false, false);
        avatarLayout.findViewById(R.id.avatar).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                ActivityHelper.startCustomTab(getActivity(), userModel.getAvatarUrl());
                return true;
            }
            return false;
        });
        location.setText(userModel.getLocation());
        email.setText(userModel.getEmail());
        link.setText(userModel.getBlog());
        joined.setText(ParseDateFormat.getTimeAgo(userModel.getCreatedAt()));

        if (!InputHelper.isEmpty(userModel.getLocation())) {
            location.setVisibility(View.VISIBLE);
        }
        if (!InputHelper.isEmpty(userModel.getEmail())) {
            email.setVisibility(View.VISIBLE);
        }
        if (!InputHelper.isEmpty(userModel.getBlog())) {
            link.setVisibility(View.VISIBLE);
        }
        if (!InputHelper.isEmpty(userModel.getCreatedAt())) {
            joined.setVisibility(View.VISIBLE);
        }
        if (!InputHelper.isEmpty(userModel.getEmail())) {
            email.setVisibility(View.VISIBLE);
        }
        projects.setVisibility(userModel.isHasOrganizationProjects() ? View.VISIBLE : View.GONE);
    }

    @Override protected int fragmentLayout() {
        return R.layout.org_profile_overview_layout;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getPresenter().onFragmentCreated(getArguments());
        } else {
            if (userModel != null) {
                onInitViews(userModel);
            } else {
                getPresenter().onFragmentCreated(getArguments());
            }
        }
    }

    @NonNull @Override public OrgProfileOverviewPresenter providePresenter() {
        return new OrgProfileOverviewPresenter();
    }

    @Override public void showProgress(@StringRes int resId) {
        progress.setVisibility(View.VISIBLE);
    }

    @Override public void hideProgress() {
        progress.setVisibility(View.GONE);
    }

    @Override public void showErrorMessage(@NonNull String message) {
        onHideProgress();
        super.showErrorMessage(message);
    }

    @Override public void showMessage(int titleRes, int msgRes) {
        onHideProgress();
        super.showMessage(titleRes, msgRes);
    }

    private void onHideProgress() {
        hideProgress();
    }

}
