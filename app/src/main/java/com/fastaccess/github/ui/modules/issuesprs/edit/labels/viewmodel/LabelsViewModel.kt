package com.fastaccess.github.ui.modules.issuesprs.edit.labels.viewmodel

import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.api.Input
import com.fastaccess.data.model.PageInfoModel
import com.fastaccess.data.model.parcelable.LabelModel
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.usecase.issuesprs.CreateLabelUseCase
import com.fastaccess.github.usecase.issuesprs.GetLabelsUseCase
import javax.inject.Inject

/**
 * Created by Kosh on 2018-11-26.
 */
class LabelsViewModel @Inject constructor(
    private val usecase: GetLabelsUseCase,
    private val createLabelUseCase: CreateLabelUseCase
) : BaseViewModel() {

    private var pageInfo: PageInfoModel? = null
    private val list = ArrayList<LabelModel>()
    val data = MutableLiveData<List<LabelModel>>()

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

    fun addLabel(login: String, repo: String, name: String, color: String) {
        createLabelUseCase.login = login
        createLabelUseCase.repo = repo
        createLabelUseCase.name = name
        createLabelUseCase.color = color
        justSubscribe(createLabelUseCase.buildObservable()
            .doOnNext {
                list.add(0, it)
                data.postValue(ArrayList(list))
            })
    }
}