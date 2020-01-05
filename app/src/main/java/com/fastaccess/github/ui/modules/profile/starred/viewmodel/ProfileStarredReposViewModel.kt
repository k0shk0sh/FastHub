package com.fastaccess.github.ui.modules.profile.starred.viewmodel

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.fastaccess.data.model.PageInfoModel
import com.fastaccess.data.persistence.models.ProfileStarredRepoModel
import com.fastaccess.data.repository.UserStarredReposRepository
import javax.inject.Inject

/**
 * Created by Kosh on 13.10.18.
 */
class ProfileStarredReposViewModel @Inject constructor(
    private val reposProvider: UserStarredReposRepository
) : com.fastaccess.github.base.BaseViewModel() {

    private var pageInfo: PageInfoModel? = null

    fun starredRepos(login: String): LiveData<PagedList<ProfileStarredRepoModel>> {
        val dataSourceFactory = reposProvider.getStarredRepos(login)
        val config = PagedList.Config.Builder()
            .setPrefetchDistance(com.fastaccess.github.base.utils.PRE_FETCH_SIZE)
            .setPageSize(com.fastaccess.github.base.utils.PAGE_SIZE)
            .build()
        return LivePagedListBuilder(dataSourceFactory, config)
            .build()
    }

    fun loadStarredRepos(
        login: String,
        reload: Boolean = false
    ) {
        if (reload) {
            pageInfo = null
        }
        val pageInfo = pageInfo
        if (!reload && (pageInfo != null && !pageInfo.hasNextPage)) return
        add(
            callApi(reposProvider.getStarredReposFromRemote(login, if (hasNext()) pageInfo?.endCursor else null))
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