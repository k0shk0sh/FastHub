package com.fastaccess.fasthub.commit.list

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.fastaccess.data.model.FragmentType
import com.fastaccess.fasthub.commit.R
import com.fastaccess.fasthub.commit.adapter.CommitsAdapter
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.base.extensions.isConnected
import com.fastaccess.github.base.utils.EXTRA
import com.fastaccess.github.base.utils.EXTRA_THREE
import com.fastaccess.github.base.utils.EXTRA_TWO
import com.fastaccess.github.base.viewmodel.ViewModelProviders
import com.fastaccess.github.extensions.isTrue
import com.fastaccess.github.extensions.observeNotNull
import com.fastaccess.github.extensions.route
import javax.inject.Inject

class CommitListFragment : BaseFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(CommitListViewModel::class.java) }
    private val login by lazy { arguments?.getString(EXTRA) ?: throw NullPointerException("no login") }
    private val repo by lazy { arguments?.getString(EXTRA_TWO) ?: throw NullPointerException("no repo") }
    private val number by lazy { arguments?.getInt(EXTRA_THREE, 0) ?: 0 }
    private val isPr by lazy { number > 0 }
    private val adapter by lazy {
        CommitsAdapter {
            route(it.commitUrl)
        }
    }

    override fun viewModel(): BaseViewModel? = viewModel
    override fun layoutRes(): Int = if (isPr) {
        R.layout.simple_refresh_list_layout
    } else {
        R.layout.toolbar_fragment_list_layout
    }

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        toolbar?.let {
            setupToolbar(
                "$login/$repo/${if (number > 0) {
                    "$number/${getString(R.string.commits)}"
                } else {
                    getString(R.string.commits)
                }}"
            )
        }
        if (isPr) {
            recyclerView.adapter = adapter
            recyclerView.setEmptyView(emptyLayout)
            fastScroller.attachRecyclerView(recyclerView)
            swipeRefresh.setOnRefreshListener {
                if (isConnected()) {
                    recyclerView.resetScrollState()
                    viewModel.loadData(login, repo, number, true)
                } else {
                    swipeRefresh.isRefreshing = false
                }
            }

            if (savedInstanceState == null || viewModel.commitsLiveData.value == null) {
                viewModel.loadData(login, repo, number, true)
            }

            if (isPr) {
                recyclerView.addOnLoadMore { isConnected().isTrue { viewModel.loadData(login, repo, number) } }
            }

            observeChanges()
        }
    }

    private fun observeChanges() {
        viewModel.commitsLiveData.observeNotNull(this) {
            adapter.submitList(it)
        }
        viewModel.counter.observeNotNull(this) {
            postCount(FragmentType.COMMITS, it)
        }
        viewModel.changedFilesCount.observeNotNull(this) {
            postCount(FragmentType.FILES, it)
        }
    }

    companion object {
        fun newInstance(
            login: String?,
            repo: String?,
            number: Int? = null
        ) = CommitListFragment().apply {
            arguments = bundleOf(
                EXTRA to login,
                EXTRA_TWO to repo,
                EXTRA_THREE to number
            )
        }
    }
}