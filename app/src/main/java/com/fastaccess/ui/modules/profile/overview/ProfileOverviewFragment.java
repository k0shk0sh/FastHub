package com.fastaccess.ui.modules.profile.overview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.transition.AutoTransition;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.evernote.android.state.State;
import com.fastaccess.R;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.provider.emoji.EmojiParser;
import com.fastaccess.provider.scheme.SchemeParser;
import com.fastaccess.ui.adapter.ProfileOrgsAdapter;
import com.fastaccess.ui.adapter.ProfilePinnedReposAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.profile.ProfilePagerMvp;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.contributions.GitHubContributionsView;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;
import com.fastaccess.ui.widgets.recyclerview.layout_manager.GridManager;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import pr.GetPinnedReposQuery;

import static android.view.Gravity.TOP;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by Kosh on 03 Dec 2016, 9:16 AM
 */

public class ProfileOverviewFragment extends BaseFragment<ProfileOverviewMvp.View, ProfileOverviewPresenter> implements ProfileOverviewMvp.View {

    @BindView(R.id.contributionsCaption) FontTextView contributionsCaption;
    @BindView(R.id.organizationsCaption) FontTextView organizationsCaption;
    @BindView(R.id.headerImage) RelativeLayout headerImage;
    @BindView(R.id.userInformation) LinearLayout userInformation;
    @BindView(R.id.chooseBanner) Button chooseBanner;
    @BindView(R.id.banner_edit) ImageButton chooseBanner_pencil;
    @BindView(R.id.username) FontTextView username;
    @BindView(R.id.fullname) FontTextView fullname;
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
    @BindView(R.id.orgsList) DynamicRecyclerView orgsList;
    @BindView(R.id.orgsCard) CardView orgsCard;
    @BindView(R.id.parentView) NestedScrollView parentView;
    @BindView(R.id.contributionView) GitHubContributionsView contributionView;
    @BindView(R.id.contributionCard) CardView contributionCard;
    @BindView(R.id.pinnedReposTextView) FontTextView pinnedReposTextView;
    @BindView(R.id.pinnedList) DynamicRecyclerView pinnedList;
    @BindView(R.id.pinnedReposCard) CardView pinnedReposCard;
    @State User userModel;
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

    @OnClick(R.id.userInformation) void onOpenAvatar() {
        if (userModel != null) ActivityHelper.startCustomTab(getActivity(), userModel.getAvatarUrl());
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
        onInitPinnedRepos(getPresenter().getNodes());
        if (savedInstanceState == null) {
            getPresenter().onFragmentCreated(getArguments());
        } else {
            if (userModel != null) {
                invalidateFollowBtn();
                onInitViews(userModel);
            } else {
                getPresenter().onFragmentCreated(getArguments());
            }
        }
        if (isMeOrOrganization()) {
            followBtn.setVisibility(GONE);
        }
    }

    @NonNull @Override public ProfileOverviewPresenter providePresenter() {
        return new ProfileOverviewPresenter();
    }

    @SuppressLint("ClickableViewAccessibility") @Override public void onInitViews(@Nullable User userModel) {
        progress.setVisibility(GONE);
        if (userModel == null) return;
        if (profileCallback != null) profileCallback.onCheckType(userModel.isOrganizationType());
        if (getView() != null) {
            if (this.userModel == null) {
                TransitionManager.beginDelayedTransition((ViewGroup) getView(),
                        new AutoTransition().addListener(new Transition.TransitionListener() {

                            @Override public void onTransitionStart(@NonNull Transition transition) {

                            }

                            @Override public void onTransitionEnd(@NonNull Transition transition) {
                                if (contributionView != null) getPresenter().onLoadContributionWidget(contributionView);
                            }

                            @Override public void onTransitionCancel(@NonNull Transition transition) {

                            }

                            @Override public void onTransitionPause(@NonNull Transition transition) {

                            }

                            @Override public void onTransitionResume(@NonNull Transition transition) {

                            }
                        }));
            } else {
                getPresenter().onLoadContributionWidget(contributionView);
            }
        }
        this.userModel = userModel;
        followBtn.setVisibility(!isMeOrOrganization() ? VISIBLE : GONE);
        username.setText(userModel.getLogin());
        fullname.setText(userModel.getName());
        if (userModel.getBio() != null) {
            description.setText(EmojiParser.parseToUnicode(userModel.getBio()));
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
        organization.setText(userModel.getCompany());
        location.setText(userModel.getLocation());
        email.setText(userModel.getEmail());
        link.setText(userModel.getBlog());
        joined.setText(ParseDateFormat.getTimeAgo(userModel.getCreatedAt()));
        if (InputHelper.isEmpty(userModel.getCompany())) {
            organization.setVisibility(GONE);
        }
        if (InputHelper.isEmpty(userModel.getLocation())) {
            location.setVisibility(GONE);
        }
        if (InputHelper.isEmpty(userModel.getEmail())) {
            email.setVisibility(GONE);
        }
        if (InputHelper.isEmpty(userModel.getBlog())) {
            link.setVisibility(GONE);
        }
        if (InputHelper.isEmpty(userModel.getCreatedAt())) {
            joined.setVisibility(GONE);
        }
        followers.setText(SpannableBuilder.builder()
                .append(getString(R.string.followers))
                .append("\n")
                .bold(String.valueOf(userModel.getFollowers())));
        following.setText(SpannableBuilder.builder()
                .append(getString(R.string.following))
                .append("\n")
                .bold(String.valueOf(userModel.getFollowing())));
    }

    @Override public void invalidateFollowBtn() {
        hideProgress();
        if (isMeOrOrganization()) return;
        if (getPresenter().isSuccessResponse()) {
            followBtn.setEnabled(true);
            followBtn.setActivated(getPresenter().isFollowing());
            followBtn.setText(getPresenter().isFollowing() ? getString(R.string.unfollow) : getString(R.string.follow));
        }
    }

    @Override public void onInitContributions(boolean show) {
        if (contributionView == null) return;
        if (show) {
            contributionView.onResponse();
        }
        contributionCard.setVisibility(show ? VISIBLE : GONE);
        contributionsCaption.setVisibility(show ? VISIBLE : GONE);
    }

    @Override public void onInitOrgs(@Nullable List<User> orgs) {
        if (orgs != null && !orgs.isEmpty()) {
            orgsList.setNestedScrollingEnabled(false);
            ProfileOrgsAdapter adapter = new ProfileOrgsAdapter();
            adapter.addItems(orgs);
            orgsList.setAdapter(adapter);
            orgsCard.setVisibility(VISIBLE);
            organizationsCaption.setVisibility(VISIBLE);
            ((GridManager) orgsList.getLayoutManager()).setIconSize(getResources().getDimensionPixelSize(R.dimen.header_icon_zie) + getResources()
                    .getDimensionPixelSize(R.dimen.spacing_xs_large));
        } else {
            organizationsCaption.setVisibility(GONE);
            orgsCard.setVisibility(GONE);
        }
    }

    @Override public void onUserNotFound() {
        showMessage(R.string.error, R.string.no_user_found);
    }

    @Override public void onImagePosted(@Nullable String link) {
        hideProgress();
    }

    @Override public void onInitPinnedRepos(@NonNull List<GetPinnedReposQuery.Node> nodes) {
        if (!nodes.isEmpty()) {
            pinnedReposTextView.setVisibility(VISIBLE);
            pinnedReposCard.setVisibility(VISIBLE);
            ProfilePinnedReposAdapter adapter = new ProfilePinnedReposAdapter(nodes);
            adapter.setListener(new BaseViewHolder.OnItemClickListener<GetPinnedReposQuery.Node>() {
                @Override public void onItemClick(int position, View v, GetPinnedReposQuery.Node item) {
                    SchemeParser.launchUri(getContext(), item.url().toString());
                }

                @Override public void onItemLongClick(int position, View v, GetPinnedReposQuery.Node item) {}
            });
            pinnedList.addDivider();
            pinnedList.setAdapter(adapter);
        } else {
            pinnedReposTextView.setVisibility(GONE);
            pinnedReposCard.setVisibility(GONE);
        }
    }

    @Override public void showProgress(@StringRes int resId) {
        progress.setVisibility(VISIBLE);
    }

    @Override public void hideProgress() {
        progress.setVisibility(GONE);
    }

    @Override public void showErrorMessage(@NonNull String message) {
        onHideProgress();
        super.showErrorMessage(message);
    }

    @Override public void showMessage(int titleRes, int msgRes) {
        onHideProgress();
        super.showMessage(titleRes, msgRes);
    }

    @Override public void onScrollTop(int index) {
        super.onScrollTop(index);
    }

    @Override public void onHeaderLoaded(@Nullable Bitmap bitmap) {
        if (bitmap != null) {
            headerImage.setBackground(new BitmapDrawable(getResources(), bitmap));
            headerImage.setVisibility(VISIBLE);
            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            headerImage.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, Math.round(metrics.widthPixels / 3.33333f)));
            ((ViewGroup) userInformation.getParent()).removeView(userInformation);
            headerImage.addView(userInformation);
            userInformation.setPaddingRelative(getResources().getDimensionPixelSize(R.dimen.spacing_xs_large), 0, 0, getResources()
                    .getDimensionPixelSize(R.dimen.spacing_xs_large));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                username.setTextColor(getResources().getColor(android.R.color.primary_text_dark, getActivity().getTheme()));
                userInformation.setBackground(getResources().getDrawable(R.drawable.scrim, getActivity().getTheme()));
            } else {
                username.setTextColor(getResources().getColor(android.R.color.primary_text_dark));
                userInformation.setBackground(getResources().getDrawable(R.drawable.scrim));
            }
            chooseBanner.setVisibility(GONE);
            if (getPresenter().getLogin().equals(Login.getUser().getLogin())) {
                chooseBanner_pencil.setVisibility(VISIBLE);
                chooseBanner_pencil.bringToFront();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    chooseBanner_pencil.setColorFilter(getResources().getColor(R.color.material_light_white, getActivity().getTheme()));
                    chooseBanner_pencil.setForegroundGravity(TOP);
                } else {
                    chooseBanner_pencil.setColorFilter(getResources().getColor(R.color.material_light_white));
                }
            }
        }
    }

    private void onHideProgress() {
        hideProgress();
    }

    private boolean isMeOrOrganization() {
        return Login.getUser() != null && Login.getUser().getLogin().equalsIgnoreCase(getPresenter().getLogin()) ||
                (userModel != null && userModel.getType() != null && !userModel.getType().equalsIgnoreCase("user"));
    }
}
