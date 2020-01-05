package com.fastaccess.github.usecase.issuesprs

import com.fastaccess.data.model.MilestoneDemilestonedEventModel
import com.fastaccess.data.model.TimelineModel
import com.fastaccess.data.model.parcelable.MilestoneModel
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
class MilestoneIssuePrUseCase @Inject constructor(
    private val issueRepositoryProvider: IssueRepository,
    private val pullRequestRepository: PullRequestRepository,
    private val issuePrService: IssuePrService,
    private val loginRepositoryProvider: LoginRepository,
    private val schedulerProvider: SchedulerProvider
) : BaseObservableUseCase() {

    var repo: String = ""
    var login: String = ""
    var number: Int = -1
    var milestone: Int = -1
    var isPr: Boolean = false

    override fun buildObservable(): Observable<Pair<TimelineModel, MilestoneModel>> {
        val observable = issuePrService.editIssue(login, repo, number, IssueRequestModel(milestone = milestone))
            .subscribeOn(schedulerProvider.ioThread())
            .observeOn(schedulerProvider.uiThread())
        return observable.flatMapMaybe {
            return@flatMapMaybe if (isPr) {
                issueRepositoryProvider.getIssueByNumberMaybe("$login/$repo", number)
                    .map { issue ->
                        val milestone = MilestoneModel(
                            it.milestone?.id?.toString(),
                            it.milestone?.title, it.milestone?.description,
                            it.milestone?.state, it.milestone?.url,
                            it.milestone?.number, it.milestone?.closedAt != null,
                            it.milestone?.dueOn
                        )
                        issue.milestone = milestone
                        issueRepositoryProvider.upsert(issue)
                        val me = loginRepositoryProvider.getLoginBlocking()?.me()
                        return@map Pair(
                            TimelineModel(
                                milestoneEventModel = MilestoneDemilestonedEventModel(
                                    Date(), me, it.milestone?.title ?: it.milestone?.number?.toString(), true
                                )
                            ), milestone
                        )
                    }
            } else {
                pullRequestRepository.getPullRequestByNumberMaybe("$login/$repo", number)
                    .map { issue ->
                        val milestone = MilestoneModel(
                            it.milestone?.id?.toString(),
                            it.milestone?.title, it.milestone?.description,
                            it.milestone?.state, it.milestone?.url,
                            it.milestone?.number, it.milestone?.closedAt != null,
                            it.milestone?.dueOn
                        )
                        issue.milestone = milestone
                        pullRequestRepository.upsert(issue)
                        val me = loginRepositoryProvider.getLoginBlocking()?.me()
                        return@map Pair(
                            TimelineModel(
                                milestoneEventModel = MilestoneDemilestonedEventModel(
                                    Date(), me, it.milestone?.title ?: it.milestone?.number?.toString(), true
                                )
                            ), milestone
                        )
                    }
            }
        }
    }
}