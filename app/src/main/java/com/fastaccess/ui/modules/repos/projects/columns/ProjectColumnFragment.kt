package com.fastaccess.ui.modules.repos.projects.columns

import android.content.Context
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
import com.fastaccess.helper.Logger
import com.fastaccess.helper.PrefGetter
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.ui.adapter.ColumnCardAdapter
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.modules.main.premium.PremiumActivity
import com.fastaccess.ui.modules.repos.projects.crud.ProjectCurdDialogFragment
import com.fastaccess.ui.modules.repos.projects.details.ProjectPagerMvp
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.StateLayout
import com.fastaccess.ui.widgets.dialog.MessageDialogView
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
    @BindView(R.id.editColumn) lateinit var editColumn: View
    @BindView(R.id.addCard) lateinit var addCard: View
    @BindView(R.id.deleteColumn) lateinit var deleteColumn: View

    private var onLoadMore: OnLoadMore<Long>? = null
    private val adapter by lazy { ColumnCardAdapter(presenter.getCards(), isOwner()) }
    private var pageCallback: ProjectPagerMvp.DeletePageListener? = null


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        pageCallback = when {
            parentFragment is ProjectPagerMvp.DeletePageListener -> parentFragment as ProjectPagerMvp.DeletePageListener
            context is ProjectPagerMvp.DeletePageListener -> context
            else -> null
        }
    }

    override fun onDetach() {
        pageCallback = null
        super.onDetach()
    }

    @OnClick(R.id.editColumn) fun onEditColumn() {
        if (canEdit()) {
            ProjectCurdDialogFragment.newInstance(getColumn().name)
                    .show(childFragmentManager, ProjectCurdDialogFragment.TAG)
        }
    }

    @OnClick(R.id.deleteColumn) fun onDeleteColumn() {
        if (canEdit()) {
            MessageDialogView.newInstance(getString(R.string.delete), getString(R.string.confirm_message),
                    false, MessageDialogView.getYesNoBundle(context!!))
                    .show(childFragmentManager, MessageDialogView.TAG)
        }
    }

    @OnClick(R.id.addCard) fun onAddCard() {
        if (canEdit()) {
            ProjectCurdDialogFragment.newInstance(isCard = true)
                    .show(childFragmentManager, ProjectCurdDialogFragment.TAG)
        }
    }

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
        addCard.visibility = if(isOwner()) View.VISIBLE else View.GONE
        deleteColumn.visibility = if(isOwner()) View.VISIBLE else View.GONE
        editColumn.visibility = if(isOwner()) View.VISIBLE else View.GONE
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
        recycler.scrollToPosition(0)
    }

    override fun onDestroyView() {
        recycler.removeOnScrollListener(getLoadMore())
        super.onDestroyView()
    }

    override fun onCreatedOrEdited(text: String, isCard: Boolean, position: Int) {
        Logger.e(text, isCard, position)
        if (!isCard) {
            columnName.text = text
            presenter.onEditOrDeleteColumn(text, getColumn())
        } else {
            if (position == -1) {
                presenter.createCard(text, getColumn().id)
            } else {
                presenter.editCard(text, adapter.getItem(position), position)
            }
        }
    }

    override fun onMessageDialogActionClicked(isOk: Boolean, bundle: Bundle?) {
        super.onMessageDialogActionClicked(isOk, bundle)
        if (isOk) {
            if (bundle != null) {
                if (bundle.containsKey(BundleConstant.ID)) {
                    val position = bundle.getInt(BundleConstant.ID)
                    presenter.onDeleteCard(position, adapter.getItem(position))
                } else {
                    presenter.onEditOrDeleteColumn(null, getColumn())
                }
            } else {
                presenter.onEditOrDeleteColumn(null, getColumn())
            }
        }
    }

    override fun deleteColumn() {
        pageCallback?.onDeletePage(getColumn())
        hideBlockingProgress()
    }

    override fun showBlockingProgress() {
        super.showProgress(0)
    }

    override fun hideBlockingProgress() {
        super.hideProgress()
    }

    override fun isOwner(): Boolean = arguments!!.getBoolean(BundleConstant.EXTRA)

    override fun onDeleteCard(position: Int) {
        if (canEdit()) {
            val yesNoBundle = MessageDialogView.getYesNoBundle(context!!)
            yesNoBundle.putInt(BundleConstant.ID, position)
            MessageDialogView.newInstance(getString(R.string.delete), getString(R.string.confirm_message),
                    false, yesNoBundle).show(childFragmentManager, MessageDialogView.TAG)
        }
    }

    override fun onEditCard(note: String?, position: Int) {
        if (canEdit()) {
            ProjectCurdDialogFragment.newInstance(note, true, position)
                    .show(childFragmentManager, ProjectCurdDialogFragment.TAG)
        }
    }

    override fun addCard(it: ProjectCardModel) {
        hideBlockingProgress()
        adapter.addItem(it, 0)
    }

    override fun updateCard(response: ProjectCardModel, position: Int) {
        hideBlockingProgress()
        adapter.swapItem(response, position)
    }

    override fun onRemoveCard(position: Int) {
        hideBlockingProgress()
        adapter.removeItem(position)
    }

    private fun showReload() {
        hideProgress()
        stateLayout.showReload(adapter.itemCount)
    }

    private fun getColumn(): ProjectColumnModel = arguments!!.getParcelable(BundleConstant.ITEM)

    private fun canEdit(): Boolean = if (PrefGetter.isProEnabled() || PrefGetter.isAllFeaturesUnlocked()) {
        true
    } else {
        PremiumActivity.startActivity(context!!)
        false
    }

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