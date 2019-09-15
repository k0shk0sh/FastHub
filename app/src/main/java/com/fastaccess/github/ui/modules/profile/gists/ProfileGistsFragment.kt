package com.fastaccess.github.ui.modules.profile.gists

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fastaccess.github.platform.viewmodel.ViewModelProviders
import com.fastaccess.data.model.FragmentType
import com.fastaccess.github.R
import com.fastaccess.github.base.extensions.addDivider
import com.fastaccess.github.base.extensions.isConnected
import com.fastaccess.github.base.utils.EXTRA
import com.fastaccess.github.extensions.isTrue
import com.fastaccess.github.ui.adapter.ProfileGistsAdapter
import com.fastaccess.github.ui.adapter.base.CurrentState
import com.fastaccess.github.ui.modules.profile.gists.viewmodel.ProfileGistsViewModel
import com.fastaccess.github.extensions.observeNotNull

import javax.inject.Inject

/**
 * Created by Kosh on 14.10.18.
 */
class ProfileGistsFragment : com.fastaccess.github.base.BaseFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(ProfileGistsViewModel::class.java) }
    private val loginBundle: String by lazy { arguments?.getString(EXTRA) ?: "" }
    private val adapter by lazy { ProfileGistsAdapter() }

    override fun viewModel(): com.fastaccess.github.base.BaseViewModel? = viewModel
    override fun layoutRes(): Int = R.layout.simple_refresh_list_layout

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        recyclerView.adapter = adapter
        recyclerView.addDivider()
        recyclerView.setEmptyView(emptyLayout)
        fastScroller.attachRecyclerView(recyclerView)
        if (savedInstanceState == null) isConnected().isTrue { viewModel.loadGists(loginBundle, true) }
        swipeRefresh.setOnRefreshListener {
            if (isConnected()) {
                recyclerView.resetScrollState()
                viewModel.loadGists(loginBundle, true)
            } else {
                swipeRefresh.isRefreshing = false
            }
        }
        recyclerView.addOnLoadMore { isConnected().isTrue { viewModel.loadGists(loginBundle) } }
        listenToChanges()
    }

    private fun listenToChanges() {
        viewModel.progress.observeNotNull(this) {
            adapter.currentState = if (it) CurrentState.LOADING else CurrentState.DONE
        }

        viewModel.getGists(loginBundle).observeNotNull(this) {
            adapter.currentState = CurrentState.DONE
            adapter.submitList(it)
        }

        viewModel.counter.observeNotNull(this) {
            postCount(FragmentType.GISTS, it)
        }
    }

    companion object {
        fun newInstance(login: String) = ProfileGistsFragment().apply {
            arguments = Bundle().apply {
                putString(EXTRA, login)
            }
        }
    }
}