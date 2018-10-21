package com.fastaccess.github.platform.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList

/**
 * Created by Kosh on 21.10.18.
 */
class LoadMoreBoundary<T>(private val loadMoreLiveData: MutableLiveData<Boolean>) : PagedList.BoundaryCallback<T>() {
    override fun onItemAtEndLoaded(itemAtEnd: T) {
        super.onItemAtEndLoaded(itemAtEnd)
        loadMoreLiveData.postValue(true)
    }
}