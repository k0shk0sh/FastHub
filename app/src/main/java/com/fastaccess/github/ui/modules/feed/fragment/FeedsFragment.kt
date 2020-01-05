package com.fastaccess.github.ui.modules.feed.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fastaccess.data.model.FragmentType
import com.fastaccess.fasthub.commit.dialog.CommitListCallback
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.adapter.CurrentState
import com.fastaccess.github.base.extensions.isConnected
import com.fastaccess.github.base.viewmodel.ViewModelProviders
import com.fastaccess.github.extensions.isTrue
import com.fastaccess.github.extensions.observeNotNull
import com.fastaccess.github.extensions.route
import com.fastaccess.github.platform.extension.onClick
import com.fastaccess.github.ui.adapter.ProfileFeedsAdapter
import com.fastaccess.github.ui.modules.feed.fragment.viewmodel.FeedsViewModel
import javax.inject.Inject

/**
 * Created by Kosh on 20.10.18.
 */
class FeedsFragment : BaseFragment(), CommitListCallback {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(FeedsViewModel::class.java) }
    private val adapter by lazy {
        ProfileFeedsAdapter {
            it.onClick(this)
        }
    }

    override fun viewModel(): com.fastaccess.github.base.BaseViewModel? = viewModel
    override fun layoutRes(): Int = R.layout.toolbar_fragment_list_layout

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        setupToolbar(R.string.feeds)
        recyclerView.adapter = adapter
        recyclerView.setEmptyView(emptyLayout)
        fastScroller.attachRecyclerView(recyclerView, appBar)
        if (savedInstanceState == null) isConnected().isTrue { viewModel.loadFeeds(true) }
        swipeRefresh.setOnRefreshListener {
            if (isConnected()) {
                recyclerView.resetScrollState()
                viewModel.loadFeeds(true)
            } else {
                swipeRefresh.isRefreshing = false
            }
        }
        recyclerView.addOnLoadMore { isConnected().isTrue { viewModel.loadFeeds() } }
        listenToChanges()
    }

    override fun onCommitClicked(url: String) {
        route(url)
    }

    private fun listenToChanges() {
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

