package com.fastaccess.fasthub.reviews.usecase

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.rx2.Rx2Apollo
import com.fastaccess.data.model.*
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import com.fastaccess.extension.toReactionGroup
import github.GetPullRequestReviewsQuery
import github.fragment.PullRequestReviewCommentWithReview
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
            var previousNode: PullRequestReviewCommentWithReview.PullRequestReview? = null

            response.nodes?.forEach {
                val review = it.comments.nodes?.firstOrNull()?.fragments?.pullRequestReviewCommentWithReview?.pullRequestReview
                review?.let { node ->
                    if (previousNode == null || (previousNode?.author != node.author && previousNode?.state != node.state)) {
                        previousNode = node
                        timelineModel.add(TimelineModel(review = ReviewModel(
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
                        )))
                    }
                }
                timelineModel.addAll(it.comments.nodes
                    ?.mapNotNull { it.fragments.pullRequestReviewCommentWithReview }
                    ?.mapIndexed { index, node1 ->
                        TimelineModel(
                            comment = CommentModel(
                                node1.id,
                                node1.databaseId,
                                ShortUserModel(
                                    node1.author?.login,
                                    node1.author?.login,
                                    node1.author?.url?.toString(),
                                    avatarUrl = node1.author?.avatarUrl?.toString()
                                ),
                                node1.body,
                                CommentAuthorAssociation.fromName(node1.authorAssociation.rawValue()),
                                node1.reactionGroups?.map { it.fragments.reactions.toReactionGroup() },
                                node1.updatedAt,
                                node1.updatedAt,
                                node1.isViewerCanReact,
                                node1.isViewerCanDelete,
                                node1.isViewerCanUpdate,
                                node1.isViewerDidAuthor,
                                false,
                                if (index == 0) node1.path else null,
                                if (index == 0) node1.originalPosition else null,
                                node1.isOutdated,
                                if (index == 0) node1.diffHunk else
                                    null
                            )
                        )
                    } ?: arrayListOf()
                )
                timelineModel.add(TimelineModel(dividerId = it.id))
            }
            return@map Pair(pageInfo, timelineModel)
        }
        .subscribeOn(schedulerProvider.ioThread())
        .observeOn(schedulerProvider.uiThread())
}