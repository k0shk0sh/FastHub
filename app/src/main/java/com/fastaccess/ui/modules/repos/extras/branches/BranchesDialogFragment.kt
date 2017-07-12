package com.fastaccess.ui.modules.repos.extras.branches

import android.content.Context
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import com.fastaccess.R
import com.fastaccess.data.dao.BranchesModel
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.ui.adapter.BranchesAdapter
import com.fastaccess.ui.base.BaseDialogFragment
import com.fastaccess.ui.widgets.StateLayout
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView

/**
 * Created by Kosh on 06 Jul 2017, 9:48 PM
 */
class BranchesDialogFragment : BaseDialogFragment<BranchesMvp.View, BranchesPresenter>(), BranchesMvp.View {
    val recycler: DynamicRecyclerView  by lazy { view!!.findViewById<DynamicRecyclerView>(R.id.recycler) }
    val refresh: SwipeRefreshLayout by lazy { view!!.findViewById<SwipeRefreshLayout>(R.id.refresh) }
    val stateLayout: StateLayout by lazy { view!!.findViewById<StateLayout>(R.id.stateLayout) }
    private var onLoadMore: OnLoadMore<Any>? = null
    private var branchCallback: BranchesMvp.BranchSelectionListener? = null

    val adapter by lazy { BranchesAdapter(presenter.branches, presenter) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is BranchesMvp.BranchSelectionListener) {
            branchCallback = parentFragment as BranchesMvp.BranchSelectionListener
        } else branchCallback = context as BranchesMvp.BranchSelectionListener
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
        branchCallback?.onBranchSelected(item!!)
        dismiss()
    }

    override fun getLoadMore(): OnLoadMore<Any> {
        if (onLoadMore == null) {
            onLoadMore = OnLoadMore(presenter)
        }
        return onLoadMore!!
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        stateLayout.setEmptyText(R.string.no_branches)
        refresh.setOnRefreshListener { presenter.onCallApi(1, null) }
        recycler.setEmptyView(stateLayout, refresh)
        stateLayout.setOnReloadListener { presenter.onCallApi(1, null) }
        recycler.adapter = adapter
        recycler.addDivider()
        if (savedInstanceState == null) {
            presenter.onFragmentCreated(arguments)
        }
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
        fun newInstance(login: String, repoId: String): BranchesDialogFragment {
            val fragment = BranchesDialogFragment()
            fragment.arguments = Bundler.start()
                    .put(BundleConstant.ID, repoId)
                    .put(BundleConstant.EXTRA, login)
                    .end()
            return fragment
        }
    }
}