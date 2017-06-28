package com.fastaccess.ui.modules.profile.events

import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.StringRes
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import com.fastaccess.R
import com.fastaccess.data.dao.GitCommitModel
import com.fastaccess.data.dao.NameParser
import com.fastaccess.data.dao.SimpleUrlsModel
import com.fastaccess.data.dao.model.Event
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.provider.scheme.SchemeParser
import com.fastaccess.ui.adapter.FeedsAdapter
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.modules.repos.code.commit.details.CommitPagerActivity
import com.fastaccess.ui.widgets.StateLayout
import com.fastaccess.ui.widgets.dialog.ListDialogView
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView
import java.util.*

/**
 * Created by Kosh on 11 Nov 2016, 12:36 PM
 */

open class ProfileEventsFragment : BaseFragment<ProfileEventsMvp.View, ProfileEventsPresenter>(), ProfileEventsMvp.View {

    val recycler: DynamicRecyclerView  by lazy { view!!.findViewById<DynamicRecyclerView>(R.id.recycler) }
    val refresh: SwipeRefreshLayout by lazy { view!!.findViewById<SwipeRefreshLayout>(R.id.refresh) }
    val stateLayout: StateLayout by lazy { view!!.findViewById<StateLayout>(R.id.stateLayout) }

    val adapter by lazy { FeedsAdapter(presenter.getEvents(), true) }
    private var onLoadMore: OnLoadMore<String>? = null

    override fun fragmentLayout(): Int {
        return R.layout.micro_grid_refresh_list
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        stateLayout.setEmptyText(R.string.no_feeds)
        stateLayout.setOnReloadListener(this)
        refresh.setOnRefreshListener(this)
        recycler.setEmptyView(stateLayout, refresh)
        adapter.listener = presenter
        getLoadMore().setCurrent_page(presenter.currentPage, presenter.previousTotal)
        recycler.adapter = adapter
        recycler.addOnScrollListener(getLoadMore())
        recycler.addDivider()
        if (presenter.getEvents().isEmpty() && !presenter.isApiCalled) {
            onRefresh()
        }
    }

    override fun onRefresh() {
        presenter.onCallApi(1, arguments.getString(BundleConstant.EXTRA))
    }

    override fun onNotifyAdapter(events: List<Event>?, page: Int) {
        hideProgress()
        if (events == null || events.isEmpty()) {
            adapter.clear()
            return
        }
        if (page <= 1) {
            adapter.insertItems(events)
        } else {
            adapter.addItems(events)
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

    override fun onOpenRepoChooser(models: ArrayList<SimpleUrlsModel>) {
        val dialogView = ListDialogView<SimpleUrlsModel>()
        dialogView.initArguments(getString(R.string.repo_chooser), models)
        dialogView.show(childFragmentManager, "ListDialogView")
    }

    override fun providePresenter(): ProfileEventsPresenter {
        return ProfileEventsPresenter()
    }

    override fun getLoadMore(): OnLoadMore<String> {
        if (onLoadMore == null) {
            onLoadMore = OnLoadMore(presenter)
        }
        onLoadMore!!.parameter = arguments.getString(BundleConstant.EXTRA)
        return onLoadMore as OnLoadMore<String>
    }

    override fun onDestroyView() {
        recycler.removeOnScrollListener(getLoadMore())
        super.onDestroyView()
    }

    override fun onClick(view: View) {
        onRefresh()
    }

    override fun onItemSelected(item: Parcelable) {
        if (item is SimpleUrlsModel) {
            SchemeParser.launchUri(context, Uri.parse(item.item))
        } else if (item is GitCommitModel) {
            val nameParser = NameParser(item.url)
            val intent = CommitPagerActivity.createIntent(context, nameParser.name,
                    nameParser.username, item.sha, true)
            context.startActivity(intent)
        }
    }

    override fun onScrollTop(index: Int) {
        super.onScrollTop(index)
        recycler.scrollToPosition(0)
    }

    override fun onOpenCommitChooser(commits: List<GitCommitModel>) {
        val dialogView = ListDialogView<GitCommitModel>()
        dialogView.initArguments(getString(R.string.commits), commits)
        dialogView.show(childFragmentManager, "ListDialogView")
    }

    private fun showReload() {
        hideProgress()
        stateLayout.showReload(adapter.itemCount)
    }

    companion object {
        fun newInstance(login: String): ProfileEventsFragment {
            val fragment = ProfileEventsFragment()
            fragment.arguments = Bundler.start().put(BundleConstant.EXTRA, login).end()
            return fragment
        }
    }
}
