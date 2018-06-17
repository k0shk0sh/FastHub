package com.fastaccess.github.ui.modules.main.fragment.viewmodel

import androidx.lifecycle.Transformations
import com.fastaccess.data.repository.MainIssuesPullsRepositoryProvider
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.ui.modules.adapter.MainIssuesCell
import com.fastaccess.github.ui.modules.adapter.MainPullRequestsCell
import com.fastaccess.github.usecase.main.IssuesMainScreenUseCase
import com.fastaccess.github.usecase.main.PullRequestsMainScreenUseCase
import javax.inject.Inject

/**
 * Created by Kosh on 16.06.18.
 */
class MainFragmentViewModel @Inject constructor(private val issuesMainScreenUseCase: IssuesMainScreenUseCase,
                                                mainIssuesPullsRepo: MainIssuesPullsRepositoryProvider,
                                                private val pullRequestsMainScreenUseCase: PullRequestsMainScreenUseCase) : BaseViewModel() {

    val issues = Transformations.map(mainIssuesPullsRepo.getIssues(), { it.map { MainIssuesCell(it) } })
    val prs = Transformations.map(mainIssuesPullsRepo.getPulls(), { it.map { MainPullRequestsCell(it) } })

    fun load() {
        issuesMainScreenUseCase.executeSafely(issuesMainScreenUseCase.buildObservable()
                .doOnSubscribe { showProgress() }
                .doOnNext { hideProgress() }
                .doOnError { handleError(it) })

        pullRequestsMainScreenUseCase.executeSafely(pullRequestsMainScreenUseCase.buildObservable()
                .doOnSubscribe { showProgress() }
                .doOnNext { hideProgress() }
                .doOnError { handleError(it) })
    }

    override fun onCleared() {
        super.onCleared()
        issuesMainScreenUseCase.dispose()
        pullRequestsMainScreenUseCase.dispose()
    }
}