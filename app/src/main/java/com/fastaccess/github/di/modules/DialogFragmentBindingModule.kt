package com.fastaccess.github.di.modules

import com.fastaccess.fasthub.commit.dialog.CommitListBottomSheetDialog
import com.fastaccess.fasthub.dagger.scopes.PerFragment
import com.fastaccess.github.base.dialog.IconDialogFragment
import com.fastaccess.github.editor.dialog.CreateLinkDialogFragment
import com.fastaccess.github.ui.modules.issuesprs.edit.labels.create.CreateLabelFragment
import com.fastaccess.github.ui.modules.issuesprs.edit.milestone.CreateMilestoneDialogFragment
import com.fastaccess.github.ui.modules.issuesprs.filter.FilterIssuesPrsBottomSheet
import com.fastaccess.github.ui.modules.multipurpose.MultiPurposeBottomSheetDialog
import com.fastaccess.github.ui.modules.quickmsg.QuickMessageBottomSheetDialog
import com.fastaccess.github.ui.modules.search.filter.FilterSearchBottomSheet
import com.fastaccess.github.ui.modules.trending.filter.FilterTrendingBottomSheet
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by Kosh on 19.05.18.
 */
@Suppress("unused")
@Module
abstract class DialogFragmentBindingModule {
    @PerFragment @ContributesAndroidInjector abstract fun provideIconDialog(): IconDialogFragment
    @PerFragment @ContributesAndroidInjector abstract fun provideCreateLabel(): CreateLabelFragment
    @PerFragment @ContributesAndroidInjector abstract fun provideCreateMilestone(): CreateMilestoneDialogFragment
    @PerFragment @ContributesAndroidInjector abstract fun provideMultiPurposeBottomSheetDialog(): MultiPurposeBottomSheetDialog
    @PerFragment @ContributesAndroidInjector abstract fun provideFilterIssuesPrsBottomSheet(): FilterIssuesPrsBottomSheet
    @PerFragment @ContributesAndroidInjector abstract fun provideFilterSearchBottomSheet(): FilterSearchBottomSheet
    @PerFragment @ContributesAndroidInjector abstract fun provideFilterTrendingBottomSheet(): FilterTrendingBottomSheet
    @PerFragment @ContributesAndroidInjector abstract fun provideCreateLinkDialogFragment(): CreateLinkDialogFragment
    @PerFragment @ContributesAndroidInjector abstract fun provideQuickMessageBottomSheetDialog(): QuickMessageBottomSheetDialog
    @PerFragment @ContributesAndroidInjector abstract fun provideCommitListBottomSheetDialog(): CommitListBottomSheetDialog
}