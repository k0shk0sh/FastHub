package com.fastaccess.github.usecase.issuesprs

import com.fastaccess.domain.repository.services.RepoService
import com.fastaccess.domain.response.body.LabelsBodyModel
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 16.02.19.
 */
class PutLabelsUseCase @Inject constructor(
    private val repoService: RepoService
) : BaseObservableUseCase() {

    var repo: String = ""
    var login: String = ""
    var number: Int = 0
    var toAdd: List<String>? = null
    var toRemove: List<String>? = null

    override fun buildObservable(): Observable<Boolean> = when (toRemove.isNullOrEmpty()) {
        true -> repoService.addLabelsToIssue(login, repo, number, LabelsBodyModel(toAdd))
            .map { true }
        else -> repoService.addLabelsToIssue(login, repo, number, LabelsBodyModel(toAdd))
            .flatMap { Observable.fromIterable(toRemove) }
            .flatMap { repoService.removeLabelsToIssue(login, repo, number, it) }
            .map { true }
    }
}