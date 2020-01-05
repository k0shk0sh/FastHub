package com.fastaccess.github.di.modules

import com.fastaccess.fasthub.dagger.scopes.PerFragment
import com.fastaccess.github.editor.di.MentionsModule
import com.fastaccess.github.ui.modules.issue.fragment.IssueFragment
import com.fastaccess.github.ui.modules.issuesprs.edit.EditIssuePrFragment
import com.fastaccess.github.ui.modules.pr.fragment.PullRequestFragment
import com.fastaccess.markdown.di.MarkdownModule
import dagger.Module
import dagger.Provides

/**
 * Created by Kosh on 02.02.19.
 */

@Module(includes = [MarkdownModule::class, MentionsModule::class])
class IssueModule {
    @PerFragment @Provides fun provideIssueContext(fragment: IssueFragment) = fragment.requireContext()
}

@Module(includes = [MarkdownModule::class, MentionsModule::class])
class EditIssuePrModule {
    @PerFragment @Provides fun provideEditorContext(fragment: EditIssuePrFragment) = fragment.requireContext()
}

@Module(includes = [MarkdownModule::class, MentionsModule::class])
class PullRequestModule {
    @PerFragment @Provides fun provideEditorContext(fragment: PullRequestFragment) = fragment.requireContext()
}