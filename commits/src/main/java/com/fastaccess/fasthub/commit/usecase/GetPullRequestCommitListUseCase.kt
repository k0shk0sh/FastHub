package com.fastaccess.fasthub.commit.usecase

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.rx2.Rx2Apollo
import com.fastaccess.data.model.FullCommitModel
import com.fastaccess.data.model.PageInfoModel
import com.fastaccess.data.model.ShortUserModel
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.response.ResponseWithCounterModel
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import github.GetPullRequestCommitsQuery
import io.reactivex.Observable
import javax.inject.Inject

class GetPullRequestCommitListUseCase @Inject constructor(
    private val apolloClient: ApolloClient,
    private val schedulerProvider: SchedulerProvider
) : BaseObservableUseCase() {

    var login: String = ""
    var repo: String = ""
    var number: Int = 0
    var page: Input<String> = Input.absent<String>()
    var changedFiles: Int = 0

    override fun buildObservable(): Observable<Pair<PageInfoModel, ResponseWithCounterModel<FullCommitModel>>> = Rx2Apollo.from(
        apolloClient.query(
            GetPullRequestCommitsQuery.builder()
                .login(login)
                .repo(repo)
                .number(number)
                .pageInput(page)
                .build()
        )
    )
        .map { it.data()?.repositoryOwner?.repository?.pullRequest }
        .map { response ->
            this.changedFiles = response.changedFiles
            val list = response.commits.nodes?.map { it.commit.fragments.fullCommit }?.map {
                FullCommitModel(
                    it.id, it.abbreviatedOid, it.oid.toString(),
                    ShortUserModel(
                        it.author?.user?.login ?: it.author?.name, it.author?.user?.login ?: it.author?.name,
                        it.author?.user?.url?.toString(), avatarUrl = it.author?.user?.avatarUrl?.toString() ?: it.author?.avatarUrl?.toString()
                    ),
                    it.messageHeadline, it.messageBody, it.commitUrl.toString(), it.committedDate, it.isCommittedViaWeb, it.signature?.isValid,
                    it.status?.state?.rawValue(), it.changedFiles, it.additions, it.deletions
                )
            } ?: arrayListOf()

            val pageInfo = PageInfoModel(
                response.commits.pageInfo.fragments.pageInfoFragment.startCursor,
                response.commits.pageInfo.fragments.pageInfoFragment.endCursor,
                response.commits.pageInfo.fragments.pageInfoFragment.isHasNextPage,
                response.commits.pageInfo.fragments.pageInfoFragment.isHasPreviousPage
            )
            return@map Pair(pageInfo, ResponseWithCounterModel(response.commits.totalCount, list))
        }
        .subscribeOn(schedulerProvider.ioThread())
        .observeOn(schedulerProvider.uiThread())

}