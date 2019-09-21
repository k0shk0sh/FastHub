package com.fastaccess.github.ui.modules.feed.fragment.viewmodel

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.fastaccess.data.persistence.models.FeedModel
import com.fastaccess.data.repository.FeedsRepository
import com.fastaccess.github.usecase.feed.FeedsUseCase
import javax.inject.Inject

/**
 * Created by Kosh on 20.10.18.
 */
class FeedsViewModel @Inject constructor(
    private val provider: FeedsRepository,
    private val usecase: FeedsUseCase
) : com.fastaccess.github.base.BaseViewModel() {

    private var currentPage = 0
    private var isLastPage = false

    fun feeds(): LiveData<PagedList<FeedModel>> {
        val dataSourceFactory = provider.getReceivedEventAsLiveData()
        val config = PagedList.Config.Builder()
            .setPrefetchDistance(com.fastaccess.github.base.utils.PRE_FETCH_SIZE)
            .setPageSize(com.fastaccess.github.base.utils.PAGE_SIZE)
            .build()
        return LivePagedListBuilder(dataSourceFactory, config)
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
        justSubscribe(usecase.buildObservable()
            .doOnNext { isLastPage = it.last == currentPage })
    }

    fun hasNext() = isLastPage
}