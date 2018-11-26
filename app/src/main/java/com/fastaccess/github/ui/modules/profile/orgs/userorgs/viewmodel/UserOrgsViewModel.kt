package com.fastaccess.github.ui.modules.profile.orgs.userorgs.viewmodel

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.fastaccess.data.model.PageInfoModel
import com.fastaccess.data.persistence.models.OrganizationModel
import com.fastaccess.data.repository.OrgRepositoryProvider
import com.fastaccess.github.base.BaseViewModel
import javax.inject.Inject

/**
 * Created by Kosh on 2018-11-26.
 */
class UserOrgsViewModel @Inject constructor(
    private val provider: OrgRepositoryProvider
) : BaseViewModel() {

    private var pageInfo: PageInfoModel? = null

    fun getOrgs(): LiveData<PagedList<OrganizationModel>> {
        val dataSourceFactory = provider.getOrgs()
        val config = PagedList.Config.Builder()
            .setPrefetchDistance(com.fastaccess.github.utils.PRE_FETCH_SIZE)
            .setPageSize(com.fastaccess.github.utils.PAGE_SIZE)
            .build()
        return LivePagedListBuilder(dataSourceFactory, config)
            .build()
    }

    fun loadOrgs(reload: Boolean = false) {
        val pageInfo = pageInfo
        if (!reload && (pageInfo != null && !pageInfo.hasNextPage)) return
        val cursor = if (hasNext()) pageInfo?.endCursor else null

        justSubscribe(callApi(provider.getOrgFromRemote(cursor))
            .doOnNext {
                this.pageInfo = it.pageInfo
            })
    }

    fun hasNext() = pageInfo?.hasNextPage == true
}