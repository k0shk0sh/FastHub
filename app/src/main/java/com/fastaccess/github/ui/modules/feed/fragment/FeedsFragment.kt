package com.fastaccess.github.ui.modules.feed.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.fastaccess.data.model.FragmentType
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.ui.adapter.ProfileFeedsAdapter
import com.fastaccess.github.ui.adapter.base.CurrentState
import com.fastaccess.github.ui.modules.feed.fragment.viewmodel.FeedsViewModel
import com.fastaccess.github.utils.extensions.addDivider
import com.fastaccess.github.utils.extensions.observeNotNull
import kotlinx.android.synthetic.main.appbar_center_title_layout.*
import kotlinx.android.synthetic.main.empty_state_layout.*
import kotlinx.android.synthetic.main.simple_refresh_list_layout.*
import javax.inject.Inject

/**
 * Created by Kosh on 20.10.18.
 */
class FeedsFragment : BaseFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(FeedsViewModel::class.java) }
    private val adapter by lazy { ProfileFeedsAdapter() }

    override fun viewModel(): BaseViewModel? = viewModel
    override fun layoutRes(): Int = R.layout.toolbar_fragment_list_layout

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        toolbarTitle.text = getString(R.string.feeds)
        toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        recyclerView.adapter = adapter
        recyclerView.addDivider()
        recyclerView.setEmptyView(emptyLayout)
        fastScroller.attachRecyclerView(recyclerView)
        if (savedInstanceState == null) viewModel.loadFeeds(true)
        swipeRefresh.setOnRefreshListener {
            viewModel.loadFeeds(true)
        }
        listenToChanges()
    }

    private fun listenToChanges() {
        viewModel.loadMoreLiveData.observeNotNull(this) {
            viewModel.loadFeeds()
        }

        viewModel.progress.observeNotNull(this) {
            adapter.currentState = if (it) CurrentState.LOADING else CurrentState.DONE
        }

        viewModel.feeds().observeNotNull(this) {
            adapter.currentState = CurrentState.DONE
            adapter.submitList(it)
        }

        viewModel.counter.observeNotNull(this) {
            postCount(FragmentType.FEEDS, it)
        }
    }

    companion object {
        fun newInstance() = FeedsFragment()
    }
}