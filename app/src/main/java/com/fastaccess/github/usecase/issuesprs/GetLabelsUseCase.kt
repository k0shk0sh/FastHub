package com.fastaccess.github.usecase.issuesprs

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.rx2.Rx2Apollo
import com.fastaccess.data.model.PageInfoModel
import com.fastaccess.data.model.parcelable.LabelModel
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import com.fastaccess.extension.toLabels
import github.GetLabelsQuery
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 27.01.19.
 */
class GetLabelsUseCase @Inject constructor(
    private val apolloClient: ApolloClient
) : BaseObservableUseCase() {

    var login: String? = null
    var repo: String? = null
    var page: Input<String> = Input.absent<String>()

    override fun buildObservable(): Observable<Pair<PageInfoModel, List<LabelModel>>> {
        val login = login
        val repo = repo

        if (login.isNullOrEmpty() || repo.isNullOrEmpty()) {
            return Observable.error(Throwable("this should never happen ;)"))
        }
        return Rx2Apollo.from(apolloClient.query(GetLabelsQuery(login, repo, page)))
            .map { it.data()?.repositoryOwner?.repository?.labels }
            .map { data ->
                val pageInfo = PageInfoModel(data.pageInfo.startCursor, data.pageInfo.endCursor,
                    data.pageInfo.isHasNextPage, data.pageInfo.isHasPreviousPage)
                return@map Pair(pageInfo, data.nodes?.map { it.fragments.labels.toLabels() } ?: listOf())
            }
    }
}