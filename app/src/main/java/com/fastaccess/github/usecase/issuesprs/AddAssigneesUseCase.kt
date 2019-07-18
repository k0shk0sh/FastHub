package com.fastaccess.github.usecase.issuesprs

import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.repository.services.RepoService
import com.fastaccess.domain.response.body.AssigneesBodyModel
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 16.02.19.
 */
class AddAssigneesUseCase @Inject constructor(
    private val repoService: RepoService,
    private val schedulerProvider: SchedulerProvider
) : BaseObservableUseCase() {

    var repo: String = ""
    var login: String = ""
    var number: Int = 0
    var assignees: List<String>? = null
    var toRemove: List<String>? = null

    override fun buildObservable(): Observable<Boolean> = when (toRemove.isNullOrEmpty()) {
        true -> repoService.addAssignees(login, repo, number, AssigneesBodyModel(assignees))
            .subscribeOn(schedulerProvider.ioThread())
            .observeOn(schedulerProvider.uiThread())
            .map { true }
        else -> repoService.removeAssignees(login, repo, number, AssigneesBodyModel(toRemove))
            .subscribeOn(schedulerProvider.ioThread())
            .observeOn(schedulerProvider.uiThread())
            .flatMap { repoService.addAssignees(login, repo, number, AssigneesBodyModel(assignees)) }
            .map { true }
    }
}