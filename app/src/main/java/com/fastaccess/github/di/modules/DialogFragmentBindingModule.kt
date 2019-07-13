package com.fastaccess.github.di.modules

import com.fastaccess.github.di.scopes.PerFragment
import com.fastaccess.github.ui.modules.issuesprs.edit.labels.create.CreateLabelFragment
import com.fastaccess.github.ui.modules.issuesprs.edit.milestone.CreateMilestoneDialogFragment
import com.fastaccess.github.ui.widget.dialog.IconDialogFragment
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
}