package com.fastaccess.github.ui.modules.search.fragment.viewmodel

import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.api.Input
import com.fastaccess.data.model.PageInfoModel
import com.fastaccess.data.model.ShortRepoModel
import com.fastaccess.data.model.parcelable.FilterSearchModel
import com.fastaccess.data.persistence.models.MyIssuesPullsModel
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.usecase.issuesprs.FilterIssuesUseCase
import com.fastaccess.github.usecase.issuesprs.FilterPullRequestsUseCase
import com.fastaccess.github.usecase.search.FilterSearchReposUseCase
import javax.inject.Inject

/**
 * Created by Kosh on 20.10.18.
 */
class FilterSearchViewModel @Inject constructor(
    private val filterIssuesUseCase: FilterIssuesUseCase,
    private val filterPullRequestsUseCase: FilterPullRequestsUseCase,
    private val filterSearchReposUseCase: FilterSearchReposUseCase
) : BaseViewModel() {

    private var pageInfo: PageInfoModel? = null
    var filterModel = FilterSearchModel()
    val issuesPrsList = arrayListOf<MyIssuesPullsModel>()
    val issuesPrsData = MutableLiveData<List<MyIssuesPullsModel>>()
    val reposList = arrayListOf<ShortRepoModel>()
    val reposData = MutableLiveData<List<ShortRepoModel>>()

    override fun onCleared() {
        super.onCleared()
        filterIssuesUseCase.dispose()
        filterPullRequestsUseCase.dispose()
        filterSearchReposUseCase.dispose()
    }

    fun loadData(reload: Boolean = false) {
        if (reload) {
            this.issuesPrsList.clear()
            this.reposList.clear()
            pageInfo = null
        }
        val pageInfo = pageInfo
        if (!reload && (pageInfo != null && !pageInfo.hasNextPage)) return
        val cursor = if (hasNext()) pageInfo?.endCursor else null
        when (filterModel.searchBy) {
            FilterSearchModel.SearchBy.REPOS -> searchByRepo(filterModel, cursor)
            FilterSearchModel.SearchBy.ISSUES -> searchByIssue(filterModel, cursor)
            FilterSearchModel.SearchBy.PRS -> searchByPr(filterModel, cursor)
            FilterSearchModel.SearchBy.USERS -> searchByUser(filterModel, cursor)
            FilterSearchModel.SearchBy.NONE -> {
                // Nothing!
            }
        }
    }

    private fun searchByRepo(filterModel: FilterSearchModel, cursor: String?) {
        filterSearchReposUseCase.cursor = Input.optional(cursor)
        filterSearchReposUseCase.keyword = filterModel.searchQuery
        filterSearchReposUseCase.filterModel = filterModel.filterByRepo
        justSubscribe(filterSearchReposUseCase.buildObservable()
            .doOnNext {
                this.issuesPrsData.postValue(null)
                this.pageInfo = it.first
                this.reposList.addAll(it.second)
                this.reposData.postValue(ArrayList(reposList))
            })
    }

    private fun searchByPr(filterModel: FilterSearchModel, cursor: String?) {
        filterPullRequestsUseCase.cursor = Input.optional(cursor)
        filterPullRequestsUseCase.filterModel = filterModel.filterIssuesPrsModel
        filterPullRequestsUseCase.keyword = filterModel.searchQuery
        justSubscribe(filterPullRequestsUseCase.buildObservable()
            .doOnNext {
                onRequestFinished(it)
            })
    }

    private fun searchByIssue(filterModel: FilterSearchModel, cursor: String?) {
        filterIssuesUseCase.cursor = Input.optional(cursor)
        filterIssuesUseCase.filterModel = filterModel.filterIssuesPrsModel
        filterIssuesUseCase.keyword = filterModel.searchQuery
        justSubscribe(filterIssuesUseCase.buildObservable()
            .doOnNext {
                onRequestFinished(it)
            })
    }

    private fun searchByUser(filterModel: FilterSearchModel, cursor: String?) {

    }

    private fun onRequestFinished(pair: Pair<PageInfoModel, List<MyIssuesPullsModel>>) {
        this.reposData.postValue(null)
        this.pageInfo = pair.first
        this.issuesPrsList.addAll(pair.second)
        this.issuesPrsData.postValue(ArrayList(issuesPrsList)) // create new copy of list as submitList will never be notified
    }

    fun filter(model: FilterSearchModel) {
        this.filterModel = model
        loadData(true)
    }

    fun hasNext() = pageInfo?.hasNextPage ?: false
}