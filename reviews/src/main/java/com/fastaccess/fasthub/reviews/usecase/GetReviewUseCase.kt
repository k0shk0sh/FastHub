package com.fastaccess.fasthub.reviews.usecase

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.rx2.Rx2Apollo
import com.fastaccess.data.model.*
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import com.fastaccess.extension.toReactionGroup
import github.GetPullRequestReviewQuery
import io.reactivex.Observable
import javax.inject.Inject

class GetReviewUseCase @Inject constructor(
    private val apolloClient: ApolloClient,
    private val schedulerProvider: SchedulerProvider
) : BaseObservableUseCase() {

    var id: String = ""

    var page: Input<String> = Input.absent<String>()

    override fun buildObservable(): Observable<Pair<PageInfoModel, ArrayList<TimelineModel>>> = Rx2Apollo.from(
        apolloClient.query(
            GetPullRequestReviewQuery.builder()
                .id(id)
                .pageInput(page)
                .build()
        )
    )
        .filter { !it.hasErrors() && it.data() != null }
        .map { it.data()?.node as GetPullRequestReviewQuery.AsPullRequestReview }
        .map { response ->
            val pageInfoFragment = response.comments.pageInfo.fragments.pageInfoFragment
            val pageInfo = PageInfoModel(
                pageInfoFragment.startCursor, pageInfoFragment.endCursor,
                pageInfoFragment.isHasNextPage, pageInfoFragment.isHasPreviousPage
            )
            val timelineModel = arrayListOf<TimelineModel>()
            if (!page.defined) {
                timelineModel.add(TimelineModel(review = ReviewModel(
                    response.id,
                    response.databaseId,
                    ShortUserModel(
                        response.author?.login,
                        response.author?.login,
                        response.author?.url?.toString(),
                        avatarUrl = response.author?.avatarUrl?.toString()
                    ),
                    response.body,
                    response.authorAssociation.rawValue(),
                    response.state.rawValue(),
                    response.createdAt,
                    null,
                    response.isViewerCanReact,
                    response.isViewerCanDelete,
                    response.isViewerCanUpdate,
                    response.isViewerDidAuthor,
                    false,
                    response.reactionGroups?.map { it.fragments.reactions.toReactionGroup() }
                )))
            }
            timelineModel.addAll(response.comments.nodes?.mapIndexedNotNull { index, node ->
                val node1 = node.fragments.pullRequestReviewCommentFragment
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
            } ?: arrayListOf())

            return@map Pair(pageInfo, timelineModel)
        }
        .subscribeOn(schedulerProvider.ioThread())
        .observeOn(schedulerProvider.uiThread())
}