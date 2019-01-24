package com.fastaccess.github.usecase.search

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.rx2.Rx2Apollo
import com.fastaccess.data.model.PageInfoModel
import com.fastaccess.data.model.ShortUserModel
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import github.SearchUserQuery
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 20.01.19.
 */
class FilterSearchUsersUseCase @Inject constructor(
    private val apolloClient: ApolloClient
) : BaseObservableUseCase() {

    var cursor: Input<String?> = Input.absent()
    var keyword: String = ""

    override fun buildObservable(): Observable<Pair<PageInfoModel, List<ShortUserModel>>> = Rx2Apollo
        .from(apolloClient.query(SearchUserQuery(keyword, cursor)))
        .map { it.data()?.search }
        .map { search ->
            val list = search.nodes
                ?.mapNotNull { it.fragments.shortUserRowItem }
                ?.map { user ->
                    ShortUserModel(user.id, user.login, user.url.toString(), user.name, user.location,
                        user.bio, user.avatarUrl.toString(), user.isViewerCanFollow, user.isViewerIsFollowing)
                } ?: arrayListOf()
            return@map Pair(PageInfoModel(search.pageInfo.startCursor, search.pageInfo.endCursor,
                search.pageInfo.isHasNextPage, search.pageInfo.isHasPreviousPage), list)
        }


}