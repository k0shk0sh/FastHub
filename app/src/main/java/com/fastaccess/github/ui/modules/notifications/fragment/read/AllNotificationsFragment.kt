package com.fastaccess.github.ui.modules.notifications.fragment.read

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fastaccess.github.R
import com.fastaccess.github.base.extensions.isConnected
import com.fastaccess.github.extensions.isTrue
import com.fastaccess.github.extensions.observeNotNull
import com.fastaccess.github.base.viewmodel.ViewModelProviders
import com.fastaccess.github.ui.adapter.AllNotificationsAdapter
import com.fastaccess.github.ui.modules.notifications.NotificationPagerFragment

import javax.inject.Inject

/**
 * Created by Kosh on 04.11.18.
 */
class AllNotificationsFragment : com.fastaccess.github.base.BaseFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(AllNotificationsViewModel::class.java) }
    private val adapter by lazy { AllNotificationsAdapter() }

    override fun viewModel(): com.fastaccess.github.base.BaseViewModel? = viewModel
    override fun layoutRes(): Int = R.layout.simple_refresh_list_layout

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        recyclerView.adapter = adapter
        recyclerView.setEmptyView(emptyLayout)
        fastScroller.attachRecyclerView(recyclerView, (parentFragment as? NotificationPagerFragment)?.view?.findViewById(R.id.appBar))
        if (savedInstanceState == null) isConnected().isTrue { viewModel.loadNotifications() }
        swipeRefresh.setOnRefreshListener {
            if (isConnected()) {
                viewModel.loadNotifications()
            } else {
                swipeRefresh.isRefreshing = false
            }
        }
        listenToChanges()
    }

    private fun listenToChanges() {
        viewModel.data.observeNotNull(this) {
            adapter.submitList(it)
        }
    }

    companion object {
        fun newInstance() = AllNotificationsFragment()
    }
}