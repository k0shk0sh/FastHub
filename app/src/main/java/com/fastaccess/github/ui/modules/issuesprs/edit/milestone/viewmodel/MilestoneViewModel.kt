package com.fastaccess.github.ui.modules.issuesprs.edit.milestone.viewmodel

import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.api.Input
import com.fastaccess.data.model.PageInfoModel
import com.fastaccess.data.model.TimelineModel
import com.fastaccess.data.model.parcelable.MilestoneModel
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.extensions.DatePrettifier
import com.fastaccess.github.usecase.issuesprs.CreateMilestoneUseCase
import com.fastaccess.github.usecase.issuesprs.GetMilestonesUseCase
import com.fastaccess.github.usecase.issuesprs.MilestoneIssuePrUseCase
import java.util.*
import javax.inject.Inject

/**
 * Created by Kosh on 2018-11-26.
 */
class MilestoneViewModel @Inject constructor(
    private val usecase: GetMilestonesUseCase,
    private val createMilestoneUseCase: CreateMilestoneUseCase,
    private val milestoneIssuePrUseCase: MilestoneIssuePrUseCase
) : BaseViewModel() {

    private var pageInfo: PageInfoModel? = null
    private val list = ArrayList<MilestoneModel>()
    val data = MutableLiveData<List<MilestoneModel>>()
    val response = MutableLiveData<Pair<TimelineModel, MilestoneModel>>()

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

    fun onSubmit(login: String, repo: String, number: Int, milestone: MilestoneModel) {
        milestoneIssuePrUseCase.login = login
        milestoneIssuePrUseCase.repo = repo
        milestoneIssuePrUseCase.number = number
        milestoneIssuePrUseCase.milestone = milestone.number ?: -1
        justSubscribe(milestoneIssuePrUseCase.buildObservable()
            .doOnNext {
                response.postValue(it)
            })
    }

    fun addMilestone(title: String, dueOn: Date, description: String?, login: String?, repo: String?) {
        createMilestoneUseCase.login = login ?: ""
        createMilestoneUseCase.repo = repo ?: ""
        createMilestoneUseCase.title = title
        createMilestoneUseCase.description = description
        createMilestoneUseCase.dueOn = DatePrettifier.toGithubDate(dueOn)
        justSubscribe(createMilestoneUseCase.buildObservable()
            .doOnNext {
                list.add(0, MilestoneModel(title = title, description = description, dueOn = dueOn))
                data.postValue(ArrayList(list))
            })
    }

}