package com.fastaccess.github.ui.modules.issuesprs.edit

import androidx.lifecycle.MutableLiveData
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.usecase.files.GetFileContentUseCase
import com.fastaccess.github.usecase.issuesprs.CreateIssueUseCase
import javax.inject.Inject

/**
 * Created by Kosh on 2019-07-30.
 */
class EditIssuePrViewModel @Inject constructor(
    private val getFileContentUseCase: GetFileContentUseCase,
    private val createIssueUseCase: CreateIssueUseCase
) : BaseViewModel() {

    val templateLiveData = MutableLiveData<String>()
    val issueUrlLiveData = MutableLiveData<String>()

    fun loadTemplate(
        login: String,
        repo: String
    ) {
        getFileContentUseCase.login = login
        getFileContentUseCase.repo = repo
        getFileContentUseCase.path = "master:.github/ISSUE_TEMPLATE.md"
        justSubscribe(getFileContentUseCase.buildObservable()
            .map {
                it.text?.replace(Regex("(?s)<!--.*?-->"), "")?.replace("<>", "") ?: "" // replace all comments! keep the text small!
            }
            .doOnNext { templateLiveData.postValue(it) })
    }

    fun createIssue(
        login: String,
        repo: String,
        title: String,
        description: String?
    ) {
        createIssueUseCase.login = login
        createIssueUseCase.repo = repo
        createIssueUseCase.title = title
        createIssueUseCase.description = description
        justSubscribe(
            createIssueUseCase.buildObservable()
                .doOnNext { issueUrlLiveData.postValue(it) }
        )
    }
}