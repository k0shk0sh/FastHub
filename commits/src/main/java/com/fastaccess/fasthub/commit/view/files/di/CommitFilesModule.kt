package com.fastaccess.fasthub.commit.view.files.di

import com.fastaccess.fasthub.commit.view.files.CommitFilesFragment
import com.fastaccess.fasthub.dagger.scopes.PerFragment
import com.fastaccess.github.editor.di.MentionsModule
import com.fastaccess.markdown.di.MarkdownModule
import dagger.Module
import dagger.Provides

@Module(includes = [MarkdownModule::class, MentionsModule::class])
class CommitFilesModule {
    @PerFragment @Provides fun provideCommitFilesContext(fragment: CommitFilesFragment) = fragment.requireContext()
}