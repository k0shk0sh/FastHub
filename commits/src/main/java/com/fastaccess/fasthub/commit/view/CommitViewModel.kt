package com.fastaccess.fasthub.commit.view

import androidx.lifecycle.MutableLiveData
import com.fastaccess.data.model.FullCommitModel
import com.fastaccess.fasthub.commit.usecase.GetCommitUseCase
import com.fastaccess.github.base.BaseViewModel
import javax.inject.Inject

class CommitViewModel @Inject constructor(
    private val commitUseCase: GetCommitUseCase
) : BaseViewModel() {

    val commitLiveData = MutableLiveData<FullCommitModel>()

    fun loadCommit(login: String, repo: String, sha: String) {
        commitUseCase.login = login
        commitUseCase.repo = repo
        commitUseCase.sha = sha
        justSubscribe(commitUseCase.buildObservable()
            .doOnNext {
                commitLiveData.postValue(it)
            })
    }

}