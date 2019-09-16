package com.fastaccess.fasthub.commit.view.files

import androidx.lifecycle.MutableLiveData
import com.fastaccess.data.model.CommitFilesModel
import com.fastaccess.fasthub.commit.usecase.GetCommitFilesUseCase
import com.fastaccess.github.base.BaseViewModel
import javax.inject.Inject

class CommitFilesViewModel @Inject constructor(
    private val commitFilesUseCase: GetCommitFilesUseCase
) : BaseViewModel() {

    val filesLiveData = MutableLiveData<List<CommitFilesModel>>()

    fun loadFiles(login: String, repo: String, sha: String) {
        commitFilesUseCase.login = login
        commitFilesUseCase.repo = repo
        commitFilesUseCase.sha = sha
        justSubscribe(commitFilesUseCase.buildObservable()
            .doOnNext {
                postCounter(it.size)
                filesLiveData.postValue(it)
            })
    }
}