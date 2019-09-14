package com.fastaccess.github.ui.modules.profile.feeds.viewmodel

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.fastaccess.data.persistence.models.FeedModel
import com.fastaccess.data.repository.FeedsRepository
import com.fastaccess.github.usecase.user.UserFeedsUseCase
import javax.inject.Inject

/**
 * Created by Kosh on 20.10.18.
 */
class ProfileFeedsViewModel @Inject constructor(
    private val provider: FeedsRepository,
    private val usecase: UserFeedsUseCase
) : com.fastaccess.github.base.BaseViewModel() {

    private var currentPage = 0
    private var isLastPage = false

    fun feeds(login: String): LiveData<PagedList<FeedModel>> {
        val dataSourceFactory = provider.getFeeds(login)
        val config = PagedList.Config.Builder()
            .setPrefetchDistance(com.fastaccess.github.base.utils.PRE_FETCH_SIZE)
            .setPageSize(com.fastaccess.github.base.utils.PAGE_SIZE)
            .build()
        return LivePagedListBuilder(dataSourceFactory, config)
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