package com.fastaccess.ui.modules.repos;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fastaccess.data.dao.model.Repo;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.modules.filter.chooser.FilterAddChooserListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

/**
 * Created by Kosh on 09 Dec 2016, 4:16 PM
 */

public interface RepoPagerMvp {

    int CODE = 0;
    int ISSUES = 1;
    int PULL_REQUEST = 2;
    int PROJECTS = 3;
    int PROFILE = 4;

    @IntDef({
            CODE,
            ISSUES,
            PULL_REQUEST,
            PROJECTS,
            PROFILE
    })
    @Retention(RetentionPolicy.SOURCE) @interface RepoNavigationType {}


    interface View extends BaseMvp.FAView, FilterAddChooserListener {

        void onNavigationChanged(@RepoNavigationType int navType);

        void onFinishActivity();

        void onInitRepo();

        void onRepoWatched(boolean isWatched);

        void onRepoStarred(boolean isStarred);

        void onRepoForked(boolean isForked);

        void onRepoPinned(boolean isPinned);

        void onEnableDisableWatch(boolean isEnabled);

        void onEnableDisableStar(boolean isEnabled);

        void onEnableDisableFork(boolean isEnabled);

        void onChangeWatchedCount(boolean isWatched);

        void onChangeStarCount(boolean isStarred);

        void onChangeForkCount(boolean isForked);

        boolean hasUserInteractedWithView();

        void disableIssueTab();

        void openUserProfile();

        void onScrolled(boolean isUp);

        boolean isCollaborator();
    }

    interface Presenter extends BaseMvp.FAPresenter, BottomNavigation.OnMenuItemSelectionListener {

        void onUpdatePinnedEntry(@NonNull String repoId, @NonNull String login);

        void onActivityCreate(@NonNull String repoId, @NonNull String login, @RepoPagerMvp.RepoNavigationType int navTyp);

        @NonNull String repoId();

        @NonNull String login();

        @Nullable Repo getRepo();

        boolean isWatched();

        boolean isStarred();

        boolean isForked();

        boolean isRepoOwner();

        void onWatch();

        void onStar();

        void onFork();

        void onCheckWatching();

        void onCheckStarring();

        void onWorkOffline();

        void onModuleChanged(@NonNull FragmentManager fragmentManager, @RepoNavigationType int type);

        void onShowHideFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment toShow, @NonNull Fragment toHide);

        void onAddAndHide(@NonNull FragmentManager fragmentManager, @NonNull Fragment toAdd, @NonNull Fragment toHide);

        void onDeleteRepo();

        void onPinUnpinRepo();

        void updatePinned(int forks, int stars, int watching);
    }

    interface TabsBadgeListener {
        void onSetBadge(int tabIndex, int count);
    }
}
