package com.fastaccess.github.usecase.issuesprs

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.rx2.Rx2Apollo
import com.fastaccess.data.model.*
import com.fastaccess.data.model.parcelable.LabelModel
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import com.fastaccess.extension.*
import com.fastaccess.github.extensions.addIfNotNull
import github.GetIssueTimelineQuery
import github.GetIssueTimelineQuery.*
import github.fragment.*
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 27.01.19.
 */
class GetIssueTimelineUseCase @Inject constructor(
    private val apolloClient: ApolloClient,
    private val schedulerProvider: SchedulerProvider
) : BaseTimelineUseCase() {

    var login: String? = null
    var repo: String? = null
    var number: Int? = null
    var page: Input<String> = Input.absent<String>()

    override fun buildObservable(): Observable<Pair<PageInfoModel, List<TimelineModel>>> {
        val login = login
        val repo = repo
        val number = number

        if (login.isNullOrEmpty() || repo.isNullOrEmpty() || number == null) {
            return Observable.error(Throwable("this should never happen ;)"))
        }

        return Rx2Apollo.from(apolloClient.query(GetIssueTimelineQuery(login, repo, number, page)))
            .subscribeOn(schedulerProvider.ioThread())
            .observeOn(schedulerProvider.uiThread())
            .map { it.data()?.repositoryOwner?.repository?.issue }
            .map { issue ->
                val list = arrayListOf<TimelineModel>()
                val timeline = issue.timelineItems
                val pageInfo = PageInfoModel(
                    timeline.pageInfo.startCursor, timeline.pageInfo.endCursor,
                    timeline.pageInfo.isHasNextPage, timeline.pageInfo.isHasPreviousPage
                )
                timeline.nodes?.forEach { node ->
                    when (node) {
                        is AsIssueComment -> node.fragments.comment?.let { list.add(getComment(it)) }
                        is AsCrossReferencedEvent -> node.fragments.crossReferenced?.let { list.add(getCrossReference(it)) }
                        is AsClosedEvent -> node.fragments.closed?.let { list.add(getClosed(it)) }
                        is AsReopenedEvent -> node.fragments.reopened?.let { list.add(getReopened(it)) }
                        is AsSubscribedEvent -> node.fragments.subscribed?.let { list.add(getSubscribed(it)) }
                        is AsUnsubscribedEvent -> node.fragments.unsubscribed?.let { list.add(getUnsubscribed(it)) }
                        is AsReferencedEvent -> node.fragments.referenced?.let { list.add(getReference(it)) }
                        is AsAssignedEvent -> node.fragments.assigned?.let { list.addIfNotNull(getAssigned(it, list)) }
                        is AsUnassignedEvent -> node.fragments.unAssigned?.let { list.addIfNotNull(getUnassigned(it, list)) }
                        is AsLabeledEvent -> node.fragments.labeled?.let { list.addIfNotNull(getLabel(it, list)) }
                        is AsUnlabeledEvent -> node.fragments.unLabeled?.let { list.addIfNotNull(getUnlabeled(it, list)) }
                        is AsMilestonedEvent -> node.fragments.milestoned?.let { list.add(getMilestone(it)) }
                        is AsDemilestonedEvent -> node.fragments.demilestoned?.let { list.add(getDemilestoned(it)) }
                        is AsRenamedTitleEvent -> node.fragments.renamed?.let { list.add(getRenamed(it)) }
                        is AsLockedEvent -> node.fragments.locked?.let { list.add(getLock(it)) }
                        is AsUnlockedEvent -> node.fragments.unlocked?.let { list.add(getUnlocked(it)) }
                        is AsTransferredEvent -> node.fragments.transferred?.let { list.add(getTransferred(it)) }
                    }
                }
                return@map Pair(pageInfo, list)
            }
    }
}