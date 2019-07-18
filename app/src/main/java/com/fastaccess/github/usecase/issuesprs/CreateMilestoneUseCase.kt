package com.fastaccess.github.usecase.issuesprs

import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.repository.services.RepoService
import com.fastaccess.domain.response.body.MilestoneBodyModel
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 16.02.19.
 */
class CreateMilestoneUseCase @Inject constructor(
    private val repoService: RepoService,
    private val schedulerProvider: SchedulerProvider
) : BaseObservableUseCase() {

    var repo: String = ""
    var login: String = ""
    var title: String = ""
    var description: String? = null
    var dueOn: String = ""

    override fun buildObservable(): Observable<Boolean> = repoService.createMilestone(login, repo, MilestoneBodyModel(title, description, dueOn))
        .subscribeOn(schedulerProvider.ioThread())
        .observeOn(schedulerProvider.uiThread())
        .map { true }
}