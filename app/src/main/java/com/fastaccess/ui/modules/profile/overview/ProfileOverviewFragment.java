package com.fastaccess.ui.modules.profile.overview;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;

import com.fastaccess.R;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.ui.adapter.ProfileOrgsAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.profile.ProfilePagerMvp;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.contributions.ContributionsDay;
import com.fastaccess.ui.widgets.contributions.GitHubContributionsView;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;
import com.fastaccess.ui.widgets.recyclerview.layout_manager.GridManager;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import icepick.State;

/**
 * Created by Kosh on 03 Dec 2016, 9:16 AM
 */

public class ProfileOverviewFragment extends BaseFragment<ProfileOverviewMvp.View, ProfileOverviewPresenter> implements ProfileOverviewMvp.View {

    @BindView(R.id.username) FontTextView username;
    @BindView(R.id.description) FontTextView description;
    @BindView(R.id.avatarLayout) AvatarLayout avatarLayout;
    @BindView(R.id.organization) FontTextView organization;
    @BindView(R.id.location) FontTextView location;
    @BindView(R.id.email) FontTextView email;
    @BindView(R.id.link) FontTextView link;
    @BindView(R.id.joined) FontTextView joined;
    @BindView(R.id.following) FontTextView following;
    @BindView(R.id.followers) FontTextView followers;
    @BindView(R.id.progress) View progress;
    @BindView(R.id.followBtn) Button followBtn;
    @State User userModel;
    @BindView(R.id.orgsList) DynamicRecyclerView orgsList;
    @BindView(R.id.orgsCard) CardView orgsCard;
    @BindView(R.id.parentView) NestedScrollView parentView;
    @BindView(R.id.contributionView) GitHubContributionsView contributionView;
    @BindView(R.id.contributionCard) CardView contributionCard;
    private ProfilePagerMvp.View profileCallback;

    public static ProfileOverviewFragment newInstance(@NonNull String login) {
        ProfileOverviewFragment view = new ProfileOverviewFragment();
        view.setArguments(Bundler.start().put(BundleConstant.EXTRA, login).end());
        return view;
    }

    @OnClick({R.id.following, R.id.followers, R.id.followBtn}) void onClick(View view) {
        if (view.getId() == R.id.followers) {
            profileCallback.onNavigateToFollowers();
        } else if (view.getId() == R.id.following) {
            profileCallback.onNavigateToFollowing();
        } else if (view.getId() == R.id.followBtn) {
            getPresenter().onFollowButtonClicked(getPresenter().getLogin());
            followBtn.setEnabled(false);
        }
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof ProfilePagerMvp.View) {
            profileCallback = (ProfilePagerMvp.View) getParentFragment();
        } else {
            profileCallback = (ProfilePagerMvp.View) context;
        }
    }

    @Override public void onDetach() {
        profileCallback = null;
        super.onDetach();
    }

    @Override protected int fragmentLayout() {
        return R.layout.profile_overview_layout;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        onInitOrgs(getPresenter().getOrgs());
        onInitContributions(getPresenter().getContributions());
        if (savedInstanceState == null) {
            getPresenter().onFragmentCreated(getArguments());
        } else {
            if (userModel != null) {
                onInvalidateMenuItem();
                onInitViews(userModel);
            } else {
                getPresenter().onFragmentCreated(getArguments());
            }
        }
        if (isMeOrOrganization()) {
            followBtn.setVisibility(View.GONE);
        }
    }

    @NonNull @Override public ProfileOverviewPresenter providePresenter() {
        return new ProfileOverviewPresenter();
    }

    @Override public void onInitViews(@Nullable User userModel) {
        progress.setVisibility(View.GONE);
        if (userModel == null) return;
        this.userModel = userModel;
        followBtn.setVisibility(!isMeOrOrganization() ? View.VISIBLE : View.GONE);
        username.setText(userModel.getLogin());
        description.setText(userModel.getBio());
        avatarLayout.setUrl(userModel.getAvatarUrl(), null);
        organization.setText(InputHelper.toNA(userModel.getCompany()));
        location.setText(InputHelper.toNA(userModel.getLocation()));
        email.setText(InputHelper.toNA(userModel.getEmail()));
        link.setText(InputHelper.toNA(userModel.getBlog()));
        joined.setText(userModel.getCreatedAt() != null ? ParseDateFormat.getTimeAgo(userModel.getCreatedAt()) : "N/A");
        followers.setText(SpannableBuilder.builder()
                .append(getString(R.string.followers))
                .append("\n")
                .bold(String.valueOf(userModel.getFollowers())));
        following.setText(SpannableBuilder.builder()
                .append(getString(R.string.following))
                .append("\n")
                .bold(String.valueOf(userModel.getFollowing())));
    }

    @Override public void onInvalidateMenuItem() {
        hideProgress();
        if (isMeOrOrganization()) return;
        if (getPresenter().isSuccessResponse()) {
            followBtn.setEnabled(true);
            followBtn.setActivated(getPresenter().isFollowing());
            followBtn.setText(getPresenter().isFollowing() ? getString(R.string.unfollow) : getString(R.string.follow));
        }
    }

    @Override public void onInitContributions(@Nullable List<ContributionsDay> items) {
        if (items != null && !items.isEmpty()) {
            contributionView.onResponse(items);
            contributionCard.setVisibility(View.VISIBLE);
        } else {
            contributionCard.setVisibility(View.GONE);
        }
    }

    @Override public void onInitOrgs(@Nullable List<User> orgs) {
        if (orgs != null && !orgs.isEmpty()) {
            orgsList.setNestedScrollingEnabled(false);
            ProfileOrgsAdapter adapter = new ProfileOrgsAdapter();
            adapter.addItems(orgs);
            orgsList.setAdapter(adapter);
            orgsCard.setVisibility(View.VISIBLE);
            ((GridManager) orgsList.getLayoutManager()).setIconSize(getResources().getDimensionPixelSize(R.dimen.header_icon_zie) +
                    getResources().getDimensionPixelSize(R.dimen.spacing_xs_large));
        } else {
            orgsCard.setVisibility(View.GONE);
        }
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

    private boolean isMeOrOrganization() {
        return Login.getUser() != null && Login.getUser().getLogin().equalsIgnoreCase(getPresenter().getLogin()) ||
                (userModel != null && userModel.getType() != null && !userModel.getType().equalsIgnoreCase("user"));
    }
}
