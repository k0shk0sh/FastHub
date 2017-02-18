package com.fastaccess.ui.modules.repos;

import android.content.Intent;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fastaccess.data.dao.RepoModel;
import com.fastaccess.ui.base.mvp.BaseMvp;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

/**
 * Created by Kosh on 09 Dec 2016, 4:16 PM
 */

interface RepoPagerMvp {

    int CODE = 0;
    int ISSUES = 1;
    int PULL_REQUEST = 2;

    @IntDef({
            CODE,
            ISSUES,
            PULL_REQUEST,
    })
    @Retention(RetentionPolicy.SOURCE) @interface RepoNavigationType {}


    interface View extends BaseMvp.FAView {

        void onNavigationChanged(@RepoNavigationType int navType);

        void onFinishActivity();

        void onInitRepo();

        void onRepoWatched(boolean isWatched);

        void onRepoStarred(boolean isStarred);

        void onRepoForked(boolean isForked);

        void onEnableDisableWatch(boolean isEnabled);

        void onEnableDisableStar(boolean isEnabled);

        void onEnableDisableFork(boolean isEnabled);

        void onChangeWatchedCount(boolean isWatched);

        void onChangeStarCount(boolean isStarred);

        void onChangeForkCount(boolean isForked);
    }

    interface Presenter extends BaseMvp.FAPresenter, BottomNavigation.OnMenuItemSelectionListener {
        void onActivityCreated(@Nullable Intent intent);

        @NonNull String repoId();

        @NonNull String login();

        @Nullable RepoModel getRepo();

        boolean isWatched();

        boolean isStarred();

        boolean isForked();

        void onWatch();

        void onStar();

        void onFork();

        void onCheckWatching();

        void onCheckStarring();

        void onWorkOffline();

        void onModuleChanged(@NonNull FragmentManager fragmentManager, @RepoNavigationType int type);

        void onShowHideFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment toShow, @NonNull Fragment toHide);

        void onAddAndHide(@NonNull FragmentManager fragmentManager, @NonNull Fragment toAdd, @NonNull Fragment toHide);

    }
}
