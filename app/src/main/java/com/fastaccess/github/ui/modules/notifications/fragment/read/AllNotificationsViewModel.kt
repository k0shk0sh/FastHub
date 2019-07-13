package com.fastaccess.github.ui.modules.notifications.fragment.read

import androidx.lifecycle.LiveData
import com.fastaccess.data.model.GroupedNotificationsModel
import com.fastaccess.data.repository.NotificationRepositoryProvider
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.usecase.notification.NotificationUseCase
import javax.inject.Inject

/**
 * Created by Kosh on 04.11.18.
 */
class AllNotificationsViewModel @Inject constructor(
    provider: NotificationRepositoryProvider,
    private val usecase: NotificationUseCase
) : BaseViewModel() {

    val data: LiveData<List<GroupedNotificationsModel>> = provider.getAllNotifications()

    fun loadNotifications() {
        usecase.all = true
        add(callApi(usecase.buildObservable())
            .subscribe({}, ::println))
    }
}