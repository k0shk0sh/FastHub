package com.fastaccess.github.di.modules

import com.fastaccess.fasthub.dagger.scopes.PerFragment
import com.fastaccess.github.ui.modules.issue.fragment.IssueFragment
import com.fastaccess.github.ui.modules.issuesprs.edit.EditIssuePrFragment
import com.fastaccess.github.ui.modules.pr.fragment.PullRequestFragment
import com.fastaccess.github.ui.modules.pr.reviews.ListReviewsFragment
import dagger.Module
import dagger.Provides

/**
 * Created by Kosh on 02.02.19.
 */

@Module
class IssueModule {
    @PerFragment @Provides fun provideIssueContext(fragment: IssueFragment) = fragment.requireContext()
}

@Module
class EditIssuePrModule {
    @PerFragment @Provides fun provideEditorContext(fragment: EditIssuePrFragment) = fragment.requireContext()
}

@Module
class PullRequestModule {
    @PerFragment @Provides fun provideEditorContext(fragment: PullRequestFragment) = fragment.requireContext()
}

@Module
class ListReviewsModule {
    @PerFragment @Provides fun provideEditorContext(fragment: ListReviewsFragment) = fragment.requireContext()
}