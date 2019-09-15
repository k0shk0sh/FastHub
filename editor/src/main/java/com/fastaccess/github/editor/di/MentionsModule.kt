package com.fastaccess.github.editor.di

import android.content.Context
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.fasthub.dagger.scopes.PerFragment
import com.fastaccess.github.editor.presenter.MentionsPresenter
import com.fastaccess.github.editor.usecase.FilterSearchUsersUseCase
import dagger.Module
import dagger.Provides

@Module
class MentionsModule {
    @PerFragment @Provides fun provideMentionsPresenter(
        context: Context,
        searchUsersUseCase: FilterSearchUsersUseCase,
        schedulerProvider: SchedulerProvider
    ) = MentionsPresenter(context, searchUsersUseCase, schedulerProvider)
}