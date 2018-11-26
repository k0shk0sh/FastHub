package com.fastaccess.github.ui.modules.profile.orgs.userorgs

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.fastaccess.data.model.FragmentType
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.extensions.observeNotNull
import com.fastaccess.github.ui.adapter.ProfileGistsAdapter
import com.fastaccess.github.ui.adapter.base.CurrentState
import com.fastaccess.github.ui.modules.profile.orgs.userorgs.viewmodel.UserOrgsViewModel
import com.fastaccess.github.utils.extensions.addDivider
import kotlinx.android.synthetic.main.empty_state_layout.*
import kotlinx.android.synthetic.main.simple_refresh_list_layout.*
import javax.inject.Inject

/**
 * Created by Kosh on 2018-11-26.
 */
class UserOrgsFragment : BaseFragment() {
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(UserOrgsViewModel::class.java) }
    private val adapter by lazy { ProfileGistsAdapter() }

    override fun viewModel(): BaseViewModel? = viewModel
    override fun layoutRes(): Int = R.layout.simple_refresh_list_layout

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        recyclerView.adapter = adapter
        recyclerView.addDivider()
        recyclerView.setEmptyView(emptyLayout)
        fastScroller.attachRecyclerView(recyclerView)
        if (savedInstanceState == null) viewModel.loadOrgs(true)
        swipeRefresh.setOnRefreshListener {
            recyclerView.resetScrollState()
            viewModel.loadOrgs(true)
        }
        recyclerView.addOnLoadMore { viewModel.loadOrgs() }
        listenToChanges()
    }

    private fun listenToChanges() {
        viewModel.progress.observeNotNull(this) {
            adapter.currentState = if (it) CurrentState.LOADING else CurrentState.DONE
        }

        viewModel.getOrgs().observeNotNull(this) {
            adapter.currentState = CurrentState.DONE
//            adapter.submitList(it)
        }

        viewModel.counter.observeNotNull(this) {
            postCount(FragmentType.GISTS, it)
        }
    }


    companion object {
        fun newInstance() = UserOrgsFragment()
    }
}