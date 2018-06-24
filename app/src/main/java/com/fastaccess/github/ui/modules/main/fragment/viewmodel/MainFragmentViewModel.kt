package com.fastaccess.github.ui.modules.main.fragment.viewmodel

import com.fastaccess.data.repository.LoginRepositoryProvider
import com.fastaccess.data.repository.MainIssuesPullsRepositoryProvider
import com.fastaccess.github.base.BaseViewModel
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
                                                private val notificationUseCase: NotificationUseCase,
                                                private val loginRepositoryProvider: LoginRepositoryProvider) : BaseViewModel() {

    private val me by lazy { loginRepositoryProvider.getLoginBlocking() }

    val issues = mainIssuesPullsRepo.getIssues(me?.login ?: "")
    val prs = mainIssuesPullsRepo.getPulls(me?.login ?: "")
    val notifications = notificationUseCase.getMainNotifications(me?.login ?: "")

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