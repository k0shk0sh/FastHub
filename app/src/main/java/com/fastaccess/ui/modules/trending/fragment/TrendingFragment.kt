package com.fastaccess.ui.modules.trending.fragment

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import com.fastaccess.R
import com.fastaccess.data.dao.kot.TrendingResponse
import com.fastaccess.helper.Logger
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.widgets.StateLayout
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView
import icepick.State

/**
 * Created by Kosh on 30 May 2017, 11:37 PM
 */

class TrendingFragment : BaseFragment<TrendingFragmentMvp.View, TrendingFragmentPresenter>(), TrendingFragmentMvp.View {

    val recycler by lazy { view!!.findViewById(R.id.recycler) as DynamicRecyclerView }
    val refresh by lazy { view!!.findViewById(R.id.refresh) as SwipeRefreshLayout }
    val stateLayout by lazy { view!!.findViewById(R.id.stateLayout) as StateLayout }

    @State var lang: String? = null
    @State var since: String? = null

    override fun providePresenter(): TrendingFragmentPresenter {
        return TrendingFragmentPresenter()
    }

    override fun fragmentLayout(): Int {
        return R.layout.micro_grid_refresh_list
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        stateLayout.setEmptyText(R.string.no_trending)
        recycler.setEmptyView(stateLayout, refresh)
        refresh.setOnRefreshListener { onCallApi() }
        stateLayout.setOnReloadListener { onCallApi() }
        //TODO
    }

    override fun onNotifyAdapter(items: List<TrendingResponse>) {
        hideProgress()
        Logger.e(items)
    }

    override fun onSetQuery(lang: String, since: String) {
        this.lang = lang
        this.since = since
        //TODO CLEAR ADAPTER
        presenter.onCallApi(lang, since)
    }

    override fun showProgress(resId: Int) {
        refresh.isRefreshing = true
        stateLayout.hideProgress()
    }

    override fun hideProgress() {
        refresh.isRefreshing = false
        stateLayout.hideProgress()
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
        if (lang != null && since != null) presenter.onCallApi(lang!!, since!!)
    }
}