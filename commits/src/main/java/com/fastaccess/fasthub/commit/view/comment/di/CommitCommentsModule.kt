package com.fastaccess.fasthub.commit.view.comment.di

import com.fastaccess.fasthub.commit.view.comment.CommitCommentsFragment
import com.fastaccess.fasthub.dagger.scopes.PerFragment
import com.fastaccess.github.editor.di.MentionsModule
import com.fastaccess.markdown.di.MarkdownModule
import dagger.Module
import dagger.Provides


@Module(includes = [MarkdownModule::class, MentionsModule::class])
class CommitCommentsModule {
    @PerFragment @Provides fun provideCommitCommentsFragmentContext(fragment: CommitCommentsFragment) = fragment.requireContext()
}