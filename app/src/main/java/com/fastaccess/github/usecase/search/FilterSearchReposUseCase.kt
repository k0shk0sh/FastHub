package com.fastaccess.github.usecase.search

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.rx2.Rx2Apollo
import com.fastaccess.data.model.CountModel
import com.fastaccess.data.model.PageInfoModel
import com.fastaccess.data.model.ShortRepoModel
import com.fastaccess.data.model.parcelable.FilterByRepo
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import github.SearchReposQuery
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 20.01.19.
 */
class FilterSearchReposUseCase @Inject constructor(
    private val apolloClient: ApolloClient,
    private val schedulerProvider: SchedulerProvider
) : BaseObservableUseCase() {

    var cursor: Input<String?> = Input.absent()
    var filterModel = FilterByRepo()
    var keyword: String = ""

    override fun buildObservable(): Observable<Pair<PageInfoModel, List<ShortRepoModel>>> = Rx2Apollo
        .from(apolloClient.query(SearchReposQuery(constructQuery(keyword), cursor)))
        .subscribeOn(schedulerProvider.ioThread())
        .observeOn(schedulerProvider.uiThread())
        .map { it.data()?.search }
        .map { search ->
            val list = search.nodes
                ?.mapNotNull { it.fragments.shortRepoRowItem }
                ?.map { repo ->
                    ShortRepoModel(
                        repo.id, repo.databaseId, repo.name,
                        CountModel(repo.stargazers.totalCount), CountModel(repo.issues.totalCount),
                        CountModel(repo.pullRequests.totalCount), repo.forkCount, repo.isFork, repo.isPrivate
                    )
                } ?: arrayListOf()
            return@map Pair(
                PageInfoModel(
                    search.pageInfo.startCursor, search.pageInfo.endCursor,
                    search.pageInfo.isHasNextPage, search.pageInfo.isHasPreviousPage
                ), list
            )
        }


    private fun constructQuery(keyword: String): String {
        return StringBuilder()
            .apply {
                append(keyword).append(" ")
                when (filterModel.filterByRepoIn) {
                    FilterByRepo.FilterByRepoIn.NAME -> append("in:name").append(" ")
                    FilterByRepo.FilterByRepoIn.DESCRIPTION -> append("in:description").append(" ")
                    FilterByRepo.FilterByRepoIn.README -> append("in:readme").append(" ")
                    FilterByRepo.FilterByRepoIn.ALL -> {
                        //ignored for now!
                    }
                }
                if (!filterModel.name.isNullOrEmpty()) {
                    when (filterModel.filterByRepoLimitBy) {
                        FilterByRepo.FilterByRepoLimitBy.USERNAME -> append("user:${filterModel.name}").append(" ")
                        FilterByRepo.FilterByRepoLimitBy.ORG -> append("org:${filterModel.name}").append(" ")
                        null -> {
                        }
                    }
                }
                if (!filterModel.language.isNullOrEmpty()) {
                    append("language:${filterModel.language}").append(" ")
                }
            }
            .toString()
    }
}