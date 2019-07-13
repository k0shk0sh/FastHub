package com.fastaccess.github.usecase.issuesprs

import com.fastaccess.data.model.CloseOpenEventModel
import com.fastaccess.data.model.TimelineModel
import com.fastaccess.data.persistence.models.MyIssuesPullsModel
import com.fastaccess.data.repository.IssueRepositoryProvider
import com.fastaccess.data.repository.LoginRepositoryProvider
import com.fastaccess.domain.repository.services.IssuePrService
import com.fastaccess.domain.response.IssueRequestModel
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import com.fastaccess.extension.me
import io.reactivex.Observable
import java.util.*
import javax.inject.Inject

/**
 * Created by Kosh on 16.02.19.
 */
class CloseOpenIssuePrUseCase @Inject constructor(
    private val issueRepositoryProvider: IssueRepositoryProvider,
    private val issuePrService: IssuePrService,
    private val loginRepositoryProvider: LoginRepositoryProvider
) : BaseObservableUseCase() {

    var repo: String = ""
    var login: String = ""
    var number: Int = -1

    override fun buildObservable(): Observable<TimelineModel> = issueRepositoryProvider.getIssueByNumberMaybe("$login/$repo", number)
        .flatMapObservable { issue ->
            issuePrService.editIssue(login, repo, number, IssueRequestModel(state = if ("closed".equals(issue.state, true)) "open" else "closed"))
                .map {
                    issue.state = it.issueState
                    issueRepositoryProvider.upsert(issue)
                    val me = loginRepositoryProvider.getLoginBlocking()?.me()
                    return@map TimelineModel(closeOpenEventModel = CloseOpenEventModel(Date(), me, null,
                        MyIssuesPullsModel(issue.id, issue.databaseId, issue.number, issue.title, issue.repo?.nameWithOwner, 0, it.issueState, issue.url)))
                }
        }
}