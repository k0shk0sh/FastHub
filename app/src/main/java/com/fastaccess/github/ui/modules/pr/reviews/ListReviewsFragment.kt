package com.fastaccess.github.ui.modules.pr.reviews

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fastaccess.github.platform.viewmodel.ViewModelProviders
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.extensions.isTrue
import com.fastaccess.github.ui.modules.search.fragment.viewmodel.FilterSearchViewModel
import com.fastaccess.github.utils.extensions.isConnected
import kotlinx.android.synthetic.main.empty_state_layout.*
import kotlinx.android.synthetic.main.simple_refresh_list_layout.*
import javax.inject.Inject

class ListReviewsFragment : BaseFragment() {
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(FilterSearchViewModel::class.java) }

    override fun layoutRes(): Int = R.layout.toolbar_fragment_list_layout
    override fun viewModel(): BaseViewModel? = null

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        recyclerView.setEmptyView(emptyLayout)
        fastScroller.attachRecyclerView(recyclerView)
        swipeRefresh.setOnRefreshListener {
            if (isConnected()) {
                recyclerView.resetScrollState()
//                viewModel.loadData(true)
            } else {
                swipeRefresh.isRefreshing = false
            }
        }
        recyclerView.addOnLoadMore {
            isConnected().isTrue {
                // viewModel.loadData()
            }
        }
        observeChanges()
    }

    private fun observeChanges() {

    }
}