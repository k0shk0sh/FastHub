package com.fastaccess.github.ui.modules.feed.fragment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.fastaccess.data.persistence.models.FeedModel
import com.fastaccess.data.repository.FeedsRepositoryProvider
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.usecase.feed.FeedsUseCase
import javax.inject.Inject

/**
 * Created by Kosh on 20.10.18.
 */
class FeedsViewModel @Inject constructor(
        private val provider: FeedsRepositoryProvider,
        private val usecase: FeedsUseCase
) : BaseViewModel() {

    private var currentPage = 0
    private var isLastPage = false
    val loadMoreLiveData = MutableLiveData<Boolean>()


    fun feeds(): LiveData<PagedList<FeedModel>> {
        val dataSourceFactory = provider.getReceivedEventAsLiveData()
        val config = PagedList.Config.Builder()
                .setPrefetchDistance(15)
                .setPageSize(30)
                .build()
        return LivePagedListBuilder(dataSourceFactory, config)
                .setBoundaryCallback(FeedsBoundary(loadMoreLiveData))
                .build()
    }

    fun loadFeeds(reload: Boolean = false) {
        if (reload) {
            currentPage = 0
            isLastPage = false
        }
        currentPage++
        if (!reload && isLastPage) return
        usecase.page = currentPage
        add(callApi(usecase.buildObservable())
                .subscribe({
                    isLastPage = it.last == currentPage
                }, ::println))
    }

    fun hasNext() = isLastPage
}

class FeedsBoundary(private val loadMoreLiveData: MutableLiveData<Boolean>) : PagedList.BoundaryCallback<FeedModel>() {
    override fun onItemAtEndLoaded(itemAtEnd: FeedModel) {
        super.onItemAtEndLoaded(itemAtEnd)
        loadMoreLiveData.postValue(true)
    }
}