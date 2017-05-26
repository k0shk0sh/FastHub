package com.fastaccess.ui.modules.profile.overview;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.fastaccess.BuildConfig;
import com.fastaccess.R;
import com.fastaccess.data.dao.CreateGistModel;
import com.fastaccess.data.dao.FilesListModel;
import com.fastaccess.data.dao.ImgurReponseModel;
import com.fastaccess.data.dao.model.Gist;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.FileHelper;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.helper.PrefHelper;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.ImgurProvider;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.adapter.ProfileOrgsAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.profile.ProfilePagerMvp;
import com.fastaccess.ui.modules.profile.banner.BannerInfoActivity;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.contributions.ContributionsDay;
import com.fastaccess.ui.widgets.contributions.GitHubContributionsView;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;
import com.fastaccess.ui.widgets.recyclerview.layout_manager.GridManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import icepick.State;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by Kosh on 03 Dec 2016, 9:16 AM
 */

public class ProfileOverviewFragment extends BaseFragment<ProfileOverviewMvp.View, ProfileOverviewPresenter> implements ProfileOverviewMvp.View {

    @BindView(R.id.contributionsCaption)
    FontTextView contributionsCaption;
    @BindView(R.id.organizationsCaption)
    FontTextView organizationsCaption;
    @BindView(R.id.headerImage)
    RelativeLayout headerImage;
    @BindView(R.id.userInformation)
    LinearLayout userInformation;
    @BindView(R.id.chooseBanner)
    Button chooseBanner;
    @BindView(R.id.banner_edit)
    ImageButton chooseBanner_pencil;
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

    private static int READ_REQUEST_CODE = 256;

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
        onInitContributions(getPresenter().getContributions());
        onHeaderLoaded(getPresenter().getHeader());
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
        if(getPresenter().getLogin().equals(Login.getUser().getLogin())&&PrefHelper.getBoolean("banner_learned"))
            chooseBanner.setVisibility(VISIBLE);
    }

    @NonNull @Override public ProfileOverviewPresenter providePresenter() {
        return new ProfileOverviewPresenter();
    }

    @Override public void onInitViews(@Nullable User userModel) {
        progress.setVisibility(GONE);
        if (userModel == null) return;
        this.userModel = userModel;
        followBtn.setVisibility(!isMeOrOrganization() ? VISIBLE : GONE);
        username.setText(userModel.getLogin());
        description.setText(userModel.getBio());
        if (userModel.getBio() == null)
            description.setVisibility(GONE);
        avatarLayout.setUrl(userModel.getAvatarUrl(), null);
        organization.setText(InputHelper.toNA(userModel.getCompany()));
        location.setText(InputHelper.toNA(userModel.getLocation()));
        email.setText(InputHelper.toNA(userModel.getEmail()));
        link.setText(InputHelper.toNA(userModel.getBlog()));
        joined.setText(userModel.getCreatedAt() != null ? ParseDateFormat.getTimeAgo(userModel.getCreatedAt()) : "N/A");
        ViewGroup parent = (ViewGroup) organization.getParent();
        if (organization.getText().equals("N/A")) {
            int i = parent.indexOfChild(organization);
            ((ViewGroup) organization.getParent()).removeViewAt(i + 1);
            organization.setVisibility(GONE);
        }
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

        if (userModel.getLogin().equals(Login.getUser().getLogin()))
            if (headerImage.getVisibility()==GONE) {
                if(PrefHelper.getBoolean("banner_learned")) return;
                headerImage.setBackground(getResources().getDrawable(R.drawable.header));
                headerImage.setVisibility(VISIBLE);
                headerImage.setOnClickListener(view -> {
                    PrefHelper.set("banner_learned", true);
                    Intent intent = new Intent(getContext(), BannerInfoActivity.class);
                    startActivity(intent);
                });
            }

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

    @Override public void onInitContributions(@Nullable List<ContributionsDay> items) {
        if (items != null && !items.isEmpty()) {
            contributionView.onResponse(items);
            contributionCard.setVisibility(VISIBLE);
            contributionsCaption.setVisibility(VISIBLE);
        } else {
            contributionCard.setVisibility(GONE);
            contributionsCaption.setVisibility(GONE);
        }
    }

    @Override public void onInitOrgs(@Nullable List<User> orgs) {
        if (orgs != null && !orgs.isEmpty()) {
            orgsList.setNestedScrollingEnabled(false);
            ProfileOrgsAdapter adapter = new ProfileOrgsAdapter();
            adapter.addItems(orgs);
            orgsList.setAdapter(adapter);
            orgsCard.setVisibility(VISIBLE);
            organizationsCaption.setVisibility(VISIBLE);
            ((GridManager) orgsList.getLayoutManager()).setIconSize(getResources().getDimensionPixelSize(R.dimen.header_icon_zie) +
                    getResources().getDimensionPixelSize(R.dimen.spacing_xs_large));
        } else {
            organizationsCaption.setVisibility(GONE);
            orgsCard.setVisibility(GONE);
        }
    }

    @Override public void onUserNotFound() {
        if (isSafe()) getActivity().finish();
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

    private void onHideProgress() {
        hideProgress();
    }

    private boolean isMeOrOrganization() {
        return Login.getUser() != null && Login.getUser().getLogin().equalsIgnoreCase(getPresenter().getLogin()) ||
                (userModel != null && userModel.getType() != null && !userModel.getType().equalsIgnoreCase("user"));
    }

    @Override
    public void onHeaderLoaded(@Nullable Bitmap bitmap) {
        if(bitmap != null) {
            headerImage.setBackground(new BitmapDrawable(getResources(), bitmap));
            headerImage.setVisibility(VISIBLE);
            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            headerImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Math.round(metrics.widthPixels/3.33333f)));
            ((ViewGroup)userInformation.getParent()).removeView(userInformation);
            headerImage.addView(userInformation);
            userInformation.setPaddingRelative(getResources().getDimensionPixelSize(R.dimen.spacing_xs_large), 0, 0,
                    getResources().getDimensionPixelSize(R.dimen.spacing_xs_large));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                username.setTextColor(getResources().getColor(android.R.color.primary_text_dark, getActivity().getTheme()));
                userInformation.setBackground(getResources().getDrawable(R.drawable.scrim, getActivity().getTheme()));
            } else {
                username.setTextColor(getResources().getColor(android.R.color.primary_text_dark));
                userInformation.setBackground(getResources().getDrawable(R.drawable.scrim));
            }
            chooseBanner.setVisibility(GONE);
            chooseBanner_pencil.setVisibility(VISIBLE);
            chooseBanner_pencil.bringToFront();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                chooseBanner_pencil.setColorFilter(getResources().getColor(R.color.material_light_white, getActivity().getTheme()));
            } else {
                chooseBanner_pencil.setColorFilter(getResources().getColor(R.color.material_light_white));
            }
            chooseBanner_pencil.animate().y(0).setDuration(0).start();
        }
    }

    @OnClick({R.id.chooseBanner, R.id.banner_edit}) public void chooseBanner() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            showFileChooser();
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_REQUEST_CODE);
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), BundleConstant.REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BundleConstant.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                RequestBody image = RequestBody.create(MediaType.parse("image/*"), new File(FileHelper.getPath(getContext(), data.getData())));
                ImgurProvider.getImgurService().postImage("", image);
                RxHelper.getObserver(ImgurProvider.getImgurService().postImage("", image)).subscribe(imgurReponseModel -> {
                    if (imgurReponseModel.getData() != null) {
                        ImgurReponseModel.ImgurImage imageResponse = imgurReponseModel.getData();

                        ImageLoader.getInstance().loadImage(imageResponse.getLink(), new ImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String s, View view) {
                                Log.d(getClass().getSimpleName(), "LOADING STARTED :::");
                            }

                            @Override
                            public void onLoadingFailed(String s, View view, FailReason failReason) {
                                Log.e(getClass().getSimpleName(), "LOADING FAILED :::");
                            }

                            @Override
                            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                                onHeaderLoaded(bitmap);
                                Log.d(getClass().getSimpleName(), "LOADING SUCCESSFUL :::");
                            }

                            @Override
                            public void onLoadingCancelled(String s, View view) {
                                Log.e(getClass().getSimpleName(), "LOADING CANCELLED :::");
                            }
                        });

                        Gist.getMyGists(Login.getUser().getLogin()).forEach(gists -> {
                            for (Gist gist : gists) {
                                if (gist.getDescription().equalsIgnoreCase("header.fst")) {
                                    RxHelper.getObserver(RestProvider.getGistService().deleteGist(gist.getGistId()))
                                            .subscribe();
                                }
                            }
                        });

                        CreateGistModel createGistModel = new CreateGistModel();
                        createGistModel.setDescription(InputHelper.toString("header.fst"));
                        createGistModel.setPublicGist(true);
                        HashMap<String, FilesListModel> modelHashMap = new HashMap<>();
                        FilesListModel file = new FilesListModel();
                        file.setFilename("header.fst");
                        file.setContent(imageResponse.getLink());
                        modelHashMap.put("header.fst", file);
                        createGistModel.setFiles(modelHashMap);
                        RxHelper.getObserver(RestProvider.getGistService().createGist(createGistModel))
                                .subscribe(gist -> Toasty.success(getContext(), getString(R.string.success)));
                    }
                });
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_REQUEST_CODE) {
            if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showFileChooser();
                } else {
                    Toasty.error(getContext(), getString(R.string.permission_failed)).show();
                }
            }
        }
    }

}
