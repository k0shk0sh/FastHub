package com.fastaccess.github.ui.modules.issuesprs.edit.assignees.viewmodel

import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.api.Input
import com.fastaccess.data.model.PageInfoModel
import com.fastaccess.data.model.ShortUserModel
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.usecase.issuesprs.AddAssigneesUseCase
import com.fastaccess.github.usecase.issuesprs.GetAssigneesUseCase
import javax.inject.Inject

/**
 * Created by Kosh on 2018-11-26.
 */
class AssigneesViewModel @Inject constructor(
    private val usecase: GetAssigneesUseCase,
    private val addAssigneesUseCase: AddAssigneesUseCase
) : BaseViewModel() {

    private var pageInfo: PageInfoModel? = null
    private val list = ArrayList<ShortUserModel>()
    val data = MutableLiveData<List<ShortUserModel>>()
    val additionLiveData = MutableLiveData<Boolean>()

    fun load(login: String, repo: String, reload: Boolean = false) {
        if (reload) {
            pageInfo = null
            list.clear()
        }

        val pageInfo = pageInfo
        if (!reload && (pageInfo != null && !pageInfo.hasNextPage)) return
        val cursor = if (hasNext()) pageInfo?.endCursor else null

        usecase.login = login
        usecase.repo = repo
        usecase.page = Input.optional(cursor)
        justSubscribe(callApi(usecase.buildObservable())
            .doOnNext {
                this.pageInfo = it.first
                list.addAll(it.second)
                data.postValue(ArrayList(list))
            })
    }

    fun hasNext() = pageInfo?.hasNextPage == true

    fun addAssignees(login: String, repo: String, number: Int, assignees: List<String>?,
                     toRemove: List<String>?) {
        addAssigneesUseCase.login = login
        addAssigneesUseCase.repo = repo
        addAssigneesUseCase.number = number
        addAssigneesUseCase.assignees = assignees
        addAssigneesUseCase.toRemove = toRemove // remove all assignees and re-add them just in case.
        justSubscribe(addAssigneesUseCase.buildObservable()
            .doOnNext {
                additionLiveData.postValue(it)
            })
    }
}