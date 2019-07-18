package com.fastaccess.github.usecase.issuesprs

import com.fastaccess.data.model.parcelable.LabelModel
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.repository.services.RepoService
import com.fastaccess.domain.response.LabelResponse
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 16.02.19.
 */
class CreateLabelUseCase @Inject constructor(
    private val repoService: RepoService,
    private val schedulerProvider: SchedulerProvider
) : BaseObservableUseCase() {

    var repo: String = ""
    var login: String = ""
    var name: String = ""
    var color: String = ""

    override fun buildObservable(): Observable<LabelModel> = repoService.addLabel(login, repo, LabelResponse(color = color, name = name))
        .subscribeOn(schedulerProvider.ioThread())
        .observeOn(schedulerProvider.uiThread())
        .map {
            LabelModel(it.name, it.color, it.url)
        }
}