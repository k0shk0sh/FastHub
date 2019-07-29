package com.fastaccess.github.di.modules

import com.fastaccess.github.di.scopes.PerActivity
import com.fastaccess.github.ui.modules.auth.LoginChooserActivity
import com.fastaccess.github.ui.modules.editor.EditorActivity
import com.fastaccess.github.ui.modules.issue.IssueActivity
import com.fastaccess.github.ui.modules.issuesprs.edit.EditIssuePrActivity
import com.fastaccess.github.ui.modules.main.MainActivity
import com.fastaccess.github.ui.modules.multipurpose.MultiPurposeActivity
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
}