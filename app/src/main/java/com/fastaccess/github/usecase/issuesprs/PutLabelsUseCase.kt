package com.fastaccess.github.usecase.issuesprs

import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.repository.services.IssuePrService
import com.fastaccess.domain.response.body.LabelsBodyModel
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 16.02.19.
 */
class PutLabelsUseCase @Inject constructor(
    private val issueService: IssuePrService,
    private val schedulerProvider: SchedulerProvider
) : BaseObservableUseCase() {

    var repo: String = ""
    var login: String = ""
    var number: Int = 0
    var toAdd: List<String>? = null
    var toRemove: List<String>? = null

    override fun buildObservable(): Observable<Boolean> = when (toRemove.isNullOrEmpty()) {
        true -> issueService.addLabelsToIssue(login, repo, number, LabelsBodyModel(toAdd))
            .subscribeOn(schedulerProvider.ioThread())
            .observeOn(schedulerProvider.uiThread())
            .map { true }
        else -> issueService.addLabelsToIssue(login, repo, number, LabelsBodyModel(toAdd))
            .subscribeOn(schedulerProvider.ioThread())
            .observeOn(schedulerProvider.uiThread())
            .flatMap { Observable.fromIterable(toRemove) }
            .flatMap { issueService.removeLabelsToIssue(login, repo, number, it) }
            .map { true }
    }
}