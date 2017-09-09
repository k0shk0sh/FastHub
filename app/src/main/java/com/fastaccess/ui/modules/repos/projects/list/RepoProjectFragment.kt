package com.fastaccess.ui.modules.repos.projects.list

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import butterknife.BindView
import com.fastaccess.R
import com.fastaccess.data.dao.ProjectsModel
import com.fastaccess.data.dao.types.IssueState
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.ui.adapter.ProjectsAdapter
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.widgets.StateLayout
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller

/**
 * Created by kosh on 09/09/2017.
 */

class RepoProjectFragment : BaseFragment<RepoProjectMvp.View, RepoProjectPresenter>(), RepoProjectMvp.View {

    @BindView(R.id.recycler) lateinit var recycler: DynamicRecyclerView
    @BindView(R.id.refresh) lateinit var refresh: SwipeRefreshLayout
    @BindView(R.id.stateLayout) lateinit var stateLayout: StateLayout
    @BindView(R.id.fastScroller) lateinit var fastScroller: RecyclerViewFastScroller
    private var onLoadMore: OnLoadMore<IssueState>? = null
    private val adapter by lazy { ProjectsAdapter(presenter.getProjects()) }


    override fun providePresenter(): RepoProjectPresenter = RepoProjectPresenter()

    override fun onNotifyAdapter(items: List<ProjectsModel>?, page: Int) {
        hideProgress()
        if (items == null || items.isEmpty()) {
            adapter.clear()
            return
        }
        if (page <= 1) {
            adapter.insertItems(items)
        } else {
            adapter.addItems(items)
        }
    }

    override fun getLoadMore(): OnLoadMore<IssueState> {
        if (onLoadMore == null) {
            onLoadMore = OnLoadMore<IssueState>(presenter)
        }
        onLoadMore!!.parameter = getState()
        return onLoadMore!!
    }

    override fun fragmentLayout(): Int = R.layout.micro_grid_refresh_list

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        stateLayout.setEmptyText(R.string.no_projects)
        stateLayout.setOnReloadListener({ presenter.onCallApi(1, getState()) })
        refresh.setOnRefreshListener({ presenter.onCallApi(1, getState()) })
        recycler.setEmptyView(stateLayout, refresh)
        getLoadMore().initialize(presenter.currentPage, presenter
                .previousTotal)
        adapter.listener = presenter
        recycler.adapter = adapter
        recycler.addDivider()
        recycler.addOnScrollListener(getLoadMore())
        fastScroller.attachRecyclerView(recycler)
        if (presenter.getProjects().isEmpty() && !presenter.isApiCalled) {
            presenter.onFragmentCreate(arguments)
            presenter.onCallApi(1, getState())
        }
    }

    override fun showProgress(@StringRes resId: Int) {
        refresh.isRefreshing = true
        stateLayout.showProgress()
    }

    override fun hideProgress() {
        refresh.isRefreshing = false
        stateLayout.hideProgress()
    }

    override fun showErrorMessage(message: String) {
        showReload()
        super.showErrorMessage(message)
    }

    override fun showMessage(titleRes: Int, msgRes: Int) {
        showReload()
        super.showMessage(titleRes, msgRes)
    }

    override fun onScrollTop(index: Int) {
        super.onScrollTop(index)
        if (recycler != null) {
            recycler.scrollToPosition(0)
        }
    }

    override fun onDestroyView() {
        recycler.removeOnScrollListener(getLoadMore())
        super.onDestroyView()
    }

    private fun showReload() {
        hideProgress()
        stateLayout.showReload(adapter.itemCount)
    }

    private fun getState(): IssueState = arguments.getSerializable(BundleConstant.EXTRA_TYPE) as IssueState

    companion object {
        fun newInstance(login: String, repoId: String, state: IssueState): RepoProjectFragment {
            val fragment = RepoProjectFragment()
            fragment.arguments = Bundler.start()
                    .put(BundleConstant.ID, repoId)
                    .put(BundleConstant.EXTRA, login)
                    .put(BundleConstant.EXTRA_TYPE, state)
                    .end()
            return fragment
        }
    }
}