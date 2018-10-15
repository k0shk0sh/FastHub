package com.fastaccess.github.ui.modules.profile.gists

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.ui.adapter.ProfileGistsAdapter
import com.fastaccess.github.ui.adapter.base.CurrentState
import com.fastaccess.github.ui.modules.profile.gists.viewmodel.ProfileGistsViewModel
import com.fastaccess.github.utils.EXTRA
import com.fastaccess.github.utils.extensions.addDivider
import com.fastaccess.github.utils.extensions.observeNotNull
import kotlinx.android.synthetic.main.empty_state_layout.*
import kotlinx.android.synthetic.main.simple_refresh_list_layout.*
import javax.inject.Inject

/**
 * Created by Kosh on 14.10.18.
 */
class ProfileGistsFragment : BaseFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(ProfileGistsViewModel::class.java) }
    private val loginBundle: String by lazy { arguments?.getString(EXTRA) ?: "" }
    private val adapter by lazy { ProfileGistsAdapter() }

    override fun viewModel(): BaseViewModel? = viewModel
    override fun layoutRes(): Int = R.layout.simple_refresh_list_layout

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        recyclerView.adapter = adapter
        recyclerView.addDivider()
        recyclerView.setEmptyView(emptyLayout)
        fastScroller.attachRecyclerView(recyclerView)
        if (savedInstanceState == null) viewModel.loadGists(loginBundle, true)
        swipeRefresh.setOnRefreshListener { viewModel.loadGists(loginBundle, true) }
        listenToChanges()
    }

    private fun listenToChanges() {
        viewModel.loadMoreLiveData.observeNotNull(this) {
            viewModel.loadGists(loginBundle)
        }

        viewModel.progress.observeNotNull(this) {
            adapter.currentState = if (it) CurrentState.LOADING else CurrentState.DONE
        }

        viewModel.getGists(loginBundle).observeNotNull(this) {
            adapter.currentState = CurrentState.DONE
            adapter.submitList(it)
        }

        viewModel.counter.observeNotNull(this) {
            (parentFragment as? BaseFragment)?.viewModel()?.tabCounterLiveData?.postValue(Pair(getString(R.string.gists), it))
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