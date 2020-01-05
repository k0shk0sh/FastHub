package com.fastaccess.fasthub.commit.usecase

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.Rx2Apollo
import com.fastaccess.data.model.FullCommitModel
import com.fastaccess.data.model.ShortUserModel
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import github.GetCommitQuery
import io.reactivex.Observable
import javax.inject.Inject

class GetCommitUseCase @Inject constructor(
    private val apolloClient: ApolloClient,
    private val schedulerProvider: SchedulerProvider
) : BaseObservableUseCase() {

    var sha: String = ""
    var login: String = ""
    var repo: String = ""

    override fun buildObservable(): Observable<FullCommitModel> = Rx2Apollo.from(
        apolloClient.query(
            GetCommitQuery.builder()
                .login(login)
                .repo(repo)
                .sha(sha)
                .build()
        )
    ).filter { ((it.data()?.repositoryOwner?.repository?.`object` as? GetCommitQuery.AsCommit)?.fragments?.fullCommit) != null }
        .map { response ->
            (response.data()?.repositoryOwner?.repository?.`object` as? GetCommitQuery.AsCommit)?.fragments?.fullCommit?.let {
                return@map FullCommitModel(
                    it.id, it.abbreviatedOid, it.oid.toString(),
                    ShortUserModel(
                        it.author?.user?.login ?: it.author?.name, it.author?.user?.login ?: it.author?.name,
                        it.author?.user?.url?.toString(), avatarUrl = it.author?.user?.avatarUrl?.toString() ?: it.author?.avatarUrl?.toString()
                    ),
                    it.messageHeadline, it.messageBody, it.commitUrl.toString(), it.committedDate, it.isCommittedViaWeb, it.signature?.isValid,
                    it.status?.state?.rawValue(), it.changedFiles, it.additions, it.deletions
                )
            }
            FullCommitModel()
        }
        .subscribeOn(schedulerProvider.ioThread())
        .observeOn(schedulerProvider.uiThread())

}