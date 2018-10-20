package com.fastaccess.github.ui.modules.profile.feeds.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.fastaccess.data.persistence.models.FeedModel
import com.fastaccess.data.repository.FeedsRepositoryProvider
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.usecase.user.UserFeedsUseCase
import javax.inject.Inject

/**
 * Created by Kosh on 20.10.18.
 */
class ProfileFeedsViewModel @Inject constructor(
        private val provider: FeedsRepositoryProvider,
        private val usecase: UserFeedsUseCase
) : BaseViewModel() {

    private var currentPage = 0
    private var isLastPage = false
    val loadMoreLiveData = MutableLiveData<Boolean>()


    fun feeds(login: String): LiveData<PagedList<FeedModel>> {
        val dataSourceFactory = provider.getFeeds(login)
        val config = PagedList.Config.Builder()
                .setPrefetchDistance(15)
                .setPageSize(30)
                .build()
        return LivePagedListBuilder(dataSourceFactory, config)
                .setBoundaryCallback(ProfileFeedsBoundary(loadMoreLiveData))
                .build()
    }

    fun loadFeeds(login: String, reload: Boolean = false) {
        if (reload) {
            currentPage = 0
            isLastPage = false
        }
        currentPage++
        if (!reload && isLastPage) return
        usecase.page = currentPage
        usecase.login = login
        add(callApi(usecase.buildObservable())
                .subscribe({
                    isLastPage = it.last == currentPage
                }, ::println))
    }

    fun hasNext() = isLastPage
}

class ProfileFeedsBoundary(private val loadMoreLiveData: MutableLiveData<Boolean>) : PagedList.BoundaryCallback<FeedModel>() {
    override fun onItemAtEndLoaded(itemAtEnd: FeedModel) {
        super.onItemAtEndLoaded(itemAtEnd)
        loadMoreLiveData.postValue(true)
    }
}