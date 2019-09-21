package com.fastaccess.fasthub.commit.usecase

import com.fastaccess.data.model.CommitFilesModel
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.response.PageableResponse
import com.fastaccess.domain.services.PullRequestService
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import javax.inject.Inject

class GetPullRequestCommitFiles @Inject constructor(
    private val pullRequestService: PullRequestService,
    private val gson: Gson,
    private val schedulerProvider: SchedulerProvider
) : BaseObservableUseCase() {

    var login: String = ""
    var repo: String = ""
    var number: Int = 0
    var page: Int = 0

    override fun buildObservable(): Observable<PageableResponse<CommitFilesModel>> = pullRequestService.getPullRequestFiles(login, repo, number, page)
        .map {
            val items = gson.fromJson<ArrayList<CommitFilesModel>>(
                gson.toJson(it.items),
                object : TypeToken<ArrayList<CommitFilesModel>>() {}.type
            )
            val response = PageableResponse<CommitFilesModel>()
            response.items = items
            response.first = it.first
            response.incompleteResults = it.incompleteResults
            response.last = it.last
            response.next = it.next
            response.previous = it.previous
            response.totalCount = it.totalCount
            return@map response
        }
        .subscribeOn(schedulerProvider.ioThread())
        .observeOn(schedulerProvider.uiThread())

}