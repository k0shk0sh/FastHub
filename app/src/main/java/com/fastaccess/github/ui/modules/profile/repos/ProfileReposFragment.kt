package com.fastaccess.github.ui.modules.profile.repos

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fastaccess.data.model.FragmentType
import com.fastaccess.github.R
import com.fastaccess.github.base.extensions.addDivider
import com.fastaccess.github.base.extensions.isConnected
import com.fastaccess.github.base.utils.EXTRA
import com.fastaccess.github.extensions.isTrue
import com.fastaccess.github.extensions.observeNotNull
import com.fastaccess.github.platform.viewmodel.ViewModelProviders
import com.fastaccess.github.ui.adapter.ProfileReposAdapter
import com.fastaccess.github.ui.adapter.base.CurrentState
import com.fastaccess.github.ui.modules.profile.repos.viewmodel.ProfileReposViewModel

import javax.inject.Inject

/**
 * Created by Kosh on 06.10.18.
 */
class ProfileReposFragment : com.fastaccess.github.base.BaseFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(ProfileReposViewModel::class.java) }
    private val loginBundle: String by lazy { arguments?.getString(EXTRA) ?: "" }
    private val adapter by lazy { ProfileReposAdapter() }

    override fun viewModel(): com.fastaccess.github.base.BaseViewModel? = viewModel
    override fun layoutRes(): Int = R.layout.simple_refresh_list_layout

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        recyclerView.adapter = adapter
        recyclerView.addDivider()
        recyclerView.setEmptyView(emptyLayout)
        fastScroller.attachRecyclerView(recyclerView)
        if (savedInstanceState == null) isConnected().isTrue { viewModel.loadRepos(loginBundle, true) }
        swipeRefresh.setOnRefreshListener {
            if (isConnected()) {
                recyclerView.resetScrollState()
                viewModel.loadRepos(loginBundle, true)
            } else {
                swipeRefresh.isRefreshing = false
            }
        }
        recyclerView.addOnLoadMore { isConnected().isTrue { viewModel.loadRepos(loginBundle) } }
        listenToChanges()
    }

    private fun listenToChanges() {
        viewModel.progress.observeNotNull(this) {
            adapter.currentState = if (it) CurrentState.LOADING else CurrentState.DONE
        }

        viewModel.repos(loginBundle).observeNotNull(this) {
            adapter.currentState = CurrentState.DONE
            adapter.submitList(it)
        }

        viewModel.counter.observeNotNull(this) {
            postCount(FragmentType.REPOS, it)
        }
    }

    companion object {
        fun newInstance(login: String) = ProfileReposFragment().apply {
            arguments = Bundle().apply {
                putString(EXTRA, login)
            }
        }
    }
}