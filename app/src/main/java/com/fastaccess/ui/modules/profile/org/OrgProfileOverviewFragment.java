package com.fastaccess.ui.modules.profile.org;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.fastaccess.R;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.provider.emoji.EmojiParser;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;

import butterknife.BindView;
import butterknife.OnClick;
import com.evernote.android.state.State;

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

    @State User userModel;

    public static OrgProfileOverviewFragment newInstance(@NonNull String login) {
        OrgProfileOverviewFragment view = new OrgProfileOverviewFragment();
        view.setArguments(Bundler.start().put(BundleConstant.EXTRA, login).end());
        return view;
    }

    @OnClick(R.id.userInformation) void onOpenAvatar() {
        if (userModel != null) ActivityHelper.startCustomTab(getActivity(), userModel.getAvatarUrl());
    }

    @Override public void onInitViews(@Nullable User userModel) {
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
        avatarLayout.setUrl(userModel.getAvatarUrl(), null);
        location.setText(InputHelper.toNA(userModel.getLocation()));
        email.setText(InputHelper.toNA(userModel.getEmail()));
        link.setText(InputHelper.toNA(userModel.getBlog()));
        joined.setText(userModel.getCreatedAt() != null ? ParseDateFormat.getTimeAgo(userModel.getCreatedAt()) : "N/A");
        ViewGroup parent = (ViewGroup) location.getParent();
        if (location.getText().equals("N/A")) {
            int i = parent.indexOfChild(location);
            ((ViewGroup) location.getParent()).removeViewAt(i + 1);
            location.setVisibility(GONE);
        }
        if (email.getText().equals("N/A")) {
            int i = parent.indexOfChild(email);
            ((ViewGroup) email.getParent()).removeViewAt(i + 1);
            email.setVisibility(GONE);
        }
        if (link.getText().equals("N/A")) {
            int i = parent.indexOfChild(link);
            ((ViewGroup) link.getParent()).removeViewAt(i + 1);
            link.setVisibility(GONE);
        }
        if (joined.getText().equals("N/A")) {
            int i = parent.indexOfChild(joined);
            ((ViewGroup) joined.getParent()).removeViewAt(i + 1);
            joined.setVisibility(GONE);
        }
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
