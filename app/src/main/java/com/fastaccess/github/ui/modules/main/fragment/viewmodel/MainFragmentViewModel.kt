package com.fastaccess.github.ui.modules.main.fragment.viewmodel

import androidx.lifecycle.MutableLiveData
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.usecase.main.IssuesMainScreenUseCase
import com.fastaccess.github.usecase.main.PullRequestsMainScreenUseCase
import github.GetIssuesQuery
import github.GetPullRequestsQuery
import javax.inject.Inject

/**
 * Created by Kosh on 16.06.18.
 */
class MainFragmentViewModel @Inject constructor(private val issuesMainScreenUseCase: IssuesMainScreenUseCase,
                                                private val pullRequestsMainScreenUseCase: PullRequestsMainScreenUseCase) : BaseViewModel() {

    val issuesNode = MutableLiveData<List<GetIssuesQuery.Node?>>()
    val prNode = MutableLiveData<List<GetPullRequestsQuery.Node?>>()

    fun load() {
        issuesMainScreenUseCase.executeSafely(issuesMainScreenUseCase.buildObservable()
                .doOnSubscribe {
                    issuesNode.postValue(null)
                    showProgress()
                }
                .map { it.data()?.user?.issues?.nodes ?: arrayListOf() }
                .doOnNext { response ->
                    hideProgress()
                    issuesNode.postValue(response)
                }
                .doOnError {
                    hideProgress()
                    handleError(it)
                })

        pullRequestsMainScreenUseCase.executeSafely(pullRequestsMainScreenUseCase.buildObservable()
                .doOnSubscribe {
                    showProgress()
                    prNode.postValue(null)
                }
                .map { it.data()?.user?.pullRequests?.nodes ?: arrayListOf() }
                .doOnNext { response ->
                    hideProgress()
                    prNode.postValue(response)
                }
                .doOnError {
                    handleError(it)
                })
    }

    override fun onCleared() {
        super.onCleared()
        issuesMainScreenUseCase.dispose()
        pullRequestsMainScreenUseCase.dispose()
    }
}