package com.fastaccess.github.usecase.issuesprs

import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.repository.services.IssuePrService
import com.fastaccess.domain.response.IssueRequestModel
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 16.02.19.
 */
class CreateIssueUseCase @Inject constructor(
    private val issuePrService: IssuePrService,
    private val schedulerProvider: SchedulerProvider
) : BaseObservableUseCase() {

    var repo: String = ""
    var login: String = ""
    var title: String = ""
    var description: String? = null

    override fun buildObservable(): Observable<String> = issuePrService.createIssue(login, repo, IssueRequestModel(title, description))
        .subscribeOn(schedulerProvider.ioThread())
        .observeOn(schedulerProvider.uiThread())
        .map { it.htmlUrl }
}