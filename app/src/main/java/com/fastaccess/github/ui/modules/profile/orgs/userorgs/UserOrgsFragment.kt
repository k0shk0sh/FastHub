package com.fastaccess.github.ui.modules.profile.orgs.userorgs

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fastaccess.github.R
import com.fastaccess.github.base.extensions.addDivider
import com.fastaccess.github.base.extensions.isConnected
import com.fastaccess.github.extensions.isTrue
import com.fastaccess.github.extensions.observeNotNull
import com.fastaccess.github.base.viewmodel.ViewModelProviders
import com.fastaccess.github.ui.adapter.OrganizationsAdapter
import com.fastaccess.github.base.adapter.CurrentState
import com.fastaccess.github.ui.modules.profile.orgs.userorgs.viewmodel.UserOrgsViewModel

import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Kosh on 2018-11-26.
 */
class UserOrgsFragment : com.fastaccess.github.base.BaseFragment() {
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(UserOrgsViewModel::class.java) }
    private val adapter by lazy {
        OrganizationsAdapter { url ->
            Timber.e(url)
        }
    }

    override fun viewModel(): com.fastaccess.github.base.BaseViewModel? = viewModel
    override fun layoutRes(): Int = R.layout.rounded_toolbar_fragment_list_layout

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        setupToolbar(R.string.organizations)
        recyclerView.adapter = adapter
        recyclerView.addDivider()
        recyclerView.setEmptyView(emptyLayout)
        fastScroller.attachRecyclerView(recyclerView)
        if (savedInstanceState == null) isConnected().isTrue { viewModel.loadOrgs(true) }
        swipeRefresh.setOnRefreshListener {
            if (isConnected()) {
                recyclerView.resetScrollState()
                viewModel.loadOrgs(true)
            } else {
                swipeRefresh.isRefreshing = false
            }
        }
        recyclerView.addOnLoadMore { isConnected().isTrue { viewModel.loadOrgs() } }
        listenToChanges()
    }

    private fun listenToChanges() {
        viewModel.progress.observeNotNull(this) {
            adapter.currentState = if (it) CurrentState.LOADING else CurrentState.DONE
        }

        viewModel.getOrgs().observeNotNull(this) {
            adapter.currentState = CurrentState.DONE
            adapter.submitList(it)
        }
    }


    companion object {
        fun newInstance() = UserOrgsFragment()
    }
}