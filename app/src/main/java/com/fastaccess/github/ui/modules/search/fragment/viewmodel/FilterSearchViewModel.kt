package com.fastaccess.github.ui.modules.search.fragment.viewmodel

import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.api.Input
import com.fastaccess.data.model.PageInfoModel
import com.fastaccess.data.model.parcelable.FilterSearchModel
import com.fastaccess.data.persistence.models.MyIssuesPullsModel
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.usecase.issuesprs.FilterIssuesUseCase
import com.fastaccess.github.usecase.issuesprs.FilterPullRequestsUseCase
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Kosh on 20.10.18.
 */
class FilterSearchViewModel @Inject constructor(
    private val filterIssuesUseCase: FilterIssuesUseCase,
    private val filterPullRequestsUseCase: FilterPullRequestsUseCase
) : BaseViewModel() {

    private var pageInfo: PageInfoModel? = null
    var filterModel = FilterSearchModel()
    val list = arrayListOf<MyIssuesPullsModel>()
    val data = MutableLiveData<List<MyIssuesPullsModel>>()

    override fun onCleared() {
        super.onCleared()
        filterIssuesUseCase.dispose()
        filterPullRequestsUseCase.dispose()
    }

    fun loadData(reload: Boolean = false) {
        if (reload) {
            this.list.clear()
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
        }
    }

    private fun searchByRepo(filterModel: FilterSearchModel, cursor: String?) {

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
        this.pageInfo = pair.first
        this.list.addAll(pair.second)
        this.data.postValue(ArrayList(list)) // create new copy of list as submitList will never be notified
    }

    fun filter(model: FilterSearchModel) {
        if (model == filterModel) return
        Timber.e("$model")
        this.filterModel = model
        loadData(true)
    }

    fun hasNext() = pageInfo?.hasNextPage ?: false
}