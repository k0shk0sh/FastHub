package com.fastaccess.github.usecase.issuesprs

import com.fastaccess.data.repository.IssueRepositoryProvider
import com.fastaccess.domain.repository.services.IssuePrService
import com.fastaccess.domain.response.IssueRequestModel
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 16.02.19.
 */
class EditIssurPrUseCase @Inject constructor(
    private val issueRepositoryProvider: IssueRepositoryProvider,
    private val issuePrService: IssuePrService
) : BaseObservableUseCase() {

    var repo: String = ""
    var login: String = ""
    var number: Int = -1

    override fun buildObservable(): Observable<*> = issueRepositoryProvider.getIssueByNumberSingle("$login/$repo", number)
        .flatMapObservable { issue ->
            issuePrService.editIssue(login, repo, number, IssueRequestModel(state = if (issue.state == "closed") "open" else "closed"))
                .map {
                    issue.state = it.issueState
                    issueRepositoryProvider.upsert(issue)
                    issue
                }
        }
}