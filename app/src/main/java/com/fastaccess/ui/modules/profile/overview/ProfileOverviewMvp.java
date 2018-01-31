package com.fastaccess.ui.modules.profile.overview;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.model.User;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.contributions.ContributionsDay;
import com.fastaccess.ui.widgets.contributions.GitHubContributionsView;

import java.util.ArrayList;
import java.util.List;

import github.GetPinnedReposQuery;

/**
 * Created by Kosh on 03 Dec 2016, 9:15 AM
 */

public interface ProfileOverviewMvp {

    interface View extends BaseMvp.FAView {
        void onInitViews(@Nullable User userModel);

        void invalidateFollowBtn();

        void onInitContributions(boolean show);

        void onInitOrgs(@Nullable List<User> orgs);

        void onUserNotFound();

        void onInitPinnedRepos(@NonNull List<GetPinnedReposQuery.Node> nodes);
    }

    interface Presenter extends BaseMvp.FAPresenter {

        void onFragmentCreated(@Nullable Bundle bundle);

        void onWorkOffline(@NonNull String login);

        void onCheckFollowStatus(@NonNull String login);

        boolean isSuccessResponse();

        boolean isFollowing();

        void onFollowButtonClicked(@NonNull String login);

        void onSendUserToView(@Nullable User userModel);

        void onLoadContributionWidget(@NonNull GitHubContributionsView view);

        @NonNull ArrayList<User> getOrgs();

        @NonNull ArrayList<ContributionsDay> getContributions();

        @NonNull ArrayList<GetPinnedReposQuery.Node> getNodes();

        @NonNull String getLogin();
    }
}
