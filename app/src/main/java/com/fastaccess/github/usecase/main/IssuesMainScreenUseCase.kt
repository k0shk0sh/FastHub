package com.fastaccess.github.usecase.main

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.Rx2Apollo
import com.fastaccess.data.persistence.models.MainIssuesPullsModel
import com.fastaccess.data.repository.LoginRepositoryProvider
import com.fastaccess.data.repository.MainIssuesPullsRepositoryProvider
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import github.GetIssuesQuery
import github.type.IssueState
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 16.06.18.
 */
class IssuesMainScreenUseCase @Inject constructor(private val loginRepository: LoginRepositoryProvider,
                                                  private val mainIssues: MainIssuesPullsRepositoryProvider,
                                                  private val apolloClient: ApolloClient) : BaseObservableUseCase() {
    var state: IssueState = IssueState.OPEN

    override fun buildObservable(): Observable<*> = loginRepository.getLogin()
            .flatMapObservable { it ->
                return@flatMapObservable it.login?.let { it ->
                    Rx2Apollo.from(apolloClient.query(GetIssuesQuery.builder()
                            .login(it)
                            .state(arrayListOf(state))
                            .build()))
                            .map { it.data()?.user?.issues?.nodes }
                            .map { value ->
                                mainIssues.deleteAllIssues()
                                val me = loginRepository.getLoginBlocking()
                                value.forEach {
                                    val issue = MainIssuesPullsModel(it.id, it.databaseId, it.number, it.title, it.repository.nameWithOwner, it
                                            .comments.totalCount, "", me?.login)
                                    mainIssues.insert(issue)
                                }
                            }
                } ?: Observable.empty()
            }
}