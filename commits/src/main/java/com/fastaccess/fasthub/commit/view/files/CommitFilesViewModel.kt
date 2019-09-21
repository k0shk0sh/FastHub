package com.fastaccess.fasthub.commit.view.files

import androidx.lifecycle.MutableLiveData
import com.fastaccess.data.model.CommitFilesModel
import com.fastaccess.fasthub.commit.usecase.GetCommitFilesUseCase
import com.fastaccess.fasthub.commit.usecase.GetPullRequestCommitFiles
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.extensions.toArrayList
import javax.inject.Inject

class CommitFilesViewModel @Inject constructor(
    private val commitFilesUseCase: GetCommitFilesUseCase,
    private val getPullRequestCommitFiles: GetPullRequestCommitFiles
) : BaseViewModel() {

    private var currentPage = 0
    private var isLastPage = false
    private val list = arrayListOf<CommitFilesModel>()

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

    fun loadFiles(login: String, repo: String, number: Int, reload: Boolean = false) {
        if (reload) {
            currentPage = 0
            isLastPage = false
            list.clear()
        }
        currentPage++
        if (!reload && isLastPage) return
        getPullRequestCommitFiles.page = currentPage
        getPullRequestCommitFiles.login = login
        getPullRequestCommitFiles.repo = repo
        getPullRequestCommitFiles.number = number
        justSubscribe(getPullRequestCommitFiles.buildObservable()
            .doOnNext {
                isLastPage = it.last == currentPage
                it.items?.let {
                    list.addAll(it)
                    filesLiveData.postValue(list.toArrayList())
                }
            })
    }
}