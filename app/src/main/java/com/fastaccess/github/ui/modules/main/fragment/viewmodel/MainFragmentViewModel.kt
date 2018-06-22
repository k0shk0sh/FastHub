package com.fastaccess.github.ui.modules.main.fragment.viewmodel

import androidx.lifecycle.Transformations
import com.fastaccess.data.repository.MainIssuesPullsRepositoryProvider
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.ui.modules.adapter.MainIssuesCell
import com.fastaccess.github.ui.modules.adapter.MainNotificationCell
import com.fastaccess.github.ui.modules.adapter.MainPullRequestsCell
import com.fastaccess.github.usecase.main.IssuesMainScreenUseCase
import com.fastaccess.github.usecase.main.PullRequestsMainScreenUseCase
import com.fastaccess.github.usecase.notification.NotificationUseCase
import javax.inject.Inject

@Suppress("HasPlatformType")
/**
 * Created by Kosh on 16.06.18.
 */
class MainFragmentViewModel @Inject constructor(private val issuesMainScreenUseCase: IssuesMainScreenUseCase,
                                                private val mainIssuesPullsRepo: MainIssuesPullsRepositoryProvider,
                                                private val pullRequestsMainScreenUseCase: PullRequestsMainScreenUseCase,
                                                private val notificationUseCase: NotificationUseCase) : BaseViewModel() {

    val issues = Transformations.map(mainIssuesPullsRepo.getIssues(), { it.map { MainIssuesCell(it) } })
    val prs = Transformations.map(mainIssuesPullsRepo.getPulls(), { it.map { MainPullRequestsCell(it) } })
    val notifications = Transformations.map(notificationUseCase.getMainNotifications(), { it.map { MainNotificationCell(it) } })

    fun load() {
        notificationUseCase.executeSafely(notificationUseCase.buildObservable()
                .flatMap { issuesMainScreenUseCase.buildObservable() }
                .flatMap { pullRequestsMainScreenUseCase.buildObservable() }
                .doOnSubscribe { showProgress() }
                .doOnNext { hideProgress() }
                .doOnError { handleError(it) })

    }

    override fun onCleared() {
        super.onCleared()
        notificationUseCase.dispose()
        issuesMainScreenUseCase.dispose()
        pullRequestsMainScreenUseCase.dispose()
    }
}