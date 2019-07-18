package com.fastaccess.github.usecase.issuesprs

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.rx2.Rx2Apollo
import com.fastaccess.data.model.PageInfoModel
import com.fastaccess.data.model.ShortUserModel
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import com.fastaccess.extension.toUser
import github.GetAssigneesQuery
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 27.01.19.
 */
class GetAssigneesUseCase @Inject constructor(
    private val apolloClient: ApolloClient,
    private val schedulerProvider: SchedulerProvider
) : BaseObservableUseCase() {

    var login: String? = null
    var repo: String? = null
    var page: Input<String> = Input.absent<String>()

    override fun buildObservable(): Observable<Pair<PageInfoModel, List<ShortUserModel>>> {
        val login = login
        val repo = repo

        if (login.isNullOrEmpty() || repo.isNullOrEmpty()) {
            return Observable.error(Throwable("this should never happen ;)"))
        }
        return Rx2Apollo.from(apolloClient.query(GetAssigneesQuery(login, repo, page)))
            .subscribeOn(schedulerProvider.ioThread())
            .observeOn(schedulerProvider.uiThread())
            .map { it.data()?.repositoryOwner?.repository?.assignableUsers }
            .map { data ->
                val pageInfo = PageInfoModel(
                    data.pageInfo.startCursor, data.pageInfo.endCursor,
                    data.pageInfo.isHasNextPage, data.pageInfo.isHasPreviousPage
                )
                return@map Pair(pageInfo, data.nodes?.map { it.fragments.shortUserRowItem.toUser() } ?: listOf())
            }
    }
}