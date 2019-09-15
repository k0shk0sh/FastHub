package com.fastaccess.fasthub.commit.view.di

import com.fastaccess.fasthub.commit.view.CommitFragment
import com.fastaccess.fasthub.dagger.scopes.PerFragment
import com.fastaccess.github.editor.di.MentionsModule
import com.fastaccess.markdown.di.MarkdownModule
import dagger.Module
import dagger.Provides

@Module(includes = [MarkdownModule::class, MentionsModule::class])
class CommitModule {
    @PerFragment @Provides fun provideIssueContext(fragment: CommitFragment) = fragment.requireContext()
}