package com.fastaccess.github.ui.modules.main.fragment.viewmodel

import androidx.lifecycle.LiveData
import com.fastaccess.data.model.MainScreenModel
import com.fastaccess.data.model.MainScreenModelRowType
import com.fastaccess.data.repository.FeedsRepositoryProvider
import com.fastaccess.data.repository.LoginRepositoryProvider
import com.fastaccess.data.repository.MainIssuesPullsRepositoryProvider
import com.fastaccess.data.repository.NotificationRepositoryProvider
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.usecase.main.FeedsMainScreenUseCase
import com.fastaccess.github.usecase.main.IssuesMainScreenUseCase
import com.fastaccess.github.usecase.main.PullRequestsMainScreenUseCase
import com.fastaccess.github.usecase.notification.NotificationUseCase
import com.snakydesign.livedataextensions.map
import com.snakydesign.livedataextensions.switchMap
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
    private val loginProvider: LoginRepositoryProvider,
    private val feedsRepositoryProvider: FeedsRepositoryProvider,
    private val mainIssuesPullsRepo: MainIssuesPullsRepositoryProvider,
    private val notificationRepositoryProvider: NotificationRepositoryProvider
) : BaseViewModel() {

    val login = loginProvider.getLogin()

    fun getList(): LiveData<ArrayList<MainScreenModel>> = feedsRepositoryProvider.getMainFeedsAsLiveData()
        .map { feeds ->
            val list = arrayListOf<MainScreenModel>()
            list.add(MainScreenModel(MainScreenModelRowType.FEED_TITLE))
            list.addAll(feeds.asSequence().map { MainScreenModel(MainScreenModelRowType.FEED, feed = it) }.toList())
            return@map list
        }.switchMap { list ->
            notificationRepositoryProvider.getMainNotifications().map { notifications ->
                list.add(MainScreenModel(MainScreenModelRowType.NOTIFICATION_TITLE))
                list.addAll(notifications.asSequence().map { MainScreenModel(MainScreenModelRowType.NOTIFICATION, notificationModel = it) }.toList())
                return@map list
            }
        }.switchMap { list ->
            mainIssuesPullsRepo.getIssues().map { issues ->
                list.add(MainScreenModel(MainScreenModelRowType.ISSUES_TITLE))
                list.addAll(issues.asSequence().map { MainScreenModel(MainScreenModelRowType.ISSUES, issuesPullsModel = it) }.toList())
                return@map list
            }
        }.switchMap { list ->
            mainIssuesPullsRepo.getPulls().map { prs ->
                list.add(MainScreenModel(MainScreenModelRowType.PRS_TITLE))
                list.addAll(prs.asSequence().map { MainScreenModel(MainScreenModelRowType.PRS, issuesPullsModel = it) }.toList())
                return@map list
            }
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
        feedsMainScreenUseCase.dispose()
    }

    fun logout() { //TODO
//        add(Observable.fromCallable { fastHubDatabase.clearAllTables() }
//                .subscribe { logoutProcess.postValue(true) })
    }
}