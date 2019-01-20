package com.fastaccess.github.ui.modules.issuesprs.fragment.viewmodel

import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.api.Input
import com.fastaccess.data.model.PageInfoModel
import com.fastaccess.data.model.parcelable.FilterIssuesPrsModel
import com.fastaccess.data.persistence.models.MyIssuesPullsModel
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.usecase.issuesprs.FilterIssuesUseCase
import com.fastaccess.github.usecase.issuesprs.FilterPullRequestsUseCase
import javax.inject.Inject

/**
 * Created by Kosh on 20.10.18.
 */
class FilterIssuePullRequestsViewModel @Inject constructor(
    private val filterIssuesUseCase: FilterIssuesUseCase,
    private val filterPullRequestsUseCase: FilterPullRequestsUseCase
) : BaseViewModel() {

    private var pageInfo: PageInfoModel? = null
    var filterModel = FilterIssuesPrsModel()
    var isPr = true
        set(value) {
            field = value
            filterModel.isPr = field
        }
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
        if (isPr) {
            filterPullRequestsUseCase.cursor = Input.optional(cursor)
            filterPullRequestsUseCase.filterModel = filterModel
            justSubscribe(filterPullRequestsUseCase.buildObservable()
                .doOnNext {
                    onRequestFinished(it)
                })
        } else {
            filterIssuesUseCase.cursor = Input.optional(cursor)
            filterIssuesUseCase.filterModel = filterModel
            justSubscribe(filterIssuesUseCase.buildObservable()
                .doOnNext {
                    onRequestFinished(it)
                })
        }
    }

    private fun onRequestFinished(pair: Pair<PageInfoModel, List<MyIssuesPullsModel>>) {
        this.pageInfo = pair.first
        this.list.addAll(pair.second)
        this.data.postValue(ArrayList(list))
    }

    fun filter(model: FilterIssuesPrsModel) {
        if (model == filterModel) return
        this.filterModel = model
        loadData(true)
    }

    fun hasNext() = pageInfo?.hasNextPage ?: false
}