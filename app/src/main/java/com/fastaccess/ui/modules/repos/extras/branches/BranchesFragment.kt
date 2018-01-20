package com.fastaccess.ui.modules.repos.extras.branches

import android.content.Context
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import butterknife.BindView
import com.fastaccess.R
import com.fastaccess.data.dao.BranchesModel
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.ui.adapter.BranchesAdapter
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.modules.repos.extras.branches.pager.BranchesPagerListener
import com.fastaccess.ui.widgets.StateLayout
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller

/**
 * Created by Kosh on 06 Jul 2017, 9:48 PM
 */
class BranchesFragment : BaseFragment<BranchesMvp.View, BranchesPresenter>(), BranchesMvp.View {

    @BindView(R.id.recycler) lateinit var recycler: DynamicRecyclerView
    @BindView(R.id.refresh) lateinit var refresh: SwipeRefreshLayout
    @BindView(R.id.stateLayout) lateinit var stateLayout: StateLayout
    @BindView(R.id.fastScroller) lateinit var fastScroller: RecyclerViewFastScroller

    private var onLoadMore: OnLoadMore<Boolean>? = null
    private var branchCallback: BranchesPagerListener? = null

    private val adapter by lazy { BranchesAdapter(presenter.branches, presenter) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        branchCallback = if (parentFragment is BranchesPagerListener) {
            parentFragment as BranchesPagerListener
        } else context as BranchesPagerListener
    }

    override fun onDetach() {
        branchCallback = null
        super.onDetach()
    }

    override fun fragmentLayout(): Int = R.layout.small_grid_refresh_list

    override fun providePresenter(): BranchesPresenter = BranchesPresenter()

    override fun onNotifyAdapter(branches: ArrayList<BranchesModel>, page: Int) {
        hideProgress()
        if (page == 1) adapter.insertItems(branches)
        else adapter.addItems(branches)
    }

    override fun onBranchSelected(item: BranchesModel?) {
        branchCallback?.onItemSelect(item!!)
    }

    override fun getLoadMore(): OnLoadMore<Boolean> {
        if (onLoadMore == null) {
            onLoadMore = OnLoadMore(presenter)
        }
        return onLoadMore!!
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        getLoadMore().initialize(presenter.currentPage, presenter.previousTotal)
        stateLayout.setEmptyText(R.string.no_branches)
        refresh.setOnRefreshListener { presenter.onCallApi(1, null) }
        recycler.setEmptyView(stateLayout, refresh)
        stateLayout.setOnReloadListener { presenter.onCallApi(1, null) }
        recycler.adapter = adapter
        recycler.addOnScrollListener(getLoadMore())
        recycler.addDivider()
        if (savedInstanceState == null) {
            arguments?.let { presenter.onFragmentCreated(it) }
        }
        fastScroller.attachRecyclerView(recycler)
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

    companion object {
        fun newInstance(login: String, repoId: String, branch: Boolean): BranchesFragment {
            val fragment = BranchesFragment()
            fragment.arguments = Bundler.start()
                    .put(BundleConstant.ID, repoId)
                    .put(BundleConstant.EXTRA, login)
                    .put(BundleConstant.EXTRA_TYPE, branch)
                    .end()
            return fragment
        }
    }
}