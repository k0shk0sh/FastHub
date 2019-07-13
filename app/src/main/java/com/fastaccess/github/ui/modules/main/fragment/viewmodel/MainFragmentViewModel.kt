package com.fastaccess.github.ui.modules.main.fragment.viewmodel

import androidx.lifecycle.LiveData
import com.fastaccess.data.model.FastHubErrors
import com.fastaccess.data.model.MainScreenModel
import com.fastaccess.data.model.MainScreenModelRowType
import com.fastaccess.data.persistence.db.FastHubDatabase
import com.fastaccess.data.persistence.models.FeedModel
import com.fastaccess.data.repository.FeedsRepositoryProvider
import com.fastaccess.data.repository.LoginRepositoryProvider
import com.fastaccess.data.repository.MyIssuesPullsRepositoryProvider
import com.fastaccess.data.repository.NotificationRepositoryProvider
import com.fastaccess.extension.uiThread
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.extensions.map
import com.fastaccess.github.extensions.switchMap
import com.fastaccess.github.usecase.main.FeedsMainScreenUseCase
import com.fastaccess.github.usecase.main.IssuesMainScreenUseCase
import com.fastaccess.github.usecase.main.PullRequestsMainScreenUseCase
import com.fastaccess.github.usecase.notification.NotificationUseCase
import io.reactivex.Observable
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
    private val feedsRepositoryProvider: FeedsRepositoryProvider,
    private val myIssuesPullsRepo: MyIssuesPullsRepositoryProvider,
    private val notificationRepositoryProvider: NotificationRepositoryProvider,
    private val fasthubDatabase: FastHubDatabase,
    private val loginProvider: LoginRepositoryProvider
) : BaseViewModel() {

    val login = loginProvider.getLogin()

    val list: LiveData<ArrayList<MainScreenModel>> by lazy {
        feedsRepositoryProvider.getMainFeedsAsLiveData()
            .map(mapFeed())
            .switchMap(mapNotifications())
            .switchMap(mapIssues())
            .switchMap(mapPulls())
    }

    val unreadNotificationLiveData = notificationRepositoryProvider.getMainNotifications().map { it.firstOrNull { it.unread == true } != null }

    fun load() {
        feedsMainScreenUseCase.executeSafely(callApi(
            feedsMainScreenUseCase.buildObservable()
                .flatMap { notificationUseCase.buildObservable() }
                .flatMap { issuesMainScreenUseCase.buildObservable() }
                .flatMap { pullRequestsMainScreenUseCase.buildObservable() }
        ))
    }

    fun logout() {
        add(Observable.fromCallable {
            fasthubDatabase.clearAll()
            loginProvider.logoutAll()
        }
            .uiThread()
            .subscribe({ logoutProcess.postValue(true) },
                { error.postValue(FastHubErrors(FastHubErrors.ErrorType.OTHER, it.message)) })
        )
    }

    override fun onCleared() {
        super.onCleared()
        notificationUseCase.dispose()
        issuesMainScreenUseCase.dispose()
        pullRequestsMainScreenUseCase.dispose()
        feedsMainScreenUseCase.dispose()
    }

    /**
     * Fixes Cannot infer a type for this parameter. Please specify it explicitly. 🤷‍🤷‍🤷‍🤷‍🤷‍🤷‍
     */
    private fun mapPulls(): (ArrayList<MainScreenModel>) -> LiveData<ArrayList<MainScreenModel>> {
        return { list ->
            myIssuesPullsRepo.getMainScreenPulls().map { prs ->
                if (prs.isEmpty()) return@map list
                list.add(MainScreenModel(MainScreenModelRowType.PRS_TITLE))
                list.addAll(prs.asSequence().map { MainScreenModel(MainScreenModelRowType.PRS, issuesPullsModel = it) }.toList())
                return@map list
            }
        }
    }

    /**
     * Fixes Cannot infer a type for this parameter. Please specify it explicitly. 🤷‍🤷‍🤷‍🤷‍🤷‍🤷‍
     */
    private fun mapIssues(): (ArrayList<MainScreenModel>) -> LiveData<ArrayList<MainScreenModel>> {
        return { list ->
            myIssuesPullsRepo.getMainScreenIssues().map { issues ->
                if (issues.isEmpty()) return@map list
                list.add(MainScreenModel(MainScreenModelRowType.ISSUES_TITLE))
                list.addAll(issues.asSequence().map { MainScreenModel(MainScreenModelRowType.ISSUES, issuesPullsModel = it) }.toList())
                return@map list
            }
        }
    }

    /**
     * Fixes Cannot infer a type for this parameter. Please specify it explicitly. 🤷‍🤷‍🤷‍🤷‍🤷‍🤷‍
     */
    private fun mapNotifications(): (ArrayList<MainScreenModel>) -> LiveData<ArrayList<MainScreenModel>> {
        return { list ->
            notificationRepositoryProvider.getMainNotifications().map { notifications ->
                if (notifications.isEmpty()) return@map list
                list.add(MainScreenModel(MainScreenModelRowType.NOTIFICATION_TITLE,
                    hasBubble = notifications.firstOrNull { it.unread == true } != null))
                list.addAll(notifications.asSequence().map { MainScreenModel(MainScreenModelRowType.NOTIFICATION, notificationModel = it) }.toList())
                return@map list
            }
        }
    }

    /**
     * Fixes Cannot infer a type for this parameter. Please specify it explicitly. 🤷‍🤷‍🤷‍🤷‍🤷‍🤷‍
     */
    private fun mapFeed(): (List<FeedModel>) -> ArrayList<MainScreenModel> {
        return { feeds ->
            val list = arrayListOf<MainScreenModel>()
            if (feeds.isNotEmpty()) {
                list.add(MainScreenModel(MainScreenModelRowType.FEED_TITLE))
                list.addAll(feeds.asSequence().map { MainScreenModel(MainScreenModelRowType.FEED, feed = it) }.toList())
            }
            list
        }
    }

}