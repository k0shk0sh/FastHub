package com.fastaccess.github.ui.modules.main.fragment.viewmodel

import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.usecase.main.IssuesMainScreenUseCase
import com.fastaccess.github.usecase.main.PullRequestsMainScreenUseCase
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Kosh on 16.06.18.
 */
class MainFragmentViewModel @Inject constructor(private val issuesMainScreenUseCase: IssuesMainScreenUseCase,
                                                private val pullRequestsMainScreenUseCase: PullRequestsMainScreenUseCase) : BaseViewModel() {


    fun load() {
        issuesMainScreenUseCase.executeSafely(issuesMainScreenUseCase.buildObservable()
                .doOnSubscribe { showProgress() }
                .doOnNext { response ->
                    Timber.e("${response.data()?.user}")
                    hideProgress()
                    if (!response.hasErrors()) {
                    }
                }
                .doOnError {
                    hideProgress()
                    handleError(it)
                })

        pullRequestsMainScreenUseCase.executeSafely(pullRequestsMainScreenUseCase.buildObservable()
                .doOnSubscribe { showProgress() }
                .doOnNext { response ->
                    Timber.e("${response.data()?.user}")
                    hideProgress()
                    if (!response.hasErrors()) {
                    }
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