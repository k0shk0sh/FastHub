package com.fastaccess.github.ui.modules.main.fragment.viewmodel

import com.fastaccess.data.repository.FeedsRepositoryProvider
import com.fastaccess.data.repository.LoginRepositoryProvider
import com.fastaccess.data.repository.MainIssuesPullsRepositoryProvider
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.usecase.main.FeedsMainScreenUseCase
import com.fastaccess.github.usecase.main.IssuesMainScreenUseCase
import com.fastaccess.github.usecase.main.PullRequestsMainScreenUseCase
import com.fastaccess.github.usecase.notification.NotificationUseCase
import javax.inject.Inject

@Suppress("HasPlatformType")
/**
 * Created by Kosh on 16.06.18.
 */
class MainFragmentViewModel @Inject constructor(
        private val issuesMainScreenUseCase: IssuesMainScreenUseCase,
        private val pullRequestsMainScreenUseCase: PullRequestsMainScreenUseCase,
        private val notificationUseCase: NotificationUseCase,
        private val feedsMainScreenUseCase: FeedsMainScreenUseCase,
        loginProvider: LoginRepositoryProvider,
        feedsRepositoryProvider: FeedsRepositoryProvider,
        mainIssuesPullsRepo: MainIssuesPullsRepositoryProvider
) : BaseViewModel() {

    val issues = mainIssuesPullsRepo.getIssues()
    val prs = mainIssuesPullsRepo.getPulls()
    val notifications = notificationUseCase.getMainNotifications()
    val feeds = feedsRepositoryProvider.getMainFeedsAsLiveData()
    val login = loginProvider.getLogin()

    fun load() {
        feedsMainScreenUseCase.executeSafely(callApi(
                feedsMainScreenUseCase.buildObservable()
                        .flatMap { notificationUseCase.buildObservable() }
                        .flatMap { issuesMainScreenUseCase.buildObservable() }
                        .flatMap { pullRequestsMainScreenUseCase.buildObservable() }
        ))
    }

    override fun onCleared() {
        super.onCleared()
        notificationUseCase.dispose()
        issuesMainScreenUseCase.dispose()
        pullRequestsMainScreenUseCase.dispose()
    }

    fun logout() {
//        add(Observable.fromCallable { fastHubDatabase.clearAllTables() }
//                .subscribe { logoutProcess.postValue(true) })
    }
}