package com.fastaccess.github.usecase.main

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.Rx2Apollo
import com.fastaccess.data.persistence.models.MyIssuesPullsModel
import com.fastaccess.data.repository.LoginRepository
import com.fastaccess.data.repository.MyIssuesPullsRepository
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import github.GetPullRequestsQuery
import github.type.IssueState
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 16.06.18.
 */
class PullRequestsMainScreenUseCase @Inject constructor(
    private val loginRepository: LoginRepository,
    private val myIssues: MyIssuesPullsRepository,
    private val apolloClient: ApolloClient,
    private val schedulerProvider: SchedulerProvider
) : BaseObservableUseCase() {
    var state: IssueState = IssueState.OPEN

    override fun buildObservable(): Observable<*> = loginRepository.getLogin()
        .flatMapObservable { loginModel ->
            return@flatMapObservable loginModel.login?.let { login ->
                Rx2Apollo.from(
                    apolloClient.query(
                        GetPullRequestsQuery.builder()
                            .login(login)
                            .count(5)
                            .build()
                    )
                )
                    .subscribeOn(schedulerProvider.ioThread())
                    .observeOn(schedulerProvider.uiThread())
                    .map { it.data()?.user?.pullRequests?.nodes }
                    .map { value ->
                        myIssues.deleteAllPrs()
                        myIssues.insert(value.asSequence().map { it.fragments.shortPullRequestRowItem }.map {
                            MyIssuesPullsModel(
                                it.id, it.databaseId, it.number, it.title,
                                it.repository.nameWithOwner, it.comments.totalCount, it.state.name, it.url.toString(), true
                            )
                        }.toList())
                    }
            } ?: Observable.empty()
        }
}