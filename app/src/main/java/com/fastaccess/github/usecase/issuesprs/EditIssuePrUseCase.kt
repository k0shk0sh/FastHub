package com.fastaccess.github.usecase.issuesprs

import com.fastaccess.data.repository.IssueRepository
import com.fastaccess.data.repository.PullRequestRepository
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.response.IssueRequestModel
import com.fastaccess.domain.services.IssuePrService
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 16.02.19.
 */
class EditIssuePrUseCase @Inject constructor(
    private val issueRepositoryProvider: IssueRepository,
    private val pullRequestRepository: PullRequestRepository,
    private val issuePrService: IssuePrService,
    private val schedulerProvider: SchedulerProvider
) : BaseObservableUseCase() {

    var repo: String = ""
    var login: String = ""
    var number: Int = -1
    var title: String? = null
    var description: String? = null
    var isPr: Boolean = false

    override fun buildObservable(): Observable<Boolean> = if (isPr) {
        pullRequestRepository.getPullRequestByNumberMaybe("$login/$repo", number)
            .subscribeOn(schedulerProvider.ioThread())
            .observeOn(schedulerProvider.uiThread())
            .flatMapObservable { issue ->
                issuePrService.editIssue(login, repo, number, IssueRequestModel(title = title, body = description))
                    .subscribeOn(schedulerProvider.ioThread())
                    .observeOn(schedulerProvider.uiThread())
                    .map {
                        issue.title = it.title
                        issue.body = it.body
                        pullRequestRepository.upsert(issue)
                        return@map true
                    }
            }
    } else {
        issueRepositoryProvider.getIssueByNumberMaybe("$login/$repo", number)
            .subscribeOn(schedulerProvider.ioThread())
            .observeOn(schedulerProvider.uiThread())
            .flatMapObservable { issue ->
                issuePrService.editIssue(login, repo, number, IssueRequestModel(title = title, body = description))
                    .subscribeOn(schedulerProvider.ioThread())
                    .observeOn(schedulerProvider.uiThread())
                    .map {
                        issue.title = it.title
                        issue.body = it.body
                        issueRepositoryProvider.upsert(issue)
                        return@map true
                    }
            }
    }
}