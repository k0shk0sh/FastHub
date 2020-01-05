package com.fastaccess.fasthub.reviews.di

import com.fastaccess.fasthub.dagger.scopes.PerFragment
import com.fastaccess.fasthub.reviews.ReviewsFragment
import com.fastaccess.github.editor.di.MentionsModule
import com.fastaccess.markdown.di.MarkdownModule
import dagger.Module
import dagger.Provides

@Module(includes = [MarkdownModule::class, MentionsModule::class])
class ReviewsModule {
    @PerFragment @Provides fun provideReviewContext(fragment: ReviewsFragment) = fragment.requireContext()
}