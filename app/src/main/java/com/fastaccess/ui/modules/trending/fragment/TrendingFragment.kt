package com.fastaccess.ui.modules.trending.fragment

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import com.evernote.android.state.State
import com.fastaccess.R
import com.fastaccess.data.dao.TrendingModel
import com.fastaccess.ui.adapter.TrendingAdapter
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.widgets.StateLayout
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller

/**
 * Created by Kosh on 30 May 2017, 11:37 PM
 */

class TrendingFragment : BaseFragment<TrendingFragmentMvp.View, TrendingFragmentPresenter>(), TrendingFragmentMvp.View {

    val recycler: DynamicRecyclerView  by lazy { view!!.findViewById<DynamicRecyclerView>(R.id.recycler) }
    val refresh: SwipeRefreshLayout by lazy { view!!.findViewById<SwipeRefreshLayout>(R.id.refresh) }
    val stateLayout: StateLayout by lazy { view!!.findViewById<StateLayout>(R.id.stateLayout) }
    val fastScroller: RecyclerViewFastScroller by lazy { view!!.findViewById<RecyclerViewFastScroller>(R.id.fastScroller) }
    val adapter by lazy { TrendingAdapter(presenter.getTendingList()) }

    @State var lang: String = ""
    @State var since: String = ""

    override fun providePresenter(): TrendingFragmentPresenter {
        return TrendingFragmentPresenter()
    }

    override fun fragmentLayout(): Int {
        return R.layout.small_grid_refresh_list
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        stateLayout.setEmptyText(R.string.no_trending)
        recycler.setEmptyView(stateLayout, refresh)
        refresh.setOnRefreshListener { onCallApi() }
        stateLayout.setOnReloadListener { onCallApi() }
        adapter.listener = presenter
        recycler.adapter = adapter
        fastScroller.attachRecyclerView(recycler)
    }

    override fun onNotifyAdapter(items: TrendingModel) {
        adapter.addItem(items)
    }

    override fun onSetQuery(lang: String, since: String) {
        this.lang = lang
        this.since = since
        adapter.clear()
        presenter.onCallApi(lang, since)
    }

    override fun showProgress(resId: Int) {
        refresh.isRefreshing = true
        stateLayout.showProgress()
    }

    override fun hideProgress() {
        refresh.isRefreshing = false
        stateLayout.hideProgress()
        stateLayout.showReload(adapter.itemCount)
    }

    override fun showMessage(titleRes: Int, msgRes: Int) {
        hideProgress()
        super.showMessage(titleRes, msgRes)
    }

    override fun showMessage(titleRes: String, msgRes: String) {
        hideProgress()
        super.showMessage(titleRes, msgRes)
    }

    override fun showErrorMessage(msgRes: String) {
        hideProgress()
        super.showErrorMessage(msgRes)
    }

    private fun onCallApi() {
        if (true) presenter.onCallApi(lang, since)
    }
}