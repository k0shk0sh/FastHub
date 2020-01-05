package com.fastaccess.github.usecase.issuesprs

import com.fastaccess.data.model.CloseOpenEventModel
import com.fastaccess.data.model.TimelineModel
import com.fastaccess.data.persistence.models.MyIssuesPullsModel
import com.fastaccess.data.repository.IssueRepository
import com.fastaccess.data.repository.LoginRepository
import com.fastaccess.data.repository.PullRequestRepository
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.response.IssueRequestModel
import com.fastaccess.domain.services.IssuePrService
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import com.fastaccess.extension.me
import io.reactivex.Observable
import java.util.*
import javax.inject.Inject

/**
 * Created by Kosh on 16.02.19.
 */
class CloseOpenIssuePrUseCase @Inject constructor(
    private val issueRepositoryProvider: IssueRepository,
    private val pullRequestRepository: PullRequestRepository,
    private val issuePrService: IssuePrService,
    private val loginRepositoryProvider: LoginRepository,
    private val schedulerProvider: SchedulerProvider
) : BaseObservableUseCase() {

    var repo: String = ""
    var login: String = ""
    var number: Int = -1
    var isPr: Boolean = false

    override fun buildObservable(): Observable<TimelineModel> = if (isPr) {
        pullRequestRepository.getPullRequestByNumberMaybe("$login/$repo", number)
            .subscribeOn(schedulerProvider.ioThread())
            .observeOn(schedulerProvider.uiThread())
            .flatMapObservable { issue ->
                issuePrService.editIssue(login, repo, number, IssueRequestModel(state = if ("closed".equals(issue.state, true)) "open" else "closed"))
                    .subscribeOn(schedulerProvider.ioThread())
                    .observeOn(schedulerProvider.uiThread())
                    .map {
                        issue.state = it.issueState
                        pullRequestRepository.upsert(issue)
                        val me = loginRepositoryProvider.getLoginBlocking()?.me()
                        return@map TimelineModel(
                            closeOpenEventModel = CloseOpenEventModel(
                                Date(), me, null,
                                MyIssuesPullsModel(
                                    issue.id, issue.databaseId, issue.number, issue.title, issue.repo?.nameWithOwner,
                                    0, it.issueState, issue.url, true
                                ), it.issueState?.equals("closed", true)
                            )
                        )
                    }
            }
    } else {
        issueRepositoryProvider.getIssueByNumberMaybe("$login/$repo", number)
            .subscribeOn(schedulerProvider.ioThread())
            .observeOn(schedulerProvider.uiThread())
            .flatMapObservable { issue ->
                issuePrService.editIssue(login, repo, number, IssueRequestModel(state = if ("closed".equals(issue.state, true)) "open" else "closed"))
                    .subscribeOn(schedulerProvider.ioThread())
                    .observeOn(schedulerProvider.uiThread())
                    .map {
                        issue.state = it.issueState
                        issueRepositoryProvider.upsert(issue)
                        val me = loginRepositoryProvider.getLoginBlocking()?.me()
                        return@map TimelineModel(
                            closeOpenEventModel = CloseOpenEventModel(
                                Date(), me, null,
                                MyIssuesPullsModel(
                                    issue.id, issue.databaseId, issue.number, issue.title, issue.repo?.nameWithOwner, 0, it.issueState, issue.url
                                ), it.issueState?.equals("closed", true)
                            )
                        )
                    }
            }
    }
}