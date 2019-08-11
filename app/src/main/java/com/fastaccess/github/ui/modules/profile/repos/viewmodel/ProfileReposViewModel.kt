package com.fastaccess.github.ui.modules.profile.repos.viewmodel

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.fastaccess.data.model.PageInfoModel
import com.fastaccess.data.persistence.models.ProfileRepoModel
import com.fastaccess.data.repository.UserReposRepository
import com.fastaccess.github.base.BaseViewModel
import javax.inject.Inject

/**
 * Created by Kosh on 08.10.18.
 */
class ProfileReposViewModel @Inject constructor(
    private val reposProvider: UserReposRepository
) : BaseViewModel() {

    private var pageInfo: PageInfoModel? = null

    fun repos(login: String): LiveData<PagedList<ProfileRepoModel>> {
        val dataSourceFactory = reposProvider.getRepos(login)
        val config = PagedList.Config.Builder()
            .setPrefetchDistance(com.fastaccess.github.utils.PRE_FETCH_SIZE)
            .setPageSize(com.fastaccess.github.utils.PAGE_SIZE)
            .build()
        return LivePagedListBuilder(dataSourceFactory, config)
            .build()
    }

    fun loadRepos(
        login: String,
        reload: Boolean = false
    ) {
        if (reload) {
            pageInfo = null
        }
        val pageInfo = pageInfo
        if (!reload && (pageInfo != null && !pageInfo.hasNextPage)) return
        add(
            callApi(reposProvider.getReposFromRemote(login, if (hasNext()) pageInfo?.endCursor else null))
                .subscribe({
                    this.pageInfo = it.pageInfo
                    postCounter(it.totalCount)
                }, {
                    it.printStackTrace()
                })
        )
    }

    fun hasNext() = pageInfo?.hasNextPage == true
}