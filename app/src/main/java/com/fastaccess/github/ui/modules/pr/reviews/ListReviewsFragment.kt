package com.fastaccess.github.ui.modules.pr.reviews

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fastaccess.github.base.viewmodel.ViewModelProviders
import com.fastaccess.github.R
import com.fastaccess.github.base.extensions.isConnected
import com.fastaccess.github.extensions.isTrue
import com.fastaccess.github.ui.modules.search.fragment.viewmodel.FilterSearchViewModel

import javax.inject.Inject

class ListReviewsFragment : com.fastaccess.github.base.BaseFragment() {
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(FilterSearchViewModel::class.java) }

    override fun layoutRes(): Int = R.layout.toolbar_fragment_list_layout
    override fun viewModel(): com.fastaccess.github.base.BaseViewModel? = null

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