package com.fastaccess.github.ui.modules.main.fragment.viewmodel

import androidx.lifecycle.MutableLiveData
import com.fastaccess.data.persistence.models.FeedModel
import com.fastaccess.data.persistence.models.MainIssuesPullsModel
import com.fastaccess.data.persistence.models.NotificationModel
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
class MainFragmentViewModel @Inject constructor(private val issuesMainScreenUseCase: IssuesMainScreenUseCase,
                                                private val mainIssuesPullsRepo: MainIssuesPullsRepositoryProvider,
                                                private val pullRequestsMainScreenUseCase: PullRequestsMainScreenUseCase,
                                                private val notificationUseCase: NotificationUseCase,
                                                private val feedsMainScreenUseCase: FeedsMainScreenUseCase,
                                                private val loginRepositoryProvider: LoginRepositoryProvider,
                                                private val feedsRepositoryProvider: FeedsRepositoryProvider) : BaseViewModel() {

    private val me by lazy { loginRepositoryProvider.getLoginBlocking() }

    val issues = MutableLiveData<List<MainIssuesPullsModel>>()
    val prs = MutableLiveData<List<MainIssuesPullsModel>>()
    val notifications = MutableLiveData<List<NotificationModel>>()
    val feeds = MutableLiveData<List<FeedModel>>()

    init {
        add(feedsRepositoryProvider.getMainFeeds(me?.login ?: "")
                .subscribe({ feeds.postValue(it) }, { handleError(it) }))
        add(mainIssuesPullsRepo.getIssues(me?.login ?: "")
                .subscribe({ issues.postValue(it) }, { handleError(it) }))
        add(mainIssuesPullsRepo.getPulls(me?.login ?: "")
                .subscribe({ prs.postValue(it) }, { handleError(it) }))
        add(notificationUseCase.getMainNotifications(me?.login ?: "")
                .subscribe({ notifications.postValue(it) }, { handleError(it) }))
    }

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
}