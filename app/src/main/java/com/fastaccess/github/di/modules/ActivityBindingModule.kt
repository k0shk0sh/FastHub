package com.fastaccess.github.di.modules

import com.fastaccess.fasthub.commit.list.CommitsListActivity
import com.fastaccess.fasthub.dagger.scopes.PerActivity
import com.fastaccess.fasthub.diff.DiffViewerActivity
import com.fastaccess.fasthub.reviews.PullRequestReviewsActivity
import com.fastaccess.github.editor.EditorActivity
import com.fastaccess.github.editor.comment.CommentActivity
import com.fastaccess.github.ui.modules.auth.LoginChooserActivity
import com.fastaccess.github.ui.modules.issue.IssueActivity
import com.fastaccess.github.ui.modules.issuesprs.edit.EditIssuePrActivity
import com.fastaccess.github.ui.modules.main.MainActivity
import com.fastaccess.github.ui.modules.multipurpose.MultiPurposeActivity
import com.fastaccess.github.ui.modules.pr.PullRequestActivity
import com.fastaccess.github.ui.modules.profile.ProfileActivity
import com.fastaccess.github.ui.modules.trending.TrendingActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.android.support.AndroidSupportInjectionModule

/**
 * Created by Kosh on 12.05.18.
 */
@Suppress("unused")
@Module(
    includes = [
        AndroidSupportInjectionModule::class,
        FragmentBindingModule::class,
        DialogFragmentBindingModule::class]
)
abstract class ActivityBindingModule {
    @PerActivity @ContributesAndroidInjector abstract fun mainActivity(): MainActivity
    @PerActivity @ContributesAndroidInjector abstract fun loginChooser(): LoginChooserActivity
    @PerActivity @ContributesAndroidInjector abstract fun profileActivity(): ProfileActivity
    @PerActivity @ContributesAndroidInjector abstract fun multiPurposeActivity(): MultiPurposeActivity
    @PerActivity @ContributesAndroidInjector abstract fun trendingActivity(): TrendingActivity
    @PerActivity @ContributesAndroidInjector abstract fun issueActivity(): IssueActivity
    @PerActivity @ContributesAndroidInjector abstract fun editorActivity(): EditorActivity
    @PerActivity @ContributesAndroidInjector abstract fun editIssuePrActivity(): EditIssuePrActivity
    @PerActivity @ContributesAndroidInjector abstract fun commentActivity(): CommentActivity
    @PerActivity @ContributesAndroidInjector abstract fun pullRequestActivity(): PullRequestActivity
    @PerActivity @ContributesAndroidInjector abstract fun commitsListActivity(): CommitsListActivity
    @PerActivity @ContributesAndroidInjector abstract fun diffViewerActivity(): DiffViewerActivity
    @PerActivity @ContributesAndroidInjector abstract fun pullRequestReviewsActivity(): PullRequestReviewsActivity
}