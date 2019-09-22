package com.fastaccess.fasthub.reviews.usecase

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.rx2.Rx2Apollo
import com.fastaccess.data.model.*
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import com.fastaccess.extension.toReactionGroup
import github.GetPullRequestReviewsQuery
import io.reactivex.Observable
import javax.inject.Inject

class GetReviewsUseCase @Inject constructor(
    private val apolloClient: ApolloClient,
    private val schedulerProvider: SchedulerProvider
) : BaseObservableUseCase() {

    var login: String = ""
    var repo: String = ""
    var number: Int = 0
    var page: Input<String> = Input.absent<String>()

    override fun buildObservable(): Observable<Pair<PageInfoModel, ArrayList<TimelineModel>>> = Rx2Apollo.from(
        apolloClient.query(
            GetPullRequestReviewsQuery.builder()
                .login(login)
                .repo(repo)
                .number(number)
                .pageInput(page)
                .build()
        )
    )
        .filter { !it.hasErrors() && it.data() != null }
        .map { it.data()?.repositoryOwner?.repository?.pullRequest?.reviewThreads }
        .map { response ->
            val pageInfoFragment = response.pageInfo.fragments.pageInfoFragment
            val pageInfo = PageInfoModel(
                pageInfoFragment.startCursor, pageInfoFragment.endCursor,
                pageInfoFragment.isHasNextPage, pageInfoFragment.isHasPreviousPage
            )
            val timelineModel = arrayListOf<TimelineModel>()

            val comments = response.nodes?.flatMap { it.comments.nodes?.asIterable() ?: emptyList() } ?: emptyList()
            val reviews = response.nodes?.flatMap { it.comments.nodes?.asIterable() ?: emptyList() }
                ?.distinctBy { it.pullRequestReview?.id }
                ?.mapNotNull { it.pullRequestReview }
                ?.sortedBy { it.createdAt }
                ?.map { node ->
                    ReviewModel(
                        node.id,
                        node.databaseId,
                        ShortUserModel(
                            node.author?.login,
                            node.author?.login,
                            node.author?.url?.toString(),
                            avatarUrl = node.author?.avatarUrl?.toString()
                        ),
                        node.body,
                        node.authorAssociation.rawValue(),
                        node.state.rawValue(),
                        node.createdAt,
                        null,
                        node.isViewerCanReact,
                        node.isViewerCanDelete,
                        node.isViewerCanUpdate,
                        node.isViewerDidAuthor,
                        false,
                        node.reactionGroups?.map { it.fragments.reactions.toReactionGroup() }
                    )
                } ?: arrayListOf()

            reviews.forEach { review ->
                timelineModel.add(TimelineModel(review = review))
                timelineModel.addAll(comments.filter { it.pullRequestReview?.id == review.id }
                    .sortedBy { it.createdAt }
                    .map {
                        TimelineModel(
                            comment = CommentModel(
                                it.id,
                                it.databaseId,
                                ShortUserModel(
                                    it.author?.login,
                                    it.author?.login,
                                    it.author?.url?.toString(),
                                    avatarUrl = it.author?.avatarUrl?.toString()
                                ),
                                it.body, CommentAuthorAssociation.fromName(it.authorAssociation.rawValue()),
                                it.reactionGroups?.map { it.fragments.reactions.toReactionGroup() },
                                it.updatedAt, it.updatedAt, it.isViewerCanReact, it.isViewerCanDelete,
                                it.isViewerCanUpdate, it.isViewerDidAuthor, false,
                                it.path, it.originalPosition, it.isOutdated, it.diffHunk
                            )
                        )
                    })
            }
            return@map Pair(pageInfo, timelineModel)
        }
        .subscribeOn(schedulerProvider.ioThread())
        .observeOn(schedulerProvider.uiThread())
}