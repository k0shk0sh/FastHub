package com.fastaccess.github.di.modules

import com.fastaccess.fasthub.commit.list.CommitListFragment
import com.fastaccess.fasthub.commit.list.CommitPagerFragment
import com.fastaccess.fasthub.commit.view.CommitFragment
import com.fastaccess.fasthub.commit.view.comment.CommitCommentsFragment
import com.fastaccess.fasthub.commit.view.comment.di.CommitCommentsModule
import com.fastaccess.fasthub.commit.view.di.CommitModule
import com.fastaccess.fasthub.commit.view.files.CommitFilesFragment
import com.fastaccess.fasthub.commit.view.files.di.CommitFilesModule
import com.fastaccess.fasthub.dagger.scopes.PerFragment
import com.fastaccess.fasthub.reviews.ReviewsFragment
import com.fastaccess.fasthub.reviews.di.ReviewsModule
import com.fastaccess.github.editor.EditorFragment
import com.fastaccess.github.editor.comment.CommentFragment
import com.fastaccess.github.editor.di.CommentModule
import com.fastaccess.github.editor.di.EditorModule
import com.fastaccess.github.ui.modules.auth.chooser.LoginChooserFragment
import com.fastaccess.github.ui.modules.auth.login.AuthLoginFragment
import com.fastaccess.github.ui.modules.feed.fragment.FeedsFragment
import com.fastaccess.github.ui.modules.issue.fragment.IssueFragment
import com.fastaccess.github.ui.modules.issuesprs.edit.EditIssuePrFragment
import com.fastaccess.github.ui.modules.issuesprs.edit.assignees.AssigneesFragment
import com.fastaccess.github.ui.modules.issuesprs.edit.labels.LabelsFragment
import com.fastaccess.github.ui.modules.issuesprs.edit.lockunlock.LockUnlockFragment
import com.fastaccess.github.ui.modules.issuesprs.edit.milestone.MilestoneFragment
import com.fastaccess.github.ui.modules.issuesprs.fragment.FilterIssuePullRequestsFragment
import com.fastaccess.github.ui.modules.main.fragment.MainFragment
import com.fastaccess.github.ui.modules.notifications.NotificationPagerFragment
import com.fastaccess.github.ui.modules.notifications.fragment.read.AllNotificationsFragment
import com.fastaccess.github.ui.modules.notifications.fragment.unread.UnreadNotificationsFragment
import com.fastaccess.github.ui.modules.pr.fragment.PullRequestFragment
import com.fastaccess.github.ui.modules.profile.feeds.ProfileFeedFragment
import com.fastaccess.github.ui.modules.profile.followersandfollowings.ProfileFollowersFragment
import com.fastaccess.github.ui.modules.profile.fragment.ProfileFragment
import com.fastaccess.github.ui.modules.profile.gists.ProfileGistsFragment
import com.fastaccess.github.ui.modules.profile.orgs.userorgs.UserOrgsFragment
import com.fastaccess.github.ui.modules.profile.repos.ProfileReposFragment
import com.fastaccess.github.ui.modules.profile.starred.ProfileStarredReposFragment
import com.fastaccess.github.ui.modules.search.fragment.SearchFragment
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
    @PerFragment @ContributesAndroidInjector abstract fun provideUserOrgsFragment(): UserOrgsFragment
    @PerFragment @ContributesAndroidInjector abstract fun provideFilterIssuePullRequestsFragment(): FilterIssuePullRequestsFragment
    @PerFragment @ContributesAndroidInjector abstract fun provideSearchFragment(): SearchFragment
    @PerFragment @ContributesAndroidInjector abstract fun provideTrendingFragment(): TrendingFragment
    @PerFragment @ContributesAndroidInjector abstract fun provideLockUnlockFragment(): LockUnlockFragment
    @PerFragment @ContributesAndroidInjector abstract fun provideLabelsFragment(): LabelsFragment
    @PerFragment @ContributesAndroidInjector abstract fun provideAssigneesFragment(): AssigneesFragment
    @PerFragment @ContributesAndroidInjector abstract fun provideMilestoneFragment(): MilestoneFragment
    @PerFragment @ContributesAndroidInjector abstract fun provideCommitListFragment(): CommitListFragment
    @PerFragment @ContributesAndroidInjector abstract fun provideCommitPagerFragment(): CommitPagerFragment
    @PerFragment @ContributesAndroidInjector(modules = [EditorModule::class]) abstract fun provideEditorFragment(): EditorFragment
    @PerFragment @ContributesAndroidInjector(modules = [CommentModule::class]) abstract fun provideCommentFragment(): CommentFragment
    @PerFragment @ContributesAndroidInjector(modules = [CommitModule::class]) abstract fun provideCommitFragment(): CommitFragment
    @PerFragment @ContributesAndroidInjector(modules = [CommitCommentsModule::class]) abstract fun provideCommitCommentsFragment(): CommitCommentsFragment
    @PerFragment @ContributesAndroidInjector(modules = [CommitFilesModule::class]) abstract fun provideCommitFilesFragment(): CommitFilesFragment
    @PerFragment @ContributesAndroidInjector(modules = [ReviewsModule::class]) abstract fun provideReviewsFragment(): ReviewsFragment
    @PerFragment @ContributesAndroidInjector(modules = [IssueModule::class]) abstract fun provideIssueFragment(): IssueFragment
    @PerFragment @ContributesAndroidInjector(modules = [EditIssuePrModule::class]) abstract fun provideEditIssuePrFragment(): EditIssuePrFragment
    @PerFragment @ContributesAndroidInjector(modules = [PullRequestModule::class]) abstract fun providePullRequestFragment(): PullRequestFragment
}