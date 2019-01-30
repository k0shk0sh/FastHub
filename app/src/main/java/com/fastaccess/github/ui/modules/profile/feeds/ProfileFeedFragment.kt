package com.fastaccess.github.ui.modules.profile.feeds

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.fastaccess.data.model.FragmentType
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.extensions.observeNotNull
import com.fastaccess.github.platform.extension.onClick
import com.fastaccess.github.ui.adapter.ProfileFeedsAdapter
import com.fastaccess.github.ui.adapter.base.CurrentState
import com.fastaccess.github.ui.modules.profile.feeds.viewmodel.ProfileFeedsViewModel
import com.fastaccess.github.utils.EXTRA
import com.fastaccess.github.utils.extensions.addDivider
import kotlinx.android.synthetic.main.empty_state_layout.*
import kotlinx.android.synthetic.main.simple_refresh_list_layout.*
import javax.inject.Inject

/**
 * Created by Kosh on 20.10.18.
 */
class ProfileFeedFragment : BaseFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(ProfileFeedsViewModel::class.java) }
    private val loginBundle: String by lazy { arguments?.getString(EXTRA) ?: "" }
    private val adapter by lazy {
        ProfileFeedsAdapter {
            it.onClick(this)
        }
    }

    override fun viewModel(): BaseViewModel? = viewModel
    override fun layoutRes(): Int = R.layout.simple_refresh_list_layout

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        recyclerView.adapter = adapter
        recyclerView.addDivider()
        recyclerView.setEmptyView(emptyLayout)
        fastScroller.attachRecyclerView(recyclerView)
        if (savedInstanceState == null) viewModel.loadFeeds(loginBundle, true)
        swipeRefresh.setOnRefreshListener {
            recyclerView.resetScrollState()
            viewModel.loadFeeds(loginBundle, true)
        }
        recyclerView.addOnLoadMore { viewModel.loadFeeds(loginBundle) }
        listenToChanges()
    }

    private fun listenToChanges() {
        viewModel.progress.observeNotNull(this) {
            adapter.currentState = if (it) CurrentState.LOADING else CurrentState.DONE
        }

        viewModel.feeds(loginBundle).observeNotNull(this) {
            adapter.currentState = CurrentState.DONE
            adapter.submitList(it)
        }

        viewModel.counter.observeNotNull(this) {
            postCount(FragmentType.FEEDS, it)
        }
    }

    companion object {
        fun newInstance(login: String) = ProfileFeedFragment().apply {
            arguments = Bundle().apply {
                putString(EXTRA, login)
            }
        }
    }
}