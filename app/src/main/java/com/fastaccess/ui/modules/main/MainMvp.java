package com.fastaccess.ui.modules.main;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.drawerlayout.widget.DrawerLayout;

import com.fastaccess.ui.base.mvp.BaseMvp;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

/**
 * Created by Kosh on 09 Nov 2016, 7:51 PM
 */

public interface MainMvp {

    int FEEDS = 0;
    int ISSUES = 1;
    int PULL_REQUESTS = 2;
    int PROFILE = 3;

    @IntDef({
            FEEDS,
            ISSUES,
            PULL_REQUESTS,
            PROFILE
    })
    @Retention(RetentionPolicy.SOURCE) @interface NavigationType {}

    interface View extends BaseMvp.FAView {

        void onNavigationChanged(@NavigationType int navType);

        void onUpdateDrawerMenuHeader();

        void onOpenProfile();

        void onInvalidateNotification();

        void onUserIsBlackListed();
    }

    interface Presenter extends BaseMvp.FAPresenter,
            BottomNavigation.OnMenuItemSelectionListener {

        boolean canBackPress(@NonNull DrawerLayout drawerLayout);

        void onModuleChanged(@NonNull FragmentManager fragmentManager, @NavigationType int type);

        void onShowHideFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment toShow, @NonNull Fragment toHide);

        void onAddAndHide(@NonNull FragmentManager fragmentManager, @NonNull Fragment toAdd, @NonNull Fragment toHide);
    }
}
