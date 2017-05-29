package com.fastaccess.ui.modules.profile.overview;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.model.User;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.contributions.ContributionsDay;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kosh on 03 Dec 2016, 9:15 AM
 */

public interface ProfileOverviewMvp {

    String HEADER_FST_URL = "https://gist.githubusercontent" +
            ".com/k0shk0sh/44c5d0ba29d179c9e78bc892e8573138/raw/4d443b23dda00c568fc6905b3c28103d55d00b51/header.fst";
    String HEADER_GIST_ID = "44c5d0ba29d179c9e78bc892e8573138";

    interface View extends BaseMvp.FAView {
        void onInitViews(@Nullable User userModel);

        void invalidateFollowBtn();

        void onInitContributions(@Nullable List<ContributionsDay> items);

        void onInitOrgs(@Nullable List<User> orgs);

        void onHeaderLoaded(@Nullable Bitmap bitmap);

        void onUserNotFound();

        void onImagePosted(@Nullable String link);
    }

    interface Presenter extends BaseMvp.FAPresenter {

        void onFragmentCreated(@Nullable Bundle bundle);

        void onWorkOffline(@NonNull String login);

        void onCheckFollowStatus(@NonNull String login);

        boolean isSuccessResponse();

        boolean isFollowing();

        void onFollowButtonClicked(@NonNull String login);

        void onSendUserToView(@Nullable User userModel);

        @NonNull ArrayList<User> getOrgs();

        @NonNull ArrayList<ContributionsDay> getContributions();

        @NonNull String getLogin();

        void onPostImage(@NonNull String path);
    }
}
