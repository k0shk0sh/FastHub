package com.fastaccess.github.usecase.issuesprs

import com.fastaccess.data.model.MilestoneDemilestonedEventModel
import com.fastaccess.data.model.TimelineModel
import com.fastaccess.data.model.parcelable.MilestoneModel
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
class MilestoneIssuePrUseCase @Inject constructor(
    private val issueRepositoryProvider: IssueRepositoryProvider,
    private val issuePrService: IssuePrService,
    private val loginRepositoryProvider: LoginRepositoryProvider
) : BaseObservableUseCase() {

    var repo: String = ""
    var login: String = ""
    var number: Int = -1
    var milestone: Int = -1

    override fun buildObservable(): Observable<Pair<TimelineModel, MilestoneModel>> =
        issueRepositoryProvider.getIssueByNumberMaybe("$login/$repo", number)
            .flatMapObservable { issue ->
                issuePrService.editIssue(login, repo, number, IssueRequestModel(milestone = milestone))
                    .map {
                        val milestone = MilestoneModel(it.milestone?.id?.toString(),
                            it.milestone?.title, it.milestone?.description,
                            it.milestone?.state, it.milestone?.url, it.milestone?.number, it.milestone?.closedAt != null, it.milestone?.dueOn)
                        issue.milestone = milestone
                        issueRepositoryProvider.upsert(issue)
                        val me = loginRepositoryProvider.getLoginBlocking()?.me()
                        return@map Pair(TimelineModel(milestoneEventModel = MilestoneDemilestonedEventModel(Date(), me,
                            it.milestone?.title ?: it.milestone?.number?.toString(), true)), milestone)
                    }
            }
}