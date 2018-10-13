package com.fastaccess.github.ui.modules.profile.repos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.fastaccess.data.model.PageInfoModel
import com.fastaccess.data.persistence.models.ProfileRepoModel
import com.fastaccess.data.repository.UserReposRepositoryProvider
import com.fastaccess.github.base.BaseViewModel
import javax.inject.Inject

/**
 * Created by Kosh on 08.10.18.
 */
class ProfileReposViewModel @Inject constructor(
        private val reposProvider: UserReposRepositoryProvider
) : BaseViewModel() {

    private var pageInfo: PageInfoModel? = null
    val loadMoreLiveData = MutableLiveData<Boolean>()


    fun repos(login: String): LiveData<PagedList<ProfileRepoModel>> {
        val dataSourceFactory = reposProvider.getRepos(login)
        val config = PagedList.Config.Builder()
                .setPrefetchDistance(15)
                .setPageSize(30)
                .build()
        return LivePagedListBuilder(dataSourceFactory, config)
                .setBoundaryCallback(ProfileRepoBoundary(loadMoreLiveData))
                .build()
    }

    fun loadRepos(login: String, reload: Boolean = false) {
        if (reload) {
            pageInfo = null
        }
        val pageInfo = pageInfo
        if (!reload && (pageInfo != null && !pageInfo.hasNextPage)) return
        add(callApi(reposProvider.getReposFromRemote(login, if (hasNext()) pageInfo?.endCursor else null))
                .subscribe({
                    this.pageInfo = it.pageInfo
                    postCounter(it.totalCount)
                }, { it.printStackTrace() }))
    }

    fun hasNext() = pageInfo?.hasNextPage == true
}

class ProfileRepoBoundary(private val loadMoreLiveData: MutableLiveData<Boolean>) : PagedList.BoundaryCallback<ProfileRepoModel>() {
    override fun onItemAtEndLoaded(itemAtEnd: ProfileRepoModel) {
        super.onItemAtEndLoaded(itemAtEnd)
        loadMoreLiveData.postValue(true)
    }
}