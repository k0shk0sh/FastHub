package com.fastaccess.github.ui.modules.profile.starred.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.fastaccess.data.model.PageInfoModel
import com.fastaccess.data.persistence.models.ProfileStarredRepoModel
import com.fastaccess.data.repository.UserStarredReposRepositoryProvider
import com.fastaccess.github.base.BaseViewModel
import javax.inject.Inject

/**
 * Created by Kosh on 13.10.18.
 */
class ProfileStarredReposViewModel @Inject constructor(
        private val reposProvider: UserStarredReposRepositoryProvider
) : BaseViewModel() {

    private var pageInfo: PageInfoModel? = null
    val loadMoreLiveData = MutableLiveData<Boolean>()


    fun starredRepos(login: String): LiveData<PagedList<ProfileStarredRepoModel>> {
        val dataSourceFactory = reposProvider.getStarredRepos(login)
        val config = PagedList.Config.Builder()
                .setPrefetchDistance(15)
                .setPageSize(30)
                .build()
        return LivePagedListBuilder(dataSourceFactory, config)
                .setBoundaryCallback(ProfileStarredRepoBoundary(loadMoreLiveData))
                .build()
    }

    fun loadStarredRepos(login: String, reload: Boolean = false) {
        if (reload) {
            pageInfo = null
        }
        val pageInfo = pageInfo
        if (!reload && (pageInfo != null && !pageInfo.hasNextPage)) return
        add(callApi(reposProvider.getStarredReposFromRemote(login, if (hasNext()) pageInfo?.endCursor else null))
                .subscribe({
                    this.pageInfo = it.pageInfo
                    postCounter(it.totalCount)
                }, { it.printStackTrace() }))
    }

    fun hasNext() = pageInfo?.hasNextPage == true
}

class ProfileStarredRepoBoundary(private val loadMoreLiveData: MutableLiveData<Boolean>) : PagedList.BoundaryCallback<ProfileStarredRepoModel>() {
    override fun onItemAtEndLoaded(itemAtEnd: ProfileStarredRepoModel) {
        super.onItemAtEndLoaded(itemAtEnd)
        loadMoreLiveData.postValue(true)
    }
}