package com.fastaccess.github.ui.modules.issuesprs.edit

import androidx.lifecycle.MutableLiveData
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.usecase.files.GetFileContentUseCase
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Kosh on 2019-07-30.
 */
class EditIssuePrViewModel @Inject constructor(
    private val getFileContentUseCase: GetFileContentUseCase
) : BaseViewModel() {

    val templateLiveData = MutableLiveData<String>()

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
            .doOnNext {
                Timber.e(it)
                templateLiveData.postValue(it)
            })
    }
}