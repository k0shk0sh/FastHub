package com.fastaccess.github.ui.modules.notifications.fragment.unread.viewmodel

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.fastaccess.data.persistence.models.NotificationModel
import com.fastaccess.data.repository.NotificationRepositoryProvider
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.usecase.notification.NotificationUseCase
import javax.inject.Inject

/**
 * Created by Kosh on 31.10.18.
 */
class UnreadNotificationsViewModel @Inject constructor(
    private val provider: NotificationRepositoryProvider,
    private val usecase: NotificationUseCase
) : BaseViewModel() {

    private var currentPage = 0
    private var isLastPage = false

    fun notifications(): LiveData<PagedList<NotificationModel>> {
        val dataSourceFactory = provider.getNotifications(true)
        val config = PagedList.Config.Builder()
            .setPrefetchDistance(com.fastaccess.github.utils.PRE_FETCH_SIZE)
            .setPageSize(com.fastaccess.github.utils.PAGE_SIZE)
            .build()
        return LivePagedListBuilder(dataSourceFactory, config)
            .build()
    }

    fun loadNotifications(reload: Boolean = false) {
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

    fun markAsRead(id: String) = add(provider.markAsRead(id).subscribe())
    fun markAllAsRead() = add(provider.markAllAsRead().subscribe({}, { it.printStackTrace() }))
    fun getAllUnreadNotifications() = provider.getAllNotificationsAsMaybe(true)

    fun hasNext() = isLastPage
}