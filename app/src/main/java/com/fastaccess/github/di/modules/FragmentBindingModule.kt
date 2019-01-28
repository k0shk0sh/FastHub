package com.fastaccess.github.di.modules

import com.fastaccess.github.di.scopes.PerFragment
import com.fastaccess.github.ui.modules.auth.chooser.LoginChooserFragment
import com.fastaccess.github.ui.modules.auth.login.AuthLoginFragment
import com.fastaccess.github.ui.modules.feed.fragment.FeedsFragment
import com.fastaccess.github.ui.modules.issue.fragment.IssueFragment
import com.fastaccess.github.ui.modules.issuesprs.filter.FilterIssuesPrsBottomSheet
import com.fastaccess.github.ui.modules.issuesprs.fragment.FilterIssuePullRequestsFragment
import com.fastaccess.github.ui.modules.main.fragment.MainFragment
import com.fastaccess.github.ui.modules.multipurpose.MultiPurposeBottomSheetDialog
import com.fastaccess.github.ui.modules.notifications.NotificationPagerFragment
import com.fastaccess.github.ui.modules.notifications.fragment.read.AllNotificationsFragment
import com.fastaccess.github.ui.modules.notifications.fragment.unread.UnreadNotificationsFragment
import com.fastaccess.github.ui.modules.profile.feeds.ProfileFeedFragment
import com.fastaccess.github.ui.modules.profile.followersandfollowings.ProfileFollowersFragment
import com.fastaccess.github.ui.modules.profile.fragment.ProfileFragment
import com.fastaccess.github.ui.modules.profile.gists.ProfileGistsFragment
import com.fastaccess.github.ui.modules.profile.orgs.userorgs.UserOrgsFragment
import com.fastaccess.github.ui.modules.profile.repos.ProfileReposFragment
import com.fastaccess.github.ui.modules.profile.starred.ProfileStarredReposFragment
import com.fastaccess.github.ui.modules.search.filter.FilterSearchBottomSheet
import com.fastaccess.github.ui.modules.search.fragment.SearchFragment
import com.fastaccess.github.ui.modules.trending.filter.FilterTrendingBottomSheet
import com.fastaccess.github.ui.modules.trending.fragment.TrendingFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by Kosh on 19.05.18.
 */
@Suppress("unused")
@Module
abstract class FragmentBindingModule {
    @PerFragment @ContributesAndroidInjector abstract fun provideLoginChooseFragment(): LoginChooserFragment
    @PerFragment @ContributesAndroidInjector abstract fun provideLoginFragment(): AuthLoginFragment
    @PerFragment @ContributesAndroidInjector abstract fun provideMainFragment(): MainFragment
    @PerFragment @ContributesAndroidInjector abstract fun provideProfileFragment(): ProfileFragment
    @PerFragment @ContributesAndroidInjector abstract fun provideProfileReposFragment(): ProfileReposFragment
    @PerFragment @ContributesAndroidInjector abstract fun provideProfileStarredReposFragment(): ProfileStarredReposFragment
    @PerFragment @ContributesAndroidInjector abstract fun provideProfileGistsFragment(): ProfileGistsFragment
    @PerFragment @ContributesAndroidInjector abstract fun provideProfileFollowersFragment(): ProfileFollowersFragment
    @PerFragment @ContributesAndroidInjector abstract fun provideProfileFeedsFragment(): ProfileFeedFragment
    @PerFragment @ContributesAndroidInjector abstract fun provideFeedsFragment(): FeedsFragment
    @PerFragment @ContributesAndroidInjector abstract fun provideNotificationPagerFragment(): NotificationPagerFragment
    @PerFragment @ContributesAndroidInjector abstract fun provideUnreadNotificationsFragment(): UnreadNotificationsFragment
    @PerFragment @ContributesAndroidInjector abstract fun provideAllNotificationsFragment(): AllNotificationsFragment
    @PerFragment @ContributesAndroidInjector abstract fun provideMultiPurposeBottomSheetDialog(): MultiPurposeBottomSheetDialog
    @PerFragment @ContributesAndroidInjector abstract fun provideUserOrgsFragment(): UserOrgsFragment
    @PerFragment @ContributesAndroidInjector abstract fun provideFilterIssuePullRequestsFragment(): FilterIssuePullRequestsFragment
    @PerFragment @ContributesAndroidInjector abstract fun provideFilterIssuesPrsBottomSheet(): FilterIssuesPrsBottomSheet
    @PerFragment @ContributesAndroidInjector abstract fun provideSearchFragment(): SearchFragment
    @PerFragment @ContributesAndroidInjector abstract fun provideFilterSearchBottomSheet(): FilterSearchBottomSheet
    @PerFragment @ContributesAndroidInjector abstract fun provideTrendingFragment(): TrendingFragment
    @PerFragment @ContributesAndroidInjector abstract fun provideFilterTrendingBottomSheet(): FilterTrendingBottomSheet
    @PerFragment @ContributesAndroidInjector abstract fun provideIssueFragment(): IssueFragment
}