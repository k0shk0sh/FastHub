package com.fastaccess.github.ui.modules.profile.gists.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
    val loadMoreLiveData = MutableLiveData<Boolean>()


    fun getGists(login: String): LiveData<PagedList<ProfileGistModel>> {
        val dataSourceFactory = reposProvider.getGists(login)
        val config = PagedList.Config.Builder()
                .setPrefetchDistance(15)
                .setPageSize(30)
                .build()
        return LivePagedListBuilder(dataSourceFactory, config)
                .setBoundaryCallback(ProfileGistsBoundary(loadMoreLiveData))
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

class ProfileGistsBoundary(private val loadMoreLiveData: MutableLiveData<Boolean>) : PagedList.BoundaryCallback<ProfileGistModel>() {
    override fun onItemAtEndLoaded(itemAtEnd: ProfileGistModel) {
        super.onItemAtEndLoaded(itemAtEnd)
        loadMoreLiveData.postValue(true)
    }
}