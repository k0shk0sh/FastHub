package com.fastaccess.github.ui.modules.notifications.fragment.unread

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fastaccess.github.base.viewmodel.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import com.fastaccess.github.R
import com.fastaccess.github.base.extensions.isConnected
import com.fastaccess.github.extensions.isTrue
import com.fastaccess.github.extensions.observeNotNull
import com.fastaccess.github.platform.works.MarkAsReadNotificationWorker
import com.fastaccess.github.ui.adapter.UnreadNotificationsAdapter
import com.fastaccess.github.base.adapter.CurrentState
import com.fastaccess.github.ui.modules.notifications.NotificationPagerFragment
import com.fastaccess.github.ui.modules.notifications.fragment.unread.viewmodel.UnreadNotificationsViewModel
import com.fastaccess.github.base.widget.recyclerview.SwipeToDeleteCallback

import javax.inject.Inject

/**
 * Created by Kosh on 21.10.18.
 */
class UnreadNotificationsFragment : com.fastaccess.github.base.BaseFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(UnreadNotificationsViewModel::class.java) }
    private val adapter by lazy { UnreadNotificationsAdapter() }

    override fun viewModel(): com.fastaccess.github.base.BaseViewModel? = viewModel
    override fun layoutRes(): Int = R.layout.simple_refresh_list_layout

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        recyclerView.adapter = adapter
        recyclerView.setEmptyView(emptyLayout)
        fastScroller.attachRecyclerView(recyclerView, (parentFragment as? NotificationPagerFragment)?.view?.findViewById(R.id.appBar))
        if (savedInstanceState == null) isConnected().isTrue { viewModel.loadNotifications(true) }
        swipeRefresh.setOnRefreshListener {
            if (isConnected()) {
                recyclerView.resetScrollState()
                viewModel.loadNotifications(true)
            } else {
                swipeRefresh.isRefreshing = false
            }
        }
        recyclerView.addOnLoadMore { isConnected().isTrue { viewModel.loadNotifications() } }
        listenToChanges()

        val swipeCallback = SwipeToDeleteCallback { viewHolder, _ ->
            adapter.getValue(viewHolder.adapterPosition)?.let {
                if (it.unread == true) {
                    MarkAsReadNotificationWorker.enqueue(it.id)
                    viewModel.markAsRead(it.id)
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    fun markAllAsRead() {
        addDisposal(viewModel.getAllUnreadNotifications()
            .subscribe({ list ->
                if (list.isNullOrEmpty()) return@subscribe
                list.map { it.id }.toTypedArray().let {
                    MarkAsReadNotificationWorker.enqueue(ids = it)
                }
                viewModel.markAllAsRead()
            }, {
                it.printStackTrace()
            })
        )
    }

    private fun listenToChanges() {

        viewModel.progress.observeNotNull(this) {
            adapter.currentState = if (it) CurrentState.LOADING else CurrentState.DONE
        }

        viewModel.notifications().observeNotNull(this) {
            adapter.currentState = CurrentState.DONE
            adapter.submitList(it)
        }
    }

    companion object {
        fun newInstance() = UnreadNotificationsFragment()
    }
}