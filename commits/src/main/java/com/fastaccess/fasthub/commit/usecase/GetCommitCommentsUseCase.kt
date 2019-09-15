package com.fastaccess.fasthub.commit.usecase

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.rx2.Rx2Apollo
import com.fastaccess.data.model.CommentAuthorAssociation
import com.fastaccess.data.model.CommentModel
import com.fastaccess.data.model.PageInfoModel
import com.fastaccess.data.model.ShortUserModel
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.response.ResponseWithCounterModel
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import com.fastaccess.extension.toReactionGroup
import github.GetCommitCommentsQuery
import io.reactivex.Observable
import javax.inject.Inject

class GetCommitCommentsUseCase @Inject constructor(
    private val apolloClient: ApolloClient,
    private val schedulerProvider: SchedulerProvider
) : BaseObservableUseCase() {

    var login: String = ""
    var repo: String = ""
    var sha: String = ""
    var page: Input<String> = Input.absent<String>()

    override fun buildObservable(): Observable<Pair<PageInfoModel, ResponseWithCounterModel<CommentModel>>> {
        return Rx2Apollo.from(apolloClient.query(GetCommitCommentsQuery(login, repo, sha, page)))
            .subscribeOn(schedulerProvider.ioThread())
            .observeOn(schedulerProvider.uiThread())
            .filter { (it.data()?.repositoryOwner?.repository?.commitOb as? GetCommitCommentsQuery.AsCommit) != null }
            .map { it.data()?.repositoryOwner?.repository?.commitOb as GetCommitCommentsQuery.AsCommit }
            .map { commit ->
                val data = commit.comments
                val pageInfo = PageInfoModel(
                    data.pageInfo.fragments.pageInfoFragment.startCursor, data.pageInfo.fragments.pageInfoFragment.endCursor,
                    data.pageInfo.fragments.pageInfoFragment.isHasNextPage, data.pageInfo.fragments.pageInfoFragment.isHasPreviousPage
                )
                return@map Pair(pageInfo, ResponseWithCounterModel(data.totalCount,
                    data.nodes?.map { node ->
                        val comment = node.fragments.fullCommitComment
                        CommentModel(
                            comment.id,
                            comment.databaseId,
                            ShortUserModel(
                                comment.author?.login,
                                comment.author?.login,
                                comment.author?.url?.toString(),
                                avatarUrl = comment.author?.avatarUrl?.toString()
                            ),
                            comment.body,
                            CommentAuthorAssociation.fromName(comment.authorAssociation.rawValue()),
                            comment.reactionGroups?.map { it.fragments.reactions.toReactionGroup() },
                            comment.createdAt, comment.updatedAt, comment.isViewerCanReact, comment.isViewerCanDelete,
                            comment.isViewerCanUpdate, comment.isViewerDidAuthor, comment.isViewerCanMinimize,
                            comment.path, comment.position
                        )
                    } ?: listOf()))
            }
    }
}