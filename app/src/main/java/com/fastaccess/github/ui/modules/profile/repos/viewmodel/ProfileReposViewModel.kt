package com.fastaccess.github.ui.modules.profile.repos.viewmodel

import com.fastaccess.data.model.PageInfoModel
import com.fastaccess.data.repository.UserReposRespositoryProvider
import com.fastaccess.github.base.BaseViewModel
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Kosh on 08.10.18.
 */
class ProfileReposViewModel @Inject constructor(private val reposProvider: UserReposRespositoryProvider) : BaseViewModel() {
    private var pageInfo: PageInfoModel? = null

    fun loadRepos(login: String, reload: Boolean) {
        if (reload) {
            pageInfo = null
        }
        add(callApi(reposProvider.getReposFromRemote(login, if (pageInfo?.hasNextPage == true) {
            pageInfo?.endCursor ?: ""
        } else {
            ""
        }))
                .subscribe({
                    Timber.e("$it")
                }, { it.printStackTrace() })
        )
    }
}