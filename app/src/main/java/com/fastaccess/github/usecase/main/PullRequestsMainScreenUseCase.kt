package com.fastaccess.github.usecase.main

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.Rx2Apollo
import com.fastaccess.data.persistence.models.MainIssuesPullsModel
import com.fastaccess.data.repository.LoginRepositoryProvider
import com.fastaccess.data.repository.MainIssuesPullsRepositoryProvider
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import github.GetPullRequestsQuery
import github.type.IssueState
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 16.06.18.
 */
class PullRequestsMainScreenUseCase @Inject constructor(private val loginRepository: LoginRepositoryProvider,
                                                        private val mainIssues: MainIssuesPullsRepositoryProvider,
                                                        private val apolloClient: ApolloClient) : BaseObservableUseCase() {
    var state: IssueState = IssueState.OPEN

    override fun buildObservable(): Observable<*> = loginRepository.getLogin()
            .flatMapObservable {
                return@flatMapObservable it.login?.let {
                    Rx2Apollo.from(apolloClient.query(GetPullRequestsQuery.builder()
                            .login(it)
                            .build()))
                            .map { it.data()?.user?.pullRequests?.nodes }
                            .map { value ->
                                mainIssues.deleteAllPrs()
                                value.forEach {
                                    val issue = MainIssuesPullsModel(it.id, it.databaseId, it.number, it.title, it.repository.nameWithOwner, it
                                            .comments.totalCount, it.state.name)
                                    mainIssues.insert(issue)
                                }
                            }
                } ?: Observable.empty()
            }
}