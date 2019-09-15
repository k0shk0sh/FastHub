package com.fastaccess.github.editor.di

import com.fastaccess.fasthub.dagger.scopes.PerFragment
import com.fastaccess.github.editor.EditorFragment
import com.fastaccess.markdown.di.MarkdownModule
import dagger.Module
import dagger.Provides

@Module(includes = [MarkdownModule::class, MentionsModule::class])
class EditorModule {
    @PerFragment @Provides fun provideEditorContext(fragment: EditorFragment) = fragment.requireContext()
}