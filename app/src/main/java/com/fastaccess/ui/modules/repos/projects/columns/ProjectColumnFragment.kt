package com.fastaccess.ui.modules.repos.projects.columns

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import butterknife.BindView
import butterknife.OnClick
import com.fastaccess.R
import com.fastaccess.data.dao.ProjectCardModel
import com.fastaccess.data.dao.ProjectColumnModel
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.ui.adapter.ColumnCardAdapter
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.StateLayout
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller

/**
 * Created by Hashemsergani on 11.09.17.
 */
class ProjectColumnFragment : BaseFragment<ProjectColumnMvp.View, ProjectColumnPresenter>(), ProjectColumnMvp.View {

    @BindView(R.id.recycler) lateinit var recycler: DynamicRecyclerView
    @BindView(R.id.refresh) lateinit var refresh: SwipeRefreshLayout
    @BindView(R.id.stateLayout) lateinit var stateLayout: StateLayout
    @BindView(R.id.fastScroller) lateinit var fastScroller: RecyclerViewFastScroller
    @BindView(R.id.columnName) lateinit var columnName: FontTextView
    @BindView(R.id.editColumnHolder) lateinit var editColumnHolder: View

    private var onLoadMore: OnLoadMore<Long>? = null
    private val adapter by lazy { ColumnCardAdapter(presenter.getCards(), isOwner()) }

    @OnClick(R.id.editColumn) fun onEditColumn() {}
    @OnClick(R.id.deleteColumn) fun onDeleteColumn() {}
    @OnClick(R.id.addCard) fun onAddCard() {}


    override fun onNotifyAdapter(items: List<ProjectCardModel>?, page: Int) {
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

    override fun getLoadMore(): OnLoadMore<Long> {
        if (onLoadMore == null) {
            onLoadMore = OnLoadMore<Long>(presenter)
        }
        onLoadMore!!.parameter = getColumn().id
        return onLoadMore!!
    }

    override fun providePresenter(): ProjectColumnPresenter = ProjectColumnPresenter()

    override fun fragmentLayout(): Int = R.layout.project_columns_layout

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        val column = getColumn()
        columnName.text = column.name
        refresh.setOnRefreshListener { presenter.onCallApi(1, column.id) }
        stateLayout.setOnReloadListener { presenter.onCallApi(1, column.id) }
        stateLayout.setEmptyText(R.string.no_cards)
        recycler.setEmptyView(stateLayout, refresh)
        getLoadMore().initialize(presenter.currentPage, presenter.previousTotal)
        adapter.listener = presenter
        recycler.adapter = adapter
        recycler.addOnScrollListener(getLoadMore())
        fastScroller.attachRecyclerView(recycler)
        if (presenter.getCards().isEmpty() && !presenter.isApiCalled) {
            presenter.onCallApi(1, column.id)
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
        recycler?.scrollToPosition(0)
    }

    override fun onDestroyView() {
        recycler.removeOnScrollListener(getLoadMore())
        super.onDestroyView()
    }

    private fun showReload() {
        hideProgress()
        stateLayout.showReload(adapter.itemCount)
    }

    private fun getColumn(): ProjectColumnModel = arguments.getParcelable(BundleConstant.ITEM)

    private fun isOwner(): Boolean = arguments.getBoolean(BundleConstant.EXTRA)

    companion object {
        fun newInstance(column: ProjectColumnModel, isCollaborator: Boolean): ProjectColumnFragment {
            val fragment = ProjectColumnFragment()
            fragment.arguments = Bundler.start()
                    .put(BundleConstant.ITEM, column)
                    .put(BundleConstant.EXTRA, isCollaborator)
                    .end()
            return fragment
        }
    }
}