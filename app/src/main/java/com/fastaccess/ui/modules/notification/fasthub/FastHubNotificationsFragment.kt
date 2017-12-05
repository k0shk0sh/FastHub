package com.fastaccess.ui.modules.notification.fasthub

import android.os.Bundle
import android.view.View
import butterknife.BindView
import com.fastaccess.R
import com.fastaccess.data.dao.model.FastHubNotification
import com.fastaccess.ui.adapter.FastHubNotificationsAdapter
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.modules.main.notifications.FastHubNotificationDialog
import com.fastaccess.ui.widgets.AppbarRefreshLayout
import com.fastaccess.ui.widgets.StateLayout
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller

/**
 * Created by Kosh on 19.11.17.
 */
class FastHubNotificationsFragment : BaseFragment<FastHubNotificationsMvp.View, FastHubNotificationsPresenter>(), FastHubNotificationsMvp.View {

    @BindView(R.id.recycler) lateinit var recycler: DynamicRecyclerView
    @BindView(R.id.refresh) lateinit var refresh: AppbarRefreshLayout
    @BindView(R.id.stateLayout) lateinit var stateLayout: StateLayout
    @BindView(R.id.fastScroller) lateinit var fastScroller: RecyclerViewFastScroller
    private val adapter by lazy { FastHubNotificationsAdapter(presenter.getData()) }

    override fun fragmentLayout(): Int = R.layout.small_grid_refresh_list

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        adapter.listener = this
        stateLayout.setEmptyText(R.string.no_notifications)
        recycler.setEmptyView(stateLayout, refresh)
        recycler.addDivider()
        refresh.setOnRefreshListener { presenter.load() }
        stateLayout.setOnReloadListener { presenter.load() }
        if (savedInstanceState == null) {
            stateLayout.showProgress()
            presenter.load()
        }
        recycler.adapter = adapter
        fastScroller.attachRecyclerView(recycler)
    }

    override fun providePresenter(): FastHubNotificationsPresenter = FastHubNotificationsPresenter()

    override fun notifyAdapter(items: List<FastHubNotification>?) {
        refresh.isRefreshing = false
        stateLayout.hideProgress()
        if (items != null) {
            adapter.insertItems(items)
        } else {
            adapter.clear()
        }
    }

    override fun onItemClick(position: Int, v: View?, item: FastHubNotification) = FastHubNotificationDialog.show(childFragmentManager, item)

    override fun onItemLongClick(position: Int, v: View?, item: FastHubNotification) {}
}