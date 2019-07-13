package com.fastaccess.github.ui.modules.profile.gists.viewmodel

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.fastaccess.data.model.PageInfoModel
import com.fastaccess.data.persistence.models.ProfileGistModel
import com.fastaccess.data.repository.UserGistsRepositoryProvider
import com.fastaccess.github.base.BaseViewModel
import javax.inject.Inject

/**
 * Created by Kosh on 14.10.18.
 */
class ProfileGistsViewModel @Inject constructor(
        private val reposProvider: UserGistsRepositoryProvider
) : BaseViewModel() {

    private var pageInfo: PageInfoModel? = null

    fun getGists(login: String): LiveData<PagedList<ProfileGistModel>> {
        val dataSourceFactory = reposProvider.getGists(login)
        val config = PagedList.Config.Builder()
                .setPrefetchDistance(com.fastaccess.github.utils.PRE_FETCH_SIZE)
                .setPageSize(com.fastaccess.github.utils.PAGE_SIZE)
                .build()
        return LivePagedListBuilder(dataSourceFactory, config)
                .build()
    }

    fun loadGists(login: String, reload: Boolean = false) {
        if (reload) {
            pageInfo = null
        }
        val pageInfo = pageInfo
        if (!reload && (pageInfo != null && !pageInfo.hasNextPage)) return
        add(callApi(reposProvider.getGistsFromRemote(login, if (hasNext()) pageInfo?.endCursor else null))
                .subscribe({
                    this.pageInfo = it.pageInfo
                    postCounter(it.totalCount)
                }, { it.printStackTrace() }))
    }

    fun hasNext() = pageInfo?.hasNextPage == true
}