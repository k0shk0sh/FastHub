package com.fastaccess.github.ui.modules.profile.followersandfollowings.viewmodel

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.fastaccess.data.model.PageInfoModel
import com.fastaccess.data.persistence.models.FollowingFollowerModel
import com.fastaccess.data.repository.FollowersFollowingRepository
import javax.inject.Inject

/**
 * Created by Kosh on 15.10.18.
 */
class FollowersFollowingViewModel @Inject constructor(
        private val provider: FollowersFollowingRepository
) : com.fastaccess.github.base.BaseViewModel() {

    private var pageInfo: PageInfoModel? = null

    fun getUsers(login: String, isFollowers: Boolean): LiveData<PagedList<FollowingFollowerModel>> {
        val dataSourceFactory = provider.getFollowersOrFollowing(login, isFollowers)
        val config = PagedList.Config.Builder()
                .setPrefetchDistance(com.fastaccess.github.base.utils.PRE_FETCH_SIZE)
                .setPageSize(com.fastaccess.github.base.utils.PAGE_SIZE)
                .build()
        return LivePagedListBuilder(dataSourceFactory, config)
                .build()
    }

    fun loadUsers(login: String, isFollowers: Boolean, reload: Boolean = false) {
        if (reload) {
            pageInfo = null
        }
        val pageInfo = pageInfo
        if (!reload && (pageInfo != null && !pageInfo.hasNextPage)) return
        val cursor = if (hasNext()) pageInfo?.endCursor else null
        val observable = if (isFollowers) {
            provider.getFollowersFromRemote(login, cursor)
        } else {
            provider.getFollowingFromRemote(login, cursor)
        }
        add(callApi(observable)
                .subscribe({
                    this.pageInfo = it.pageInfo
                    postCounter(it.totalCount)
                }, { it.printStackTrace() }))
    }

    fun hasNext() = pageInfo?.hasNextPage == true
}