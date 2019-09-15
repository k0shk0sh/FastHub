package com.fastaccess.github.editor.di

import com.fastaccess.fasthub.dagger.scopes.PerFragment
import com.fastaccess.github.editor.comment.CommentFragment
import com.fastaccess.markdown.di.MarkdownModule
import dagger.Module
import dagger.Provides

@Module(includes = [MarkdownModule::class, MentionsModule::class])
class CommentModule {
    @PerFragment @Provides fun provideEditorContext(fragment: CommentFragment) = fragment.requireContext()
}