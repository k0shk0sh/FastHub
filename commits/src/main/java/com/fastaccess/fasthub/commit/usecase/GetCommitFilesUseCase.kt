package com.fastaccess.fasthub.commit.usecase

import com.fastaccess.data.model.CommitFilesModel
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.services.CommitService
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import javax.inject.Inject

class GetCommitFilesUseCase @Inject constructor(
    private val commitService: CommitService,
    private val gson: Gson,
    private val schedulerProvider: SchedulerProvider
) : BaseObservableUseCase() {

    var sha: String = ""
    var login: String = ""
    var repo: String = ""

    override fun buildObservable(): Observable<List<CommitFilesModel>> = commitService.getCommitFiles(login, repo, sha)
        .map { it.files ?: listOf() }
        .map { gson.fromJson<List<CommitFilesModel>>(gson.toJson(it), object : TypeToken<List<CommitFilesModel>>() {}.type) }
        .subscribeOn(schedulerProvider.ioThread())
        .observeOn(schedulerProvider.uiThread())

}